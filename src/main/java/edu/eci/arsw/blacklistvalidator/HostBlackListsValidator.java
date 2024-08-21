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
    public List<Integer> checkHost(String ipaddress, int N) {
        LinkedList<Integer> blackListOccurrences = new LinkedList<>();
        HostBlacklistsDataSourceFacade skds = HostBlacklistsDataSourceFacade.getInstance();
    
        ArrayList<Thread_BlackList> threads = new ArrayList<>();
        int totalBlacklists = skds.getRegisteredServersCount();
        int range = totalBlacklists / N;
        int startIndex = 0;
        int occurrencesCount = 0;
        for (int i = 0; i < N; i++) {
            int endIndex;
            if (i == N - 1) {
                // Último hilo: debe cubrir hasta el final
                endIndex = totalBlacklists;
            } else {
                // Hilos intermedios: cubrir hasta el final del rango calculado
                endIndex = startIndex + range;
            }

            Thread_BlackList thread = new Thread_BlackList(startIndex, endIndex, ipaddress);
            thread.start();
            threads.add(thread);
            startIndex = endIndex;
        }
    
        for (Thread_BlackList thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    
        for (Thread_BlackList thread : threads) {
            occurrencesCount += thread.coincidenceCount();
            checkedListsCount += thread.getCheckedListsCount();
            blackListOccurrences.addAll(thread.getBlackList());
        }
    
        if (occurrencesCount >= BLACK_LIST_ALARM_COUNT) {
            skds.reportAsNotTrustworthy(ipaddress);
        } else {
            skds.reportAsTrustworthy(ipaddress);
        }
    
        LOG.log(Level.INFO, "Checked Black Lists:{0} of {1}", new Object[]{checkedListsCount, totalBlacklists});
    
        return blackListOccurrences;
    }
    
    
    
    private static final Logger LOG = Logger.getLogger(HostBlackListsValidator.class.getName());
    
    
    
}
