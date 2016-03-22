/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testserver;

import com.esotericsoftware.kryonet.Connection;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.io.ByteArrayOutputStream;

/**
 *
 * @author Abd
 */
public class SendUtil {

    /// send that New User Online
    public static void broadcastNewUser(HashMap clientTable, UserLogin ul1) {
        Set set = clientTable.entrySet();
        // Get an iterator
        Iterator i = set.iterator();
        // Display elements
        while (i.hasNext()) {
            Map.Entry me = (Map.Entry) i.next();
            Connection temp = (Connection) me.getValue();
            String senderID = (String) me.getKey();
            if (!(senderID.equals(ul1.getUserID()))) {
                temp.sendTCP(ul1);
                System.out.println("NEW user Message Send to : " + senderID);
            }

        }

    }

    /// send file to recivers (FileChunkMessageV2 fcv2: used toget sender details)
    public static void SendFileToRecivers(HashMap clientTable, FileChunkMessageV2 fcv2, String[] tRecivers) {
        RandomAccessFile aFile = null;
        long chunkcounter;
        int bufferSize = 2000;
        if (tRecivers != null) {

            try {
                aFile = new RandomAccessFile("E:/" + fcv2.getFileName(), "rw");

                chunkcounter = 0;
                ByteArrayOutputStream bos = new ByteArrayOutputStream();;
                byte[] buf = new byte[bufferSize];

                for (int readNum; (readNum = aFile.read(buf)) != -1;) {

                    chunkcounter++;
                    //bos = new ByteArrayOutputStream();
                    bos.reset();
                    bos.write(buf, 0, readNum);
                    byte[] currentchunk = bos.toByteArray();

                    bos = new ByteArrayOutputStream();
                    FileChunkMessageV2 chunkFromFile = new FileChunkMessageV2();
                    chunkFromFile.setSenderName(fcv2.getSenderName());
                    chunkFromFile.setSenderID(fcv2.getSenderID());
                    chunkFromFile.setChunkCounter(chunkcounter);
                    chunkFromFile.setChunk(currentchunk);
                    chunkFromFile.setFileName(fcv2.getFileName());

                    sendFileChunkToRecivers(clientTable, fcv2, tRecivers);

                    //client.sendTCP(chunkFromFile);
                }

                // send end of file Packet
                FileChunkMessageV2 endofFile = new FileChunkMessageV2();
                endofFile.setSenderName(fcv2.getSenderName());

                endofFile.setSenderID(fcv2.getSenderID());

                endofFile.setChunkCounter((-1L));
                endofFile.setFileName(fcv2.getFileName());

            } catch (Exception e) {

                e.printStackTrace();

            }
        }
    }

    public static void sendFileChunkToRecivers(HashMap clientTable, FileChunkMessageV2 fcv2, String[] tRecivers) {
        // FileChunkMessageV2 fcv2 used to get file informations 

        if (tRecivers != null) {
            System.out.println("There IS Recivers");

            Set set = clientTable.entrySet();
            // Get an iterator
            Iterator it = set.iterator();
            // Display elements
            while (it.hasNext()) {
                Map.Entry me = (Map.Entry) it.next();
                Connection temp = (Connection) me.getValue();
                String reciverID = (String) me.getKey();
               
                //String[] tRecivers = tm.getRecivers();
                if (!(reciverID.equals(fcv2.getSenderID()))) {
                     System.out.println("Current Client is :" +reciverID);
                    if (findUserIDInArray(reciverID, tRecivers)) {
                        System.out.println("Current Reciver is :" +reciverID);
                        temp.sendTCP(fcv2);
                    }

                }

            }
        }

    }

    /// Send Message from sender to reciver 
    public static void sendSimpleMessageToRecivers(HashMap clientTable, TextMeesage tm) {

        if (tm.getRecivers() != null) {

            Set set = clientTable.entrySet();
            // Get an iterator
            Iterator it = set.iterator();
            // Display elements
            while (it.hasNext()) {
                Map.Entry me = (Map.Entry) it.next();
                Connection temp = (Connection) me.getValue();
                String reciverID = (String) me.getKey();
                String[] recivers = tm.getRecivers();
                if (recivers != null) {
                    if (!(reciverID.equals(tm.getSenderID()))) {
                        if (findUserIDInArray(reciverID, recivers)) {
                            temp.sendTCP(new SimpleTextMessage(tm.getSenderID(), tm.getSenderName(), "TXT", tm.getTextMessage()));

                        }

                    }

                }

            }
        }

    }

    public static void sendLockMessageToRecivers(HashMap clientTable, LockMessage tm) {

        if (tm.getReceivers() != null) {

            Set set = clientTable.entrySet();
            // Get an iterator
            Iterator it = set.iterator();
            // Display elements
            while (it.hasNext()) {
                Map.Entry me = (Map.Entry) it.next();
                Connection temp = (Connection) me.getValue();
                String reciverID = (String) me.getKey();
                String[] recivers = tm.getReceivers();
                if (recivers != null) {
                    if (!(reciverID.equals(tm.getSenderID()))) {
                        if (findUserIDInArray(reciverID, recivers)) {
                        	temp.sendTCP(tm);
                        }

                    }

                }

            }
        }

    }

    private static boolean findUserIDInArray(String uid, String[] recivers) {
        for (String reciver : recivers) {
            if (uid.equals(reciver)) {
                return true;
            }
        }
        return false;
    }

}
