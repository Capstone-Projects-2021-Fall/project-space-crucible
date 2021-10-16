package editor.gdx.windows;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.*;
import com.badlogic.gdx.utils.Array;
import core.level.info.LevelTile;
import core.wad.funcs.WadFuncs;
import editor.gdx.launch.EditorScreen;
import editor.gdx.windows.actors.NumberField;
import net.mtrop.doom.WadFile;

public class EditTileWindow extends Window {

    Array<WadFile> resources;
    LevelTile tile;
    Image tileTexture;
    SelectBox<String> textureList;
    CheckBox solidCheckBox;
    Slider lightSlider;
    NumberField effectField;
    NumberField arg1Field;
    NumberField arg2Field;
    CheckBox repeatCheckBox;
    NumberField tagField;
    TextButton okButton;
    TextButton cancelButton;
    EditorScreen editor;

    //Creates a new EditTileWindow instance
    public EditTileWindow(String title, Skin skin, LevelTile tile, Array<WadFile> resources, EditorScreen editor) {
        super(title, skin);
        this.resources = resources;
        this.tile = tile;
        this.editor = editor;

        //Focus this window
        setModal(true);

        //Set the image preview to the tile's texture
        tileTexture = new Image(tile.graphic);
        add(tileTexture);
        textureList = new SelectBox<>(skin);
        textureList.setItems(getTextureList());
        textureList.setSelected(tile.graphicname);

        textureList.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                changePreview();
            }
        });

        //Add buttons, fields, etc.
        add(textureList);
        row();
        solidCheckBox = new CheckBox("Solid", skin);
        solidCheckBox.setChecked(tile.solid);
        add(solidCheckBox);
        row();
        add(new Label("Light:", skin));
        lightSlider = new Slider(0, 255, 1, false, skin);
        lightSlider.setValue(tile.light);
        add(lightSlider);
        row();
        add(new Label("Effect:", skin));
        effectField = new NumberField(Integer.toString(tile.effect), skin);
        add(effectField);
        row();
        add(new Label("Arg 1:", skin));
        arg1Field = new NumberField(Integer.toString(tile.arg1), skin);
        add(arg1Field);
        row();
        add(new Label("Arg 2:", skin));
        arg2Field = new NumberField(Integer.toString(tile.arg2), skin);
        add(arg2Field);
        row();
        repeatCheckBox = new CheckBox("Repeat", skin);
        repeatCheckBox.setChecked(tile.repeat);
        add(repeatCheckBox);
        row();
        add(new Label("Tag:", skin));
        tagField = new NumberField(Integer.toString(tile.tag), skin);
        add(tagField);
        row();
        okButton = new TextButton("OK", skin);

        okButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                changeTile();
            }
        });

        add(okButton);
        cancelButton = new TextButton("Cancel", skin);

        cancelButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                close();
            }
        });

        add(cancelButton);
        pack();


    }

    private void changePreview() {

        tileTexture.setDrawable(new TextureRegionDrawable(WadFuncs.getTexture(resources, textureList.getSelected())));
    }

    private Array<String> getTextureList() {

        Array<String> list = new Array<>();

        for (WadFile w : resources) {

            if (!w.contains("G_START") || !w.contains("G_END")) {continue;}

            int start = w.lastIndexOf("G_START") + 1;
            int end = w.lastIndexOf("G_END");

            for (int i = start; i < end; i++) {

                if (!list.contains(w.getEntry(i).getName(), false)) {
                    list.add(w.getEntry(i).getName());
                    System.out.println(w.getEntry(i).getName());
                }
            }
        }

        return list;
    }

    private void close() {
        remove();
        editor.windowOpen = false;
    }

    private void changeTile() {
        tile.graphicname = textureList.getSelected();
        tile.graphic = WadFuncs.getTexture(resources, tile.graphicname);
        tile.solid = solidCheckBox.isChecked();
        tile.light = (int) lightSlider.getValue();
        tile.effect = Integer.parseInt(effectField.getText());
        tile.arg1 = Integer.parseInt(arg1Field.getText());
        tile.arg2 = Integer.parseInt(arg2Field.getText());
        tile.repeat = repeatCheckBox.isChecked();
        tile.tag = Integer.parseInt(tagField.getText());

        close();
    }
}
