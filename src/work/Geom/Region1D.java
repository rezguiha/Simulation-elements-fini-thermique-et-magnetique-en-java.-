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
public class Region1D {
   double dirichletValue;
   String name;
   public Region1D(double dirichletValue){
       this.dirichletValue=dirichletValue;
   }
    public double getDirichletValue(){
        return dirichletValue;
    }
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name=name;
    }
}
