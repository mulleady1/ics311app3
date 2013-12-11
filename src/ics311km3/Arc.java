package ics311km3;

public class Arc {
    private Object data;
    private Vertex origin;
    private Vertex destination;

    public Arc() { }
    public Arc(Object data) { this.data = data; }
    public Object getData() { return this.data; }
    public Vertex getOrigin() { return this.origin; }
    public Vertex getDestination() { return this.destination; }
    public void setOrigin(Vertex v) { this.origin = v; ; }
    public void setDestination(Vertex v) { this.destination = v; }
    public void setData(Object o) { this.data = o; }
    public void removeData() { this.data = null; }
    public String toString() {
    	return this.data == null ? "no data" : this.data.toString();
    }
}
