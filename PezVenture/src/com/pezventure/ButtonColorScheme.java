package com.pezventure;

import com.badlogic.gdx.graphics.Color;
import com.pezventure.graphics.Graphics;

public class ButtonColorScheme
{	
	public static ButtonColorScheme scheme =
			new ButtonColorScheme(Graphics.hsva(251f, .8f, .3f, 1f), Graphics.hsva(251f, 1f, .9f, 1f),
					              Graphics.hsva(49f, .8f, .3f, 1f), Graphics.hsva(49f, 1f, .9f, 1f),
					              Graphics.hsva(49f, .8f, .3f, 1f), Graphics.hsva(49f, 1f, .9f, 1f),
					              Graphics.hsva(49f, .8f, .3f, 1f), Graphics.hsva(49f, 1f, .9f, 1f),
					              Graphics.hsva(251f, 0.01f, .3f, 1f), Graphics.hsva(251f, 0.01f, .7f, 1f));
	
	
	Color adark, alight;
	Color bdark, blight;
	Color xdark, xlight;
	Color ydark, ylight;
	
	Color dpaddark, dpadlight;

	public ButtonColorScheme(Color adark, Color alight, Color bdark,
			Color blight, Color xdark, Color xlight, Color ydark, Color ylight,
			Color dpaddark, Color dpadlight) {
		this.adark = adark;
		this.alight = alight;
		this.bdark = bdark;
		this.blight = blight;
		this.xdark = xdark;
		this.xlight = xlight;
		this.ydark = ydark;
		this.ylight = ylight;
		this.dpaddark = dpaddark;
		this.dpadlight = dpadlight;
	}
	
	
		
}