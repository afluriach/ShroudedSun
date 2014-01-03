package com.pezventure;

import java.util.List;
import java.util.Random;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.badlogic.gdx.utils.GdxNativesLoader;
import com.pezventure.graphics.SpriteLoader;
import com.pezventure.map.Area;
import com.pezventure.map.AreaLoader;
import com.pezventure.map.MapLink;
import com.pezventure.map.Room;
import com.pezventure.map.TileGraph;
import com.pezventure.objects.GameObjectSystem;
import com.pezventure.objects.Player;
import com.pezventure.objects.RenderLayer;
import com.pezventure.physics.Physics;

public class Game implements ApplicationListener
{
	public static Game inst;
	
	//constants
	public static final boolean physicsDebugRender = false;
	public static final boolean keyControls = true;
	
	public static final int DEFAULT_SCREEN_WIDTH = 1280;
	public static final int DEFAULT_SCREEN_HEIGHT = 720;
	
	public static final int GUI_EDGE_MARGIN = 20;
	
	public static final int BUTTON_RADIUS = 35;
	
	public static final int HEALTH_BAR_THICKNESS = 30;
	public static final int HEALTH_BAR_LENGTH = 200;
	public static final int HEALTH_BAR_OUTLINE = 4;
	
	public static final int HEALTH_MAGIC_SPACING = 10;

	public static final int MAGIC_BAR_THICKNESS = 30;
	public static final int MAGIC_BAR_LENGTH = 200;
	public static final int MAGIC_BAR_OUTLINE = 4;
	
	public static final Rectangle dialogPos = new Rectangle(350, 50, 500, 400);
	public static final float minDialogChangeTime = 0.25f;
		
	public static final int MAX_TOUCH_EVENTS = 5;
	
	public static final String TAG = "GensokyouAdventure";
	
	public static final int FRAMES_PER_SECOND = 60;
	public static final float SECONDS_PER_FRAME = 1.0f / FRAMES_PER_SECOND;
	
	public static final int PIXELS_PER_TILE = 32;
	public static final float TILES_PER_PIXEL = 1.0f / PIXELS_PER_TILE;
	
	public static final float ENTRANCE_CLEAR_DISTANCE = 0.5f;

	public static final String startingLevel = "level_select";
	public static final String startingLink = "entrance";
	
	private static final float abilityWaitInterval = 0.7f;
	
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
	TileGraph tileGraph;
	
	//control state
	Controls controls;
	//if the interact button has been held from the previous frame
	boolean interactHeld = false;
	boolean pauseHeld = false;
	boolean bHeld;
	boolean xHeld;
	boolean yHeld;
		
	//logic
	float updateTimeAccumulated = 0;
	public Player player;
	public Physics physics;
	public GameObjectSystem gameObjectSystem;
	public Random random = new Random();
	public Dialog activeDialog;
	float timeInDialog = 0f;
	boolean paused = false;
	public Runnable onExitDialog;
	String teleportDestLink;
	String teleportDestMap;
	
	//ability
	Ability bEquipped;
	Ability xEquipped;
	Ability yEquipped;
	boolean bToggleActive = false;
	boolean xToggleActive = false;
	boolean yToggleActive = false;
	float abilityWaitTimeRemaining;
	
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
		int hp = 0; 
		int mp = 0;
		
		if(player != null)
		{
			hp = player.getHP();
			mp = player.getMP();
		}
		
		player = new Player(Util.clearRectangle(link.location, link.entranceDir, ENTRANCE_CLEAR_DISTANCE), link.entranceDir);
		gameObjectSystem.addObject(player);
		gameObjectSystem.handleAdditions();
		
		if(hp != 0)
		{
			player.setHP(hp);
			player.setMP(mp);
		}
	}
	
	public static void log(String msg)
	{
		Gdx.app.log(Game.TAG, msg);
	}


	void drawGUI()
	{
		shapeRenderer.begin(ShapeType.Filled);
		int healthBarLen = (int) (HEALTH_BAR_LENGTH*player.getHP()*1.0f/player.getMaxHP());
		int magicBarLen  = (int) (MAGIC_BAR_LENGTH*player.getMP()*1.0f/player.getMaxMP());

		//health bar
		shapeRenderer.setColor(1,1,1,1);
		shapeRenderer.rect(GUI_EDGE_MARGIN,
						   screenHeight - HEALTH_BAR_THICKNESS - 2*HEALTH_BAR_OUTLINE - GUI_EDGE_MARGIN,
						   HEALTH_BAR_LENGTH+2*HEALTH_BAR_OUTLINE,
						   HEALTH_BAR_THICKNESS+2*HEALTH_BAR_OUTLINE);
		
		shapeRenderer.setColor(1,0,0,1);
		shapeRenderer.rect(GUI_EDGE_MARGIN + HEALTH_BAR_OUTLINE,
						   screenHeight - HEALTH_BAR_THICKNESS - HEALTH_BAR_OUTLINE - GUI_EDGE_MARGIN,
						   healthBarLen,
						   HEALTH_BAR_THICKNESS);
		
		shapeRenderer.setColor(0,0,0,1);
		shapeRenderer.rect(GUI_EDGE_MARGIN+HEALTH_BAR_OUTLINE+healthBarLen,
				           screenHeight - HEALTH_BAR_THICKNESS - HEALTH_BAR_OUTLINE - GUI_EDGE_MARGIN,
				           HEALTH_BAR_LENGTH - healthBarLen,
				           HEALTH_BAR_THICKNESS);
		
		//magic bar
		shapeRenderer.setColor(1,1,1,1);
		shapeRenderer.rect(GUI_EDGE_MARGIN,
						   screenHeight - HEALTH_BAR_THICKNESS - GUI_EDGE_MARGIN - 2*HEALTH_BAR_OUTLINE - HEALTH_MAGIC_SPACING - 2*MAGIC_BAR_OUTLINE - MAGIC_BAR_THICKNESS,
						   MAGIC_BAR_LENGTH+2*MAGIC_BAR_OUTLINE,
						   MAGIC_BAR_THICKNESS+2*MAGIC_BAR_OUTLINE);
		
		shapeRenderer.setColor(0f,0.8f,0.1f,1f);
		shapeRenderer.rect(GUI_EDGE_MARGIN+MAGIC_BAR_OUTLINE,
				           screenHeight - HEALTH_BAR_THICKNESS - GUI_EDGE_MARGIN - 2*HEALTH_BAR_OUTLINE - HEALTH_MAGIC_SPACING - MAGIC_BAR_OUTLINE - MAGIC_BAR_THICKNESS,
				           magicBarLen,
				           MAGIC_BAR_THICKNESS);
		
		shapeRenderer.setColor(0,0,0,1);
		shapeRenderer.rect(GUI_EDGE_MARGIN+MAGIC_BAR_OUTLINE+magicBarLen,
						   screenHeight - HEALTH_BAR_THICKNESS - GUI_EDGE_MARGIN - 2*HEALTH_BAR_OUTLINE - HEALTH_MAGIC_SPACING - MAGIC_BAR_OUTLINE - MAGIC_BAR_THICKNESS,
				           MAGIC_BAR_LENGTH - magicBarLen,
				           MAGIC_BAR_THICKNESS);
		
		shapeRenderer.end();		
		
		if(activeDialog == null)
			controls.render(shapeRenderer, guiBatch, font);
		else
		{
			activeDialog.render(batch, shapeRenderer);
		}
		
		if(paused)
		{
			//TODO draw box/background for the pause message, similar to a dialog box
			guiBatch.begin();
			batch.setColor(Color.WHITE);
			font.draw(guiBatch, "-PAUSED-", screenWidth/2, screenHeight/2);
			guiBatch.end();
		}
	}
	
	void handleInput()
	{
		controls.update();
		
		if(controls.pause && !pauseHeld)
		{
			paused = !paused;
			pauseHeld = true;
		}
		else
		{
			pauseHeld = controls.pause;
		}
		
		//do not handle other button presses if the game is paused.
		if(paused) return;
		
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
				
		if(controls.a && !interactHeld)
		{
			player.setInteract();
			interactHeld = true;
		}
		else
		{
			interactHeld = controls.a;
		}
		
		checkUpdateAbility();
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
		
		//default abilities
		bEquipped = new Shield();
		xEquipped = new IceBullet();
		
		//initialize game world
		loadArea(startingLevel, mapEntranceLink);
		
		initCamera();
	}
	
	void checkMaplinkCollision()
	{
		MapLink link = area.checkMaplinkCollision(player);
		
		//if no link is returned the player did not touch any maplink this frame
		//if destlink is empty then this link doesn't go anywhere
		if(link == null || link.destLink.equals("")) return;
		
		traverseLink(link.destMap, link.destLink);
	}
	
	public void setTeleporDestination(String destMap, String destLink)
	{
		teleportDestMap = destMap;
		teleportDestLink = destLink;
	}
	
	void checkTeleport()
	{
		if(teleportDestLink != null)
		{
			traverseLink(teleportDestMap, teleportDestLink);
			teleportDestLink = null;
		}
	}
		
	void traverseLink(String destMap, String destLink)
	{
		if(destMap != null && !destMap.equals(""))
		{
			loadArea(destMap, destLink);
			mapEntranceLink = destLink;
		}
		
		MapLink dest = area.getMapLink(destLink);		
		player.setPos(Util.clearRectangle(dest.location, dest.entranceDir, ENTRANCE_CLEAR_DISTANCE));
	}
		
	void loadArea(String areaName, String mapLink)
	{
		gameObjectSystem.clear();
		physics.clear();

		area = areaLoader.loadArea(areaName);
		
		mapRenderer = new OrthogonalTiledMapRenderer(area.map);
		
		Game.log("loading map objects...");
		area.instantiateMapObjects();
		Game.log("...done.");
		
		area.init();

		loadPlayer(mapLink);
		gameObjectSystem.initAll();
		
		tileGraph = new TileGraph(area);
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
		tileGraph.refresh();
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
		checkMaplinkCollision();
		checkTeleport();
		
		//handle input
		if(activeDialog == null)
			handleInput();
		else
		{
			timeInDialog += Game.SECONDS_PER_FRAME;
			handleDialogControls();
		}
		
		//update logic
		if(activeDialog == null && !paused)
		{
			updateTimeAccumulated += Gdx.graphics.getDeltaTime();
			while(updateTimeAccumulated >= SECONDS_PER_FRAME)
			{
				update();
				updateTimeAccumulated -= SECONDS_PER_FRAME;
			}
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
		gameObjectSystem.render(RenderLayer.groundLevel, batch);
		gameObjectSystem.render(RenderLayer.aboveGround, batch);
		batch.end();
		
		if(physicsDebugRender)
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
		
		//any touch on the dialog box to continue;
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
			
			if(onExitDialog != null)
			{
				onExitDialog.run();
				onExitDialog = null;
			}
		}
	}


	private void popScissors() {
		if(crntRoom != null)
		{
			ScissorStack.popScissors();
		}
	}


	private void pushScissors() {
		crntRoom = area.getCurrentRoom(player.getCenterPos());
		if(crntRoom != null)
		{
			
			Rectangle rect = crntRoom.getBox();

			Rectangle scissor = new Rectangle();
			
			Rectangle clip = new Rectangle();
			
			Vector2 pos = player.getCenterPos();
			
			clip.x = (rect.x - pos.x)*Game.PIXELS_PER_TILE;
			clip.y = (rect.y - pos.y)*Game.PIXELS_PER_TILE;
			clip.width = rect.width*Game.PIXELS_PER_TILE;
			clip.height = rect.height*Game.PIXELS_PER_TILE;
			
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
	
	public List<PathSegment> getPath(Vector2 startPos, Vector2 endPos)
	{
		return tileGraph.getPath(startPos, endPos);
	}
	
	public List<PathSegment> getPathWithinRadius(Vector2 start, Vector2 target, float minDist, float maxDist)
	{
		return tileGraph.radiusSearch(start, target, minDist, maxDist, area.getCurrentRoom(start).getBox());
	}
			
	void handleActionAbilityPress(ActionAbility a)
	{
		//can't use action ability when holding item
		if(abilityWaitTimeRemaining <= 0 && a.canPerform() && player.holdingItem == null)
		{
			a.perform();
			abilityWaitTimeRemaining = abilityWaitInterval;
		}
	}
	
	/**
	 * 
	 * @param a the ability to handle
	 * @param toggleActive whether or not this toggle ability is active
	 * @return whether or not this toggle ability is now active.
	 */
	boolean handleToggleAbilityPress(ToggleAbility a, boolean toggleActive)
	{
		//try to activate
		if(!toggleActive)
		{
			if(a.canActivate())
			{
				a.onActivate();
				return true;
			}
			else
			{
				return false;
			}
		}
		
		//deactivate
		else
		{
			a.onDeactivate();
			return false;
		}
	}
	
	void checkUpdateAbility()
	{
		if(!controls.b) bHeld = false;
		if(!controls.x) xHeld = false;
		if(!controls.y) yHeld = false;
		
		//only allow one ability use per frame.
		if(controls.b && bEquipped != null && !bHeld)
		{
			bHeld = true;
			
			if(bEquipped instanceof ToggleAbility)
				bToggleActive = handleToggleAbilityPress((ToggleAbility) bEquipped, bToggleActive);
			else
				handleActionAbilityPress((ActionAbility) bEquipped);
		}
		
		else if(controls.x && xEquipped != null && !xHeld)
		{
			xHeld = true;
			
			if(xEquipped instanceof ToggleAbility)
				xToggleActive = handleToggleAbilityPress((ToggleAbility) xEquipped, xToggleActive);
			else
				handleActionAbilityPress((ActionAbility) xEquipped);
		}
		
		else if(controls.y && yEquipped != null && !yHeld)
		{
			yHeld = true;
			
			if(yEquipped instanceof ToggleAbility)
				yToggleActive = handleToggleAbilityPress((ToggleAbility) yEquipped, yToggleActive);
			else
				handleActionAbilityPress((ActionAbility) yEquipped);
		}
		
		//update toggle ability.
		if(bToggleActive)
		{
			((ToggleAbility)bEquipped).updateActive();
		}
		if(xToggleActive)
		{
			((ToggleAbility)xEquipped).updateActive();
		}
		if(yToggleActive)
		{
			((ToggleAbility)yEquipped).updateActive();
		}
		
		//update ability countdown
		abilityWaitTimeRemaining -= Game.SECONDS_PER_FRAME;
		if(abilityWaitTimeRemaining < 0) abilityWaitTimeRemaining = 0;
	}
	
	public Area getCrntArea()
	{
		return area;
	}

}
