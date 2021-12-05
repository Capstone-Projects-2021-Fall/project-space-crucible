package integration;

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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.lwjgl.system.CallbackI;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class MultiplayerTest {

    @Test
    @DisplayName("Test Multiplayer Functionality")
    void multiplayerTest() {

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

        MasterServer masterServer;
        SpaceServer spaceServer;
        SpaceClient player1, player2;
        try {
            masterServer = new MasterServer(27980, 27990, "asd");
            spaceServer = new SpaceServer(27980);
            player1 = new SpaceClient(null, null, null);
        } catch (IOException io) {
            fail();
            return;
        }
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

        player2 = new SpaceClient(null, null, null);
        Network.JoinLobby jl = new Network.JoinLobby();
        jl.names = cl.names;
        jl.hashes = cl.hashes;

        String code = "";
        for (String s: masterServer.ports.keySet()) {
            if(masterServer.ports.get(s) == 27980) {
                code = s;
                break;
            }
        }
        jl.lobbyCode = code;

        player2.getMasterClient().sendTCP(jl);

        do {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                fail();
                return;
            }
        } while (!player2.getGameClient().isConnected());

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
        } while(!spaceServer.gameStartedByHost);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            fail();
            return;
        }

        //2 players plus dummy player 0
        assertEquals(3, SpaceServer.idToPlayerNum.size());

        //GameLogic is running
        assertNotEquals(0, GameLogic.ticCounter);
    }
}
