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
        analyser.registerListener(this);
    }
    
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
    
    public void closeLogfile()
    {
        if ( output != null )
        {
            output.close();
            output = null;
        }        
    }
    
    @Override
    public void analysisUpdated(SpectrumAnalyser analyser)
    {
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
    boolean     headerPrinted;
}
