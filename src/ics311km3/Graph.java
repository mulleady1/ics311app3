package ics311km3;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Graph {

	// Maps a data object to a vertex object.
    private Map<Object, Vertex> vertices;
    
    // Maps a list of 2 vertices to an arc object.
    private Map<List<Vertex>, Arc> arcs;
    
    // Maps a vertex object to a map that, in turn, maps a key to a value.
    private Map<Vertex, Map<Object, Object>> vertexAnnotations;
    // Maps an arc object to a map that, in turn, maps a key to a value.
    private Map<Arc, Map<Object, Object>> arcAnnotations;

    public Graph() {
        this.vertices = new HashMap<Object, Vertex>();
        this.arcs = new HashMap<List<Vertex>, Arc>();
        this.vertexAnnotations = new HashMap<Vertex, Map<Object, Object>>();
        this.arcAnnotations = new HashMap<Arc, Map<Object, Object>>();
    }

    public int numVertices() { 
    	return this.vertices.size(); 
    }
    public int numArcs() { 
    	return this.arcs.size(); 
    }
    public Collection<Vertex> verticesCollection() {
    	return this.vertices.values();
    }
    public Iterator<Vertex> vertices() { 
    	return this.vertices.values().iterator(); 
    }
    public Iterator<Arc> arcs() { 
    	return this.arcs.values().iterator(); 
    }
    public Vertex getVertex(Object key) { 
    	return this.vertices.get(key); 
    }
    public Arc getArc(Object source, Object target) {
    	List<Vertex> vertices = new ArrayList<Vertex>(2);
    	vertices.add((Vertex)source);
    	vertices.add((Vertex)target);
    	return this.arcs.get(vertices); 
    }
    public Object getVertexData(Vertex v) { 
    	return v.getData(); 
    }
    public Object getArcData(Arc a) { 
    	return a.getData(); 
    }
    public int inDegree(Vertex v) { 
    	return v.inDegree();
    }
    public int outDegree(Vertex v) { 
    	return v.outDegree(); 
    }
    public Iterator<Vertex> inAdjacentVertices(Vertex v) { 
    	return v.inAdjacentVertices(); 
    }
    public Iterator<Vertex> outAdjacentVertices(Vertex v) { 
    	return v.outAdjacentVertices(); 
    }
    public Vertex origin(Arc a) { 
    	return a.getOrigin(); 
    }
    public Vertex destination(Arc a) { 
    	return a.getDestination(); 
    }
    public Vertex insertVertex(Object key) { 
        Vertex v = new Vertex(key);
        this.vertices.put(key, v);
        // Create vertexAnnotations inner map.
        Map<Object, Object> map = new HashMap<Object, Object>();
        this.vertexAnnotations.put(v, map);
        return v;
    }
    public Vertex insertVertex(Object key, Object data) {
        Vertex v = this.insertVertex(key);
        v.setData(data);
        return v;
    }

    public Arc insertArc(Vertex u, Vertex v) { 
        Arc a = new Arc();
        List<Vertex> vertices = new ArrayList<Vertex>(2);
    	vertices.add(u);
    	vertices.add(v);
    	this.arcs.put(vertices, a);
        // Tell arc what its origin and destination vertices are.
        a.setOrigin(u);
        a.setDestination(v);
        // Add to in/out adjacent vertices.
        u.addOutAdjacentVertex(v);
        v.addInAdjacentVertex(u);
        // Create arcAnnotations inner map.
        Map<Object, Object> map = new HashMap<Object, Object>();
        this.arcAnnotations.put(a, map);
        return a;
    }
    public Arc insertArc(Vertex u, Vertex v, Object data) { 
    	Arc a = this.insertArc(u, v);
    	a.setData(data);
        return a;
    }
    public void setVertexData(Vertex v, Object data) { 
    	v.setData(data); 
    }
    public void setArcData(Arc a, Object data) { 
    	a.setData(data); 
    }
    public Object removeVertex(Vertex v) {
    	// Iterate over the set of vertices in the graph.
    	Iterator<Vertex> vertices = this.vertices();
    	while (vertices.hasNext()) {
    		Vertex u = vertices.next();
    		// For each vertex u, check u's inAdjacentVertices for v. If found, remove v.
    		Iterator<Vertex> inAdjVertices = u.inAdjacentVertices();
    		while (inAdjVertices.hasNext()) {
    			Vertex w = inAdjVertices.next();
    			if (w.equals(v)) {
    				inAdjVertices.remove();
    			}
    		}
    		// For each vertex u, check u's outAdjacentVertices for v. If found, remove v.
    		Iterator<Vertex> outAdjVertices = u.outAdjacentVertices();
    		while (outAdjVertices.hasNext()) {
    			Vertex w = outAdjVertices.next();
    			if (w.equals(v)) {
    				outAdjVertices.remove();
    			}
    		}
    		// Found v in the set.
    		if (u.equals(v)) {
    			// Remove all arcs incident to v.
    			this.removeIncidentEdges(v);
    			// Remove v from the set of vertices in the graph.
    			vertices.remove();
    			// Remove any annotations on v.
    			this.vertexAnnotations.remove(v);
    			// Return client data.
    			return v.getData();
    		}
    	}
    	
    	// If we made it this far, v was not in the graph.
    	return null;
    }
    
    /**
     * Helper method for remove a vertex: Given a vertex u, removes all arcs coming into
     * or going out of u.
     * 
     * @param u the vertex of interest.
     */
    private void removeIncidentEdges(Vertex v) {
    	Iterator<Vertex> inAdjVertices = v.inAdjacentVertices();
    	while (inAdjVertices.hasNext()) {
    		Vertex u = inAdjVertices.next();
    		Arc a = this.getArc(u, v);
    		this.removeArc(a);
    		inAdjVertices.remove();
    		u.removeOutAdjacentVertex(v);
    	}
    	Iterator<Vertex> outAdjVertices = v.outAdjacentVertices();
    	while (outAdjVertices.hasNext()) {
    		Vertex u = outAdjVertices.next();
    		Arc a = this.getArc(v, u);
    		this.removeArc(a);
    		outAdjVertices.remove();
    		u.removeInAdjacentVertex(v);
    	}
    }
    
    public Object removeArc(Arc a) {
    	// Iterate over the set of arcs in the graph.
    	Iterator<Arc> arcs = this.arcs();
    	while (arcs.hasNext()) {
    		Arc arc = arcs.next();
    		if (a.equals(arc)) {
    			// Remove the arc of interest.
    			arcs.remove();
    			// Remove any annotations on a.
    			this.arcAnnotations.remove(a);
    			// Return client data.
    			return a.getData();
    		}
    	}
    	// If we made it this far, a was not in the graph.
    	return null; 
    }
    public void reverseDirection(Arc a) {
    	Vertex u = a.getOrigin();
    	Vertex v = a.getDestination();
    	u.removeOutAdjacentVertex(v);
    	u.addInAdjacentVertex(v);
    	v.removeInAdjacentVertex(u);
    	v.addOutAdjacentVertex(u);
    	a.setOrigin(v);
    	a.setDestination(u);
    }
    public void setAnnotation(Vertex v, Object k, Object o) {
    	Map<Object, Object> map = this.vertexAnnotations.get(v);
    	map.put(k, o);
    }
    public void setAnnotation(Arc a, Object k, Object o) {
    	Map<Object, Object> map = this.arcAnnotations.get(a);
    	map.put(k, o);
    } 
    public Object getAnnotation(Vertex v, Object k) { 
    	return this.vertexAnnotations.get(v).get(k); 
    } 
    public Object getAnnotation(Arc a, Object k) { 
    	return this.arcAnnotations.get(a).get(k); 
    } 
    public Object removeAnnotation(Vertex v, Object k) {
    	Object o = this.vertexAnnotations.get(v).get(k); 
    	this.vertexAnnotations.get(v).remove(k);
    	return o;
    }
    public Object removeAnnotation(Arc a, Object k) { 
    	Object o = this.vertexAnnotations.get(a).get(k); 
    	this.vertexAnnotations.get(a).remove(k);
    	return o;
    }
    public void clearAnnotations(Object k) {
    	// Iterate over arcAnnotations.
    	for (Arc a : this.arcAnnotations.keySet()) {
    		// In arcAnnotations, arc a has a set of keys. Check for k in the set of keys.
    		if (this.arcAnnotations.get(a).keySet().contains(k)) {
    			// If found, remove it.
    			this.arcAnnotations.get(a).remove(k);
    		}
    	}
    	// Iterate over vertexAnnotations.
    	for (Vertex v : this.vertexAnnotations.keySet()) {
    		// In vertexAnnotations, arc a has a set of keys. Check for k in the set of keys.
    		if (this.vertexAnnotations.get(v).keySet().contains(k)) {
    			// If found, remove it.
    			this.vertexAnnotations.get(v).remove(k);
    		}
    	}
    }
}
