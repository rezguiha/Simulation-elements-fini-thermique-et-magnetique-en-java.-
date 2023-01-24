package mesh;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jfree.chart.axis.NumberAxis;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import work.Geom.Edge;
import work.Geom.Region2D;
import work.Geom.Triangle2D;

/**
 * Class for plotting the studied problem and the isovalues of the solution
 * 
 * @author rezguiha
 * 
 */
public class PlotXY extends JPanel {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The title of the curve
	 */
	String title;

	/**
	 * Set of mesh elements
	 * 
	 */
	ArrayList<Element> triangles;

	/**
	 * The number of IsoValues
	 */
	int nbrIso;

	Path2D colorPlot;

	/**
	 *   
	 */
	double[] xyMinMax;

	/**
	 * Trace 2D of the border of the studied domain and isovalues of the
	 * solution ( *
	 * 
	 * @param title
	 *            The title
	 * @param mesh2d
	 *            The mesh problem (it contains elements of the mesh).
	 * @param nbrIso
	 *            Number of the lines in the plotting IsoValue.
	 */
	public PlotXY(String title, Mesh2D mesh2d, int nbrIso) {

		this.title = title;
		this.triangles = mesh2d.getElementsMesh();
		this.nbrIso = nbrIso;
		this.xyMinMax = this.xyMinMax(triangles);

	}

	/**
	 * Trace 2D of the border of the studied domain and isovalues of the
	 * solution ( *
	 * 
	 * @param title
	 *            The title
	 * @param mesh2d
	 *            The mesh problem (it contains elements of the mesh).
	 * @param nbrIso
	 *            Number of the lines in the plotting IsoValue.
	 */
	public PlotXY(String title, Triangle2D[] triangles, Edge[] edges) {

		this.title = title;
		Mesh2D mesh2d = new Mesh2D(triangles, edges);
		this.triangles = mesh2d.getElementsMesh();
		this.nbrIso = 0;
		this.xyMinMax = this.xyMinMax(this.triangles);

	}

	/**
	 * Plot the geomtry problem
	 */
	public void geometryPlot() {
		// Create dataset
		XYSeriesCollection dataset = new XYSeriesCollection();
		String subtitle = "Domain Borders";

		// Boundary
		ArrayList<double[][]> boundary = getBorderDomain();
		if (boundary != null) {

			{
				XYSeries xySeries = new XYSeries("Borders", false);
				for (int i = 0; i < boundary.size(); i++) {
					double xOri = boundary.get(i)[0][0];
					double yOri = boundary.get(i)[0][1];

					double xExt = boundary.get(i)[1][0];
					double yExt = boundary.get(i)[1][1];

					xySeries.add(xOri, yOri);
					xySeries.add(xExt, yExt);
					xySeries.add(xExt, null);
				}
				dataset.addSeries(xySeries);
			}
			// Create chart
			JFreeChart chart = ChartFactory.createXYLineChart(title, "X", "Y",
					dataset, PlotOrientation.VERTICAL, false, true, false);
			chart.addSubtitle(new TextTitle(subtitle));

			XYPlot plot = chart.getXYPlot();
			plot.setBackgroundPaint(Color.WHITE);
			plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
			plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
			NumberAxis valueScaleRange = (NumberAxis) plot.getRangeAxis();
			valueScaleRange.setAutoRangeIncludesZero(false);
			int count = dataset.getSeriesCount();

			plot.getRenderer().setSeriesPaint(0, Color.BLACK);
			for (int k = 1; k < count; k++) {
				plot.getRenderer().setSeriesPaint(k, Color.BLUE);
			}

			// Frame
			JFrame frame = new JFrame(title);
			// frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			
			frame.setPreferredSize(new Dimension(500,500));
			frame.setLayout(new GridLayout(1, 1));
			frame.add(new ChartPanel(chart));
			frame.pack();
			frame.setVisible(true);
		}
	}

	/**
	 * Plot the mesh geomtry
	 */
	public void meshPlot() {
		// Create dataset
		XYSeriesCollection dataset = new XYSeriesCollection();
		String subtitle = "Domain Borders";

		// Boundary
		// ArrayList<double[][]> boundary = getBorderDomain();
		if (triangles != null) {

			{

				XYSeries xySeries = new XYSeries("Mesh Domain", false);
				for (int i = 0; i < triangles.size(); i++) {
					int dim = triangles.get(i).getNodes().size();
					for (int j = 0; j < dim; j++) {
						double xOri = triangles.get(i).getNodes().get(j)
								.getCoo()[0];
						double yOri = triangles.get(i).getNodes().get(j)
								.getCoo()[1];

						double xExt = triangles.get(i).getNodes()
								.get((j + 1) % 3).getCoo()[0];
						double yExt = triangles.get(i).getNodes()
								.get((j + 1) % 3).getCoo()[1];

						xySeries.add(xOri, yOri);
						xySeries.add(xExt, yExt);
						xySeries.add(xExt, null);

					}

				}
				dataset.addSeries(xySeries);
			}
			// Create chart
			JFreeChart chart = ChartFactory.createXYLineChart(title, "X", "Y",
					dataset, PlotOrientation.VERTICAL, false, true, false);
			chart.addSubtitle(new TextTitle(subtitle));

			XYPlot plot = chart.getXYPlot();
			plot.setBackgroundPaint(Color.WHITE);
			plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
			plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
			NumberAxis valueScaleRange = (NumberAxis) plot.getRangeAxis();
			valueScaleRange.setAutoRangeIncludesZero(false);
			int count = dataset.getSeriesCount();

			plot.getRenderer().setSeriesPaint(0, Color.BLACK);
			for (int k = 1; k < count; k++) {
				plot.getRenderer().setSeriesPaint(k, Color.BLUE);
			}

			// Frame
			JFrame frame = new JFrame(title);
			// frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			frame.setPreferredSize(new Dimension(500, 500));
			frame.setLayout(new GridLayout(1, 1));
			frame.add(new ChartPanel(chart));
			frame.setLocation(10, 500);
			frame.pack();
			frame.setVisible(true);
		}
	}

	/**
	 * Plot the solution
	 */
	public void solutionIsoLinesPlot() {

		// Create dataset
		XYSeriesCollection dataset = new XYSeriesCollection();
		String subtitle = "Domain Borders";

		// Boundary
		ArrayList<double[][]> boundary = getBorderDomain();
		if (boundary != null) {

			{
				XYSeries xySeries = new XYSeries("Borders", false);
				for (int i = 0; i < boundary.size(); i++) {
					double xOri = boundary.get(i)[0][0];
					double yOri = boundary.get(i)[0][1];

					double xExt = boundary.get(i)[1][0];
					double yExt = boundary.get(i)[1][1];

					xySeries.add(xOri, yOri);
					xySeries.add(xExt, yExt);
					xySeries.add(xExt, null);
				}
				dataset.addSeries(xySeries);
			}

			// Isovalues
			if (triangles != null && nbrIso > 0) {

				double[] uMinUmax = this.getUminUmax(triangles);
				subtitle = "Isovaleurs (uMin=" + (float) uMinUmax[0]
						+ ", uMax=" + (float) uMinUmax[1] + ")";

				double[] uIso = this.getUiso(uMinUmax[0], uMinUmax[1], nbrIso);
				List<Double>[] xyuIso = this.getXyuIsovals(triangles, uIso);

				for (int k = 0; k < nbrIso; k++) {
					String seriesName = "u=" + (float) uIso[k];
					XYSeries xySeries = new XYSeries(seriesName, false);
					for (Iterator<Double> it = xyuIso[k].iterator(); it
							.hasNext();) {
						double xxS = it.next();
						double yyS = it.next();
						it.next(); // uuS
						double xxE = it.next();
						double yyE = it.next();
						it.next(); // uuE
						xySeries.add(xxS, yyS);
						xySeries.add(xxE, yyE);
						xySeries.add(xxE, null);
					}
					dataset.addSeries(xySeries);
				}
			}

			// Create chart
			JFreeChart chart = ChartFactory.createXYLineChart(title, "X", "Y",
					dataset, PlotOrientation.VERTICAL, false, true, false);
			chart.addSubtitle(new TextTitle(subtitle));

			XYPlot plot = chart.getXYPlot();
			plot.setBackgroundPaint(Color.WHITE);
			plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
			plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
			NumberAxis valueScaleRange = (NumberAxis) plot.getRangeAxis();
			valueScaleRange.setAutoRangeIncludesZero(false);
			int count = dataset.getSeriesCount();

			plot.getRenderer().setSeriesPaint(0, Color.BLACK);
			for (int k = 1; k < count; k++) {
				plot.getRenderer().setSeriesPaint(k, Color.BLUE);
			}

			// Frame
			JFrame frame = new JFrame(title);
			frame.setLocation(800, 400);
			// frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setPreferredSize(new Dimension(500, 500));
			frame.setLayout(new GridLayout(1, 1));
			frame.add(new ChartPanel(chart));
			frame.pack();
			frame.setVisible(true);
		}
	}

	/**
	 * 
	 * Plot the cartography of the solution
	 * 
	 */

	public void solutionColorPlot() {
		JFrame frameColor = new JFrame(this.title);

		frameColor.setPreferredSize(new Dimension(500, 500));
		frameColor.getContentPane().setLayout(new BorderLayout());
		frameColor.getContentPane().add(this, BorderLayout.CENTER);

		//
		JPanel jPanel = new JPanel();
		jPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		// double[] uMinUmax = this.getUminUmax(triangles);
		double[] uMinUmax = this.getUminUmax(triangles);
		String label1 = this.title;
		String label2 = " Isovaleurs (uMin=" + (float) uMinUmax[0] + ", uMax="
				+ (float) uMinUmax[1] + ")";

		JLabel jLabel = new JLabel(label1);

		// create new Font
		Font font = new Font("Courier", Font.BOLD, 32);
		jLabel.setFont(font);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		jPanel.add(jLabel, c);

		jLabel = new JLabel(label2);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 1;
		jPanel.add(jLabel, c);

		frameColor.getContentPane().add(jPanel, BorderLayout.NORTH);
		frameColor.setLocation(600, 10);
		frameColor.pack();
		frameColor.setVisible(true);
	}

	/**
	 * Returns the minimum and maximum potentials of the set of triangles
	 * 
	 * @param triangles
	 *            the elements.
	 * @return minimum and maximum potentials.
	 */
	private double[] getUminUmax(ArrayList<Element> triangles) {

		double uMin = Double.MAX_VALUE;
		double uMax = Double.MIN_VALUE;
		for (Element t : triangles) {
			double uA = t.getNodes().get(0).getValue();
			double uB = t.getNodes().get(1).getValue();
			double uC = t.getNodes().get(2).getValue();

			if (uA < uMin) {
				uMin = uA;
			}
			if (uA > uMax) {
				uMax = uA;
			}
			if (uB < uMin) {
				uMin = uB;
			}
			if (uB > uMax) {
				uMax = uB;
			}
			if (uC < uMin) {
				uMin = uC;
			}
			if (uC > uMax) {
				uMax = uC;
			}
		}
		return new double[] { uMin, uMax };
	}

	/**
	 * Returns the minimum and maximum potentials of the set of triangles
	 * 
	 * @param triangles
	 *            the elements.
	 * @return minimum and maximum potentials.
	 */
	private double[] getGradUminUmax(ArrayList<Element> triangles) {

		double uMin = Double.MAX_VALUE;
		double uMax = Double.MIN_VALUE;
		for (Element t : triangles) {
			double[] xy = t.getXYCenter();
			double uA = Math.sqrt(Math.pow(t.computeGradVariable(xy)[0], 2)
					+ Math.pow(t.computeGradVariable(xy)[1], 2));

			if (uA < uMin) {
				uMin = uA;
			}
			if (uA > uMax) {
				uMax = uA;
			}

		}
		return new double[] { uMin, uMax };
	}

	/**
	 * Retourne les isovaleurs équir parties entre valeur min et valeur max.
	 * 
	 * @param uMin
	 *            min value.
	 * @param uMax
	 *            max value.
	 * @param nbrIso
	 *            the number of the Isovalues.
	 * @return Isovalues.
	 */
	private double[] getUiso(double uMin, double uMax, int nbrIso) {

		nbrIso = Math.max(0, nbrIso);
		double[] uIso = new double[nbrIso];
		if (nbrIso == 1) {
			uIso[0] = (uMax + uMin) / 2;
		} else {
			double dU = (uMax - uMin) / (nbrIso - 1);
			for (int k = 0; k < nbrIso; k++) {
				uIso[k] = uMin + dU * k;
			}
		}
		return uIso;
	}

	/**
	 * Returns isovalues equally distributed between min and max values.
	 * 
	 * @param triangles
	 *            the elements.
	 * @param uIso
	 *            Isovalues.
	 * @return the table containing the lists of isovalue lines. Each list is
	 *         composed as follows [x0Start, y0Start, u0start, x0End, y0End,
	 *         u0end, x1Start, y1Start, u1start, x1End, y1End, u1end, ...]
	 * 
	 */
	private List<Double>[] getXyuIsovals(ArrayList<Element> triangles,
			double[] uIso) {

		// Initialize the lists
		@SuppressWarnings("unchecked")
		List<Double>[] xyuIso = new List[uIso.length];
		for (int k = 0; k < uIso.length; k++) {
			xyuIso[k] = new ArrayList<Double>();
		}

		// Loop on triangles
		double[] xT = new double[3];
		double[] yT = new double[3];
		double[] uT = new double[3];

		for (Element t : triangles) {

			double tuA = t.getNodes().get(0).getValue();
			double tuB = t.getNodes().get(1).getValue();
			double tuC = t.getNodes().get(2).getValue();

			double txA = t.getNodes().get(0).getCoo()[0];
			double txB = t.getNodes().get(1).getCoo()[0];
			double txC = t.getNodes().get(2).getCoo()[0];

			double tyA = t.getNodes().get(0).getCoo()[1];
			double tyB = t.getNodes().get(1).getCoo()[1];
			double tyC = t.getNodes().get(2).getCoo()[1];

			// Minimim and maximum values
			double uMin = Math.min(tuA, Math.min(tuB, tuC));
			double uMax = Math.max(tuA, Math.max(tuB, tuC));

			xT[0] = txA;
			yT[0] = tyA;
			uT[0] = tuA;
			xT[1] = txB;
			yT[1] = tyB;
			uT[1] = tuB;
			xT[2] = txC;
			yT[2] = tyC;
			uT[2] = tuC;

			// Loop on isovalues
			double[] xIso = new double[3];
			double[] yIso = new double[3];
			for (int k = 0; k < uIso.length; k++) {
				double uIsoK = uIso[k];
				if (uIsoK >= uMin && uIsoK <= uMax) {
					int cIso = 0;
					for (int c = 0; c < 3; c++) {
						int cA = c;
						int cB = (c + 1) % 3;
						if (uT[cA] > uT[cB]) {
							cA = cB;
							cB = c;
						}
						double xA = xT[cA];
						double yA = yT[cA];
						double uA = uT[cA];
						double xB = xT[cB];
						double yB = yT[cB];
						double uB = uT[cB];
						double dU = uB - uA;
						if (uIsoK >= uA && uIsoK <= uB && dU != 0) {
							xIso[cIso] = xA + (xB - xA) * (uIsoK - uA) / dU;
							yIso[cIso] = yA + (yB - yA) * (uIsoK - uA) / dU;
							cIso++;
						}
					}
					if (cIso >= 2) {
						List<Double> xyuIsoK = xyuIso[k];
						if (xIso[0] != xIso[1] || yIso[0] != yIso[1]) {
							xyuIsoK.add(xIso[0]);
							xyuIsoK.add(yIso[0]);
							xyuIsoK.add(uIsoK);
							xyuIsoK.add(xIso[1]);
							xyuIsoK.add(yIso[1]);
							xyuIsoK.add(uIsoK);
						}
						if (cIso >= 3) {
							if (xIso[1] != xIso[2] || yIso[1] != yIso[2]) {
								xyuIsoK.add(xIso[1]);
								xyuIsoK.add(yIso[1]);
								xyuIsoK.add(uIsoK);
								xyuIsoK.add(xIso[2]);
								xyuIsoK.add(yIso[2]);
								xyuIsoK.add(uIsoK);
							}
						}
					}
				}
			}
		}
		return xyuIso;
	}

	/**
	 * Return the min/max xyCoordinates of the studied problem
	 * 
	 * @param triangles
	 * @return
	 */
	public static double[] xyMinMax(ArrayList<Element> triangles) {

		double xMin = 0.0;
		double yMin = 0.0;

		double xMax = 0.0;
		double yMax = 0.0;

		for (Element e : triangles) {
			for (Node n : e.getNodes()) {
				if (n.getCoo()[0] < xMin)
					xMin = n.getCoo()[0];
				if (n.getCoo()[0] > xMax)
					xMax = n.getCoo()[0];
				if (n.getCoo()[1] < yMin)
					yMin = n.getCoo()[1];
				if (n.getCoo()[1] > yMax)
					yMax = n.getCoo()[1];
			}

		}

		return new double[] { xMin, xMax, yMin, yMax };
	}

	/**
	 * returns the coordinates of the border edges
	 * 
	 * @return ArrayList containing the coordinates of the border edges
	 */

	public ArrayList<double[][]> getBorderDomain() {
		double[][] xy = new double[2][2];
		ArrayList<double[][]> border = new ArrayList<double[][]>();
		for (Element e : this.triangles) {
			Region2D reg = e.getRegion();
			for (int j = 0; j < e.getNodes().size(); j++) {
				xy = new double[][] { e.getNodes().get(j % 3).getCoo(),
						e.getNodes().get((j + 1) % 3).getCoo() };
				if (containsEdgeBorder(xy, reg) == 1) {
					border.add(xy);

				}
			}

		}

		return border;
	}

	/**
	 * returns the number of elements containing an edge
	 * 
	 * @param xy
	 *            Edge coordinates
	 * @return 1/2 : the number of the elements containing the edge
	 */
	private int containsEdgeBorder(double[][] xy, Region2D reg1) {
		int val = 0;
		for (Element e : this.triangles) {

			for (int j = 0; j < e.getNodes().size(); j++) {
				if (((xy[0][0] == e.getNodes().get(j % 3).getCoo()[0])
						&& (xy[0][1] == e.getNodes().get(j % 3).getCoo()[1])
						&& (xy[1][0] == e.getNodes().get((j + 1) % 3).getCoo()[0]) && (xy[1][1] == e
						.getNodes().get((j + 1) % 3).getCoo()[1]))
						|| ((xy[1][0] == e.getNodes().get(j % 3).getCoo()[0])
								&& (xy[1][1] == e.getNodes().get(j % 3)
										.getCoo()[1])
								&& (xy[0][0] == e.getNodes().get((j + 1) % 3)
										.getCoo()[0]) && (xy[0][1] == e
								.getNodes().get((j + 1) % 3).getCoo()[1]))
						&& equalRegion(reg1, e.getRegion()))
					val++;
			}

		}

		return val;
	}

	private boolean equalRegion(Region2D r1, Region2D r2) {
		if (r1.getConductivity() == r2.getConductivity()
				&& r1.getSource() == r2.getSource()
				&& r1.getName() == r2.getName())
			return true;
		else
			return false;
	}

	@Override
	protected void paintComponent(Graphics g) {

		super.paintComponent(g);

		Graphics2D g2d = (Graphics2D) g.create();

		for (int j = 0; j < triangles.size(); j++) {
			colorPlot = new Path2D.Double();
			double xn0 = triangles.get(j).getNodes().get(0).getCoo()[0];
			double xn1 = triangles.get(j).getNodes().get(1).getCoo()[0];
			double xn2 = triangles.get(j).getNodes().get(2).getCoo()[0];

			double yn0 = triangles.get(j).getNodes().get(0).getCoo()[1];
			double yn1 = triangles.get(j).getNodes().get(1).getCoo()[1];
			double yn2 = triangles.get(j).getNodes().get(2).getCoo()[1];

			double[] xp = new double[] { xn0, xn1, xn2 };
			double[] yp = new double[] { yn0, yn1, yn2 };

			GraphicConversion gs = new GraphicConversion(this.getWidth(),
					this.getHeight(), this.xyMinMax(triangles));

			colorPlot.moveTo(gs.toDisplayX(xp[0]), gs.toDisplayY(yp[0]));
			for (int i = 1; i < xp.length; i++) {
				colorPlot.lineTo(gs.toDisplayX(xp[i]), gs.toDisplayY(yp[i]));
			}
			colorPlot.closePath();

			double value = triangles.get(j).computeVariable(
					triangles.get(j).getXYCenter());

			// double value = Math.sqrt(Math.pow(triangles.get(j)
			// .computeGradVariable(triangles.get(j).getXYCenter())[0], 2)
			// + Math.pow(
			// triangles.get(j).computeGradVariable(
			// triangles.get(j).getXYCenter())[1], 2));

			int[] rgb = ColorMap.getColor(value, getUminUmax(triangles));
			g2d.setColor(new Color(rgb[0], rgb[1], rgb[2]));

			g2d.draw(colorPlot);

			g2d.fill(colorPlot);
			this.repaint();
		}

	}

}

/**
 * Class to define the gradient color
 * 
 */
class ColorMap {

	private static int[] red = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7, 23, 39, 55, 71, 87, 103, 119, 135,
			151, 167, 183, 199, 215, 231, 247, 255, 255, 255, 255, 255, 255,
			255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 246, 228, 211,
			193, 175, 158, 140 };
	private static int[] green = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 11, 27, 43, 59,
			75, 91, 107, 123, 139, 155, 171, 187, 203, 219, 235, 251, 255, 255,
			255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255,
			255, 247, 231, 215, 199, 183, 167, 151, 135, 119, 103, 87, 71, 55,
			39, 23, 7, 0, 0, 0, 0, 0, 0, 0 };
	private static int[] blue = { 0, 143, 159, 175, 191, 207, 223, 239, 255,
			255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255,
			255, 255, 255, 247, 231, 215, 199, 183, 167, 151, 135, 119, 103,
			87, 71, 55, 39, 23, 7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0 };

	public static int colorCount = red.length;

	public static int[] getColor(double value, double[] minMax) {
		int i = (int) ((colorCount - 1) * (value - minMax[0]) / (minMax[1] - minMax[0]));
		return new int[] { red[i], green[i], blue[i] };
	}
}

/**
 * Class to convert the real coordinate to the local coordinate
 * 
 * @author rezguiha
 * 
 */
class GraphicConversion {

	/**
	 * The window width
	 */
	private int width;

	/**
	 * The window height
	 */
	private int height;

	/**
	 * The Min value of the abscissa of the studied problem
	 */
	private double xMin;

	/**
	 * The Max value of the abscissa of the studied problem
	 */
	private double xMax;

	/**
	 * The Min value of the ordinate of the studied problem
	 */
	private double yMin;

	/**
	 * The Max value of the ordinate of the studied problem
	 */
	private double yMax;

	/**
	 * The scale
	 */
	private double scale;

	/**
	 * Basic scale
	 */
	private double basicScale = 100.0;

	/**
	 * The center offset
	 */
	private double offsetX;

	private double offsetY;

	/**
	 * 
	 * @param width
	 *            the window width
	 * @param height
	 *            the window height
	 * @param xyMinMax
	 *            The min/max values of xy the studied problem
	 */
	public GraphicConversion(int width, int height, double[] xyMinMax) {
		this.width = width;
		this.height = height;
		this.xMin = xyMinMax[0];
		this.xMax = xyMinMax[1];
		this.yMin = xyMinMax[2];
		this.yMax = xyMinMax[3];

		offsetX = this.width / 2.0;
		offsetY = this.height / 2.0;
		this.centreView();

	}

	/**
	 * 
	 * Convert a real abscissa into local abscissa (JPanel coordinate system)
	 * 
	 * 
	 * @param x
	 *            The real abscissa
	 * 
	 * @return Local abscissa
	 */
	public int toDisplayX(double x) {

		// return (int) Math.round(this.width * (x - this.xMin) /
		// (this.xMax-this.xMin)-offsetX);
		return (int) Math.round(width / 2 + this.offsetX + x * this.scale
				/ this.basicScale);
	}

	/**
	 * 
	 * Convert a real ordinate into local ordinate (JPanel coordinate system)
	 * 
	 * 
	 * @param x
	 *            The real ordinate
	 * 
	 * @return Local ordinate
	 */
	public int toDisplayY(double y) {

		return (int) Math.round(height / 2 + this.offsetY - y * this.scale
				/ this.basicScale);

	}

	/**
	 * Center the view of the studied domain
	 * 
	 */
	public void centreView() {

		if ((xMax - xMin != Double.NEGATIVE_INFINITY)
				&& (yMax - yMin != Double.NEGATIVE_INFINITY)) {

			double scaleX = (width - this.basicScale / 2.0) * this.basicScale
					/ (xMax - xMin);
			double scaleY = (height - this.basicScale / 2.0) * this.basicScale
					/ (yMax - yMin);

			this.scale = Math.min(scaleX, scaleY);

			this.offsetX = -(this.toDisplayX((xMax + xMin) / 2) - this
					.toDisplayX(0));
			this.offsetY = -(this.toDisplayY((yMax + yMin) / 2) - this
					.toDisplayY(0));

		}
	}

}
