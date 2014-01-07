package com.gensokyouadventure.graphics;

import java.util.ArrayList;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.TreeMap;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.gensokyouadventure.Game;
import com.gensokyouadventure.Util;
import com.gensokyouadventure.physics.PrimaryDirection;

public class SpriteLoader
{
	private static final int numPortraitFrames = 9;
	
	private Map<String, Texture> textures = new TreeMap<String, Texture>();
	private Map<String, EntitySpriteSet8Dir> entitySpriteSets8Dir = new TreeMap<String, EntitySpriteSet8Dir>();
	private Map<String, AnimationSpriteSet> animationSpriteSet = new TreeMap<String, AnimationSpriteSet>();
	private Map<String, Portrait> portraits = new TreeMap<String, Portrait>();
	private ArrayList<Texture> spriteSheetsLoaded = new ArrayList<Texture>();
		
	public static final String[] portraitNames = 
		{
			"alice", "aya", "cirno", "iku", "komachi", "marisa", "meiling",
			"patchouli", "reimu", "reisen", "remilia", "sakuya", "sanae",
			"suika", "suwako", "tenshi", "utsuho", "youmu", "yukari", "yuyuko"
		};
	
	public SpriteLoader()
	{
		loadTexturesInFolder();
		load8DirTouhouSprites();
		loadAnimations();
		loadPortraits();
	}
		
	private Texture loadTexture(String internalName)
	{
		return new Texture( Util.getInternalFile(internalName));
	}
	
	private Texture loadTexture(FileHandle fh)
	{
		return new Texture(fh);
	}
		
	private void load8DirTouhouSprites()
	{
		FileHandle entityDir = Util.getInternalDirectory("entities/");
		
		for(FileHandle entitySheet : entityDir.list())
		{
			Texture entityTexture = new Texture(entitySheet);
			EntitySpriteSet8Dir spriteSet = new EntitySpriteSet8Dir(entityTexture, 32, 3);
			entitySpriteSets8Dir.put(entitySheet.nameWithoutExtension(), spriteSet);
			spriteSheetsLoaded.add(entityTexture);
		}
	}
	
	public Animation loadAnimation(String name, float frameInterval, PrimaryDirection dir)
	{
		if(!animationSpriteSet.containsKey(name))
			throw new NoSuchElementException(name + " not found");
		
		return new Animation(animationSpriteSet.get(name), frameInterval, dir);
	}
	
	private void loadPortraits()
	{
		for(String name : portraitNames)
		{
			portraits.put(name, new Portrait(name, numPortraitFrames));
		}
		
		portraits.put("fairy_maid", new Portrait("fairy_maid", 1));
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
	
	private void loadAnimations()
	{
		loadAnimation(loadTexture("animations/flame64.png"), 64, 8, "flame64", PrimaryDirection.up);
		loadAnimation(loadTexture("animations/flame32.png"), 32, 8, "flame32", PrimaryDirection.up);
		loadAnimation(loadTexture("animations/cirno_bullet_aa.png"), 128, 15, "cirno_bullet_aa", PrimaryDirection.up);
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
		
	public Texture getTexture(String name)
	{
		if(!textures.containsKey(name))
			throw new NoSuchElementException(String.format("Texture %s not found", name));
		return textures.get(name);
	}
	
	public Portrait getPortrait(String name)
	{
		if(!portraits.containsKey(name))
			throw new NoSuchElementException(String.format("Portrait %s not found", name));
		return portraits.get(name);
	}
}
