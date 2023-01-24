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
public class Triangle2D {
    Point2D[] points;
    Region2D region;
    public Triangle2D(Point2D[] points,Region2D region){
        this.points=points;
        this.region=region;
       
    }
    public double[][] getCoordinate(){
        double[][] pointsCoor = new double [points.length][];
        
        for (int i=0; i<points.length; i++){
            pointsCoor[i] = points[i].getCoordinate();
        }
        return pointsCoor;
        
    }
    public Region2D getRegion(){
        return region;
    }
}
