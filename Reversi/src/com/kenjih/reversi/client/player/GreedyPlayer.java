package com.kenjih.reversi.client.player;

import com.kenjih.reversi.common.Game;

public class GreedyPlayer extends Player {

	public GreedyPlayer(String name) {
		super(name);
	}

	@Override
	public int[] getNextHand(Game game) {
		
		int[] ret = new int[2];
		int bestScore = -1;
		char c = getStone();
		
		for (int y = 0; y < Game.BOARD_H; y++) {
			for (int x = 0; x < Game.BOARD_W; x++) {
				if (game.isValidHand(y, x, c)) {
					Game nxtBoard = Game.copyOf(game);
					nxtBoard.putStoneAt(y, x, c);
					int cnt = nxtBoard.countStone(c);
					if (cnt > bestScore) {
						bestScore = cnt;
						ret[0] = y;
						ret[1] = x;
					}
				}
			}
		}
		
		return ret;
	}

}
