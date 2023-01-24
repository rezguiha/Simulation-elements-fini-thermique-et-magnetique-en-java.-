/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package work.test;


import java.util.ArrayList;
import mesh.Element;
import mesh.Mesh2D;
import mesh.PlotXY;
import work.FEM.ElementT3;
import work.FEM.LinearSystem;
import work.FEM.Problem;
import work.Geom.Edge;
import work.Geom.Triangle2D;

/**
 *
 * @author rezguiha
 */
public class test {
    
    public static void main(String[] args) {       
        double a = 10;
        double b = 10;
        double c = 8;
        double d = 8;
        double T1 = 0;
        double T0 = 20;
        double k = 0.1;
        //Defining all the parameters
       
        HollowWall hollowWall = new HollowWall("Temperature Distribution", a, b, c, d, T0, T1, k);
        //Square2D hollowWall = new Square2D("Temperature Distribution");

        PlotXY plot = new PlotXY(hollowWall.getTitle(),hollowWall.getTriangles(),hollowWall.getEdges());
        plot.geometryPlot();// draw of the geometry
        long time1 = System.currentTimeMillis();
        Mesh2D mesh2D = new Mesh2D(hollowWall.getTriangles(),hollowWall.getEdges(),4);
        long time2 = System.currentTimeMillis();        
        PlotXY plotmesh = new PlotXY(hollowWall.getTitle(),mesh2D,20);
        long t=time2-time1;
        //Calculates the time compuatation
        
        plotmesh.meshPlot();//draw of mesh
        Problem problem = new Problem(mesh2D,hollowWall.getTitle());
        problem.solve();
        
        //This is only to print the M matrix
        double[][] m=mesh2D.getElementsMesh().get(0).integrateMatriceElementaire();
        for (int i=0; i<3;i++){// print element 0 matrix
            for (int j=0; j<3; j++)
                System.out.print(m[i][j]+"   \t");
            System.out.println("   ");
        }
        m=mesh2D.getElementsMesh().get(1).integrateMatriceElementaire();
        for (int i=0; i<3;i++){// print element 1 matrix
            for (int j=0; j<3; j++)
                System.out.print(m[i][j]+"   \t");
            System.out.println("   ");
        }
        
        plotmesh.solutionIsoLinesPlot(); // draw the iso lines
        plotmesh.solutionColorPlot();// draw the color distribution graphic
        
        System.out.println("\n Computed Energy ="+ problem.computeEnergy());
        
        System.out.println("\n Thermal Resistence ="
                + problem.computeResistence(hollowWall.getOutputRegion1D(),hollowWall.getInputRegion1D()));
        
        System.out.println("\n Calculation time ="+ t);
        
        problem.plotVariable(new double [] {0.0, 0.0} , new double[] {1.0,1.0},10);
    }
}
