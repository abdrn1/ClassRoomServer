/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testserver;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 *
 * @author Abd
 */
public class BuildFileFromBytesV2 {
      RandomAccessFile aFile = null;
      String[] fileRecivers;
      String SaveDirectoryPath;
    
    public BuildFileFromBytesV2(String SaveDirectoryPath)  {
         this.SaveDirectoryPath=SaveDirectoryPath; 
    }

    public boolean constructFile(FileChunkMessageV2 imMsg) throws FileNotFoundException, IOException {
     
        if(imMsg.getChunkCounter()==(-1L)){ //End OF Fle Packet
            System.out.println("End Of File Packet");
            aFile.close();
            
            return true;  /// return true if the file completed
        }
        if(imMsg.getChunkCounter()==1L){ // New File
               aFile = new RandomAccessFile(SaveDirectoryPath+imMsg.getFileName(), "rw");
               System.out.println("New File Created");
                aFile.write(imMsg.getChunk());
                fileRecivers = imMsg.getRecivers();
        }else{// File data PAcket
           //  System.out.println("Normal packet =" + Long.toString(imMsg.getChunkCounter()));
             
            aFile.write(imMsg.getChunk());
            
        }
        return  false;
    }
    
}
