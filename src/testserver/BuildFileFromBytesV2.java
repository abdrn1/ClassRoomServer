/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testserver;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 *
 * @author Abd
 */
public class BuildFileFromBytesV2 {
    final static int bufferSize = 2000;
      RandomAccessFile aFile = null;
      String[] fileRecivers;
      String SaveDirectoryPath;
    boolean fileDone = false;
    private LogRecord lr;
    
    public BuildFileFromBytesV2(String SaveDirectoryPath)  {
         this.SaveDirectoryPath=SaveDirectoryPath; 
    }


    public boolean constructFile(FileChunkMessageV2 imMsg) throws IOException {
     
        if(imMsg.getChunkCounter()==(-1L)){ //End OF Fle Packet
            System.out.println("End Of File Packets");
            aFile.close();
            fileDone = true;
            return true;  /// return true if the file completed
        }
        if (imMsg.getChunkCounter() == 1L) {
// New File
            System.out.println("New File Recived From : " + imMsg.getSenderName());
            aFile = new RandomAccessFile(SaveDirectoryPath + "/" + imMsg.getFileName(), "rw");
             
                aFile.write(imMsg.getChunk());
                fileRecivers = imMsg.getRecivers();
        } else {// File data PAcket
            System.out.println("Old File Recived From : " + imMsg.getSenderName() + " Counter =" + imMsg.getChunkCounter());
            aFile.seek((bufferSize * (imMsg.getChunkCounter() - 1)));
            aFile.write(imMsg.getChunk());
        }
        return  false;
    }

    /**
     * @return the lr
     */
    public LogRecord getLr() {
        return lr;
    }

    /**
     * @param lr the lr to set
     */
    public void setLr(LogRecord lr) {
        this.lr = lr;
    }
    
}
