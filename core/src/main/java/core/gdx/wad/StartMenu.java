package core.gdx.wad;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import core.server.SpaceClient;
import editor.launch.EditorScreen;

public class StartMenu extends Actor{

    public MyGDxTest myGDxTest;
    SettingsScreen settingsScreen;
    SpaceClient client;
    final StartMenu startMenu = this;
    static CreateImageButton startButton = new CreateImageButton("buttons/start.png", "buttons/startnobg.png");
    static CreateImageButton coopButton = new CreateImageButton("buttons/coop.png", "buttons/coopnobg.png");
    static CreateImageButton settingsButton = new CreateImageButton("buttons/settings.png", "buttons/settingsnobg.png");
    static CreateImageButton levelEditorButton = new CreateImageButton("buttons/leveleditor.png", "buttons/leveleditornobg.png");
    static CreateImageButton exitButton = new CreateImageButton("buttons/exit.png", "buttons/exitnobg.png");
    CreateImageButton createLobbyButton = new CreateImageButton("buttons/createlobby.png", "buttons/createlobbynobg.png");
    CreateImageButton joinLobbyButton = new CreateImageButton("buttons/joinlobby.png", "buttons/joinlobbynobg.png");
    CreateImageButton backButton = new CreateImageButton("buttons/back.png", "buttons/backnobg.png");
    static Button[] MainMenuButtons = {startButton.button, coopButton.button, settingsButton.button, exitButton.button, levelEditorButton.button};
    Button[] CoopButtons = {createLobbyButton.button, joinLobbyButton.button, backButton.button};

    public StartMenu(Skin skin, TitleScreen titleScreen, Stage stage, MyGDxTest myGDxTest) {
        this.myGDxTest=myGDxTest;
        this.settingsScreen=new SettingsScreen(myGDxTest);

        //Main menu buttons
        startButton.button.setBounds((Gdx.graphics.getWidth()-200)/ 2f, (Gdx.graphics.getHeight())/ 2f, 200, 50);
        stage.addActor(startButton.button);

        coopButton.button.setBounds((Gdx.graphics.getWidth() - 150)/ 2f, (Gdx.graphics.getHeight() - 100)/ 2f, 150, 50);
        stage.addActor(coopButton.button);

        settingsButton.button.setBounds((Gdx.graphics.getWidth() - 200)/ 2f, (Gdx.graphics.getHeight() - 200)/ 2f, 200, 50);
        stage.addActor(settingsButton.button);

        levelEditorButton.button.setBounds((Gdx.graphics.getWidth() - 250)/ 2f, (Gdx.graphics.getHeight() - 320)/ 2f, 250, 60);
        stage.addActor(levelEditorButton.button);

        exitButton.button.setBounds((Gdx.graphics.getWidth() - 150)/ 2f, (Gdx.graphics.getHeight() - 420)/ 2f, 150, 50);
        stage.addActor(exitButton.button);

        //Coop buttons
        createLobbyButton.button.setBounds((Gdx.graphics.getWidth() - 200)/ 2f, Gdx.graphics.getHeight() / 2f, 200, 50);
        stage.addActor(createLobbyButton.button);

        joinLobbyButton.button.setBounds((Gdx.graphics.getWidth() - 200)/ 2f, (Gdx.graphics.getHeight() - 110)/ 2f, 200, 50);
        stage.addActor(joinLobbyButton.button);

        backButton.button.setBounds((Gdx.graphics.getWidth() - 200)/ 2f, (Gdx.graphics.getHeight() - 220)/ 2f, 200, 50);
        stage.addActor(backButton.button);

        setCoopButtonsVisible(false);

        startButton.button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                ChooseDifficultyWindow window = new ChooseDifficultyWindow("Choose Difficulty:", skin, titleScreen, startMenu, MainMenuButtons);
                window.setBounds(((Gdx.graphics.getWidth() - 150)/ 2f), ((Gdx.graphics.getHeight() - 110) / 2f), 150, 110);
                setMainMenuButtonsVisible(false);
                stage.addActor(window);
            }
        });
        coopButton.button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if(NameChangeWindow.playerName.length() == 0){
                    PopupWindow error = new PopupWindow("No Username Found!", skin, "Go to settings and create a username");
                    titleScreen.showPopup(error);
                    error.setPosition((Gdx.graphics.getWidth() - error.getWidth())/ 2f, (Gdx.graphics.getHeight() - error.getHeight())/ 2f);
                    return;
                }
                setMainMenuButtonsVisible(false);
                setCoopButtonsVisible(true);
                GameScreen gameScreen= new GameScreen(null, false, myGDxTest);
                client = new SpaceClient(gameScreen, startMenu);
                if(client.getMasterClient() == null) {
                    PopupWindow error = new PopupWindow("Connection Error", skin,
                            "Error connecting to the server!\nTry Again Later.");
                    titleScreen.showPopup(error);
                    error.setPosition((Gdx.graphics.getWidth() - error.getWidth())/ 2f, (Gdx.graphics.getHeight() - error.getHeight())/ 2f);
                    return;
                }
                gameScreen.client = client;

                backButton.button.addListener(new ClickListener(){
                    public void clicked(InputEvent event, float x, float y) {
                        super.clicked(event, x, y);
                        client.getMasterClient().close();
                        setCoopButtonsVisible(false);
                        setMainMenuButtonsVisible(true);
                    }
                });
                createLobbyButton.button.addListener(new ClickListener(){
                    public void clicked(InputEvent event, float x, float y) {
                        super.clicked(event, x, y);
                        client.makeLobby();
                    }
                });
                joinLobbyButton.button.addListener(new ClickListener(){
                    public void clicked(InputEvent event, float x, float y) {
                        super.clicked(event, x, y);
                        TextButton submit = new TextButton("submit", skin);
                        TextButton back = new TextButton("back", skin);
                        TextField lobbyCode = new TextField("Lobby Code", skin);
                        lobbyCode.setBounds(((Gdx.graphics.getWidth() - 100)/ 2f),((Gdx.graphics.getHeight() - 25)/ 2f), 100, 35);
                        submit.setBounds(((Gdx.graphics.getWidth() + 105)/ 2f),((Gdx.graphics.getHeight() - 25)/ 2f),60,35);
                        back.setBounds(((Gdx.graphics.getWidth() - 50)/ 2f),((Gdx.graphics.getHeight() - 100)/ 2f),50,35);
                        setCoopButtonsVisible(false);

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
                                setCoopButtonsVisible(true);

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
                myGDxTest.setScreen(settingsScreen);
            }
        });

        levelEditorButton.button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                myGDxTest.setScreen(new EditorScreen());
            }
        });

        exitButton.button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                System.exit(0);
            }
        });
    }

    public void setCoopButtonsVisible(boolean visible){
        for(Button button : CoopButtons)
            button.setVisible(visible);
    }
    public static void setMainMenuButtonsVisible(boolean visible){
        for(Button button : MainMenuButtons)
            button.setVisible(visible);
    }
}