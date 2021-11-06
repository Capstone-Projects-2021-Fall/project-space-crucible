package core.gdx.wad;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import core.server.SpaceClient;
import editor.launch.EditorScreen;

public class StartMenu extends Actor{

    public MyGDxTest myGDxTest;
    SettingsScreen settingsScreen;
    SpaceClient client;
    final StartMenu startMenu = this;

    public StartMenu(String title, Skin skin, TitleScreen titleScreen, Stage stage, MyGDxTest myGDxTest) {
        this.myGDxTest=myGDxTest;
        this.settingsScreen=new SettingsScreen(myGDxTest);

        CreateImageButton startButton = new CreateImageButton("buttons/start.png", "buttons/startnobg.png");
        startButton.button.setBounds((int)((Gdx.graphics.getWidth()-200)/ 2),(int)((Gdx.graphics.getHeight())/ 2), 200, 50);
        stage.addActor(startButton.button);

        CreateImageButton coopButton = new CreateImageButton("buttons/coop.png", "buttons/coopnobg.png");
        coopButton.button.setBounds((int)((Gdx.graphics.getWidth() - 150)/ 2), (int)((Gdx.graphics.getHeight() - 100)/ 2), 150, 50);
        stage.addActor(coopButton.button);

        CreateImageButton settingsButton = new CreateImageButton("buttons/settings.png", "buttons/settingsnobg.png");
        settingsButton.button.setBounds((int)((Gdx.graphics.getWidth() - 200)/ 2), (int)((Gdx.graphics.getHeight() - 200)/ 2), 200, 50);
        stage.addActor(settingsButton.button);

        CreateImageButton levelEditorButton = new CreateImageButton("buttons/leveleditor.png", "buttons/leveleditornobg.png");
        levelEditorButton.button.setBounds((int)((Gdx.graphics.getWidth() - 250)/ 2), (int)((Gdx.graphics.getHeight() - 320)/ 2), 250, 60);
        stage.addActor(levelEditorButton.button);

        CreateImageButton exitButton = new CreateImageButton("buttons/exit.png", "buttons/exitnobg.png");
        exitButton.button.setBounds((int)((Gdx.graphics.getWidth() - 150)/ 2), (int)((Gdx.graphics.getHeight() - 420)/ 2), 150, 50);
        stage.addActor(exitButton.button);

        Button buttons[] = {startButton.button, coopButton.button, settingsButton.button, exitButton.button, levelEditorButton.button};

        startButton.button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                System.out.println("Start\n");
                ChooseDifficultyWindow window = new ChooseDifficultyWindow("Choose Difficulty:", skin, titleScreen, startMenu, buttons);
                window.setBounds((int)((Gdx.graphics.getWidth() - 150)/ 2), (int)((Gdx.graphics.getHeight() - 110) / 2), 150, 110);
                for(Button button : buttons)
                    button.setVisible(false);
                stage.addActor(window);
            }
        });
        coopButton.button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                GameScreen gameScreen= new GameScreen(null, false, myGDxTest);
                client = new SpaceClient(gameScreen, startMenu);
                if(client.getMasterClient() == null) {
                    Dialog error = new Dialog("Error", skin);
                    error.text("Error connecting to the server!\nTry Again Later.");
                    error.setBounds((int)((Gdx.graphics.getWidth() - 250)/ 2), (int)((Gdx.graphics.getHeight() - 70) / 2), 250, 100);
                    error.button("Ok").addListener(new ClickListener(){
                        public void clicked(InputEvent event, float x, float y) {
                            super.clicked(event, x, y);
                            error.remove();
                        }
                    });
                    stage.addActor(error);
                    return;
                }
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
                lobbyMenu.setBounds((int)((Gdx.graphics.getWidth() - 250)/ 2), (int)((Gdx.graphics.getHeight() - 150) / 2), 250, 150);
                stage.addActor(lobbyMenu);
                for(Button button : buttons)
                    button.setVisible(false);
                back.addListener(new ClickListener(){
                    public void clicked(InputEvent event, float x, float y) {
                        super.clicked(event, x, y);
                        for(Button button : buttons)
                            button.setVisible(true);
                        lobbyMenu.remove();
                    }
                });
                createLobby.addListener(new ClickListener(){
                    public void clicked(InputEvent event, float x, float y) {
                        super.clicked(event, x, y);
                        client.makeLobby();
                        //myGDxTest.setScreen(gameScreen);
                        //titleScreen.dispose();
                    }
                });
                joinLobby.addListener(new ClickListener(){
                    public void clicked(InputEvent event, float x, float y) {
                        super.clicked(event, x, y);
                        TextButton submit = new TextButton("submit", skin);
                        TextButton back = new TextButton("back", skin);
                        TextField lobbyCode = new TextField("Lobby Code", skin);
                        lobbyCode.setBounds((int)((Gdx.graphics.getWidth() - 100)/ 2),(int)((Gdx.graphics.getHeight() - 25)/ 2), 100, 35);
                        submit.setBounds((int)((Gdx.graphics.getWidth() + 105)/ 2),(int)((Gdx.graphics.getHeight() - 25)/ 2),60,35);
                        back.setBounds((int)((Gdx.graphics.getWidth() - 50)/ 2),(int)((Gdx.graphics.getHeight() - 100)/ 2),50,35);
                        lobbyMenu.setVisible(false);
                        stage.addActor(lobbyCode);
                        stage.addActor(submit);
                        stage.addActor(back);
                        lobbyCode.addListener(new ClickListener() {
                            @Override
                            public void clicked(InputEvent event, float x, float y) {
                                super.clicked(event, x, y);
                                lobbyCode.setText("");
                            }
                        });
                        submit.addListener(new ClickListener(){
                            public void clicked(InputEvent event, float x, float y) {
                                String lCode = lobbyCode.getText();
                                lCode = lCode.toUpperCase();
                                client.sendLobbyCode(lCode);
                                System.out.println("Waiting...");
                                synchronized (startMenu){
                                    try {
                                        startMenu.wait(1000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                                System.out.println("Done waiting.");
                                if(!client.validLobby.valid) {
                                    //myGDxTest.setScreen(gameScreen);
                                    System.out.println("Invalid.");
                                    titleScreen.showPopup(new PopupWindow("Invalid Lobby", skin,
                                            client.validLobby.reason));
                                }
                            }
                        });
                        back.addListener(new ClickListener(){
                            public void clicked(InputEvent event, float x, float y) {
                                submit.remove();
                                back.remove();
                                lobbyCode.remove();
                                lobbyMenu.setVisible(true);

                            }
                        });
                    }
                });
            }
        });
        settingsButton.button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                System.out.println("Settings\n");
                myGDxTest.setScreen(settingsScreen);
                //((MyGDxTest) Gdx.app.getApplicationListener()).setScreen(new SettingsScreen());
            }
        });

        levelEditorButton.button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                System.out.println("Editor\n");
                myGDxTest.setScreen(new EditorScreen());
                //((MyGDxTest) Gdx.app.getApplicationListener()).setScreen(new SettingsScreen());
            }
        });

        exitButton.button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                System.out.println("Exit\n");
                System.exit(0);
            }
        });
    }
}