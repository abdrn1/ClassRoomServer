/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testserver;

import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Method;
import org.imgscalr.Scalr.Mode;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Abd
 */
public class BoardViewer extends javax.swing.JFrame {

    /**
     * Creates new form BoardViewer
     */
    private int imgWidth;
    private int imgHieght;
    private BufferedImage currentImage;
    private BufferedImage editedImage;
    private ImageIcon currentIco;
    private ImageIcon resetIco;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel imgLabel;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;

    public BoardViewer() {

        currentIco = new ImageIcon();
        initComponents();
        setExtendedState(java.awt.Frame.MAXIMIZED_BOTH);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(BoardViewer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(BoardViewer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(BoardViewer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(BoardViewer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                BoardViewer aa = new BoardViewer();
                // aa.setImage(null);
                //aa.setCurrentImage();
                //aa.scalrImageResize(0.9);
                aa.setImage(null);
                aa.setVisible(true);

            }
        });
    }

    public void iniGui() {

    }

    public void setCurrentImage() {
        try {
            File imageFile = new File("C:\\Users\\Abd\\Documents\\NetBeansProjects\\ClassRoomServer\\CaptureCMS_٢٠١٦١٠٢٧١٠١٠٣٥.jpg");
            currentImage = ImageIO.read(imageFile);
            imgHieght = currentImage.getHeight();
            imgWidth = currentImage.getWidth();
            currentIco.setImage(currentImage);
            editedImage = currentImage;
            imgLabel.setIcon(currentIco);

            repaint();
        } catch (IOException ex) {
            Logger.getLogger(BoardViewer.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void setImage(ImageIcon img) {
        //ImageIcon aa = new ImageIcon("C:\\Users\\Abd\\Documents\\NetBeansProjects\\ClassRoomServer\\CaptureCMS_٢٠١٦١٠٢٧١٠١٠٣٥.jpg");
        resetIco = img;
        this.imgLabel.setIcon(img);
        Image temp = img.getImage();
        editedImage = new BufferedImage(temp.getWidth(null), temp.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D bGr = editedImage.createGraphics();
        bGr.drawImage(temp, 0, 0, null);
        bGr.dispose();
        repaint();

    }

    public void scalrImageResize(double zoom) {

        //ImageIcon ico = new ImageIcon("C:\\Users\\Abd\\Documents\\NetBeansProjects\\ClassRoomServer\\CaptureCMS_٢٠١٦١٠٢٧١٠١٠٣٥.jpg");
        // BufferedImage opImage = currentImage;
        int w = (int) (editedImage.getWidth() * zoom);
        int h = (int) (editedImage.getHeight() * zoom);
        editedImage = Scalr.resize(editedImage,
                Method.ULTRA_QUALITY,
                Mode.AUTOMATIC,
                w, h,
                Scalr.OP_ANTIALIAS);

        currentIco.setImage(editedImage);
        imgLabel.setIcon(currentIco);
        repaint();
        jScrollPane1.updateUI();
    }

    public void rotateImage() {

        editedImage = Scalr.rotate(editedImage, Scalr.Rotation.CW_90, Scalr.OP_ANTIALIAS);
        currentIco.setImage(editedImage);
        imgLabel.setIcon(currentIco);
        repaint();

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel4 = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        imgLabel = new javax.swing.JLabel();

        setTitle("Viewer");
        setAlwaysOnTop(true);
        setExtendedState(1);

        jPanel4.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                jPanel4ComponentResized(evt);
            }
        });
        jPanel4.setLayout(null);

        jButton2.setText("Reset");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel4.add(jButton2);
        jButton2.setBounds(10, 10, 90, 23);

        jButton3.setText("Zoom IN");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        jPanel4.add(jButton3);
        jButton3.setBounds(10, 30, 90, 23);

        jButton4.setText("Rotate");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        jPanel4.add(jButton4);
        jButton4.setBounds(10, 70, 90, 23);

        jButton5.setText("Zoom OUT");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
        jPanel4.add(jButton5);
        jButton5.setBounds(10, 50, 90, 23);

        getContentPane().add(jPanel4, java.awt.BorderLayout.WEST);

        imgLabel.setToolTipText("");
        jScrollPane1.setViewportView(imgLabel);

        getContentPane().add(jScrollPane1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jPanel4ComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jPanel4ComponentResized
        // TODO add your handling code here:


    }//GEN-LAST:event_jPanel4ComponentResized

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:\
        setImage(resetIco);

        //  scalrImageResize(0.9);

    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        //  rotateImage
        scalrImageResize(1.2);
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
        scalrImageResize(0.9);
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        rotateImage();
    }//GEN-LAST:event_jButton4ActionPerformed
    // End of variables declaration//GEN-END:variables
}
