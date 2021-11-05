package core.gdx.wad;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import core.level.info.LevelData;

public class MiniMap extends Actor {
    MyGDxTest myGDxTest;
    GameScreen gameScreen;
    SpriteBatch spriteBatch;
    Texture texture;
    //OrthographicCamera camera = new OrthographicCamera();



    public MiniMap(){
        this.myGDxTest = myGDxTest;
        this.gameScreen = gameScreen;
        this.spriteBatch = spriteBatch;
        this.texture = texture;

        ShapeRenderer shapeRenderer = new ShapeRenderer();
//        Rectangle mapTiles = new Rectangle(100,100);
//        mapTiles.setLocation(0, (int) camera.viewportHeight);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(com.badlogic.gdx.graphics.Color.RED);
        shapeRenderer.rect(0, 0, 100,100);
//        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.end();




        //1. draw a solid black rectangle in top left corner based on size of map
        //2. for every solid tile in this map, draw a white square in the black rectangle
        //3. draw a red square in the black rectangle (the player's position)




    }

    public static void renderTiles(LevelData levelData){

    }
}
