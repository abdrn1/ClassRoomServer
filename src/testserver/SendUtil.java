/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testserver;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

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
                System.out.println("New user Become online , Tell  : " + senderID);
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

    public static void sendCapturedImageToRecivers(Hashtable clientTable, CapturedImageMessage cim, boolean saveToDB) {
        if (cim.getRecivers() != null) {

            System.out.println("Ther Is recivers Here");
            Set set = clientTable.entrySet();
            // Get an iterator
            Iterator it = set.iterator();
            // Display elements
            while (it.hasNext()) {
                Map.Entry me = (Map.Entry) it.next();
                Connection temp = (Connection) me.getValue();
                String reciverID = (String) me.getKey();
                String[] recivers = cim.getRecivers();
                if (recivers != null) {

                    if (!(reciverID.equals(cim.getSenderID()))) {
                        if (findUserIDInArray(reciverID, recivers)) {

                            LogRecord lr = new LogRecord();
                            lr.setSenderID(cim.getSenderID());
                            lr.setReciverID(reciverID);
                            lr.setMessageType(LogRecord.CAPTURE);
                            lr.setDetail(cim.getFileName());
                            lr.setNote("");
                            if (temp.isConnected()) {
                                temp.sendTCP(cim);
                                lr.setRecived(true);
                            } else {
                                lr.setRecived(false);
                            }
                            if (saveToDB) {
                                DBManager dbManager = new DBManager();
                                dbManager.addNewLogRecord(lr);
                            }

                            System.out.print("5- Send Capture Image To user ID : " + reciverID);

                        }

                    }

                }

            }
        }
    }


    /// Send Message from sender to reciver
    public static void sendSimpleMessageToRecivers(Hashtable clientTable, TextMeesage tm, boolean saveToDB) {

        LogRecord lr = new LogRecord();
        
        if (tm.getRecivers() != null) {

            // System.out.println("Ther Is recivers Here");
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
                            lr.setSenderID(tm.getSenderID());
                            lr.setReciverID(reciverID);
                            if (tm.getMessageType().equals("TXT")) {
                                lr.setMessageType(LogRecord.TEXT);
                            } else {
                                lr.setMessageType("OK MESSAGE");
                            }
                            lr.setDetail(tm.getTextMessage());
                            lr.setNote("");
                            if (temp.isConnected()) {
                                System.out.println("Send Message to : " + reciverID);
                                temp.sendTCP(new SimpleTextMessage(tm.getSenderID(), tm.getSenderName(), tm.getMessageType(), tm.getTextMessage()));
                                lr.setRecived(true);
                            } else {
                                lr.setRecived(false);
                            }
                            if (saveToDB) {
                                DBManager dbmanger = new DBManager();
                                dbmanger.addNewLogRecord(lr);
                            }
                            //   System.out.print("Send Simple Message To user ID : " + reciverID);
                        
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
                            LogRecord lr = new LogRecord();
                            lr.setSenderID(tm.getSenderID());
                            lr.setReciverID(reciverID);
                            if (tm.isLock()) {
                                lr.setMessageType(LogRecord.LOCK);
                            } else {
                                lr.setMessageType(LogRecord.UNLOCK);
                            }

                            lr.setDetail("Lock Reciver Device");
                            lr.setNote("");
                            if (temp.isConnected()) {
                                temp.sendTCP(tm);
                                lr.setRecived(true);
                            } else {
                                lr.setRecived(false);
                            }
                            DBManager dbManager = new DBManager();
                            dbManager.addNewLogRecord(lr);
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

                            LogRecord lr = new LogRecord();
                            lr.setSenderID(tm.getSenderID());
                            lr.setReciverID(reciverID);
                            lr.setMessageType(LogRecord.BOARD);
                            lr.setDetail("Send student's device screen shot");
                            lr.setNote("");
                            if (temp.isConnected()) {
                                temp.sendTCP(tm);
                                lr.setRecived(true);
                            } else {
                                lr.setRecived(true);
                            }
                            DBManager dbManager = new DBManager();
                            dbManager.addNewLogRecord(lr);


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

                            LogRecord lr = new LogRecord();
                            lr.setSenderID(tm.getSenderID());
                            lr.setReciverID(reciverID);
                            lr.setMessageType(LogRecord.MONITORREQUEST);
                            lr.setDetail("Monitor Reciver Device");
                            lr.setNote("");
                            if (temp.isConnected()) {
                                temp.sendTCP(tm);
                                lr.setRecived(true);
                            } else {
                                lr.setRecived(true);
                            }
                            DBManager dbManager = new DBManager();
                            dbManager.addNewLogRecord(lr);
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
                            LogRecord lr = new LogRecord();
                            lr.setSenderID(tm.getSenderID());
                            lr.setReciverID(reciverID);
                            lr.setMessageType(LogRecord.EXAMRESULT);
                            lr.setDetail("send exam result to the techer: ID :" + reciverID);
                            lr.setNote("");
                            if (temp.isConnected()) {
                                System.out.println("Send Result");
                                temp.sendTCP(tm);
                                lr.setRecived(true);
                            } else {
                                //System.out.println("Send Result")
                                lr.setRecived(true);
                            }
                            DBManager dbManager = new DBManager();
                            dbManager.addNewLogRecord(lr);
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

    public static boolean checkIfFileIsImage(String fileName) {

        String ext = null;
        int i = fileName.lastIndexOf('.');
        if (i > 0 && i < (fileName.length() - 1)) {
            ext = fileName.substring(i + 1).toLowerCase();
        }
        if (ext == null) {
            return false;
        } else {
            return !(!ext.equals("jpg") && !ext.equals("jpeg") && !ext.equals("png") && !ext.equals("gif"));
        }
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
