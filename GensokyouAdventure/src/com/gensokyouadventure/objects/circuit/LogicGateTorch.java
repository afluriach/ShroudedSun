package com.gensokyouadventure.objects.circuit;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.Texture;
import com.gensokyouadventure.Game;
import com.gensokyouadventure.map.TilespaceRectMapObject;
import com.gensokyouadventure.objects.GameObject;
import com.gensokyouadventure.objects.environment.Switch;
import com.gensokyouadventure.objects.environment.Torch;
import com.gensokyouadventure.objects.projectile.PlayerBullet;
import com.gensokyouadventure.physics.PrimaryDirection;

public class LogicGateTorch extends Torch implements Switch
{
	private static Map<String, LogicGate> ops;
	//since the torch textures' names are prefixed by color
	private static Map<String, String> colors;

	private static void ops()
	{
		 ops = new HashMap<String, LogicGate>();
		 
		 ops.put("and", new And());
		 ops.put("or", new Or());
		 ops.put("not", new Not());
		 ops.put("nand", new Nand());
		 ops.put("nor", new Nor());
		 ops.put("xor", new Xor());		 
		 
		 ops.put(null, null);
	}
	
	private static void colors()
	{
		colors = new HashMap<String, String>();
		
		colors.put("nand", "purple");
		colors.put("and", "blue");
		colors.put("xor", "green");
		colors.put("or", "yellow");
		colors.put("nor", "orange");
		colors.put("not", "red");
		
		colors.put(null, null);
	}
	
	static LogicGate getGateOp(String opName)
	{
		if(ops == null) ops();
				
		return ops.get(opName);
	}
	
	static String getColor(String opName)
	{
		if(colors == null) colors();
		
		return colors.get(opName);
	}
	
	LogicGate torchOp;
	
	public LogicGateTorch(TilespaceRectMapObject mo)
	{
		super(mo);
		
		torchOp = getGateOp(mo.type);
		
		//the base torch 
		String color = getColor(mo.type);
		texture = Game.inst.spriteLoader.getTexture(color == null ? "torch" : color + "_torch");
		flame = Game.inst.spriteLoader.loadAnimation(color == null ? "white_flame32" : color + "_flame32", 0.125f, PrimaryDirection.up);
	}
	enum Arity
	{
		one, two, var
	}
	
	@Override
	public void handleContact(GameObject other)
	{
		//for the input torches
		if(torchOp == null && other instanceof PlayerBullet) lit = !lit;
	}

	@Override
	public boolean isPermanent() {
		return false;
	}

	@Override
	public void activate() {
		//ignore. circuit torches are not permanent switches
	}
	
}
