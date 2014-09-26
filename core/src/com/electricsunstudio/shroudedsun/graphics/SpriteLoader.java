package com.electricsunstudio.shroudedsun.graphics;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.TreeMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.electricsunstudio.shroudedsun.Game;
import com.electricsunstudio.shroudedsun.Util;
import com.electricsunstudio.shroudedsun.physics.PrimaryDirection;

public class SpriteLoader
{
	private static final int numPortraitFrames = 9;
	
	private Map<String, Texture> textures = new TreeMap<String, Texture>();
	private Map<String, EntitySpriteSet8Dir> entitySpriteSets8Dir = new TreeMap<String, EntitySpriteSet8Dir>();
	private Map<String, AnimationSpriteSet> animationSpriteSet = new TreeMap<String, AnimationSpriteSet>();
	private Map<String, Portrait> portraits = new TreeMap<String, Portrait>();
	private ArrayList<Texture> spriteSheetsLoaded = new ArrayList<Texture>();
			
	public SpriteLoader()
	{
//		loadTexturesInFolder();
		loadEntityAnimations();
		loadAnimations();
	}
		
	private Texture loadTexture(String internalName)
	{
		return new Texture( Util.getInternalFile(internalName));
	}
	
	private Texture loadTexture(FileHandle fh)
	{
		return new Texture(fh);
	}
		
	private void loadEntityAnimations()
	{
		FileHandle entityDir = Util.getInternalDirectory("entities/");
		
		for(FileHandle entitySheet : entityDir.list())
		{
			loadEntity(entitySheet);
		}
	}

	private void loadEntity(FileHandle entitySheet) {
		Texture entityTexture = new Texture(entitySheet);
		EntitySpriteSet8Dir spriteSet = new EntitySpriteSet8Dir(entityTexture, 32, 3);
		entitySpriteSets8Dir.put(entitySheet.nameWithoutExtension(), spriteSet);
		spriteSheetsLoaded.add(entityTexture);
	}
	
	public Animation loadAnimation(String name, float frameInterval, PrimaryDirection dir)
	{
		if(!animationSpriteSet.containsKey(name))
			throw new NoSuchElementException(name + " not found");
		
		return new Animation(animationSpriteSet.get(name), frameInterval, dir);
	}
		
	void loadPortrait(String name)
	{
		//first check to see if the first frame, index 0 exists.
		//if so, iterate to see how many frames there are total, then
		//create the portrait object.
		FileHandle fh = Gdx.files.internal(String.format("portraits/%s0.png", name));
		int count = 1;
		
		if(!fh.exists())
		{
			throw new NoSuchElementException(String.format("Portrait %s not found", name));
		}
		
		for(;;++count)
		{
			fh = Gdx.files.internal(String.format("portraits/%s%d.png", name, count));
			
			if(!fh.exists()) break;
		}
		
		portraits.put(name, new Portrait(name, count));
	}
	
	private void loadTexturesInFolder()
	{
		FileHandle handle = Util.getInternalDirectory("sprites/");
		
		for(FileHandle fh : handle.list())
		{
			if(fh.extension().equals("png"))
			{
				textures.put(fh.nameWithoutExtension(), loadTexture(fh));
			}
		}
	}	
	
	/**
	 * 
	 * @param sheet texure contianing the animations in one row
	 * @param spriteSize the size of each sprite
	 * @param spriteCount the number of frames in the animation. use to ignore unused slots in padded
	 * images (i.e. if sprite count is not a power of two)
	 */
	private void loadAnimation(Texture sheet, int spriteSize, int spriteCount, String name, PrimaryDirection dir)
	{
		TextureRegion [][] splice = TextureRegion.split(sheet, spriteSize, spriteSize);
		TextureRegion[] frames = new TextureRegion[spriteCount];
		
		System.arraycopy(splice[0], 0, frames, 0, spriteCount);
		
		AnimationSpriteSet spriteSet = new AnimationSpriteSet(frames, dir);
		animationSpriteSet.put(name, spriteSet);
		spriteSheetsLoaded.add(sheet);
	}
	
	private void loadAnimation(Texture sheet, int spriteWidth, int spriteHeight, int spriteCount, String name, PrimaryDirection dir)
	{
		TextureRegion [][] splice = TextureRegion.split(sheet, spriteWidth, spriteHeight);
		TextureRegion[] frames = new TextureRegion[spriteCount];
		
		System.arraycopy(splice[0], 0, frames, 0, spriteCount);
		
		AnimationSpriteSet spriteSet = new AnimationSpriteSet(frames, dir);
		animationSpriteSet.put(name, spriteSet);
		spriteSheetsLoaded.add(sheet);
	}

	
	private void loadAnimations()
	{
		loadAnimation(loadTexture("animations/flame64.png"), 64, 8, "flame64", PrimaryDirection.up);
		loadAnimation(loadTexture("animations/cirno_bullet_aa.png"), 128, 15, "cirno_bullet_aa", PrimaryDirection.up);
		loadAnimation(loadTexture("animations/flame32.png"), 32, 8, "flame32", PrimaryDirection.up);
		loadAnimation(loadTexture("animations/white_flame32.png"), 32, 8, "white_flame32", PrimaryDirection.up);
		loadAnimation(loadTexture("animations/orange_flame32.png"), 32, 8, "orange_flame32", PrimaryDirection.up);
		loadAnimation(loadTexture("animations/red_flame32.png"), 32, 8, "red_flame32", PrimaryDirection.up);
		loadAnimation(loadTexture("animations/purple_flame32.png"), 32, 8, "purple_flame32", PrimaryDirection.up);
		loadAnimation(loadTexture("animations/blue_flame32.png"), 32, 8, "blue_flame32", PrimaryDirection.up);
		loadAnimation(loadTexture("animations/green_flame32.png"), 32, 8, "green_flame32", PrimaryDirection.up);
		loadAnimation(loadTexture("animations/yellow_flame32.png"), 32, 8, "yellow_flame32", PrimaryDirection.up);
		loadAnimation(loadTexture("animations/flame_bullet.png"), 256, 128, 16, "flame_bullet", PrimaryDirection.right);
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
		
		for(Portrait p : portraits.values())
		{
			for(Texture t : p.frames)
			{
				t.dispose();
			}
		}
	}
	
	public EntityAnimation8Dir getSpriteAnimation(String name, int startingDir)
	{
		if(!entitySpriteSets8Dir.containsKey(name))
			throw new NoSuchElementException(String.format("Sprite %s not found", name));
		
		return new EntityAnimation8Dir(entitySpriteSets8Dir.get(name), startingDir);
	}
	
	void loadTextureName(String name)
	{
		FileHandle fh = Gdx.files.internal("sprites/" + name + ".png");
		textures.put(fh.nameWithoutExtension(), loadTexture(fh));
	}
		
	public Texture getTexture(String name)
	{
		if(!textures.containsKey(name))
			loadTextureName(name);
		
		return textures.get(name);
	}
	
	public Portrait getPortrait(String name)
	{
		if(!portraits.containsKey(name))
			loadPortrait(name);

		return portraits.get(name);
	}
	
	public void unload()
	{
		//
	}
}
