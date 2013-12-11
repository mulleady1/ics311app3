package ics311km3;

import static org.junit.Assert.*;

import org.junit.Test;

public class GraphGetTest {

	@Test
	public void testGetArcNoData() {
        Graph g = new Graph();
        Vertex u = new Vertex("a");
        Vertex v = new Vertex("b");
        Arc a = g.insertArc(u, v);
        assertEquals(a, g.getArc(u, v));
	}
	
	@Test
	public void testGetArcWithData() {
        Graph g = new Graph();
        Vertex u = new Vertex("a");
        Vertex v = new Vertex("b");
        String data = "asdf";
        Arc a = g.insertArc(u, v, data);
        assertEquals(a, g.getArc(u, v));
	}
	
	@Test
	public void testGetVertexNoData() {
		Graph g = new Graph();
        String key = "key";
        Vertex v = g.insertVertex(key);
        assertEquals(v, g.getVertex(key));
	}
	
	@Test
	public void testGetVertexWithData() {
        Graph g = new Graph();
        String key = "key", data = "data";
        Vertex v = g.insertVertex(key, data);
        assertEquals(v, g.getVertex(key));
	}
	
	@Test
	public void testGetArcAnnotation() {
		Graph g = new Graph();
		String key = "key", value = "value";
        Vertex v = g.insertVertex("v");
        Vertex u = g.insertVertex("u");
        Arc a = g.insertArc(u, v);
        g.setAnnotation(a, key, value);
        assertEquals(g.getAnnotation(a, key), value);
	}

	@Test
	public void testGetVertexAnnotation() {
		Graph g = new Graph();
		String key = "key", value = "value";
        Vertex v = g.insertVertex("v");
        g.setAnnotation(v, key, value);
        assertEquals(g.getAnnotation(v, key), value);
	}

}
