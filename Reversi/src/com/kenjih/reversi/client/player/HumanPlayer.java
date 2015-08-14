package com.kenjih.reversi.client.player;

import com.kenjih.reversi.common.Game;
import com.kenjih.reversi.common.GameVisualizer;

public class HumanPlayer extends Player {

	private GameVisualizer visualizer;
	
	public HumanPlayer(String name) throws InterruptedException {
		super(name);
		visualizer = new GameVisualizer();
		visualizer.setBoard(new Game().getBoard());
	}

	@Override
	public int[] getNextHand(Game game) throws InterruptedException {
		System.out.printf("Your(%s) turn:\n", getStone());
		
		// set available hands
		char[][] board = game.getBoard();
		for (int y = 0; y < board.length; y++) {
			for (int x = 0; x < board[y].length; x++) {
				if (game.isValidHand(y, x, getStone())) {
					board[y][x] = Character.toLowerCase(getStone()); 
				}
			}
		}
		visualizer.setBoard(board);
		
		// get a selected hand
		int[] ret = new int[] {-1, -1};
		while (!game.isValidHand(ret[0], ret[1], getStone())) {
			ret = visualizer.getInputPosition();
		}
		
		return ret;
	}

	@Override
	public void setCurGameState(Game game) throws InterruptedException {
		visualizer.setBoard(game.getBoard());
		if (game.isEnd()) {
			int bc = game.countStone(Game.STONE_BLACK);
			int wc = game.countStone(Game.STONE_WHITE);
			
			String res;
			if (getStone() == Game.STONE_BLACK) {
				res = String.format("Game End.\nBLACK(you):%d vs WHITE(opponent):%d.", bc, wc);
			} else {
				res = String.format("Game End.\nWHITE(you):%d vs BLACK(opponent):%d.", wc, bc);				
			}
			visualizer.dispResult(res);
		}
	}
	
}
