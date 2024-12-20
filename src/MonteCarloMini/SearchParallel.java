package MonteCarloMini;
/* M. Kuttel 2023
 * Searcher class that lands somewhere random on the surfaces and 
 * then moves downhill, stopping at the local minimum.
 *@author M Kuttel
 *@date 2023
 */

public class SearchParallel {
	private int id;				// Searcher identifier
	private int pos_row, pos_col;		// Position in the grid
	private int steps; //number of steps to end of search
	private boolean stopped;			// Did the search hit a previous trail?
	
	private TerrainArea terrain;

        /*
         *enum class which defines the directions of movements
         *
         */

	enum Direction {
		STAY_HERE,
	    LEFT,
	    RIGHT,
	    UP,
	    DOWN
	  }
        
        
        /**
        *constructor for the search parallel class which takes in the
        *id of the search, position row, position column and terrain
        *@param id
        *@param pos_row
        *@param pos_col
        *@param terrain
        *
        */ 
	public SearchParallel(int id, int pos_row, int pos_col, TerrainArea terrain) {
		this.id = id;
		this.pos_row = pos_row; //randomly allocated
		this.pos_col = pos_col; //randomly allocated
		this.terrain = terrain;
		this.stopped = false;
	}
	
	
	/*
	*findValleys method that finds the minimum point in the terrain
	*@return height
	*/
	public int find_valleys() {	
		int height=Integer.MAX_VALUE;
		Direction next = Direction.STAY_HERE;
		while(terrain.visited(pos_row, pos_col)==0) { // stop when hit existing path
			height=terrain.get_height(pos_row, pos_col);
			terrain.mark_visited(pos_row, pos_col, id); //mark current position as visited
			steps++;
			next = terrain.next_step(pos_row, pos_col);
			switch(next) {
				case STAY_HERE: return height; //found local valley
				case LEFT: 
					pos_row--;
					break;
				case RIGHT:
					pos_row=pos_row+1;
					break;
				case UP: 
					pos_col=pos_col-1;
					break;
				case DOWN: 
					pos_col=pos_col+1;
					break;
			}
		}
		stopped=true;
		return height;
	}
       
       
       /**
       *The getter method getID returns the id of the search
       *@return id
       *
       */
	public int getID() {
		return id;
	}


       /**
       *The getter method getPos_row return the position row
       *@return the pos_row
       *
       */
	public int getPos_row() {
		return pos_row;
	}


       /**
       *THe getter method getPos_col returns the position column
       *@return position of column
       *
       */
	public int getPos_col() {
		return pos_col;
	}


        /**
        *The getter method getSteps returns the number of steps
        *@return steps
        *
        */
	public int getSteps() {
		return steps;
	}
	
	/**
	*getter method isStopped
	*@return stopped
	*
	*/
	public boolean isStopped() {
		return stopped;
	}

}
