package ics311km3;

import java.util.Iterator;
import java.util.Map;

public class NetworkMetrics implements Constants {

	public static Map<String, Object> compute(Graph g, Map<String, Object> data) {
		data = computeReciprocity(g, data);
		data = computeDegreeCorrelation(g, data);
		data = computeClusteringCoefficient(g, data);
		data = computeMeanGeodesicDistance(g, data);
		data = computeGraphDiameter(g, data);
		return data;
	}
	
	private static Map<String, Object> computeReciprocity(Graph g, Map<String, Object> data) {
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
		int degU, degV;
		Iterator<Arc> i = g.arcs();
		while (i.hasNext()) {
			Arc a = i.next();
			Vertex u = a.getOrigin();
			Vertex v = a.getDestination();
			degU = u.numAdjacentVertices();
			degV = v.numAdjacentVertices();
		}
		
		return data;
	}
	
	private static Map<String, Object> computeClusteringCoefficient(Graph g, Map<String, Object> data) {
        // Iterate over vertices.
		Iterator<Vertex> i = g.vertices();
        int numTriads = 0;
        while (i.hasNext()) {
            Vertex u = i.next();
            // Iterate over vertices adjacent to u.
            Iterator<Vertex> j = u.adjacentVertices();
            while (j.hasNext()) {
                Vertex v = j.next();
                // Iterate over vertices adjacent to v.
                Iterator<Vertex> k = v.adjacentVertices();
                while (k.hasNext()) {
                    Vertex w = k.next();
                    // If w == u, there's a triad.
                    if (w == u) {
                        numTriads++;
                    }
                }
            }
        }
        numTriads /= 3;
        data.put(CLUSTERING_COEFFICIENT, (float)numTriads / g.numVertices());
		return data;
	}
	
	private static Map<String, Object> computeMeanGeodesicDistance(Graph g, Map<String, Object> data) {
		return data;
	}
	
	private static Map<String, Object> computeGraphDiameter(Graph g, Map<String, Object> data) {
		return data;
	}
}
