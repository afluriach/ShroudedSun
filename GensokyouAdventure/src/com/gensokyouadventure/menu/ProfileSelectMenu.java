package com.gensokyouadventure.menu;

import com.gensokyouadventure.Game;
import com.gensokyouadventure.SaveState;

//display a list of available profiles, as well an the option to select a new profile.
public class ProfileSelectMenu extends TextListMenu
{
	static final String blankProfile = "----------";
	int [] profilesCreeated;
	
	public ProfileSelectMenu()
	{
		super("Profile Select", new String[10]);
		
		//get list of available profiles. fill in the profile name for each profile
		//that has already been created, show blank (hyphens) for the other slots
		
		menuEntries = new String[Game.numProfiles];
		profilesCreeated = Game.inst.getProfileIds();
		
		for(int profileID : profilesCreeated)
		{
			SaveState state = Game.inst.loadSaveState(profileID);
			menuEntries[profileID] = state.profileName;
		}
		
		for(int i=0; i < Game.numProfiles; ++i)
		{
			if(menuEntries[i] == null)
				menuEntries[i] = blankProfile;
		}
	}

	@Override
	public TextListMenu handleSelection(int menuOption) {
		//if an existing profile has been selected, go to modify profile menu (start, copy, delete)
		//else bring up start new profile menu (input name)
		
		if(menuEntries[menuOption].equals(blankProfile))
		{
			return new NewProfileMenu(menuOption);
		}
		else
		{
			return new ModifyProfileMenu(menuOption);
		}
	}

	@Override
	public boolean onBack() {
		return true;
	}

}
