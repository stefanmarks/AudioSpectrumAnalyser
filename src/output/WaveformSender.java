package output;

import analyser.SpectrumAnalyser;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An addon that sends the audio data to a UDP receiver.
 * 
 * @author Stefan Marks
 * @verison 1.0 - 21.05.2013: Created
 */
public class WaveformSender implements SpectrumAnalyser.Listener
{
    public WaveformSender(SpectrumAnalyser analyser)
    {
       DatagramSocket clientSocket;
        try
        {
            clientSocket = new DatagramSocket();
            InetAddress receiverAddress = InetAddress.getByName("localhost");
            byte[] sendData = new byte[1024];
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, receiverAddress, 9876);
            clientSocket.send(sendPacket);
        } catch (IOException ex)
        {
            Logger.getLogger(WaveformSender.class.getName()).log(Level.SEVERE, null, ex);
        } 
        analyser.registerListener(this);
    }


    @Override
    public void analysisUpdated(SpectrumAnalyser analyser)
    {
      samplesL = analyser.getAudioDataL();
      samplesR = analyser.getAudioDataR();
    }
 
    private float[] samplesL;
    private float[] samplesR;  
}
