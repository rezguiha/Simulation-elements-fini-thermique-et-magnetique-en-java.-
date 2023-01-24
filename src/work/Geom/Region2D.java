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
public class Region2D {
    double conductiviity,source;
    String name;
    public Region2D(double conductivity,double source){
        this.conductiviity=conductivity;
        this.source=source;
    }
    public double getConductivity(){
        return conductiviity;
    }
     public double getSource(){
        return source;
    }
      public String getName(){
        return name;
    }
      public void setName(String name){
          this.name=name;
      }
}
