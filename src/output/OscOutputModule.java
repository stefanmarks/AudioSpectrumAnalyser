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
        targetAddress    = "127.0.0.1:8989";
        oscTargetAddress = "/MightG/stringmessage";
        enabled          = false;
        outputPort1       = null;
        message1          = null;
        bundle1           = null;
        outputPort2       = null;
        message2          = null;
        bundle2           = null;
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
        if ( outputPort1 == null )
        {
            try
            {
                String[] parts = targetAddress.split(":");                
                // frequency spectrum on the target port
                InetSocketAddress target = new InetSocketAddress(parts[0], Integer.parseInt(parts[1]));
                outputPort1 = new OSCPortOut(target.getAddress(), target.getPort());
                message1    = new OSCMessage(oscTargetAddress);
                bundle1     = new OSCBundle();
                bundle1.addPacket(message1);
                // playback progress on the next port 
                outputPort2 = new OSCPortOut(target.getAddress(), target.getPort() + 1);
                message2    = new OSCMessage(oscTargetAddress);
                bundle2     = new OSCBundle();
                bundle2.addPacket(message2);
            } 
            catch ( Exception ex )
            {
                Logger.getLogger(OscOutputModule.class.getName()).log(Level.SEVERE, null, ex);
                closeSocket();
                enabled = false;
            } 
        }
    }
    
    private void closeSocket()
    {
        if ( outputPort1 != null )
        {
            outputPort1.close();
            outputPort1 = null;
        }
        if ( outputPort2 != null )
        {
            outputPort2.close();
            outputPort2 = null;
        }
        message1 = null;
        message2 = null;
    }
    
    @Override
    public void analysisUpdated(SpectrumAnalyser analyser)
    {
        if ( !enabled ) return;
       
        ensureSocketIsOpen();
        
        if ( outputPort1 != null )
        {
            message1.clearArguments();
            SpectrumInfo  info = analyser.getSpectrumInfo(0);
            for ( float f : info.intensity )
            {
                message1.addArgument(f);
            }
            
            try
            {
                outputPort1.send(bundle1); //  bundle consists of only one messsge
            } 
            catch (IOException ex)
            {
                Logger.getLogger(OscOutputModule.class.getName()).log(Level.SEVERE, null, ex);
                closeSocket();
                enabled = false;
            }
        }
        
        if ( outputPort2 != null )
        {
            message2.clearArguments();
            message2.addArgument(analyser.getSpectrumInfo(0).position);
            
            try
            {
                outputPort2.send(bundle2); //  bundle consists of only one messsge
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
    private OSCPortOut     outputPort1, outputPort2;
    private OSCMessage     message1, message2;
    private OSCBundle      bundle1, bundle2;

}
