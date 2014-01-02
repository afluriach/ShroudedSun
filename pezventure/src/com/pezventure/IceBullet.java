package com.pezventure;

import com.badlogic.gdx.graphics.Texture;
import com.pezventure.objects.Player;
import com.pezventure.objects.PlayerIceBullet;

public class IceBullet extends ActionAbility
{
	public IceBullet()
	{
		super(Game.inst.spriteLoader.getTexture("ice_bullet32"));
	}
	
	public boolean canPerform()
	{
//		return Game.inst.player.getMP() > 0;
		return true;
	}
	
	public void perform()
	{
//		Game.inst.player.useMP(1);
		
		Game.inst.player.shoot(new PlayerIceBullet(Game.inst.player.getCenterPos(), Game.inst.player.getDir()), Player.shotInitDist);
	}
}
