package MonteCarloMini;
/* Serial  program to use Monte Carlo method to 
 * locate a minimum in a function
 * This is the reference sequential version (Do not modify this code)
 * Michelle Kuttel 2023, University of Cape Town
 * Adapted from "Hill Climbing with Montecarlo"
 * EduHPC'22 Peachy Assignment" 
 * developed by Arturo Gonzalez Escribano  (Universidad de Valladolid 2021/2022)
 */
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;


class MonteCarloMinimizationParallel extends RecursiveTask<Integer>{

      int hi;
      int lo;
      static final int SEQUENTIAL_CUTOFF=1000;
      TerrainArea terrain;
      SearchParallel [] searches;
      int min=Integer.MAX_VALUE;
      int local_min=Integer.MAX_VALUE;
      static int finder =-1;

     
      MonteCarloMinimizationParallel(int lo,int hi,TerrainArea terr,SearchParallel [] arr ){
         this.hi = hi;
    	 this.lo=lo;
         terrain =terr;
    	 searches=arr;
      }


      /*
      *compute method which splits the search into smaller subtasks
      *and then return the minimim
      *@return min
      */
      protected Integer compute(){
         if ((hi-lo)<SEQUENTIAL_CUTOFF){
	      for  (int i=lo;i<hi;i++) {
		    local_min=searches[i].find_valleys();
		    if((!searches[i].isStopped())&&(local_min<min)) { //don't look at  those who stopped because hit exisiting path
			min=local_min;
		        finder=i; //keep track of who found it
		     }
	      return min;
	     //if(DEBUG) System.out.println("Search "+searches[i].getID()+" finished at  "+local_min + " in " +searches[i].getSteps());
              }	  
         }

         else {
              MonteCarloMinimizationParallel left = new MonteCarloMinimizationParallel(lo,(hi+lo)/2,terrain,searches);
              MonteCarloMinimizationParallel right = new MonteCarloMinimizationParallel((hi+lo)/2,hi,terrain,searches);
              left.fork();
              int  rightAns= right.compute();
              int leftAns= left.join();
              if (rightAns>leftAns) {
                   return leftAns;
	      }
              else {
                   return rightAns;
                   }
              }
              
      return min;
      }

      static final boolean DEBUG=false;
      static long startTime = 0;
      static long endTime = 0;

      //timers - note milliseconds
      /*
      * the tick method sets the start time
      */
      private static void tick(){
	    startTime = System.currentTimeMillis();
      }
	
	
      /*
      *tock methods sets the endtime
      */
      private static void tock(){
	    endTime=System.currentTimeMillis(); 
	}
	
	
      /*
      *main method responsible for running the program
      *
      *@param args
      */
       public static void main(String[] args)  {

	  int rows, columns; //grid size
	  double xmin, xmax, ymin, ymax; //x and y terrain limits
    	  TerrainArea terrain;  //object to store the heights and grid points visited by searches
    	  double searches_density;	// Density - number of Monte Carlo  searches per grid position - usually less than 1!

	  int num_searches;		// Number of searches
    	  SearchParallel [] searches;		// Array of searches
    	  Random rand = new Random();  //the random number generator
    	
    	
    	
    	  if (args.length!=7) {  
    		System.out.println("Incorrect number of command line arguments provided.");   	
    		System.exit(0);
    	  }
    	  //Read argument values 
	  rows =Integer.parseInt( args[0] );
    	  columns = Integer.parseInt( args[1] );
    	  xmin = Double.parseDouble(args[2] );
    	  xmax = Double.parseDouble(args[3] );
    	  ymin = Double.parseDouble(args[4] );
    	  ymax = Double.parseDouble(args[5] );
    	  searches_density = Double.parseDouble(args[6] );
      
	  if(DEBUG) {
    		/* Print arguments */
    		System.out.printf("Arguments, Rows: %d, Columns: %d\n", rows, columns);
    		System.out.printf("Arguments, x_range: ( %f, %f ), y_range( %f, %f )\n", xmin, xmax, ymin, ymax );
    		System.out.printf("Arguments, searches_density: %f\n", searches_density );
    		System.out.printf("\n");
    	  }
    	
    	 // Initialize 
    	 terrain = new TerrainArea(rows, columns, xmin,xmax,ymin,ymax);
    	 num_searches = (int)( rows * columns * searches_density );
	 searches= new SearchParallel[num_searches];
    	 
    	 for (int i=0;i<num_searches;i++) 
    		searches[i]=new SearchParallel(i+1, rand.nextInt(rows),rand.nextInt(columns),terrain);
    	
      	 if(DEBUG) {
    		/* Print initial values */
    		System.out.printf("Number searches: %d\n", num_searches);
    		//terrain.print_heights();
    	 }
    	
    	 //start timer
    	 tick();
    	
    	
   	 //Fork join implementation 
		MonteCarloMinimizationParallel monteCarlo = new MonteCarloMinimizationParallel(0,num_searches,terrain,searches);
		ForkJoinPool pool = new ForkJoinPool();
		int min=pool.invoke(monteCarlo);

	//end timer
	tock();
   		
    	if(DEBUG) {
    		/* print final state */
    		terrain.print_heights();
    		terrain.print_visited();
    	}
    	
	System.out.printf("Run parameters\n");
	System.out.printf("\t Rows: %d, Columns: %d\n", rows, columns);
	System.out.printf("\t x: [%f, %f], y: [%f, %f]\n", xmin, xmax, ymin, ymax );
	System.out.printf("\t Search density: %f (%d searches)\n", searches_density,num_searches );

	/*  Total computation time */
	System.out.printf("Time: %d ms\n",endTime - startTime );
	int tmp=terrain.getGrid_points_visited();
	System.out.printf("Grid points visited: %d  (%2.0f%s)\n",tmp,(tmp/(rows*columns*1.0))*100.0, "%");
	tmp=terrain.getGrid_points_evaluated();
	System.out.printf("Grid points evaluated: %d  (%2.0f%s)\n",tmp,(tmp/(rows*columns*1.0))*100.0, "%");
	
	/* Results*/
	System.out.printf("Global minimum: %d at x=%.1f y=%.1f\n\n", min, terrain.getXcoord(searches[finder].getPos_row()), terrain.getYcoord(searches[monteCarlo.finder].getPos_col()) );
				
    	
    }
}