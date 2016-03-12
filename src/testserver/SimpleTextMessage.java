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
public class SimpleTextMessage implements SimpleMessage{
    String senderID;
    String senderName;
    String messageType;
    String textMessage;
    
    public SimpleTextMessage() {
    }

    public SimpleTextMessage(String senderID, String senderName, String messageType, String textMessage) {
        this.senderID = senderID;
        this.senderName = senderName;
        this.messageType = messageType;
        this.textMessage = textMessage;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

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

    public String getTextMessage() {
        return textMessage;
    }

    public void setTextMessage(String textMessage) {
        this.textMessage = textMessage;
    }
}
