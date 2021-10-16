package editor.gdx.windows;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import core.level.info.LevelData;
import core.wad.funcs.WadFuncs;
import editor.gdx.launch.EditorScreen;
import editor.gdx.windows.actors.NumberField;
import net.mtrop.doom.WadEntry;
import net.mtrop.doom.WadFile;

import java.util.HashMap;
import java.util.Map;

public class LevelChooserWindow extends Window {

    private WadFile file;
    private SelectBox<String> levelList;
    private TextButton newLevelButton;
    private TextButton okButton;
    private EditorScreen editor;
    private Map<String, Integer> levelNumToName;

    private class NewLevelWindow extends Window {

        private TextField name;
        private NumberField levelnum;
        private TextButton okButton;
        private TextButton cancelButton;
        private Map<String, Integer> levelNumToName;

        public NewLevelWindow(Skin skin, Map<String, Integer> levelNumToName, LevelChooserWindow parent) {
            super("New Level:", skin);
            this.levelNumToName = levelNumToName;

            setModal(true);

            name = new TextField("", skin);
            add(new Label("Level Name:", skin));
            add(name);
            row();

            levelnum = new NumberField("", skin);
            add(new Label("Level Number:", skin));
            add(levelnum);
            row();

            okButton = new TextButton("OK", skin);
            okButton.addListener(new ClickListener() {

                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);
                    if (levelNumToName.containsValue(Integer.parseInt(levelnum.getText()))) {
                        System.out.println("Level number taken, please pick another.");
                    } else if (levelNumToName.containsKey(name.getText())) {
                        System.out.println("Level name taken, please pick another,");
                    } else {
                        parent.openNewLevel(name.getText(), Integer.parseInt(levelnum.getText()));
                        remove();
                    }
                }
            });

            cancelButton = new TextButton("Cancel", skin);
            cancelButton.addListener(new ClickListener() {

                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);
                    remove();
                }
            });
            add(okButton);
            add(cancelButton);
            pack();
        }
    }

    public LevelChooserWindow(String title, Skin skin, WadFile file, EditorScreen editor) {
        super(title, skin);
        this.file = file;
        this.editor = editor;
        levelList = new SelectBox<>(skin);
        newLevelButton = new TextButton("+", skin);
        okButton = new TextButton("OK", skin);
        levelNumToName = new HashMap<>();

        setModal(true);

        listLevels();

        add(levelList);
        add(newLevelButton);
        row();
        add(okButton);
        pack();

        newLevelButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                addLevel();
            }
        });

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

                LevelData data = WadFuncs.loadLevel(file, level, editor.resources);
                if (data != null) {
                    if (data.getName() != null) {
                        levelNames.add(data.getName());
                        levelNumToName.put(data.getName(), level);
                    } else {
                        levelNames.add("Level " + level);
                        levelNumToName.put("Level " + level, level);
                    }
                }
            }
        }

        levelList.setItems(levelNames);
    }

    private void addLevel() {
        editor.stage.addActor(new NewLevelWindow(getSkin(), levelNumToName, this));
    }

    private void openLevel() {

        if (levelList.getSelected() == null) {
            return;
        }

        editor.levelnum = levelNumToName.get(levelList.getSelected());
        editor.loadLevel();
        remove();
        editor.windowOpen = false;
    }

    private void openNewLevel(String name, Integer level) {

        editor.loadNewLevel(name, level);
        remove();
        editor.windowOpen = false;
    }
}
