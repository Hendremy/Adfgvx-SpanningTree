package spanning;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.SpanningTreeAlgorithm;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

public class PrimMinimumSpanningTree<V extends Comparable<? super V>, E> implements SpanningTreeAlgorithm<E>{

	private Graph<V,E> graph;
	private Set<V> forestVertices;
	private Set<V> usedForestVertices;
	private Set<V> remainingVertices;
	private Set<V> usedVertices;
	/**
	 * Constructor
	 * 
	 * @param g The graph
	 */
	public PrimMinimumSpanningTree(Graph<V, E> graph) {
		validateGraph(graph);
		this.graph = graph;
		this.forestVertices = null;
		this.usedForestVertices = null;
		this.remainingVertices = null;
		this.usedVertices = null;
	}
	
	/**
	 * Valide le graphe fourni en argument du constructeur et lance des exceptions dans le cas où
	 * le graphe ne serait pas valide. 
	 * @param graph le graphe à valider
	 * @throws NullPointerException si graph est null
	 */
	private void validateGraph(Graph<V,E> graph) {
		if(graph == null) throw new NullPointerException();
	}
	
	/**
	 * Finds the minimum spanning tree/forest of the
	 * weighted undirected graph.
	 */
	@Override
	public SpanningTree<E> getSpanningTree() {
		var spanningForest = new TempTree();
		//Initialisation du 'pool' de sommets à explorer (mis à jour au fur et à mesure de la création de l'arbre)
		//Nécessaire pour que la méthode getSpanningTree(V vertex) sache quels sommets explorer
		initForestVertices();

		//Tant qu'il y a des sommets à explorer
		while(!this.forestVertices.isEmpty()) {
			var vertex = this.forestVertices.iterator().next();
			var spanningTree = getSpanningTree(vertex);
			spanningForest.addAllEdges(spanningTree.getEdges(),spanningTree.getWeight());
		}

		//Remise à null du 'pool' de sommet à explorer
		teardownForestVertices();
		return new SpanningTreeImpl<>(spanningForest.getEdges(), spanningForest.getWeight());
	}
	
	/**
	 * Initialise les ensembles de sommets de la forêt.
	 */
	private void initForestVertices() {
		this.forestVertices = new HashSet<V>(graph.vertexSet());
		this.usedForestVertices = new HashSet<V>();
	}
	
	/**
	 * Détruit les ensembles de sommets de la forêt.
	 */
	private void teardownForestVertices() {
		this.forestVertices = null;
		this.usedForestVertices = null;
	}
	
	/**
	 * Finds the minimum spanning tree of the weighted undirected
	 * graph rooted at the supplied start vertex.
	 * 
	 * @param startVertex first vertex of the SPT
	 */
	public SpanningTree<E> getSpanningTree(V startVertex) {
		//Validation du sommet et lancement d'exception si nécessaire
		checkStartVertex(startVertex);
		initRemainingVertices();
		//Initialisation d'un objet intermédiaire pour pouvoir ajouter les arêtes au fur et à mesure
		var spanningTree = new TempTree();
		useVertex(startVertex);
		if(this.graph.degreeOf(startVertex) > 0) {
			//Initialisation des arêtes disponibles depuis le sommet de départ
			var availableEdges = initAvailableEdges(startVertex);
			buildSpanningTree(startVertex, availableEdges, spanningTree);
		}
		return new SpanningTreeImpl<>(spanningTree.getEdges(),spanningTree.getWeight());
	}
	
	/**
	 * Initialise les ensembles de sommets utilisés & non utilisés.
	 * Si appelé par getSpanningTree(), reprendra les sommets utilisés & non-utilisés de la création de la forêt
	 */
	private void initRemainingVertices() {
		if(this.forestVertices != null && this.usedForestVertices != null) {
			this.remainingVertices = this.forestVertices;
			this.usedVertices = this.usedForestVertices;
		}else {
			this.remainingVertices = new HashSet<V>(graph.vertexSet());
			this.usedVertices = new HashSet<V>();
		}
	}
	
	/**
	 * Vérifie que le sommet de départ de l'arbre soit valide, lance des exceptions si invalide.
	 * @param startVertex le sommet de départ
	 * @throws NullPointerException si startVertex est null
	 * @throws IllegalArgumentException si startVertex est invalide
	 */
	private void checkStartVertex(V startVertex) {
		if(startVertex == null) throw new NullPointerException();
		if(!this.graph.containsVertex(startVertex)) throw new IllegalArgumentException();
	}
	
	/**
	 * Construit l'arbre couvrant à partir du sommet spécifié.
	 * @param startVertex le sommet de départ
	 * @param availableEdges l'ensemble des arêtes disponible depuis le sommet
	 * @param spanningTree l'arbre à construire
	 * @param totalWeight le poids total de l'arbre à construire
	 */
	private void buildSpanningTree(V startVertex, SortedSet<E> availableEdges, TempTree spanningTree) {
		while(!(availableEdges.isEmpty() || this.remainingVertices.isEmpty())){
			//On récupère le premier élément du SortedSet qui est donc l'arête de poids minimal
			E minWeightEdge = availableEdges.first();
			//Si l'arête est valide (un sommet déjà dans l'arbre, l'autre pas encore), 
			//on l'ajoute à l'arbre, sinon on la retire des arêtes disponibles
			if(edgeIsValid(minWeightEdge)) {
				updateSpanningTree(minWeightEdge, availableEdges, spanningTree);
			}else {
				availableEdges.remove(minWeightEdge);
			}
		}
	}
	
	/**
	 * Ajoute l'arête de poids minimum à l'arbre couvrant et met à jour l'ensemble des arêtes disponibles.
	 * @param minWeightEdge l'arête de poids minimum
	 * @param availableEdges l'ensemble des arêtes disponibles
	 * @param spanningTree l'arbre couvrant
	 * @param totalWeight le poids de l'arbre couvrant
	 */
	private void updateSpanningTree(E minWeightEdge, SortedSet<E> availableEdges, TempTree spanningTree) {
		//Ajout de l'arête à l'arbre
		spanningTree.addEdge(minWeightEdge, this.graph.getEdgeWeight(minWeightEdge));
		availableEdges.remove(minWeightEdge);
		//Identification du sommet cible (qui n'était pas encore dans l'arbre) pour
		//ajouter ses arêtes au 'pool' des arêtes disponibles 
		V target = getActualTarget(minWeightEdge);
		availableEdges.addAll(this.graph.edgesOf(target));
		useVertex(target);
	}
	
	/**
	 * Retourne le sommet cible de l'arête (dans le sens de création de l'arbre couvrant).
	 * @param edge l'arête
	 * @return le sommet cible de l'arête
	 */
	private V getActualTarget(E edge) {
		V supposedTarget = getTarget(edge);
		return this.remainingVertices.contains(supposedTarget) ? supposedTarget : this.getSource(edge);
	}
	
	/**
	 * Initialise l'arbre-ensemble des arêtes disponibles à partir du sommet de départ
	 * @param startVertex
	 * @return
	 */
	private SortedSet<E> initAvailableEdges(V startVertex){
		var edgeComp = new EdgeComparator<E>(this.graph);
		SortedSet<E> availableEdges = new TreeSet<E>(edgeComp);
		availableEdges.addAll(this.graph.edgesOf(startVertex));
		return availableEdges;
	}
	
	/**
	 * Marque le sommet comme étant utilisé et faisant déjà partie de l'arbre/forêt couvrant(e).
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
	 * @param <Edge> classe de l'arête
	 */
	private class EdgeComparator<Edge> implements Comparator<Edge>{

		private Graph<V,Edge> graph;
		
		/**
		 * Construit un comparateur d'arêtes pondérés à partir d'un graphe pondéré.
		 * @param graph le graphe pondéré
		 */
		public EdgeComparator(Graph<V,Edge> graph){
			this.graph = graph;
		}
		
		/**
		 * Compare deux arêtes pondérées.
		 * @param edgeA la première arête pondérée
		 * @param edgeB la deuxième arête pondérée
		 * @return un entier positif si poids de A > poids de B, 0 si leurs poids sont égaux ou un entier négatif si poids A < poids B
		 */
		@Override
		public int compare(Edge edgeA, Edge edgeB) {
			double weightA = this.graph.getEdgeWeight(edgeA);
			double weightB = this.graph.getEdgeWeight(edgeB);
			if(weightA == weightB) {//Comparaison des sources & cibles pour pas considérer deux arêtes de même poids comme étant la même
				if(!edgesAreDuplicate(edgeA, edgeB)) {
					return -1;
				}
			}
			return Double.compare(weightA, weightB);
		}
		
		/**
		 * Vérifie si deux arêtes relients les mêmes sommets.
		 * @param edgeA la première arête
		 * @param edgeB la deuxième arête
		 * @return vrai si les deux arêtes relient les mêmes sommets, sinon faux.
		 */
		private boolean edgesAreDuplicate(Edge edgeA, Edge edgeB) {
			V sourceA = graph.getEdgeSource(edgeA), targetA = graph.getEdgeTarget(edgeA);
			V sourceB = graph.getEdgeSource(edgeB), targetB = graph.getEdgeTarget(edgeB);
			
			return (sourceA.equals(sourceB) && targetA.equals(targetB))
					|| (sourceA.equals(targetB) && targetA.equals(sourceB));
		}
	}
	
	/**
	 * Définit un arbre en train d'être construit.
	 * @author hendr
	 */
	private class TempTree{
		
		private Set<E> edges;
		private double weight;
		
		/**
		 * Initialise l'arbre en construction.
		 */
		public TempTree() {
			edges = new HashSet<E>();
			weight = 0.0;
		}
		
		/**
		 * Retourne les arêtes de l'arbre.
		 * @return les arêtes de l'arbre
		 */
		public Set<E> getEdges() {
			return this.edges;
		}
		
		/**
		 * Retourne le poids total de l'arbre
		 * @return le poids total de l'arbre
		 */
		public double getWeight() {
			return this.weight;
		}
		
		/**
		 * Ajoute une arête et son poids à l'arbre.
		 * @param edge l'arête à ajouter
		 * @param weight le poids de l'arête à ajouter
		 */
		public void addEdge(E edge, double weight) {
			this.edges.add(edge);
			this.weight += weight;
		}
		
		/**
		 * Ajoute toutes les arêtes d'une collection et leur poids total à l'arbre.
		 * @param edges les arêtes d'une collection
		 * @param totalWeight le poids total des arêtes de la collection
		 */
		public void addAllEdges(Collection<E> edges, double totalWeight) {
			this.edges.addAll(edges);
			this.weight += totalWeight;
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
//		SimpleWeightedGraph<Integer, DefaultWeightedEdge> g2 = createUndirectedWeightedGraph(5000, 10000);
//		SimpleWeightedGraph<Integer, DefaultWeightedEdge> g2 = createUndirectedWeightedGraph(10000, 20000);
//		SimpleWeightedGraph<Integer, DefaultWeightedEdge> g2 = createUndirectedWeightedGraph(20000, 40000);
		SimpleWeightedGraph<Integer, DefaultWeightedEdge> g2 = createUndirectedWeightedGraph(100000, 200000);
		
		
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
		g.addVertex(7);
		g.addVertex(8);
		g.setEdgeWeight(g.addEdge(7, 8), 2);
		
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

