package spanning;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import graphics.Image;

public class MazeAnalysisTest {
	private static MazeAnalysis maze1 = new MazeAnalysis(Image.loadImage("img/mazes/maze_perfect.png"));
	
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
}
