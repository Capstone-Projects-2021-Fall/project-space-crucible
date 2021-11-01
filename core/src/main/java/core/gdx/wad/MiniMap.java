package core.gdx.wad;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import core.level.info.LevelData;

import java.awt.*;

public class MiniMap extends Actor {
    MyGDxTest myGDxTest;
    GameScreen gameScreen;
    SpriteBatch spriteBatch;
    Texture texture;


    public MiniMap(LevelData levelData) {
        this.myGDxTest = myGDxTest;
        this.gameScreen = gameScreen;
        this.spriteBatch = spriteBatch;
        this.texture = texture;

        Rectangle mapTiles = new Rectangle(); //black background of map


        //1. draw a solid black rectangle in top left corner based on size of map
        //2. for every solid tile in this map, draw a white square in the black rectangle
        //3. draw a red square in the black rectangle (the player's position)




    }
}
