package analyser;

/**
 * Class for shaping spectrum data.
 * 
 * The function shape expects a value between 0 and 1 and 
 * 
 * @author  Stefan Marks
 * @version 1.0 - 16.06.2013: Created
 */
public abstract class SpectrumShaper 
{
    public static final SpectrumShaper LINEAR = new LinearSpectrumShaper();
    public static final SpectrumShaper SQUARE_ROOT = new SquareRootSpectrumShaper();
    public static final SpectrumShaper LOGARITHMIC = new LogSpectrumShaper();
    
    /**
     * Gets the name of the spectrum shaper.
     * 
     * @return the name of the shaper
     */
    public abstract String getName();
    
    /**
     * Shapes an input value.
     * 
     * @param value the input value
     * 
     * @return the shaped output value
     */
    public abstract float  shape(float value);
    
    
    private static class LinearSpectrumShaper extends SpectrumShaper
    {
        @Override
        public String getName() { return "Linear"; }

        @Override
        public float shape(float value)
        {
            return value;
        }
    }

    private static class SquareRootSpectrumShaper extends SpectrumShaper
    {
        @Override
        public String getName() { return "Square Root"; }

        @Override
        public float shape(float value)
        {
            return (float) Math.sqrt(value);
        }
    }
    
    private static class LogSpectrumShaper extends SpectrumShaper
    {
        private static final float log2 = (float) Math.log(2);
        
        @Override
        public String getName() { return "Logarithmic"; }

        @Override
        public float shape(float value)
        {
            return (float) Math.max(0, 1 + (Math.log(value) / log2) / 12);
        }
    }
}
