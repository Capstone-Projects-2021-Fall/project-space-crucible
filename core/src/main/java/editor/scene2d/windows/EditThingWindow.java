package editor.scene2d.windows;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import core.game.entities.Entity;
import core.game.logic.GameLogic;
import core.gdx.wad.RenderFuncs;
import core.level.info.LevelObject;
import editor.launch.EditorScreen;
import editor.scene2d.actors.NumberField;
import net.mtrop.doom.WadFile;

public class EditThingWindow extends Window {

    Array<WadFile> resources;
    Entity entity;
    LevelObject obj;
    Array<LevelObject> objs = new Array<>();
    EditorScreen editor;
    Image sprite;
    NumberField typeField;
    NumberField xField;
    NumberField yField;
    NumberField angleField;
    CheckBox singleCheck;
    CheckBox coopCheck;
    CheckBox skill1Check;
    CheckBox skill2Check;
    CheckBox skill3Check;
    CheckBox skill4Check;
    CheckBox skill5Check;
    CheckBox ambushCheck;
    NumberField tagField;
    NumberField layerField;
    TextButton okButton;
    TextButton cancelButton;

    public EditThingWindow(String title, Skin skin, Entity entity, Array<LevelObject> objs, Array<WadFile> resources, EditorScreen editor) {
        this(title, skin, entity, objs.get(0), resources, editor);
        this.objs = objs;
    }

    public EditThingWindow(String title, Skin skin, Entity entity, LevelObject obj, Array<WadFile> resources, EditorScreen editor) {
        super(title, skin);
        this.resources = resources;
        this.entity = entity;
        this.obj = obj;
        this.editor = editor;

        if (objs.isEmpty()) {objs.add(obj);}

        add(new Label("Thing:", skin));
        row();
        sprite = new Image(RenderFuncs.spriteMap.get(entity.getCurrentSprite()).getFrame(entity.getCurrentFrame(), entity.getPos().angle));
        typeField = new NumberField(Integer.toString(obj.type), skin);
        add(sprite);
        add(typeField);
        row();
        add(new Label("Angle:", skin));
        angleField = new NumberField(Float.toString(entity.getPos().angle), skin);
        add(angleField);
        row();
        add(new Label("Singleplayer:", skin));
        singleCheck = new CheckBox("", skin);
        singleCheck.setChecked(obj.singleplayer);
        add(singleCheck);
        row();
        add(new Label("Cooperative:", skin));
        coopCheck = new CheckBox("", skin);
        coopCheck.setChecked(obj.cooperative);
        add(coopCheck);
        row();
        add(new Label("Skill 1:", skin));
        skill1Check = new CheckBox("", skin);
        skill1Check.setChecked(obj.skill[0]);
        add(skill1Check);
        row();
        add(new Label("Skill 2:", skin));
        skill2Check = new CheckBox("", skin);
        skill2Check.setChecked(obj.skill[1]);
        add(skill2Check);
        row();
        add(new Label("Skill 3:", skin));
        skill3Check = new CheckBox("", skin);
        skill3Check.setChecked(obj.skill[2]);
        add(skill3Check);
        row();
        add(new Label("Skill 4:", skin));
        skill4Check = new CheckBox("", skin);
        skill4Check.setChecked(obj.skill[3]);
        add(skill4Check);
        row();
        add(new Label("Skill 5:", skin));
        skill5Check = new CheckBox("", skin);
        skill5Check.setChecked(obj.skill[4]);
        add(skill5Check);
        row();
        add(new Label("Ambush:", skin));
        ambushCheck = new CheckBox("", skin);
        ambushCheck.setChecked(obj.ambush);
        add(ambushCheck);
        row();
        add(new Label("Tag:", skin));
        tagField = new NumberField(Integer.toString(obj.tag), skin);
        add(tagField);
        row();
        add(new Label("Layer:", skin));
        layerField = new NumberField(Integer.toString(obj.layer), skin);
        add(layerField);
        row();
        okButton = new TextButton("OK", skin);

        okButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                changeThing();
            }
        });

        cancelButton = new TextButton("Cancel", skin);

        cancelButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                close();
            }
        });
        add(okButton);
        add(cancelButton);
        pack();
    }

    private void changeThing() {

        try {
            Float.parseFloat(angleField.getText());
        } catch (NumberFormatException n) {
            System.out.println("Position fields are not readable, try again");
            return;
        }

        for (LevelObject lo : objs) {
            lo.type = typeField.getInteger();
            //lo.xpos = Float.parseFloat(xField.getText());
            //lo.ypos = Float.parseFloat(yField.getText());
            lo.angle = angleField.getFloat();
            lo.singleplayer = singleCheck.isChecked();
            lo.cooperative = coopCheck.isChecked();
            lo.skill[0] = skill1Check.isChecked();
            lo.skill[1] = skill2Check.isChecked();
            lo.skill[2] = skill3Check.isChecked();
            lo.skill[3] = skill4Check.isChecked();
            lo.skill[4] = skill5Check.isChecked();
            lo.ambush = ambushCheck.isChecked();
            lo.tag = tagField.getInteger();
            lo.layer = layerField.getInteger();
        }
        GameLogic.loadEntities(editor.level, true);
        close();
    }

    private void close() {
        remove();
        editor.windowOpen = false;
    }

}
