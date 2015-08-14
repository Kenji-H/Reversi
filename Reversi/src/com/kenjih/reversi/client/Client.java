package com.kenjih.reversi.client;

import java.io.IOException;
import java.net.Socket;

import com.kenjih.reversi.client.player.Player;
import com.kenjih.reversi.common.Game;
import com.kenjih.reversi.socket.SocketWrapper;

public class Client {
	
	SocketWrapper socket;
	Player player;
	
	public Client(String host, int port, String name) throws IOException, ReflectiveOperationException {
		socket = new SocketWrapper(new Socket(host, port));
		player = Player.createInstance(name);
	}
		
	public void run() throws IOException, InterruptedException {

		// GSTART [black player name] [white player name]
		// TURN [player name]
		// PASS [player name]
		// VIOLATION [player name]
		// PUT [pos_y] [pos_x]
		// BOARD [current board string]
		// GEND [black point] [white point]
		// CLOSE

		socket.writeLine(player.getName());
		
		Game game = null;
		while (true) {
			String line = socket.readLine();
			String[] args = line.split(" ");
			
			if (args[0].equals("GSTART")) {
				if (args[1].equals(player.getName())) {
					player.setStone(Game.STONE_BLACK);
				} else {
					player.setStone(Game.STONE_WHITE);
				}
				System.out.println("Start a new game.");
			}
			else if (args[0].equals("TURN")) {
				if (args[1].equals(player.getName())) {
					int[] pos = player.getNextHand(Game.copyOf(game));
					socket.writeLine(String.format("%s %s", pos[0], pos[1]));
				}
			}
			else if (args[0].equals("PASS")) {
				
			}
			else if (args[0].equals("VIOLATION")) {
				System.out.println(line);
			}
			else if (args[0].equals("PUT")) {
				
			}
			else if (args[0].equals("BOARD")) {
				game = Game.createInstance(args[1]);
				player.setCurGameState(Game.copyOf(game));
			}
			else if (args[0].equals("GEND")) {
				System.out.printf("Result: %s %s", args[1], args[2]);
				System.out.println();
			}
			else if (args[0].equals("CLOSE")) {
				break;
			}
			else {
				throw new IllegalArgumentException();
			}
		}
	}
	
	public static void main(String[] args) throws IOException, ReflectiveOperationException, InterruptedException {
		if (args.length < 3) {
			System.err.println("3 options are necessary: [server_host] [port] [name]");
			System.exit(1);
		}
		
		String host = args[0];
		int port = Integer.parseInt(args[1]);
		String name = args[2];
		
		new Client(host, port, name).run();
	}
}
