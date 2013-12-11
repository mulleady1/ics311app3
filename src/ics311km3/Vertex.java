package ics311km3;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Vertex {
    private Object data;
    private List<Vertex> inAdjacentVertices;
    private List<Vertex> outAdjacentVertices;

    public Vertex() {
    	this.inAdjacentVertices = new LinkedList<Vertex>();
    	this.outAdjacentVertices = new LinkedList<Vertex>();
    }
    
    public Vertex(Object data) {
    	this.inAdjacentVertices = new LinkedList<Vertex>();
    	this.outAdjacentVertices = new LinkedList<Vertex>();
    	this.data = data; 
    }

    public int inDegree()  { return this.inAdjacentVertices.size(); }
    public int outDegree() { return this.outAdjacentVertices.size(); }
    public Object getData() { return this.data; }
    public Iterator<Vertex> inAdjacentVertices()  { return this.inAdjacentVertices.iterator(); }
    public Iterator<Vertex> outAdjacentVertices() { return this.outAdjacentVertices.iterator(); }
    public void addInAdjacentVertex(Vertex v)  { this.inAdjacentVertices.add(v); }
    public void addOutAdjacentVertex(Vertex v) { this.outAdjacentVertices.add(v); }
    public void removeInAdjacentVertex(Vertex v)  { this.inAdjacentVertices.remove(v); }
    public void removeOutAdjacentVertex(Vertex v) { this.outAdjacentVertices.remove(v); }
    public void removeData() { this.data = null; }
    public void setData(Object o) { this.data = o; }
    public String toString() {
    	return this.data == null ? "no data" : this.data.toString();
    }
}
