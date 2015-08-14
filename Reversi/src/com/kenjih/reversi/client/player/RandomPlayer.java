package com.kenjih.reversi.client.player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.kenjih.reversi.common.Game;

public class RandomPlayer extends Player {

	private Random rand = new Random();
	
	public RandomPlayer(String name) {
		super(name);
	}

	@Override
	public int[] getNextHand(Game game) {
		List<int[]> pos = new ArrayList<int[]>();
		
		for (int y = 0; y < Game.BOARD_H; y++) {
			for (int x = 0; x < Game.BOARD_W; x++) {
				if (game.isValidHand(y, x, getStone())) {
					pos.add(new int[] {y, x});
				}
			}
		}
				
		int x = rand.nextInt(pos.size());
		return pos.get(x);
	}

}
