package ics311km3;

import static org.junit.Assert.*;

import org.junit.Test;

public class GraphInsertTest {

	@Test
	public void testInsertArcNoData() {
        Graph g = new Graph();
        Arc a = g.insertArc(new Vertex("a"), new Vertex("b"));
        assertEquals("no data", a.toString());
	}
	
	@Test
	public void testInsertArcWithData() {
        Graph g = new Graph();
        String data = "asdf";
        Arc a = g.insertArc(new Vertex("a"), new Vertex("b"), data);
        assertEquals(data, a.toString());
	}
	
	@Test
	public void testInsertVertexNoData() {
		Graph g = new Graph();
        String key = "key";
        Vertex v = g.insertVertex(key);
        assertEquals("no data", v.toString());
	}
	
	@Test
	public void testInsertVertexWithData() {
        Graph g = new Graph();
        String key = "key", data = "data";
        Vertex v = g.insertVertex(key, data);
        assertEquals(data, v.toString());
	}
}
