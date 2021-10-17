package core.wad.funcs;

import com.badlogic.gdx.utils.Array;
import net.mtrop.doom.WadFile;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequencer;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MIDIFuncs {

    final public static Map<String, byte[]> gameMIDIs = new HashMap<>();
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

    public static void stopMIDI() {
        if (sequencer.isRunning()) {sequencer.stop();}
        sequencer.close();
    }

    public static void loadMIDIs(Array<WadFile> wads) {

        for (WadFile w : wads) {

            if (!w.contains("M_START") || !w.contains("M_END")) {continue;}

            int start = w.lastIndexOf("M_START") + 1;
            int end = w.lastIndexOf("M_END");

            for (int i = start; i < end; i++) {

                try {
                    System.out.println(w.getEntry(i).getName());
                    gameMIDIs.put(w.getEntry(i).getName(), w.getData(i));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
