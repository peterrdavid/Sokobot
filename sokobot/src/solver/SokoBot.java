package solver;
import java.util.ArrayList;
import java.util.HashMap;

public class SokoBot {

  public String solveSokobanPuzzle(int width, int height, char[][] mapData, char[][] itemsData) {
    /*
     * YOU NEED TO REWRITE THE IMPLEMENTATION OF THIS METHOD TO MAKE THE BOT SMARTER
     */
    /*
     * Default stupid behavior: Think (sleep) for 3 seconds, and then return a
     * sequence
     * that just moves left and right repeatedly.
     */


    //Initializing: code length for state codes, list of goal positions, max cost
    //Generating: start state from inputs, state code for start state
    int codeLength = (Math.floor(Math.log(height*width)/Math.log(10))+".").length();
    ArrayList<int[]> goalPos = new ArrayList<int[]>();
    for(int i=0; i<height; i++){
      for(int j=0; j<width; j++){
        if(mapData[i][j] == '.'){
          goalPos.add(new int[] {i,j});
        }
      }
    }
    State start = new State(width, height, goalPos, itemsData);
    String code = stateCode(itemsData, codeLength);
    
    //Initializing: frontier, epxlored list, move list
    HashMap<String,State> frontier = new HashMap<String, State>();
    frontier.put(code, start);
    ArrayList<String> explored = new ArrayList<String>();
    char[] moveList = {'u','d','l','r'};

    while(frontier.size()>0){ //Main search loop
      //Finding next state to explore, marking as explored
      String currentCode = getMin(frontier);
      State currentState = frontier.get(currentCode);
      explored.add(currentCode);
      frontier.remove(currentCode);

      if(isEnd(currentState)){ //Found end state case
        return currentState.getPath();

      }else{ //General state case
        for(char move : moveList){ //Iterating through possible actions
          if(currentState.boxMoved || currentState.lastMove!=opp(move)){ //Simple anti-redundancy check
            //Computing relevent check positions
            int[] next1 = getNext(currentState.playerPos, move);
            int[] next2 = getNext2(currentState.playerPos, move);

            if(!isBlocked(mapData, currentState.itemsData, next1, next2)){ //Check if move is blocked
              //Compute new itemsData, if box was moved, new state code
              char[][] nextItems = getNextItems(currentState.itemsData, currentState.playerPos, next1, next2, width, height);
              boolean boxMoved = !(currentState.itemsData[next1[0]][next1[1]] == ' ');
              String nextCode = stateCode(nextItems,codeLength);

              if(frontier.containsKey(nextCode)){ //Already in frontier case
                if(currentState.cost+1 < frontier.get(nextCode).cost){ //Checking if cost is better, updating if needed
                  frontier.get(nextCode).update(currentState, currentState.cost + 1, move, boxMoved);
                }
              }else if(!explored.contains(nextCode)){ //Completely new state case
                //Generating new state and adding to frontier
                State nextState = new State(width, height, currentState, goalPos, nextItems, move, boxMoved);
                frontier.put(nextCode,nextState);
              }
            }
          }
        }
      }
    }

    return "";
  }

  public String stateCode(char[][] itemsData, int codeLength){ //Generates unique code from itemsData
    int h = itemsData.length;
    int w = itemsData[0].length;
    String formCode = "%0" + codeLength + "d";
    String code = "";
    for(int i=0; i<h; i++){
      for(int j=0; j<w; j++){
        if(itemsData[i][j] == '$'){
          int pos = w*i + j;
          String coord = String.format(formCode,pos);
          code = code + coord;
        }else if(itemsData[i][j] == '@'){
          int pos = w*i + j;
          String coord = String.format(formCode,pos);
          code = coord + code;
        }
      }
    }
    
    return code;
  }

  public String getMin(HashMap<String, State> frontier){ //Finds state in frontier with lowest cost-heuristic sum
    String minCode = "";
    int minSum = -1;
    for(String code : frontier.keySet()){
      int test = frontier.get(code).getTotal();
      if(test<minSum){
        minSum = test;
        minCode = code;
      }else if(minSum < 0){
        minSum = test;
        minCode = code;
      }
    }

    return minCode;
  }

  public char opp(char move){ //Computes opposite of a move
    if(move=='u'){
      return 'd';
    }else if(move=='l'){
      return 'r';
    }else if(move=='r'){
      return 'l';
    }else{
      return 'u';
    }
  }

  public boolean isBlocked(char[][] mapData, char[][] itemsData, int[] next1, int[] next2){ //Checks if move is blocked
      if(mapData[next1[0]][next1[1]] == '#'){
        return true;
      }else{
        return itemsData[next1[0]][next1[1]] == '$' && (itemsData[next2[0]][next2[1]] == '$' || mapData[next2[0]][next2[1]] == '#');
      }
  }

  public boolean isEnd(State current){ //Checks if goal is reached
    return (current.heuristic == 0);
  }

  public int[] getNext(int[] currentPos, char move){ //Computes 1st position in front of move direction
    int[] next1 =  new int[2];
    System.arraycopy(currentPos,0,next1,0,2);
    if(move == 'u'){
      next1[0]--;
    }else if(move == 'l'){
      next1[1]--;
    }else if(move == 'r'){
      next1[1]++;
    }else{
      next1[0]++;
    }
    return next1;
  }

  public int[] getNext2(int[] currentPos, char move){ //Computes 2nd position in front of move direction
    int[] next2 =  new int[2];
    System.arraycopy(currentPos,0,next2,0,2);
    if(move == 'u'){
      next2[0]-=2;
    }else if(move == 'l'){
      next2[1]-=2;
    }else if(move == 'r'){
      next2[1]+=2;
    }else{
      next2[0]+=2;
    }
    return next2;
  }

  public char[][] getNextItems(char[][] itemsData, int[] current, int[] next1, int[] next2, int w, int h){ //Generates next itemsData from previous and move
    char[][] nextItems = new char[h][w];
    for(int i=0; i<h; i++){
      System.arraycopy(itemsData[i], 0, nextItems[i], 0, w);
    }

    if(nextItems[next1[0]][next1[1]] == '$'){
      nextItems[next2[0]][next2[1]] = '$';
    }

    nextItems[next1[0]][next1[1]] = '@';
    nextItems[current[0]][current[1]] = ' ';

    return nextItems;
  }
}
