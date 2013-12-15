package ics311km3;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class NetworkMetrics implements Constants {

	public static Map<String, Object> compute(Graph g, Map<String, Object> data) {
		data = computeReciprocity(g, data);
		data = computeDegreeCorrelation(g, data);
		data = computeClusteringCoefficient(g, data);
		data = computeMeanGeodesicDistance(g, data);
		return data;
	}
	
	private static Map<String, Object> computeReciprocity(Graph g, Map<String, Object> data) {
		// Don't touch anything here, this algorithm is g2g.
		Iterator<Arc> i = g.arcs();
		int numReciprocals = 0;
		while (i.hasNext()) {
			Arc a = i.next();
			Arc reciprocal = g.getArc(a.getOrigin(), a.getDestination());
			if (reciprocal != null) {
				numReciprocals++;
			}
		}
		data.put(NUM_RECIPROCALS, (float)numReciprocals / g.numArcs());
		return data;
	}
	
	private static Map<String, Object> computeDegreeCorrelation(Graph g, Map<String, Object> data) {
		Iterator<Arc> i = g.arcs();
		int k = 0;
		while (i.hasNext()) {
			Arc a = i.next();
			Vertex u = a.getOrigin();
			Vertex v = a.getDestination();
			k += u.degree() * v.degree();
		}
		Iterator<Vertex> j = g.vertices();
		int s1 = 0, s2 = 0, s3 = 0;
		while (j.hasNext()) {
			Vertex v = j.next();
			s1 += v.degree();
			s2 += Math.pow(v.degree(), 2);
			s3 += Math.pow(v.degree(), 3);
		}
		int se = 2 * k;
		double degreeCorrelation = (s1 * se - Math.pow(s2, 2)) / (s1 * s3 - Math.pow(s2, 2));
		data.put(DEGREE_CORRELATION, degreeCorrelation);
		
		return data;
	}
	
	private static Map<String, Object> computeClusteringCoefficient(Graph g, Map<String, Object> data) {
		Iterator<Vertex> i = g.vertices();
		int numTriangles = 0; 
		float connectedTriples = 0;
		while (i.hasNext()) {
			Vertex v = i.next();
			int d = v.degree();
			for (int j = 0; j < d - 1; j++) {
				for (int k = j + 1; k < d; k++) {
					if (g.getArc(v.adjacentVertices().get(j), v.adjacentVertices().get(k)) != null
				  	 || g.getArc(v.adjacentVertices().get(k), v.adjacentVertices().get(j)) != null) {
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
	
	private static Map<String, Object> computeMeanGeodesicDistance(Graph g, Map<String, Object> data) {
		Vertex[] vertices = (Vertex[])g.verticesCollection().toArray(new Vertex[g.verticesCollection().size()]);
		int meanGeoDist = 0, minSum = 0, max = 0;
		for (int j = 0; j < vertices.length - 1; j++) {
			for (int k = j; k < vertices.length; k++) {
				Vertex u = vertices[j];
				Vertex v = vertices[k];
				// BFS returns a 2-element array: the min and max distances.
				int[] distances = bfs(g, u, v);
				minSum += distances[0];
				if (distances[1] > max) {
					max = distances[1];
				}
			}
		}
		meanGeoDist = minSum / (int) (Math.pow(g.numVertices(), 2));
        data.put(MEAN_GEODESIC_DISTANCE, meanGeoDist);
        data.put(DIAMETER, max);
		return data;
	}
	
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
		int min = -1, max = -1;
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
					// Check if v == t.
					if (v == t) {
						min = (int)g.getAnnotation(v, D);
					}
					max = (int)g.getAnnotation(v, D);
				}
			}
			g.setAnnotation(u, COLOR, BLACK);
		}
		return new int[] { min, max };
	}
}







