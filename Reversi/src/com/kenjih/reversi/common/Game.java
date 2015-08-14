package com.kenjih.reversi.common;

import java.util.Arrays;

public class Game {
	
	public static final int BOARD_H = 8;
	public static final int BOARD_W = 8;
	
	public static final char STONE_BLACK = 'B';    // must be an upper-case alphabet
	public static final char STONE_WHITE = 'W';    // must be an upper-case alphabet
	public static final char STONE_EMPTY = '.';
		
	private char[][] board;

	public static Game createInstance(char[][] board) {
		Game g = new Game();
		
		for (int y = 0; y < BOARD_H; y++) {
			for (int x = 0; x < BOARD_W; x++) {
				g.board[y][x] = board[y][x];
			}
		}
		
		return g;
	}

	public static Game createInstance(String s) {
		Game g = new Game();
		
		int i = 0;
		for (int y = 0; y < BOARD_H; y++) {
			for (int x = 0; x < BOARD_W; x++) {
				g.board[y][x] = s.charAt(i++);
			}
		}
		
		return g;
	}
	
	public static Game copyOf(Game g) {
		return Game.createInstance(g.toString());
	}
	
	public Game() {
		board = new char[BOARD_H][BOARD_W];
		init();
	}
	
	private void init() {
		for (char[] cs : board)
			Arrays.fill(cs, STONE_EMPTY);
		
		board[3][4] = board[4][3] = STONE_BLACK;
		board[3][3] = board[4][4] = STONE_WHITE;		
	}
	
	public char[][] getBoard() {
		char[][] ret = new char[BOARD_H][BOARD_W];
		
		for (int y = 0; y < BOARD_H; y++)
			for (int x = 0; x < BOARD_W; x++)
				ret[y][x] = board[y][x];
		
		return ret;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int y = 0; y < BOARD_H; y++) {
			for (int x = 0; x < BOARD_W; x++) {
				sb.append(board[y][x]);
			}
		}
		return sb.toString();
	}
		
	public char getStoneAt(int y, int x) {
		return board[y][x];
	}
		
	public boolean isOnBoard(int y, int x) {
		if (y < 0 || y >= BOARD_H) return false;
		if (x < 0 || x >= BOARD_W) return false;
		return true;
	}
	
	public boolean isValidHand(int y, int x, char stone) {
		if (!isOnBoard(y, x)) return false;
		if (board[y][x] != STONE_EMPTY) return false;
		
		final int[] dy = {0,1,1,1,0,-1,-1,-1};
		final int[] dx = {-1,-1,0,1,1,1,0,-1};
	
		char opponentStone = stone == STONE_BLACK ? STONE_WHITE : STONE_BLACK;
		for (int i = 0; i < dy.length; i++) {
			int yt = y;
			int xt = x;
			int cnt = 0;
			
			do {
				++cnt;
				yt += dy[i];
				xt += dx[i];
			} while (isOnBoard(yt, xt) && getStoneAt(yt, xt) == opponentStone);
			
			if (!isOnBoard(yt, xt)) continue;
			if (getStoneAt(yt, xt) != stone) continue;
			if (cnt == 1) continue;
			
			return true;
		}
		
		return false;
	}
	
	public boolean hasAnyValidHand(char stone) {
		
		for (int y = 0; y < BOARD_H; y++) {
			for (int x = 0; x < BOARD_W; x++) {
				if (isValidHand(y, x, stone))
					return true;
			}
		}
		
		return false;
	}
	
	public boolean isEnd() {
		return !hasAnyValidHand(STONE_BLACK) && !hasAnyValidHand(STONE_WHITE)	;	
	}
	
	public boolean putStoneAt(int y, int x, char stone) {
		if (!isValidHand(y, x, stone))
			throw new IllegalArgumentException(
					String.format("not a valid hand (%d, %d)", y, x));
		
		final int[] dy = {0,1,1,1,0,-1,-1,-1};
		final int[] dx = {-1,-1,0,1,1,1,0,-1};
	
		char opponentStone = stone == STONE_BLACK ? STONE_WHITE : STONE_BLACK;
		for (int i = 0; i < dy.length; i++) {
			int yt = y;
			int xt = x;
			int cnt = 0;
			
			// check a direction
			do {
				++cnt;
				yt += dy[i];
				xt += dx[i];
			} while (isOnBoard(yt, xt) && getStoneAt(yt, xt) == opponentStone);
			
			if (!isOnBoard(yt, xt)) continue;
			if (getStoneAt(yt, xt) != stone) continue;
			if (cnt == 1) continue;
			
			// flip stones
			do {
				board[yt][xt] = stone;
				yt -= dy[i];
				xt -= dx[i];
			} while (isOnBoard(yt, xt) && getStoneAt(yt, xt) == opponentStone);
		}
		board[y][x] = stone;
		
		return true;
	}
	
	public int countStone(char stone) {
		int ret = 0;
		
		for (int y = 0; y < BOARD_H; y++) {
			for (int x = 0; x < BOARD_W; x++) {
				if (board[y][x] == stone)
					++ret;
			}
		}
		
		return ret;
	}
}
