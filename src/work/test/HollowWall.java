package work.test;

import work.Geom.Edge;
import work.Geom.Point2D;
import work.Geom.Region1D;
import work.Geom.Region2D;
import work.Geom.Triangle2D;

public final class HollowWall {

    String title;

    private Triangle2D[] triangles;

    private Edge[] edges;

    Region1D borderOutput;

    Region1D borderInput;

    /**
     * ********************** Test case : Square *******************************
     *
     * To build the geometry, it must be decomposed into triangles (Triangle2D).
     * A Triangle2D is defined by 3 Point2D and a surface region (Region2D) to
     * which it belongs.
     *
     * For the borders where the variable is known, it must define an Edge An
     * Edge is defined by 2 Point2D and a linear region (Region1D) to which it
     * belongs.
     *
     *
     *
     * @param title the geometry title
     * @author rezguiha
     *
     */
    double a, b, c, d;
    double T0,T1;
    double k;
    
    public HollowWall(String title, double a, double b, double c, double d, double T0, double T1, double k) {
        this.title = title;
        this.a = a;
        this.b = b;        
        this.c = c;        
        this.d = d;
        this.k = k;
        this.T0 = T0;
        this.T1 = T1;
        // Set the geometry domain
        this.triangles=setDomain();
        
        // Set the geometry border
        this.edges=setEdges();
        
        
    }

    /**
     * 
     * @return a set of triangles representing the geometry domain
     */
    public Triangle2D[] setDomain() {

        Point2D[] pts = new Point2D[]{new Point2D(0, 0), new Point2D(c, b-d), new Point2D(0, b-d)};
        
        Point2D[] pts1 = new Point2D[]{new Point2D(c,b-d), new Point2D(a, 0), new Point2D(a,b)};
        
        Point2D[] pts2 = new Point2D[]{new Point2D(0, 0), new Point2D(c, b-d), new Point2D(a,0)};
        
        Point2D[] pts3 = new Point2D[]{new Point2D(c,b-d), new Point2D(c,b), new Point2D(a,b)};
        
        Region2D region = new Region2D(k,0);
        
        return new Triangle2D[]{new Triangle2D(pts, region), new Triangle2D(pts1, region), new Triangle2D(pts2, region), new Triangle2D(pts3, region)};

    }
    
    
    /**
     * 
     * @return a set of edges representing the geometry domain
     */
    public Edge[] setEdges(){
        
        Point2D[] ptsE0 = new Point2D[]{new Point2D(a,b), new Point2D(a, 0)};


        Point2D[] ptsE1 = new Point2D[]{new Point2D(0, b-d), new Point2D(0, 0)};

        borderOutput = new Region1D(T1);
        borderInput = new Region1D((T0+T1)/2);

        return new Edge[]{new Edge(ptsE1, borderOutput), new Edge(ptsE0, borderInput)}; 
        
    }

    /**
     * return the title of the problem
     *
     * @return String title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Returns the set of triangles
     *
     * @return Triangle2D[]
     */
    public Triangle2D[] getTriangles() {
        return this.triangles;
    }

    /**
     * Returns the set of edges
     *
     * @return Edge[]
     */
    public Edge[] getEdges() {
        return this.edges;
    }

    /**
     * Retruns the region of output border
     *
     * @return Region1D
     */
    public Region1D getOutputRegion1D() {
        return borderOutput;

    }

    /**
     * Retruns the region of input border
     *
     * @return Region1D
     */
    public Region1D getInputRegion1D() {
        return borderInput;

    }

}
