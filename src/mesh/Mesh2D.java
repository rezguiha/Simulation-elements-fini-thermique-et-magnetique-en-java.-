package mesh;

import java.util.ArrayList;

import work.FEM.ElementT3;
import work.Geom.Edge;
import work.Geom.Point2D;
import work.Geom.Triangle2D;

/**
 * 
 * Class for build a 2D triangular mesh
 * 
 * @author rezguiha
 * 
 */
public class Mesh2D {

	/**
	 * The set of the triangles of the problem geometry
	 */
	private Triangle2D[] triangles;

	/**
	 * The set of edges of the problem border (only borders in which the value
	 * of the variable is known are defined )
	 */
	private Edge[] edges;

	/**
	 * the set of the mesh elements
	 */
	ArrayList<Element> elements;

	/**
	 * the set of the mesh nodes.
	 */
	ArrayList<Node> nodes;

	/**
	 * Dof to be calculated
	 */
	private int NumberfreeNodes = 0;

	/**
	 * Number of successive mesh splitting
	 */
	private int nbDicho = 0;

	/**
	 * Mesh import
	 */
	private ImportMeshFlux importMesh = null;

	/**
	 * Build a 2D mesh from the geometry given by a set of triangles and edges
	 * 
	 * @param triangles
	 *            Set of the triangles describing the problem geometry
	 * @param edges
	 *            Set of the edges describing the border of the geometry (only
	 *            borders in which the value of the variable is known are
	 *            defined )
	 * @param nbDicho
	 *            recursive splitting of the mesh
	 */
	public Mesh2D(Triangle2D[] triangles, Edge[] edges, int nbDicho) {

		this.triangles = triangles;
		this.edges = edges;
		this.nbDicho = nbDicho;
		
		long t0 = System.currentTimeMillis();
		if (nbDicho != 0) {
			for (int i = 0; i < this.nbDicho; i++) {
				this.triangles = this.subdivideTriangle(this.triangles);
				this.edges = this.subdivideEdge(this.edges);
			}
		}
		// Construit le maillage
		buildMesh2D();
		// Oriente les noeuds
		orienteMesh();
		long t1 = System.currentTimeMillis();
		
		
		// Mesh informations
		System.out.println(toString());
		System.out.println(" Meshing geometry : " + (t1-t0)+" ms \n ");
	}

	/**
	 *  Import a 2D mesh from Flux mesh file
	 * @param fileName
	 *        the name of the mesh file
	 */
	public Mesh2D(String fileName) {
		this.importMesh = new ImportMeshFlux(fileName);
		this.elements = importMesh.getElementsMesh();
		this.nodes = importMesh.getNodesMesh();
		this.NumberfreeNodes = importMesh.getNumberfreeNodes();
	}

	/**
	 * 
	 * @param triangles
	 *            Set of the triangles describing the problem geometry
	 * @param edges
	 *            Set of the edges describing the border of the geometry (only
	 *            borders in which the value of the variable is known are
	 *            defined )
	 */
	public Mesh2D(Triangle2D[] triangles, Edge[] edges) {

		this(triangles, edges, 0);

	}

    public Mesh2D() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

	public String toString() {
		return " Geometry FEM mesh:\n number of elements = "
				+ this.elements.size() + " \n number of nodes = "
				+ this.nodes.size()+" \n number of free nodes = "+this.NumberfreeNodes;
	}

	/**
	 * build the 2D mesh : it initializes the nodes and elements of the mesh
	 */
	public void buildMesh2D() {

		this.buildNodesList();
		this.buildElementList();

	}

	/**
	 * returns the nodes of the mesh
	 * 
	 * @return ArrayList<Node> nodes of the mesh
	 * 
	 */
	public ArrayList<Node> getNodesMesh() {
		return this.nodes;
	}

	/**
	 * returns the elements of the mesh
	 * 
	 * @return ArrayList<Element> elements of the mesh
	 */
	public ArrayList<Element> getElementsMesh() {
		return this.elements;
	}

	/**
	 * 
	 * returns number of the free nodes : a node is free when its variable is
	 * unknown
	 * 
	 * @return int number of the free nodes
	 */
	public int getNumberfreeNodes() {
		return this.NumberfreeNodes;
	}

	/**
	 * build the nodes of the mesh
	 */
	public void buildNodesList() {

		ArrayList<Node> nodesTemp = new ArrayList<Node>();

		Node newNode;
		for (Triangle2D tr : triangles) {
			double[][] coo = tr.getCoordinate();

			for (int i = 0; i < coo.length; i++) {
				if (!containNode(nodesTemp,
						new double[] { coo[i][0], coo[i][1] })) {
					newNode = new Node();
					newNode.setCoo(new double[] { coo[i][0], coo[i][1] });
					nodesTemp.add(newNode);
				}
			}

		}

		setFreeNodes(nodesTemp);
		renumberingNodes(nodesTemp);

	}

	/**
	 * build the elements of the mesh
	 */
	public void buildElementList() {
		elements = new ArrayList<Element>();
		ElementT3 newElement;
		int count = 0;
		for (Triangle2D tr : triangles) {
			double[][] coo = tr.getCoordinate();
			ArrayList<Node> nn = getNodeOfCoordinates(this.nodes, coo);
			newElement = new ElementT3(tr.getRegion(), nn);
			newElement.setID(count);
			elements.add(newElement);
			count++;
		}

	}

	/**
	 * returns the list of nodes that correspond to a list of point
	 * coordinates.The node list is retrieved from the list of all the nodes of
	 * the mesh
	 * 
	 * @param nodeTemp
	 *            the list of the nodes of the mesh
	 * @param coo
	 *            The coordinates of the points
	 * @return ArrayList<Node> the list of nodes corresponding to the
	 *         coordinates of the points
	 */
	public ArrayList<Node> getNodeOfCoordinates(ArrayList<Node> nodeTemp,
			double[][] coo) {

		ArrayList<Node> nodeElement = new ArrayList<Node>();

		for (int i = 0; i < coo.length; i++) {
			for (int j = 0; j < nodeTemp.size(); j++) {
				if ((nodeTemp.get(j).getCoo()[0] == coo[i][0])
						&& (nodeTemp.get(j).getCoo()[1] == coo[i][1]))
					nodeElement.add(nodeTemp.get(j));

			}
		}
		return nodeElement;

	}

	/**
	 * cut into four the given triangles.
	 * 
	 * @param t0
	 *            Triangles to subdivided.
	 * @return The results of the dichotomy (4 times more triangles).
	 */
	private Triangle2D[] subdivideTriangle(Triangle2D[] t0) {
		Triangle2D[] t1 = new Triangle2D[4 * t0.length];
		for (int k = 0, n = 0; k < t0.length; k++) {
			Triangle2D[] t2 = subdivide(t0[k]);
			for (int j = 0; j < t2.length; j++) {
				t1[n++] = t2[j];
			}
		}
		return t1;
	}

	/**
	 * cut into four this triangle.
	 * 
	 * @return four sub-triangles.
	 */
	private Triangle2D[] subdivide(Triangle2D tr) {

		double xA = tr.getCoordinate()[0][0];
		double yA = tr.getCoordinate()[0][1];

		double xB = tr.getCoordinate()[1][0];
		double yB = tr.getCoordinate()[1][1];

		double xC = tr.getCoordinate()[2][0];
		double yC = tr.getCoordinate()[2][1];

		double xAB = (xA + xB) / 2;
		double yAB = (yA + yB) / 2;
		double xBC = (xB + xC) / 2;
		double yBC = (yB + yC) / 2;
		double xCA = (xC + xA) / 2;
		double yCA = (yC + yA) / 2;

		return new Triangle2D[] {
				new Triangle2D(new Point2D[] { new Point2D(xA, yA),
						new Point2D(xAB, yAB), new Point2D(xCA, yCA) },
						tr.getRegion()),
				new Triangle2D(new Point2D[] { new Point2D(xB, yB),
						new Point2D(xAB, yAB), new Point2D(xBC, yBC) },
						tr.getRegion()),
				new Triangle2D(new Point2D[] { new Point2D(xC, yC),
						new Point2D(xCA, yCA), new Point2D(xBC, yBC) },
						tr.getRegion()),
				new Triangle2D(new Point2D[] { new Point2D(xAB, yAB),
						new Point2D(xBC, yBC), new Point2D(xCA, yCA) },
						tr.getRegion()) };
	}

	/**
	 * Bisects all given edges.
	 * 
	 * @param e0
	 *            Edges to subdivided.
	 * @return The results of the dichotomy (2 times more edges).
	 */
	private Edge[] subdivideEdge(Edge[] e0) {
		Edge[] e1 = new Edge[2 * e0.length];
		for (int k = 0, n = 0; k < e0.length; k++) {
			Edge[] e2 = subdivide(e0[k]);
			for (int j = 0; j < e2.length; j++) {
				e1[n++] = e2[j];
			}
		}
		return e1;
	}

	/**
	 * Cut into 2 this edge
	 * 
	 * @return two sub-Edge.
	 */
	private Edge[] subdivide(Edge e) {

		double xA = e.getCoordinate()[0][0];
		double yA = e.getCoordinate()[0][1];

		double xB = e.getCoordinate()[1][0];
		double yB = e.getCoordinate()[1][1];

		double xM = (e.getCoordinate()[0][0] + e.getCoordinate()[1][0]) / 2;
		double yM = (e.getCoordinate()[0][1] + e.getCoordinate()[1][1]) / 2;

		return new Edge[] {
				new Edge(new Point2D[] { new Point2D(xA, yA),
						new Point2D(xM, yM) }, e.getRegion()),
				new Edge(new Point2D[] { new Point2D(xM, yM),
						new Point2D(xB, yB) }, e.getRegion()) };
	}

	/**
	 * This method seeks if the coordinates of a node corresponding to an
	 * existing node in a list
	 * 
	 * @param nodesTemp
	 *            The node list
	 * @param coo
	 *            The coordinates of the node
	 * @return boolean true/false
	 */
	private boolean containNode(ArrayList<Node> nodesTemp, double[] coo) {

		boolean val = false;

		if (nodesTemp.size() == 0)
			val = false;
		else {
			for (Node node : nodesTemp) {
				if ((node.getCoo()[0] == coo[0])
						&& (node.getCoo()[1] == coo[1])) {
					val = true;
					return val;
				}

				else
					val = false;

			}

		}

		return val;

	}

	/**
	 * update the status of a node belonging to a list of nodes (free or not)
	 * 
	 * @param nodesTemp
	 *            The node list
	 */
	private void setFreeNodes(ArrayList<Node> nodesTemp) {

		ArrayList<Node> nodeEdge;
		for (Edge edge : edges) {
			nodeEdge = getNodeOfCoordinates(nodesTemp, edge.getCoordinate());

			for (Node node : nodeEdge) {
				node.setValue(edge.getRegion().getDirichletValue());
				node.setFree(false);

			}

		}

	}

	/**
	 * This method renumbers the nodes by classifying constraint nodes down the
	 * list
	 * 
	 * @param nodesT
	 *            The list to be renumbered
	 */
	private void renumberingNodes(ArrayList<Node> nodesT) {

		this.nodes = new ArrayList<Node>();
		ArrayList<Node> nodesTempn = new ArrayList<Node>();

		Node node;

		for (int i = 0; i < nodesT.size(); i++) {
			node = nodesT.get(i);
			if (node.getFree()) {
				node.setID(NumberfreeNodes);
				nodes.add(node);
				NumberfreeNodes++;
			} else {
				nodesTempn.add(node);

			}
		}

		nodes.addAll(nodesTempn);

		for (int i = NumberfreeNodes; i < nodes.size(); i++)
			this.nodes.get(i).setID(i);
	}

	/**
	 * 
	 * Oriente les elements
	 */

	public void orienteMesh() {

		for (Element e : elements) {
			ArrayList<Node> nodes = e.getNodes();

			double delta = (nodes.get(1).getCoo()[0] - nodes.get(0).getCoo()[0])
					* (nodes.get(2).getCoo()[1] - nodes.get(0).getCoo()[1])
					- (nodes.get(2).getCoo()[0] - nodes.get(0).getCoo()[0])
					* (nodes.get(1).getCoo()[1] - nodes.get(0).getCoo()[1]);

			// Orientation necessaire

			if (delta < 0.0) {

				ArrayList<Node> nTemp = new ArrayList<Node>(3);
				for (int i = 2; i >= 0; i--) {// permutation cyclique inversee
					nTemp.add(e.getNodes().get(i));
				}
				e.setNodes(nTemp);

			}
		}

	}

	public Edge[] getEdges() {

		return this.edges;
	}

	public ImportMeshFlux getImportMesh() {
		return importMesh;
	}

}
