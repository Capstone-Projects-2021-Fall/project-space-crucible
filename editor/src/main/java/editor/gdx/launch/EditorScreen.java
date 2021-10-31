package editor.gdx.launch;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import core.game.entities.Entity;
import core.game.entities.PlayerPawn;
import core.game.logic.CollisionLogic;
import core.game.logic.GameLogic;
import core.gdx.wad.RenderFuncs;
import core.level.info.LevelData;
import core.level.info.LevelObject;
import core.level.info.LevelTile;
import core.wad.funcs.WadFuncs;
import editor.gdx.copy.CopiedThingData;
import editor.gdx.copy.CopiedTileData;
import editor.gdx.windows.EditThingWindow;
import editor.gdx.windows.EditTileWindow;
import editor.gdx.windows.FileChooserWindow;
import editor.gdx.windows.LevelChooserWindow;
import editor.gdx.write.LevelWriter;
import net.mtrop.doom.WadFile;
import org.lwjgl.system.CallbackI;

import javax.swing.*;
import java.io.IOException;

public class EditorScreen implements Screen {

    private LevelEditor editor;
    private OrthographicCamera camera;
    private ShapeRenderer sr;
    private SpriteBatch batch;
    private float cameraspeed = 5;
    private Vector3 mouseInWorld = new Vector3();

    public WadFile file = null;
    public Array<WadFile> resources = new Array<>();
    public LevelData level;
    public Integer levelnum;
    public boolean windowOpen;

    //Copy-paste
    private CopiedTileData copiedTileData = null;
    private CopiedThingData copiedThingData = null;

    //Dragging
    private boolean dragging = false;
    private LevelObject dragThing = null;

    //Selection
    private boolean selecting = false;
    private boolean selectThings = false;
    private float selectionX1 = 0;
    private float selectionY1 = 0;
    private float selectionX2 = 0;
    private float selectionY2 = 0;
    private Array<LevelObject> selectedObjs = new Array<>();
    private Array<LevelTile> selectedTiles = new Array<>();

    //UI Stuff
    public Stage stage = new Stage(new ScreenViewport());
    final private Skin skin = new Skin(Gdx.files.internal("assets/uiskin.json"));

    public EditorScreen(LevelEditor editor) {
        this.editor = editor;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1920, 1080);
        camera.position.set(0, 0, 0);
        batch = new SpriteBatch();
        sr = new ShapeRenderer();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        openFilePrompt();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0,0,0,1F);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        moveCamera();
        checkShortcuts();
        checkControls();

        if (level != null) {
            batch.setProjectionMatrix(camera.combined);
            batch.begin();
            RenderFuncs.worldDraw(batch, level.getTiles(), true);
            RenderFuncs.entityDraw(batch, GameLogic.entityList);
            batch.end();

            sr.setProjectionMatrix(camera.combined);
            sr.begin(ShapeRenderer.ShapeType.Line);
            if (selecting || !selectedTiles.isEmpty() || !selectedObjs.isEmpty()) {
                drawSelectionBox();
            }
            RenderFuncs.gridDraw(camera, sr);
            sr.end();
        }

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    private void checkControls() {

        if (windowOpen) {return;}

        float x = (int)(mouseInWorld.x);
        float y = (int)(mouseInWorld.y);

        int tilex = (int) x/LevelTile.TILE_SIZE;
        int tiley = (int) y/LevelTile.TILE_SIZE;

        if (x < 0) {tilex--;}
        if (y < 0) {tiley--;}

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            selectedObjs.clear();
            selectedTiles.clear();
            selecting = false;
        }

        //Check for right click
        if(Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
            rightClick(x, y, tilex, tiley);
        }

        //Check for middle click
        if (Gdx.input.isButtonJustPressed(Input.Buttons.MIDDLE)) {
            middleClick(x, y, tilex, tiley);
        }

        //Check for left clip
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            leftClick(x, y, tilex, tiley);
        }

        //If not holding left click, stop dragging or selecting
        else {
            if (dragging) {
                dragging = false;
                GameLogic.loadEntities(level, true);
            }

            if (selecting) {
                selectionX2 = x;
                selectionY2 = y;

                makeSelection();
                selecting = false;
            }
        }
    }

    private void checkShortcuts() {

        if (windowOpen) {return;}

        float x = (int)(mouseInWorld.x);
        float y = (int)(mouseInWorld.y);

        int tilex = (int) x/LevelTile.TILE_SIZE;
        int tiley = (int) y/LevelTile.TILE_SIZE;

        if (x < 0) {tilex--;}
        if (y < 0) {tiley--;}


        if (isCtrlPressed()) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.S)) {

                try {
                    LevelWriter.write(file, level, levelnum);
                } catch (IOException e) {
                    System.out.println("Could not save!");
                    e.printStackTrace();
                }

            } else if (Gdx.input.isKeyJustPressed(Input.Keys.O)) {
                if (isShiftPressed()) {
                    level = null;
                    openLevelPrompt();
                    windowOpen = true;
                } else {
                    openFilePrompt();
                    windowOpen = true;
                }
            } else if (Gdx.input.isKeyJustPressed(Input.Keys.C)) {
                System.out.println("Copy!");

                //If you're hovering on an object, copy it.
                for (Entity e : GameLogic.entityList) {
                    if (e.getBounds().contains(x, y)) {
                        LevelObject obj = level.getObjects().get(GameLogic.entityList.indexOf(e));
                        copiedThingData = new CopiedThingData(obj.type, obj.angle, obj.singleplayer,
                                obj.cooperative, obj.skill, obj.ambush, obj.tag);
                        return;
                    }
                }

                LevelTile tile = level.getTile(tilex, tiley);
                if (tile != null) {
                    copiedTileData = new CopiedTileData(tile.solid, tile.graphicname, tile.light,
                            tile.effect, tile.arg1, tile.arg2, tile.repeat, tile.tag, resources);
                }
            } else  if (Gdx.input.isKeyJustPressed(Input.Keys.V)) {
                if (isShiftPressed()) {
                    if (copiedThingData == null) {
                        return;
                    }
                    pasteThing(x, y);
                } else {
                    if (copiedTileData == null) {
                        return;
                    }
                    pasteTile(tilex, tiley);
                }
            }
        }
    }

    private void moveCamera() {

        if (windowOpen) {return;}

        if(Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A))
            camera.position.set(camera.position.x - cameraspeed, camera.position.y, 0);
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D))
            camera.position.set(camera.position.x + cameraspeed, camera.position.y, 0);
        if(Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W))
            camera.position.set(camera.position.x, camera.position.y + cameraspeed, 0);
        if(Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S))
            camera.position.set(camera.position.x, camera.position.y - cameraspeed, 0);

        camera.update();
        //System.out.println(camera.position.x + ", " + camera.position.y);

        mouseInWorld.x = Gdx.input.getX();
        mouseInWorld.y = Gdx.input.getY();
        mouseInWorld.z = 0;
        camera.unproject(mouseInWorld);
    }

    //Show menu for editing level tiles
    private void editTilePrompt(int tilex, int tiley) {

        LevelTile tile = level.getTile(tilex, tiley);

        if (tile == null) {
            tile = new LevelTile(new LevelTile.TilePosition(tilex, tiley),
                    false, "TAN1", 0, 0,
                    0, 0, false, 0);
            level.getTiles().add(tile);
        }

        stage.addActor(new EditTileWindow("Edit Tile", skin, tile, resources, this));
        windowOpen = true;
    }

    private void editTilePrompt() {
        stage.addActor(new EditTileWindow("Edit Tiles", skin, selectedTiles, resources, this));
        windowOpen = true;
    }

    //Edit game Entities
    private void editThingPrompt(LevelObject obj, Entity e) {
        stage.addActor(new EditThingWindow("Edit Thing", skin, e, obj, resources, this));
        windowOpen = true;
    }

    private void editThingPrompt(Entity e) {
        stage.addActor(new EditThingWindow("Edit Things", skin, e, selectedObjs, resources, this));
        windowOpen = true;
    }

    public void openFilePrompt() {
        windowOpen = true;
        stage.addActor(new FileChooserWindow("Choose File:", skin, this));
    }

    public void openLevelPrompt() {

        if (checkForNoTextures()) {
            System.out.println("No textures found. Try loading a resource file.");
            openFilePrompt();
            return;
        }

        windowOpen = true;
        stage.addActor(new LevelChooserWindow("Choose ", skin, file, this));
    }

    @Override
    public void resize(int width, int height) {
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
    }

    public void loadLevel() {
        level = WadFuncs.loadLevel(file, levelnum);

        WadFuncs.loadSprites(resources);
        WadFuncs.loadStates();
        WadFuncs.loadTextures(resources);
        WadFuncs.setEntityTypes();
        GameLogic.loadEntities(level, true);
    }

    public void loadNewLevel(String name, Integer level) {
        this.level = new LevelData(name, level);
        levelnum = level;
    }

    private boolean checkForNoTextures() {

        for (WadFile w : resources) {

            if (w.contains("G_START") && w.contains("G_END")) {
                return false;
            }
        }

        return true;
    }

    private void pasteTile(int tilex, int tiley) {

        LevelTile tile = level.getTile(tilex, tiley);

        if (tile != null) {
            tile.graphicname = copiedTileData.graphicname;
            tile.solid = copiedTileData.solid;
            tile.light = copiedTileData.light;
            tile.effect = copiedTileData.effect;
            tile.arg1 = copiedTileData.arg1;
            tile.arg2 = copiedTileData.arg2;
            tile.repeat = copiedTileData.repeat;
            tile.tag = copiedTileData.tag;
        } else {
            tile = new LevelTile(new LevelTile.TilePosition(tilex, tiley), copiedTileData.solid,
                    copiedTileData.graphicname, copiedTileData.light, copiedTileData.effect, copiedTileData.arg1,
                    copiedTileData.arg2, copiedTileData.repeat, copiedTileData.tag);
            level.getTiles().add(tile);
        }
    }

    private void pasteThing(float x, float y) {
        level.getObjects().add(new LevelObject(copiedThingData.type, x, y, copiedThingData.angle,
                copiedThingData.singleplayer, copiedThingData.cooperative, copiedThingData.skill,
                copiedThingData.ambush, copiedThingData.tag));
        GameLogic.loadEntities(level, true);
    }

    private boolean isCtrlPressed() {
        return (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) || Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT));
    }

    private boolean isShiftPressed() {
        return (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT));
    }

    private void leftClick(float x, float y, int tilex, int tiley) {

        //Check if *just* pressed in particular
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {

            if (isCtrlPressed()) {
                selecting = true;
                selectionX1 = x;
                selectionY1 = y;

                selectThings = isShiftPressed();

            } else {
                //If you clicked on an object, drag it.
                for (Entity e : GameLogic.entityList) {
                    if (e.getBounds().contains(x, y)) {
                        dragThing = level.getObjects().get(GameLogic.entityList.indexOf(e));
                        dragging = true;
                        return;
                    }
                }
            }
        } else {
            if (dragging) {
                dragThing.xpos = x;
                dragThing.ypos = y;
                GameLogic.loadEntities(level, true);
            }
        }
    }

    private void middleClick(float x, float y, int tilex, int tiley) {

        for (Entity e : GameLogic.entityList) {
            if (e.getBounds().contains(x, y)) {
                int index = GameLogic.entityList.indexOf(e);
                GameLogic.entityList.remove(index);
                level.getObjects().remove(index);
                GameLogic.loadEntities(level, true);
                return;
            }
        }

        LevelTile tile = level.getTile(tilex, tiley);
        level.getTiles().remove(tile);
    }

    private void rightClick(float x, float y, int tilex, int tiley) {

        //First check if anything is selected
        if (!selectedObjs.isEmpty()) {
            editThingPrompt(GameLogic.entityList.get(level.getObjects().indexOf(selectedObjs.get(0))));
            return;
        }

        if (!selectedTiles.isEmpty()) {
            editTilePrompt();
            return;
        }

        if (isShiftPressed()) {
            LevelObject newObj = new LevelObject(0, x, y, 0, true, true,
                    new boolean[]{true, true, true, true, true}, false, 0);
            level.getObjects().add(newObj);

            Entity newThing = new PlayerPawn(new Entity.Position(x, y, 0), 0);
            GameLogic.entityList.add(newThing);

            GameLogic.loadEntities(level, true);
            editThingPrompt(newObj, newThing);
            return;
        }

        //If you clicked on an object, edit it.
        for (Entity e : GameLogic.entityList) {
            if (e.getBounds().contains(x, y)) {
                LevelObject obj = level.getObjects().get(GameLogic.entityList.indexOf(e));
                editThingPrompt(obj, e);
                return;
            }
        }

        editTilePrompt(tilex, tiley);
    }

    private void makeSelection() {

        selectedObjs.clear();
        selectedTiles.clear();

        Rectangle selectionBounds = new Rectangle(
                Math.min(selectionX1, selectionX2),
                Math.min(selectionY1, selectionY2),
                Math.abs(selectionX1 - selectionX2), Math.abs(selectionY1 - selectionY2)
        );

        if (selectThings) {

            for (Entity e : GameLogic.entityList) {
                if (selectionBounds.overlaps(e.getBounds())) {
                    selectedObjs.add(level.getObjects().get(GameLogic.entityList.indexOf(e)));
                }
            }
            //System.out.println(selectedObjs);
        } else {

            for (LevelTile t : level.getTiles()) {
                Rectangle tileBounds
                        = new Rectangle(t.pos.x * LevelTile.TILE_SIZE,
                        t.pos.y * LevelTile.TILE_SIZE,
                            LevelTile.TILE_SIZE, LevelTile.TILE_SIZE);

                if (selectionBounds.overlaps(tileBounds)) {
                    selectedTiles.add(t);
                }
            }
            //System.out.println(selectedTiles);
        }
    }

    private void drawSelectionBox() {

        float x = (int)(mouseInWorld.x);
        float y = (int)(mouseInWorld.y);

        Color color = selectThings ? Color.GREEN : Color.BLUE;

        if (selecting) {
            sr.rect(
                    Math.min(x, selectionX1),
                    Math.min(y, selectionY1),
                    Math.abs(x - selectionX1),
                    Math.abs(y - selectionY1),
                   color, color, color, color
            );
        } else {
            sr.rect(
                    Math.min(selectionX2, selectionX1),
                    Math.min(selectionY2, selectionY1),
                    Math.abs(selectionX2 - selectionX1),
                    Math.abs(selectionY2 - selectionY1),
                    color, color, color, color
            );
        }

    }
}
