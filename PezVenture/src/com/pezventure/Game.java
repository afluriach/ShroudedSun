package com.pezventure;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GLCommon;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.Map;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.TmxMapLoader.Parameters;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.GdxNativesLoader;
import com.pezventure.graphics.SpriteLoader;
import com.pezventure.map.Area;
import com.pezventure.map.Level1;
import com.pezventure.objects.GameObject;
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
	boolean up = false;
	boolean down = false;
	boolean left = false;
	boolean right = false;
	
	//GUI elements
	Rectangle dpadHorizontal;
	Rectangle dpadVertical;
	
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
		
		if(touchControls)
		{
			drawDpad();
			drawButtons();
		}
	}
	
	void drawButtons()
	{
		shapeRenderer.begin(ShapeType.Filled);
		
		//center button
		shapeRenderer.setColor(Color.DARK_GRAY);
		shapeRenderer.circle(screenWidth-GUI_EDGE_MARGIN-3*BUTTON_RADIUS, GUI_EDGE_MARGIN+3*BUTTON_RADIUS, BUTTON_RADIUS);
		
		//left button
		shapeRenderer.setColor(Color.BLUE);
		shapeRenderer.circle(screenWidth-GUI_EDGE_MARGIN-5*BUTTON_RADIUS, GUI_EDGE_MARGIN+3*BUTTON_RADIUS, BUTTON_RADIUS);
		
		//top button
		shapeRenderer.setColor(Color.YELLOW);
		shapeRenderer.circle(screenWidth-GUI_EDGE_MARGIN-3*BUTTON_RADIUS, GUI_EDGE_MARGIN+5*BUTTON_RADIUS, BUTTON_RADIUS);
		
		//right button
		shapeRenderer.setColor(Color.RED);
		shapeRenderer.circle(screenWidth-GUI_EDGE_MARGIN-BUTTON_RADIUS, GUI_EDGE_MARGIN + 3*BUTTON_RADIUS, BUTTON_RADIUS);
		
		//lower button
		shapeRenderer.setColor(Color.GREEN);
		shapeRenderer.circle(screenWidth-GUI_EDGE_MARGIN-3*BUTTON_RADIUS, GUI_EDGE_MARGIN+BUTTON_RADIUS, BUTTON_RADIUS);

		shapeRenderer.end();
	}
	
	void drawDpad()
	{
		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.setColor(Color.DARK_GRAY);
		
		//horizontal bar
		shapeRenderer.rect(GUI_EDGE_MARGIN, DPAD_LENGTH/2-DPAD_THICKNESS/2+GUI_EDGE_MARGIN, DPAD_LENGTH, DPAD_THICKNESS);
		//vertical  bar
		shapeRenderer.rect(DPAD_LENGTH/2-DPAD_THICKNESS/2+GUI_EDGE_MARGIN, GUI_EDGE_MARGIN, DPAD_THICKNESS, DPAD_LENGTH);
		shapeRenderer.end();
	}
	
	void handleInput()
	{
		//blank control state from previous frame
		up = false;
		down = false;
		right = false;
		left = false;
		
		//if has keyboard/using keyboard
		//keyboard
		
		if(touchControls)
		{
			handleTouchInputs();
		}
		else
		{
			handleKeyboard();
		}
		
		//set player movement
		PrimaryDirection dir = null;
		
		if(up)
			dir = PrimaryDirection.up;
		else if(down)
			dir = PrimaryDirection.down;
		else if(left)
			dir = PrimaryDirection.left;
		else if(right)
			dir = PrimaryDirection.right;
		
		player.setDesiredDir(dir);	
	}
	
	void handleTouchInputs()
	{
		for(int id=0; id < MAX_TOUCH_EVENTS; ++id)
		{
			if(Gdx.input.isTouched(id))
			{
				//Gdx.app.log(TAG, String.format("touch event: %d,%d", Gdx.input.getX(id), screenHeight - Gdx.input.getY(id)));
				
				handleTouchEvent(Gdx.input.getX(id), screenHeight - Gdx.input.getY(id));
			}
		}
	}
	
	void handleTouchEvent(int x, int y)
	{
		if(y-GUI_EDGE_MARGIN>= DPAD_LENGTH/2-DPAD_THICKNESS/2 && y-GUI_EDGE_MARGIN <= DPAD_LENGTH/2+DPAD_THICKNESS/2)
		{
			//may be on the horizontal bar of the d-pad.
			
			if(x-GUI_EDGE_MARGIN < DPAD_LENGTH/2 - DPAD_THICKNESS/2)
			{
				left = true;
			}
			else if(x-GUI_EDGE_MARGIN > DPAD_LENGTH/2 + DPAD_THICKNESS/2 && x-GUI_EDGE_MARGIN <= DPAD_LENGTH)
			{
				right = true;
			}
		}
		
		if(x-GUI_EDGE_MARGIN>= DPAD_LENGTH/2 - DPAD_THICKNESS/2 && x-GUI_EDGE_MARGIN <= DPAD_LENGTH/2 + DPAD_THICKNESS/2)
		{
			//may be on the vertical bar of the d-pad
			
			if(y-GUI_EDGE_MARGIN < DPAD_LENGTH/2 - DPAD_THICKNESS/2)
			{
				down = true;
			}
			else if(y-GUI_EDGE_MARGIN > DPAD_LENGTH/2 + DPAD_THICKNESS/2 && y-GUI_EDGE_MARGIN <= DPAD_LENGTH)
			{
				up = true;
			}
		}
	}
	
	void handleKeyboard()
	{
		up = Gdx.input.isKeyPressed(Keys.W);
		down = Gdx.input.isKeyPressed(Keys.S);
		left = Gdx.input.isKeyPressed(Keys.A);
		right = Gdx.input.isKeyPressed(Keys.D);
	}
	
	@Override
	public void create() {
		inst = this;
		
		//load box2d library
		GdxNativesLoader.load();
		
		screenWidth = Gdx.graphics.getWidth();
		screenHeight = Gdx.graphics.getHeight();
		
		//TODO check camera scaling based on tilespace area
	//	camera = new OrthographicCamera(1, screenHeight/screenWidth);
		camera = new OrthographicCamera(screenWidth, screenHeight);
		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
		font = new BitmapFont();
		
		physics = new Physics();
		spriteLoader = new SpriteLoader();
		gameObjectSystem = new GameObjectSystem();
		
		//initialize game world
		loadMap();
		loadPlayer();
		
		initGuiElements();

		initCamera();
	}
	
	void initGuiElements()
	{		
		dpadHorizontal = new Rectangle(GUI_EDGE_MARGIN, DPAD_LENGTH/2-DPAD_THICKNESS/2+GUI_EDGE_MARGIN, DPAD_LENGTH, DPAD_THICKNESS);
		dpadVertical = new Rectangle(DPAD_LENGTH/2-DPAD_THICKNESS/2+GUI_EDGE_MARGIN, GUI_EDGE_MARGIN, DPAD_THICKNESS, DPAD_LENGTH);
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
		
		initGuiElements();
		
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
