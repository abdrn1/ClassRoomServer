package testserver;

import com.esotericsoftware.kryonet.Connection;

import java.util.*;

/**
 * Created by abd on 26/03/16.
 */
public class CheckClientsStatus implements Runnable {

    Hashtable clientTable;
    int timwout=30000;

    public CheckClientsStatus(Hashtable clientTable,int  timwout){
        this.clientTable = clientTable;
        this.timwout=timwout;
    }
    public CheckClientsStatus(Hashtable clientTable){
        this.clientTable = clientTable;
    }
    @Override
    public void run() {
        while (true){

            System.out.println("Removing Offline Users");

            Set set = clientTable.entrySet();
            // Get an iterator
            Iterator i = set.iterator();
            // Display elements
            while (i.hasNext()) {
                Map.Entry me = (Map.Entry) i.next();
                Connection temp = (Connection) me.getValue();
                String clientID = (String) me.getKey();

                if(!temp.isConnected()){
                    System.out.println("Client Become OFFline : "+clientID);
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(!temp.isConnected()) {
                        clientTable.remove(clientID);
                    }
                    SendUtil.broadcastStatusMessage(clientTable,new StatusMessage(clientID,1));
                }


            }
            try {
                Thread.sleep(30000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
