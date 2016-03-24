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


/**
 *
 * @author Abd
 */
public class FileSenderThreadV2 extends Thread{

    HashMap clientTablel;
    FileChunkMessageV2 fcv2;
    String[] tRecivers;
    String savedFilesDirectory;
    
    public FileSenderThreadV2(HashMap clientTable, FileChunkMessageV2 fcv2, String[] tRecivers,String savedFilesDirectory){
        this.clientTablel=clientTable;
        this.fcv2 =fcv2;
        this.tRecivers =tRecivers;
        this.savedFilesDirectory = savedFilesDirectory;
    }

    @Override
    public void run() {
        SendFileToRecivers(clientTablel, fcv2, tRecivers);
    }

    
        public  void SendFileToRecivers(HashMap clientTable, FileChunkMessageV2 fcv2, String[] tRecivers) {
        RandomAccessFile aFile = null;
        long chunkcounter;
        int bufferSize = 1024;
       if (tRecivers != null) {

            try {
                aFile = new RandomAccessFile(savedFilesDirectory + fcv2.getFileName(), "r");
                //    System.err.println("PATH :"+ savedFilesDirectory + fcv2.getFileName());
                chunkcounter = 0;
                java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream();;
                byte[] buf = new byte[bufferSize];
                     System.out.println("Start Loop To send Chunks");
                for (int readNum; (readNum = aFile.read(buf)) != -1;) {
                       
                    chunkcounter++;
                    //bos = new ByteArrayOutputStream();
                    
                    bos.write(buf, 0, readNum);
                    byte[] currentchunk = bos.toByteArray();
                    //bos = new java.io.ByteArrayOutputStream();
                    FileChunkMessageV2 chunkFromFile = new FileChunkMessageV2();
                    chunkFromFile.setSenderName(fcv2.getSenderName());
                    chunkFromFile.setSenderID(fcv2.getSenderID());
                    chunkFromFile.setChunkCounter(chunkcounter);
                    chunkFromFile.setChunk(currentchunk);
                    chunkFromFile.setFileName(fcv2.getFileName());
                    chunkFromFile.setFiletype(fcv2.getFiletype());
                    
                    // method Check recivers and send them chunk of file
                    System.out.println("Send Packet To Recivers with ID: "+chunkFromFile.getChunkCounter());
                    sendFileChunkToRecivers(clientTable, chunkFromFile, tRecivers);
                    bos.reset();       
                }

                // send end of file Packet
                FileChunkMessageV2 endofFile = new FileChunkMessageV2();
                endofFile.setSenderName(fcv2.getSenderName());
                endofFile.setSenderID(fcv2.getSenderID());
                endofFile.setChunkCounter((-1L));
                endofFile.setFileName(fcv2.getFileName());
                endofFile.setFiletype(fcv2.getFiletype());
                System.out.println("Send End Of File To recivers");
                sendFileChunkToRecivers(clientTable, endofFile, tRecivers);
                aFile.close();
            } catch (Exception e) {

                e.printStackTrace();

            }
        }
        
        System.out.println("End of sending File");
    }
    public void sendFileChunkToRecivers(HashMap clientTable, FileChunkMessageV2 fcv2, String[] tRecivers) throws InterruptedException {
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
                        //System.out.println("Current Chunk Size is :" + fcv2.getChunk().length );
                        if(temp.isConnected()){
                            
                            // if the buffer full wait until free some buffer
                         while(temp.getTcpWriteBufferSize()>8000){
                             sleep(10);
                         }
                          temp.sendTCP(fcv2); 
                        }else{
                            System.out.println("Connection IS down");
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
