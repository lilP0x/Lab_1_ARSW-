/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.threads;

/**
 *
 * @author hcadavid
 */
public class CountThreadsMain {
    
    public static void main(String a[]){
        int [] arreglo = {0,99};
        int [] arreglo2 = {99,199};
        int [] arreglo3 = {200,299};

        CountThread intervalo1 = new CountThread(arreglo);
        CountThread intervalo2 = new CountThread(arreglo2);
        CountThread intervalo3 = new CountThread(arreglo3);

        intervalo1.start();
        intervalo2.start();
        intervalo3.start();
       
    }
    
}
