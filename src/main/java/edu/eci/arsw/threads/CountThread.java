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


public class CountThread extends Thread {

    private int[] bandas = new int[2];

    public CountThread ( int[] bandas){
        this.bandas = bandas;
    }

    @Override
    public void run(){

        for(int i=bandas[0]; i<=bandas[1]; i++){

            System.out.println(i);
        }

    }
    
    
}
