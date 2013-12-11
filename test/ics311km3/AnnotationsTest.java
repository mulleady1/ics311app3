package ics311km3;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

public class AnnotationsTest implements Constants {

	@Test
	public void testSetAndGetAnnotation() {
		Graph g = new Graph();
        Vertex v = g.insertVertex("v");
        Vertex u = g.insertVertex("u");
        Vertex w = g.insertVertex("w");
        Vertex x = g.insertVertex("x");
		final String COLOR = "COLOR";
		final String WHITE = "WHITE";
        g.setAnnotation(v, COLOR, WHITE);
        g.setAnnotation(u, "b", "2");
        g.setAnnotation(w, "c", "3");
        g.setAnnotation(x, "d", "4");	
        assertEquals(WHITE, g.getAnnotation(v, COLOR));
	}

	@Test
	public void testMultipleAnnotations() {
		Graph g = new Graph();
        Vertex v = g.insertVertex("v");
        Vertex u = g.insertVertex("u");
        Vertex w = g.insertVertex("w");
        Vertex x = g.insertVertex("x");
		final String COLOR = "COLOR";
		final String WHITE = "WHITE";
		final String BLACK = "BLACK";
		final String GRAY  = "GRAY";
		List<Vertex> l = new ArrayList<Vertex>();
		l.add(v);
		l.add(u);
		l.add(w);
		l.add(x);
        g.setAnnotation(v, COLOR, WHITE);
        g.setAnnotation(u, COLOR, WHITE);
        g.setAnnotation(w, COLOR, BLACK);
        g.setAnnotation(x, COLOR, GRAY);	
        assertEquals(WHITE, g.getAnnotation(v, COLOR));
	}
	
	@Test
	public void testAnnotationsInGraphFromFile() {
		Graph g = Driver.loadGraph("scc_test.vna");
		Iterator<Vertex> i = g.vertices();
		while (i.hasNext()) {
			Vertex v = i.next();
			g.setAnnotation(v, COLOR, WHITE);
			g.setAnnotation(v, PARENT, NIL);
		}
		i = g.vertices();
		while (i.hasNext()) {
			Vertex v = i.next();
			assertEquals(WHITE, g.getAnnotation(v, COLOR));
		}
	}
}
