package com.gensokyouadventure.graphics;

import java.util.ArrayList;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.TreeMap;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.gensokyouadventure.Util;
import com.gensokyouadventure.physics.PrimaryDirection;

public class SpriteLoader
{
	private static final int numPortraitFrames = 9;
	
	private Map<String, Texture> textures = new TreeMap<String, Texture>();
	private Map<String, EntitySpriteSet4Dir> entitySpriteSets4Dir = new TreeMap<String, EntitySpriteSet4Dir>();
	private Map<String, EntitySpriteSet8Dir> entitySpriteSets8Dir = new TreeMap<String, EntitySpriteSet8Dir>();
	private Map<String, AnimationSpriteSet> animationSpriteSet = new TreeMap<String, AnimationSpriteSet>();
	private Map<String, Portrait> portraits = new TreeMap<String, Portrait>();
	private ArrayList<Texture> spriteSheetsLoaded = new ArrayList<Texture>();
	
	//the characters spatial layout in the sprite sheet
	public static final String[][] spriteSheetNames = 
		{
			{"reimu", "marisa", "cirno"},
			{"meiling", "patchouli", "sakuya"},
			{"remilia", "remilia_bat", "flandre"},
			{"flandre_bat", "chen", "alice"},
			{"shanghai", "youmu", "yuyuko"},
			{"ran", "yukari", "keine"},
			{"tewi", "reisen", "eiren"},
			{"kaguya", "mokou", "aya"},
			{"melancholy", "yuuka", "komachi"},
			{"yamaxanadu", "aki", "hina"},
			{"nitori", "sanae", "kanako"},
			{"suwako", "suika", "mini_suika"}
		};
	
	public static final String[] portraitNames = 
		{
			"alice", "aya", "cirno", "iku", "komachi", "marisa", "meiling",
			"patchouli", "reimu", "reisen", "remilia", "sakuya", "sanae",
			"suika", "suwako", "tenshi", "utsuho", "youmu", "yukari", "yuyuko"
		};
	
	public SpriteLoader()
	{
		loadTexturesInFolder();
		load4DirEntitySprites();
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
	
	private void load4DirEntitySprites()
	{		
		linkSpritesheet();
	}
	
	private void load8DirTouhouSprites()
	{
		//starts at 3,10
		
		Texture spriteSheet = new Texture(Util.getInternalFile("spritesheets/touhou-5dir-sheet.png"));
		spriteSheetsLoaded.add(spriteSheet);
		
		for(int i=0;i<spriteSheetNames.length; ++i)
		{
			for(int j=0;j<spriteSheetNames[i].length; ++j)
			{
				EntitySpriteSet8Dir spriteSet = new EntitySpriteSet8Dir(spriteSheet, j, i, 36, 3);
				
				entitySpriteSets8Dir.put(spriteSheetNames[i][j], spriteSet);
			}
		}
	}
	
	public Animation loadAnimation(String name, float frameInterval, PrimaryDirection dir)
	{
		if(!animationSpriteSet.containsKey(name))
			throw new NoSuchElementException(name + " not found");
		
		return new Animation(animationSpriteSet.get(name), frameInterval, dir);
	}

	private void linkSpritesheet() {
		int linkSpriteSize = 32;
		Texture linkSpriteSheet = new Texture(Util.getInternalFile("spritesheets/link_sprites.png"));
        TextureRegion [][] linkSprites = TextureRegion.split(linkSpriteSheet, linkSpriteSize, linkSpriteSize);
        spriteSheetsLoaded.add(linkSpriteSheet);
		
        entitySpriteSets4Dir.put("link_green", new EntitySpriteSet4Dir(linkSprites, 0,0,linkSpriteSize, 3));
        entitySpriteSets4Dir.put("link_green_hat", new EntitySpriteSet4Dir(linkSprites, 3,0,linkSpriteSize, 3));
        entitySpriteSets4Dir.put("link_red", new EntitySpriteSet4Dir(linkSprites, 6,0,linkSpriteSize, 3));
        entitySpriteSets4Dir.put("link_red_hat", new EntitySpriteSet4Dir(linkSprites, 9,0,linkSpriteSize, 3));
        
        entitySpriteSets4Dir.put("link_blue", new EntitySpriteSet4Dir(linkSprites, 0,4,linkSpriteSize, 3));
        entitySpriteSets4Dir.put("link_blue_hat", new EntitySpriteSet4Dir(linkSprites, 3,4,linkSpriteSize, 3));
        entitySpriteSets4Dir.put("link_dark", new EntitySpriteSet4Dir(linkSprites, 6,4,linkSpriteSize, 3));
        entitySpriteSets4Dir.put("link_dark_hat", new EntitySpriteSet4Dir(linkSprites, 9,4,linkSpriteSize, 3));
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
	
	public EntityAnimation4Dir getSpriteAnimation(String name, PrimaryDirection startingDir)
	{
		if(!entitySpriteSets4Dir.containsKey(name))
		{
			throw new NoSuchElementException(String.format("Sprite %s not found", name));
		}
		return new EntityAnimation4Dir(entitySpriteSets4Dir.get(name), startingDir);
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
