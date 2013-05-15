package detector;

import ddf.minim.AudioListener;
import ddf.minim.AudioSource;
import ddf.minim.Playable;
import ddf.minim.analysis.BlackmanWindow;
import ddf.minim.analysis.FFT;
import ddf.minim.analysis.HannWindow;
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
        dataRawL = dataRawR = null; 
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
        // calculate minimum buffer size to 
        // reliably measure a whole phase of a specific minimum frequency
        float rate = as.sampleRate();
        float minFreq = 20;
        int   minBufferSize = 1 << (int) (Math.log(rate / minFreq) / Math.log(2));
        System.out.println(minBufferSize);
        
        dataRawL = new float[minBufferSize];
        dataFftL = new float[minBufferSize];
        dataRawR = new float[minBufferSize];
        dataFftR = new float[minBufferSize];
        
        fft = new FFT(minBufferSize, rate);
        fft.logAverages(100, 8);
        fft.window(new HannWindow());
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
        
        // shift data in input arrays
        System.arraycopy(dataRawL, sampL.length, dataRawL, 0, dataRawL.length - sampL.length);
        System.arraycopy(dataRawR, sampR.length, dataRawR, 0, dataRawR.length - sampR.length);
        // copy samples into array for analysis
        System.arraycopy(sampL, 0, dataRawL, dataRawL.length - sampL.length, sampL.length); 
        System.arraycopy(sampL, 0, dataRawR, dataRawR.length - sampR.length, sampR.length); 
        // copy samples array into FFT array so values can be shaped by teh windows
        System.arraycopy(dataRawL, 0, dataFftL, 0, dataRawL.length);
        System.arraycopy(dataRawR, 0, dataFftR, 0, dataRawR.length); 

        if ( fft != null ) 
        {
            fft.forward(dataFftL);
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
  
    public float[] getAudioDataL()
    {
        return dataRawL;
    }
    
    public float[] getAudioDataR()
    {
        return dataRawR;
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
    
    /**
     * Gets the FFT analyser.
     * 
     * @return the FFT analyser
     */
    public FFT getFFT()
    {
        return fft;
    }

    private Playable             audioSource;
    private float[]              dataRawL, dataRawR, dataFftL, dataFftR;
    private FFT                  fft;
    private final SpectrumInfo[] history;
    private int                  historyIdx;
    private Set<Listener>        listeners;
}
