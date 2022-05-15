package spanning;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import graphics.Image;

public class MazeAnalysis {
	
	private Graph<Integer, DefaultEdge> mazeGraph;
	private Map<Cell, Integer> rooms;
	private BufferedImage image;
	private int cellHeight;
	private int rowCount;
	private int colCount;
	private int roomNumber = 0;
	/**
	 * Constructor
	 * 
	 * @param image The bitmap image to analyze
	 */
	public MazeAnalysis(BufferedImage image) {
		this.image = image;
		this.cellHeight = getCellHeight(image);
		this.mazeGraph = new SimpleGraph<>(DefaultEdge.class);
		this.rooms = new HashMap<>();
		this.rowCount = image.getWidth() / cellHeight;
		this.colCount = image.getHeight() / cellHeight;
		buildMazeGraph();
	}
	
	/**
	 * Retourne la hauteur d'une pièce de labyrinthe à partir de son image.
	 * @param image l'image du labyrinthe
	 * @return la hauteur d'une pièce de labyrinthe
	 */
	private int getCellHeight(BufferedImage image) {
		for(int i = 0; i < image.getTileHeight(); ++i ) {
			if(image.getRGB(0, i) == Color.WHITE.getRGB()) return i;
		}
		return -1;
	}
	
	/**
	 * Construit le graphe du labyrinthe à partir d'une image de labyrinthe.
	 * @param image l'image de labyrinthe
	 * @param cellHeight la taille d'une case du labyrinthe
	 * @return le graphe du labyrinthe
	 */
	private void buildMazeGraph(){
		mazeGraph = new SimpleGraph<>(DefaultEdge.class);
		Cell mazeStart = new Cell(cellHeight, cellHeight);
		Cell mazeEnd = new Cell(image.getTileWidth() - 2 * cellHeight + 1, image.getTileHeight() - 2 * cellHeight + 1);
		}
	
	/**
	 * Trouve les passages horizontaux entre deux pièces du labyrinthe.
	 */
	private void findHorizontalPassages() {
		for(int i = 1; i < rowCount; i += 2) {
			for(int j = 2; j < colCount; j+= 2) {
				
			}
		}
	}
	
	/**
	 * Trouve les passages verticaux entre deux pièces du labyrinthe.
	 */
	private void findVerticalPassages() {
		for(int i = 2; i < rowCount; i+= 2) {//Attention si 2 out of bounds => exception ?
			for(int j = 1; j < colCount; j+=2) {
				
			}
		}
	}
	
	private int getRGBAtCoordinates() {
		
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
	 * Définit une case du labyrinthe contenant ses coordonnées dans le tableau.
	 * @author hendr
	 *
	 */
	private class Cell{
		private int x;
		private int y;
		
		/**
		 * Construit une case du labyrinthe avec ses coordonnées dans le tableau.
		 * @param x coordonnée horizontale de la case
		 * @param y coordonnée verticale de la case
		 */
		public Cell(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
		/**
		 * Retourne les coordonnées horizontale et verticale de la case.
		 * @return les coordonnées horizontale et verticale de la case
		 */
		public int[] getPos() {
			return new int[] {x,y};
		}
	}
	
	/**
	 * Définit un passage entre deux cases du labyrinthe.
	 * @author hendr
	 *
	 */
	private class Passage{
		private Cell a;
		private Cell b;
		
		/**
		 * Construit un passage à partir de deux cases du labyrinthe.
		 * @param a la première case
		 * @param b la deuxième case
		 */
		public Passage(Cell a, Cell b) {
			this.a = a;
			this.b = b;
		}
		
		/**
		 * Retourne les deux cases reliés par le passage.
		 * @return les deux cases reliés par le passage
		 */
		public Cell[] getCells() {
			return new Cell[] {a,b};
		}
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
