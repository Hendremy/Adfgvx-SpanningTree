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

	/*
	 * SPANNING TREE FROM SPECIFIC NODE TESTS
	 */
	@Test
	void testGetSpanningTreeV() {
		SimpleWeightedGraph<Integer, DefaultWeightedEdge> g = createSimpleWeightedGraph();
		PrimMinimumSpanningTree<Integer, DefaultWeightedEdge> prim = new PrimMinimumSpanningTree<>(g);
		SpanningTree<DefaultWeightedEdge> spt = prim.getSpanningTree(1);
		assertNotNull(spt);
		assertEquals(5, spt.getEdges().size());
		assertEquals(10.0, spt.getWeight(), 0.001);
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
}
