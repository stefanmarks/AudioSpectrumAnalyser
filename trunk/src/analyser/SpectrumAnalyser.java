package analyser;

import ddf.minim.AudioListener;
import ddf.minim.AudioSource;
import ddf.minim.Playable;
import ddf.minim.analysis.FFT;
import ddf.minim.analysis.HannWindow;
import detector.Feature;
import detector.FeatureDetector;
import java.util.List;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

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
        
        featureDetectors = new HashSet<>();
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
        LOG.log(Level.INFO, "Attached to sound source (Sample Rate {0}, FFT Buffer size {1})", new Object[] {rate, minBufferSize});
        
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
    
    /**
     * Registers a new feature detector.
     * 
     * @param fd  the feature detector to register.
     * @return <code>true</code> if the detector was successfully registered,
     *         <code>false</code> if not
     */
    public boolean registerFeatureDetector(FeatureDetector fd)
    {
        return featureDetectors.add(fd);
    }
    
    /**
     * Gets a list of detected features of this analyser.
     * 
     * @return a list of detected features
     */
    public List<Feature> getDetectedFeatures()
    {
        List<Feature> features = new LinkedList<>();
        for ( FeatureDetector fd : featureDetectors )
        {
            features.add(fd.getFeature());
        }
        return features;
    }
    
    /**
     * Gets the number of detected features of this analyser.
     * 
     * @return the number of detected features
     */
    public int getDetectedFeaturesCount()
    {
        return featureDetectors.size();
    }
    
    /**
     * Unregisters a feature detector.
     * 
     * @param fd  the feature detector to unregister.
     * @return <code>true</code> if the detector was successfully unregistered,
     *         <code>false</code> if not
     */
    public boolean unregisterDetector(FeatureDetector fd)
    {
        return featureDetectors.remove(fd);
    }
    
    /**
     * Registers a new analysis result listener.
     * 
     * @param fd  the analysis result listener to register.
     * @return <code>true</code> if the analysis result listener was successfully registered,
     *         <code>false</code> if not
     */
    public boolean registerListener(Listener l)
    {
        return listeners.add(l);
    }
    
    /**
     * Unregisters an analysis result listener.
     * 
     * @param fd  the analysis result listener to un register.
     * @return <code>true</code> if the analysis result listener was successfully unregistered,
     *         <code>false</code> if not
     */
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
                history[historyIdx].copySpectrumData(fft);
                historyIdx = (historyIdx + 1) % history.length;
            }
            
            // run feature detectors
            for ( FeatureDetector featureDetector : featureDetectors )
            {
                if ( featureDetector.detectFeature(this) )
                {
                    // feature detected: set bit
                    history[historyIdx].features |= featureDetector.getFeature().getBitmask();
                }
            }
            
            // notify listeners
            for (Listener listener : listeners)
            {
                listener.analysisUpdated(this);
            }
        }
    }
  
    /**
     * Gets the raw audio data for the left channel.
     * 
     * @return the raw left channel audio data
     */
    public float[] getAudioDataL()
    {
        return dataRawL;
    }
    
    /**
     * Gets the raw audio data for the right channel.
     * 
     * @return the raw right channel audio data
     */
    public float[] getAudioDataR()
    {
        return dataRawR;
    }
    
    /**
     * Gets the number of frequency bands the analyser returns.
     * 
     * @return the number of ferquency bands
     */
    public int getSpectrumBandCount()
    {
        return fft.avgSize();
    }
    
    /**
     * Gets the size of the spectrum history buffer.
     * 
     * @return the size of the frequency history buffer
     */
    public int getHistorySize()
    {
        return history.length;
    }
    
    /**
     * Gets the spectrum information for a specific position in history.
     * 
     * @param idx  the index of history (0: most recent)
     * @return the spectrum information 
     *         or <code>null</code> if there is no information
     */
    public SpectrumInfo getSpectrumInfo(int idx)
    {
        SpectrumInfo retInfo = null;
        
        if ( idx < history.length )
        {
            synchronized(history)
            {
                int absIdx = historyIdx - 1 - idx;
                if ( absIdx < 0 ) 
                { 
                    absIdx += history.length; 
                }
                retInfo = history[absIdx];
            }
            if ( !retInfo.isDefined() )
            {
                // not defined -> return null
                retInfo = null;
            }
        }
        return retInfo;
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
    private Set<FeatureDetector> featureDetectors;
    private Set<Listener>        listeners;

    private static final Logger LOG = Logger.getLogger(SpectrumAnalyser.class.getName());
}
