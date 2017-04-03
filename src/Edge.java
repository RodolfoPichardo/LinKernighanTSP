import java.util.*;

public class Edge implements Comparable<Edge> {
    private int endPoint1;
    private int endPoint2;
    
    public Edge(int a, int b) {
        this.endPoint1 = a > b? a:b;
        this.endPoint2 = a > b? b:a;
    }

    public int get1() {
        return this.endPoint1;
    }

    public int get2() {
        return this.endPoint2;
    }

    public int compareTo(Edge e2) {
        if(this.get1() < e2.get1() || this.get1() == e2.get1() && this.get2() < e2.get2()) {
            return -1;
        } else if (this.equals(e2)) {
            return 0;
        } else {
            return 1;
        }

    }

    public boolean equals(Edge e2) {
        return (this.get1() == e2.get1()) && (this.get2() == e2.get2());
    }

}
