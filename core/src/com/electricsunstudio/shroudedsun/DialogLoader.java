package com.electricsunstudio.shroudedsun;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.TreeMap;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.SerializationException;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;

public class DialogLoader
{
	private Map<String, Map<String, Dialog>> dialogCollections = new TreeMap<String, Map<String,Dialog>>();
	
	public DialogLoader()
	{
		FileHandle dialogDir = Util.getInternalDirectory("dialogs/");
		XmlReader reader = new XmlReader();
		
		//each xml file will be a collection, like a name space. 
		
		for(FileHandle file : dialogDir.list())
		{
			Element xmlRoot;			
			try
			{
				xmlRoot = reader.parse(file);
			}
			catch(IOException ex)
			{
				Game.log(String.format("Error opening dialog file: %s.", file.nameWithoutExtension()));
				continue;
			}
			catch(SerializationException ex)
			{
				Game.log(String.format("Error parsing dialog file: %s.", file.nameWithoutExtension()));
				continue;
			}
			
			String collectionName = xmlRoot.getName();
			if(!dialogCollections.containsKey(collectionName))
			{
				dialogCollections.put(collectionName, new HashMap<String, Dialog>());
			}

			for(int i=0;i<xmlRoot.getChildCount(); ++i)
			{
				dialogCollections.get(collectionName).put(xmlRoot.getChild(i).getName(), Dialog.loadFromXml(xmlRoot.getChild(i)));
			}
		}
	}
	
	public Dialog getDialog(String collection, String name)
	{
		if(!dialogCollections.containsKey(collection))
		{
			throw new NoSuchElementException("collection not found: " + collection);
		}
		return dialogCollections.get(collection).get(name);
	}
}
