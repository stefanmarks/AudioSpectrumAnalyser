package app;

import gui.FrequencySpectrumHistoryPanel;
import gui.FrequencySpectrumRenderPanel;
import gui.PlaybackControlPanel;
import detector.SpectrumAnalyser;
import detector.SpectrumLogger;
import gui.WaveformRenderPanel;
import ddf.minim.*;
import java.io.File;
import javax.swing.JFileChooser;
import processing.core.PApplet;

/**
 * Beat Analyser Framework
 * 
 * @author Stefan Marks
 * @version 1.0 - 15.05.2013: Created
 */
public class BeatAnalyzerApp extends javax.swing.JFrame
{
    /**
     * Creates new form BeatAnalyzerApp
     */
    public BeatAnalyzerApp()
    {
        initComponents();
        
        analyser = new SpectrumAnalyser(1024);
        logger   = new SpectrumLogger(analyser);
        
        renderWaveform = new WaveformRenderPanel(analyser);
        pnlWaveform.add(renderWaveform);
        
        renderFrequencySpectrum = new FrequencySpectrumRenderPanel(analyser);
        pnlFrequencySpectrum.add(renderFrequencySpectrum);
        
        renderFrequencyHistory = new FrequencySpectrumHistoryPanel(analyser);
        pnlFrequencyHistory.add(renderFrequencyHistory);
        
        playbackControl = new PlaybackControlPanel();
        pnlPlaybackControls.add(playbackControl);
        
        PApplet p = new PApplet();
        minim = new Minim(p);
        sound = null;
     
        setLocationRelativeTo(null);
        
        openSoundFile(new File("data/Sweep.wav"));
    }

    private void selectSoundFile()
    {
        JFileChooser jfc = new JFileChooser(".");
        int choice = jfc.showOpenDialog(this);
        if ( choice == JFileChooser.APPROVE_OPTION )
        {
            File file = jfc.getSelectedFile();
            openSoundFile(file);
        }
    }
    
    private void openSoundFile(File file)
    {
        // close old file first?
        if ( sound != null )
        {
            analyser.detachFromAudio(sound);
            logger.closeLogfile();                    
            playbackControl.detachFromAudio();
            sound.close();
            sound = null;
        }
        
        sound = minim.loadFile(file.getAbsolutePath(), 1024);
        analyser.attachToAudio(sound);
        logger.openLogfile(new File(file.getAbsolutePath() + ".log"));
        playbackControl.attachToAudio(sound);
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {
        java.awt.GridBagConstraints gridBagConstraints;

        pnlWaveform = new javax.swing.JPanel();
        pnlFrequencySpectrum = new javax.swing.JPanel();
        pnlFrequencyHistory = new javax.swing.JPanel();
        pnlPlaybackControls = new javax.swing.JPanel();
        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu menuFile = new javax.swing.JMenu();
        javax.swing.JMenuItem menuFileOpen = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Beat Analyser v1.0");
        setPreferredSize(new java.awt.Dimension(800, 600));
        getContentPane().setLayout(new java.awt.GridBagLayout());

        pnlWaveform.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createTitledBorder("Waveform"), javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255))));
        pnlWaveform.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(pnlWaveform, gridBagConstraints);

        pnlFrequencySpectrum.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createTitledBorder("Frequency Spectrum"), javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255))));
        pnlFrequencySpectrum.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(pnlFrequencySpectrum, gridBagConstraints);

        pnlFrequencyHistory.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createTitledBorder("Frequency Spectrum History"), javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255))));
        pnlFrequencyHistory.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(pnlFrequencyHistory, gridBagConstraints);

        pnlPlaybackControls.setBorder(javax.swing.BorderFactory.createTitledBorder("Playback Controls"));
        pnlPlaybackControls.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        getContentPane().add(pnlPlaybackControls, gridBagConstraints);

        menuFile.setText("File");

        menuFileOpen.setText("Open File");
        menuFileOpen.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                openSoundFile(evt);
            }
        });
        menuFile.add(menuFileOpen);

        menuBar.add(menuFile);

        setJMenuBar(menuBar);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void openSoundFile(java.awt.event.ActionEvent evt)//GEN-FIRST:event_openSoundFile
    {//GEN-HEADEREND:event_openSoundFile
        selectSoundFile();
    }//GEN-LAST:event_openSoundFile

    /**
     * @param args the command line arguments
     */
    public static void main(String args[])
    {
        java.awt.EventQueue.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                new BeatAnalyzerApp().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JPanel pnlFrequencyHistory;
    private javax.swing.JPanel pnlFrequencySpectrum;
    private javax.swing.JPanel pnlPlaybackControls;
    private javax.swing.JPanel pnlWaveform;
    // End of variables declaration//GEN-END:variables

    private Minim            minim;
    private AudioPlayer      sound;
    private SpectrumAnalyser analyser;
    private SpectrumLogger   logger;
    
    private WaveformRenderPanel           renderWaveform;
    private FrequencySpectrumRenderPanel  renderFrequencySpectrum;
    private FrequencySpectrumHistoryPanel renderFrequencyHistory;
    private PlaybackControlPanel          playbackControl;
}
