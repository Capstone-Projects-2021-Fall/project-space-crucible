package core.rcon;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Array;
import core.game.entities.PlayerPawn;
import core.server.Network;

public class LiveWindow extends Window {

    final private Stage stage;
    final private RCONScreen parent;
    final private List<String> playersList;
    final private ScrollPane scrollPane;
    final private Table liveStats;
    final private Label sentPacketsPerSecond;
    final private Label receivedPacketsPerSecond;
    final private Label avgSentPacketsPerSecond;
    final private Label avgReceivedPacketsPerSecond;

    public LiveWindow(String title, Skin skin, Stage stage, RCONScreen parent) {
        super(title, skin);
        setResizable(true);
        setMovable(true);
        this.stage = stage;
        this.parent = parent;
        playersList = new List<>(skin);
        scrollPane = new ScrollPane(playersList);
        liveStats = new Table(skin);
        sentPacketsPerSecond = new Label("", skin);
        receivedPacketsPerSecond = new Label("", skin);
        avgSentPacketsPerSecond = new Label("", skin);
        avgReceivedPacketsPerSecond = new Label("", skin);
        liveStats.add(sentPacketsPerSecond);
        liveStats.row();
        liveStats.add(receivedPacketsPerSecond);
        liveStats.row();
        liveStats.add(avgSentPacketsPerSecond);
        liveStats.row();
        liveStats.add(avgReceivedPacketsPerSecond);
        liveStats.pack();
        add(scrollPane).width(320).height(250);
        row();
        add(liveStats).height(200);
        pack();
    }

    public void updatePlayers(Network.RCONPlayerStats stats) {
        Array<String> listEntries = new Array<>();
        for (int i = 0; i < stats.playerList.size(); i++) {
            String name = stats.usernames.size() > i ? (stats.usernames.get(i)) : "Player " + (i+1);
            if (name == null) {name = "BOT";}

            listEntries.add(name
            + " Pos: (" + (int)stats.playerList.get(i).getPos().x
            + ", " + (int)stats.playerList.get(i).getPos().y
            + ") HP: " + stats.playerList.get(i).getHealth()
            + " Ping: " + stats.pings.get(i));
        }

        playersList.setItems(listEntries);
    }

    public void updatePacketStats(Network.RCONPacketStats stats) {
        sentPacketsPerSecond.setText("Packets sent last second: " + stats.sentPerSec);
        receivedPacketsPerSecond.setText("Packets received last second: " + stats.receivedPerSec);
        avgSentPacketsPerSecond.setText("Average packets sent per second: " + stats.avgSentPerSec);
        avgReceivedPacketsPerSecond.setText("Average packets received per second: " + stats.avgReceivedPerSec);
    }
}
