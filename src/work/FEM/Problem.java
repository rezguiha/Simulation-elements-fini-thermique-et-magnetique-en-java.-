/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package work.FEM;

import java.util.ArrayList;
import mesh.Element;
import mesh.Mesh2D;
import work.Geom.Region1D;

/**
 *
 * @author rezguiha
 */
public class Problem {
    
    
    Mesh2D mesh;
    String name;
    
    public Problem(Mesh2D mesh,String name){
        this.mesh = mesh;
        this.name = name;
    }
    
    public void solve(){
        LinearSystem linearSystem=new LinearSystem(mesh);
        linearSystem.assemble();
        linearSystem.solve();
    }
    
    public double computeEnergy(){
        double gradTsquare;
        ArrayList<Element> elementTab = mesh.getElementsMesh();
        double qTotal = 0;
         
        for ( int i=0; i< elementTab.size(); i++){
            Element e = mesh.getElementsMesh().get(i);
            double grad [] =e.computeGradVariable(e.getXYCenter());
            gradTsquare = Math.pow(grad[0],2) + Math.pow(grad[1], 2);
            qTotal += gradTsquare * e.getConductivity()* e.getArea();     
        }
        return qTotal;
    }
    
    public double computeResistence(Region1D output, Region1D input){
        return Math.pow(output.getDirichletValue() - input.getDirichletValue(), 2)/computeEnergy();    
    }
    
    public void plotVariable(double[] xyO, double[] xyE, int nbPoints){
        ArrayList<Double> temperature = new ArrayList <>();        
    }    
}
