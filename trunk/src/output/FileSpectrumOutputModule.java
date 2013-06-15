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
public class FileSpectrumOutputModule implements OutputModule
{
    /**
     * Creates a spectrum information logger.
     * 
     * @param analyser the spectrum analyser to attach to
     */
    public FileSpectrumOutputModule(SpectrumAnalyser analyser)
    {
        outputFile    = null;
        output        = null;
        headerPrinted = false;
        enabled       = false;
        analyser.registerListener(this);
    }
    
    @Override
    public String getName()
    {
        String name = "Spectrum File Output";
        if ( outputFile != null ) 
        {
            name += " (" + outputFile + ")";
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
            closeFile();
        }
    } 
    
    public String getOutputFilename()
    {
        return (outputFile != null) ? outputFile.getAbsolutePath() : "";
    }
    
    public void setOutputFilename(String filename)
    {
        outputFile = new File(filename);
        headerPrinted = false;
    }
    
    @Override
    public void audioFileOpened(File file) 
    {
        setOutputFilename(file.getAbsolutePath() + ".txt");
    }
    
    @Override
    public void audioFileClosed()
    {
        closeFile();
    }
    
    private void ensureFileIsOpen()
    {
        if ( output == null )
        {
            try
            {
                output = new PrintStream(outputFile);
            }
            catch (FileNotFoundException e)
            {
                enabled = false;
            }
        }
    }
    
    
    private void closeFile()
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
        if ( !enabled ) return;
        
        ensureFileIsOpen();
        
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
