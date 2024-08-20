package edu.eci.arsw.blacklistvalidator;

import java.util.LinkedList;

import edu.eci.arsw.spamkeywordsdatasource.HostBlacklistsDataSourceFacade;



public class Thread_BlackList extends Thread {


    private static final int BLACK_LIST_ALARM_COUNT=5;
    
    private int ocurrencesCount = 0;
    private int checkedListsCount=0;
    private String ip;

    
    HostBlacklistsDataSourceFacade skds=HostBlacklistsDataSourceFacade.getInstance();

    public Thread_BlackList(int inicio, int fin, String ip){
        this.ip = ip;

    }

    @Override
    public void run(){
        
        LinkedList<Integer> blackListOcurrences=new LinkedList<>();


        for (int i=0;i<skds.getRegisteredServersCount() && ocurrencesCount<BLACK_LIST_ALARM_COUNT;i++){
            checkedListsCount++;
            
            if (skds.isInBlackListServer(i, ip)){
                
                blackListOcurrences.add(i);
                
                ocurrencesCount++;
            }
        }

    }

    public int coincidenceCount(){

        return ocurrencesCount;
    }



}
