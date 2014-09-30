package com.electricsunstudio.shroudedsun.server;

import com.electricsunstudio.shroudedsun.Game;

public class Server {
	public static void main (String[] arg)
	{
		//start an instance of the Game engine
		Game game = new Game(false);
		
		System.out.println("server started");
	}
}
