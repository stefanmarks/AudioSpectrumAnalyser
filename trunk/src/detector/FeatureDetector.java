package detector;

import analyser.SpectrumAnalyser;

/**
 * Base class for an audio feature detector.
 * 
 * @author  Stefan Marks
 * @version 1.0 - 16.05.2013: Created
 */
public abstract class FeatureDetector 
{ 
    protected FeatureDetector(Feature feature)
    {
        this.feature = feature;
    }
    
    /**
     * Runs the feature detection.
     * 
     * @param analyser  the analyser to run the detection on
     * @return <code>true</code> if the feature was detected,
     *         <code>false</code> if not
     */
    public abstract boolean detectFeature(SpectrumAnalyser analyser);
    
    /**
     * Returns the detection delay of the feature. 
     * 
     * @return the detection delay in spectrum history steps
     */
    public abstract int getDetectionDelay();
            
    /**
     * Gets information about the detected feature.
     * 
     * @return feature information
     */
    public final Feature getFeature()
    { 
        return feature;
    }
    
    // the feature that this detector is looking for
    private final Feature feature;
}
