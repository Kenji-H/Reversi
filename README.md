# Reversi

### Synopsis
Reversi AI programs.  
I am going to implement Alpha-Beta Tree Search, Monte Carlo Tree Search.   
I also interested in machine-learning-based AI.   
![visualizer](https://github.com/Kenji-H/Reversi/blob/master/Reversi/doc/visualizer.png "reversi_visualizer")


### Architecture
Client-server model.   
Server is responsible for the management of games:  
1) decides who plays black and who plays white.  
2) checks hands given are valid.  
3) flips stones based on hands given.  
4) determins which player won the game.  
  
Client is responsible for giving a hand based on the current game board. This hand decision is delegated to a Player instance.  
![architecture](https://github.com/Kenji-H/Reversi/blob/master/Reversi/doc/architecture.png "architecture")

### How to run
As an example, I will show you how to run the programs with *HumanPlayer* and *DfsStoneNumPlayer*.

At the Reversi directory,   
$ java -cp bin com.kenjih.reversi.server.Server  
  
Open a new terminal. At the Reversi directory,  
$ java -cp bin com.kenjih.reversi.client.Client localhost 55555 *HumanPlayer*  
  
Open another new terminal. At the Reversi directory,  
$ java -cp bin com.kenjih.reversi.client.Client localhost 55555 *DfsStoneNumPlayer*

### Available Players
Here is a list of some available players:  
1) RandomPlayer: gives random hands.  
2) GreedyPlayer: gives hands based on a greedy strategy.  
3) DfsStoneNumPlayer:  give hands based on DFS of game tree.  
4) HumanPlayer: give hands selected(clicked on GUI game board) by human. 
