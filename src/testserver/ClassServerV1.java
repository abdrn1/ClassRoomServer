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

import java.util.*;

public class ClassServerV1 {

	private Hashtable<String, Connection> clientTable;
	ArrayList<UserLogin> usersList;
	Connection teacherConnection;

	// BuildFileFromBytesV2 buildfromBytesV2;

	/**
	 * @param args
	 *            the command line arguments
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
		clientTable = new Hashtable<String, Connection>();
		System.out.println("Hello There ");
		Server classroomServer = new Server(16384, 8192);
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
						ul1.setLogin_Succesful(true);
						c.sendTCP(ul1);
						if (ul1.getUserType().equals("TEACHER")) {
							System.out.println("SEND List Of Active Uers TO: " + ul1.getUserName());
							sendMeListOfActiveClients(ul1, c);
						}
						System.out.println(ul1.getUserType());
						SendUtil.broadcastNewUser(clientTable, ul1);
						System.out.println("Set Listener To Connection");
						clientTable.put(ul1.getUserID(), c);

						setCustomizedConnectionListenter(c);

                        System.out.println("Student Login Successfuly: " + ul1.getUserName());

					} else {
						ul1 = new UserLogin();
						ul1.setLogin_Succesful(false);
						c.sendTCP(ul1);
						System.out.println("Send failed Login Message");
					}

				}

			}

		});
        /// here we denfine way that tcheck if clients online or offline
		CheckClientsStatus statusChecker = new CheckClientsStatus(clientTable,15000);
		Thread statusCheckerThread = new Thread(statusChecker);
		 //statusCheckerThread.start();

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

	private void setCustomizedConnectionListenter(Connection con) {
		con.addListener(new Listener() {

			BuildFileFromBytesV2 buildfromBytesV2 = null;
			String[] tRecivers;

			@Override
			public void received(Connection clientCon, Object clientob) {
				// System.out.println("Hello There");
				if (clientob instanceof FileChunkMessageV2) {
					try {

						FileChunkMessageV2 fcmv2 = (FileChunkMessageV2) clientob;
						String workingDir = System.getProperty("user.dir");

						//System.out.println("working dir is: " + workingDir);
						// recive the //first packet from new file

						if (fcmv2.getChunkCounter() == 1L) {
							buildfromBytesV2 = new BuildFileFromBytesV2(workingDir);
							tRecivers = fcmv2.getRecivers();
							buildfromBytesV2.constructFile(fcmv2);
                            System.out.println("New File Chunk Recived");
							// SendUtil.sendFileChunkToRecivers(clientTable,
							// fcmv2, tRecivers);

						} else if (buildfromBytesV2 != null) {
							if (buildfromBytesV2.constructFile(fcmv2)) {
								System.out.println("file recived Completly, Start TO send File To the Recivers");
								System.out.println(fcmv2.getFileName());
								System.out.println(fcmv2.getSenderID());
								FileSenderThreadV2 fsv2 = new FileSenderThreadV2(clientTable, fcmv2, tRecivers,
										workingDir);
								// Thread t = new Thread(fsv2);
								// t.start();
								fsv2.start();

								System.out.println("Start Thread to Send File To the Recivets");
							}
							/// SendUtil.sendFileChunkToRecivers(clientTable,
							/// fcmv2, tRecivers);
						}

					} catch (Exception ex) {
						ex.printStackTrace();
					}

				} else if (clientob instanceof TextMeesage) {
					System.out.println("New Text Message Recived From :" + ((TextMeesage) clientob).getSenderName());
					SendUtil.sendSimpleMessageToRecivers(clientTable, (TextMeesage) clientob);
				} else if (clientob instanceof LockMessage) {
					System.out.println("New Lock Message Recived From :" + ((LockMessage) clientob).getSenderName());
					SendUtil.sendLockMessageToRecivers(clientTable, (LockMessage) clientob);

				}else if (clientob instanceof  ExamResultMessage){
					System.out.println("New Lock Message Recived From");
					SendUtil.sendExamResuloRecivers(clientTable,(ExamResultMessage)clientob);
				}

			}
		});
	}



	public void sendMeListOfActiveClients(UserLogin me, Connection myconnection) {
		Set set = clientTable.entrySet();
		// Get an iterator
		Iterator i = set.iterator();
		UserLogin tempuul1 = new UserLogin();
		// Display elements
		while (i.hasNext()) {
			Map.Entry men = (Map.Entry) i.next();
			Connection temp = (Connection) men.getValue();
			String senderID = (String) men.getKey();
			tempuul1.setUserID(senderID);
			if (!(senderID.equals(me.getUserID()))) {

				UserLogin currActive = findUser(tempuul1);
				if (currActive != null) {
					if (temp.isConnected()) {
						myconnection.sendTCP(currActive);
					}
				}

			}

		}

	}

}
