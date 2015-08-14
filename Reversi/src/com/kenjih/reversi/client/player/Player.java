package com.kenjih.reversi.client.player;

import java.lang.reflect.Constructor;

import com.kenjih.reversi.common.Game;

public abstract class Player {

	private String name;
	private char stone;

	public static Player createInstance(String name) throws ReflectiveOperationException {
		String className = Player.class.getPackage().getName() + "." + name;
		Class<?> clazz = Class.forName(className);
		Constructor<?> constructor = clazz.getConstructor(String.class);
		return (Player) constructor.newInstance(name);
	}
		
	public Player(String name) {
		this.name = name;
	}

	public abstract int[] getNextHand(Game game) throws InterruptedException;
	public void setCurGameState(Game game) throws InterruptedException {}
	
	public String getName() {
		return name;
	}

	public char getStone() {
		return stone;
	}

	public void setStone(char stone) {
		this.stone = stone;
	}
}
