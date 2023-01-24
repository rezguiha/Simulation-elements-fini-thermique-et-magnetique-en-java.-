package mesh;

import java.util.ArrayList;

/**
 *
 * A class for the managing of the nodes
 *
 * @author rezguiha
 *
 */
public class Node {

    /**
     * Number of coordinates
     *
     */
    final static protected int nbrCoords = 2;

    /**
     * Coordinates of the point
     */
    protected double[] coo;

    /**
     * Value of the node
     */
    private double value = 0;

    /**
     * True if the node is free (No dirichlet condition on it)
     */
    private boolean free = true;

    /**
     * The ID of this node.
     */
    private int ID;

    /**
     * Build a node
     */
    public Node() {

    }

    /**
     * Return the coordinates of the node
     *
     * @return the coordinates of the node
     */
    public double[] getCoo() {
        double[] xy = new double[nbrCoords];
        for (int j = 0; j < nbrCoords; j++) {
            xy[j] = coo[j];
        }
        return xy;
    }

    /**
     * Set the coordinates of the node
     *
     * @param the coordinates of a point
     */
    public void setCoo(double[] xy) {
        this.coo = new double[nbrCoords];
        for (int k = 0; k < nbrCoords; k++) {
            this.coo[k] = xy[k];
        }
    }

    /**
     * Return the value of the variable of the node
     *
     * @return the value of the variable of the node
     */
    public double getValue() {
        return value;
    }

    /**
     * set the state of node as free or not (true or false)
     *
     * @param free the state of the node (true or false)
     */
    public void setFree(boolean free) {
        this.free = free;
    }

    /**
     * returns the state of the node
     *
     * @return boolean true/false : the state of the node
     */
    public boolean getFree() {
        return this.free;
    }

    /**
     * Return the value of the variable of the node
     *
     * @return the value of the variable of the node
     */
    public void setValue(double value) {
        this.value = value;
    }

    /**
     * set the ID of the node
     *
     * @param newID the new ID of the node
     */
    public void setID(int newID) {
        this.ID = newID;
    }

    /**
     * returns the ID of the element
     *
     * @return int the ID of the element
     */
    public int getID() {
        return this.ID;
    }

    /**
     * Returns a string representation of the node.
     *
     * @return A string representation of the node.
     */
    public String toString() {

        return " Node " + ID + " : x= " + this.coo[0] + ", y = " + this.coo[1]
                + " is " + this.free + ", value = " + this.value;
    }

    /**
     * Returns list of elements which the node is belongs
     * 
     * @param mesh the geometry mesh of the problem
     * @param nj   the node j
     * @return ArrayList<Element>  : the list of elements belonging to the two nodes, this and the node nj
     */
    ArrayList<Element> getElementsListOfNode(Mesh2D mesh, Node nj) {

        boolean valI = false;
        boolean valJ = false;
        ArrayList<Element> el = new ArrayList<Element>();

        for (int i = 0; i < mesh.getElementsMesh().size(); i++) {
            ArrayList<Node> nodes = mesh.getElementsMesh().get(i).getNodes();
            for (int j = 0; j < nodes.size(); j++) {

                if (nodes.get(j).getID() == this.getID()) {
                    valI = true;
                }

            }

            if (valI == true && valJ == true) {
                for (int j = 0; j < nodes.size(); j++) {

                    if (nodes.get(j).getID() == nj.getID()) {
                        el.add(mesh.getElementsMesh().get(i));
                    }

                }
            }
        }
        return el;

    }
}
