package com.pezventure;

import java.util.Random;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.badlogic.gdx.utils.GdxNativesLoader;
import com.pezventure.graphics.SpriteLoader;
import com.pezventure.map.Area;
import com.pezventure.map.AreaLoader;
import com.pezventure.map.Level1;
import com.pezventure.map.MapLink;
import com.pezventure.map.PuzzleRoom;
import com.pezventure.map.Room;
import com.pezventure.objects.GameObject;
import com.pezventure.objects.GameObjectSystem;
import com.pezventure.objects.Player;
import com.pezventure.objects.RenderLayer;
import com.pezventure.physics.Physics;
import com.pezventure.physics.PrimaryDirection;
import com.sun.corba.se.impl.oa.poa.ActiveObjectMap.Key;

public class Game implements ApplicationListener
{
	public static Game inst;
	
	//constants
	public static final boolean DEBUG = false;
	public static final boolean keyControls = true;
	
	public static final int DEFAULT_SCREEN_WIDTH = 1280;
	public static final int DEFAULT_SCREEN_HEIGHT = 720;
	
	public static final int GUI_EDGE_MARGIN = 20;
	
	public static final int BUTTON_RADIUS = 35;
	
	public static final int HEALTH_BAR_THICKNESS = 30;
	public static final int HEALTH_BAR_LENGTH = 200;
	public static final int HEALTH_BAR_OUTLINE = 4;
	
	public static final Rectangle dialogPos = new Rectangle(350, 50, 500, 400);
	public static final float minDialogChangeTime = 0.25f;
		
	public static final int MAX_TOUCH_EVENTS = 5;
	
	public static final String TAG = "PezVenture";
	
	public static final int FRAMES_PER_SECOND = 30;
	public static final float SECONDS_PER_FRAME = 1.0f / FRAMES_PER_SECOND;
	
	public static final int PIXELS_PER_TILE = 32;
	public static final float TILES_PER_PIXEL = 1.0f / PIXELS_PER_TILE;
	
	public static final float ENTRANCE_CLEAR_DISTANCE = 0.5f;

	public static final String startingLevel = "facer_floor";
	public static final String startingLink = "entrance";
	
	//graphics
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private SpriteBatch guiBatch;
	private BitmapFont font;
	private ShapeRenderer shapeRenderer;
	public SpriteLoader spriteLoader;

	//map
	Area area;
	String areaName = startingLevel;
	String mapEntranceLink=startingLink;
	AreaLoader areaLoader;
	OrthogonalTiledMapRenderer mapRenderer;
	Room crntRoom;
	
	//control state
	Controls controls;
	//if the interact button has been held from the previous frame
	boolean interactHeld = false;
		
	//logic
	float updateTimeAccumulated = 0;
	public Player player;
	public Physics physics;
	public GameObjectSystem gameObjectSystem;
	public Random random = new Random();
	public Dialog activeDialog;
	float timeInDialog = 0f;
	
	//game session info
	public boolean touchControls;
	public int screenHeight;
	public int screenWidth;
	
	enum state
	{
		play,
		dialog,
		pause,
		mainMenu,
	}
	
	public Game(boolean touch)
	{
		touchControls = touch;
		screenHeight = DEFAULT_SCREEN_HEIGHT;
		screenWidth = DEFAULT_SCREEN_WIDTH;
		
		inst = this;
		
	}

	
	public void loadPlayer(String mapLinkStart)
	{
		MapLink link = area.getMapLink(mapLinkStart);
		System.out.println("link: "+mapLinkStart);
		
		player = new Player(Util.clearRectangle(link.location, link.entranceDir, ENTRANCE_CLEAR_DISTANCE), link.entranceDir);
		gameObjectSystem.addObject(player);		
	}
	
	void drawGUI()
	{
		shapeRenderer.begin(ShapeType.Filled);
//		shapeRenderer.setColor(1, 0, 0, 1);
		int length = (int) (HEALTH_BAR_LENGTH*player.getHP()*1.0f/player.getMaxHP());
//		shapeRenderer.rect(GUI_EDGE_MARGIN, screenHeight-HEALTH_BAR_THICKNESS-GUI_EDGE_MARGIN, length, HEALTH_BAR_THICKNESS);
		
		shapeRenderer.setColor(1,1,1,1);
		shapeRenderer.rect(GUI_EDGE_MARGIN,
						   screenHeight - HEALTH_BAR_THICKNESS - 2*HEALTH_BAR_OUTLINE - GUI_EDGE_MARGIN,
						   HEALTH_BAR_LENGTH+2*HEALTH_BAR_OUTLINE,
						   HEALTH_BAR_THICKNESS+2*HEALTH_BAR_OUTLINE);
		
		shapeRenderer.setColor(1,0,0,1);
		shapeRenderer.rect(GUI_EDGE_MARGIN + HEALTH_BAR_OUTLINE,
						   screenHeight - HEALTH_BAR_THICKNESS - HEALTH_BAR_OUTLINE - GUI_EDGE_MARGIN,
						   length,
						   HEALTH_BAR_THICKNESS);
		
		shapeRenderer.setColor(0,0,0,1);
		shapeRenderer.rect(GUI_EDGE_MARGIN+HEALTH_BAR_OUTLINE+length,
				           screenHeight - HEALTH_BAR_THICKNESS - HEALTH_BAR_OUTLINE - GUI_EDGE_MARGIN,
				           HEALTH_BAR_LENGTH - length,
				           HEALTH_BAR_THICKNESS);
		
		shapeRenderer.end();		
		
		if(activeDialog == null)
			controls.render(shapeRenderer, guiBatch, font);
		else
		{
			activeDialog.render(batch, shapeRenderer);
		}
	}
	
	void handleInput()
	{
		controls.update();
		
		//set player movement
		//velocity and direction are handled separately.
		//in the case of diagonal movement, don't change direction if one direction matches current
		//e.g. facing up and direction is up-right
		
		//else, change direction to one whose component is closest to current direction
		//e.g. if facing right and down-left is pressed, face down
		
		player.setDesiredVel(Util.get8DirUnit(controls.controlPad8Dir).scl(Player.SPEED));
		
		//determine desired dir for the player
		//TODO add strafe lock 
		
		if(controls.controlPad8Dir == -1)
		{
			//no direction chosen, no change
		}
		
		else
		{
			player.setDesiredDir(controls.controlPad8Dir);
		}

		if(controls.x)
			player.setDesireToShoot();
				
		if(controls.a && !interactHeld)
		{
			player.setInteract();
			interactHeld = true;
		}
		else
		{
			interactHeld = controls.a;
		}
		
		player.shield = controls.b;
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
		batch = new SpriteBatch();
		guiBatch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
		//font = new BitmapFont();
		font = new BitmapFont(Gdx.files.internal("fonts/sansation.fnt"));
		
		physics = new Physics();
		spriteLoader = new SpriteLoader();
		gameObjectSystem = new GameObjectSystem();
		areaLoader = new AreaLoader();
		
		controls = new Controls(screenWidth, screenHeight, touchControls, keyControls);
		
		//initialize game world
		loadArea(startingLevel, mapEntranceLink);
		
		initCamera();
	}
	
	void handleMaplink()
	{
		MapLink link = area.checkMaplinkCollision(player);
		
		//if no link is returned the player did not touch any maplink this frame
		//if destlink is empty then this link doesn't go anywhere
		if(link == null || link.destLink.equals("")) return;
		
		if(!link.destMap.equals(""))
		{
			loadArea(link.destMap, link.destLink);
			mapEntranceLink = link.name;
		}
		
		MapLink dest = area.getMapLink(link.destLink);		
		player.setPos(Util.clearRectangle(dest.location, dest.entranceDir, ENTRANCE_CLEAR_DISTANCE));
	}
		
	void loadArea(String areaName, String mapLink)
	{
		gameObjectSystem.clear();
		physics.clear();

		area = areaLoader.loadArea(areaName);
		
		mapRenderer = new OrthogonalTiledMapRenderer(area.map);
		
		Gdx.app.log(Game.TAG, "loading map objects");
		area.instantiateMapObjects();
		area.init();
		
		loadPlayer(mapLink);
		
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
		gameObjectSystem.handleAdditions();
		gameObjectSystem.updateAll();
		gameObjectSystem.removeExpired();
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
		checkPlayerDeath();
		handleMaplink();

		
		//handle input
		if(activeDialog == null)
			handleInput();
		else
		{
			timeInDialog += Game.SECONDS_PER_FRAME;
			handleDialogControls();
		}
		
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
		
		//room occlusion
		pushScissors();
		
		Matrix4 defaultMatrix = batch.getProjectionMatrix();
		
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		mapRenderer.render();
		batch.end();
		
		batch.begin();
		gameObjectSystem.render(RenderLayer.floor, batch);
		gameObjectSystem.render(RenderLayer.above_floor, batch);
		batch.end();
		
		if(DEBUG)
			physics.debugRender(camera.combined);
		
		batch.setProjectionMatrix(defaultMatrix);
		
		popScissors();
		
		drawGUI();		
	}


	private void handleDialogControls()
	{
		boolean pressed = false;
		
		//ignore any button presses before the minimum elapsed time
		if(timeInDialog < minDialogChangeTime) return;
		
		if(touchControls)
		{
			for(int i=0; i< MAX_TOUCH_EVENTS; ++i)
			{
				if(Gdx.input.isTouched(i))
				{
					if(dialogPos.contains(Gdx.input.getX(i), screenHeight - Gdx.input.getY(i)))
					{
						pressed = true;
						break;
					}
				}
				else break;
			}

		}
		if(keyControls)
		{
			if(Gdx.input.isKeyPressed(Input.Keys.DOWN))
				pressed = true;
		}
		
		if(pressed)
		{
			activeDialog = null;
			timeInDialog = 0f;
		}
	}


	private void popScissors() {
		if(crntRoom != null)
			ScissorStack.popScissors();
	}


	private void pushScissors() {
		crntRoom = area.getCurrentRoom(player.getCenterPos());
		if(crntRoom != null)
		{			
			Rectangle scissor = new Rectangle();
			
			Rectangle clip = new Rectangle();
			
			Vector2 pos = player.getCenterPos();
			
			clip.x = (crntRoom.location.x - pos.x)*Game.PIXELS_PER_TILE;
			clip.y = (crntRoom.location.y - pos.y)*Game.PIXELS_PER_TILE;
			clip.width = crntRoom.location.width*Game.PIXELS_PER_TILE;
			clip.height = crntRoom.location.height*Game.PIXELS_PER_TILE;
			
			ScissorStack.calculateScissors(camera,
										   camera.position.x,
										   camera.position.y,
										   screenWidth,
										   screenHeight,
										   batch.getTransformMatrix(),
										   clip,
										   scissor);
			ScissorStack.pushScissors(scissor);
		}
	}

	public void setDialogMsg(String msg)
	{
		activeDialog = new Dialog(dialogPos, font, msg);
	}
	
	public void exitDialog()
	{
		activeDialog = null;
	}
	
	@Override
	public void resize(int width, int height) {
		screenWidth = width;
		screenHeight = height;
		
		controls.setResolution(width, height);
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
	
	public void checkPlayerDeath()
	{
		if(player.getHP() <= 0)
		{
			gameObjectSystem.clear();
			physics.clear();
			
			//reload current area
			loadArea(areaName,mapEntranceLink);
			initCamera();
		}
	}
	
}
