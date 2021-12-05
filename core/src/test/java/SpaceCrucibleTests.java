import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import core.game.logic.GameLogic;
import core.gdx.wad.SettingsMenu;
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

    // Action Button
    // Automated unit tests for UI objects on the title and settings screens. For testing if a certain menu is open, we can call a visibility check on its associated Scene2D UI object.
    @Test
    @DisplayName("Should reach the next screen after select difficulty")
    void testStart(){


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
    @DisplayName("Request is sent to the server , and it generated a code and takes the player into the lobby.")
    void testCreateLobby(){


    }
    @Test
    @DisplayName("Takes user to screen where lobby code is to be entered.")
    void testJoinLobby(){


    }
    @Test
    @DisplayName("Takes the user to the next screen where level add-ons and audio settings can be done. ")
    void testSettingWindow(){
        SpaceClient client = new SpaceClient(null, null, null);

    }
    @Test
    @DisplayName("The game volume should turn off and the sequencer should be muted")
    void testVolumeSlider(){


    }

    // Client Lobby Connection
    // 1.Create a field where user can input lobby code.
    // 2.Process the user input.
    @Test
    @DisplayName("Connection should be denied, since lobby is invalid.")
    void testLobbyDNE(){


    }
    @Test
    @DisplayName("Check if the lobby exists or not. If the lobby exists connect the client to the lobby server.")
    void testLobbyExists(){


    }

    // Level Selected
    // Create a field where user can select a map and  send the map selected to the server.
    @Test
    @DisplayName("Check if server received the map selected and check if player is being loaded into the map.")
    void testMapSelected(){


    }

    // Select Difficulty Level
    // Create a field where user select the difficulty level
    @Test
    @DisplayName("Check if the difficulty level selected is being sent to the server.")
    void testDifficultySelected(){


    }

    // Server Input Handling
    // Receive client’s input from the keyboard
    @Test
    @DisplayName("Check if the server received the correct input from the client.")
    void testPlayerMoves(){


    }
    @Test
    @DisplayName("Check if the server received the correct angle the player is facing from the client.")
    void testRotateEntity(){


    }
    @Test
    @DisplayName("Check if the server received the mouse input from the client.")
    void testPlayerShot(){


    }

    // Loading Player Entity
    // 1.Receive how many players are in a lobby
    // 2.Create the player entities in the map
    @Test
    @DisplayName("Check if the number of player entities loaded in map match the number of players in lobby.")
    void testPlayerNumber(){


    }
    @Test
    @DisplayName("Check if the correct player entity is removed from the game.")
    void testPlayerLeft(){


    }


    @Test
    @DisplayName("True if Robot input was recognized and position integer was changed accordingly.")
    void testKeyBinds(){


    }

    @Test
    @DisplayName("True if screen in context is show, else false.")
    void testShowScreen(){


    }
    @Test
    @DisplayName("True if the height and/or width are different, else false.")
    void testResizeScreen(){


    }
    @Test
    @DisplayName("True if its state is paused, else false.")
    void testPauseScreen(){


    }
    @Test
    @DisplayName("True if state is not paused, else false.")
    void testResumeScreen(){


    }
    @Test
    @DisplayName("True if it has released all its resources, else false.")
    void testDisposeScreen(){


    }

}
