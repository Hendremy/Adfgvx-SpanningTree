package spanning;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import graphics.Image;

public class MazeAnalysisTest {
	private static MazeAnalysis maze1 = new MazeAnalysis(Image.loadImage("img/mazes/maze_perfect.png"));
	private static MazeAnalysis maze2 = new MazeAnalysis(Image.loadImage("img/mazes/maze_separate_zones_exit_reachable.png"));
	private static MazeAnalysis maze3 = new MazeAnalysis(Image.loadImage("img/mazes/maze_separate_zones_no_exit.png"));
	private static MazeAnalysis maze4 = new MazeAnalysis(Image.loadImage("img/mazes/maze_with_cycles.png"));
	
	@Test
	void testIsPerfectMaze1() {
		assertTrue(maze1.isPerfect());
	}

	@Test
	void testIsConnectedMaze1() {
		assertTrue(maze1.isConnected());
	}

	@Test
	void testIsConnectedWithCyclesMaze1() {
		assertFalse(maze1.isConnectedWithCycles());
	}

	@Test
	void testIsExitReachableMaze1() {
		assertTrue(maze1.isExitReachable());
	}
	
	@Test
	void testAllMaze1PerfectExitReachable() {
		assertTrue(maze1.isPerfect());
		assertTrue(maze1.isConnected());
		assertFalse(maze1.isConnectedWithCycles());
		assertTrue(maze1.isExitReachable());
	}

	@Test
	void testAllMaze2SeparatedExitReachable() {
		assertFalse(maze2.isPerfect());
		assertFalse(maze2.isConnected());
		assertFalse(maze2.isConnectedWithCycles());
		assertTrue(maze2.isExitReachable());
	}
	
	@Test
	void testAllMaze3SeparatedNoExit() {
		assertFalse(maze3.isPerfect());
		assertFalse(maze3.isConnected());
		assertFalse(maze3.isConnectedWithCycles());
		assertFalse(maze3.isExitReachable());
	}
	
	@Test
	void testAllMaze4Cycles() {
		assertFalse(maze4.isPerfect());
		assertTrue(maze4.isConnected());
		assertTrue(maze4.isConnectedWithCycles());
		assertTrue(maze4.isExitReachable());
	}
	
	@Test
	void analyzeNullImage() {
		assertThrows(NullPointerException.class, 
				() -> new MazeAnalysis(null));
	}
	
	@Test
	void analyzeMazeInvalidEntrance() {
		assertThrows(IllegalArgumentException.class,
				() -> new MazeAnalysis(Image.loadImage("img/mazes/maze_invalid_entrance.png")));
	}
}
