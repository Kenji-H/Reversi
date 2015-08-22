package com.kenjih.reversi.client.player;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.kenjih.reversi.common.Game;

public class MctsPlayer extends Player {

	public static final double UCB_C = Math.sqrt(2.0);
	public static final int PLAY_OUT = 1000;
	
	class Node {
		int vis;
		double bWin; // # of black win
		List<Node> children;
		Game g;
		char stone;
		boolean isLeaf;

		Node(Game g, char stone) {
			vis = 1;
			bWin = 0.0;
			children = null;
			this.g = g;
			this.stone = stone;
			isLeaf = g.isEnd();
		}

		Node expand() {

			if (isLeaf || children != null) {
				return this;
			}

			char opponent = (stone == Game.STONE_BLACK) ? Game.STONE_WHITE : Game.STONE_BLACK;
			
			children = new ArrayList<MctsPlayer.Node>();
			
			for (int y = 0; y < Game.BOARD_H; y++) {
				for (int x = 0; x < Game.BOARD_W; x++) {

					if (g.isValidHand(y, x, stone)) {
						Game gt = Game.copyOf(g);
						gt.putStoneAt(y, x, stone);
						children.add(new Node(gt, opponent));
					}
				}
			}

			// pass a turn
			if (children.size() == 0) {
				children.add(new Node(g, opponent));
			}

			return this;
		}
		
		double winRate(char stone) {
			if (stone == Game.STONE_BLACK)
				return bWin / vis;
			else
				return 1.0 - bWin / vis;
		}
		
		double ucbScore(char stone, int t) {
			return	winRate(stone) + UCB_C * Math.sqrt(Math.log(t) / vis);
		}
		
		Node select() {

			Node node = null;
			double bestScore = -1;

			for (Node x : children) {
				double score = x.ucbScore(stone, vis);
				if (score > bestScore) {
					bestScore = score;
					node = x;
				}
			}

			return node;
		}

	}

	public MctsPlayer(String name) {
		super(name);
	}

	@Override
	public int[] getNextHand(Game game) {
		
		Node root = new Node(game, getStone());
		
		for (int t = 0; t < PLAY_OUT; t++) {
			
			Stack<Node> stack = new Stack<MctsPlayer.Node>();
			Node node = root;
			stack.push(node);
			
			while (!node.isLeaf) {
				node = node.expand().select();
				stack.push(node);
			}
			
			int bc = node.g.countStone(Game.STONE_BLACK);
			int wc = node.g.countStone(Game.STONE_WHITE);
			
			double bWin = 0.0;
			if (bc > wc) 
				bWin = 1.0;
			else if (bc == wc)
				bWin = 0.5;
			
			while (!stack.isEmpty()) {
				Node tmp = stack.pop();
				tmp.vis++;
				tmp.bWin += bWin;
			}
		}

		Node bestNode = null;
		double score = -1.0;
		for (Node node : root.children) {
			double tmp = node.winRate(getStone());
			
			if (getStone() == Game.STONE_BLACK)
				System.out.printf("  win rate = %f (%f / %d)\n", tmp, node.bWin, node.vis);
			else
				System.out.printf("  win rate = %f (%f / %d)\n", tmp, node.vis - node.bWin, node.vis);				
			
			if (tmp > score) {
				score = tmp;
				bestNode = node;
			}
		}
		
		System.out.printf("best win rate = %f\n", score);
		
		for (int y = 0; y < Game.BOARD_H; y++) {
			for (int x = 0; x < Game.BOARD_W; x++) {
				if (game.getStoneAt(y, x) == Game.STONE_EMPTY
						&& bestNode.g.getStoneAt(y, x) == getStone()) {
					return new int[] {y, x};
				}
			}
		}
		
		return new int[] {-1, -1};
	}

}
