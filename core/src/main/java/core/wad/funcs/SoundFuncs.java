package core.wad.funcs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.AudioDevice;
import com.badlogic.gdx.utils.Array;
import core.game.logic.GameLogic;
import net.mtrop.doom.WadFile;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequencer;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class SoundFuncs {

    final private static int SAMPLERATE = 22050;

    final public static Map<String, byte[]> gameMIDIs = new HashMap<>();
    final public static Map<String, short[]> soundLumps = new HashMap<>();  //Map lump name to data
    final public static Map<String, String> gameSounds = new HashMap<>();   //Map nice name to lump name
    public static float volume = 0.5f;
    public static Sequencer sequencer = null;

    public static void startSequencer() {
        try {
            sequencer = MidiSystem.getSequencer();
            sequencer.open();
        } catch (MidiUnavailableException e) {
            System.out.println("Could not get MIDI sequencer. Will continue without music.");
        }
    }

    public static void playMIDI(String name) {
        if (sequencer == null || name == null || name.equals("")) {
            System.out.println(name);
            return;
        }
        if (sequencer.isRunning()) {sequencer.stop();}

        try {
            sequencer.setSequence(new ByteArrayInputStream(gameMIDIs.get(name)));
            sequencer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
            sequencer.start();
        } catch (IOException e) {
            System.out.println("Could not read data, skipping...");
        } catch (InvalidMidiDataException e) {
            System.out.println("Not a valid MIDI file, skipping...");
        }
    }

    public static void playSound(String name) {

        if (GameLogic.isSinglePlayer) {
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    short[] sound = soundLumps.get(gameSounds.get(name));
                    AudioDevice soundPlayer = Gdx.audio.newAudioDevice(SAMPLERATE, true);
                    soundPlayer.setVolume(volume);
                    soundPlayer.writeSamples(sound, 0, sound.length);
                    soundPlayer.dispose();
                    try {
                        join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        } else {
            GameLogic.playServerSound(name);
        }
    }

    public static void stopMIDI() {
        if (sequencer.isRunning()) {sequencer.stop();}
    }

    public static void closeSequencer() {
        sequencer.close();
    }

    public static void loadMIDIs(Array<WadFile> wads) {

        for (WadFile w : wads) {

            if (!w.contains("M_START") || !w.contains("M_END")) {continue;}

            int start = w.lastIndexOf("M_START") + 1;
            int end = w.lastIndexOf("M_END");

            for (int i = start; i < end; i++) {

                try {
                    gameMIDIs.put(w.getEntry(i).getName(), w.getData(i));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void loadSounds(Array<WadFile> wads) {

        for (WadFile w : wads) {

            if (!w.contains("FX_START") || !w.contains("FX_END")
                || !w.contains("SNDINFO")) {continue;}

            try {
                String soundInfo = w.getTextData("SNDINFO", Charset.defaultCharset());
                Scanner soundReader = new Scanner(soundInfo);

                while (soundReader.hasNextLine()) {

                    String line = soundReader.nextLine();

                    if (line.isBlank() || line.startsWith("//")) {continue;}

                    String soundName = line.substring(0, line.indexOf(' '));
                    String soundLump = line.substring(line.indexOf(' ') + 1);

                    //If sound already exists, link nice name to sound lump
                    //Otherwise put new sound lump and link nice name to that.
                    if (!soundLumps.containsKey(soundLump)) {
                        byte[] rawSound = w.getData(soundLump);
                        short[] sound = new short[rawSound.length / 2];
                        ByteBuffer.wrap(rawSound).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(sound);

                        soundLumps.put(soundLump, sound);
                    }
                    gameSounds.put(soundName, soundLump);

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
