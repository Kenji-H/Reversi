package com.kenjih.reversi.client.player;

import com.kenjih.reversi.common.Game;

public class DummyPlayer extends Player {

	public DummyPlayer(String name) {
		super(name);
	}

	@Override
	public int[] getNextHand(Game game) {
		for (int y = 0; y < Game.BOARD_H; y++) {
			for (int x = 0; x < Game.BOARD_W; x++) {
				if (game.isValidHand(y, x, getStone())) {
					return new int[] {y, x};
				}
			}
		}
		return new int[] {-1, -1};
	}

}
