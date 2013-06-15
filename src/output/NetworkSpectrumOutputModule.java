package output;

import analyser.SpectrumAnalyser;
import analyser.SpectrumInfo;
import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An output module that sends the spectrum data to a UDP receiver.
 * 
 * @author Stefan Marks
 * @verison 1.0 - 21.05.2013: Created
 */
public class NetworkSpectrumOutputModule implements OutputModule
{
    public NetworkSpectrumOutputModule(SpectrumAnalyser analyser)
    {
        targetAddress = "127.0.0.1:8080";
        enabled       = false;
        outputSocket  = null;
        outputPacket  = null;
        analyser.registerListener(this);
    }

    @Override
    public String getName()
    {
        String name = "Network Spectrum Output";
        if ( targetAddress != null ) 
        {
            name += " (" + targetAddress + ")";
        }
        return name;
    }
    
    @Override
    public boolean isEnabled()
    {
        return enabled;
    }
    
    @Override
    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
        if ( !enabled )
        {
            closeSocket();
        }
    }

    public String getTargetAddress()
    {
        return targetAddress;
    }
    
    public void setTargetAddress(String address)
    {
        targetAddress = address;
    }
    
    @Override
    public void audioFileOpened(File file)
    {
        // nothing to do here
    }

    @Override
    public void audioFileClosed()
    {
        closeSocket(); 
    }
    
    private void ensureSocketIsOpen()
    {
        if ( outputPacket == null )
        {
            try
            {
                String[] parts = targetAddress.split(":");                
                InetSocketAddress target = new InetSocketAddress(parts[0], Integer.parseInt(parts[1]));
                outputSocket = new DatagramSocket();
                outputPacket = new DatagramPacket(new byte[1], 1, target);
            } 
            catch ( NumberFormatException | SocketException ex )
            {
                Logger.getLogger(NetworkSpectrumOutputModule.class.getName()).log(Level.SEVERE, null, ex);
                closeSocket();
                enabled = false;
            } 
        }
    }
    
    private void closeSocket()
    {
        if ( outputSocket != null )
        {
            outputSocket.close();
            outputSocket = null;
        }
        outputPacket = null;
    }
    
    @Override
    public void analysisUpdated(SpectrumAnalyser analyser)
    {
        if ( !enabled ) return;
       
        ensureSocketIsOpen();
        
        if ( outputPacket != null )
        {
            SpectrumInfo  info = analyser.getSpectrumInfo(0);
            StringBuilder out  = new StringBuilder("stringmessage\n");
            for ( float f : info.intensity )
            {
                out.append(String.format("%.3f\n", f));
            }
            
            outputPacket.setData(out.toString().getBytes());
            try
            {
                outputSocket.send(outputPacket);
            } 
            catch (IOException ex)
            {
                Logger.getLogger(NetworkSpectrumOutputModule.class.getName()).log(Level.SEVERE, null, ex);
                closeSocket();
                enabled = false;
            }
        }
    }
 
    private boolean        enabled;
    private String         targetAddress;
    private DatagramSocket outputSocket;
    private DatagramPacket outputPacket;

}
