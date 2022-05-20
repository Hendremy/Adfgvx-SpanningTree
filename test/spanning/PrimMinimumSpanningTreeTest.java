package spanning;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import org.jgrapht.alg.interfaces.SpanningTreeAlgorithm.SpanningTree;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

public class PrimMinimumSpanningTreeTest {

	/*
	 * CONSTRUCTOR TESTS
	 */
	@Test
	void testPrimMinimumSpanningTree() {
		SimpleWeightedGraph<Integer, DefaultWeightedEdge> g = createSimpleWeightedGraph();
		assertNotNull(new PrimMinimumSpanningTree<>(g));
	}
	
	@Test
	void constructWithNullGraph() {
		SimpleWeightedGraph<Integer, DefaultWeightedEdge> g = null;
		assertThrows(NullPointerException.class, () -> {
			new PrimMinimumSpanningTree<>(g);
			});
	}

	/*
	 * SPANNING TREE/FOREST TESTS
	 */
	@Test
	void testGetSpanningTree() {
		SimpleWeightedGraph<Integer, DefaultWeightedEdge> g = createSimpleWeightedGraph();
		PrimMinimumSpanningTree<Integer, DefaultWeightedEdge> prim = new PrimMinimumSpanningTree<>(g);
		SpanningTree<DefaultWeightedEdge> spt = prim.getSpanningTree();
		assertNotNull(spt);
		assertEquals(6, spt.getEdges().size());
		assertEquals(12.0, spt.getWeight(), 0.001);
	}
	
	@Test
	void getSpanningTreeMultipleTimes() {
		SimpleWeightedGraph<Integer, DefaultWeightedEdge> g = createSimpleWeightedGraph();
		PrimMinimumSpanningTree<Integer, DefaultWeightedEdge> prim = new PrimMinimumSpanningTree<>(g);
		SpanningTree<DefaultWeightedEdge> spt = prim.getSpanningTree();
		spt = prim.getSpanningTree();
		spt = prim.getSpanningTree();		
		assertNotNull(spt);
		assertEquals(6, spt.getEdges().size());
		assertEquals(12.0, spt.getWeight(), 0.001);
	}
	
	@Test
	void graphWithoutEdgesSpanningTree() {
		SimpleWeightedGraph<Integer, DefaultWeightedEdge> g = createNoEdgeGraph();
		PrimMinimumSpanningTree<Integer, DefaultWeightedEdge> prim = new PrimMinimumSpanningTree<>(g);
		SpanningTree<DefaultWeightedEdge> spt = prim.getSpanningTree();
		assertNotNull(spt);
		assertEquals(0, spt.getEdges().size());
		assertEquals(0, spt.getWeight(), 0.001);
	}
	
	@Test
	void emptyGraphSpanningTree() {
		SimpleWeightedGraph<Integer, DefaultWeightedEdge> g = createEmptyGraph();
		PrimMinimumSpanningTree<Integer, DefaultWeightedEdge> prim = new PrimMinimumSpanningTree<>(g);
		SpanningTree<DefaultWeightedEdge> spt = prim.getSpanningTree();
		assertNotNull(spt);
		assertEquals(0, spt.getEdges().size());
		assertEquals(0, spt.getWeight(), 0.001);
	}

	/*
	 * SPANNING TREE FROM SPECIFIC NODE TESTS
	 */
	@Test
	void testGetSpanningTreeFromVertex() {
		SimpleWeightedGraph<Integer, DefaultWeightedEdge> g = createSimpleWeightedGraph();
		PrimMinimumSpanningTree<Integer, DefaultWeightedEdge> prim = new PrimMinimumSpanningTree<>(g);
		SpanningTree<DefaultWeightedEdge> spt = prim.getSpanningTree(1);
		assertNotNull(spt);
		assertEquals(5, spt.getEdges().size());
		assertEquals(10.0, spt.getWeight(), 0.001);
	}
	
	@Test
	void getSpanningTreeFromAllVertices() {
		SimpleWeightedGraph<Integer, DefaultWeightedEdge> g = createSimpleWeightedGraph();
		PrimMinimumSpanningTree<Integer, DefaultWeightedEdge> prim = new PrimMinimumSpanningTree<>(g);
		SpanningTree<DefaultWeightedEdge> spt = prim.getSpanningTree(1);
		
		for(int i = 1; i < 7; ++i) {
			spt = prim.getSpanningTree(i);
			assertNotNull(spt);
			assertEquals(5, spt.getEdges().size());
			assertEquals(10.0, spt.getWeight(), 0.001);
		}
		for(int i = 7; i < 9 ; ++i) {
			spt = prim.getSpanningTree(i);
			assertNotNull(spt);
			assertEquals(1, spt.getEdges().size());
			assertEquals(2, spt.getWeight(), 0.001);
		}
	}
	
	@Test
	void getSpanningTreeVertexOutofGraph() {
		SimpleWeightedGraph<Integer, DefaultWeightedEdge> g = createSimpleWeightedGraph();
		PrimMinimumSpanningTree<Integer, DefaultWeightedEdge> prim = new PrimMinimumSpanningTree<>(g);
		assertThrows(IllegalArgumentException.class, () -> prim.getSpanningTree(50));
	}
	
	@Test
	void getSpanningTreeNullVertex() {
		SimpleWeightedGraph<Integer, DefaultWeightedEdge> g = createSimpleWeightedGraph();
		PrimMinimumSpanningTree<Integer, DefaultWeightedEdge> prim = new PrimMinimumSpanningTree<>(g);
		assertThrows(NullPointerException.class, () -> prim.getSpanningTree(null));
	}
	
	@Test
	void graphWithoutEdgesSpanningTreeFromVertex() {
		SimpleWeightedGraph<Integer, DefaultWeightedEdge> g = createNoEdgeGraph();
		PrimMinimumSpanningTree<Integer, DefaultWeightedEdge> prim = new PrimMinimumSpanningTree<>(g);
		SpanningTree<DefaultWeightedEdge> spt = prim.getSpanningTree(1);
		assertNotNull(spt);
		assertEquals(0, spt.getEdges().size());
		assertEquals(0, spt.getWeight(), 0.001);
	}

	/**
	 * Creates a sample connected undirected weighted graph [MULLER example p.20]
	 * 
	 * @return connected undirected graph
	 */
	public static SimpleWeightedGraph<Integer, DefaultWeightedEdge> createSimpleWeightedGraph() {
		SimpleWeightedGraph<Integer, DefaultWeightedEdge> g = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);

		// Création des sommets
		g.addVertex(1);
		g.addVertex(2);
		g.addVertex(3);
		g.addVertex(4);
		g.addVertex(5);
		g.addVertex(6);
		
		// Création des arêtes pondérées
		g.setEdgeWeight(g.addEdge(1, 2), 5);
		g.setEdgeWeight(g.addEdge(1, 4), 3);
		g.setEdgeWeight(g.addEdge(1, 5), 2);
		g.setEdgeWeight(g.addEdge(2, 3), 5);
		g.setEdgeWeight(g.addEdge(2, 5), 4);
		g.setEdgeWeight(g.addEdge(3, 5), 2);
		g.setEdgeWeight(g.addEdge(3, 6), 1);
		g.setEdgeWeight(g.addEdge(4, 5), 1);
		g.setEdgeWeight(g.addEdge(5, 6), 3);

		// Ajout d'une deuxième composante connexe
		g.addVertex(7);
		g.addVertex(8);
		g.setEdgeWeight(g.addEdge(7, 8), 2);
		
		return g;
	}
	
	public static SimpleWeightedGraph<Integer, DefaultWeightedEdge> createNoEdgeGraph() {
		SimpleWeightedGraph<Integer, DefaultWeightedEdge> g = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		g.addVertex(1);
		g.addVertex(2);
		g.addVertex(3);
		g.addVertex(4);
		g.addVertex(5);
		g.addVertex(6);
		return g;
	}
	

	public static SimpleWeightedGraph<Integer, DefaultWeightedEdge> createEmptyGraph() {
		SimpleWeightedGraph<Integer, DefaultWeightedEdge> g = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		return g;
	}
	
}
