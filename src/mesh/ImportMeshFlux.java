package mesh;

import java.util.ArrayList;
import java.util.List;

//* WARNING - Potential conflict with Lecture in outils package

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Vector;

import work.FEM.ElementT3;
import work.Geom.Region1D;
import work.Geom.Region2D;

/**
 * A classe to import mesh from FLUX sofware The file format is *.DEC In FLUX
 * sofware use FluxExpert mesh export format All regions of the file are
 * imported
 * 
 * @author rezguiha
 */
public class ImportMeshFlux {


	/**
	 * Element table
	 */
	private ArrayList<Element> elementsTemp;

	/**
	 * Element table
	 */
	private ArrayList<Element> elements;

	/**
	 * Node table
	 */
	private ArrayList<Node> nodesTab;

	/**
	 * Node table
	 */
	private ArrayList<Node> nodes;

	/**
	 * Fichier a parser
	 */
	private String file_path;

	/**
	 * Store the number of nodes in this mesh.
	 */
	private int nb_nd;

	ArrayList<Region2D> region2D;
	ArrayList<Region1D> region1D;

	/**
	 * Dof to be calculated
	 */
	private int NumberfreeNodes = 0;

	/**
	 * Creates a new instance of ImportFluxMeshRegion The file format is *.DEC
	 * In FLUX sofware use FluxExpert mesh export format
	 * 
	 * @param file_path
	 *            File path of the *.DEC
	 */
	public ImportMeshFlux(String file_path) {
		this.file_path = file_path;
		this.elementsTemp = new ArrayList<Element>();

		makeAll();
	}

	/**
	 * Get the number of nodes on this mesh.
	 * 
	 * @return number of nodes.
	 */
	public int getNbNodes() {
		return nb_nd;
	}

	/**
	 * A modifier pour accepter le PF3 Construction de l'ensemble du probleme
	 */
	private void makeAll() {
		// ArrayList[] elementsTable = null;
		List<Element>[] elementsTable = null;
		List<Integer> regionsTable = new ArrayList<Integer>();

		region2D = new ArrayList<Region2D>();
		region1D = new ArrayList<Region1D>();

		String temp = " ";
		System.out.println("*******************************");
		System.out.println("***  Import du fichier Flux ***");
		System.out.println("*******************************");
		try {
			// File opening
			LectureFlux file_dec = new LectureFlux(file_path);

			// jump of 1 line (Fichier cree par F3DXPE 1.0)
			temp = file_dec.lireLigne();

			// get mesh dimension
			int pbDimension = file_dec.lireInt(0, 12);

			// Element number
			int nb_elt = file_dec.lireInt(0, 12);

			// Volume Element number
			int nb_elt_vol = file_dec.lireInt(0, 12);

			// Surface Element number
			int nb_elt_surf = file_dec.lireInt(0, 12);

			// Line Element number
			int nb_elt_line = file_dec.lireInt(0, 12);

			// Punctual Element number
			int nb_elt_punc = file_dec.lireInt(0, 12);

			// jump "NOMBRE DE MACRO-ELEMENTS"
			temp = file_dec.lireLigne();

			// Nodes number
			nb_nd = file_dec.lireInt(0, 12);

			// Regions number
			int nb_reg = file_dec.lireInt(0, 12);
			elementsTable = new ArrayList[nb_reg];
			for (int i = 0; i < nb_reg; i++) {
				elementsTable[i] = new ArrayList();
			}

			// Volume region Number
			int nb_reg_vol = file_dec.lireInt(0, 12);

			// Surface region Number
			int nb_reg_surf = file_dec.lireInt(0, 12);

			// Line region Number
			int nb_reg_line = file_dec.lireInt(0, 12);

			// Punctual region Number
			int nb_reg_punc = file_dec.lireInt(0, 12);

			// jump of 3 lines
			// (NOMBRE DE REGIONS MACRO-ELEMENTAIRES)
			// (NOMBRE DE NOEUDS DANS 1 ELEMENT (MAX))
			// NOMBRE DE POINTS D'INTEGRATION / ELEMENT (MAX))
			for (int i = 0; i < 3; i++) {
				temp = file_dec.lireLigne();
			}

			// Jump of 1 line (NOMS DES REGIONS)
			temp = file_dec.lireLigne();

			// region numbering
			int regCount = 0;
			// Name of volume regions
			if (nb_reg_vol > 0) {
				temp = file_dec.lireLigne();
				for (int i = 0; i < nb_reg_vol; i++) {
					temp = file_dec.lireLigne().trim();
					regionsTable.add(regCount);

					regCount++;
				}
			}

			// Name of surface regions
			if (nb_reg_surf > 0) {
				temp = file_dec.lireLigne();
				for (int i = 0; i < nb_reg_surf; i++) {
					temp = file_dec.lireLigne().trim();
					regionsTable.add(regCount);
					String[] val = temp.split("_");
					if (val.length == 3)
						region2D.add(new Region2D(Double.parseDouble(val[1]),
								Double.parseDouble(val[2])));
					else
						region2D.add(new Region2D(Double.parseDouble(val[1]),
								0.0));
					region2D.get(i).setName(val[0]);
					regCount++;
				}
			}

			// Name of line regions
			if (nb_reg_line > 0) {
				temp = file_dec.lireLigne();
				for (int i = 0; i < nb_reg_line; i++) {
					temp = file_dec.lireLigne().trim();
					regionsTable.add(regCount);
					String[] val = temp.split("_");
					region1D.add(new Region1D(Double.parseDouble(val[1])));
					region1D.get(i).setName(val[0]);
					regCount++;
				}
			}

			// Name of punctual regions
			if (nb_reg_punc > 0) {
				temp = file_dec.lireLigne();
				for (int i = 0; i < nb_reg_punc; i++) {
					temp = file_dec.lireLigne().trim();
					regionsTable.add(regCount);
					regCount++;
				}
			}

			// Jump regions characteristics
			for (int i = 0; i < nb_reg; i++) {
				temp = file_dec.lireLigne();
			}

			// Jump 1 line (DESCRIPTEUR DE TOPOLOGIE DES ELEMENTS)
			temp = file_dec.lireLigne();

			// Jump elements description (go directly to nodes description)
			temp = file_dec.sauteetLireLigne(2 * nb_elt);

			// Jump 1 line (COORDONNEES DES NOEUDS)
			temp = file_dec.lireLigne();

			// Nodes table construction
			// Node[] nodesTable = new Node[nb_nd];
			Node newNode;
			nodesTab = new ArrayList<Node>();
			for (int i = 0; i < nb_nd; i++) {
				//
				double[] coord = file_dec.lireCooNode();

				newNode = new Node();
				newNode.setCoo(coord);
				newNode.setFree(true);
				newNode.setID(i);
				nodesTab.add(newNode);

			}

			// Go back to elements description
			// Jump 1 line (COORDONNEES DES NOEUDS) (forward)
			temp = file_dec.sauteetLireLigne(-1);

			// Jump node description (forward)
			temp = file_dec.sauteetLireLigne(-1 * nb_nd);

			// Jump elements description (forward)
			temp = file_dec.sauteetLireLigne(-2 * nb_elt);

			// elements loop
			for (int i = 0; i < nb_elt_surf; i++) {
				// Current element information
				// int dscp[] = file_dec.lireLigneIntVecteur(6, 6);
				int dscp[] = file_dec.lireDonneesElement();

				// Element iD
				int iD = dscp[0] - 1;

				// Element type
				int type = dscp[2];

				// Region number
				int n_r = dscp[3] - 1;

				// Element dimension
				int elem_dim = dscp[4];

				// Node number in the element
				int nbNd = dscp[7];

				// Node table
				// int[] nodesnumber = file_dec.lireLigneIntVecteur(6, 6);
				int[] nodesnumber = file_dec.lireDonneesElement();
				Node[] nodetemp = new Node[nbNd];
				for (int j = 0; j < nbNd; j++) {
					nodetemp[j] = nodesTab.get(nodesnumber[j] - 1);
				}

				// Element newElement;

				ArrayList<Node> noeudsOfElement = new ArrayList<Node>(3);
				noeudsOfElement.add(nodesTab.get(nodesnumber[0] - 1));
				noeudsOfElement.add(nodesTab.get(nodesnumber[1] - 1));
				noeudsOfElement.add(nodesTab.get(nodesnumber[2] - 1));
				ElementT3 newElement = new ElementT3(region2D.get(n_r),
						noeudsOfElement);
				newElement.setID(iD);
				elementsTemp.add(newElement);

			}

			// elements loop
			for (int i = 0; i < nb_elt_line; i++) {
				// Current element information
				// int dscp[] = file_dec.lireLigneIntVecteur(6, 6);
				int dscp[] = file_dec.lireDonneesElement();

				// Element iD
				int iD = dscp[0] - 1;

				// Element type
				int type = dscp[2];

				// Region number
				int n_r = dscp[3] - 1 - region2D.size();

				// Element dimension
				int elem_dim = dscp[4];

				// Node number in the element
				int nbNd = dscp[7];

				// Node table
				// int[] nodesnumber = file_dec.lireLigneIntVecteur(6, 6);
				int[] nodesnumber = file_dec.lireDonneesElement();

				for (int j = 0; j < nbNd; j++) {
					nodesTab.get(nodesnumber[j] - 1).setFree(false);
					//

					nodesTab.get(nodesnumber[j] - 1).setValue(
							region1D.get(n_r).getDirichletValue());

				}

			}
			System.out.println("*******************************");
			System.out.println("***       Fin Import        ***");
			System.out.println("*******************************");

		} catch (Exception exc) {
			exc.printStackTrace();
			// trop violent?
			throw new RuntimeException(exc);
		}

		renumberingNodes(nodesTab);
		buildElementList();
		
		System.out.println(" Geometry FEM mesh:\n number of elements = "
		+ this.elements.size() + " \n number of nodes = "
		+ this.nodes.size()+" \n number of free nodes = "+this.NumberfreeNodes);
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

	public ArrayList<Region1D> getRegion1D() {
		return this.region1D;

	}

	public ArrayList<Region2D> getRegion2D() {
		return this.region2D;

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
			node = new Node();
			node.setCoo(nodesT.get(i).getCoo());

			if (nodesT.get(i).getFree()) {
				node.setFree(true);
				node.setID(NumberfreeNodes);
				nodes.add(node);
				NumberfreeNodes++;
			} else {
				node.setFree(false);
				node.setValue(nodesT.get(i).getValue());
				nodesTempn.add(node);

			}
		}

		nodes.addAll(nodesTempn);

		for (int i = NumberfreeNodes; i < nodes.size(); i++)
			nodes.get(i).setID(i);

	}

	/**
	 * build the elements of the mesh
	 */
	public void buildElementList() {
		elements = new ArrayList<Element>();
		ElementT3 newElement;
		int count = 0;
		for (Element tr : elementsTemp) {
			double[][] coo = new double[tr.getNodes().size()][2];
			for (int j = 0; j < tr.getNodes().size(); j++) {
				coo[j][0] = tr.getNodes().get(j).getCoo()[0];
				coo[j][1] = tr.getNodes().get(j).getCoo()[1];
			}

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
	 * 
	 * returns number of the free nodes : a node is free when its variable is
	 * unknown
	 * 
	 * @return int number of the free nodes
	 */
	public int getNumberfreeNodes() {
		return this.NumberfreeNodes;
	}

	// public static void main (String[] args){
	// ImportMeshFlux imp=new ImportMeshFlux("MESH2D.DEC");
	//
	//
	//
	// for(int i=0;i<imp.nodes.size();i++)
	// System.out.println(imp.nodes.get(i).toString());
	//
	// //
	// for(int i=0;i<imp.region2D.size();i++)
	// System.out.println(" region "+imp.region2D.get(i).getName()+" "+imp.region2D.get(i).getConductivity()+" "+imp.region2D.get(i).getSource());
	// //
	// for(int i=0;i<imp.region1D.size();i++)
	// System.out.println(" region "+imp.region1D.get(i).getName()+" "+imp.region1D.get(i).getDirichletValue());
	// }

}




/**
 * Permet de lire un fichier en code ASCII
 * 
 * @author JMG
 * @date 23/08/02 Historique: 06/05/99: Creation 23/08/02: Refonte totale de
 *       l'ancienne classe Lecture cr�e le 06/05/99 (mise en place d'un
 *       tampon)
 */

/**
 * Impror des fichier flux (*.DEC)
 * 
 * @author chadebec
 */
class LectureFlux {

	// Nombre de ligne contenu dans le fichier
	private int nbLigne;

	// Memoire tampon
	private Vector tampon;

	// Num�ro de ligne en cours
	private int ligneEnCours;

	/**
	 * Constructeur
	 * 
	 * @param acces
	 *            Chemin d'acces et nom du fichier � lire
	 * @throws IOException
	 */
	public LectureFlux(String acces) throws IOException {
		LineNumberReader reader = new LineNumberReader(new FileReader(new File(
				acces)));
		getFile(reader);
	}

	/**
	 * Constructeur
	 * 
	 * @param chemin
	 *            Chemin d'acces au fichier
	 * @param fichier
	 *            Nom du fichieer a lire
	 * @throws IOException
	 */
	public LectureFlux(String chemin, String fichier) throws IOException {
		LineNumberReader reader = new LineNumberReader(new FileReader(new File(
				chemin, fichier)));
		getFile(reader);
	}

	/**
	 * Fermeture du fichier. Existe seulement pour des raisons de compatibilite
	 * date 07/05/99
	 */
	public void close() {
	}

	/**
	 * Lit une ligne correpondant aux coordonnees des noeuds Ajout JMG pour
	 * compatibilite PF3
	 * 
	 * @return Coordonnees x,y,z du noeud
	 * @throws NumberFormatException
	 */
	public double[] lireCooNode() throws NumberFormatException {
		int ind, indDeb;
		String chaine = lireLigne();
		if (chaine == null)
			return null;

		// String temp[] = chaine.split(" +");
		// return new
		// double[]{Double.parseDouble(temp[2]),Double.parseDouble(temp[3]),Double.parseDouble(temp[4])};

		char t[] = chaine.toCharArray();
		double coo[] = new double[3];
		// Recuperation du nombre de valeurs
		int nbVal = nbVal(t);
		if (nbVal != 4)
			throw new RuntimeException(
					"Nombre de valeur incoherent pour un noeud (4 valeurs attendues:num: num, x, y, z");
		// Recuperation des nombres
		nbVal = 0;
		for (ind = 0; ind < t.length; ind++) {
			if (t[ind] != ' ') {
				indDeb = ind;
				nbVal++;
				// Recherche de la fin du nombre
				for (; ind < t.length; ind++)
					if (t[ind] == ' ' || ind == t.length - 1) {
						if (nbVal != 1) {
							String val;
							if (ind != t.length - 1)
								val = String.valueOf(t, indDeb, ind - indDeb);
							else
								val = String.valueOf(t, indDeb, ind - indDeb
										+ 1);
							coo[nbVal - 2] = Double.parseDouble(val);
						}
						break;
					}
			}
		}
		return coo;
	}

	/**
	 * Retourne le nombre de valeurs presente dans le tableau de char sachant
	 * que les separateur sont des espaces dont on ne connait pas le nombre
	 * Ajout JMG pour compatibilite PF3
	 * 
	 * @param t
	 *            Table a analyser
	 * @return Nombre de termes
	 */
	private int nbVal(char t[]) {
		int nbVal = 0;
		for (int ind = 0; ind < t.length; ind++) {
			if (t[ind] != ' ') {
				nbVal++;
				// Recherche de la fin du nombre
				for (; ind < t.length; ind++)
					if (t[ind] == ' ')
						break;
			}
		}
		return nbVal;
	}

	/**
	 * Recupere toutes les informations concernant un element Ajout JMG pour
	 * compatibilite PF3
	 * 
	 * @return Information decrivant l'element
	 * @throws NumberFormatException
	 */
	public int[] lireDonneesElement() throws NumberFormatException {
		int indDeb;
		String chaine = lireLigne();
		if (chaine == null)
			return null;

		// String temp[] = chaine.split(" +");
		// int data[] = new int[temp.length-1];
		// for(int i=1;i<temp.length;i++)
		// data[i-1] = Integer.parseInt(temp[i]);
		// return data;

		char t[] = chaine.toCharArray();
		// Recuperation du nombre de valeurs
		int data[] = new int[nbVal(t)];
		// Recuperation des nombres
		for (int ind = 0, nbVal = 0; ind < t.length; ind++) {
			if (t[ind] != ' ') {
				indDeb = ind;
				nbVal++;
				// Recherche de la fin du nombre
				for (; ind < t.length; ind++)
					if (t[ind] == ' ' || ind == t.length - 1) {
						String val;
						if (ind != t.length - 1)
							val = String.valueOf(t, indDeb,
									ind - indDeb == 0 ? 1 : ind - indDeb);
						else
							val = String.valueOf(t, indDeb,
									ind - indDeb == 0 ? 1 : ind - indDeb + 1);
						data[nbVal - 1] = Integer.parseInt(val);
						break;
					}
			}
		}
		return data;
	}

	/**
	 * Lit une ligne du fichier et la retourne sous forme de String date
	 * 23/08/02
	 * 
	 * @return Ligne lue
	 */
	public String lireLigne() {
		if (ligneEnCours >= nbLigne)
			return null;
		ligneEnCours++;
		return ((String) tampon.get(ligneEnCours - 1));
	}

	/**
	 * Saute des lignes et lit
	 * 
	 * @param n
	 *            nombre de ligne a sauter
	 * @return chaine
	 */
	public String sauteetLireLigne(int n) {
		if (ligneEnCours + n >= nbLigne)
			return null;
		ligneEnCours = ligneEnCours + n;
		return ((String) tampon.get(ligneEnCours - 1));
	}

	/**
	 * Lit une ligne du fichier et la retourne sous forme de double (doit etre
	 * un nombre)
	 * 
	 * @return Double lu, retourne NaN dans le cas ou le fichier est termine
	 * @throws NumberFormatException
	 *             date 23/08/02
	 */
	public double lireLigneDouble() throws NumberFormatException {
		String temp = lireLigne();
		if (temp == null)
			return Double.NaN;
		return Double.parseDouble(temp);
	}

	/**
	 * Lit une ligne du fichier et la retourne sous forme d'entier (doit etre un
	 * nombre)
	 * 
	 * @return Int lu, retourne MIN_VALUE+1 dans le cas ou le fichier est
	 *         termine
	 * @throws NumberFormatException
	 *             date 23/08/02
	 */
	public int lireLigneInt() throws NumberFormatException {
		String temp = lireLigne();
		if (temp == null)
			return Integer.MIN_VALUE + 1;
		return Integer.parseInt(temp);
	}

	/**
	 * Lit une ligne de String s�par� par le caract�re separateur
	 * 
	 * @param separateur
	 *            Separateur de valeur
	 * @return Vecteur contenant les valeurs lues, retorune null si le fichier
	 *         est fini
	 */
	public String[] lireLigneStringVecteur(char separateur) {
		String chaine = lireLigne();
		if (chaine == null)
			return null;
		int i, j, compte = 0;
		String vecteur[];
		int num;
		char temp[] = chaine.toCharArray();
		char analyse[] = new char[temp.length];
		for (i = 0; i < temp.length; i++) {
			if (temp[i] == separateur)
				compte++;
		}
		vecteur = new String[compte + 1];
		compte = 0;
		num = 0;
		for (i = 0; i < temp.length; i++) {
			if (temp[i] != separateur) {
				analyse[compte] = temp[i];
				compte++;
			} else {
				char analyseTaille[] = new char[compte];
				for (j = 0; j < compte; j++)
					analyseTaille[j] = analyse[j];
				if (num != -1)
					vecteur[num] = String.valueOf(analyseTaille);
				num++;
				compte = 0;
			}
		}
		if (compte != 0) {
			char analyseTaille[] = new char[compte];
			for (j = 0; j < compte; j++)
				analyseTaille[j] = analyse[j];
			if (num != -1)
				vecteur[num] = String.valueOf(analyseTaille);
			num++;
			compte = 0;
		}
		return vecteur;
	}

	/**
	 * Lit une ligne de double s�par� par le caract�re separateur
	 * 
	 * @param separateur
	 *            Separateur de valeur
	 * @throws NumberFormatException
	 * @return Vecteur contenant les valeurs lues, retorune null si le fichier
	 *         est fini
	 */
	public double[] lireLigneDoubleVecteur(char separateur)
			throws NumberFormatException {
		String chaine = lireLigne();
		if (chaine == null)
			return null;
		int i, j, compte = 0;
		double vecteur[];
		int num;
		char temp[] = chaine.toCharArray();
		char analyse[] = new char[temp.length];
		for (i = 0; i < temp.length; i++) {
			if (temp[i] == separateur)
				compte++;
		}
		vecteur = new double[compte + 1];
		compte = 0;
		num = 0;
		for (i = 0; i < temp.length; i++) {
			if (temp[i] != separateur) {
				analyse[compte] = temp[i];
				compte++;
			} else {
				char analyseTaille[] = new char[compte];
				for (j = 0; j < compte; j++)
					analyseTaille[j] = analyse[j];
				if (num != -1)
					vecteur[num] = Double.parseDouble(String
							.valueOf(analyseTaille));
				num++;
				compte = 0;
			}
		}
		if (compte != 0) {
			char analyseTaille[] = new char[compte];
			for (j = 0; j < compte; j++)
				analyseTaille[j] = analyse[j];
			if (num != -1)
				vecteur[num] = Double
						.parseDouble(String.valueOf(analyseTaille));
			num++;
			compte = 0;
		}
		return vecteur;
	}

	/**
	 * Lit une ligne d'entier s�par� par le caract�re separateur
	 * 
	 * @param separateur
	 *            Separateur de valeur
	 * @throws NumberFormatException
	 * @return Vecteur contenant les valeurs lues, retorune null si le fichier
	 *         est fini
	 */
	public int[] lireLigneIntVecteur(char separateur)
			throws NumberFormatException {
		String chaine = lireLigne();
		if (chaine == null)
			return null;
		int i, j, compte = 0;
		int vecteur[];
		int num;
		char temp[] = chaine.toCharArray();
		char analyse[] = new char[temp.length];
		for (i = 0; i < temp.length; i++) {
			if (temp[i] == separateur)
				compte++;
		}
		vecteur = new int[compte + 1];
		compte = 0;
		num = 0;
		for (i = 0; i < temp.length; i++) {
			if (temp[i] != separateur) {
				analyse[compte] = temp[i];
				compte++;
			} else {
				char analyseTaille[] = new char[compte];
				for (j = 0; j < compte; j++)
					analyseTaille[j] = analyse[j];
				if (num != -1)
					vecteur[num] = Integer.parseInt(String
							.valueOf(analyseTaille));
				num++;
				compte = 0;
			}
		}
		if (compte != 0) {
			char analyseTaille[] = new char[compte];
			for (j = 0; j < compte; j++)
				analyseTaille[j] = analyse[j];
			if (num != -1)
				vecteur[num] = Integer.parseInt(String.valueOf(analyseTaille));
			num++;
			compte = 0;
		}
		return vecteur;
	}

	/**
	 * Retourne un tableau d'entier. Le parser commence apres nb_blank
	 * caract�res Size est la taille des blocs, les caracteres blanc sont
	 * elimines.
	 * 
	 * @param nb_blank
	 * @param size
	 * @return number
	 * @throws NumberFormatException
	 */
	public int[] lireLigneIntVecteur(int nb_blank, int size)
			throws NumberFormatException {
		String chaine = lireLigne();
		if (chaine == null)
			return null;
		int lg = chaine.length() - nb_blank;
		int nb_bloc = (int) lg / size;
		int[] tab_int = new int[nb_bloc];
		for (int i = 0; i < nb_bloc; i++) {
			String temp = chaine.substring(nb_blank + i * size,
					nb_blank + i * size + size).trim();
			int nd_num = Integer.parseInt(temp);
			tab_int[i] = nd_num;
		}
		return tab_int;
	}

	/**
	 * Retourne un tableau de double. Le parser commence apres nb_blank
	 * caract�res Size est la taille des blocs, les caracteres blanc sont
	 * �limin�s.
	 * 
	 * @param nb_blank
	 * @param size
	 * @return line
	 * @throws NumberFormatException
	 */
	public double[] lireLigneDoubleVecteur(int nb_blank, int size)
			throws NumberFormatException {
		String chaine = lireLigne();
		if (chaine == null)
			return null;
		int lg = chaine.length() - nb_blank;
		int nb_bloc = (int) lg / size;
		double[] tab_double = new double[nb_bloc];
		for (int i = 0; i < nb_bloc; i++) {
			String temp = chaine.substring(nb_blank + i * size,
					nb_blank + i * size + size).trim();
			float nd_num = Float.valueOf(temp);
			tab_double[i] = (double) nd_num;
		}
		return tab_double;
	}

	/**
	 * Retorune l'entier compris entre les postions start et end Les blanc sont
	 * elilimines
	 * 
	 * @param start
	 * @param end
	 * @return int
	 */
	public int lireInt(int start, int end) {
		String chaine = lireLigne();
		String num = chaine.substring(start, end).trim();
		return Integer.parseInt(num);
	}

	/**
	 * Remet au debut un fichier date 23/08/02
	 * 
	 * @author JMG
	 */
	public void setDebut() {
		ligneEnCours = 0;
	}

	/**
	 * Recherche le nombre de ligne contenu dans le fichier
	 * 
	 * @date 23/08/02
	 */
	private void getFile(LineNumberReader reader) throws IOException {
		String temp;
		boolean fin = true;
		nbLigne = 0;
		tampon = new Vector();
		while (fin) {
			temp = reader.readLine();
			if (temp != null) {
				tampon.add(temp);
				nbLigne++;
			} else
				fin = false;
		}
		ligneEnCours = 0;
		reader.close();
	}

	/**
	 * Retourne le nombre de ligne contenu dans le fichier
	 * 
	 * @return date 12/07/04
	 */
	public int getNbLignes() {
		return tampon.size();
	}

}
