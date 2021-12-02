import core.server.MasterServer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class SpaceCrucibleTests {
    @Test
    @DisplayName("Port is not being used and free to use")
    void testIsPortAvailable() throws IOException {
        MasterServer masterServer = new MasterServer(27980, 27990, "asd");
        assertTrue(masterServer.isPortAvailable(27981));
    }
    @Test
    @DisplayName("Should created random lobby codes")
    void testCreateRandomLobbyCode(){
        String lobbya = MasterServer.createRandomLobbyCode();
        String lobbyb = MasterServer.createRandomLobbyCode();
        assertNotEquals(lobbya, lobbyb);
    }
}
