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
public class FileChunkMessageV2 {

    byte[] chunk;
    String senderID;
    String senderName;
    String[] recivers;
    String fileName;
    long chunkCounter = 0;

    public byte[] getChunk() {
        return chunk;
    }

    public void setChunk(byte[] chunk) {
        this.chunk = chunk;
    }

    public long getChunkCounter() {
        return chunkCounter;
    }

    public void setChunkCounter(long chunkCounter) {
        this.chunkCounter = chunkCounter;
    }



    public FileChunkMessageV2(){
    }

    public FileChunkMessageV2(String sename,String seid ){
        this.senderName= sename;
        this.senderID= seid;
    }

   /* public int getFileSize() {
        return fileSize;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }*/

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }





   /*  public boolean isNewfile() {
        return newfile;
    }

    public void setNewfile(boolean newfile) {
        this.newfile = newfile;
    }*/




    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String[] getRecivers() {
        return recivers;
    }

    public void setRecivers(String[] recivers) {
        this.recivers = recivers;
    }

   /* public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }*/
}
