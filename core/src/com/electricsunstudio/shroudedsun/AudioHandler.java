package com.electricsunstudio.shroudedsun;

import java.util.TreeMap;

import com.badlogic.gdx.Audio;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;

public class AudioHandler
{
	private TreeMap<String, Sound> sounds = new TreeMap<String, Sound>();
	private TreeMap<String, Music> music = new TreeMap<String, Music>();
	
	private String crntMusicTitle = null;
	private Music crntMusicTrack = null;
	
	public AudioHandler()
	{
		loadSoundsInFolder();
		loadMusicInFolder();
	}
	
	private void loadSoundsInFolder()
	{		
		FileHandle handle = Util.getInternalDirectory("sounds/");
		
		//ignore bootanim0.raw or other non-sound files packaged into the assets folder
		for(FileHandle fh : handle.list())
		{
			if(fh.extension().equals("wav") || fh.extension().equals("ogg"))
				sounds.put(fh.nameWithoutExtension(), Gdx.audio.newSound(fh));			
		}
	}
	
	private void loadMusicInFolder()
	{
		FileHandle handle = Util.getInternalDirectory("music/");
		
		for(FileHandle fh : handle.list())
		{
			music.put(fh.nameWithoutExtension(), Gdx.audio.newMusic(fh));			
		}

	}
	
	public void dispose()
	{
		for(Sound s : sounds.values())
		{
			s.dispose();
		}
		
		if(crntMusicTrack != null && crntMusicTrack.isPlaying())
		{
			crntMusicTrack.stop();
		}
		
		for(Music m : music.values())
		{
			m.dispose();
		}
	}
	
	public void playSound(String sound)
	{
		sounds.get(sound).play();
	}
	
	public void playSound(String sound, Vector2 soundPos)
	{
		float volume = calculateVolume(Game.inst.player.getCenterPos(), soundPos);
		float pan = calculateAudioPan(Game.inst.player.getCenterPos(), soundPos);
		
		sounds.get(sound).play(volume, 1f, pan);
	}
	
	/**
	 * get a music track, set it as the current music and start playing.
	 * @param track title of the music
	 * @param forceRestart if the title is the same as the title currently playing, whether to stop and restart the music
	 */
	public void playMusic(String track, boolean forceRestart)
	{
		if(crntMusicTrack != null)
			stopMusic();
		
		//e.g. loading another area with the same music
		if(crntMusicTitle != null && crntMusicTitle.equals(track) && !forceRestart) return;
		
		if(music.containsKey(track))
		{
			crntMusicTrack = music.get(track);
			crntMusicTitle = track;
			crntMusicTrack.play();			
		}
		else
		{
			Game.log("music track " + track + " not loaded.");
		}
	}
	
	public void pauseMusic()
	{
		if(crntMusicTrack != null)
			crntMusicTrack.pause();
	}
	
	public void resumeMusic()
	{
		if(crntMusicTrack != null)
			crntMusicTrack.play();
	}

	public void stopMusic()
	{
		if(crntMusicTrack != null)
		{
			crntMusicTrack.stop();
			crntMusicTrack = null;
			crntMusicTitle = null;
		}
	}
	
	static float calculateAudioPan(Vector2 listenerPos, Vector2 soundPos)
	{
		//dot displacement onto right unit vector, will give positive if displacement is to the right, 
		//negative if to the left.
		
		Vector2 dir = Util.displacement(listenerPos, soundPos).nor();
		
		return dir.dot(Util.ihat);
	}
	
	//make 1 tile away the unit distance, clip anything closer otherwise inverse quadratic dropoff.
	static float calculateVolume(Vector2 listenerPos, Vector2 soundPos)
	{
		float dist2 = Util.displacement(listenerPos, soundPos).len2();
		
		if(dist2 <= 1) return 1;
		else return 1.0f/dist2;
	}

}
