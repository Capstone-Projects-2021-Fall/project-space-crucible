package core.gdx.wad;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import core.game.logic.GameLogic;
import core.level.info.LevelData;
import core.server.Network;
import core.server.SpaceClient;
import org.lwjgl.system.CallbackI;

import java.util.function.BiConsumer;

public class StartMenu extends Actor{

    public MyGDxTest myGDxTest;
    SettingsScreen settingsScreen;
    SpaceClient client;
    final StartMenu startMenu = this;

    public StartMenu(String title, Skin skin, TitleScreen titleScreen, Stage stage, MyGDxTest myGDxTest) {
        this.myGDxTest=myGDxTest;
        this.settingsScreen=new SettingsScreen(myGDxTest);

        ImageButton.ImageButtonStyle startStyle = new ImageButton.ImageButtonStyle();
        startStyle.over = new TextureRegionDrawable(new TextureRegion( new Texture("buttons/start.png")));
        startStyle.up = new TextureRegionDrawable(new TextureRegion( new Texture("buttons/startnobg.png")));
        Button startButton = new ImageButton(startStyle);
        startButton.setBounds((int)((Gdx.graphics.getWidth()-200)/ 2),(int)((Gdx.graphics.getWidth() - 200)/ 2), 200, 60);
        stage.addActor(startButton);

        ImageButton.ImageButtonStyle coopStyle = new ImageButton.ImageButtonStyle();
        coopStyle.over = new TextureRegionDrawable(new TextureRegion( new Texture("buttons/coop.png")));
        coopStyle.up = new TextureRegionDrawable(new TextureRegion( new Texture("buttons/coopnobg.png")));
        Button coopButton = new ImageButton(coopStyle);
        coopButton.setBounds((int)((Gdx.graphics.getWidth() - 150)/ 2), (int)((Gdx.graphics.getWidth() - 310)/ 2), 150, 60);
        stage.addActor(coopButton);

        ImageButton.ImageButtonStyle settingsStyle = new ImageButton.ImageButtonStyle();
        settingsStyle.over = new TextureRegionDrawable(new TextureRegion( new Texture("buttons/settings.png")));
        settingsStyle.up = new TextureRegionDrawable(new TextureRegion( new Texture("buttons/settingsnobg.png")));
        Button settingsButton = new ImageButton(settingsStyle);
        settingsButton.setBounds((int)((Gdx.graphics.getWidth() - 200)/ 2), (int)((Gdx.graphics.getWidth() - 420)/ 2), 200, 60);
        stage.addActor(settingsButton);

        ImageButton.ImageButtonStyle exitStyle = new ImageButton.ImageButtonStyle();
        exitStyle.up = new TextureRegionDrawable(new TextureRegion( new Texture("buttons/exitnobg.png")));
        exitStyle.over = new TextureRegionDrawable(new TextureRegion( new Texture("buttons/exit.png")));
        Button exitButton = new ImageButton(exitStyle);
        exitButton.setBounds((int)((Gdx.graphics.getWidth() - 150)/ 2), (int)((Gdx.graphics.getWidth() - 530)/ 2), 150, 60);
        stage.addActor(exitButton);

        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                System.out.println("Start\n");
                ChooseDifficultyWindow window = new ChooseDifficultyWindow("Choose Difficulty:", skin, titleScreen, startMenu);
                window.setBounds((int)((Gdx.graphics.getWidth() - 150)/ 2), (int)((Gdx.graphics.getHeight() - 110) / 2), 150, 110);
                stage.addActor(window);
                startMenu.setVisible(false);
            }
        });
        coopButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                GameScreen gameScreen= new GameScreen(null, false);
                client = new SpaceClient(gameScreen, startMenu);
                if(client.getClient() == null) {
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
                startMenu.setVisible(false);
                back.addListener(new ClickListener(){
                    public void clicked(InputEvent event, float x, float y) {
                        super.clicked(event, x, y);
                        startMenu.setVisible(true);
                        lobbyMenu.remove();
                    }
                });
                createLobby.addListener(new ClickListener(){
                    public void clicked(InputEvent event, float x, float y) {
                        super.clicked(event, x, y);
                        client.makeLobby();
                        myGDxTest.setScreen(gameScreen);
                        titleScreen.dispose();
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
                                synchronized (startMenu){
                                    try {
                                        startMenu.wait();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                                if(client.validLobby.valid) {
                                    myGDxTest.setScreen(gameScreen);
                                    titleScreen.dispose();
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
        settingsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                System.out.println("Settings\n");
                myGDxTest.setScreen(settingsScreen);
                //((MyGDxTest) Gdx.app.getApplicationListener()).setScreen(new SettingsScreen());
            }
        });

        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                System.out.println("Exit\n");
                System.exit(0);
            }
        });
    }
}