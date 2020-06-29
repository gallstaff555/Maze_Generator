/*
 * DZS
 */

import java.awt.Color;

import javax.swing.WindowConstants;

/*
 * Driver for random Maze Generator. This will build a 5x5 maze with debug 
 * turned on, and a 15x15 maze with debug turned off that will render with java.swing.
 */
public class Main {

	//change this to change the size of maze
	public static final int MAZE_SIZE = 15;
	
	public static final int VERTEX_SIZE = 20;
	public static final int FRAME_WIDTH = ((MAZE_SIZE * 2) + 2) * VERTEX_SIZE;
	public static final int FRAME_HEIGHT = ((MAZE_SIZE * 2) + 3) * VERTEX_SIZE;
	
	public static void main(String[] args) {
		
		//5 x 5 maze with debug turned on
		Maze maze = new Maze(5, 5, true);
		System.out.println("Final iteration with solution as '$'");
		maze.display();

		//variable size maze with debug turned off, but will render with swing
		java.awt.EventQueue.invokeLater(new Runnable() {
	          public void run() {
	        	   System.out.println("\n==============================");
	        	   System.out.println("\nBIG MAZE: (debug turned off) \n");
	               Maze maze = new Maze(MAZE_SIZE, MAZE_SIZE, false);
	               maze.setTitle("Random Maze");
	               maze.setSize(FRAME_WIDTH, FRAME_HEIGHT);
	               maze.setBackground(Color.BLACK);
	               maze.setVisible(true);
	               maze.setLocation(100, 100);
	               maze.setResizable(true);
	               maze.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	          }
	    }); 
		
		//testMaze();
	}
	
	//I used the debug == true for testing. I printed out the list of "frontier" vertices,
	//which are vertices that have not been visited but are adjacent to a visted vertex.
	//The 'Parent vertex' in the debugger is the Vertex that originally "discovered" a frontier
	//vertex. When the frontier vertex is travelled to, the wall will be removed between the
	//frontier vertex and its parent. 
	public static void testMaze() {
		Maze testMaze = new Maze(5, 5, true);
		//Verified this is the correct solution path
		System.out.println("Solution Path: " + testMaze.solutionPath.toString());
		
		
		//My maze does not work for non-square matrices
		Maze testMaze2 = new Maze(5, 4, true);
		Maze testMaze2_5 = new Maze(2, 3, true);
		Maze testMaze3 = new Maze(1, 1, false);
		testMaze2.display();
	}
}