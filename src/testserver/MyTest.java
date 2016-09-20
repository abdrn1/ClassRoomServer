/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testserver;

import java.sql.SQLException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Abd
 */
public class MyTest {
    static DBManager dbManger;

    public static void main(String args[]) {
        if (showre()) {

            System.out.println("OK done)");
            Date s = new Date(System.currentTimeMillis());
            System.out.println("TIme is)" + s.toString());
            LogRecord lr = new LogRecord();
            lr.setSenderID("25");
            lr.setReciverID("24");
            lr.setMessageTime(new Date(System.currentTimeMillis()));
            lr.setMessageType("Image");
            lr.setDetail("jfkjd.jpg");
            lr.setNote("");
            lr.setRecived(true);
            try {
                DBManager.connectDB();
                dbManger = new DBManager();
                dbManger.addNewLogRecord(lr);
            } catch (SQLException ex) {
                Logger.getLogger(MyTest.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(MyTest.class.getName()).log(Level.SEVERE, null, ex);
            }


        }
    }


    public static boolean showre() {

        for (int i = 0; i < 10; i++) {
            if (i == 5) {
                return true;
            }
        }

        return false;
    }

}
