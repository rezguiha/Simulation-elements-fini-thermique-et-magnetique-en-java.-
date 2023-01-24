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
public class Point2D {
    double xCoordinate,yCoordinate;
    public Point2D(double x,double y){
        xCoordinate=x;
        yCoordinate=y;
    }
    public double[] getCoordinate(){
        double[] coordinate = new double[2];
        coordinate[0]=xCoordinate;
        coordinate[1]=yCoordinate;
       
        return coordinate;
    }
    
}
