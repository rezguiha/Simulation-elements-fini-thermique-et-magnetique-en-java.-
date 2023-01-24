/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package work.FEM;

import java.util.ArrayList;
import matrix.SquareMatrix;
import mesh.Element;
import mesh.Mesh2D;
import mesh.Node;


/**
 *
 * @author rezguiha
 */
public class LinearSystem {
    
    Mesh2D mesh;
    double[] vector = null;
    SquareMatrix AMatrix = null;
  
    public LinearSystem(Mesh2D mesh){
        this.mesh = mesh;
        vector=new double[mesh.getNumberfreeNodes()];        
        AMatrix=new SquareMatrix(mesh.getNumberfreeNodes());

               
    }
    
    public void assemble(){
        Element e;
        int j=0;
        int i=0;
        
        int length = mesh.getElementsMesh().size();
        
        for(int k=0; k<length; k++){  
            e=mesh.getElementsMesh().get(k);
            for(int iloc=0;iloc<3;iloc++){
                i =e.getNodes().get(iloc).getID(); 
                if (i<mesh.getNumberfreeNodes()){
                    vector[i]=vector[i]+e.integrateVecteurElementaire()[iloc];
                    for(int jloc=0;jloc<3;jloc++){
                        j=e.getNodes().get(jloc).getID();
                        if (j<mesh.getNumberfreeNodes())
                            AMatrix.setElement(i, j, AMatrix.getElement(i, j)+e.integrateMatriceElementaire()[iloc][jloc]);        
                        else
                            vector[i]=vector[i]-e.integrateMatriceElementaire()[iloc][jloc]*mesh.getNodesMesh().get(j).getValue();
                    }
                }
            }      
        }      
    }
    
   public void solve(){
       double[] t = AMatrix.solve(vector);
       setSolutionToNodes(t);
   }
    
   public void setSolutionToNodes(double[] solution){
        for (Node n : mesh.getNodesMesh()) {
            int position = n.getID();
            if (position < mesh.getNumberfreeNodes()) {
                n.setValue(solution[position]);
            }
        }
   } 
    
}
