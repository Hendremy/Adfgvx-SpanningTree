package spanning;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.SpanningTreeAlgorithm.SpanningTree;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import graphics.Image;

public class MazeAnalysis {
	
	private Graph<Integer, DefaultEdge> mazeGraph;
	private Map<Cell, Integer> rooms;
	private BufferedImage image;
	private SpanningTree<DefaultEdge> spanTree;
	private Set<Integer> spanTreeVertices;
	private int cellHeight;
	private int rowCount;
	private int colCount;
	private int roomNumber = 1;
	/**
	 * Constructor
	 * 
	 * @param image The bitmap image to analyze
	 */
	public MazeAnalysis(BufferedImage image) {
		this.image = image;
		initMazeGrid();
		this.mazeGraph = new SimpleGraph<>(DefaultEdge.class);
		this.rooms = new HashMap<>();
		this.spanTreeVertices = new HashSet<Integer>();
		buildMazeGraph();
		findSpanningTree();
	}
	
	/**
	 * Initialise la taille d'une case de la grille du labyrinthe et le nombre de colonnes et rangées.
	 */
	private void initMazeGrid() {
		this.cellHeight = getCellHeight(image);
		this.rowCount = image.getHeight() / cellHeight;
		this.colCount = image.getWidth() / cellHeight;
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
		findRooms();
		findHorizontalPassages();
		findVerticalPassages();
	}
	
	/**
	 * Trouve les pièces du labyrinthe.
	 */
	private void findRooms() {
		for(int col = 1; col < colCount - 1; col += 2) {
			for(int row = 1; row < rowCount - 1; row +=2) {
				if(getRGBAtCoordinates(col, row) == Color.WHITE.getRGB()) {
					rooms.put(new Cell(col, row), roomNumber);
					this.mazeGraph.addVertex(roomNumber);
					roomNumber++;
				}
			}
		}
	}
	
	/**
	 * Trouve les passages horizontaux entre deux pièces du labyrinthe.
	 */
	private void findHorizontalPassages() {
		findPassages(2,1, true);
	}
	
	/**
	 * Trouve les passages verticaux entre deux pièces du labyrinthe.
	 */
	private void findVerticalPassages() {
		findPassages(1,2, false);
	}
	
	/**
	 * Trouve les passages horizontaux ou verticaux entre deux pièces d'un labyrinthe
	 * à partir d'une colonne et d'une rangée définie.
	 * @param colStart la colonne de départ
	 * @param rowStart la rangée du départ
	 * @param horizontal indique si le passage est horizontal ou vertical
	 */
	private void findPassages(int colStart, int rowStart, boolean horizontal) {
		for(int row = rowStart; row < rowCount - 1; row+= 2) {//Attention si 2 out of bounds => exception ?
			for(int col = colStart; col < colCount - 1; col+=2) {
				if(getRGBAtCoordinates(col,row) == Color.WHITE.getRGB()) {
					registerPassage(col, row, horizontal);
				}
			}
		}
	}
	
	/**
	 * Enregistre le passage entre deux pièces dans le graphe.
	 * @param col la colonne du passage
	 * @param row la rangée du passage
	 * @param horizontal indique si le passage est horizontal ou vertical
	 */
	private void registerPassage(int col, int row, boolean horizontal) {
		Cell cellSource = horizontal ? new Cell(col-1, row) : new Cell(col, row-1);
		Cell cellTarget = horizontal ? new Cell(col+1, row) : new Cell(col, row+1);
		this.mazeGraph.addEdge(rooms.get(cellSource), rooms.get(cellTarget));
	}
	
	/**
	 * Retourne la couleur de la case du labyrinthe à la colonne et à la rangée désignée.
	 * @param cell la case du labyrinthe
	 * @return la couleur de la case
	 */
	private int getRGBAtCoordinates(int col, int row) {
		int[] pos = calcPixelCoordinates(col, row);
		return image.getRGB(pos[0], pos[1]);
	}
	
	/**
	 * Calcule les coordonnées du pixel correspondant à la rangée et à la colonne.
	 * @param col la colonne
	 * @param row la rangée
	 * @return les coordonnées du pixel correspondant à la rangée et à la colonne
	 */
	private int[] calcPixelCoordinates(int col, int row) {
		int pixelX = col * this.cellHeight;
		int pixelY = row * this.cellHeight;
		return new int[] {pixelX, pixelY};
	}
	
	/**
	 * Trouve l'arbre couvrant de poids minimal du labyrinthe à partir de son entrée.
	 */
	private void findSpanningTree() {
		var primAlg = new PrimMinimumSpanningTree<Integer, DefaultEdge>(mazeGraph);
		this.spanTree = primAlg.getSpanningTree(1);
		for(var edge : spanTree.getEdges()) {
			this.spanTreeVertices.add(mazeGraph.getEdgeSource(edge));
			this.spanTreeVertices.add(mazeGraph.getEdgeTarget(edge));
		}
	}
	
	/**
	 * Determines if the maze is perfect.
	 * 
	 * @return true if the maze is perfect, false otherwise
	 */
	public boolean isPerfect() {
		return isConnected() && !hasCycles();
	}
	

	/**
	 * Determines if all the rooms in the maze are interconnected.
	 *  
	 * @return true if connected, false otherwide
	 */
	public boolean isConnected() {
		return spanTreeVertices.size() == mazeGraph.vertexSet().size();
	}
	
	/**
	 * Determines if all the rooms in the maze are interconnected,
	 * but with the possibility of going in circles.
	 * 
	 * @return true if connected + cycles, false otherwise
	 */
	public boolean isConnectedWithCycles() {
		return isConnected() && hasCycles();
	}
	
	/**
	 * Détermine si le labyrinthe a des cycles.
	 * @return vrai si le labyrinthe a des cycles, sinon faux
	 */
	private boolean hasCycles() {
		return mazeGraph.edgeSet().size() - mazeGraph.vertexSet().size() + 1 > 0;
	}
	
	/**
	 * Determines if the exit is reachable from the entry.
	 * 
	 * @return true if exit is reachable, false otherwise
	 */
	public boolean isExitReachable() {
		Cell exit = new Cell(colCount-2, rowCount-2);
		return spanTreeVertices.contains(rooms.get(exit));
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
		
		@Override
		public boolean equals(Object obj) {
			if(obj == null) throw new NullPointerException();
			if(!(obj instanceof Cell)) return false;
			Cell cell = (Cell) obj;
			return this.x == cell.x && this.y == cell.y;
		}
		
		@Override
		public int hashCode() {
			return Integer.hashCode(x) * Integer.hashCode(y);
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
