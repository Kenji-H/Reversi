package com.kenjih.reversi.common;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.kenjih.reversi.common.Game;

public class GameVisualizer extends JFrame {

	private static final long serialVersionUID = -2074858046896338109L;
	public  static final char AVAILABLE_HANDS = 'G';

	private char[][] board;
	private BlockingDeque<int[]> que = new LinkedBlockingDeque<int[]>();
	private boolean inputMode = false;

	public GameVisualizer() {
		super("reversi");
		setSize(600, 600);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		this.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				if (!inputMode)
					return;

				int posX = e.getX();
				int posY = e.getY();

				posX = 8 * (posX - 20) / 560;
				posY = 8 * (posY - 20) / 560;
				que.add(new int[] { posY, posX });
			}
		});
	}

	public void setBoard(char[][] board) throws InterruptedException {
		this.board = board;
		this.repaint();
		Thread.sleep(500);
	}

	public int[] getInputPosition() throws InterruptedException {
		inputMode = true;
		int[] ret = que.takeLast();
		System.out.println(ret[0] + " " + ret[1]);
		inputMode = false;

		return ret;
	}
	
	public void dispResult(String msg) {
		JOptionPane.showMessageDialog(this, msg);
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2 = (Graphics2D) this.getGraphics();

		// green background
		Rectangle rect = new Rectangle();
		rect.setRect(20, 20, 560, 560);
		g2.setColor(new Color(34, 139, 34));
		g2.fill(rect);

		// black lines
		g2.setStroke(new BasicStroke(2.0f));
		g2.setColor(Color.BLACK);

		for (int x = 0; x <= 560; x += 70) {
			Line2D line = new Line2D.Double(x + 20, 20 + 1, x + 20,
					560 + 20 - 1);
			g2.draw(line);
		}

		for (int y = 0; y <= 560; y += 70) {
			Line2D line = new Line2D.Double(20 + 1, y + 20, 560 + 20 - 1,
					y + 20);
			g2.draw(line);
		}

		// stones
		if (board != null) {
			for (int y = 0; y < Game.BOARD_H; y++) {
				for (int x = 0; x < Game.BOARD_W; x++) {
					
					boolean isFixedHand = Character.isUpperCase(board[y][x]);
					char c = Character.toUpperCase(board[y][x]);
					
					if (c == Game.STONE_BLACK) {
						g2.setColor(Color.BLACK);
					} else if (c == Game.STONE_WHITE) {
						g2.setColor(Color.WHITE);
					} else {
						continue;
					}

					int posY = 70 * y + 20 + 3;
					int posX = 70 * x + 20 + 3;

					Ellipse2D ellipse = new Ellipse2D.Double(posX, posY, 64, 64);
					if(isFixedHand) {
						g2.fill(ellipse);   // fixed hand
					} else {
						g2.draw(ellipse);   // available hand
					}
				}
			}
		}
	}
}
