package com.pezventure.graphics;

import java.util.ArrayList;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.TreeMap;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.pezventure.Util;
import com.pezventure.physics.PrimaryDirection;

public class SpriteLoader
{
	private Map<String, Texture> textures = new TreeMap<String, Texture>();
	private Map<String, EntitySpriteSet> entitySprites = new TreeMap<String, EntitySpriteSet>();
	private ArrayList<Texture> spriteSheetsLoaded = new ArrayList<Texture>();
	
	public SpriteLoader()
	{
		loadTextures();
		loadSprites();
	}
		
	private Texture loadTexture(String internalName)
	{
		return new Texture( Util.getInternalFile(internalName));
	}
	
	private void loadSprites()
	{
//		Texture spriteSheet = new Texture(Util.getInternalFile("sprites/pc_dtl.png"));
//		
//		TextureRegion [][] sprites = TextureRegion.split(spriteSheet, Game.ENTITY_SPRITE_SIZE, Game.ENTITY_SPRITE_SIZE);
//		
//		entitySprites.put("alice", new EntitySpriteSet(sprites, 0,0));
//		entitySprites.put("sakuya", new EntitySpriteSet(sprites, 3,0));
//		entitySprites.put("ran", new EntitySpriteSet(sprites, 6,0));
//		entitySprites.put("chen", new EntitySpriteSet(sprites, 9,0));
//        
//		entitySprites.put("marisa", new EntitySpriteSet(sprites, 0, 4));
//		entitySprites.put("reimu", new EntitySpriteSet(sprites, 3, 4));
//		entitySprites.put("meiling", new EntitySpriteSet(sprites, 6, 4));
//		entitySprites.put("melancholy", new EntitySpriteSet(sprites, 9, 4));
		
		int spriteSize = 32;
		Texture spriteSheet = new Texture(Util.getInternalFile("sprites/link_sprites.png"));
        TextureRegion [][] sprites = TextureRegion.split(spriteSheet, spriteSize, spriteSize);
        spriteSheetsLoaded.add(spriteSheet);
		
        entitySprites.put("link_green", new EntitySpriteSet(sprites, 0,0,spriteSize, 3));
        entitySprites.put("link_green_hat", new EntitySpriteSet(sprites, 3,0,spriteSize, 3));
        entitySprites.put("link_red", new EntitySpriteSet(sprites, 6,0,spriteSize, 3));
        entitySprites.put("link_red_hat", new EntitySpriteSet(sprites, 9,0,spriteSize, 3));
        
        entitySprites.put("link_blue", new EntitySpriteSet(sprites, 0,4,spriteSize, 3));
        entitySprites.put("link_blue_hat", new EntitySpriteSet(sprites, 3,4,spriteSize, 3));
        entitySprites.put("link_dark", new EntitySpriteSet(sprites, 6,4,spriteSize, 3));
        entitySprites.put("link_dark_hat", new EntitySpriteSet(sprites, 9,4,spriteSize, 3));

	}
	
	
	private void loadTextures()
	{
        textures.put("rail_h", loadTexture("sprites/rail_h.png"));
        textures.put("rail_v", loadTexture("sprites/rail_v.png"));
        textures.put("rail_curve", loadTexture("sprites/rail_curve.png"));        
        
        textures.put("block", loadTexture("sprites/block.png"));
        textures.put("door", loadTexture("sprites/door.png"));
        
        textures.put("switch_inactive", loadTexture("sprites/switch_inactive.png"));
        textures.put("switch_active", loadTexture("sprites/switch_active.png"));
        
	}
	
	public void unloadTextures()
	{
		for(Texture t : spriteSheetsLoaded)
		{
			t.dispose();
		}
		for(Texture t : textures.values())
		{
			t.dispose();
		}
	}
	
	public EntityAnimation getSpriteAnimation(String name, PrimaryDirection startingDir)
	{
		if(!entitySprites.containsKey(name))
		{
			throw new NoSuchElementException("unknow sprite animation: " + name);
		}
		return new EntityAnimation(entitySprites.get(name), startingDir);
	}
	
	public Texture getTexture(String name)
	{
		return textures.get(name);
	}
}
