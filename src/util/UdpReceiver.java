package util;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * Small UDP packet receiver.
 * 
 * @author  Stefan Marks
 * @version 1.0 - 15.06.2013: Created
 */
public class UdpReceiver extends javax.swing.JFrame
{
    /**
     * Creates a new UDP Receiver Form
     */
    public UdpReceiver()
    {
        initComponents();
        setLocationRelativeTo(null);
    }

    private void startListening()
    {
        synchronized(txtReceivedData)
        {
            append("Starting Listener...\n");
        }
        receiverThread = new ReceiverThread();
        if ( (receiverThread != null) && receiverThread.isRunning() )
        {
            receiverThread.start();
        }
    }
    
    private void stopListening()
    {
        synchronized(txtReceivedData)
        {
            append("Stopping Listener...\n");
        }
        if ( receiverThread != null )
        {
            receiverThread.stopReceiving();
            receiverThread = null;
        }
    }
    
    private void append(String s)
    {
        txtReceivedData.append(s);
        String text = txtReceivedData.getText();
        int    tlen = text.length();
        if ( tlen > 65536 )
        {
            int nextLF = text.indexOf("\n", tlen - 65536);
            if ( nextLF > -1 )
            {
                txtReceivedData.replaceRange("", 0, nextLF + 1);
                tlen -= nextLF + 1;
            }
        }
        txtReceivedData.setCaretPosition(tlen);        
    }
        

    private class ReceiverThread extends Thread
    {
        public ReceiverThread()
        {
            running = false;
            receiveBuffer = new byte[2048];
            int port = (int) spnPort.getValue();
            try
            {
                socket = chkLocalhost.isSelected() ? 
                            new DatagramSocket(port, InetAddress.getLoopbackAddress()) :
                            new DatagramSocket(port, InetAddress.getLocalHost());
                socket.setSoTimeout(100);
                String localIP = socket.getLocalAddress().toString();
                append(String.format("Listening on UDP:%s:%d%n",
                    localIP, port));       
                running = true;
            }
            catch ( SocketException | UnknownHostException ex )
            {
                append("Error while opening socket: " + ex + "\n");
                if ( socket != null )
                {
                    socket.close();
                    socket = null;
                }
            }
        }
        
        @Override
        public void run()
        {
            DatagramPacket receivePacket = 
                new DatagramPacket(receiveBuffer, receiveBuffer.length);
            append("Receiver Thread started\n");
            spnPort.setEnabled(false);
            btnListen.setSelected(true);
            
            while ( running )
            {
                try
                {
                    socket.receive(receivePacket);
                    byte[] data  = receivePacket.getData();
                    int    len   = receivePacket.getLength();

                    synchronized(txtReceivedData)
                    {
                        append("Receiving data packet (" + len + " Bytes):\n");

                        if ( btnHexText.isSelected() )
                        {
                            String ascii = "";
                            for ( int i = 0 ; i < (len / 16 + 1) * 16 ; i++ )
                            {
                                if ( i % 16 == 0 )
                                {
                                    append(String.format("%04X:  ", i));
                                    ascii = "";
                                }

                                if ( i < len ) 
                                {
                                    byte b = data[i];
                                    append(String.format("%02X ", b));
                                    ascii += ((b >= ' ') && (b <= '~')) ? (char) b : '.';
                                }
                                else
                                {
                                    append("   ");
                                }

                                if ( i % 16 == 15 )
                                {
                                    append("   " + ascii + '\n');
                                    ascii = "";
                                }                            
                            }
                        }
                        else
                        {
                            String text = new String(data);
                            append("\"" + text + "\"\n");
                        }
                    }
                }
                catch ( SocketTimeoutException ex )
                {
                    // ignore
                }
                catch ( IOException ex )
                {
                    append("Error while receiving: " + ex + "\n");
                    running = false;
                }
            }
            
            socket.close();
            
            btnListen.setSelected(false);
            spnPort.setEnabled(true);
            append("Receiver Thread stopped\n");      
        }
    
        public void stopReceiving()
        {
            running = false;
        }
        
        public boolean isRunning()
        {
            return running;
        }
        
        private boolean        running;
        private byte[ ]        receiveBuffer;
        private DatagramSocket socket;
    }


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {
        java.awt.GridBagConstraints gridBagConstraints;

        pnlSettings = new javax.swing.JPanel();
        javax.swing.JLabel lblPort = new javax.swing.JLabel();
        spnPort = new javax.swing.JSpinner();
        chkLocalhost = new javax.swing.JCheckBox();
        javax.swing.JPanel pnlSpacer = new javax.swing.JPanel();
        btnListen = new javax.swing.JToggleButton();
        btnHexText = new javax.swing.JToggleButton();
        btnClear = new javax.swing.JButton();
        javax.swing.JPanel pnlOutput = new javax.swing.JPanel();
        javax.swing.JScrollPane scrlPane = new javax.swing.JScrollPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("UDP Receiver");
        setPreferredSize(new java.awt.Dimension(600, 800));

        pnlSettings.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        pnlSettings.setLayout(new java.awt.GridBagLayout());

        lblPort.setText("Port:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlSettings.add(lblPort, gridBagConstraints);

        spnPort.setModel(new javax.swing.SpinnerNumberModel(8080, 0, 65535, 1));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 0);
        pnlSettings.add(spnPort, gridBagConstraints);

        chkLocalhost.setText("Localhost:");
        chkLocalhost.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 0);
        pnlSettings.add(chkLocalhost, gridBagConstraints);

        javax.swing.GroupLayout pnlSpacerLayout = new javax.swing.GroupLayout(pnlSpacer);
        pnlSpacer.setLayout(pnlSpacerLayout);
        pnlSpacerLayout.setHorizontalGroup(
            pnlSpacerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        pnlSpacerLayout.setVerticalGroup(
            pnlSpacerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        pnlSettings.add(pnlSpacer, gridBagConstraints);

        btnListen.setText("Listen");
        btnListen.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                btnListenActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        pnlSettings.add(btnListen, gridBagConstraints);

        btnHexText.setText("Show Text");
        btnHexText.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                btnHexTextActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        pnlSettings.add(btnHexText, gridBagConstraints);

        btnClear.setText("Clear");
        btnClear.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                btnClearActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        pnlSettings.add(btnClear, gridBagConstraints);

        getContentPane().add(pnlSettings, java.awt.BorderLayout.PAGE_START);

        pnlOutput.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        pnlOutput.setLayout(new java.awt.BorderLayout());

        txtReceivedData.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        scrlPane.setViewportView(txtReceivedData);

        pnlOutput.add(scrlPane, java.awt.BorderLayout.CENTER);

        getContentPane().add(pnlOutput, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnListenActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnListenActionPerformed
    {//GEN-HEADEREND:event_btnListenActionPerformed
        if ( btnListen.isSelected() )
        {
            startListening();
        }
        else
        {
            stopListening();
        }
    }//GEN-LAST:event_btnListenActionPerformed

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnClearActionPerformed
    {//GEN-HEADEREND:event_btnClearActionPerformed
        txtReceivedData.setText("");
    }//GEN-LAST:event_btnClearActionPerformed

    private void btnHexTextActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnHexTextActionPerformed
    {//GEN-HEADEREND:event_btnHexTextActionPerformed
        btnHexText.setText(btnHexText.isSelected() ? "Show Hex" : "Show Text");
    }//GEN-LAST:event_btnHexTextActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[])
    {
        java.awt.EventQueue.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                new UdpReceiver().setVisible(true);
            }
        });
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClear;
    private javax.swing.JToggleButton btnHexText;
    private javax.swing.JToggleButton btnListen;
    private javax.swing.JCheckBox chkLocalhost;
    private javax.swing.JPanel pnlSettings;
    private javax.swing.JSpinner spnPort;
    private final javax.swing.JTextArea txtReceivedData = new javax.swing.JTextArea();
    // End of variables declaration//GEN-END:variables
    
    private ReceiverThread receiverThread;
}
