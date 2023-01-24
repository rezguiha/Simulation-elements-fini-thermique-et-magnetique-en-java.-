package work.Geom;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author rezguiha
 */
public class Edge {
    Point2D[] pts;
    Region1D region1d;
    
    public Edge(Point2D[] pts,Region1D region1d){
        this.pts=pts;
        this.region1d=region1d;
        
    }
    public Region1D getRegion(){
        return region1d;
    }
    public Point2D[] getPoints(){
        return pts;
    }
    public double[][] getCoordinate(){
        double[][] pointsCoor = new double [pts.length][];
        
        for (int i=0; i<pts.length; i++){
            pointsCoor[i] = pts[i].getCoordinate();
        }
        return pointsCoor;
    } 
}
