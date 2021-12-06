import com.badlogic.gdx.utils.Array;
import core.game.logic.GameLogic;
import core.game.logic.EntityState;
import core.wad.funcs.EntityFuncs;
import core.server.MasterServer;
import core.server.Network;
import core.server.SpaceClient;
import core.server.SpaceServer;
import core.wad.funcs.WadFuncs;
import net.mtrop.doom.WadFile;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class SpaceCrucibleTests {

    static MasterServer masterServer;
    static SpaceServer gameServer;
    static SpaceClient player1;
    static SpaceClient player2;

    //Prepare unit tests by creating servers, two clients, and a test lobby
    @BeforeAll
    @DisplayName("Start Servers")
    static void startServers() {

        WadFile file;
        Array<WadFile> wads = new Array<>();


        //Open files. We need our two test add-on files and the resource.
        try {
            file = new WadFile("../assets/resource.wad");
            wads.add(file);
        } catch (IOException io) {
            fail();
            return;
        }

        //Load Levels
        GameLogic.loadLevels(wads);
        WadFuncs.loadLevelEffects();
        WadFuncs.loadScripts(wads);

        assertEquals(4, GameLogic.levels.size());
        assertEquals(1, GameLogic.scripts.size());
        GameLogic.entityTable.clear();

        //Load entities and states
        GameLogic.stateList.add(new EntityState("UNKN", 'A', -1, -1, null));

        wads.forEach(w -> {
            if (w.contains("ENTITIES")) {
                try {
                    EntityFuncs.loadEntityClasses(w.getTextData("ENTITIES", Charset.defaultCharset()));
                } catch (IOException | EntityFuncs.ParseException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            }
        });

        assertEquals(7, GameLogic.entityTable.size());
        GameLogic.currentLevel = GameLogic.levels.get(1);
        try {
            file.close();
        } catch (IOException io) {
            fail();
        }

        try {
            masterServer = new MasterServer(27980, 27990, "asd");
            gameServer = new SpaceServer(27980);
            player1 = new SpaceClient(null, null, null);
            player2 = new SpaceClient(null, null, null);

            Network.CreateLobby cl = new Network.CreateLobby();
            cl.names = new ArrayList<>();
            cl.hashes = new ArrayList<>();
            player1.getMasterClient().sendTCP(cl);

            do {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    fail();
                    return;
                }
            } while (!player1.getGameClient().isConnected());


        } catch (IOException io) {
            fail();
            return;
        }
    }

    @Test
    @DisplayName("Port is not being used and free to use")
    void testIsPortAvailable(){
        assertTrue(MasterServer.isPortAvailable(27981));
    }

    @Test
    @DisplayName("Should created random lobby codes")
    void testCreateRandomLobbyCode(){
        String lobbya = MasterServer.createRandomLobbyCode();
        String lobbyb = MasterServer.createRandomLobbyCode();
        assertNotEquals(lobbya, lobbyb);
    }

    @Test
    @DisplayName("Client connection to master server")
    void testConnectionToMasterServer(){
        assertTrue(player1.getMasterClient().isConnected());
    }

    @Test
    @DisplayName("Client connection to game server")
    void testConnectionToGameServer(){
        assertTrue(player1.getGameClient().isConnected());
    }


    //Upon joining lobby, player count should be correct
    @Test
    @DisplayName("Test join lobby")
    void testJoinLobby() {

        //This prepares the join lobby signal
        Network.JoinLobby jl = new Network.JoinLobby();
        jl.names = new ArrayList<>();
        jl.hashes = new ArrayList<>();

        //This just fetches the lobby code
        String code = "";
        for (String s: masterServer.ports.keySet()) {
            if(masterServer.ports.get(s) == 27980) {
                code = s;
                break;
            }
        }
        jl.lobbyCode = code;

        //This sends the "join lobby" packet. This is the method we're testing.
        player2.getMasterClient().sendTCP(jl);

        do {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                fail();
                return;
            }
        } while (!player2.getGameClient().isConnected());

        //The size of the list is 3, because there is a dummy player 0 with id -1
        assertEquals(3, SpaceServer.idToPlayerNum.size());
    }

    @Test
    @DisplayName("Test Start Game")
    void testStartGame() {
        Network.StartGame startGame = new Network.StartGame();
        startGame.startGame = true;
        startGame.difficultyLevel = GameLogic.MEDIUM;
        startGame.levelnum = 1;
        player1.getGameClient().sendTCP(startGame);

        do {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                fail();
                return;
            }
        } while(!gameServer.gameStartedByHost);
    }

    @Test
    @DisplayName("Change Level")
    void testLevelChange() {
        Network.RCONMessage rm = new Network.RCONMessage();
        rm.message = "level 2";
        player1.getGameClient().sendTCP(rm);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            fail();
            return;
        }

        assertEquals(2, GameLogic.currentLevel.levelnumber);
    }

    @Test
    @DisplayName("Change Difficulty")
    void testDifficultyChange() {
        Network.RCONMessage rm = new Network.RCONMessage();
        rm.message = "skill 4";
        player1.getGameClient().sendTCP(rm);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            fail();
            return;
        }

        assertEquals(GameLogic.NIGHTMARE, GameLogic.difficulty);
    }
}
