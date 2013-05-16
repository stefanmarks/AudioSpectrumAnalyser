package output;

import analyser.SpectrumInfo;
import analyser.SpectrumAnalyser;
import ddf.minim.analysis.FFT;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

/**
 * Class for logging the spectrum information to a text data file (e.g., CSV).
 * 
 * @author  Stefan Marks
 * @version 1.0 - 14.05.2013: Created
 */
public class SpectrumLogger implements SpectrumAnalyser.Listener
{
    /**
     * Creates a spectrum information logger.
     * 
     * @param analyser the spectrum analyser to attach to
     */
    public SpectrumLogger(SpectrumAnalyser analyser)
    {
        outputFile    = null;
        output        = null;
        headerPrinted = false;
        enabled       = true;
        analyser.registerListener(this);
    }
    
    /**
     * Opens a spectrum logfile.
     * Any file with th same name will be overwritten.
     * 
     * @param file  the file to open
     */
    public void openLogfile(File file) 
    {
        outputFile    = file;
        headerPrinted = false;
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
     * Enables or disables the logger.
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
        if ( !enabled ) return;
        
        if ( output == null )
        {
            try {
                output = new PrintStream(outputFile);
            }
            catch (FileNotFoundException e)
            {
                enabled = false;
            }
        }
        
        if ( output != null )
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
            output.printf("%.3f", info.sampleIdx / 1000.0f);
            for ( int i = 0 ; i < info.intensity.length ; i++ )
            {
                output.printf("\t%.3f", info.intensity[i]);
            }
            output.println();
            
            output.flush();
        }
    }

    File        outputFile;
    PrintStream output;
    boolean     enabled;
    boolean     headerPrinted;
}
