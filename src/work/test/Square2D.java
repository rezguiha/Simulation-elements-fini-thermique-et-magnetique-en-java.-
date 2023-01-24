package work.test;

import work.Geom.Edge;
import work.Geom.Point2D;
import work.Geom.Region1D;
import work.Geom.Region2D;
import work.Geom.Triangle2D;

public final class Square2D {

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
    public Square2D(String title) {
        this.title = title;

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

        // Definition of the points of the first Triangle
        Point2D[] pts = new Point2D[]{new Point2D(0, 0), new Point2D(10, 0),
            new Point2D(10, 10)};

        // Definition of the points of the second Triangle
        Point2D[] pts1 = new Point2D[]{new Point2D(0, 0), new Point2D(0, 10),
            new Point2D(10, 10)};

        // Definition of the square region
        Region2D r1 = new Region2D(0.1, 0);

        // Set of triangles of the square
        return new Triangle2D[]{new Triangle2D(pts, r1), new Triangle2D(pts1, r1)};

    }
    
    
    /**
     * 
     * @return a set of edges representing the geometry domain
     */
    public Edge[] setEdges(){
        

        // Defining the of the points of the first edge border
        Point2D[] ptsE0 = new Point2D[]{new Point2D(10, 10), new Point2D(10, 0)};

        // Defining the of the points of the second edge border
        Point2D[] ptsE1 = new Point2D[]{new Point2D(0, 0), new Point2D(0, 10)};

        borderOutput = new Region1D(20.0);
        borderInput = new Region1D(10);

        return new Edge[]{new Edge(ptsE0, borderOutput), new Edge(ptsE1, borderInput)}; 
        
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
