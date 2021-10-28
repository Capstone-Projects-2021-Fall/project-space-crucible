package core.gdx.wad;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class ChatWindow extends com.badlogic.gdx.scenes.scene2d.ui.Window {
    MyGDxTest myGDxTest;
    GameScreen gameScreen;
    public ChatWindow(String title, Skin skin, SettingsScreen settingsScreen, Stage stage, MyGDxTest myGDxTest) {
        super(title, skin);
        setModal(false);
        setMovable(false);
        this.myGDxTest = myGDxTest;
        this.gameScreen = gameScreen;


    }
}
