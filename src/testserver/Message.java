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
public interface Message {
    public String getSenderID();
    public void setSenderID(String senderID);
    public String getSenderName();
    public void setSenderName(String senderName);
    public String[] getRecivers();
    public void setRecivers(String[] recivers);
    public String getMessageType();
    public void setMessageType(String messageType);


}
