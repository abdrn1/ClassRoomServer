/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testserver;

import com.esotericsoftware.kryonet.Connection;

import java.util.Date;

/**
 * @author Abd
 */
public class SendToReciver {

    Connection c;
    String ReciverID;

    public SendToReciver(Connection cc, String ReciverID) {
        this.c = cc;
        this.ReciverID = ReciverID;

    }


    public void sendUnrecivedTextMessage(String SenderID, String SenderName, String Message, Date mTime) {
        SimpleTextMessage sm = new SimpleTextMessage();
        sm.setSenderID(SenderID);
        sm.setSenderName(SenderName);
        sm.setMessageType("TXT");
        sm.setTextMessage(Message);
        c.sendTCP(sm);
    }


}
