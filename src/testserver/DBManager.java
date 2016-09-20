/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testserver;

/**
 * @author Abd
 */

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;


public class DBManager {

    static Connection con;
    static DBviewer dbViewer;

    public DBManager() {


    }

    /**
     * @return the dbViewer
     */
    public static DBviewer getDbViewer() {
        return dbViewer;
    }

    /**
     * @param aDbViewer the dbViewer to set
     */
    public static void setDbViewer(DBviewer aDbViewer) {
        dbViewer = aDbViewer;
    }

    public static void connectDB() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.jdbc.Driver");
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cmsdb?characterEncoding=utf8", "root", "");
    }

    public static ResultSet getMessageOfReaciverID(String ReciverID, boolean recived) throws SQLException {

        String query = "Select * from logrecords where ReciverID like ? and recived =?  order by MessageTime DESC ";
        PreparedStatement stmt = con.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_UPDATABLE,
                ResultSet.HOLD_CURSORS_OVER_COMMIT);
        stmt.setString(1, ReciverID);
        stmt.setBoolean(2, recived);

        ResultSet re = stmt.executeQuery();
        return re;
    }

    public static void updateTorecived(String senderiD, String recID, java.util.Date mTime, boolean recived) throws SQLException {

        String query = "update  logrecord set recived =? where ReciverID =? and senderid=? and messagetime=? ";
        PreparedStatement stmt = con.prepareCall(query);
        stmt.setBoolean(1, recived);
        stmt.setString(2, recID);
        stmt.setString(3, senderiD);
        stmt.setTimestamp(4, new Timestamp(mTime.getTime()));
        stmt.executeUpdate();
    }

    public void addNewLogRecord(LogRecord lr) {
        try {
            String insertSql = "Insert Into logrecords (senderID,ReciverID,Messagetime,Messagetype,detail,Note,Recived)"
                    + " values (?,?,?,?,?,?,?)";
            PreparedStatement insertSt = con.prepareStatement(insertSql);
            insertSt.setString(1, lr.getSenderID());
            System.out.println("SQL :Sender ID =  " + lr.getSenderID());
            insertSt.setString(2, lr.getReciverID());
            lr.setMessageTime(new Timestamp(System.currentTimeMillis()));
            insertSt.setTimestamp(3, new Timestamp(lr.getMessageTime().getTime()));
            insertSt.setString(4, lr.getMessageType());
            insertSt.setString(5, lr.getDetail());
            insertSt.setString(6, lr.getNote());
            insertSt.setBoolean(7, lr.isRecived());
            insertSt.execute();
            if (dbViewer != null) {
                dbViewer.addNewRecord(lr);
            }
        } catch (SQLException ex) {
            Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
