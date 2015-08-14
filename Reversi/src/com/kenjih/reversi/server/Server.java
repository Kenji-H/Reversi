package com.kenjih.reversi.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.kenjih.reversi.common.Game;
import com.kenjih.reversi.socket.SocketWrapper;

public class Server {

	private static final int DEFAULT_PORT = 55555;
	private static final int DEFAULT_GAME_NUM = 10;
	public static final int TIME_OVER_TH = 10;

	private int port;
	private int gameNum;
	private List<SocketWrapper> clients;
	private List<String> pNames;

	public Server(Map<String, Integer> opt) {
		port = opt.containsKey("-p") ? opt.get("p") : DEFAULT_PORT;
		gameNum = opt.containsKey("-n") ? opt.get("n") : DEFAULT_GAME_NUM;
	}

	public static void main(String[] args) throws IOException {

		Map<String, Integer> opt = new HashMap<String, Integer>();
		for (int i = 0; i < args.length; i++) {
			if (args[i].matches("^-[pn]$"))
				opt.put(args[i], Integer.parseInt(args[++i]));
		}

		Server server = new Server(opt);
		server.listen();
		server.run();
	}

	public void listen() throws IOException {
		ServerSocket serverSocket = new ServerSocket(port);

		clients = new ArrayList<SocketWrapper>();
		pNames = new ArrayList<String>();
		while (clients.size() < 2) {
			SocketWrapper sr = new SocketWrapper(serverSocket.accept());
			clients.add(sr);
			pNames.add(sr.readLine());
		}

		serverSocket.close();
	}

	private void sendAll(String s) throws IOException {
		for (SocketWrapper sr : clients) {
			sr.writeLine(s);
		}
	}

	private int play() throws IOException {

		Game game = new Game();
		char[] stones = { Game.STONE_BLACK, Game.STONE_WHITE };
		int points[] = { 2, 2 };

		sendAll(String.format("GSTART %s %s", pNames.get(0), pNames.get(1)));
		sendAll(String.format("BOARD %s", game.toString()));

		for (int turn = 0;; turn ^= 1) {

			String curName = pNames.get(turn);
			char curStone = stones[turn];

			boolean bCheck = game.hasAnyValidHand(Game.STONE_BLACK);
			boolean wCheck = game.hasAnyValidHand(Game.STONE_WHITE);

			// game end check
			if (!bCheck && !wCheck)
				break;

			// pass check
			if ((curStone == Game.STONE_BLACK && !bCheck)
					|| (curStone == Game.STONE_WHITE && !wCheck)) {
				sendAll(String.format("PASS %s", curName));
				continue;
			}

			sendAll(String.format("TURN %s", curName));

			// get player's hand
			try {
				long startTime = System.nanoTime();
				String[] args = clients.get(turn).readLine().split(" ");
				long elapsedTime = System.nanoTime() - startTime;
				if (elapsedTime/1e9 > TIME_OVER_TH && !curName.equals("HumanPlayer"))
					throw new RuntimeException("time over.");
				
				int y = Integer.parseInt(args[0]);
				int x = Integer.parseInt(args[1]);
				game.putStoneAt(y, x, curStone);
				sendAll(String.format("PUT %d %d", y, x));
			} catch (RuntimeException e) {
				sendAll(String.format("VIOLATION %s %s", curName, e.getMessage()));
				points[turn] = -1;
				break;
			}

			sendAll(String.format("BOARD %s", game.toString()));

			points[0] = game.countStone(Game.STONE_BLACK);
			points[1] = game.countStone(Game.STONE_WHITE);
		}

		sendAll(String.format("GEND %s=%d %s=%d", pNames.get(0), points[0],
				pNames.get(1), points[1]));
		
		
		if (points[0] > points[1]) return 0;
		if (points[1] > points[0]) return 1;
		return -1;
	}

	public void run() throws IOException {

		// GSTART [black player name] [white player name]
		// TURN [player name]
		// PASS [player name]
		// VIOLATION [player name]
		// PUT [pos_y] [pos_x]
		// BOARD [current board string]
		// GEND [black point] [white point]
		// CLOSE

		double[] scores = new double[2];
		
		for (int _ = 0; _ < gameNum; _++) {
			
			System.out.printf("Playing game #%d ....\n", _+1);

			// swap play orders
			Collections.reverse(pNames);
			Collections.reverse(clients);
			double tmp = scores[0]; scores[0] = scores[1]; scores[1] = tmp;
			
			int winner = play();
			
			// reflect the result to the total scores 
			if (winner == 0 || winner == 1)
				++scores[winner];
			else {
				scores[0] += 0.5;
				scores[1] += 0.5;
			}
		}
		
		sendAll("CLOSE");
		
		for (int i = 0; i < pNames.size(); i++)
			System.out.printf("%s Score: %f\n", pNames.get(i), scores[i] / gameNum);
	}
}
