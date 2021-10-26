package core.gdx.wad;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import core.server.Network;
import core.server.SpaceClient;

public class StartMenu extends Window {

    MyGDxTest myGDxTest;
    SettingsScreen settingsScreen;
    SpaceClient client;

    public StartMenu(String title, Skin skin, TitleScreen titleScreen, Stage stage, MyGDxTest myGDxTest) {
        super(title, skin);
        this.myGDxTest=myGDxTest;
        this.settingsScreen=new SettingsScreen(myGDxTest);
        setModal(false);
        Button startButton = new TextButton("Start", skin);
        add(startButton);
        row();
        Button coopButton = new TextButton("Co-op", skin);
        add(coopButton);
        row();
        Button settingsButton = new TextButton("Settings", skin);
        add(settingsButton);
        row();
        pack();

        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                System.out.println("Start\n");
                stage.addActor(new ChooseDifficultyWindow("Choose Difficulty:", skin, titleScreen));
            }
        });
        coopButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                GameScreen gameScreen= new GameScreen(titleScreen.gameLoop, titleScreen.isSinglePlayer);
                client = new SpaceClient(gameScreen);
                System.out.println("Co-op\n");

                Window lobbyMenu = new Window("Lobby", skin);
                lobbyMenu.setMovable(false);
                TextButton joinLobby = new TextButton("Join Lobby", skin);
                TextButton createLobby = new TextButton("Create Lobby", skin);
                TextButton back = new TextButton("Back", skin);
                lobbyMenu.add(createLobby).row();
                lobbyMenu.add(joinLobby).row();
                lobbyMenu.add(back);
                lobbyMenu.setBounds((Gdx.graphics.getWidth() - 400)/ 2, (Gdx.graphics.getHeight() - 200) / 2, 200, 100);

                stage.addActor(lobbyMenu);
                back.addListener(new ClickListener(){
                    public void clicked(InputEvent event, float x, float y) {
                        super.clicked(event, x, y);
                        lobbyMenu.remove();
                    }
                });
                createLobby.addListener(new ClickListener(){
                    public void clicked(InputEvent event, float x, float y) {
                        super.clicked(event, x, y);
                        Network.CreateLobby create = new Network.CreateLobby();
                        create.playerCount = 4;
                        client.getClient().sendTCP(create);
                    }
                });
                joinLobby.addListener(new ClickListener(){
                    public void clicked(InputEvent event, float x, float y) {
                        super.clicked(event, x, y);

                    }
                });
            }
        });
        settingsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                System.out.println("Settings\n");
                titleScreen.remove=true;
                myGDxTest.setScreen(settingsScreen);
                //((MyGDxTest) Gdx.app.getApplicationListener()).setScreen(new SettingsScreen());
            }
        });

    }
}