package com.electricsunstudio.shroudedsun;

import java.util.LinkedList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.controllers.mappings.Ouya;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.electricsunstudio.shroudedsun.graphics.Graphics;
import com.electricsunstudio.shroudedsun.physics.PrimaryDirection;

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
	
	public static final float gamepadDeadzone = 0.3f;
	
	static final ButtonColorScheme colors = ButtonColorScheme.scheme;
	static final Color buttonTextColor = Graphics.hsva(0f, 0f, 0.5f, 1f);
	
	public static boolean showItemButtons = false;
	public static boolean showInteractButton = false;
	
	//touch
	boolean touchControls;
	boolean keyControls;
	
	//screen
	int width;
	int height;
	
	//control state
	
	//represents control pad or control stick state. length of 1 resents full movement in a given direction
	Vector2 controlPadPos = Vector2.Zero;
	
	boolean a = false;
	boolean b = false;
	boolean x = false;
	boolean y = false;
	
	boolean pause = false;
	boolean strafe = false;
	boolean target = false;
	
	//screen touches that did not touch a control GUI element
	LinkedList<Vector2> screenTouches = new LinkedList<Vector2>();
	
	//GUI elements	
	Circle buttonA;
	Circle buttonB;
	Circle buttonX;
	Circle buttonY;
	
	Circle buttonPause;
	Circle buttonStrafe;
	Circle controlPad;
		
	public Controls(int width, int height, boolean touch, boolean keys)
	{
		this.width = width;
		this.height = height;
		touchControls = touch;
		keyControls = keys;
		createShapes();
	}
	
	//the "direction" that is being input. controlpad if above deadzone. this will include arrow keys
	public PrimaryDirection controlPadDirection()
	{
		if(controlPadPos.len() < gamepadDeadzone) return null;
		
		int dir8 = Util.getNearestDir(controlPadPos.angle());
		
		//the controlpad is pointing in a primary direction. ignore diagonal
		return PrimaryDirection.from8Dir(dir8); 
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
	
	//is the select menu button pressed (a button)
	public boolean selectButton()
	{
		return a;
	}
	
	//is the back buttton pressed (x or b)
	public boolean backButton()
	{
		return b || x;
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
		buttonStrafe = new Circle(controlPad.x + margin + controlPad.radius, margin+buttonRadius, buttonRadius);
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
		
		if(showInteractButton)
			drawButtonOuter(shapeRenderer, colors.alight, buttonA);
		
		if(showItemButtons)
		{
			drawButtonOuter(shapeRenderer, colors.blight, buttonB);
			drawButtonOuter(shapeRenderer, colors.xlight, buttonX);
			drawButtonOuter(shapeRenderer, colors.ylight, buttonY);
		}
		
		if(showInteractButton)
			drawButtonInner(shapeRenderer,a, colors.alight, colors.adark, buttonA);
		if(showItemButtons)
		{
			drawButtonInner(shapeRenderer,b, colors.blight, colors.bdark, buttonB);
			drawButtonInner(shapeRenderer,x, colors.xlight, colors.xdark, buttonX);
			drawButtonInner(shapeRenderer,y, colors.ylight, colors.ydark, buttonY);
		}

		
		if(touchControls)
		{
			drawButtonOuter(shapeRenderer, colors.dpadlight, buttonPause);
			drawButtonInner(shapeRenderer, pause, colors.dpadlight, colors.dpaddark, buttonPause);

			drawButtonOuter(shapeRenderer, colors.dpadlight, controlPad);
			drawButtonInner(shapeRenderer, false, colors.dpadlight, colors.dpaddark, controlPad);
			
			drawButtonOuter(shapeRenderer, colors.dpadlight, buttonStrafe);
			drawButtonInner(shapeRenderer,  strafe, colors.dpadlight, colors.dpaddark, buttonStrafe);
		}
		
		shapeRenderer.end();
		
		batch.begin();
		
		if(Game.inst.player.holdingItem != null)
			batch.setColor(1f, 1f, 1f, 0.3f);
		
		//TODO do not draw other buttons if paused
		//TODO draw icon transparent if not available

		if(showItemButtons)
		{
			if(Game.inst.bEquipped != null)
				Graphics.drawTexture(Game.inst.bEquipped.icon,  new Vector2(buttonB.x*Game.TILES_PER_PIXEL,  buttonB.y*Game.TILES_PER_PIXEL), batch);

			if(Game.inst.xEquipped != null)
				Graphics.drawTexture(Game.inst.xEquipped.icon,  new Vector2(buttonX.x*Game.TILES_PER_PIXEL,  buttonX.y*Game.TILES_PER_PIXEL), batch);

			if(Game.inst.yEquipped != null)
				Graphics.drawTexture(Game.inst.yEquipped.icon,  new Vector2(buttonY.x*Game.TILES_PER_PIXEL,  buttonY.y*Game.TILES_PER_PIXEL), batch);
		}
		
		batch.setColor(1f, 1f, 1f, 1f);
		
		batch.end();
		
		if(showInteractButton)
			drawInteractMessage(batch, font);
		
		if(touchControls)
		{
			drawPauseMessage(batch, font);
			drawStrafeMessage(batch, font);
		}
	}

	private void drawPauseMessage(SpriteBatch batch, BitmapFont font) {
		String pauseMsg = Game.inst.paused ? "Resume" : "Pause";
				
		Graphics.drawTextCentered(buttonTextColor, pauseMsg, batch, font, buttonPause.x, buttonPause.y, buttonPause.radius*2 - 10);
	}

	private void drawInteractMessage(SpriteBatch batch, BitmapFont font) {
		String interactMsg = Game.inst.player.interactMessage;
				
		Graphics.drawTextCentered(buttonTextColor, interactMsg, batch, font, buttonA.x, buttonA.y, buttonA.radius*2 - 10);
	}
	
	private void drawStrafeMessage(SpriteBatch batch, BitmapFont font)
	{
		String strafeMsg = Game.inst.strafeEnabled ? "Walk" : "Strafe";
		
		Graphics.drawTextCentered(buttonTextColor, strafeMsg, batch, font, buttonStrafe.x, buttonStrafe.y, buttonStrafe.radius*2 - 10);
	}
	
	
	private void handleTouchControls()
	{
		for(int i=0; i< maxPressEvents; ++i)
		{
			if(Gdx.input.isTouched(i))
				checkTouchPress(Gdx.input.getX(i), height - Gdx.input.getY(i));
			else break;
		}
	}
	
	//TODO if there are multiple presses on pad, use the one that is closest to the control pad
	//position in the previous frame
	private void checkTouchPress(int x, int y)
	{		
		Vector2 point = new Vector2(x,y);

		if(buttonA.contains(x,y)) a = true;
		else if(buttonB.contains(x,y)) b = true;
		else if(buttonX.contains(x,y)) this.x = true;
		else if(buttonY.contains(x,y)) this.y = true;
		
		else if(buttonPause.contains(x,y)) pause = true;
		
		else if(buttonStrafe.contains(x,y)) strafe = true;		
		
		else if(controlPad.contains(point))
		{
			Vector2 posOnPad = point.sub(new Vector2(controlPad.x, controlPad.y));
			
			if(posOnPad.len2() >= controlpadDeadzone*controlpadDeadzone)
			{
				controlPadPos = posOnPad.scl((float)1.0/controlPad.radius);
			}
		}
		else
		{
			screenTouches.add(point);
		}
	}
	
	private void handleKeyboardControls()
	{
		if(Gdx.input.isKeyPressed(Keys.DOWN)) a = true;
		if(Gdx.input.isKeyPressed(Keys.RIGHT)) b = true;
		if(Gdx.input.isKeyPressed(Keys.UP)) y = true;
		if(Gdx.input.isKeyPressed(Keys.LEFT)) x = true;

		if(Gdx.input.isKeyPressed(Keys.ESCAPE)) pause = true;
		if(Gdx.input.isKeyPressed(Keys.SLASH)) strafe = true;
		if(Gdx.input.isKeyPressed(Keys.PERIOD)) target = true;
		
		//convert WASD to velocity vector
		boolean up = Gdx.input.isKeyPressed(Keys.W);
		boolean down = Gdx.input.isKeyPressed(Keys.S);
		boolean left = Gdx.input.isKeyPressed(Keys.A);
		boolean right = Gdx.input.isKeyPressed(Keys.D);
		Vector2 dir = new Vector2();
		
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
		
		controlPadPos = dir.nor();
	}
	
	private void resetControlState()
	{
		a = false;
		b = false;
		x = false;
		y = false;
		
		pause = false;
		strafe = false;
		target = false;
		
		controlPadPos = Vector2.Zero;
		
		screenTouches.clear();
	}
	
	void handleControllers()
	{
		for(Controller controller : Controllers.getControllers())
		{
//			if(controller.getButton(Ouya.BUTTON_O)) a = true;
//			if(controller.getButton(Ouya.BUTTON_U)) x = true;
//			if(controller.getButton(Ouya.BUTTON_Y)) y = true;
//			if(controller.getButton(Ouya.BUTTON_A)) b = true;
//			if(controller.getButton(Ouya.BUTTON_MENU)) pause = true;

			if(controller.getButton(Xbox360Pad.BUTTON_A)) a = true;
			if(controller.getButton(Xbox360Pad.BUTTON_B)) b = true;
			if(controller.getButton(Xbox360Pad.BUTTON_X)) x = true;
			if(controller.getButton(Xbox360Pad.BUTTON_Y)) y = true;
			
			if(controller.getButton(Xbox360Pad.BUTTON_START)) pause = true;
			
			if(controller.getButton(Xbox360Pad.BUTTON_LB)) strafe = true;
			if(controller.getButton(Xbox360Pad.BUTTON_RB)) target = true;
			
			Vector2 leftDisp = new Vector2(controller.getAxis(Ouya.AXIS_LEFT_Y), -controller.getAxis(Ouya.AXIS_LEFT_X));
			
			if(leftDisp.len2() > gamepadDeadzone*gamepadDeadzone)
			{
				controlPadPos = leftDisp.len2() > 1 ? leftDisp.nor() : leftDisp;
			}
		}
	}
		
	public void update()
	{
		resetControlState();
		
		if(keyControls)
		{
			handleKeyboardControls();
			handleControllers();
		}
		if(touchControls)
			handleTouchControls();
	}
}

class Xbox360Pad
{
   /*
    * It seems there are different versions of gamepads with different ID Strings.
    * Therefore its IMO a better bet to check for:
    * if (controller.getName().toLowerCase().contains("xbox") &&
                  controller.getName().contains("360"))
    * 
    * Controller (Gamepad for Xbox 360)
      Controller (XBOX 360 For Windows)
      Controller (Xbox 360 Wireless Receiver for Windows)
      Controller (Xbox wireless receiver for windows)
      XBOX 360 For Windows (Controller)
      Xbox 360 Wireless Receiver
      Xbox Receiver for Windows (Wireless Controller)
      Xbox wireless receiver for windows (Controller)
    */
   //public static final String ID = "XBOX 360 For Windows (Controller)";
   public static final int BUTTON_X = 2;
   public static final int BUTTON_Y = 3;
   public static final int BUTTON_A = 0;
   public static final int BUTTON_B = 1;
   public static final int BUTTON_BACK = 6;
   public static final int BUTTON_START = 7;
   public static final PovDirection BUTTON_DPAD_UP = PovDirection.north;
   public static final PovDirection BUTTON_DPAD_DOWN = PovDirection.south;
   public static final PovDirection BUTTON_DPAD_RIGHT = PovDirection.east;
   public static final PovDirection BUTTON_DPAD_LEFT = PovDirection.west;
   public static final int BUTTON_LB = 4;
   public static final int BUTTON_L3 = 8;
   public static final int BUTTON_RB = 5;
   public static final int BUTTON_R3 = 9;
   public static final int AXIS_LEFT_X = 1; //-1 is left | +1 is right
   public static final int AXIS_LEFT_Y = 0; //-1 is up | +1 is down
   public static final int AXIS_LEFT_TRIGGER = 4; //value 0 to 1f
   public static final int AXIS_RIGHT_X = 3; //-1 is left | +1 is right
   public static final int AXIS_RIGHT_Y = 2; //-1 is up | +1 is down
   public static final int AXIS_RIGHT_TRIGGER = 4; //value 0 to -1f
}
