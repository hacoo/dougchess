// Henry Cooney <hacaoo36@gmail.com> <Github: hacoo>
// 2 Apr. 2016
// dougchess.Rules

// Represents certain invariant chess rules, like the size 
// of the board, etc.

package dougchess;

public class Rules {
  public static final int RANKS = 6; // board rows
  public static final int FILES = 5; // board columns
  public static boolean isValid(int x, int y){
    if ((x < 0) || (x >= FILES) || (y < 0) || (y >= FILES))
      return false;
    else
      return true;
  }
    
}
