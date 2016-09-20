/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testserver;

import java.util.Date;

/**
 * @author Abd
 */

public class LogRecord {

    final static String FILE = "FILE";
    final static String IMAGE = "IMAGE";
    final static String TEXT = "TEXT";
    final static String EXAM = "EXAM";
    final static String EXAMRESULT = "EXAM RESULT";
    final static String LOCK = "LOCK DEVICE";
    final static String UNLOCK = "UNLOCK DEVICE";
    final static String BOARD = "STUDENT ON BOARD";
    final static String IMGBOARD = "IMAGE ON BOARD";
    final static String CAPTURE = "CAPTURED IMAGE";
    final static String MONITORREQUEST = "MONITOR REQUEST";
    final static String LOGIN = "LOG IN";

    private String senderID;
    private String reciverID;
    private Date messageTime;
    private String messageType;
    private String detail;
    private String Note;
    private boolean recived;

    /**
     * @return the senderID
     */
    public String getSenderID() {
        return senderID;
    }

    /**
     * @param senderID the senderID to set
     */
    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    /**
     * @return the reciverID
     */
    public String getReciverID() {
        return reciverID;
    }

    /**
     * @param reciverID the reciverID to set
     */
    public void setReciverID(String reciverID) {
        this.reciverID = reciverID;
    }

    /**
     * @return the messageTime
     */
    public Date getMessageTime() {
        return messageTime;
    }

    /**
     * @param messageTime the messageTime to set
     */
    public void setMessageTime(Date messageTime) {
        this.messageTime = messageTime;
    }

    /**
     * @return the messageType
     */
    public String getMessageType() {
        return messageType;
    }

    /**
     * @param messageType the messageType to set
     */
    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    /**
     * @return the detail
     */
    public String getDetail() {
        return detail;
    }

    /**
     * @param detail the detail to set
     */
    public void setDetail(String detail) {
        this.detail = detail;
    }

    /**
     * @return the Note
     */
    public String getNote() {
        return Note;
    }

    /**
     * @param Note the Note to set
     */
    public void setNote(String Note) {
        this.Note = Note;
    }

    /**
     * @return the recived
     */
    public boolean isRecived() {
        return recived;
    }

    /**
     * @param recived the recived to set
     */
    public void setRecived(boolean recived) {
        this.recived = recived;
    }


}
