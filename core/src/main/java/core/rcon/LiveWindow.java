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

    public LiveWindow(String title, Skin skin, Stage stage, RCONScreen parent) {
        super(title, skin);
        setResizable(true);
        setMovable(true);
        this.stage = stage;
        this.parent = parent;
        playersList = new List<>(skin);
        playersList.setItems("lol");
        scrollPane = new ScrollPane(playersList);
        liveStats = new Table(skin);
        sentPacketsPerSecond = new Label("", skin);
        receivedPacketsPerSecond = new Label("", skin);
        liveStats.add(sentPacketsPerSecond);
        liveStats.row();
        liveStats.add(receivedPacketsPerSecond);
        liveStats.pack();
        add(scrollPane).width(320).height(250);
        row();
        add(liveStats).height(200);
        pack();
    }

    public void updatePlayers(Network.RCONPlayerStats stats) {
        Array<String> listEntries = new Array<>();
        for (int i = 0; i < stats.playerList.size(); i++) {
            System.out.println(i);
            String name = stats.usernames.size() > i ? (stats.usernames.get(i)) : "Player " + (i+1);
            if (name.equals("null")) {name = "BOT";}

            listEntries.add(name
            + " Pos: (" + stats.playerList.get(i).getPos().x
            + ", " + stats.playerList.get(i).getPos().y
            + ") HP: " + stats.playerList.get(i).getHealth());
        }

        playersList.setItems(listEntries);
    }
}
