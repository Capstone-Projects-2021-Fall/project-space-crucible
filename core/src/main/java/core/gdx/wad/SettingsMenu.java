package core.gdx.wad;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class SettingsMenu extends Window {

    private SettingsMenu settingsMenu = this;
    public SettingsMenu(String title, Skin skin, Stage stage) {
        super(title, skin);
        setModal(false);

        Button addonsButton = new TextButton("Add-ons", skin);
        add(addonsButton);
        row();
        Button sfxButton = new TextButton("Volume", skin);
        add(sfxButton);
        row();
        Button changeNameButton = new TextButton("Change Player Name", skin);
        add(changeNameButton);
        row();
        Button backButton = new TextButton("Back", skin);
        add(backButton);
        row();
        pack();

        addonsButton.addListener(new ClickListener() {
             @Override
             public void clicked(InputEvent event, float x, float y) {
                 super.clicked(event, x, y);
                 setVisible(false);
                 AddonWindow addonWindow = new AddonWindow("Add .WADs", skin);
                 addonWindow.setPosition(((Gdx.graphics.getWidth() - addonWindow.getWidth())/ 2f), ((Gdx.graphics.getHeight() - addonWindow.getHeight()) / 2f));
                 stage.addActor(addonWindow);
             }
        });
        sfxButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                setVisible(false);
                SoundSettings soundSettings = new SoundSettings("Volume", skin, settingsMenu);
                soundSettings.setPosition(((Gdx.graphics.getWidth() - soundSettings.getWidth())/ 2f), ((Gdx.graphics.getHeight() - soundSettings.getHeight()) / 2f));
                stage.addActor(soundSettings);
            }
        });
        changeNameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                setVisible(false);
                NameChangeWindow nameChangeWindow = new NameChangeWindow("Enter player name", skin, settingsMenu);
                nameChangeWindow.setPosition(((Gdx.graphics.getWidth() - nameChangeWindow.getWidth())/ 2f), ((Gdx.graphics.getHeight() - nameChangeWindow.getHeight()) / 2f));
                stage.addActor(nameChangeWindow);
            }
        });
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                remove();
                TitleScreen.mainMenuTable.setVisible(true);
            }
        });
    }
}
