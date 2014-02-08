package com.gensokyouadventure.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.TextInputListener;
import com.gensokyouadventure.Game;
import com.gensokyouadventure.SaveState;

public class NewProfileMenu extends TextListMenu {

	static final String [] menu = {"input profile name"};
	public String profileName;
	public int profileID;
	boolean textBoxOpen = false;
	
	
	public NewProfileMenu(int profileID) {
		super("New Profile", menu);

		//really a placeholder menu to input text for a profile
		this.profileID = profileID;
	}

	@Override
	public TextListMenu handleSelection(int menuOption) {
		
		if(!textBoxOpen)		
		{
			textBoxOpen = true;
			Gdx.input.getTextInput(new NameListener(this), "profile name", "");	
		}
		
		return null;
	}

	@Override
	public boolean onBack() {
		return true;
	}
	
	public void startGame()
	{
		Game.inst.saveState = new SaveState();
		Game.inst.crntProfileID = profileID;
		Game.inst.saveState.profileName = profileName;
		
		//save state will be blank, other than profile name.
		//this will ensure the profile gets saved as soon as it is created.
		//Game.inst.saveGame();
		
		//save state requires player to be standing at a valid save point
		
		Game.inst.loadGameStart();
	}

}

class NameListener implements TextInputListener
{
	NewProfileMenu menu;
	public NameListener(NewProfileMenu menu)
	{
		this.menu = menu;
	}
	
	@Override
	public void input(String text) {
		menu.profileName = text;
		Game.inst.menuHandler.closeMenu = true;
		
		menu.startGame();
	}

	@Override
	public void canceled() {
		menu.textBoxOpen = false;
	}
	
}

