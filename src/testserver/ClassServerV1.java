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
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import java.io.IOException;
import com.esotericsoftware.kryo.Kryo;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ClassServerV1 {

    private HashMap<String, Connection> clientTable;
    ArrayList<UserLogin> usersList;
    Connection teacherConnection;
   
    // BuildFileFromBytesV2 buildfromBytesV2;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {

        // TODO code application logic here
        ClassServerV1 server = new ClassServerV1();
        server.startServer();
    }

    public void startServer() throws IOException {
        usersList = new ArrayList<UserLogin>();
        usersList.add(new UserLogin("25", "STUDENT", "ABD RADWAN", 1));
        usersList.add(new UserLogin("26", "STUDENT", "ALI HASSAN", 2));
        usersList.add(new UserLogin("27", "STUDENT", "MAHMOUD NAJE", 4));
        usersList.add(new UserLogin("28", "STUDENT", "MAHMOUD NAJE", 5));
        usersList.add(new UserLogin("100", "TEACHER", "AONY AHMED", 1));
        clientTable = new HashMap<String, Connection>();
        System.out.println("Hello There ");
        Server classroomServer = new Server(16384,8192);
        Kryo kryo = classroomServer.getKryo();

        kryo.register(byte[].class);
        kryo.register(String[].class);
        kryo.register(UserLogin.class);
        kryo.register(TextMeesage.class);
        kryo.register(SimpleTextMessage.class);
        kryo.register(FileChunkMessageV2.class);

        classroomServer.bind(9995, 54777);
        classroomServer.start();
        System.out.println("Server is now runing");

        classroomServer.addListener(new Listener() {
            @Override
            public void received(Connection c, Object ob) {

                if (ob instanceof UserLogin) {
                    UserLogin ul1 = null;
                    ul1 = findUser((UserLogin) ob);

                    // ID and password matched
                    if (ul1 != null) {
                        c.sendTCP(ul1);
                        System.out.println(ul1.getUserType());
                        //Set Techer Connection
                        if (ul1.getUserType().equals("TEACHER")) {
                            teacherConnection = c;
                            /// SendUtil.broadcastNewUser(clientTable, ul1);
                            ///clientTable.put(ul1.getUserID(), c);
                            System.out.println("Teacher Login Successfuly");
                        }
                        // Student login
                        SendUtil.broadcastNewUser(clientTable, ul1);
                        //teacherConnection.sendTCP(ul1);
                        System.out.println("Set Listener To Connection");
                        c.addListener(new Listener() {
                            
                            BuildFileFromBytesV2 buildfromBytesV2 = null;
                            String[] tRecivers;
                            @Override
                            public void received(Connection c, Object ob) {
                                //System.out.println("Hello There");
                                if (ob instanceof FileChunkMessageV2) {
                                    try {
                                        
                                        FileChunkMessageV2 fcmv2 = (FileChunkMessageV2) ob;
                                        //recive the first packet from new file 
                                        if (fcmv2.getChunkCounter() == 1L) {
                                            buildfromBytesV2 = new BuildFileFromBytesV2("E:/");
                                            tRecivers= fcmv2.getRecivers();
                                            buildfromBytesV2.constructFile(fcmv2);
                                          //  SendUtil.sendFileChunkToRecivers(clientTable, fcmv2, tRecivers);
                                            
                                        } else if (buildfromBytesV2 != null) {
                                          if(  buildfromBytesV2.constructFile(fcmv2)){
                                              System.out.println("End OF REcive, Start TO send File To the Recivers");
                                              System.out.println(fcmv2.getFileName());
                                              System.out.println(fcmv2.getSenderID());
                                              FileSenderThreadV2 fsv2 = new FileSenderThreadV2(clientTable, fcmv2, tRecivers,"E:/");
                                              //Thread t = new Thread(fsv2);
                                              //t.start();
                                              fsv2.start();
                                              
                                              System.out.println("Start Thread to Send File To the Recivets");
                                          }
                                          /// SendUtil.sendFileChunkToRecivers(clientTable, fcmv2, tRecivers);
                                        }
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }

                                }

                            }
                        });

                        clientTable.put(ul1.getUserID(), c);
                        System.out.println("Student Login Successfuly: " + ul1.getUserName());

                    } else {
                        ul1 = new UserLogin();
                        ul1.setLogin_Succesful(false);
                        c.sendTCP(ul1);
                        System.out.println("Send failed Login Message");
                    }

                }  else if (ob instanceof TextMeesage) {
                    System.out.println("New Text Message Recived From :" + ((TextMeesage) ob).getSenderName());
                    SendUtil.sendSimpleMessageToRecivers(clientTable, (TextMeesage) ob);
                } 
                /*else if (ob instanceof FileChunkMessageV2) {
                    try {
                        FileChunkMessageV2 fcmv2 = (FileChunkMessageV2) ob;
                        System.out.println("Chunk Counter Equal " + fcmv2.getChunkCounter());

                        if (fcmv2.getChunkCounter() == 1L) {
                            buildfromBytesV2 = new BuildFileFromBytesV2();
                            buildfromBytesV2.constructFile(fcmv2);
                        } else {
                            buildfromBytesV2.constructFile(fcmv2);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                }*/
            }

        });

    }

    private UserLogin findUser(UserLogin ul) {
        Iterator l = usersList.iterator();

        while (l.hasNext()) {
            UserLogin temp = (UserLogin) l.next();
            if (temp.getUserID().equals(ul.getUserID())) {
                temp.setLogin_Succesful(true);
                return temp;
            }

        }
        return null;
    }
}
