package com.pezventure;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.GdxNativesLoader;
import com.pezventure.graphics.SpriteLoader;
import com.pezventure.map.Area;
import com.pezventure.map.Level1;
import com.pezventure.objects.GameObjectSystem;
import com.pezventure.objects.Player;
import com.pezventure.objects.RenderLayer;
import com.pezventure.physics.Physics;
import com.pezventure.physics.PrimaryDirection;

public class Game implements ApplicationListener
{
	public static Game inst;
	
	//constants
	public static final boolean DEBUG = true;
	public static final boolean keyControls = true;
	
	public static final int DEFAULT_SCREEN_WIDTH = 1280;
	public static final int DEFAULT_SCREEN_HEIGHT = 720;
	
	public static final int GUI_EDGE_MARGIN = 20;
	
	public static final int BUTTON_RADIUS = 35;
	
	public static final int HEALTH_BAR_THICKNESS = 30;
	public static final int HEALTH_BAR_LENGTH = 200;
	
	public static final int DPAD_LENGTH = 210;
	public static final int DPAD_THICKNESS = 60;
	
	public static final int MAX_TOUCH_EVENTS = 5;
	
	public static final String TAG = "PezVenture";
	
	public static final int FRAMES_PER_SECOND = 30;
	public static final float SECONDS_PER_FRAME = 1.0f / FRAMES_PER_SECOND;
	
	public static final int PIXELS_PER_TILE = 32;
	public static final float TILES_PER_PIXEL = 1.0f / PIXELS_PER_TILE;

	//graphics
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private BitmapFont font;
	private ShapeRenderer shapeRenderer;
	public SpriteLoader spriteLoader;

	//map
	Area area;
	OrthogonalTiledMapRenderer mapRenderer;
	
	//control state
	Controls controls;
	
	//GUI elements
	Rectangle dpadUp;
	Rectangle dpadDown;
	Rectangle dpadLeft;
	Rectangle dpadRight;
	
	Circle buttonA;
	Circle buttonB;
	Circle buttonX;
	Circle buttonY;
	
	
	
	//logic
	float updateTimeAccumulated = 0;
	Player player;
	public Physics physics;
	public GameObjectSystem gameObjectSystem;
	
	//game session info
	public boolean touchControls;
	public int screenHeight;
	public int screenWidth;
	
	public Game(boolean touch)
	{
		touchControls = touch;
		screenHeight = DEFAULT_SCREEN_HEIGHT;
		screenWidth = DEFAULT_SCREEN_WIDTH;
		
		inst = this;
		
	}

	
	public void loadPlayer()
	{
		player = new Player(area.playerStartPos);
		gameObjectSystem.addObject(player);		
	}
	
	void drawGUI()
	{
		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.setColor(1, 0, 0, 1);
		int length = (int) (HEALTH_BAR_LENGTH*player.getHP()*1.0f/player.getMaxHP());
		shapeRenderer.rect(GUI_EDGE_MARGIN, screenHeight-HEALTH_BAR_THICKNESS-GUI_EDGE_MARGIN, length, HEALTH_BAR_THICKNESS);
		shapeRenderer.end();		
		
		controls.render(shapeRenderer);
	}
	
	void handleInput()
	{
		controls.update();
		
		//set player movement
		PrimaryDirection dir = null;
		
		if(controls.up)
			dir = PrimaryDirection.up;
		else if(controls.down)
			dir = PrimaryDirection.down;
		else if(controls.left)
			dir = PrimaryDirection.left;
		else if(controls.right)
			dir = PrimaryDirection.right;
		
		player.setDesiredDir(dir);	
	}
		
	
	@Override
	public void create() {
		inst = this;
		
		//load box2d library
		if(Gdx.app.getType() == ApplicationType.Android || Gdx.app.getType() == ApplicationType.Desktop )
			GdxNativesLoader.load();
		
		screenWidth = Gdx.graphics.getWidth();
		screenHeight = Gdx.graphics.getHeight();
		
		//TODO check camera scaling based on tilespace area
	//	camera = new OrthographicCamera(1, screenHeight/screenWidth);
		camera = new OrthographicCamera(screenWidth, screenHeight);
		controls = new Controls(screenWidth, screenHeight, touchControls, keyControls);
		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
		font = new BitmapFont();
		
		physics = new Physics();
		spriteLoader = new SpriteLoader();
		gameObjectSystem = new GameObjectSystem();
		
		//initialize game world
		loadMap();
		loadPlayer();
		
		initCamera();
	}
	
	void loadMap()
	{
		//initialize game world
		area = new Level1();
		area.instantiateMapObjects();
		area.init();
		mapRenderer = new OrthogonalTiledMapRenderer(area.map);
	}
	
	void initCamera()
	{
//		camera.translate(-300, -300);
//		camera.setToOrtho(true);
		camera.position.set(screenWidth/2, screenHeight/2, 0);
		mapRenderer.setView(camera);
		
		camera.update();
		camera.apply(Gdx.graphics.getGL10());
	}
	
	void update()
	{
		area.update();
		gameObjectSystem.updateAll();
		physics.update();
	}

	
	void moveCamera()
	{
		Vector2 pos = player.getCenterPos();
		camera.position.set(pos.x*PIXELS_PER_TILE, pos.y*PIXELS_PER_TILE, 0);
//		if(up)
//			camera.translate(0,5,0);
//		if(down)
//			camera.translate(0,-5,0);
	}


	@Override
	public void dispose()
	{
		batch.dispose();
		spriteLoader.unloadTextures();
		font.dispose();
		shapeRenderer.dispose();
		mapRenderer.dispose();
	}
	
	@Override
	public void render() {
		//handle input
		handleInput();
		
		//update logic
		updateTimeAccumulated += Gdx.graphics.getDeltaTime();
		while(updateTimeAccumulated >= SECONDS_PER_FRAME)
		{
			update();
			updateTimeAccumulated -= SECONDS_PER_FRAME;
		}
		
		//adjust camera
		moveCamera();
		camera.update();
		camera.apply(Gdx.graphics.getGL10());
		mapRenderer.setView(camera);
		
		//render
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT | GL10.GL_STENCIL_BUFFER_BIT);
		//Gdx.gl.glViewport(0, 0, screenWidth, screenHeight);
		
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		mapRenderer.render();
		batch.end();
		
		batch.begin();
		gameObjectSystem.render(RenderLayer.floor, batch);
		gameObjectSystem.render(RenderLayer.ground, batch);
		batch.end();
		
		if(DEBUG)
			physics.debugRender(camera.combined);
		
		drawGUI();
		
		Vector2 pos = player.getCenterPos();
		batch.begin();
		font.draw(batch, String.format("char pos: %f,%f", pos.x, pos.y), 50+camera.position.x, 50+camera.position.y);
		batch.end();
	}

	@Override
	public void resize(int width, int height) {
		screenWidth = width;
		screenHeight = height;
		
		controls.setResolution(width, height);
		
//		camera.setToOrtho(false, width, height);
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
	
	public Matrix4 getCameraComb()
	{
		return camera.combined;
	}
	
}
