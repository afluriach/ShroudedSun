package com.gensokyouadventure;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.gensokyouadventure.graphics.Graphics;

/**
 * Implements touch and button controls and keep track of control state.
 * @author ant
 * 
 */
public class Controls
{
	//constants
	public static final int maxPressEvents = 5;
	
	public static final int margin = 20;
	public static final int buttonRadius = 40;
	public static final int buttonTrimThickness = 5;
	
	public static final int controlpadDiameter = 320;
	public static final int controlpadDeadzone = 40;
	
	static final ButtonColorScheme colors = ButtonColorScheme.scheme;
	
	//touch
	boolean touchControls;
	boolean keyControls;
	
	//screen
	int width;
	int height;
	
	//control state
	int controlPad8Dir = -1;
	
	boolean a = false;
	boolean b = false;
	boolean x = false;
	boolean y = false;
	
	boolean pause = false;
	
	//GUI elements	
	Circle buttonA;
	Circle buttonB;
	Circle buttonX;
	Circle buttonY;
	
	Circle buttonPause;
	Circle controlPad;
		
	public Controls(int width, int height, boolean touch, boolean keys)
	{
		this.width = width;
		this.height = height;
		touchControls = touch;
		keyControls = keys;
		createShapes();
	}
	
	/**
	 * used to change GUI layout if screen resolution changes
	 */
	public void setResolution(int width, int height)
	{
		this.width = width;
		this.height = height;
		createShapes();
	}
	
	public void createShapes()
	{
		Vector2 controlpadCenter = new Vector2(margin+controlpadDiameter/2, margin+controlpadDiameter/2);

		controlPad = new Circle(controlpadCenter.x, controlpadCenter.y, controlpadDiameter/2);
		
		buttonA = new Circle(width-margin-3*buttonRadius, margin+buttonRadius, buttonRadius);
		buttonB = new Circle(width-margin-buttonRadius, margin + 3*buttonRadius, buttonRadius);
		buttonX = new Circle(width-margin-5*buttonRadius, margin+3*buttonRadius, buttonRadius);
		buttonY = new Circle(width-margin-3*buttonRadius, margin+5*buttonRadius, buttonRadius);
		
		buttonPause = new Circle(width/2, margin+buttonRadius, buttonRadius);
	}
	
	//buttons will be drawn dark with a light trim, unless they
	//are pressed, then the inner color will be light as well.
	public void drawButtonInner(ShapeRenderer shapeRenderer, boolean pressed, Color light, Color dark, Circle button)
	{
		shapeRenderer.setColor(pressed ? light : dark);
		
		Graphics.drawCircle(shapeRenderer, button);
	}
	
	public void drawButtonOuter(ShapeRenderer shapeRenderer, Color color, Circle button)
	{
		shapeRenderer.setColor(color);
		shapeRenderer.circle(button.x, button.y, button.radius + buttonTrimThickness);
	}
	
	public void drawDpadSegment(ShapeRenderer shapeRenderer, boolean pressed, Rectangle segment)
	{
		shapeRenderer.setColor(pressed ? colors.dpaddark : colors.dpadlight);
		
		shapeRenderer.rect(segment.x, segment.y, segment.width, segment.height);
	}
	
	public void drawDpadTriangle(ShapeRenderer sr, boolean activated, IsoscelesTriangle tri)
	{
		sr.setColor(activated ? colors.dpaddark : colors.dpadlight);
		
		tri.render(sr);
	}
	
	public void render(ShapeRenderer shapeRenderer, SpriteBatch batch, BitmapFont font)
	{
		shapeRenderer.begin(ShapeType.Filled);
		
		drawButtonOuter(shapeRenderer, colors.alight, buttonA);
		drawButtonOuter(shapeRenderer, colors.blight, buttonB);
		drawButtonOuter(shapeRenderer, colors.xlight, buttonX);
		drawButtonOuter(shapeRenderer, colors.ylight, buttonY);
		
		drawButtonInner(shapeRenderer,a, colors.alight, colors.adark, buttonA);
		drawButtonInner(shapeRenderer,b, colors.blight, colors.bdark, buttonB);
		drawButtonInner(shapeRenderer,x, colors.xlight, colors.xdark, buttonX);
		drawButtonInner(shapeRenderer,y, colors.ylight, colors.ydark, buttonY);
				
		if(touchControls)
		{
			drawButtonOuter(shapeRenderer, colors.dpadlight, buttonPause);
			drawButtonInner(shapeRenderer, pause, colors.dpadlight, colors.dpaddark, buttonPause);

			drawButtonOuter(shapeRenderer, colors.dpadlight, controlPad);
			drawButtonInner(shapeRenderer, false, colors.dpadlight, colors.dpaddark, controlPad);
		}
		
		shapeRenderer.end();
		
		batch.begin();
		
		if(Game.inst.player.holdingItem != null)
			batch.setColor(1f, 1f, 1f, 0.3f);
		
		//TODO do not draw other buttons if paused
		//TODO draw icon transparent if not available
		
		if(Game.inst.bEquipped != null)
			Graphics.drawTexture(Game.inst.bEquipped.icon,  new Vector2(buttonB.x*Game.TILES_PER_PIXEL,  buttonB.y*Game.TILES_PER_PIXEL), batch);

		if(Game.inst.xEquipped != null)
			Graphics.drawTexture(Game.inst.xEquipped.icon,  new Vector2(buttonX.x*Game.TILES_PER_PIXEL,  buttonX.y*Game.TILES_PER_PIXEL), batch);

		if(Game.inst.yEquipped != null)
			Graphics.drawTexture(Game.inst.yEquipped.icon,  new Vector2(buttonY.x*Game.TILES_PER_PIXEL,  buttonY.y*Game.TILES_PER_PIXEL), batch);

		batch.setColor(1f, 1f, 1f, 1f);
		
		batch.end();
		
		drawInteractMessage(batch, font);
		
		if(touchControls)
			drawPauseMessage(batch, font);
	}

	private void drawPauseMessage(SpriteBatch batch, BitmapFont font) {
		String pauseMsg = Game.inst.paused ? "resume" : "pause";
		
		drawTextCentered(pauseMsg, batch, font, buttonPause.x-buttonA.radius+10, buttonPause.y, 0.75f);
	}

	private void drawInteractMessage(SpriteBatch batch, BitmapFont font) {
		String interactMsg = Game.inst.player.interactMessage;
				
		drawTextCentered(interactMsg, batch, font, buttonA.x-buttonA.radius+10, buttonA.y, 1f);
	}
	
	void drawTextCentered(String msg, SpriteBatch batch, BitmapFont font, float x, float y, float scale)
	{
		font.setScale(scale);
		
		batch.begin();
		font.draw(batch, msg, x, y+font.getLineHeight()/2);
		batch.end();
		
		font.setScale(1f);
	}
	
	private void handleTouchEvents()
	{
		for(int i=0; i< maxPressEvents; ++i)
		{
			if(Gdx.input.isTouched(i))
				checkTouchPress(Gdx.input.getX(i), height - Gdx.input.getY(i));
			else break;
		}
	}
	
	private void checkTouchPress(int x, int y)
	{		
		if(buttonA.contains(x,y)) a = true;
		if(buttonB.contains(x,y)) b = true;
		if(buttonX.contains(x,y)) this.x = true;
		if(buttonY.contains(x,y)) this.y = true;
		
		if(buttonPause.contains(x,y)) pause = true;
		
		Vector2 point = new Vector2(x,y);
		
		if(controlPad.contains(point))
		{
			Vector2 posOnPad = point.sub(new Vector2(controlPad.x, controlPad.y));
			
			if(posOnPad.len2() >= controlpadDeadzone*controlpadDeadzone)
			{
				controlPad8Dir = Util.getNearestDir(posOnPad.angle());
			}
		}
	}
	
	private void checkKeyPress()
	{
		if(Gdx.input.isKeyPressed(Keys.DOWN)) a = true;
		if(Gdx.input.isKeyPressed(Keys.RIGHT)) b = true;
		if(Gdx.input.isKeyPressed(Keys.UP)) y = true;
		if(Gdx.input.isKeyPressed(Keys.LEFT)) x = true;
		
		//convert WASD to 8dir style
		boolean up = false;
		boolean down = false;
		boolean left = false;
		boolean right = false;
		Vector2 dir = new Vector2();
		
		if(Gdx.input.isKeyPressed(Keys.W)) up = true;
		if(Gdx.input.isKeyPressed(Keys.A)) left = true;
		if(Gdx.input.isKeyPressed(Keys.S)) down = true;
		if(Gdx.input.isKeyPressed(Keys.D)) right = true;
		
		if(Gdx.input.isKeyPressed(Keys.ESCAPE)) pause = true;
		
		if(up && !down)
			dir.y = 1;
		else if(down && !up)
			dir.y = -1;
		if(left && !right)
			dir.x = -1;
		else if(right && !left)
			dir.x = 1;
		
		//direction finding is not additive. this will blank any movement detected by
		//a touch event, so keys have to be checked before touch
		if(dir.len2() == 0f) controlPad8Dir = -1;
		else controlPad8Dir = (int) (dir.angle()/45f);
	}
	
	private void resetControlState()
	{
		a = false;
		b = false;
		x = false;
		y = false;
		
		pause = false;
		
		controlPad8Dir = -1;
	}
	
	public void update()
	{
		resetControlState();
		if(keyControls)
			checkKeyPress();
		if(touchControls)
			handleTouchEvents();
	}
}
