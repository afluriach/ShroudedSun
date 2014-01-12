package com.gensokyouadventure;

import java.util.List;
import java.util.Random;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.badlogic.gdx.utils.GdxNativesLoader;
import com.gensokyouadventure.ability.Ability;
import com.gensokyouadventure.ability.ActionAbility;
import com.gensokyouadventure.ability.IceBullet;
import com.gensokyouadventure.ability.Shield;
import com.gensokyouadventure.ability.ToggleAbility;
import com.gensokyouadventure.graphics.Graphics;
import com.gensokyouadventure.graphics.SpriteLoader;
import com.gensokyouadventure.map.Area;
import com.gensokyouadventure.map.AreaLoader;
import com.gensokyouadventure.map.MapLink;
import com.gensokyouadventure.map.Room;
import com.gensokyouadventure.map.TileGraph;
import com.gensokyouadventure.objects.GameObject;
import com.gensokyouadventure.objects.GameObjectSystem;
import com.gensokyouadventure.objects.RenderLayer;
import com.gensokyouadventure.objects.entity.Player;
import com.gensokyouadventure.objects.entity.enemies.Enemy;
import com.gensokyouadventure.physics.Physics;

public class Game implements ApplicationListener
{
	public static Game inst;
	
	//gui constants
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
	
	public static final Color HEALTH_BAR_COLOR = Graphics.hsva(350f, 0.8f, 0.8f, 1f);
	public static final Color MAGIC_BAR_COLOR = Graphics.hsva(135f, 0.7f, 0.7f, 1f);

	//control constants
	public static final boolean keyControls = true;
	public static final float minDialogChangeTime = 1f;
	private static final float abilityWaitInterval = 0.7f;	
	public static final int MAX_TOUCH_EVENTS = 5;
	
	//graphic constants
	public static final boolean physicsDebugRender = false;
	
	public static final int DEFAULT_SCREEN_WIDTH = 1280;
	public static final int DEFAULT_SCREEN_HEIGHT = 720;	
	
	public static final int FRAMES_PER_SECOND = 60;
	public static final float SECONDS_PER_FRAME = 1.0f / FRAMES_PER_SECOND;
	
	public static final int PIXELS_PER_TILE = 32;
	public static final float TILES_PER_PIXEL = 1.0f / PIXELS_PER_TILE;

	//game logic
	public static final float ENTRANCE_CLEAR_DISTANCE = 0.5f;

	public static final String startingLevel = "level_select";
	public static final String startingLink = "entrance";
	public static final String TAG = "GensokyouAdventure";
	
	
	//graphics
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private SpriteBatch guiBatch;
	public BitmapFont font;
	public ShapeRenderer shapeRenderer;
	public ShapeRenderer guiShapeRenderer;
	public SpriteLoader spriteLoader;

	//audio
	public AudioHandler soundLoader;
	
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
	//if the button has been held from the previous frame
	boolean interactHeld = false;
	boolean pauseHeld = false;
	boolean strafeHeld = false;
	boolean targetHeld = false;
	
	//toggle strafe
	boolean strafeEnabled = false;
	//if the player has a target selected, null otherwise.
	public GameObject target = null;
	boolean bHeld;
	boolean xHeld;
	boolean yHeld;
		
	//logic
	float updateTimeAccumulated = 0;
	public Player player;
	public Physics physics;
	public GameObjectSystem gameObjectSystem;
	public Random random = new Random();
	public TextBox activeTextBox;
	Dialog crntDialog;
	int crntConvsersationFrame;
	//time since the dialog or current frame of the conversation appeared
	float timeInDialog = 0f;
	boolean paused = false;
	public Runnable onExitDialog;
	String teleportDestLink;
	String teleportDestMap;
	public DialogLoader dialogLoader;
	
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
		guiShapeRenderer.begin(ShapeType.Filled);
		int healthBarLen = (int) (HEALTH_BAR_LENGTH*player.getHP()*1.0f/player.getMaxHP());
		int magicBarLen  = (int) (MAGIC_BAR_LENGTH*player.getMP()*1.0f/player.getMaxMP());

		//health bar
		guiShapeRenderer.setColor(1,1,1,1);
		guiShapeRenderer.rect(GUI_EDGE_MARGIN,
						   screenHeight - HEALTH_BAR_THICKNESS - 2*HEALTH_BAR_OUTLINE - GUI_EDGE_MARGIN,
						   HEALTH_BAR_LENGTH+2*HEALTH_BAR_OUTLINE,
						   HEALTH_BAR_THICKNESS+2*HEALTH_BAR_OUTLINE);
		
		guiShapeRenderer.setColor(HEALTH_BAR_COLOR);
		guiShapeRenderer.rect(GUI_EDGE_MARGIN + HEALTH_BAR_OUTLINE,
						   screenHeight - HEALTH_BAR_THICKNESS - HEALTH_BAR_OUTLINE - GUI_EDGE_MARGIN,
						   healthBarLen,
						   HEALTH_BAR_THICKNESS);
		
		guiShapeRenderer.setColor(0,0,0,1);
		guiShapeRenderer.rect(GUI_EDGE_MARGIN+HEALTH_BAR_OUTLINE+healthBarLen,
				           screenHeight - HEALTH_BAR_THICKNESS - HEALTH_BAR_OUTLINE - GUI_EDGE_MARGIN,
				           HEALTH_BAR_LENGTH - healthBarLen,
				           HEALTH_BAR_THICKNESS);
		
		//magic bar
		guiShapeRenderer.setColor(1,1,1,1);
		guiShapeRenderer.rect(GUI_EDGE_MARGIN,
						   screenHeight - HEALTH_BAR_THICKNESS - GUI_EDGE_MARGIN - 2*HEALTH_BAR_OUTLINE - HEALTH_MAGIC_SPACING - 2*MAGIC_BAR_OUTLINE - MAGIC_BAR_THICKNESS,
						   MAGIC_BAR_LENGTH+2*MAGIC_BAR_OUTLINE,
						   MAGIC_BAR_THICKNESS+2*MAGIC_BAR_OUTLINE);
		
		guiShapeRenderer.setColor(MAGIC_BAR_COLOR);
		guiShapeRenderer.rect(GUI_EDGE_MARGIN+MAGIC_BAR_OUTLINE,
				           screenHeight - HEALTH_BAR_THICKNESS - GUI_EDGE_MARGIN - 2*HEALTH_BAR_OUTLINE - HEALTH_MAGIC_SPACING - MAGIC_BAR_OUTLINE - MAGIC_BAR_THICKNESS,
				           magicBarLen,
				           MAGIC_BAR_THICKNESS);
		
		guiShapeRenderer.setColor(0,0,0,1);
		guiShapeRenderer.rect(GUI_EDGE_MARGIN+MAGIC_BAR_OUTLINE+magicBarLen,
						   screenHeight - HEALTH_BAR_THICKNESS - GUI_EDGE_MARGIN - 2*HEALTH_BAR_OUTLINE - HEALTH_MAGIC_SPACING - MAGIC_BAR_OUTLINE - MAGIC_BAR_THICKNESS,
				           MAGIC_BAR_LENGTH - magicBarLen,
				           MAGIC_BAR_THICKNESS);
		
		guiShapeRenderer.end();		
		
		if(crntDialog != null)
		{
			//switched to gui bach
			crntDialog.render(guiBatch, guiShapeRenderer, crntConvsersationFrame);
		}
		else if(activeTextBox != null)
		{
			activeTextBox.render(guiBatch, guiShapeRenderer);			
		}
		else
		{
			controls.render(guiShapeRenderer, guiBatch, font);			
		}
		
		if(paused)
		{
			//TODO draw box/background for the pause message, similar to a dialog box
			Graphics.drawTextCentered(Controls.buttonTextColor, "-PAUSED-", guiBatch, font, screenWidth/2, screenHeight/2);
		}
	}
	
	void pauseGame()
	{
		soundLoader.pauseMusic();
		paused = true;
	}
	
	void resumeGame()
	{
		soundLoader.resumeMusic();
		paused = false;
	}
	
	void handleInput()
	{
		controls.update();
		
		if(controls.pause && !pauseHeld)
		{
			if(paused)
				resumeGame();
			else
				pauseGame();
			
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
		
		if(controls.strafe && !strafeHeld)
		{
			strafeEnabled = !strafeEnabled;
			strafeHeld = true;
		}
		else
		{
			strafeHeld = controls.strafe;
		}
		
		if(controls.target && !targetHeld)
		{
			targetHeld = true;
			toggleTarget();
		}
		else if(!controls.screenTouches.isEmpty())
		{
			//allow player to target by touching targetable object
			toggleTouchTarget();
		}
		else
		{
			targetHeld = controls.target;
		}
		
		if(target != null && target.isExpired()) target = null;
		
		if(target != null)
		{
			Vector2 targetDir= target.getCenterPos().sub(player.getCenterPos()).nor(); 
			float angleToTarget = targetDir.angle();

			//face target
			//TODO change player heading to support arbritary angles, bullets
			//will aim at target better

			player.setDesiredAngle(angleToTarget);
		}

		player.setDesiredVel(controls.controlPadPos.scl(Player.SPEED));	
		
		if(target == null && controls.controlPadPos.len2() > 0 && !strafeEnabled)
		{
			//determine desired direction for the player to face, based on desired velocity.
			//do not change direction if strafe is enabled.
			player.setDesiredDir(Util.getNearestDir(controls.controlPadPos.angle()));
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
	
	void toggleTouchTarget()
	{
		boolean targetTouchedThisFrame = false;
		
		for(Vector2 pixPoint : controls.screenTouches)
		{
			Vector3 gamePoint = new Vector3(pixPoint.x, pixPoint.y, 0f);
			camera.unproject(gamePoint);
			gamePoint.x *= TILES_PER_PIXEL;
			gamePoint.y *= TILES_PER_PIXEL;
			Game.log(String.format("screen touch: %f,%f; gamePos: %f,%f", pixPoint.x, pixPoint.y, gamePoint.x, gamePoint.y));
			
			GameObject touched = physics.getTargetableObjectAtPoint(new Vector2(gamePoint.x, gamePoint.y));
			
			if(!targetHeld)
			{
				if(target != null && touched == target)
				{
					//untarget the object by touching it again
					target = null;
					targetTouchedThisFrame = true;
					break;
				}
				
				else if(target == null && touched != null)
				{
					//target the touched object
					target = touched;
					targetTouchedThisFrame = true;
					break;
				}				
			}
		}
		
		targetHeld = targetTouchedThisFrame;
	}


	private void toggleTarget() {
		//untarget if the player was targeting
		if(target != null)
			target = null;
		else
		{
			//find the best target based on highest dot product, 
			//i.e. closest to the center of the player's view
			float bestDot = 0f;
			GameObject bestObj = null; 
			for(GameObject go : player.getTargetableObjects())
			{
				Vector2 disp = go.getCenterPos().sub(player.getCenterPos());
				float dot = disp.nor().dot(Util.ray(player.getFacingAngle(), 1f));
				
				if(bestObj == null)
				{
					bestObj = go;
					bestDot = dot;
				}
				else if(dot > bestDot)
				{
					bestObj = go;
					bestDot = dot;
				}
			}
			
			if(bestObj != null) target = bestObj;
		}
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
		guiShapeRenderer = new ShapeRenderer();
		//font = new BitmapFont();
		font = new BitmapFont(Gdx.files.internal("fonts/sansation.fnt"));
		
		physics = new Physics();
		spriteLoader = new SpriteLoader();
		soundLoader = new AudioHandler();
		dialogLoader = new DialogLoader();
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
		
	public void traverseLink(String destMap, String destLink)
	{
		if(destMap != null && !destMap.equals(""))
		{
			loadArea(destMap, destLink);
			mapEntranceLink = destLink;
			areaName = destMap;
		}
		
		MapLink dest = area.getMapLink(destLink);		
		player.setPos(Util.clearRectangle(dest.location, dest.entranceDir, ENTRANCE_CLEAR_DISTANCE));
		player.setVel(Vector2.Zero);
	}
		
	void loadArea(String areaName, String mapLink)
	{
		Area oldArea = area;
		
		gameObjectSystem.clear();
		physics.clear();

		area = areaLoader.loadArea(areaName);
		
		//stop the music that is currently playing if the last area had music and it is a 
		//different track from the current area. which also means stop if this area doesn't
		//have music
		if(oldArea != null && oldArea.musicTitle != null)
		{
			if(area.musicTitle == null || !oldArea.musicTitle.equals(area.musicTitle))
			{
				soundLoader.stopMusic();
			}
		}
		
		if(area.musicTitle != null)
			soundLoader.playMusic(area.musicTitle, false);
		
		mapRenderer = new OrthogonalTiledMapRenderer(area.map);
		
		Game.log("loading map objects...");
		area.instantiateMapObjects();
		Game.log("...done.");

		loadPlayer(mapLink);
		gameObjectSystem.initAll();
		
		tileGraph = new TileGraph(area);
	}
	
	void initCamera()
	{
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
		guiBatch.dispose();
		spriteLoader.unloadTextures();
		font.dispose();
		guiShapeRenderer.dispose();
		mapRenderer.dispose();
	}
	
	@Override
	public void render() {
		checkPlayerDeath();
		checkMaplinkCollision();
		checkTeleport();
		
		controls.update();
		//handle input
		if(crntDialog != null)
		{
			timeInDialog += Game.SECONDS_PER_FRAME;
			handleConversationControls();
		}
		else if(activeTextBox != null)
		{
			timeInDialog += Game.SECONDS_PER_FRAME;
			handleDialogControls();
		}
		else
		{
			//normal gameplay input
			handleInput();			
		}
		
		//update logic
		if(activeTextBox == null && crntDialog == null && !paused)
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
		shapeRenderer.setProjectionMatrix(camera.combined);
		
		batch.begin();
		mapRenderer.render();
		batch.end();
		
		batch.begin();
		gameObjectSystem.render(RenderLayer.floor, batch);
		batch.end();
		gameObjectSystem.render(RenderLayer.floor, shapeRenderer);
		
		batch.begin();
		gameObjectSystem.render(RenderLayer.groundLevel, batch);
		batch.end();
		gameObjectSystem.render(RenderLayer.groundLevel, shapeRenderer);
		
		batch.begin();
		gameObjectSystem.render(RenderLayer.aboveGround, batch);
		batch.end();
		gameObjectSystem.render(RenderLayer.aboveGround, shapeRenderer);
		
		//draw targeting arrows
		batch.begin();
		if(target != null)
		{
			//use a dark arrow to indicate a selected target
			Texture arrowTexture = spriteLoader.getTexture(target instanceof Enemy ? "dark_yellow_arrow" : "dark_blue_arrow");
			Graphics.drawTexture(arrowTexture, target.getCenterPos().add(0f,0.5f), batch);
		}
		
		for(GameObject targetableObj : player.getTargetableObjects())
		{
			if(targetableObj == target) continue;
			
			Texture arrowTexture = spriteLoader.getTexture(targetableObj instanceof Enemy ? "yellow_arrow" : "blue_arrow");
			Graphics.drawTexture(arrowTexture, targetableObj.getCenterPos().add(0f,0.5f), batch);
		}
		batch.end();
		
		if(physicsDebugRender)
			physics.debugRender(camera.combined);
		
		batch.setProjectionMatrix(defaultMatrix);
		
		popScissors();
		
		drawGUI();		
	}

	void handleConversationControls()
	{
		boolean pressed = false;
		
		//ignore any button presses before the minimum elapsed time
		if(timeInDialog < minDialogChangeTime) return;

		//any touch on the dialog box to continue;
		if(touchControls)
		{
			Rectangle conversationDialogPos = Dialog.getDialogPos();
			if(Util.touchWithinRect(conversationDialogPos, MAX_TOUCH_EVENTS, screenHeight)) pressed = true;			
		}

		if(keyControls)
		{
			if(controls.a) pressed = true;
		}
		
		if(pressed)
		{
			++crntConvsersationFrame;
			timeInDialog = 0f;

			if(crntConvsersationFrame >= crntDialog.frames.length)
			{
				crntDialog = null;
				timeInDialog = 0f;

				if(onExitDialog != null)
				{
					onExitDialog.run();
					onExitDialog = null;
				}

			}
		}
	}
	
	private void handleDialogControls()
	{
		boolean pressed = false;
		
		//ignore any button presses before the minimum elapsed time
		if(timeInDialog < minDialogChangeTime) return;
		
		//any touch on the dialog box to continue;
		if(touchControls)
		{
			if(Util.touchWithinRect(dialogPos, MAX_TOUCH_EVENTS, screenHeight)) pressed = true;	
		}		
		if(keyControls)
		{
			if(controls.a) pressed = true;
		}
		
		if(pressed)
		{
			activeTextBox = null;
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
	
	/**
	 * 
	 * @param name the name of the dialog to play
	 * @param rightCharacter the character that the player is talking to.
	 */
	public void setDialog(String name, String rightCharacter)
	{
		//if the name contains a period, search in that collection.
		if(name.contains("."))
		{
			String[] split = name.split("\\.");
			crntDialog = dialogLoader.getDialog(split[0], split[1]);
		}
		//search in namespace of the current level
		else
		{
			crntDialog = dialogLoader.getDialog(areaName, name);
		}
		
		if(rightCharacter != null)
		{
			crntDialog.rightCharacter = rightCharacter;
		}
		crntDialog.leftCharacter = player.getCharacter();
		
		timeInDialog = 0f;
		crntConvsersationFrame = 0;
	}

	public void showTextBox(String msg)
	{
		activeTextBox = new TextBox(dialogPos, font, msg);
	}
	
	public void exitDialog()
	{
		activeTextBox = null;
	}
	
	@Override
	public void resize(int width, int height) {
		screenWidth = width;
		screenHeight = height;
		
		controls.setResolution(width, height);
	}

	@Override
	public void pause() {
		pauseGame();
	}

	@Override
	public void resume() {
		//do not auto-resume game.
	}
	
	public Matrix4 getCameraComb()
	{
		return camera.combined;
	}
	
	public void checkPlayerDeath()
	{
		if(player.getHP() <= 0)
		{
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
