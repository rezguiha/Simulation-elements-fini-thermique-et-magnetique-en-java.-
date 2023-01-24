package work.FEM;

import java.util.ArrayList;

import work.Geom.Region2D;
import mesh.Element;
import mesh.Node;

/**
 * A class representing a triangular finite element
 *
 * @authors rezguiha
 *
 */
public class ElementT3 extends Element {

    /**
     * Build a new Element
     *
     * @param region the region of the element
     * @param nodes nodes of the element
     */
    
    double x1;
    double y1;
    double x2;
    double y2;
    double x3;    
    double y3;
    double delta;
       
    
    public ElementT3(Region2D region, ArrayList<Node> nodes) {
        super(region, nodes);
        defineValues();
    }

    public void defineValues(){//this function was created in order to use x and y values in other functions
        x1=super.nodes.get(0).getCoo()[0];
        y1=super.nodes.get(0).getCoo()[1];
        x2=super.nodes.get(1).getCoo()[0];
        y2=super.nodes.get(1).getCoo()[1];
        x3=super.nodes.get(2).getCoo()[0];    
        y3=super.nodes.get(2).getCoo()[1];
        delta=Math.abs((x2*y3)-(x3*y2)+(x1*y2)-(x2*y1)+(x3*y1)-(x1*y3));
    }
    
    @Override
    public double getArea() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        defineValues();
        double area = delta/2;
        return area;
    }

    @Override
    public double[] getPhi(double[] xy) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        defineValues();
        double phi1=((x2*y3-x3*y2)+(y2-y3)*xy[0]+(x3-x2)*xy[1])/delta;
        double phi2=((x3*y1-x1*y3)+(y3-y1)*xy[0]+(x1-x3)*xy[1])/delta;
        double phi3=((x1*y2-x2*y1)+(y1-y2)*xy[0]+(x2-x1)*xy[1])/delta;
         
        double[] ret = new double[3];
        ret[0]=phi1;
        ret[1]=phi2;
        ret[2]=phi3;
        
         return ret; 
    }

    @Override
    public double[][] getGradPhi(double[] xy) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        
        defineValues();
        double[][] grad = new double[2][3];
        
        grad[0][0]=(y2-y3)/(delta);// d fi/dx
        grad[1][0]=(x3-x2)/(delta);// d fi/dy
        grad[0][1]=(y3-y1)/(delta);
        grad[1][1]=(x1-x3)/(delta);
        grad[0][2]=(y1-y2)/(delta);
        grad[1][2]=(x2-x1)/(delta);

        return grad;
    }

    @Override
    public double[][] integrateMatriceElementaire() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        defineValues();
        double k = region.getConductivity();
        double[][] M = new double[3][3];       
        double[] xy = this.getXYCenter();
        double[][] grad = getGradPhi(xy);
        for (int i=0; i<3;i++){
            for (int j=0; j<3; j++){
                M[i][j] = (delta/2) * k * (grad[0][i] * grad[0][j] + grad[1][i] * grad[1][j]);
            }
        }                
        return M;
    }

    @Override
    public double[] integrateVecteurElementaire() {        
//throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    defineValues();
    double q0=this.getSource();
    double[] s = new double[3];
    s[0] =q0*delta/6;
    s[1] =q0*delta/6;
    s[2] =q0*delta/6;
    
    return s;   
    }

  
}
