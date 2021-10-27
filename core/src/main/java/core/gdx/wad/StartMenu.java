package core.gdx.wad;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
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
                GameScreen gameScreen= new GameScreen(null, false);
                client = new SpaceClient(gameScreen);
                gameScreen.client = client;
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
                        client.getServer(4);
                        titleScreen.remove = true;
                        myGDxTest.setScreen(gameScreen);
                    }
                });
                joinLobby.addListener(new ClickListener(){
                    public void clicked(InputEvent event, float x, float y) {
                        super.clicked(event, x, y);
                        TextButton submit = new TextButton("submit", skin);
                        TextField lobbyCode = new TextField("", skin);
                        lobbyCode.setBounds(300,400, 100, 50);
                        submit.setBounds(400,400,50,50);
                        stage.addActor(lobbyCode);
                        stage.addActor(submit);
                        submit.addListener(new ClickListener(){
                            public void clicked(InputEvent event, float x, float y) {
                                String lCode = lobbyCode.getText();
                                client.sendLobbyCode(lCode);
                                titleScreen.remove = true;
                                myGDxTest.setScreen(gameScreen);
                            }
                        });
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