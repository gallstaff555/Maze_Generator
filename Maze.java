/*
 * David Solomon
 * TCSS 342 Assignment 5: Maze Generator
 */


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

import javax.swing.JFrame;
import javax.swing.JPanel;

/*
 * This class builds a randomly generated maze. The maze is first
 * generated using a modified Prim's algorithm and then the maze is 
 * solved later using recursive backtracking. This wasn't the most efficient
 * but I wanted practice building a maze iteratively and solving it recursively. 
 * 
 * A limitation of this implementation is that only square matrices can me made, 
 * so for an n x m matrix, n must equal m. 
 */
public class Maze extends JFrame {

    private static final long serialVersionUID = 1L;

    private class Vertex {
        int x;
        int y;
        char symbol;
        Vertex parent;
        boolean visited;
        boolean solutionPath;
        
        public Vertex(int theX, int theY, Vertex theParent) {
            this.x = theX;
            this.y = theY;
            this.parent = theParent;
            symbol = 'X';
            visited = false;
            solutionPath = false;
        }
        
        public char getSymbol() {
        	if (solutionPath) {
        		return '$';
        	} else
        		return symbol;
        }
        
        public void setSymbol(char theSymbol) {
            this.symbol = theSymbol;
        }
        
        public String toString() {
            return "(" + this.x + ", " + this.y + ")";
        }
     } //end Vertex class

    class MazeGraphic extends JPanel {
        private static final long serialVersionUID = 1L;
        private int width;
        private int height;
        
        MazeGraphic(int theWidth, int theHeight) {
            width = theWidth * 2 + 1;
            height = theHeight * 2 + 1;
        }

        public void paint(Graphics g) {
            for (int row = 0; row < height; row ++) {
                for (int col = 0; col < width; col ++) {
                	if (maze[row][col].getSymbol() == '$') {
                		g.setColor(Color.YELLOW);
                	} else if (maze[row][col].getSymbol() == 'X') {
                        g.setColor(Color.DARK_GRAY);
                    } else if (maze[row][col].symbol == 'S') {
                        g.setColor(Color.GREEN);
                    } else if (maze[row][col].symbol == 'E') {
                        g.setColor(Color.RED);
                    } else {
                        g.setColor(Color.WHITE);
                    }
                    g.fillRect(col * 20, row * 20, 20, 20);
                } 
            } 
            g.setColor(Color.CYAN);
        }
    } //end MazeGraphic JPanel
                            
    private int width;
    private int depth;
    private boolean debug;
    private Vertex[][] maze;
    private Random randomizer;
    Vertex startLocation;
    Vertex currentLocation;
    ArrayList<Vertex> frontier;
    Stack<Vertex> solutionPath;
    private JPanel mazeGraphic;
    
    public Maze(int theWidth, int theDepth, boolean theDebug) {
        this.width = theWidth * 2 + 1;
        this.depth = theDepth * 2 + 1;
        this.debug = theDebug;
        randomizer = new Random();
        frontier = new ArrayList<Vertex>();
        solutionPath = new Stack<Vertex>();
        maze = new Vertex[width][depth];
        buildEmptyMaze();
        randomlySelectMazeStart(); 
        randomlySelectMazeFinish();
        searchAllDirections(currentLocation);
        buildMaze(theWidth, theDepth);
        this.display();
    }
    
    private void buildMaze(int theWidth, int theDepth) {
    	
    	//this.display();
        for (int i = 0; i < (theWidth * theDepth) - 1; i++) { 
                currentLocation = moveToRandomFrontier();
                searchAllDirections(currentLocation); //search for unexplored vertices and add them to frontier
        }
        
        //Debugging steps
        if (debug) {
        	System.out.println("Frontier size: " + frontier.size());
        	System.out.println("Final frontier values");
        	for (int j = 0; j < frontier.size(); j++) {
        			System.out.println(frontier.get(j).toString());
        	}
        }
        
		startMazeTraversal();
        
        //set up graphics
        mazeGraphic = new MazeGraphic(theWidth, theDepth);
        mazeGraphic.setBackground(Color.BLACK);
        mazeGraphic.setPreferredSize(new Dimension(this.depth * 200, this.width * 200));
        mazeGraphic.setOpaque(true);
        
        this.setLayout(new BorderLayout());
        this.add(mazeGraphic, BorderLayout.CENTER);
    }
    
    private void buildEmptyMaze() {
        for (int row = 0; row < width; row++) {
            for (int col = 0; col < depth; col++) {
                Vertex v = new Vertex(row, col, null);
                maze[row][col] = v;
                if (col % 2 != 0 && row % 2 != 0) {
                    v.setSymbol('u');
                }
            }
        }
    }
    
    private void randomlySelectMazeStart() {
        int startCol = (int) (Math.random() *  ( (this.width - 1) ));
        if (startCol % 2 == 0) {
            startCol++;
        }
        //System.out.println("Start col: " + startCol);
        startLocation = maze[1][startCol];
        currentLocation = maze[1][startCol];
        currentLocation.setSymbol(' '); 
        maze[0][startCol].setSymbol('S'); //the entry point to the maze
    }
    
    private void randomlySelectMazeFinish() {
       int random3 = randomizer.nextInt(3);

       //end on bottom row in random col
       if (random3 == 0) {
           int endIndex = (int) (Math.random() *  ( (this.width - 1) ));
        if (endIndex % 2 == 0) {
            endIndex++;
        }
        //endLocation = maze[depth - 1][endIndex];
        maze[depth - 1][endIndex].setSymbol('E');
       //end on fist col in random row
       } else if (random3 == 1) {
       int endIndex = (int) (Math.random() *  ( (this.depth - 1) ));
        if (endIndex % 2 == 0) {
            endIndex++;
        }
        //endLocation = maze[endIndex][0];
        maze[endIndex][0].setSymbol('E'); 
       //end on last col in random row
       } else {
           int endIndex = (int) (Math.random() *  ( (this.depth - 1) ));
            if (endIndex % 2 == 0) {
                endIndex++;
            }
        //endLocation = maze[endIndex][width - 1];
        maze[endIndex][width - 1].setSymbol('E');
       }
    }
    
    private void startMazeTraversal() {
    	Vertex start = startLocation;
    	start.solutionPath = true;
    	traverseMaze(start);
    }
    
    //traverse the maze and save correct movements (to end of maze) in a stack
    //incorrect moves are popped off the stack
    private void traverseMaze(Vertex v) {
    	int x = v.x;
        int y = v.y;
        //left
        if (isInBounds(x - 1, y) && maze[x - 1][v.y].symbol != 'X' && maze[x - 1][v.y].visited == false) {
        	currentLocation = maze[x - 1][v.y];
			if (mazeTraversed()) {
				System.out.println("Solution path found!");
			return;
			}
			maze[x - 1][v.y].visited = true;
			maze[x - 1][v.y].solutionPath = true;
			solutionPath.push(maze[x - 1][v.y]);
			traverseMaze(currentLocation);
        }  else if (isInBounds(x, y - 1) && maze[x][v.y - 1].symbol != 'X' && maze[x][v.y - 1].visited == false) {
    		currentLocation = maze[x][v.y - 1];
    		if (mazeTraversed()) {
    			System.out.println("Solution found!");
    			return;
    		}
    		maze[x][v.y - 1].visited = true;
    		maze[x][v.y - 1].solutionPath = true;
    		solutionPath.push(maze[x][v.y -1]);
    		traverseMaze(currentLocation);
    	//right
    	}  else if (isInBounds(x, y + 1) && maze[x][v.y + 1].symbol != 'X' && maze[x][v.y + 1].visited == false) {
    		currentLocation = maze[x][v.y + 1];
    		if (mazeTraversed()) {
    			System.out.println("done!");
    			return;
    		}
    		maze[x][v.y + 1].visited = true;
    		maze[x][v.y + 1].solutionPath = true;
    		solutionPath.push(maze[x][v.y + 1]);
    		traverseMaze(currentLocation);
    	//down
    	
        //up
    	} else if (isInBounds(x + 1, y) && maze[x + 1][v.y].symbol != 'X' && maze[x + 1][v.y].visited == false) {
    		currentLocation = maze[x + 1][v.y];
    		if (mazeTraversed()) {
    			System.out.println("done!");
    			return;
    		}
    		maze[x + 1][v.y].visited = true;
    		maze[x + 1][v.y].solutionPath = true;
    		solutionPath.push(maze[x + 1][v.y]);
    		traverseMaze(currentLocation);
    	} else { //base case
    		if (solutionPath.size() > 1) {
    			Vertex pop = solutionPath.pop();
    			pop.solutionPath = false;
    			traverseMaze(solutionPath.peek());
    		} else {
    			System.out.println("No more options");
    			return;
    		}
    	}
    }
    
    private boolean mazeTraversed() {
    	boolean isMazeComplete = false;
    	int x = currentLocation.x;
        int y = currentLocation.y;
        //look left
        if (isInBounds(x, y - 1) && maze[x][currentLocation.y - 1].symbol == 'E') {
        	isMazeComplete = true;
        } 
        //look right
        if (isInBounds(x, y + 1) && maze[x][currentLocation.y + 1].symbol == 'E') {
        	isMazeComplete = true;
        }
        //look down
        if (isInBounds(x - 1, y) && maze[x - 1][y].symbol == 'E') {
        	isMazeComplete = true;
        } 
        //look up
        if (isInBounds(x + 1, y) && maze[x + 1][y].symbol == 'E') {
        	isMazeComplete = true;
        }
        if (isMazeComplete) {
        	currentLocation.setSymbol('$');
        	System.out.println("End of maze found!");
        }
    	return isMazeComplete;
    }
    
    private void searchAllDirections(Vertex current) {
        int x = current.x;
        int y = current.y;
        //look left
        if (isInBounds(x, y - 2) && maze[x][currentLocation.y - 2].symbol == 'u') {
            //add this to frontier
            maze[x][y - 2].parent = current;
            if (!frontierAlreadyExists(maze[x][y - 2])) {
                frontier.add(maze[x][y - 2]);
            }
        } 
        //look right
        if (isInBounds(x, y + 2) && maze[x][currentLocation.y + 2].symbol == 'u') {
            //add this to frontier
            maze[x][y + 2].parent = current;
            if (!frontierAlreadyExists(maze[x][y + 2])) {            
                frontier.add(maze[x][y + 2]);
            }
        }
        //look down
        if (isInBounds(x - 2, y) && maze[x - 2][y].symbol == 'u') {
            //add this to frontier
            maze[x - 2][y].parent = current;
            if (!frontierAlreadyExists(maze[x - 2][y])) {
                frontier.add(maze[x - 2][y]);
            }
        } 
        //look up
        if (isInBounds(x + 2, y) && maze[x + 2][y].symbol == 'u') {
            //add this to frontier
            maze[x + 2][y].parent = current;
            if (!frontierAlreadyExists(maze[x + 2][y])) {           
                frontier.add(maze[x + 2][y]);
            }
        }
        if (debug) {
	        System.out.println("Frontier vertices: ");
	        for (int i = 0; i < frontier.size(); i++) {
	            System.out.print(" " + frontier.get(i).toString());
	        }
	        System.out.println();
        }
    }
    
    private boolean isInBounds(int x, int y) {
        boolean inBounds = true;
        if ((x < 0) || (y < 0) || (x >= depth) || (y >= width)) {
            inBounds = false;
        }
        return inBounds;
    }

    //remove the given vertex from the list of available frontier vertices
    //a frontier vertex is one which has not been explored but neighbors and 
    //explored vertex
    private void removeFromFrontierList(Vertex vertex) {
        int x = vertex.x;
        int y = vertex.y;
        for (int i = 0; i < frontier.size(); i++) {
            Vertex v = frontier.get(i);
            if (v.x == x && v.y == y) {
                frontier.remove(i);
            }
        }
    }
    
    private boolean frontierAlreadyExists(Vertex vertex) {
        boolean result = false;
        int x = vertex.x;
        int y = vertex.y;
        for (int i = 0; i < frontier.size(); i++) {
            Vertex v = frontier.get(i);
            if (v.x == x && v.y == y) {
                result = true;
            }
        }
        return result;
    }
    
    //Move to random available frontier and remove that vertex from frontier list
    //Also update the symbol of the newly acquired vertex
    //Also break down wall between new and old vertex
    private Vertex moveToRandomFrontier() {
        Vertex nextVertex = null;

        int randomLocation = randomizer.nextInt(frontier.size());
        nextVertex = frontier.get(randomLocation);
        nextVertex.setSymbol(' ');
        if (debug) {
        	System.out.println("New vertex: " + nextVertex.toString());
        }
        removeFromFrontierList(nextVertex);
        removeWallBetweenVertices(nextVertex);
        return nextVertex;
    }
    
    //Remove the 'X' wall between the given new vertex and its parent
    private void removeWallBetweenVertices(Vertex newVertex) {
        Vertex parent = newVertex.parent;
        if (debug) {
        	System.out.println("Parent vertex: " + parent.toString());
        }
        //remove wall to the SOUTH of current location
        if (newVertex.x > parent.x) {
                        maze[parent.x + 1][parent.y].setSymbol(' ');
        //remove wall to the NORTH of current location
        } else if (newVertex.x < parent.x) {
                        maze[parent.x - 1][parent.y].setSymbol(' ');
        //remove wall to the RIGHT of current location
        } else if (newVertex.y > parent.y) {
                        maze[parent.x][parent.y + 1].setSymbol(' ');
        //remove wall to the LEFT of currentLocation
        } else if (newVertex.y < parent.y) {
                        maze[parent.x][parent.y - 1].setSymbol(' ');
        }
        
        if (debug) {
        	//System.out.println(this.display() + "\n");
        	this.display();
        }
    }
    
    void display() {
    	String original = this.toString();
    	StringBuilder newString = new StringBuilder();
    	for (int i = 0; i < original.length(); i++) {
    		if (original.charAt(i) == 'u') {
    			newString.append(' ');
    		} else {
    			newString.append(original.charAt(i));
    		}
    	}
    	System.out.println(newString.toString());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int row = 0; row < width; row++) {
            for (int col = 0; col < depth; col++) {
            	sb.append(maze[row][col].getSymbol());
                if (col + 1 == width) {
                    sb.append("\n");
                    }
                }
            }              
        return sb.toString();
    }          
}

/* GRAVEYARD
*/
