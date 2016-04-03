// Henry Cooney <hacaoo36@gmail.com> <Github: hacoo>
// 2 Apr. 2016
// dougchess.Board

// Represents a minichess board configurations. Boards are mutable,
// you should explicitely copy it when you need a new one.

// All member variables are PUBLIC which is obviously dangerous.
// So don't mess with them unless you know what you're doing.

package dougchess;

import static dougchess.Rules.*;

public class Board {
  
  public char[][] board;

  /** Default constructor. Initializes board to a new game. **/
  public Board() {
    reset();
  }
  
  /** Reset the board to the starting configuration **/
  public void reset() {
    board = new char [][] {{'k', 'q', 'b', 'r', 'n'},
			   {'p', 'p', 'p', 'p', 'p'},
			   {'.', '.', '.', '.', '.'},
			   {'.', '.', '.', '.', '.'},
			   {'P', 'P', 'P', 'P', 'P'},
			   {'R', 'N', 'B', 'Q', 'K'}};    
  }
  
  @Override
  public String toString() {
    String[] strs = new String[RANKS];
    for (int y = 0; y < RANKS; ++y) 
      strs[y] = new String(board[y]);
    return String.join("\n", strs) + "\n";
  }

  public String hi() {
    return "hi!";
  }
  
}

