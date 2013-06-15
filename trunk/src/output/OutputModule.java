package output;

import analyser.SpectrumAnalyser;
import java.io.File;

/**
 * Interface for output modules.
 * 
 * @author  Stefan Marks
 * @version 1.0 - 15.06.2013: Created
 */
public interface OutputModule extends SpectrumAnalyser.Listener
{
    /**
     * Gets the name of the module.
     * 
     * @return the name of the module
     */
    String getName();        
        
    /**
     * Checks if the output module is enabled.
     * 
     * @param enabled  <code>true</code> if the module is enabled,
     *                 <code>false</code> if not
     */
    boolean isEnabled();
    
    /**
     * Enables or disables the output module.
     * 
     * @param enabled  <code>true</code> if the module is to be enabled,
     *                 <code>false</code> if the module is to be disabled
     */
    void setEnabled(boolean enabled);
    
    /**
     * Informs the module that a new audio file was opened.
     * 
     * @param file  the opened file
     */
    void audioFileOpened(File file);
    
    /**
     * Informs the module that the audio file was closed.
     */
    void audioFileClosed(); 
}
