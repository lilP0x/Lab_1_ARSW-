/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.blacklistvalidator;

import edu.eci.arsw.spamkeywordsdatasource.HostBlacklistsDataSourceFacade;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hcadavid
 */
public class HostBlackListsValidator {

    private static final int BLACK_LIST_ALARM_COUNT=5;
    private int checkedListsCount=0;

    /**
     * Check the given host's IP address in all the available black lists,
     * and report it as NOT Trustworthy when such IP was reported in at least
     * BLACK_LIST_ALARM_COUNT lists, or as Trustworthy in any other case.
     * The search is not exhaustive: When the number of occurrences is equal to
     * BLACK_LIST_ALARM_COUNT, the search is finished, the host reported as
     * NOT Trustworthy, and the list of the five blacklists returned.
     * @param ipaddress suspicious host's IP address.
     * @return  Blacklists numbers where the given host's IP address was found.
     */
    public List<Integer> checkHost(String ipaddress, int N){
        
        LinkedList<Integer> blackListOcurrences=new LinkedList<>();
        
        HostBlacklistsDataSourceFacade skds=HostBlacklistsDataSourceFacade.getInstance();


        ArrayList<Thread_BlackList> hilos = new ArrayList<>();
        
        int ocurrencesCount=0;

        int x = 0 ; 
        int range = 80000 / N ; 
        int y= range;

        for (int i=0; i<N;i++){
           
            Thread_BlackList hilo = new Thread_BlackList(x, y, ipaddress);
            hilo.start();
            hilos.add(hilo);
            try {
                hilo.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Inicio"+ x +""+ y );
            y *= 2;
            x = y/2 +1;
            System.out.println("Final"+ x +""+ y );
        }

        for(Thread_BlackList h : hilos){
            ocurrencesCount += h.coincidenceCount();
            checkedListsCount += h.getCheckedListsCount();
            blackListOcurrences.addAll(h.getBlackList());
        }

        
        if (ocurrencesCount>=BLACK_LIST_ALARM_COUNT){
            skds.reportAsNotTrustworthy(ipaddress);
        }
        else{
            skds.reportAsTrustworthy(ipaddress);
        }                
        
        LOG.log(Level.INFO, "Checked Black Lists:{0} of {1}", new Object[]{checkedListsCount, skds.getRegisteredServersCount()});
        
        return blackListOcurrences;
    }
    
    
    private static final Logger LOG = Logger.getLogger(HostBlackListsValidator.class.getName());
    
    
    
}
