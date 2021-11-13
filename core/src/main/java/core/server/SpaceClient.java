package core.server;

import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import com.esotericsoftware.kryonet.util.InputStreamSender;
import com.google.common.hash.Hashing;
import core.game.logic.GameLogic;
import core.gdx.wad.GameScreen;
import core.gdx.wad.MyGDxTest;
import core.gdx.wad.StartMenu;
import core.server.Network.*;
import core.wad.funcs.SoundFuncs;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

public class SpaceClient implements Listener {

    Client masterClient;
    Client gameClient;
    static String ip = "localhost";//100.19.127.86
    GameScreen screen;
    public ValidLobby validLobby;
    StartMenu startMenu;
    File file;

    public SpaceClient(GameScreen screen, StartMenu startMenu){
        this.screen = screen;
        this.startMenu = startMenu;

        masterClient = new Client(8192, 8192);
        masterClient.start();
        //register the packets
        Network.register(masterClient);

        masterClient.addListener(new ThreadedListener(new Listener() {
            public void connected (Connection connection) {
            }

            public void received (Connection connection, Object object) {
                //If Master Server sends ServerDetails Create a new game client and connect it to game Server
                if(object instanceof ServerDetails) {
                    screen.setServerDetails((ServerDetails) object);
                    int tcpPort = ((ServerDetails) object).tcpPort;
                    createGameClient(tcpPort);
                }
                //If Master Server sends ValidLobby unblock the startMenu
                else if( object instanceof ValidLobby){
                    validLobby = (ValidLobby) object;
                    synchronized (startMenu){
                        startMenu.notifyAll();
                    }
                }
                else if(object instanceof Ping) {
                    if (((Ping) object).getAddonFiles) {
                        for (File file : MyGDxTest.addons) {
                            int fileSize = (int) file.length();
                            ByteArrayOutputStream out = new ByteArrayOutputStream(fileSize);
                            try {
                                out.write(Files.readAllBytes(file.toPath()));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
                            connection.addListener(new InputStreamSender(in, 1024) {
                                protected void start() {
                                    CreateWadFile sendLevelFile = new CreateWadFile();
                                    sendLevelFile.levelFileName = file.getName();
                                    sendLevelFile.createFile = true;
                                    sendLevelFile.sendFileTo = ((Ping) object).masterClientThatNeedsTheFiles;
                                    masterClient.sendTCP(sendLevelFile);
                                    System.out.println("Sending Files to Master Server");
                                }
                                @Override
                                protected Object next(byte[] chunk) {
                                    System.out.println("sending " + chunk.length);
                                    WadFile levelFile = new WadFile();
                                    levelFile.levelFile = chunk;
                                    levelFile.levelFileName = file.getName();
                                    levelFile.sendFileTo = ((Ping) object).masterClientThatNeedsTheFiles;
                                    levelFile.levelFileSize = (int) file.length();
                                    return levelFile;
                                }
                            });
                        }
                    }
                }

                else if(object instanceof CreateWadFile){
                    if(((CreateWadFile) object).createFile) {
                        System.out.println("Creating new file " + ((CreateWadFile) object).levelFileName);
                        try {
                            file = Gdx.files.internal("assets/" + ((CreateWadFile) object).levelFileName).file();
                            if(file.createNewFile()){
                                System.out.println("Couldn't create file " + file.getName());
                            }
                            System.out.println("Created a new file");
                            return;
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                    }
                }
                else if(object instanceof WadFile){
                    try {
                        System.out.println("Writing to file " + ((WadFile) object).levelFileName);
                        Files.write(file.toPath(), ((WadFile) object).levelFile, StandardOpenOption.APPEND);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
//                    if((int)file.length() == ((WadFile) object).levelFileSize) {
//                        System.out.println("Changing screens");
//                        screen.changeScreen();
//                    }
                    if((int)file.length() == ((WadFile) object).levelFileSize){
                        System.out.println("File receive complete");
                        try {
                            MyGDxTest.addons.add(file);
                            String hash;
                            hash = com.google.common.io.Files.asByteSource(file).hash(Hashing.sha256()).toString();
                            MyGDxTest.addonHashes.add(hash);
                            screen.update = true;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            public void disconnected (Connection connection) {
            }
        }));

        //Connect the client to the master server
        try {
            masterClient.connect(5000, ip, Network.tcpPort);
        } catch (IOException e) {
            System.out.println("Master Server is not running!");
            masterClient = null;
        }
    }

    public void createGameClient(int tcpPort) {
        gameClient = new Client(8192, 8192);
        gameClient.start();
        //register the packets
        Network.register(gameClient);

        gameClient.addListener(new ThreadedListener(new Listener() {
            public void connected(Connection connection) {
            }

            public void received(Connection connection, Object object) {
                if(object instanceof RenderData){
                    screen.setRenderData((RenderData) object);
                }
                //If server sends MIDIData, change client's music
                else if (object instanceof MIDIData) {
                    if (((MIDIData) object).midi != null && !((MIDIData) object).midi.equals("") ) {
                        SoundFuncs.playMIDI(((MIDIData) object).midi);
                    } else {
                        SoundFuncs.stopMIDI();
                    }
                }
                //If server sends ClientData set the client data
                else if(object instanceof ClientData){
                    screen.setClientData((ClientData) object);

                    //If playernumber is changed and data is not null
                    if (screen.playerNumber == 0
                            && ((ClientData) object).connected != null && ((ClientData) object).idToPlayerNum != null) {
                        startMenu.myGDxTest.setScreen(screen);
                    }
                }
                //If server sends SoundData, play sound matching the given name
                else if (object instanceof SoundData) {
                    SoundFuncs.playSound(((SoundData) object).sound);
                }

                //If server sends StartGame set the startGame value to it
                else if(object instanceof StartGame){
                    GameLogic.currentLevel = GameLogic.levels.get(((StartGame) object).levelnum);
                    screen.startGame = ((StartGame) object).startGame;
                }

                else if (object instanceof LevelChange) {
                    screen.updatePlayerNumber();
                    GameLogic.currentLevel = GameLogic.levels.get(((LevelChange) object).number);
                }

                else if (object instanceof ChatMessage) {
                    screen.addChatToWindow((ChatMessage) object);
                }

                else if (object instanceof PromptConnectionType) {
                    CheckConnection cc = new CheckConnection();
                    cc.type = ConnectionType.PLAYER;
                    gameClient.sendTCP(cc);
                }
            }

            public void disconnected (Connection connection) {
            }
        }));

        try {
            gameClient.connect(5000, ip, tcpPort); //Join the game server
        } catch (IOException e) {
            System.out.println("Game Server is not running!");
            gameClient = null;
        }
        //If host
        if (getGameClient().getID() == 1) {
            System.out.println("Sending .WAD data...");
            sendLevels();
            sendEntities();
            System.out.println("Done!");
        }
    }

    public void makeLobby(){
        CreateLobby createLobby = new CreateLobby();
        createLobby.names = new ArrayList<>();
        MyGDxTest.addons.forEach(f -> createLobby.names.add(f.getName()));
        createLobby.hashes = MyGDxTest.addonHashes;
        masterClient.sendTCP(createLobby);
    }
    public void sendLobbyCode(String lCode){
        Network.JoinLobby joinLobby = new Network.JoinLobby();
        joinLobby.lobbyCode = lCode;
        joinLobby.names = new ArrayList<>();
        MyGDxTest.addons.forEach(f -> joinLobby.names.add(f.getName()));
        joinLobby.hashes = MyGDxTest.addonHashes;
        masterClient.sendTCP(joinLobby);
    }

    public void getInput(boolean[] controls){
        InputData inputData = new InputData();
        inputData.controls = controls;
        inputData.angle = screen.getAngle();
        gameClient.sendTCP(inputData);
    }

    public void getCameraData(CameraData cameradata) {
        gameClient.sendTCP(cameradata);
    }

    public Client getGameClient(){
        return gameClient;
    }

    public Client getMasterClient(){
        return masterClient;
    }

    private void sendLevels() {

        GameLogic.levels.forEach((integer, levelData) -> {

            LevelInfo li = new LevelInfo();
            li.levelNumber = integer;
            li.levelName = levelData.name;
            li.levelMIDI = levelData.midi;
            gameClient.sendTCP(li);


            levelData.getTiles().forEach(levelTile -> {
                AddTile at = new AddTile();
                at.levelNumber = integer;
                at.levelTile = levelTile;
                gameClient.sendTCP(at);
            });

            levelData.getObjects().forEach(levelObject -> {
                AddObject ao = new AddObject();
                ao.levelNumber = integer;
                ao.levelObject = levelObject;
                gameClient.sendTCP(ao);
            });

        });
    }

    private void sendEntities() {

        GameLogic.stateList.forEach(s -> {
            State state = new State();
            state.state = s;
            gameClient.sendTCP(state);
        });

        GameLogic.entityTable.forEach((k, v) -> {
            GameEntity ge = new GameEntity();
            ge.spawner = v;
            ge.name = k;
            ge.mapID = -1;

            GameLogic.mapIDTable.forEach((i, e) -> {
                if (e.equals(ge.spawner)) {
                    ge.mapID = i;
                }
            });

            gameClient.sendTCP(ge);
        });
    }
}
