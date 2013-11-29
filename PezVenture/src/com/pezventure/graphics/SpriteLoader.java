package com.pezventure.graphics;

import java.util.ArrayList;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.TreeMap;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.pezventure.Game;
import com.pezventure.Util;
import com.pezventure.physics.PrimaryDirection;

public class SpriteLoader
{
	private Map<String, Texture> textures = new TreeMap<String, Texture>();
	private Map<String, EntitySpriteSet> entitySpriteSets = new TreeMap<String, EntitySpriteSet>();
	private Map<String, ProjectileSpriteSet> projectileSpriteSet = new TreeMap<String, ProjectileSpriteSet>();
	private ArrayList<Texture> spriteSheetsLoaded = new ArrayList<Texture>();
	
	public SpriteLoader()
	{
//		loadTextures();
		loadTexturesInFolder();
		loadSprites();
	}
		
	private Texture loadTexture(String internalName)
	{
		return new Texture( Util.getInternalFile(internalName));
	}
	
	private Texture loadTexture(FileHandle fh)
	{
		return new Texture(fh);
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
		
		int linkSpriteSize = 32;
		Texture linkSpriteSheet = new Texture(Util.getInternalFile("spritesheets/link_sprites.png"));
        TextureRegion [][] linkSprites = TextureRegion.split(linkSpriteSheet, linkSpriteSize, linkSpriteSize);
        spriteSheetsLoaded.add(linkSpriteSheet);
		
        entitySpriteSets.put("link_green", new EntitySpriteSet(linkSprites, 0,0,linkSpriteSize, 3));
        entitySpriteSets.put("link_green_hat", new EntitySpriteSet(linkSprites, 3,0,linkSpriteSize, 3));
        entitySpriteSets.put("link_red", new EntitySpriteSet(linkSprites, 6,0,linkSpriteSize, 3));
        entitySpriteSets.put("link_red_hat", new EntitySpriteSet(linkSprites, 9,0,linkSpriteSize, 3));
        
        entitySpriteSets.put("link_blue", new EntitySpriteSet(linkSprites, 0,4,linkSpriteSize, 3));
        entitySpriteSets.put("link_blue_hat", new EntitySpriteSet(linkSprites, 3,4,linkSpriteSize, 3));
        entitySpriteSets.put("link_dark", new EntitySpriteSet(linkSprites, 6,4,linkSpriteSize, 3));
        entitySpriteSets.put("link_dark_hat", new EntitySpriteSet(linkSprites, 9,4,linkSpriteSize, 3));

        
        
	}
	
	private void loadTexturesInFolder()
	{
		FileHandle handle = Util.getInternalDirectory("sprites/");
		
		for(FileHandle fh : handle.list())
		{
			if(fh.extension().equals("png"))
			{
				textures.put(fh.nameWithoutExtension(), loadTexture(fh));
//				Gdx.app.log(Game.TAG, String.format("file %s loaded, saved as %s", fh.name(), fh.nameWithoutExtension()));
			}
			else
			{
//				Gdx.app.log(Game.TAG, String.format("file %s skipped in texture folder", fh.name()));
			}
		}
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
        
        textures.put("bullet_aa", loadTexture("sprites/bullet_aa.png"));
        textures.put("bullet_ec", loadTexture("sprites/bullet_ec.png"));
        
        textures.put("jar", loadTexture("sprites/jar.png"));
        textures.put("red_jar", loadTexture("sprites/red_jar.png"));
        textures.put("yellow_jar", loadTexture("sprites/yellow_jar.png"));
        textures.put("green_jar", loadTexture("sprites/green_jar.png"));
        textures.put("blue_jar", loadTexture("sprites/blue_jar.png"));
        
        textures.put("sign", loadTexture("sprites/sign.png"));
        
        textures.put("shield32", loadTexture("sprites/shield32.png"));
        textures.put("shield64", loadTexture("sprites/shield64.png"));
        textures.put("shield128", loadTexture("sprites/shield128.png"));
        
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
		if(!entitySpriteSets.containsKey(name))
		{
			throw new NoSuchElementException("unknow sprite animation: " + name);
		}
		return new EntityAnimation(entitySpriteSets.get(name), startingDir);
	}
	
	public Texture getTexture(String name)
	{
		if(!textures.containsKey(name))
			throw new NoSuchElementException(String.format("Texture %s not found", name));
		return textures.get(name);
	}
}
