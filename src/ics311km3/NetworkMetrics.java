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
		// This algorithm is incorrect.
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
		float degreeSum = 0;
		while (i.hasNext()) {
			Vertex v = i.next();
			int d = v.degree();
			degreeSum += d / 2 * (d - 1);
			for (int j = 0; j < d - 1; j++) {
				for (int k = j; k < d; k++) {
					if (g.getArc(v.adjacentVertices().get(j), v.adjacentVertices().get(k)) != null) {
						numTriangles++;
					}
				}
			}
		}
		float clusteringCoefficient = numTriangles / degreeSum;
        data.put(CLUSTERING_COEFFICIENT, clusteringCoefficient);
		return data;
	}
	
	private static Map<String, Object> computeMeanGeodesicDistance(Graph g, Map<String, Object> data) {
        data.put(MEAN_GEODESIC_DISTANCE, 0);
		return data;
	}
	
	private static Map<String, Object> computeGraphDiameter(Graph g, Map<String, Object> data) {
        data.put(DIAMETER, 0);
		return data;
	}
}
