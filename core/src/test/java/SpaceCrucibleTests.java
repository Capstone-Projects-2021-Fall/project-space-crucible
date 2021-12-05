import core.game.logic.GameLogic;
import core.server.MasterServer;
import core.server.SpaceClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class SpaceCrucibleTests {
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
        MasterServer masterServer = new MasterServer(27980, 27990, "asd");
        SpaceClient spaceClient = new SpaceClient(null, null, null);
        assertTrue(spaceClient.getMasterClient().isConnected());
    }
    @Test
    @DisplayName("Should reach the next screen after click start button")
    void testStartGame(){
        SpaceClient spaceClient = new SpaceClient(null, null, null);

       //assertTrue();
    }
    @Test
    @DisplayName("Game difficulty should be changed")
    void testChangeDifficulty(){


    }
    @Test
    @DisplayName("Should display screen to join or create a lobby")
    void testCoop(){


    }
    @Test
    @DisplayName("Create a lobby with the random lobby code")
    void testCreateLobby(){


    }
    @Test
    @DisplayName("Join a lobby with the lobby code entered")
    void testJoinLobby(){


    }
    @Test
    @DisplayName("Should show the setting screen from main menu")
    void testSettingScreen(){


    }
    @Test
    @DisplayName("Game volume should be turn off")
    void testVolumeSlider(){


    }


}
