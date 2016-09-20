package testserver;

import com.esotericsoftware.kryonet.Connection;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by abd on 26/03/16.
 */
public class CheckClientsStatus implements Runnable {

    Hashtable clientTable;
    Hashtable clientStatusTable;

    int timwout = 30000;
    //  boolean [] clientsStatus;

    public CheckClientsStatus(Hashtable clientTable, Hashtable clientStatusTable, int timwout) {
        this.clientTable = clientTable;
        this.timwout = timwout;
        this.clientStatusTable = clientStatusTable;
    }

    /* public CheckClientsStatus(Hashtable clientTable){
        this.clientTable = clientTable;
        clientsStatus = new boolean[clientTable.size()];
        for (boolean s : clientsStatus ){
            s = true;
        }
    }*/
    @Override
    public void run() {
        int index;
        while (true) {

            System.out.println("Removing Offline Users");

            Set set = clientTable.entrySet();
            //    clientsStatus = new boolean[clientTable.size()];
            // Get an iterator
            Iterator i = set.iterator();
            // Display elements
            index = 0;
            while (i.hasNext()) {
                Map.Entry me = (Map.Entry) i.next();
                Connection temp = (Connection) me.getValue();
                String clientID = (String) me.getKey();
                boolean clstate = temp.isConnected();

                ClientStatus aa = (ClientStatus) clientStatusTable.get(clientID);

                if (clstate != aa.isStatus()) {
                    System.out.println("Client old Status : " + aa.isStatus());
                    // clientStatusTable.put(clientID, new Boolean(clstate));
                    aa.setStatus(clstate);
                    if (clstate) {
                        SendUtil.broadcastStatusMessage(clientTable, new StatusMessage(clientID, 0));
                        System.out.println("Client Become Online : " + clientID);
                    } else {
                        SendUtil.broadcastStatusMessage(clientTable, new StatusMessage(clientID, 1));
                        System.out.println("Client Become Offline : " + clientID);
                    }
                } else {
                    //  aa.increaseCounter();
                }

            }
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }

}
