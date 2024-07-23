package solver;
import java.util.ArrayList;

public class State {
    public char[][] itemsData;

    public int[] playerPos = new int[2];

    public int cost=0;
    public int heuristic;
    public State parent;
    public char lastMove ='.';
    public boolean boxMoved = false;

    //Start state constructor
    public State(int width, int height, ArrayList<int[]> goalPos, char[][] itemsData){
        this.itemsData = new char[height][width];

        for(int i=0; i<height; i++){
            System.arraycopy(itemsData[i], 0, this.itemsData[i], 0, width);

            for(int j=0; j<width; j++){
                if(itemsData[i][j] == '@'){
                    playerPos[0] = i;
                    playerPos[1] = j;
                }
            }
        }
        this.heuristic = this.getHeuristic(goalPos, width, height);
    }

    //Non-start state constructor
    public State(int width, int height, State parent, ArrayList<int[]> goalPos, char[][] itemsData, char lastMove, boolean boxMoved){
        this.parent = parent;

        this.itemsData = itemsData;

        for(int i=0; i<height; i++){
            for(int j=0; j<width; j++){
                if(itemsData[i][j] == '@'){
                    playerPos[0] = i;
                    playerPos[1] = j;
                }
            }
        }

        this.cost = parent.cost + 1;
        this.lastMove = lastMove;
        this.boxMoved = boxMoved;
        if(!boxMoved){
            this.heuristic = parent.heuristic;
        }else{
            this.heuristic = this.getHeuristic(goalPos, width, height);
        }
    }

    //Updates state when better path is found
    public void update(State parent, int cost, char lastMove, boolean boxMoved){
        this.parent = parent;
        this.cost = cost;
        this.lastMove = lastMove;
        this.boxMoved = boxMoved;
    }

    //computes heuristic from itemsData
    public int getHeuristic(ArrayList<int[]> goalPos, int width, int height){
        int totalDist = 0;

        for(int i=0; i<height; i++){
            for(int j=0; j<width; j++){
                if(this.itemsData[i][j] == '$'){
                    int min = height + width;
                    for(int[] goal : goalPos){
                        int dist = taxiDist(goal, new int[] {i,j});
                        if(dist<min){
                            min=dist;
                        }
                    }
                    totalDist+=min;
                }
            }
        }

        return totalDist;
    }

    //Computes manhattan distance
    public static int taxiDist(int[] a, int[] b){
        return Math.abs(a[0]-b[0]) + Math.abs(a[1]-b[1]);
    }

    //Gets sum of heuristic and cost
    public int getTotal(){
        return this.heuristic+this.cost;
    }

    //Generates path of moves to state
    public String getPath(){
        if(this.cost==0){
            return "";
        }else{
            return this.parent.getPath() +this.lastMove;
        }
    }
}
