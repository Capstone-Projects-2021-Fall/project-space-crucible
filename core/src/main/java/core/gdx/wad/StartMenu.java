package core.gdx.wad;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class StartMenu extends Window {
    MyGDxTest myGDxTest;
    SettingsScreen settingsScreen;
    public StartMenu(String title, Skin skin, TitleScreen titleScreen, Stage stage, MyGDxTest myGDxTest) {
        super(title, skin);
        this.myGDxTest=myGDxTest;
        this.settingsScreen=new SettingsScreen(myGDxTest);
        setModal(false);
        setMovable(false);
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
                System.out.println("Co-op\n");
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