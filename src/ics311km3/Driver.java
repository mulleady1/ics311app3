package ics311km3;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Driver implements Constants {

	private static Map<String, Object> data;
	
	public static void main(String[] args) {
        if (args.length < 1) {
            log("Usage: java ics311km2/Driver <vna_file> [<vna_file> ...]");
            System.exit(1);
        }
        for (String filename : args) {
        	data = new HashMap<String, Object>();
			Graph g = loadGraph(filename);
			analyzeGraph(g);
        }
	}

	static Graph loadGraph(String filename) {
		Graph g = new Graph();
		data.put(FILENAME, filename);
		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			// Skip first two lines.
			br.readLine();
			br.readLine();
			String line;
			boolean readVertices = true;
			while ((line = br.readLine()) != null) {
				// If line begins with an asterisk, start reading edges.
				if (line.charAt(0) == '*') {
					readVertices = false;
					br.readLine();
					line = br.readLine();
				}
				if (readVertices) {
					// Insert a vertex.
					String[] tokens = line.split("\\s+");
					if (tokens.length == 1) {
						g.insertVertex(tokens[0].trim(), tokens[0].trim());
					}
					else if (tokens.length > 1) {
						g.insertVertex(tokens[0], tokens[1].trim());
					}
				}
				else {
					// Insert an arc.
					String[] tokens = line.split("\\s+");
					Vertex u = g.getVertex(tokens[0].trim());
					Vertex v = g.getVertex(tokens[1].trim());
					if (u != null && v != null) {
						if (tokens.length == 2) {
							g.insertArc(u, v);
						}
						else if (tokens.length > 2) {
							g.insertArc(u, v, tokens[2].trim());
						}
					}
				}
			}
			br.close();
		} catch(IOException e) {
			log("IO Error: " + e.getMessage());
			System.exit(1);
		} 
		return g;
	}
	
	private static void analyzeGraph(Graph g) {
		computeDegree(g);
		computeDensity(g);
		computeSCC(g);
		if (g.numVertices() < 500) {
			computeAdditionalSCCData(g);
		}
		data = NetworkMetrics.compute(g, data);
		printData(g);
	}
	
	private static void computeDegree(Graph g) {
		Iterator<Vertex> i = g.vertices();
		int inDegreeMin = Integer.MAX_VALUE;
		int inDegreeMax = Integer.MIN_VALUE;
		int inDegreeTot = 0;
		int outDegreeMin = Integer.MAX_VALUE;
		int outDegreeMax = Integer.MIN_VALUE;
		int outDegreeTot = 0;
		while (i.hasNext()) {
			Vertex v = i.next();
			inDegreeTot  += v.inDegree();
			outDegreeTot += v.inDegree();
			if (v.inDegree() < inDegreeMin) {
				inDegreeMin = v.inDegree();
			}
			else if (v.inDegree() > inDegreeMax) {
				inDegreeMax = v.inDegree();
			}
			if (v.outDegree() < outDegreeMin) {
				outDegreeMin = v.outDegree();
			}
			else if (v.outDegree() > outDegreeMax) {
				outDegreeMax = v.outDegree();
			}
		}
		NumberFormat fmt = new DecimalFormat("#.###");
		String inAve  = fmt.format((float)inDegreeTot / g.numVertices());
		String outAve = fmt.format((float)outDegreeTot / g.numVertices());
		data.put(IN_DEGREE_MIN,  inDegreeMin);
		data.put(IN_DEGREE_MAX,  inDegreeMax);
		data.put(IN_DEGREE_AVE,  inAve);
		data.put(OUT_DEGREE_MIN, outDegreeMin);
		data.put(OUT_DEGREE_MAX, outDegreeMax);
		data.put(OUT_DEGREE_AVE, outAve);
	}
	
	private static void computeDensity(Graph g) {
		data.put(DENSITY, ((double)g.numArcs() / (g.numVertices() * (g.numVertices() - 1))));
	}
	
	private static List<Vertex> verticesInDecOrder;
	private static Map<Integer, Integer> sccMap;
	private static void computeSCC(Graph g) {
		verticesInDecOrder = new LinkedList<Vertex>();
		sccMap = new HashMap<Integer, Integer>();
		// Run dfs normally.
		dfs(g, true);
		// Compute g-transpose.
		Iterator<Arc> i = g.arcs();
		while (i.hasNext()) {
			g.reverseDirection(i.next());
		}
		// Run modified dfs.
		dfs(g, false);
		// Get % of vertices in largest scc.
		int largestSCC = 1;
		int max = 0;
		for (int j = 1; j <= sccMap.keySet().size(); j++) {
			if (sccMap.get(j) > max) {
				max = sccMap.get(j);
				largestSCC = j;
			}
		}
		NumberFormat fmt = new DecimalFormat("##.#");
		String percent = fmt.format((float)sccMap.get(largestSCC) / g.numVertices() * 100) + "%";
		data.put(PERCENT_IN_LARGEST_SCC, percent);
	}
	
	private static int time, scc, counter;
	private static void dfs(Graph g, boolean first) {
		Iterator<Vertex> i = g.vertices();
		while (i.hasNext()) {
			Vertex u = i.next();
			g.setAnnotation(u, COLOR, WHITE);
			g.setAnnotation(u, PARENT, NIL);
		}
		time = 0;
		scc = 0;
		// Run normal dfs.
		if (first) {
			i = g.vertices();
			while (i.hasNext()) {
				Vertex u = i.next();
				String color = (String)g.getAnnotation(u, COLOR);
				if (color.equals(WHITE)) {
					dfsVisit(g, u, first);
				}
			}
		}
		// Run dfs in order of decreasing vertices.
		else {
			for (int j = 0; j < verticesInDecOrder.size(); j++) {
				Vertex u = verticesInDecOrder.get(j);
				if (g.getAnnotation(u, COLOR).equals(WHITE)) {
					scc++;
					counter = 0;
					dfsVisit(g, u, first);
				}
			}
		}
	}
	
	private static void dfsVisit(Graph g, Vertex u, boolean first) {
		time++;
		g.setAnnotation(u, D, time);
		g.setAnnotation(u, COLOR, GRAY);
		Iterator<Vertex> i = u.outAdjacentVertices();
		while (i.hasNext()) {
			Vertex v = i.next();
			String color = (String)g.getAnnotation(v, COLOR);
			if (color.equals(WHITE)) {
				g.setAnnotation(v, PARENT, u);
				dfsVisit(g, v, first);
			}
		}
		g.setAnnotation(u, COLOR, BLACK);
		time++;
		g.setAnnotation(u, F, time);
		if (first) {
			verticesInDecOrder.add(0, u);
		}
		else {
			g.setAnnotation(u, SCC, scc);
			sccMap.put(scc, ++counter);
		}
	}

	private static void computeAdditionalSCCData(Graph g) {
		Map<Integer, List<String>> verticesBySCC = new HashMap<Integer, List<String>>();
		Iterator<Vertex> i = g.vertices();
		while (i.hasNext()) {
			Vertex v = i.next();
			// Get the list of vertices in verticesBySCC.
			int sccNumber = (Integer)g.getAnnotation(v, SCC);
			List<String> vertexList = verticesBySCC.get(sccNumber);
			// If the list is null, start one.
			if (vertexList == null) {
				vertexList = new ArrayList<String>();
				vertexList.add((String)v.getData());
				// Map v's scc annotation to its data value.
				verticesBySCC.put(sccNumber, vertexList);
			}
			// If the list is not null, append v to it.
			else {
				vertexList.add((String)v.getData());
				verticesBySCC.put(sccNumber, vertexList);
			}
		}
		// Iterate over verticesBySCC, sort them into decreasing order by size.
		List<List<String>> rows = new ArrayList<List<String>>();
		Set<Integer> verticesBySCCKeys = verticesBySCC.keySet();
		Iterator<Integer> j = verticesBySCCKeys.iterator();
		while (j.hasNext()) {
			int k = j.next();
			List<String> row = verticesBySCC.get(k);
			// Start at the front, checking sizes to see where 'row' belongs.
			for (int index = 0; index < rows.size(); index++) {
				if (row.size() > rows.get(index).size()) {
					rows.add(index, row);
					break;
				}
				if (index == rows.size()-1) {
					rows.add(row);
					break;
				}
			}
			if (rows.size() == 0) {
				rows.add(row);
			}
		}
		data.put(ADDITIONAL_SCC_DATA, rows);
	}

	private static void printData(Graph g) {
		NumberFormat fmt = new DecimalFormat("#.####");
		log(SEP);
		log("Graph " + data.get(FILENAME));
		log(SEP);
		log("|V| = " + g.numVertices());
		log("|A| = " + g.numArcs());
		log("Density: " + fmt.format(data.get(DENSITY)));
		log("Degree distribution: minimum  average  maximum");
		log("  inDegree            "
				+ data.get(IN_DEGREE_MIN) + "        "
				+ data.get(IN_DEGREE_AVE) + "     "
				+ data.get(IN_DEGREE_MAX));
		log("  outDegree           "
				+ data.get(OUT_DEGREE_MIN) + "        "
				+ data.get(OUT_DEGREE_AVE) + "     "
				+ data.get(OUT_DEGREE_MAX));
		log("Number of Strongly Connected Components: " + scc);
		log("Percent Vertices in Largest Strongly Connected Component: " + data.get(PERCENT_IN_LARGEST_SCC));
		
		if (data.get(ADDITIONAL_SCC_DATA) != null) {
			log("    SCC  Size  Members");
			@SuppressWarnings("unchecked")
			List<List<String>> rows = (List<List<String>>) data.get(ADDITIONAL_SCC_DATA);
			for (int i = 0; i < rows.size(); i++) {
				int num = i + 1;
				int size = rows.get(i).size();
				String members = rows.get(i).toString();
				log("    " + prettyPrint(num, 2) + "   " + prettyPrint(size, 3) + "    " + members);
			}
		}
		fmt = new DecimalFormat("#.###");
		log("Reciprocity: " + fmt.format(data.get(NUM_RECIPROCALS)));
		log("Clustering Coefficient: " + fmt.format(data.get(CLUSTERING_COEFFICIENT)));
		log(SEP2 + "\n");
		data.clear();
	}
	
	private static String prettyPrint(int number, int numSpaces) {
		String s = String.valueOf(number);
		while (s.length() < numSpaces) {
			s = " " + s;
		}
		return s;
	}

	private static void log(Object o) {
		System.out.println(String.valueOf(o));
	}
}
