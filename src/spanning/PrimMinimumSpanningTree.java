package spanning;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.SpanningTreeAlgorithm;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

public class PrimMinimumSpanningTree<V, E> implements SpanningTreeAlgorithm<E>{

	private Graph<V,E> graph;
	private Set<V> remainingVertices;
	private Set<V> usedVertices;
	/**
	 * Constructor
	 * 
	 * @param g The graph
	 */
	public PrimMinimumSpanningTree(Graph<V, E> g) {
		this.graph = g;
		this.remainingVertices = new HashSet<V>(g.vertexSet());
		this.usedVertices = new HashSet<V>();
	}
	
	/**
	 * Finds the minimum spanning tree/forest of the
	 * weighted undirected graph.
	 */
	@Override
	public SpanningTree<E> getSpanningTree() {//Admet graphe non-connexe => qd arbre fini, cherche si y'en a pas d'autres pr faire une foret
		/*Tant qu'il y a des vertex dans le remainingVertices, faire getSpanningTree(vertex) 
		 * et en faire une collection de SpanningTree=> Retour un SpanningTree et pas plusieurs ???
		 * 
		 * */
		var spanningForest = new HashSet<E>();
		double totalWeight = 0;
		while(!remainingVertices.isEmpty()) {
			var vertex = remainingVertices.iterator().next();
			var spanningTree = getSpanningTree(vertex);
			spanningForest.addAll(spanningTree.getEdges());
			totalWeight += spanningTree.getWeight();
		}
		return new SpanningTreeImpl<>(spanningForest, totalWeight);
	}
	
	/**
	 * Finds the minimum spanning tree of the weighted undirected
	 * graph rooted at the supplied start vertex.
	 * 
	 * @param startVertex first vertex of the SPT
	 */
	public SpanningTree<E> getSpanningTree(V startVertex) {//Admet pas graphe connexe, cherche juste arbre couvrant àpd de sommet
		useVertex(startVertex);
		var availableEdges = initAvailableEdges(startVertex);
		
		Set<E> spanningTree = new HashSet<E>();
		double totalWeight = 0;
		
		while(!availableEdges.isEmpty() && (!this.remainingVertices.isEmpty() || !availableEdges.isEmpty())){
			E minWeightEdge = availableEdges.first();
			
			if(edgeIsValid(minWeightEdge)) {
				spanningTree.add(minWeightEdge);
				totalWeight += this.graph.getEdgeWeight(minWeightEdge);
				
				V supposedTarget = getTarget(minWeightEdge);
				V source, target;
				if(this.remainingVertices.contains(supposedTarget)) {
					source = getSource(minWeightEdge);
					target = supposedTarget;
				}else {
					source = supposedTarget;
					target = getSource(minWeightEdge);
				}
				
				availableEdges.remove(minWeightEdge);
				availableEdges.addAll(this.graph.edgesOf(target));
				useVertex(target);
				
			}else {
				availableEdges.remove(minWeightEdge);
			}
		}
		//Quid de graphe connexe ou pas ? Déterminer qd on a ts les sommets d'une partie connexe
		return new SpanningTreeImpl<>(spanningTree,totalWeight);
	}
	
	/**
	 * Initialise l'arbre-ensemble des arêtes disponibles à partir du sommet de départ
	 * @param startVertex
	 * @return
	 */
	private TreeSet<E> initAvailableEdges(V startVertex){
		var edgeComp = new EdgeComparator<E>(this.graph);
		TreeSet<E> availableEdges = new TreeSet<E>(edgeComp);
		availableEdges.addAll(this.graph.edgesOf(startVertex));
		return availableEdges;
	}
	
	/**
	 * Marque le sommet comme étant utilisé et faisant déjà partie de l'arbre couvrant.
	 * @param vertex le sommet
	 */
	private void useVertex(V vertex) {
		this.remainingVertices.remove(vertex);
		this.usedVertices.add(vertex);
	}
	
	/**
	 * Retourne le sommet source de l'arête.
	 * @param edge l'arête
	 * @return le sommet source de l'arête
	 */
	private V getSource(E edge) {
		return this.graph.getEdgeSource(edge);
	}
	
	/**
	 * Retourne le sommet cible de l'arête.
	 * @param edge l'arête
	 * @return le sommet cible de l'arête
	 */
	private V getTarget(E edge) {
		return this.graph.getEdgeTarget(edge);
	}
	
	
	/**
	 * Retourne vrai si l'arête peut être utilisée pour construire l'arbre couvrant, sinon faux.
	 * 
	 * @param edge l'arête à évaluer
	 * @return vrai si l'arête peut être utilisée pour construire l'arbre couvrant, sinon faux
	 */
	private boolean edgeIsValid(E edge) {
		V source = getSource(edge);
		V target = getTarget(edge);
		return (this.remainingVertices.contains(source) && this.usedVertices.contains(target))
				|| (this.remainingVertices.contains(target) && this.usedVertices.contains(source));
	}
	
	/**
	 * Définit un comparateur d'arêtes pondérés.
	 * @author hendr
	 *
	 * @param <E> classe de l'arête
	 */
	private class EdgeComparator<E> implements Comparator<E>{

		private Graph<V,E> graph;
		
		/**
		 * Construit un comparateur d'arêtes pondérés à partir d'un graphe pondéré.
		 * @param graph le graphe pondéré
		 */
		public EdgeComparator(Graph<V,E> graph){
			this.graph = graph;
		}
		
		/**
		 * Compare deux arêtes pondérées.
		 * @param edgeA la première arête pondérée
		 * @param edgeB la deuxième arête pondérée
		 * @return un entier positif si poids de A > poids de B, 0 si leurs poids sont égaux ou un entier négatif si poids A < poids B
		 */
		@Override
		public int compare(E edgeA, E edgeB) {
			double weightA = this.graph.getEdgeWeight(edgeA);
			double weightB = this.graph.getEdgeWeight(edgeB);
			if(weightA == weightB) {//Comparaison des sources & cibles pour pas considérer deux arêtes de même poids comme étant la même
				if(this.graph.getEdgeSource(edgeA) != this.graph.getEdgeSource(edgeB)
						|| this.graph.getEdgeTarget(edgeA) != this.graph.getEdgeTarget(edgeB)) {
					return 1;
				}
			}
			return Double.compare(weightA, weightB);
		}
		
	}
	
	/*
	 * MAIN - Exemple d'utilisation 
	 */
	public static void main(String[] args) {
		long time;

		System.out.println("ARBRE COUVRANT DE POIDS MINIMUM");

		/*
		 * Exemple 1 - MULLER [page 20] 
		 */
		System.out.println("\n>>> MULLER [page 20]");
		SimpleWeightedGraph<Integer, DefaultWeightedEdge> g1 = createSimpleWeightedGraph();
		System.out.println("\nGraphe généré");
		System.out.printf("  sommets : %d / arêtes : %d\n", g1.vertexSet().size(), g1.edgeSet().size());
		
		time = System.currentTimeMillis();
		org.jgrapht.alg.spanning.PrimMinimumSpanningTree<Integer, DefaultWeightedEdge> jgtPrim1 = new org.jgrapht.alg.spanning.PrimMinimumSpanningTree<>(g1);
		SpanningTree<DefaultWeightedEdge> jgtSpt1 = jgtPrim1.getSpanningTree();	
		System.out.println("\nArbre couvrant de poids minimal (JGraphT)");
		System.out.println("  poids total : "+(int)(jgtSpt1.getWeight()*10)/10.0);
		System.out.println("  arêtes : "+jgtSpt1.getEdges().size());
		System.out.printf("  temps écoulé = %.2f seconds\n", (System.currentTimeMillis() - time) / 1000.0);

		time = System.currentTimeMillis();
		PrimMinimumSpanningTree<Integer, DefaultWeightedEdge> prim1 = new PrimMinimumSpanningTree<>(g1);
		SpanningTree<DefaultWeightedEdge> spt1 = prim1.getSpanningTree(1);
		System.out.println("\nArbre couvrant de poids minimal");
		System.out.println("  poids total : "+(int)(spt1.getWeight()*10)/10.0);
		System.out.println("  arêtes : "+spt1.getEdges().size());
		System.out.printf("  temps écoulé = %.2f secondes\n", (System.currentTimeMillis() - time) / 1000.0);
		
		/*
		 * Exemple 2 - Graphe aléatoire
		 */
		System.out.println("\n>>> Graphe pondéré connexe aléatoire");
		SimpleWeightedGraph<Integer, DefaultWeightedEdge> g2 = createUndirectedWeightedGraph(5000, 10000);
//		SimpleWeightedGraph<Integer, DefaultWeightedEdge> g2 = createUndirectedWeightedGraph(10000, 20000);
//		SimpleWeightedGraph<Integer, DefaultWeightedEdge> g2 = createUndirectedWeightedGraph(20000, 40000);
//		SimpleWeightedGraph<Integer, DefaultWeightedEdge> g2 = createUndirectedWeightedGraph(100000, 200000);
		System.out.println("\nGraphe généré");
		System.out.printf("  sommets : %d / arêtes : %d\n", g2.vertexSet().size(), g2.edgeSet().size());
		
		time = System.currentTimeMillis();
		org.jgrapht.alg.spanning.PrimMinimumSpanningTree<Integer, DefaultWeightedEdge> jgtPrim2 = new org.jgrapht.alg.spanning.PrimMinimumSpanningTree<>(g2);
		SpanningTree<DefaultWeightedEdge> jgtSpt2 = jgtPrim2.getSpanningTree();	
		long jgtDuration = System.currentTimeMillis() - time;
		System.out.println("\nArbre couvrant de poids minimal (JGraphT)");
		System.out.println("  poids total : "+(int)(jgtSpt2.getWeight()*10)/10.0);
		System.out.println("  arêtes : "+jgtSpt2.getEdges().size());
		System.out.printf("  temps écoulé = %.2f seconds\n", jgtDuration / 1000.0);
		
		time = System.currentTimeMillis();
		PrimMinimumSpanningTree<Integer, DefaultWeightedEdge> prim2 = new PrimMinimumSpanningTree<>(g2);
		SpanningTree<DefaultWeightedEdge> spt2 = prim2.getSpanningTree();
		long customDuration = System.currentTimeMillis() - time;
		System.out.println("\nArbre couvrant de poids minimal");
		System.out.println("  poids total : "+(int)(spt2.getWeight()*10)/10.0);
		System.out.println("  arêtes : "+spt2.getEdges().size());
		System.out.printf("  temps écoulé = %.2f secondes\n", customDuration / 1000.0);	
		
		float performanceFactor = (float)customDuration / jgtDuration;
		if (performanceFactor >= 1.0f) {
			System.out.printf("\n==> %.1f fois plus lent", performanceFactor);
		} else {
			System.out.printf("\n==> %.1f fois plus rapide", 1.0f/performanceFactor);
		}
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
//		g.addVertex(7);
//		g.addVertex(8);
//		g.setEdgeWeight(g.addEdge(7, 8), 2);
		
		return g;
	}

	/**
	 * Creates a random connected undirected weighted graph
	 * 
	 * @param vertexCount number of vertices
	 * @param edgeCount number of edges to target, must be >= (vertexCount-1) 
	 * @return random undirected weighted graph
	 */
	public static SimpleWeightedGraph<Integer, DefaultWeightedEdge> createUndirectedWeightedGraph(int vertexCount, int edgeCount) {
		SimpleWeightedGraph<Integer, DefaultWeightedEdge> g = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		/* Ajout de <size> sommets au graphe. Chaque nouveau sommet est
		 * connecté à un sommet précédent choisi au hasard, ce qui
		 * garantit que le graphe est connexe (c'est également un arbre).
		 */
		for (int i=0; i<vertexCount; i++) {
			g.addVertex(i);
			if (i>0) g.setEdgeWeight(g.addEdge(i, (int)(Math.random()*i)), (int)(Math.random()*100)/10.0 + 0.1);
		}
		
		/*
		 * Ajout de maximum <additionalEdges> supplémentaires au graphe
		 */
		int additionalEdges = edgeCount - vertexCount + 1;
		for (int i=0; i<additionalEdges; i++) {
			int src = (int)(Math.random()*vertexCount);
			int dst = (int)(Math.random()*vertexCount);
			if (src != dst && g.getEdge(src, dst) == null) {
				g.setEdgeWeight(g.addEdge(src, dst), (int)(Math.random()*100)/10.0 + 0.1);
			}
		}
		return g;
	}
}

