/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testserver;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;
import java.io.ByteArrayOutputStream;

/**
 * @author Abd
 */
public class SendUtil {

    /// send that New User Online
    public static void broadcastNewUser(Hashtable clientTable, UserLogin ul1) {
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

    public static void broadcastStatusMessage(Hashtable clientTable, StatusMessage toSend) {
        Set set = clientTable.entrySet();
        // Get an iterator
        Iterator i = set.iterator();
        // Display elements
        while (i.hasNext()) {
            Map.Entry me = (Map.Entry) i.next();
            Connection temp = (Connection) me.getValue();
            String recID = (String) me.getKey();
            temp.sendTCP(toSend);
            System.out.println("StatusMessage : " + recID);

        }

    }

    public static void SendFileToRecivers(HashMap clientTable, FileChunkMessageV2 fcv2, String[] tRecivers) {
        RandomAccessFile aFile = null;
        long chunkcounter;
        int bufferSize = 2000;
        if (tRecivers != null) {

            try {
                aFile = new RandomAccessFile("E:/" + fcv2.getFileName(), "rw");

                chunkcounter = 0;
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ;
                byte[] buf = new byte[bufferSize];

                for (int readNum; (readNum = aFile.read(buf)) != -1; ) {

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
                    System.out.println("Current Client is :" + reciverID);
                    if (findUserIDInArray(reciverID, tRecivers)) {
                        System.out.println("Current Reciver is :" + reciverID);
                        temp.sendTCP(fcv2);
                    }

                }

            }
        }

    }

    /// Send Message from sender to reciver
    public synchronized static void sendSimpleMessageToRecivers(Hashtable clientTable, TextMeesage tm) {

        if (tm.getRecivers() != null) {

            System.out.println("Ther Is recivers Here");
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
                            System.out.print("Send Simple Message To user ID : " + reciverID);

                        }

                    }

                }

            }
        }

    }

    public static void sendLockMessageToRecivers(Hashtable clientTable, LockMessage tm) {

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

    public static void sendScreenshotMessageToReceiver(Hashtable<String, Connection> clientTable,
		ScreenshotMessage tm) {

	    	if (tm.getReceiverID() != null) {

	            Set set = clientTable.entrySet();
	            // Get an iterator
	            Iterator it = set.iterator();
	            // Display elements
	            while (it.hasNext()) {
	                Map.Entry me = (Map.Entry) it.next();
	                Connection temp = (Connection) me.getValue();
	                String reciverID = (String) me.getKey();
	                String targetID = tm.getReceiverID();
	                if (targetID != null) {
	                    if (!(reciverID.equals(tm.getSenderID()))) {
	                        if (findUserIDInArray(reciverID, new String[]{targetID})) {
	                            temp.sendTCP(tm);
	                        }

	                    }

	                }

	            }
	        }
		
	}
    
    public static void sendMonitorRequestToReceiver(Hashtable<String, Connection> clientTable,
			MonitorRequestMessage tm) {

    	if (tm.getReceiverID() != null) {

            Set set = clientTable.entrySet();
            // Get an iterator
            Iterator it = set.iterator();
            // Display elements
            while (it.hasNext()) {
                Map.Entry me = (Map.Entry) it.next();
                Connection temp = (Connection) me.getValue();
                String reciverID = (String) me.getKey();
                String targetID = tm.getReceiverID();
                if (targetID != null) {
                    if (!(reciverID.equals(tm.getSenderID()))) {
                        if (findUserIDInArray(reciverID, new String[]{targetID})) {
                            temp.sendTCP(tm);
                        }

                    }

                }

            }
        }
	}
    public static void sendExamResuloRecivers(Hashtable clientTable, ExamResultMessage tm) {

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
            System.out.println("could be  Reciver" + reciver);
            if (uid.equals(reciver)) {
                System.out.println("Curren Simple Message Reciver is :" + reciver);
                return true;
            }
        }
        return false;
    }

    public void sendObjectTOALL(Hashtable clientTable, String senderID, Object toSend) {
        Set set = clientTable.entrySet();
        // Get an iterator
        Iterator i = set.iterator();
        // Display elements
        while (i.hasNext()) {
            Map.Entry me = (Map.Entry) i.next();
            Connection temp = (Connection) me.getValue();
            String recID = (String) me.getKey();
            if (!(recID.equals(senderID))) {
                temp.sendTCP(toSend);
                System.out.println("NEW user Message Send to : " + senderID);
            }

        }

    }

    public void reConnect(Client cl, UserLogin iam) throws IOException {
        if (!cl.isConnected()) {
            cl.reconnect();
            cl.sendTCP(iam);
        }

    }

	

	

}
