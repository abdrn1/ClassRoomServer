/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testserver;

/**
 *
 * @author Abd
 */

import Decoder.BASE64Decoder;
import Decoder.BASE64Encoder;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

//import sun.misc.BASE64Decoder;

public class ClassServerV1 {

    ArrayList<UserLogin> usersList;
    Connection teacherConnection;
    BoardViewer myViewer;
    String workingDir;
    DBManager dbManager;
    DBviewer dbViewer;
    boolean dbconnected = false;
    private Hashtable<String, Connection> clientTable;
    private Hashtable<String, ClientStatus> clientStatusTable;
    private Hashtable<String, Listener> ClientsListener;
    private Hashtable<RecivedFileKey, BuildFileFromBytesV2> recivedFilesTable;

    // BuildFileFromBytesV2 buildfromBytesV2;
    public ClassServerV1() {
        workingDir = "";
        workingDir = System.getProperty("user.dir");
        dbViewer = new DBviewer();

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {

        // TODO code application logic here
        ClassServerV1 server = new ClassServerV1();
        server.startServer();
    }

    public void startServer() throws IOException {
        try {
            DBManager.connectDB();
            dbconnected = true;
        } catch (SQLException ex) {
            Logger.getLogger(ClassServerV1.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ClassServerV1.class.getName()).log(Level.SEVERE, null, ex);
        }
        DBManager.setDbViewer(dbViewer);
        usersList = new ArrayList<UserLogin>();
        recivedFilesTable = new Hashtable<>();
        usersList.add(new UserLogin("25", "STUDENT", "طالب (1)", 1));
        usersList.add(new UserLogin("26", "STUDENT", "طالب (2)", 2));
        usersList.add(new UserLogin("27", "STUDENT", "طالب (3)", 4));
        usersList.add(new UserLogin("28", "STUDENT", "طالب (4)", 5));
        usersList.add(new UserLogin("29", "STUDENT", "طالب (5)", 5));
        usersList.add(new UserLogin("30", "STUDENT", "طالب (6)", 5));
        usersList.add(new UserLogin("31", "STUDENT", "طالب (7)", 5));
        usersList.add(new UserLogin("32", "STUDENT", "طالب (8)", 5));
        usersList.add(new UserLogin("33", "STUDENT", "طالب (9)", 5));
        usersList.add(new UserLogin("34", "STUDENT", "Mansour Hosny", 5));
        usersList.add(new UserLogin("100", "TEACHER", "Teacher", 1));
        clientTable = new Hashtable<String, Connection>();
        clientStatusTable = new Hashtable<>();
        ClientsListener = new Hashtable<>();
        System.out.println("Hello There ");

        Server classroomServer = new Server(1024 * 1024, (1024 * 1024) / 10);

        //Server classroomServer = new Server(16384, 8192);
        Kryo kryo = classroomServer.getKryo();
        kryo.register(byte[].class);
        kryo.register(String[].class);
        kryo.register(UserLogin.class);
        kryo.register(TextMeesage.class);
        kryo.register(SimpleTextMessage.class);
        kryo.register(FileChunkMessageV2.class);
        kryo.register(LockMessage.class);
        kryo.register(StatusMessage.class);
        kryo.register(ExamResultMessage.class);
        kryo.register(MonitorRequestMessage.class);
        kryo.register(ScreenshotMessage.class);
        kryo.register(BoardScreenshotMessage.class);
        kryo.register(CapturedImageMessage.class);
        kryo.register(ShowOnBoardMessage.class);
        kryo.register(CommandsMessages.class);

        classroomServer.bind(9995, 54777);
        classroomServer.start();
        System.out.println("Server is now runing");

        classroomServer.addListener(new Listener() {
            @Override
            public void received(Connection c, Object ob) {

                if (ob instanceof UserLogin) {
                    UserLogin ul1 = null;
                    ul1 = findUser((UserLogin) ob);

                    if (ul1 != null) { // ID and password matched

                        // System.out.println("New Client Conenected : "+ul1.getUserID());
                        ul1.setLogin_Succesful(true);
                        c.sendTCP(ul1);
                        LogRecord lr = new LogRecord();
                        lr.setSenderID(ul1.userID);
                        lr.setReciverID("SERVER");
                        lr.setMessageType(LogRecord.LOGIN);

                        lr.setNote("");
                        lr.setRecived(true);

                        if (ul1.getUserType().equals("TEACHER")) {

                            lr.setDetail("Teacher Log In");

                            //  System.out.println("SEND List Of Active Uers TO: " + ul1.getUserName());

                        } else {
                            lr.setDetail("Student Log In");
                            lr.setRecived(true);
                        }
                        sendMeListOfActiveClients(ul1, c);
                        DBManager dbManager = new DBManager();
                        dbManager.addNewLogRecord(lr);
                        System.out.println(ul1.getUserType());
                        SendUtil.broadcastNewUser(clientTable, ul1);
                        // System.out.println("Set Listener To Connection");

                        c.setKeepAliveTCP(6000);
                        c.setTimeout(50000);
                        Connection cc = clientTable.get(ul1.getUserID());
                        if (cc != null) {
                            Listener l1 = ClientsListener.get(ul1.getUserID());
                            cc.removeListener(l1);
                            //      System.out.println("Remove old Listener of : " + ul1.getUserName());
                        }
                        clientTable.put(ul1.getUserID(), c);
                        clientStatusTable.put(ul1.getUserID(), new ClientStatus());
                        setCustomizedConnectionListenter(c, ul1.getUserID());
                        //  System.out.println("Student Login Successfuly: " + ul1.getUserName());
                        //    System.out.println("Count of users : " + clientTable.size());

                        dealWithUnrecivedMessages(ul1.getUserID());
                        System.out.println("NOW Deal with un recived Message: " + ul1.getUserName());

                    } else {
                        ul1 = new UserLogin();
                        ul1.setLogin_Succesful(false);
                        c.sendTCP(ul1);
                        //    System.out.println("Send failed Login Message");
                    }

                }

            }

        });

        dbViewer.pack();
        dbViewer.setVisible(true);
        /// here we denfine way that tcheck if    clients online or offline
        CheckClientsStatus statusChecker = new CheckClientsStatus(clientTable, clientStatusTable, 15000);
        Thread statusCheckerThread = new Thread(statusChecker);
        statusCheckerThread.start();

    }

    private void dealWithUnrecivedMessages(String recID) {
        String messagetype;
        try {
            ResultSet re = DBManager.getMessageOfReaciverID(recID, false);
            SendToReciver sendToReciver = new SendToReciver(clientTable.get("recID"), recID);
            while (re.next()) {
                try {
                    System.out.println("Missed Message for :" + recID);
                    messagetype = re.getString("MessageType");
                    String SenderID = re.getString("senderID");
                    Date MessageTime = re.getDate("MessageTime");
                    String Mdetail = re.getString("detail");
                    //UserLogin sender = findUserBYID(new UserLogin(SenderID, ""));
                    UserLogin sender = findUser(SenderID);

                    // deal with missed mesage type
                    if (messagetype.equals(LogRecord.TEXT)) {
                        System.out.println("Missed Message Type : TEXT ");
                        TextMeesage mTM = new TextMeesage();
                        mTM.setMessageType("TXT");
                        mTM.setSenderID(SenderID);
                        mTM.setSenderName(sender.getUserName());
                        mTM.setTextMessage(Mdetail);
                        mTM.setRecivers(new String[]{recID});

                        SendUtil.sendSimpleMessageToRecivers(clientTable, mTM, false);

                        re.updateBoolean("recived", true);
                        re.updateRow();
                    } // deal with image or file message
                    else if (messagetype.equals(LogRecord.FILE) || messagetype.equals(LogRecord.IMAGE)) {
                        System.out.println("Missed FIlE Message for :" + recID);
                        FileChunkMessageV2 fcm = new FileChunkMessageV2();
                        fcm.setSenderID(SenderID);
                        fcm.setSenderName(sender.getUserName());
                        fcm.setFileName(Mdetail);
                        fcm.setFiletype("FILE");
                        FileSenderThreadV2 missedFSender = new FileSenderThreadV2(clientTable, fcm, new String[]{recID}, workingDir);
                        missedFSender.setSavetodb(false);
                        missedFSender.start();
                        re.updateBoolean("recived", true);
                        re.updateRow();
                    }// deal with exam file
                    else if (messagetype.equals(LogRecord.EXAM)) {
                        System.out.println("Missed EXAM Message for :" + recID);
                        FileChunkMessageV2 fcm = new FileChunkMessageV2();
                        fcm.setSenderID(SenderID);
                        fcm.setSenderName(sender.getUserName());
                        fcm.setFileName(Mdetail);
                        fcm.setFiletype(FileChunkMessageV2.EXAM);
                        FileSenderThreadV2 missedFSender = new FileSenderThreadV2(clientTable, fcm, new String[]{recID}, workingDir);
                        missedFSender.setSavetodb(false);
                        missedFSender.start();
                        re.updateBoolean("recived", true);
                        re.updateRow();

                    } else if (messagetype.equals(LogRecord.CAPTURE)) {
                        CapturedImageMessage cim = convertCapturedFileToMessage(Mdetail);
                        if (cim == null) {
                            System.out.println("No captured file" + recID);
                        } else {
                            cim.setSenderID(SenderID);
                            cim.setRecivers(new String[]{recID});
                            cim.setSenderName(sender.getUserName());
                            SendUtil.sendCapturedImageToRecivers(clientTable, cim, false);
                        }

                    }


                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            System.out.println("End of unrecived Mesage OF: " + recID);

        } catch (SQLException ex) {
            System.out.println("No  database Connection, [Unrecived Messages] ");
            Logger.getLogger(ClassServerV1.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private UserLogin findUser(UserLogin ul) {
        Iterator l = usersList.iterator();

        while (l.hasNext()) {
            UserLogin temp = (UserLogin) l.next();
            if ((temp.getUserID().equals(ul.getUserID())) && (temp.getUserType().equals(ul.getUserType()))) {
                temp.setLogin_Succesful(true);
                return temp;
            }

        }
        return null;
    }


    private UserLogin findUser(String uID) {
        Iterator l = usersList.iterator();

        while (l.hasNext()) {
            UserLogin temp = (UserLogin) l.next();
            if (temp.getUserID().equals(uID)) {
                temp.setLogin_Succesful(true);
                return temp;
            }

        }
        return null;
    }

    private void setCustomizedConnectionListenter(Connection con, String uID) {

        Listener l1 = new Listener() {

            // BuildFileFromBytesV2 buildfromBytesV2 = null;
            String[] tRecivers;

            @Override
            public void received(Connection clientCon, Object clientob) {

                if (clientob instanceof FileChunkMessageV2) {
                    dealWithFileChunckMessageV2((FileChunkMessageV2) clientob);
                    System.out.println("file chunck with conter = " + ((FileChunkMessageV2) clientob).getChunkCounter());

                } else if (clientob instanceof TextMeesage) {
                    System.out.println("New Text Message Recived From :" + ((TextMeesage) clientob).getSenderName());
                    SendUtil.sendSimpleMessageToRecivers(clientTable, (TextMeesage) clientob, true);
                } else if (clientob instanceof LockMessage) {
                    System.out.println("New Lock Message Recived From :" + ((LockMessage) clientob).getSenderName());
                    SendUtil.sendLockMessageToRecivers(clientTable, (LockMessage) clientob);

                } else if (clientob instanceof ExamResultMessage) {
                    System.out.println("New Exam Message Recived From");
                    SendUtil.sendExamResuloRecivers(clientTable, (ExamResultMessage) clientob);
                } else if (clientob instanceof MonitorRequestMessage) {
                    System.out.println("Monitor Request received ... ");

                    SendUtil.sendMonitorRequestToReceiver(clientTable, (MonitorRequestMessage) clientob);
                } else if (clientob instanceof ScreenshotMessage) {
                    System.out.print("Screenshot received at the server");
                    SendUtil.sendScreenshotMessageToReceiver(clientTable, (ScreenshotMessage) clientob);
                } else if (clientob instanceof BoardScreenshotMessage) {

                    dealWithBoardScreenshotMessage((BoardScreenshotMessage) clientob);

                } else if (clientob instanceof CapturedImageMessage) {
                    dealWithCapturedImageMessage((CapturedImageMessage) clientob);
                    System.out.println("Captured Image Recived, then sent to recivers ... ");

                } else if (clientob instanceof ShowOnBoardMessage) {
                    dealWithShowOnBoardMessage((ShowOnBoardMessage) clientob);
                } else if (clientob instanceof CommandsMessages) {

                    if (myViewer != null) {
                        if (myViewer.isVisible()) {

                            CommandsMessages rim = (CommandsMessages) clientob;
                            if (rim.getCommnadType() == 1) {
                                myViewer.rotateImage();
                            } else {
                                myViewer.scalrImageResize(rim.getZoomFactor());
                            }
                        }
                    }

                }

            }
        };

        ClientsListener.put(uID, l1);
        con.addListener(l1);


    }

    private void dealWithBoardScreenshotMessage(BoardScreenshotMessage bsm) {
        System.out.println("Screen shot , on Board Display");

        String encodedImage = bsm.getBase64Photo();
        if (encodedImage != null) {
            try {

                BASE64Decoder decoder = new BASE64Decoder();
                byte[] imageBytes = decoder.decodeBuffer(encodedImage);
                ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
                BufferedImage image = ImageIO.read(bis);
                bis.close();
                ImageIcon vImage = new ImageIcon(image);
                if (myViewer == null) {
                    myViewer = new BoardViewer();
                    myViewer.setTitle("Client: " + bsm.getReceiverId());

                }
                LogRecord lr = new LogRecord();
                lr.setSenderID("Teacher");
                lr.setReciverID("SERVER");
                lr.setMessageType("BOARD");
                lr.setDetail("Display Student Device on Board");
                lr.setNote("");
                lr.setRecived(true);
                DBManager dbManager = new DBManager();
                dbManager.addNewLogRecord(lr);
                myViewer.setImage(vImage);
                myViewer.setAlwaysOnTop(true);
                myViewer.setVisible(true);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // 
    private void dealWithFileChunckMessageV2(FileChunkMessageV2 fcmv2) {
        //LogRecord lr1 = new LogRecord();
        BuildFileFromBytesV2 buildfromBytesV2 = null;
        try {

            if (fcmv2.getChunkCounter() == 1L) {
                buildfromBytesV2 = new BuildFileFromBytesV2(workingDir);
                // buildfromBytesV2.constructFile(fcmv2);
                buildfromBytesV2.fileRecivers = fcmv2.getRecivers();
                recivedFilesTable.put(new RecivedFileKey(fcmv2.senderID, fcmv2.getFileName()), buildfromBytesV2);
                System.out.println("New File Created");
                System.out.println("File sender IS : " + fcmv2.getSenderID());
                LogRecord lr1 = new LogRecord();
                lr1.setReciverID("Server");
                lr1.setSenderID(fcmv2.getSenderID());
                lr1.setMessageType(LogRecord.FILE);
                lr1.setDetail(fcmv2.getFileName());
                lr1.setRecived(false);
                buildfromBytesV2.setLr(lr1);

            } else {
                buildfromBytesV2 = recivedFilesTable.get(new RecivedFileKey(fcmv2.getSenderID(), fcmv2.getFileName()));
            }
            if (buildfromBytesV2 != null) {
                if (buildfromBytesV2.constructFile(fcmv2)) {
                    System.out.println("FILE DONE");
                    buildfromBytesV2.getLr().setRecived(true);
                    buildfromBytesV2.getLr().setNote("");

                    recivedFilesTable.remove(new RecivedFileKey(fcmv2.getSenderID(), fcmv2.getFileName()));
                    FileSenderThreadV2 fsv2 = new FileSenderThreadV2(clientTable, fcmv2, buildfromBytesV2.fileRecivers,
                            workingDir);

                    System.out.println("file recived Completly, Start TO send File To the Recivers");
                    System.out.println(fcmv2.getFileName());
                    System.out.println(fcmv2.getSenderID());
                    DBManager dbm = new DBManager();
                    dbm.addNewLogRecord(buildfromBytesV2.getLr());
                    fsv2.start();
                    System.out.println("Start Thread to Send File To the Recivets");
                }
            }


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void dealWithShowOnBoardMessage(ShowOnBoardMessage sbm) {
        System.out.println("workingDir + sbm.getFileName()");
        ImageIcon vImage = new ImageIcon(workingDir + "/" + sbm.getFileName());
        if (myViewer == null) {
            myViewer = new BoardViewer();
            //   myViewer.setTitle("Client: " + ((BoardScreenshotMessage) clientob).getReceiverId());
            myViewer.setTitle("VIewer");

        }
        LogRecord lr = new LogRecord();
        lr.setSenderID(sbm.getSenderID());
        lr.setReciverID("SERVER");
        lr.setMessageType("IMG ON BOARD");
        lr.setDetail("Display Student Device on Board");
        lr.setNote("");
        lr.setRecived(true);
        DBManager dbManager = new DBManager();
        dbManager.addNewLogRecord(lr);
        myViewer.setImage(vImage);
        myViewer.setAlwaysOnTop(true);
        myViewer.repaint();
        myViewer.setVisible(true);
    }

    private void dealWithCapturedImageMessage(CapturedImageMessage cim) {
        SendUtil.sendCapturedImageToRecivers(clientTable, cim, true);
        BASE64Decoder decoder = new BASE64Decoder();
        try {
            byte[] imageBytes = decoder.decodeBuffer(cim.getPicture());
            writeByteImageTofile(imageBytes, cim.getFileName());
        } catch (Exception ex) {
            System.out.println("unable to write captured  image to disk ");
            Logger.getLogger(ClassServerV1.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private CapturedImageMessage convertCapturedFileToMessage(String CapturedFileNmae) {
        BASE64Encoder encoder = new BASE64Encoder();
        File imgFile = new File(workingDir + "/" + CapturedFileNmae);
        byte[] imageBytes;
        try {
            imageBytes = FileUtils.readFileToByteArray(imgFile);

            String encodedImage = encoder.encode(imageBytes);
            CapturedImageMessage cim = new CapturedImageMessage();

            cim.setPicture(encodedImage);
            cim.setFileName(CapturedFileNmae);

            return cim;

        } catch (IOException ex) {
            Logger.getLogger(ClassServerV1.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private void writeByteImageTofile(byte[] imageBytes, String imagefileName) throws Exception {

        System.out.println("save Captured image to : " + workingDir + "," + imagefileName);
        File destination = new File(workingDir, imagefileName);
        FileOutputStream fo;

        destination.createNewFile();
        fo = new FileOutputStream(destination);
        fo.write(imageBytes);
        fo.close();
        //Toast.makeText(getApplicationContext(), "Write IMGE DONEe", Toast.LENGTH_SHORT).show();
    }

    public void sendMeListOfActiveClients(UserLogin me, Connection myconnection) {
        System.out.println("Send Lsit Of Active Clients");
        Set set = clientTable.entrySet();
        System.out.println("Count  of current clients = : " + clientTable.size());
        // Get an iterator
        Iterator i = set.iterator();
        // UserLogin tempuul1 = new UserLogin();
        // Display elements
        while (i.hasNext()) {
            Map.Entry men = (Map.Entry) i.next();
            Connection temp = (Connection) men.getValue();
            String senderID = (String) men.getKey();
            //  tempuul1.setUserID(senderID);
            if (!(senderID.equals(me.getUserID()))) {

                UserLogin currActive = findUser(senderID);
                if (currActive != null) {
                    System.out.println("Send Client : " + currActive.getUserID() + "TO" + me.getUserID());
                    if (temp.isConnected()) {
                        currActive.setMyStatus(0);
                    } else {
                        currActive.setMyStatus(1);
                    }
                    myconnection.sendTCP(currActive);
                }

            }

        }

    }

}
