package detector;

import ddf.minim.analysis.FFT;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class for logging the spectrum information to a CSV file.
 * 
 * @author  Stefan Marks
 * @version 1.0 - 14.05.2013: Created
 */
public class SpectrumLogger implements SpectrumAnalyser.Listener
{

    public SpectrumLogger(SpectrumAnalyser analyser)
    {
        output = null;
        headerPrinted = false;
        enabled = true;
        analyser.registerListener(this);
    }
    
    /**
     * Opens a spectrum logfile.
     * Any file with th same name will be overwritten.
     * 
     * @param file  the file to open.
     */
    public void openLogfile(File file) 
    {
        try
        {
            output        = new PrintStream(file);
            headerPrinted = false;
        } 
        catch (FileNotFoundException ex)
        {
            Logger.getLogger(SpectrumLogger.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Closes the spectrum logfile.
     */
    public void closeLogfile()
    {
        if ( output != null )
        {
            output.close();
            output = null;
        }        
    }
    
    
    /**
     * Checks if the logger is enabled.
     * 
     * @return <code>true</code> if the logger is enabled, 
     *         <code>false</code> if not
     */
    public boolean isEnabled()
    {
        return enabled;
    }
    
    /**
     * Enables or disables he logger.
     * 
     * @param enabled  <code>true</code> to enable the logger, 
     *                 <code>false</code> to disable
     */
    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }
            
    
    @Override
    public void analysisUpdated(SpectrumAnalyser analyser)
    {
        if ( output != null && enabled )
        {
            if ( !headerPrinted )
            {
                output.print("time");
                FFT fft = analyser.getFFT();
                for ( int i = 0 ; i < fft.avgSize() ; i++ )
                {
                    output.printf("\t%.0fHz", fft.getAverageCenterFrequency(i));
                }
                output.println();
                headerPrinted = true;
            }
            
            SpectrumInfo info = analyser.getSpectrumInfo(0);
            output.printf("%d", info.sampleIdx);
            for ( int i = 0 ; i < info.intensity.length ; i++ )
            {
                output.printf("\t%.3f", info.intensity[i]);
            }
            output.println();
            
            output.flush();
        }
    }

    PrintStream output;
    boolean     enabled;
    boolean     headerPrinted;
}
