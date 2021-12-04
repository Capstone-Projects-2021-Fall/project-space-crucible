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

    final private Skin skin = new Skin(Gdx.files.internal("assets/uiskin.json"));
    private SettingsMenu settingsMenu = this;
    AddonWindow addonWindow = new AddonWindow("Add .WADs", skin);
    SoundSettings soundSettings = new SoundSettings("Volume", skin, settingsMenu);
    NameChangeWindow nameChangeWindow = new NameChangeWindow("Enter player name", skin, settingsMenu);

    public SettingsMenu(String title, Skin skin, Stage stage) {
        super(title, skin);
        setModal(false);
        setColor(0f,0f,0f,.8f);
        Button addonsButton = new TextButton("Add-ons", skin);
        addonsButton.setColor(.35f,.35f,.35f,1f);
        add(addonsButton).expand().fillY().fillX().padBottom(6f);
        row();
        Button sfxButton = new TextButton("Volume", skin);
        sfxButton.setColor(.35f,.35f,.35f,1f);
        add(sfxButton).expand().fillY().fillX().padBottom(6f);
        row();
        Button changeNameButton = new TextButton("Change Player Name", skin);
        changeNameButton.setColor(.35f,.35f,.35f,1f);
        add(changeNameButton).expand().fillY().fillX().padBottom(6f);
        row();
        Button backButton = new TextButton("Back", skin);
        backButton.setColor(.35f,.35f,.35f,1f);
        add(backButton).expand().fillY().fillX().padBottom(6f);
        row();
        pack();

        addonsButton.addListener(new ClickListener() {
             @Override
             public void clicked(InputEvent event, float x, float y) {
                 super.clicked(event, x, y);
                 remove();
                 addonWindow.setPosition(((Gdx.graphics.getWidth() - addonWindow.getWidth())/ 2f), ((Gdx.graphics.getHeight() - addonWindow.getHeight()) / 2f));
                 stage.addActor(addonWindow);
             }
        });
        sfxButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                setVisible(false);
                soundSettings.setPosition(((Gdx.graphics.getWidth() - soundSettings.getWidth())/ 2f), ((Gdx.graphics.getHeight() - soundSettings.getHeight()) / 2f));
                stage.addActor(soundSettings);
            }
        });
        changeNameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                setVisible(false);
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
    public void resize(){
        addonWindow.setPosition(((Gdx.graphics.getWidth() - addonWindow.getWidth())/ 2f), ((Gdx.graphics.getHeight() - addonWindow.getHeight()) / 2f));
        soundSettings.setPosition(((Gdx.graphics.getWidth() - soundSettings.getWidth())/ 2f), ((Gdx.graphics.getHeight() - soundSettings.getHeight()) / 2f));
        nameChangeWindow.setPosition(((Gdx.graphics.getWidth() - nameChangeWindow.getWidth())/ 2f), ((Gdx.graphics.getHeight() - nameChangeWindow.getHeight()) / 2f));
    }
}
