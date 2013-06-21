package output;

import analyser.SpectrumAnalyser;
import analyser.SpectrumInfo;
import com.illposed.osc.OSCBundle;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortOut;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An output module that sends the spectrum data via OSC.
 * 
 * @author Stefan Marks
 * @verison 1.0 - 21.05.2013: Created
 * @version 1.1 - 20.06.2013: Converted to send OSC messages
 */
public class OscOutputModule implements OutputModule
{
    public OscOutputModule(SpectrumAnalyser analyser)
    {
        targetAddress    = "127.0.0.1:8080";
        oscTargetAddress = "/MightG/stringmessage";
        enabled          = false;
        outputPort       = null;
        message          = null;
        bundle           = null;
        analyser.registerListener(this);
    }

    @Override
    public String getName()
    {
        String name = "OSC Output";
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
        closeSocket(); // to force update of the value
    }
    
    public String getOscTargetAddress()
    {
        return oscTargetAddress;
    }
    
    public void setOscTargetAddress(String address)
    {
        oscTargetAddress = address;
        closeSocket(); // to force update of the value
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
        if ( outputPort == null )
        {
            try
            {
                String[] parts = targetAddress.split(":");                
                InetSocketAddress target = new InetSocketAddress(parts[0], Integer.parseInt(parts[1]));
                outputPort = new OSCPortOut(target.getAddress(), target.getPort());
                message    = new OSCMessage(oscTargetAddress);
                bundle     = new OSCBundle();
                bundle.addPacket(message);
            } 
            catch ( NumberFormatException | SocketException ex )
            {
                Logger.getLogger(OscOutputModule.class.getName()).log(Level.SEVERE, null, ex);
                closeSocket();
                enabled = false;
            } 
        }
    }
    
    private void closeSocket()
    {
        if ( outputPort != null )
        {
            outputPort.close();
            outputPort = null;
        }
        message = null;
    }
    
    @Override
    public void analysisUpdated(SpectrumAnalyser analyser)
    {
        if ( !enabled ) return;
       
        ensureSocketIsOpen();
        
        if ( outputPort != null )
        {
            message.clearArguments();
            SpectrumInfo  info = analyser.getSpectrumInfo(0);
            for ( float f : info.intensity )
            {
                message.addArgument(f);
            }
            
            try
            {
                //outputPort.send(message);
                outputPort.send(bundle); //  bundle consists of only one messsge
            } 
            catch (IOException ex)
            {
                Logger.getLogger(OscOutputModule.class.getName()).log(Level.SEVERE, null, ex);
                closeSocket();
                enabled = false;
            }
        }
    }
 
    private boolean        enabled;
    private String         targetAddress, oscTargetAddress;
    private OSCPortOut     outputPort;
    private OSCMessage     message;
    private OSCBundle      bundle;

}
