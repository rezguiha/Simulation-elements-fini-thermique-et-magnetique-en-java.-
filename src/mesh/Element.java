package mesh;

import java.util.ArrayList;
import work.Geom.Region2D;

/**
 *
 * A class representing a triangular finite element
 *
 * @author rezguiha
 *
 */
public abstract class Element {

    /**
     * Nodes of the element
     */
    protected ArrayList<Node> nodes = new ArrayList<Node>();

    /**
     * Source term
     */
    protected double source;

    /**
     * The conductivity
     */
    protected double conductivity;

    /**
     * Region of the element
     */
    protected Region2D region;

    /**
     * The ID of this element.
     *
     */
    private int ID = 0;

    /**
     * Build a new Element
     *
     * @param region the region of the element
     * @param nodes nodes of the element
     */
    public Element(Region2D region, ArrayList<Node> nodes) {
        this.region = region;
        this.nodes = nodes;

        setSource(region.getSource());
        setConductivity(region.getConductivity());
    }

    /**
     * returns the surface region of the element
     *
     * @return Region2D Surface region of the element
     */
    public Region2D getRegion() {
        return this.region;
    }

    /**
     * sets the term source of the element
     *
     * @param val the value of the source
     */
    public void setSource(double val) {
        this.source = val;
    }

    /**
     * returns the source value in the element
     *
     * @return double the value of the source
     */
    public double getSource() {
        return this.source;
    }

    /**
     * sets the conductivity in the element
     *
     * @param val the value of the conductivity
     */
    public void setConductivity(double val) {
        this.conductivity = val;
    }

    /**
     * returns the conductivity in the element
     *
     * @return the value of the conductivity
     */
    public double getConductivity() {
        return this.conductivity;
    }

    /**
     * sets the ID of the element
     *
     * @param newElementID the new ID of the element
     */
    public void setID(int newElementID) {
        this.ID = newElementID;
    }

    /**
     * returns ID of the element
     *
     * @return int the ID of the element
     */
    public int getID() {
        return this.ID;
    }

    /**
     * Sets the nodes of an element
     *
     * @param newNodes A list of the nodes
     */
    public void setNodes(ArrayList<Node> newNodes) {
        if (this.nodes.size() != newNodes.size()) {
            throw new ArrayIndexOutOfBoundsException("Wrong number of nodes ("
                    + newNodes.size() + ")");
        }

        this.nodes = new ArrayList<Node>(newNodes);
    }

    /**
     * returns the nodes of the elements
     *
     * @return ArrayList<Node> The nodes list
     */
    public ArrayList<Node> getNodes() {
        return this.nodes;
    }

    /**
     * Returns a string representation of the element.
     *
     * @return A string representation of the element.
     */
    public String toString() {
        String s = "\nElement n° " + this.ID + " with conductivity= "
                + this.region.getConductivity() + " The source = "
                + this.region.getSource() + " \n";

        for (Node node : this.nodes) {
            s += node.toString() + " \n";
        }

        return s;

    }

    /**
     * Return the incenter of the triangle
     *
     * @return the incenter of the triangle (always inside the triangle)
     */
    public double[] getXYCenter() {

        double[] coo1 = nodes.get(0).getCoo();
        double[] coo2 = nodes.get(1).getCoo();
        double[] coo3 = nodes.get(2).getCoo();

        double l1 = Math.sqrt((coo2[0] - coo1[0]) * (coo2[0] - coo1[0])
                + (coo2[1] - coo1[1]) * (coo2[1] - coo1[1]));
        double l2 = Math.sqrt((coo3[0] - coo2[0]) * (coo3[0] - coo2[0])
                + (coo3[1] - coo2[1]) * (coo3[1] - coo2[1]));
        double l3 = Math.sqrt((coo1[0] - coo3[0]) * (coo1[0] - coo3[0])
                + (coo1[1] - coo3[1]) * (coo1[1] - coo3[1]));

        return new double[]{
            (l2 * coo1[0] + l3 * coo2[0] + l1 * coo3[0]) / (l1 + l2 + l3),
            (l2 * coo1[1] + l3 * coo2[1] + l1 * coo3[1]) / (l1 + l2 + l3)};
    }

    /**
     * Interpolate the value
     *
     * @param xy the point in real coordinates
     * @return the interpolation value
     */
    public double computeVariable(double[] xy) {

        double value = 0;
        if (isInsideElement(xy)) {
            double[] alpha = getPhi(xy);
            ArrayList<Node> nodes = getNodes();
            for (int i = 0; i < nodes.size(); i++) {
                value += nodes.get(i).getValue() * alpha[i];
            }
        }
        return value;

    }

    /**
     * Interpolate Curl of the variable at a point
     *
     * @param xy the coordinates of the point in which the Gradient is computed
     * @return CurlV at the interpolation point
     */
    public double[] computeGradVariable(double[] xy) {
        double[] gradV = new double[2];
        double[][] gradPhi = getGradPhi(xy);

        if (isInsideElement(xy)) {
            for (int i = 0; i < nodes.size(); i++) {
                gradV[0] += gradPhi[1][i] * nodes.get(i).getValue();
                gradV[1] -= gradPhi[0][i] * nodes.get(i).getValue();
            }
            return gradV;
        } else {
            return new double[]{0.0, 0.0};
        }

    }

    /**
     *
     * @param xy The coordinates of the point in which
     * @return The element in which the point belongs
     */
    public boolean isInsideElement(double[] xy) {
        double[] A = {this.getNodes().get(0).getCoo()[0], this.getNodes().get(0).getCoo()[1]};
        double[] B = {this.getNodes().get(1).getCoo()[0], this.getNodes().get(1).getCoo()[1]};
        double[] C = {this.getNodes().get(2).getCoo()[0], this.getNodes().get(2).getCoo()[1]};

        double[] v0 = {A[0] - C[0], A[1] - C[1]};
        double[] v1 = {A[0] - B[0], A[1] - B[1]};
        double[] v2 = {A[0] - xy[0], A[1] - xy[1]};

        double dot00 = v0[0] * v0[0] + v0[1] * v0[1];
        double dot01 = v0[0] * v1[0] + v0[1] * v1[1];
        double dot02 = v0[0] * v2[0] + v0[1] * v2[1];
        double dot12 = v1[0] * v2[0] + v1[1] * v2[1];
        double dot11 = v1[0] * v1[0] + v1[1] * v1[1];

        double invDenom = 1.0 / (dot00 * dot11 - dot01 * dot01);

        double u = (dot11 * dot02 - dot01 * dot12) * invDenom;
        double v = (dot00 * dot12 - dot01 * dot02) * invDenom;
        boolean var = ((u >= 0) && (v >= 0) && (u + v) < 1);
        return ((u >= 0) && (v >= 0) && (u + v) <= 1);
    }

    /**
     * Compute the area of the element
     *
     * @return the area of the element
     */
    public abstract double getArea();

    /**
     *
     * @param xy
     * @return
     */
    public abstract double[] getPhi(double[] xy);

    /**
     *
     * @param xy
     * @return
     */
    public abstract double[][] getGradPhi(double[] xy);

    /**
     *
     * @return Matrice elements fins elementaire
     *
     */
    public abstract double[][] integrateMatriceElementaire();

    /**
     *
     * @return vecteur elemsnt finis elementaire
     *
     */
    public abstract double[] integrateVecteurElementaire();

}
