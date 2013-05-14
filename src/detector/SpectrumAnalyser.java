package detector;

import ddf.minim.AudioListener;
import ddf.minim.AudioSource;
import ddf.minim.Playable;
import ddf.minim.analysis.BlackmanWindow;
import ddf.minim.analysis.FFT;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Class for analysing the spectrum of an audio stream.
 * 
 * @author  Stefan Marks
 * @version 1.0 - 12.05.2013: Created
 */
public class SpectrumAnalyser implements AudioListener
{
    public interface Listener
    {
        void analysisUpdated(SpectrumAnalyser analyser);
    };
    
    
    public SpectrumAnalyser(int historySize)
    {
        audioSource = null;
        samplesL = samplesR = null; 
        fft = null;
        
        history = new SpectrumInfo[historySize];
        for ( int i = 0 ; i < history.length ; i++ )
        {
            history[i] = new SpectrumInfo();
        }
        historyIdx = 0;
        
        listeners = new HashSet<>();
    }
    
    private void resetHistory()
    {
        for ( SpectrumInfo info : history )
        {
            info.reset();
        }
        historyIdx = 0;
    }
    
    public void attachToAudio(AudioSource as)
    {
        as.addListener(this);    
        samplesL = samplesR = null;
        fft = new FFT(as.bufferSize(), as.sampleRate());
        fft.logAverages(100, 8);
        fft.window(new BlackmanWindow());
        audioSource = (Playable) as;
    }
    
    public void detachFromAudio(AudioSource as)
    {
        as.removeListener(this);  
        fft = null;
        audioSource = null;
        resetHistory();
    }
    
    public boolean registerListener(Listener l)
    {
        return listeners.add(l);
    }
    
    public boolean unregisterListener(Listener l)
    {
        return listeners.remove(l);
    }
    
    @Override
    public void samples(float[] samp)
    {
        samples(samp, samp);
    }

    @Override
    public void samples(float[] sampL, float[] sampR)
    {
        if ( (audioSource == null) || !audioSource.isPlaying() ) return;
        
        // copy samples into array for analysis
        if ( samplesL == null  ) { samplesL = Arrays.copyOf(sampL, sampL.length); }
        else                     { System.arraycopy(sampL, 0, samplesL, 0, sampL.length); }
        if ( samplesR == null  ) { samplesR = Arrays.copyOf(sampR, sampR.length); }
        else                     { System.arraycopy(sampL, 0, samplesR, 0, sampR.length); }

        if ( fft != null ) 
        {
            fft.forward(samplesL);
            synchronized(history)
            {      
                history[historyIdx].sampleIdx = audioSource.position();
                history[historyIdx].copyData(fft);
                historyIdx = (historyIdx + 1) % history.length;
            }
            
            // notify listeners
            for (Listener listener : listeners)
            {
                listener.analysisUpdated(this);
            }
        }
    }
  
    public float[] getSamplesL()
    {
        return samplesL;
    }
    
    public float[] getSamplesR()
    {
        return samplesR;
    }
    
    public int getHistorySize()
    {
        return history.length;
    }
    
    public SpectrumInfo getSpectrumInfo(int idx)
    {
        if ( idx >= history.length ) return null;
        synchronized(history)
        {
            int absIdx = historyIdx - 1 - idx;
            if ( absIdx < 0 ) { absIdx += history.length; }
            return history[absIdx];
        }
    }
    
    public FFT getFFT()
    {
        return fft;
    }

    private Playable             audioSource;
    private float[]              samplesL, samplesR;
    private FFT                  fft;
    private final SpectrumInfo[] history;
    private int                  historyIdx;
    private Set<Listener>        listeners;
}
