package com.kenjih.reversi.client.player;

import com.kenjih.reversi.common.Game;
import com.kenjih.reversi.server.Server;

public class DfsStoneNumPlayer extends Player {

	private long vis = 0;
	private long startTime = 0;

	public DfsStoneNumPlayer(String name) {
		super(name);
	}

	private int[] dfs(Game g, char myStone, int d) {

		if (++vis >= 100) {
			vis = 0;
			if ((System.nanoTime() - startTime) / 1e9 > Server.TIME_OVER_TH * 0.9) {
				throw new RuntimeException("time over");
			}
		}

		if (d <= 0 || g.isEnd()) {
			int score = g.countStone(Game.STONE_BLACK)
					- g.countStone(Game.STONE_WHITE);
			return new int[] { score, -1, -1 };
		}

		char otherStone = (myStone == Game.STONE_BLACK) ? Game.STONE_WHITE
				: Game.STONE_BLACK;

		int ret[] = { 0, 0, 0 };
		
		// pass
		if (!g.hasAnyValidHand(myStone)) {
			return dfs(g, otherStone, d - 1);
		}

		if (myStone == Game.STONE_BLACK) {
						
			ret[0] = -999;
			for (int y = 0; y < Game.BOARD_H; y++) {
				for (int x = 0; x < Game.BOARD_W; x++) {
					if (!g.isValidHand(y, x, myStone))
						continue;

					Game gt = Game.copyOf(g);
					gt.putStoneAt(y, x, myStone);
					int[] nxt = dfs(gt, otherStone, d - 1);

					if (nxt[0] > ret[0]) {
						ret[0] = nxt[0];
						ret[1] = y;
						ret[2] = x;
					}
				}
			}

		} else {

			ret[0] = 999;
			for (int y = 0; y < Game.BOARD_H; y++) {
				for (int x = 0; x < Game.BOARD_W; x++) {
					if (!g.isValidHand(y, x, myStone))
						continue;

					Game gt = Game.copyOf(g);
					gt.putStoneAt(y, x, myStone);
					int[] nxt = dfs(gt, otherStone, d - 1);

					if (nxt[0] < ret[0]) {
						ret[0] = nxt[0];
						ret[1] = y;
						ret[2] = x;
					}

				}
			}
		}
		
		return ret;
	}
	
	@Override
	public int[] getNextHand(Game game) {
		System.out.println(game.toString());
		startTime = System.nanoTime();
		
		int bc = game.countStone(Game.STONE_BLACK);
		int wc = game.countStone(Game.STONE_WHITE);
		int rm = Game.BOARD_H * Game.BOARD_W - bc - wc;
		
		int[] ret = null;
		for (int d = 1; d <= rm; d += 2) {  // gradually increase the depth
			try {
				ret = dfs(game, getStone(), d);
				System.out.println("DFS with depth " + d + " done.");
			} catch (RuntimeException e) {    // time limit
				break;
			}
		}
		
		System.out.printf("score=%d\n", ret[0]);
		return new int[] {ret[1], ret[2]};
	}
}
