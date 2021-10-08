package com.projectspacecrucuble;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;

public class SpaceCrucible extends ApplicationAdapter  { //implements InputProcessor for event based
	SpriteBatch batch;
	Texture img;
	private float x, y;
	Sprite player;
	private OrthographicCamera camera;

	public static final float SPEED = 60;

	@Override
	public void create () {
		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");
		player = new Sprite(img);
		camera = new OrthographicCamera(500, 500);
		camera.update();

		//Register the input processor for the event based input handling
//		Gdx.input.setInputProcessor(this);
	}

	@Override
	public void render ( ) {
		//Input handling with polling method
		//This handles all the keys pressed with the keyboard.
		if(Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A))
			x -= SPEED * Gdx.graphics.getDeltaTime();
		if(Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D))
			x += SPEED * Gdx.graphics.getDeltaTime();
		if(Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W))
			y += SPEED * Gdx.graphics.getDeltaTime();
		if(Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S))
			y -= SPEED * Gdx.graphics.getDeltaTime();

		Vector2 mousePos = new Vector2(Gdx.input.getX(), Gdx.input.getY());
		float angle = mousePos.angleDeg();
		batch.setProjectionMatrix(camera.combined);
		player.setRotation(angle);
		ScreenUtils.clear(0, 0, 0, 1);
		batch.begin();
		batch.draw(player, x, y);
		batch.end();
	}

	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
	}

//
//	//Event based input handling methods
//	@Override
//	public boolean keyDown(int keycode) {
//		if(keycode == Input.Keys.LEFT)
//				player.translateX(-1f);
//		if(keycode == Input.Keys.RIGHT)
//			player.translateX(1f);
//		if(keycode == Input.Keys.UP)
//			player.translateY(1f);
//		if(keycode == Input.Keys.DOWN)
//			player.translateY(-1f);
//		return false;
//	}
//
//	@Override
//	public boolean keyUp(int keycode) {
//		return false;
//	}
//
//	@Override
//	public boolean keyTyped(char character) {
//		return false;
//	}
//
//	@Override
//	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
//		return false;
//	}
//
//	@Override
//	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
//		return false;
//	}
//
//	@Override
//	public boolean touchDragged(int screenX, int screenY, int pointer) {
//		return false;
//	}
//
//	@Override
//	public boolean mouseMoved(int screenX, int screenY) {
//		player.setPosition(screenX, Gdx.graphics.getHeight() - screenY);
//		return false;
//	}
//
//	@Override
//	public boolean scrolled(float amountX, float amountY) {
//		return false;
//	}
}
