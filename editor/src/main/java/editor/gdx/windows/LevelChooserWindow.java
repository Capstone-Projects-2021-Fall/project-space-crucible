package editor.gdx.windows;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import core.level.info.LevelData;
import core.wad.funcs.WadFuncs;
import editor.gdx.launch.EditorScreen;
import net.mtrop.doom.WadEntry;
import net.mtrop.doom.WadFile;

public class LevelChooserWindow extends Window {

    WadFile file;
    SelectBox<String> levelList;
    TextButton newLevelButton;
    TextButton okButton;
    EditorScreen editor;

    public LevelChooserWindow(String title, Skin skin, WadFile file, EditorScreen editor) {
        super(title, skin);
        this.file = file;
        this.editor = editor;
        levelList = new SelectBox<>(skin);
        newLevelButton = new TextButton("+", skin);
        okButton = new TextButton("OK", skin);
        setModal(true);

        listLevels();

        add(levelList);
        add(newLevelButton);
        row();
        add(okButton);
        pack();

        /*
        newLevelButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                levelList.
            }
        });*/

        okButton.addListener(new ClickListener() {
           @Override
           public void clicked(InputEvent event, float x, float y) {
               openLevel();
           }
        });
    }

    private void listLevels() {

        Array<String> levelNames = new Array<>();

        for (WadEntry we : file) {
            if (we.getName().startsWith("LEVEL")) {
                int level = Integer.parseInt(we.getName().substring(5));

                LevelData data = WadFuncs.loadLevel(file, level);
                if (data != null) {
                    if (data.getName() != null) {
                        levelNames.add(data.getName());
                    } else {
                        levelNames.add("Level " + level);
                    }
                }
            }
        }

        levelList.setItems(levelNames);
    }

    private void openLevel() {
        editor.levelnum = levelList.getSelectedIndex()+1;
        editor.loadLevel();
        remove();
        editor.windowOpen = false;
    }
}
