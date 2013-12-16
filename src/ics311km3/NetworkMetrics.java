package ics311km3;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class NetworkMetrics implements Constants {

	/**
	 * Computes the metrics specified in Project 3.
	 * 
	 * @param g
	 * @param data
	 * @return the same map data, with added mappings for the five required metrics.
	 */
	public static Map<String, Object> compute(Graph g, Map<String, Object> data) {
		data = computeReciprocity(g, data);
		data = computeDegreeCorrelation(g, data);
		data = computeClusteringCoefficient(g, data);
		data = computeMeanGeodesicDistance(g, data);
		return data;
	}
	
	/**
	 * This algorithm finds the number of reciprocal arcs in g.
	 * 
	 * Treats g as a directed graph, obviously.
	 * 
	 * @param g
	 * @param data
	 * @return the same map data, with an added mapping for reciprocity.
	 */
	private static Map<String, Object> computeReciprocity(Graph g, Map<String, Object> data) {
		// Don't touch anything here, this algorithm is g2g.
		Iterator<Arc> i = g.arcs();
		int numReciprocals = 0;
		while (i.hasNext()) {
			Arc a = i.next();
			// Check for an arc with origin and destination swapped.
			Arc reciprocal = g.getArc(a.getDestination(), a.getOrigin());
			if (reciprocal != null) {
				numReciprocals++;
			}
		}
		data.put(RECIPROCITY, (float)numReciprocals / g.numArcs());
		return data;
	}
	
	/**
	 * This algorithm is my implementation of Equation (8.27) in Newman's section 8.7. It follows
	 * the calculation for r but gives different results than those of Dr. Suthers.
	 * 
	 * Treats g as a directed graph.
	 * 
	 * @param g
	 * @param data
	 * @return the same map data, with an added mapping for degree correlation.
	 */
	private static Map<String, Object> computeDegreeCorrelation(Graph g, Map<String, Object> data) {
		Iterator<Arc> i = g.arcs();
		int k = 0;
		int s1 = 0, s2 = 0, s3 = 0;
		while (i.hasNext()) {
			Arc a = i.next();
			Vertex u = a.getOrigin();
			Vertex v = a.getDestination();
			k += u.degree() * v.degree();
			if (g.getAnnotation(u, VISITED) == null) {
				s1 += u.degree();
				s2 += Math.pow(u.degree(), 2);
				s3 += Math.pow(u.degree(), 3);
				g.setAnnotation(u, VISITED, VISITED);
			}
			if (g.getAnnotation(v, VISITED) == null) {
				s1 += v.degree();
				s2 += Math.pow(v.degree(), 2);
				s3 += Math.pow(v.degree(), 3);
				g.setAnnotation(v, VISITED, VISITED);
			}
		}
		g.clearAnnotations(VISITED);
		int se = 2 * k;
		double degreeCorrelation = (s1 * se - Math.pow(s2, 2)) / (s1 * s3 - Math.pow(s2, 2));
		data.put(DEGREE_CORRELATION, degreeCorrelation);
		
		return data;
	}
	
	/**
	 * This algorithm is my implementation of Equation (10.3) in Newman's section 10.2. It follows
	 * the calculation for C, but again gives different results than those of Dr. Suthers.
	 * 
	 * Treats g as an undirected graph.
	 * 
	 * @param g
	 * @param data
	 * @return the same map data, with an added mapping for clustering coefficient.
	 */
	private static Map<String, Object> computeClusteringCoefficient(Graph g, Map<String, Object> data) {
		Iterator<Vertex> i = g.vertices();
		int numTriangles = 0; 
		float connectedTriples = 0;
		while (i.hasNext()) {
			Vertex v = i.next();
			int d = v.degree();
			List<Vertex> adjacentVertices = v.adjacentVertices();
			for (int j = 0; j < d - 1; j++) {
				for (int k = j + 1; k < d; k++) {
					if (g.getArc(adjacentVertices.get(j), adjacentVertices.get(k)) != null
				  	 || g.getArc(adjacentVertices.get(k), adjacentVertices.get(j)) != null) {
						numTriangles++;
					}
					connectedTriples++;
				}
			}
		}
		float clusteringCoefficient = numTriangles * 3 / connectedTriples;
        data.put(CLUSTERING_COEFFICIENT, clusteringCoefficient);
		return data;
	}
	
	/**
	 * Compute mean geodesic distance with iterated BFS.
	 * 
	 * Treats g as an undirected graph.
	 * 
	 * @param g
	 * @param data
	 * @return the same map data, with an added mapping for geodesic distance and graph diameter.
	 */
	private static Map<String, Object> computeMeanGeodesicDistance(Graph g, Map<String, Object> data) {
		Vertex[] vertices = (Vertex[])g.verticesCollection().toArray(new Vertex[g.verticesCollection().size()]);
		int minSum = 0, max = 0;
		for (int j = 0; j < vertices.length - 1; j++) {
			for (int k = j + 1; k < vertices.length; k++) {
				Vertex u = vertices[j];
				Vertex v = vertices[k];
				// BFS returns a 2-element array: the shortest path length from u to v, and the
				// path length from u to the farthest vertex in g.
				int[] distances = bfs(g, u, v);
				// Sum the shortest path lengths to find mean geodesic distance.
				minSum += distances[0];
				// Check if the most recent run of bfs found the diameter of g. If so, assign to max.
				if (distances[1] > max) {
					max = distances[1];
				}
			}
		}
		double meanGeoDist = (double) minSum / (g.numVertices() * (g.numVertices() - 1));
        data.put(MEAN_GEODESIC_DISTANCE, meanGeoDist);
        data.put(DIAMETER, max);
		return data;
	}
	
	/**
	 * This algorithm is my implementation of BFS in CLRS, with the addition of min and 
	 * max, which find the shortest path from s to t and the diameter of g, respectively.
	 * 
	 * Treats g as an undirected graph.
	 * 
	 * @param g
	 * @param s
	 * @param t
	 * @return an int[] array with 2 elements: element 0 is the shortest path length between 
	 *         s and t, element 1 is the path length between s and the farthest vertex in g.
	 */
	private static int[] bfs(Graph g, Vertex s, Vertex t) {
		Iterator<Vertex> i = g.vertices();
		while (i.hasNext()) {
			Vertex v = i.next();
			if (v != s) {
				g.setAnnotation(v, COLOR, WHITE);
				g.setAnnotation(v, D, INFINITY);
				g.setAnnotation(v, PARENT, NIL);
			}
		}
		g.setAnnotation(s, COLOR, GRAY);
		g.setAnnotation(s, D, 0);
		g.setAnnotation(s, PARENT, NIL);
		Queue<Vertex> q = new LinkedList<Vertex>();
		q.add(s);
		int min = 0, max = 0;
		while (!q.isEmpty()) {
			Vertex u = q.poll();
			i = u.adjacentVertices().iterator();
			while (i.hasNext()) {
				Vertex v = i.next();
				if (g.getAnnotation(v, COLOR).equals(WHITE)) {
					g.setAnnotation(v, COLOR, GRAY);
					g.setAnnotation(v, D, (int)g.getAnnotation(u, D) + 1);
					g.setAnnotation(v, PARENT, u);
					q.add(v);
					// If v == t, we found the shortest path from s to t.
					if (v == t) {
						min = (int)g.getAnnotation(v, D);
					}
					// Set max during each iteration, as the final iteration
					// will be the maximum value.
					max = (int)g.getAnnotation(v, D);
				}
			}
			g.setAnnotation(u, COLOR, BLACK);
		}
		return new int[] { min, max };
	}
}







