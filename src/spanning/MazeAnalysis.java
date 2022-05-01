package spanning;

import java.awt.image.BufferedImage;
import graphics.Image;

public class MazeAnalysis {
	
	/**
	 * Constructor
	 * 
	 * @param image The bitmap image to analyze
	 */
	public MazeAnalysis(BufferedImage image) {
		// TODO
	}
	
	/**
	 * Determines if the maze is perfect.
	 * 
	 * @return true if the maze is perfect, false otherwise
	 */
	public boolean isPerfect() {
		// TODO
		return false;
	}

	/**
	 * Determines if all the rooms in the maze are interconnected.
	 *  
	 * @return true if connected, false otherwide
	 */
	public boolean isConnected() {
		// TODO
		return false;
	}
	
	/**
	 * Determines if all the rooms in the maze are interconnected,
	 * but with the possibility of going in circles.
	 * 
	 * @return true if connected + cycles, false otherwise
	 */
	public boolean isConnectedWithCycles() {
		// TODO
		return false;
	}

	/**
	 * Determines if the exit is reachable from the entry.
	 * 
	 * @return true if exit is reachable, false otherwise
	 */
	public boolean isExitReachable() {
		// TODO
		return false;
	}
	
	/**
	 * MAIN - Examples
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("\n>>> MAZE ANALYSIS <<<\n");
		
		BufferedImage image;
		
		image = Image.loadImage("img/mazes/maze_perfect.png");
		System.out.println("maze_perfect : "+image.getWidth()+"x"+image.getHeight());
		AnalyzeMaze(image);
		System.out.println();

		image = Image.loadImage("img/mazes/maze_with_cycles.png");
		System.out.println("maze_with_cycles : "+image.getWidth()+"x"+image.getHeight());
		AnalyzeMaze(image);
		System.out.println();

		image = Image.loadImage("img/mazes/maze_separate_zones_exit_reachable.png");
		System.out.println("maze_with_separate_zones : "+image.getWidth()+"x"+image.getHeight());
		AnalyzeMaze(image);
		System.out.println();

		image = Image.loadImage("img/mazes/maze_separate_zones_no_exit.png");
		System.out.println("maze_with_separate_zones_no_exit : "+image.getWidth()+"x"+image.getHeight());
		AnalyzeMaze(image);
		System.out.println();
	}
	
	/**
	 * Checks the characteristics of a maze.
	 * 
	 * @param image A bitmap image of a maze.
	 */
	private static void AnalyzeMaze(BufferedImage image) {
		MazeAnalysis analysis = new MazeAnalysis(image);
		if (analysis.isPerfect()) {
			System.out.println("The maze is perfect !");
		}
		if (analysis.isConnectedWithCycles()) {
			System.out.println("The maze is connected but contains cycles.");
		}
		if (!analysis.isConnected()) {
			System.out.println("The maze contains disconnected areas.");
		}
		if (analysis.isExitReachable()) {
			System.out.println("-> The exit is reachable from the entry.");
		} else {
			System.out.println("-> The exit is not reachable.");
		}
	}
}
