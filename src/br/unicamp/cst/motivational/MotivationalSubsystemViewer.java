/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.cst.motivational;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Mind;
import br.unicamp.cst.util.ChartViewerUtil;
import br.unicamp.cst.util.MindViewer;
import br.unicamp.cst.util.TreeViewerUtil;
import java.awt.BorderLayout;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.tree.DefaultTreeModel;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 *
 * @author du
 */
public class MotivationalSubsystemViewer extends javax.swing.JPanel {

    private List<MotivationalCodelet> motivationalCodelets;
    private List<EmotionalCodelet> emotionalCodelets;
    private List<GoalCodelet> goalCodelets;
    private List<AppraisalCodelet> appraisalCodelets;
    private List<MoodCodelet> moodCodelets;
    
    private Thread threadDrives;
    private Thread threadEmotionalDrives;
    private Thread threadAppraisals;
    private Thread threadMoods;
    private Thread threadGoals;
    
    private int selectedIndex = 0;
    private long refreshTime = 100;
    private boolean stopRefresh = false;
    
    private ChartPanel motivationalChart;
    private ChartPanel emotionalChart;

    private DefaultTreeModel dtMotivationalCodelets;
    private DefaultTreeModel dtEmotionalCodelets;
    private DefaultTreeModel dtAppraisalCodelets;
    private DefaultTreeModel dtMoodCodelets;
    private DefaultTreeModel dtGoalCodelets;
    /**
     * Creates new form MotivationalSubsystemViewer
     */
    public MotivationalSubsystemViewer(long refreshTime, Mind mind) {
        initComponents();
        setRefreshTime(refreshTime);
        initMotivationalSubsystemViewer(mind.getMotivationalSubsystemModule().getMotivationalCodelets(), 
                                        mind.getMotivationalSubsystemModule().getEmotionalCodelets(), 
                                        mind.getMotivationalSubsystemModule().getGoalCodelets(), 
                                        mind.getMotivationalSubsystemModule().getAppraisalCodelets(), 
                                        mind.getMotivationalSubsystemModule().getMoodCodelets());
    }
    
    private void initMotivationalSubsystemViewer(List<MotivationalCodelet> motivationalCodelets,
                                                List<EmotionalCodelet> emotionalCodelets,
                                                List<GoalCodelet> goalCodelets,
                                                List<AppraisalCodelet> appraisalCodelets,
                                                List<MoodCodelet> moodCodelets) {
        setMotivationalCodelets(motivationalCodelets);
        setEmotionalCodelets(emotionalCodelets);
        setGoalCodelets(goalCodelets);
        setAppraisalCodelets(appraisalCodelets);
        setMoodCodelets(moodCodelets);

        startMotivationalThreads();
    }
    
    private void startMotivationalThreads() {
        setThreadDrives(new Thread() {
            @Override
            public void run() {
                DefaultCategoryDataset dataset = new DefaultCategoryDataset();

                synchronized (pnDrives) {
                    pnDrives.setLayout(new BorderLayout());
                    setMotivationalChart(ChartViewerUtil.createChart(dataset, "Motivational Codelets", "Drives", "Activation", PlotOrientation.VERTICAL));
                    pnDrives.add(getMotivationalChart(), BorderLayout.CENTER);
                    pnDrives.validate();
                }

                while (!isStopRefresh()) {

                    while ((!cbRefreshDrives.isSelected() && !cbDrivesChart.isSelected()) || getSelectedIndex() != 0) {
                        try {
                            synchronized (getThreadDrives()) {
                                getThreadDrives().wait();
                            }
                        } catch (InterruptedException ex) {
                            Logger.getLogger(MindViewer.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }

                    if (cbRefreshDrives.isSelected()) {
                        TreeViewerUtil.createTreeModelGUI(spMotivationalCodelets, getMotivationalCodelets(), "Motivational Codelets");
                    }
                   
                    if (cbDrivesChart.isSelected()) {
                        ChartViewerUtil.updateValuesInChart(dataset, getMotivationalCodelets());
                    }

                    try {
                        Thread.sleep(getRefreshTime());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        });

        setThreadEmotionalDrives(new Thread() {
            @Override
            public void run() {
                DefaultCategoryDataset dataset = new DefaultCategoryDataset();

                synchronized (pnEmotional) {
                    pnEmotional.setLayout(new BorderLayout());
                    pnEmotional.add(ChartViewerUtil.createChart(dataset, "Emotional Codelets", "Emotional Drives", "Activation", PlotOrientation.VERTICAL), BorderLayout.CENTER);
                    pnEmotional.validate();
                }

                while (!isStopRefresh()) {
                    while ((!cbRefreshEmotionalDrives.isSelected() && !cbEmotionalChart.isSelected()) || getSelectedIndex() != 1) {
                        try {
                            synchronized (getThreadEmotionalDrives()) {
                                getThreadEmotionalDrives().wait();
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    TreeViewerUtil.createTreeModelGUI(spEmotionalCodelets, getEmotionalCodelets(), "Emotional Codelets");

                    ChartViewerUtil.updateValueInChartByMemory(dataset, getEmotionalCodelets(), EmotionalCodelet.OUTPUT_AFFECTED_DRIVE_MEMORY.toString());
                    try {
                        Thread.sleep(getRefreshTime());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        });

        setThreadMoods(new Thread() {
            @Override
            public void run() {
              
                while (!isStopRefresh()) {
                    while (!cbRefreshMood.isSelected() && getSelectedIndex() != 2) {
                        try {
                            synchronized (getThreadMoods()) {
                                getThreadMoods().wait();
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    TreeViewerUtil.createTreeModelGUI(spMoodCodelets, getMoodCodelets(), "Mood Codelets");

                    try {
                        Thread.sleep(getRefreshTime());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        });

        setThreadAppraisals(new Thread() {
            @Override
            public void run() {
               
                while (!isStopRefresh()) {
                    while (!cbRefreshAppraisals.isSelected() && getSelectedIndex() != 3) {
                        try {
                            synchronized (getThreadAppraisals()) {
                                getThreadAppraisals().wait();
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    TreeViewerUtil.createTreeModelGUI(spAppraisalCodelets, getAppraisalCodelets(), "Appraisal Codelets");

                    try {
                        Thread.sleep(getRefreshTime());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
        
        setThreadGoals(new Thread() {
            @Override
            public void run() {
                
                while (!isStopRefresh()) {
                    while (!cbRefreshGoals.isSelected() && getSelectedIndex() != 4) {
                        try {
                            synchronized (getThreadAppraisals()) {
                                getThreadGoals().wait();
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    TreeViewerUtil.createTreeModelGUI(spGoalCodelets, getGoalCodelets(), "Goal Codelets");
                  
                    try {
                        Thread.sleep(getRefreshTime());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        });

        if(getMotivationalCodelets() != null && getMotivationalCodelets().size() > 0)
            getThreadDrives().start();
        
        if(getAppraisalCodelets()!= null && getAppraisalCodelets().size() > 0)
            getThreadAppraisals().start();
        
        if(getMoodCodelets()!= null && getMoodCodelets().size() > 0)
            getThreadMoods().start();
        
        if(getEmotionalCodelets()!= null && getEmotionalCodelets().size() > 0)
            getThreadEmotionalDrives().start();
        
        if(getGoalCodelets()!= null && getGoalCodelets().size() > 0)
            getThreadGoals().start();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        tbMotivationalSubsystem = new javax.swing.JTabbedPane();
        splDrives = new javax.swing.JSplitPane();
        pnTreeDrives = new javax.swing.JPanel();
        cbRefreshDrives = new javax.swing.JCheckBox();
        spMotivationalCodelets = new javax.swing.JScrollPane();
        pnDrivesChart = new javax.swing.JPanel();
        pnDrives = new javax.swing.JPanel();
        cbDrivesChart = new javax.swing.JCheckBox();
        splEmotional = new javax.swing.JSplitPane();
        pnTreeEmotional = new javax.swing.JPanel();
        spEmotionalCodelets = new javax.swing.JScrollPane();
        cbRefreshEmotionalDrives = new javax.swing.JCheckBox();
        pnEmotiovalChart = new javax.swing.JPanel();
        pnEmotional = new javax.swing.JPanel();
        cbEmotionalChart = new javax.swing.JCheckBox();
        pnMoodCodelts = new javax.swing.JPanel();
        spMoodCodelets = new javax.swing.JScrollPane();
        cbRefreshMood = new javax.swing.JCheckBox();
        pnAppraisalCodelets = new javax.swing.JPanel();
        cbRefreshAppraisals = new javax.swing.JCheckBox();
        spAppraisalCodelets = new javax.swing.JScrollPane();
        cbGoalsCodelets = new javax.swing.JPanel();
        cbRefreshGoals = new javax.swing.JCheckBox();
        spGoalCodelets = new javax.swing.JScrollPane();

        setMinimumSize(new java.awt.Dimension(1049, 0));
        setPreferredSize(new java.awt.Dimension(1030, 250));
        setLayout(new java.awt.GridBagLayout());

        tbMotivationalSubsystem.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                tbMotivationalSubsystemStateChanged(evt);
            }
        });

        splDrives.setDividerLocation(400);

        pnTreeDrives.setMinimumSize(new java.awt.Dimension(0, 0));
        pnTreeDrives.setLayout(new java.awt.GridBagLayout());

        cbRefreshDrives.setSelected(true);
        cbRefreshDrives.setText("Auto Refresh");
        cbRefreshDrives.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbRefreshDrivesActionPerformed(evt);
            }
        });
        pnTreeDrives.add(cbRefreshDrives, new java.awt.GridBagConstraints());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 12;
        gridBagConstraints.gridheight = 16;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        pnTreeDrives.add(spMotivationalCodelets, gridBagConstraints);

        splDrives.setLeftComponent(pnTreeDrives);

        pnDrivesChart.setLayout(new java.awt.GridBagLayout());

        pnDrives.setBackground(new java.awt.Color(102, 102, 102));

        javax.swing.GroupLayout pnDrivesLayout = new javax.swing.GroupLayout(pnDrives);
        pnDrives.setLayout(pnDrivesLayout);
        pnDrivesLayout.setHorizontalGroup(
            pnDrivesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        pnDrivesLayout.setVerticalGroup(
            pnDrivesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 18;
        gridBagConstraints.gridheight = 16;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        pnDrivesChart.add(pnDrives, gridBagConstraints);

        cbDrivesChart.setSelected(true);
        cbDrivesChart.setText("Auto Refresh");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        pnDrivesChart.add(cbDrivesChart, gridBagConstraints);

        splDrives.setRightComponent(pnDrivesChart);

        tbMotivationalSubsystem.addTab("Drives", splDrives);

        splEmotional.setDividerLocation(400);

        pnTreeEmotional.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        pnTreeEmotional.add(spEmotionalCodelets, gridBagConstraints);

        cbRefreshEmotionalDrives.setSelected(true);
        cbRefreshEmotionalDrives.setText("Auto Refresh");
        cbRefreshEmotionalDrives.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbRefreshEmotionalDrivesActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        pnTreeEmotional.add(cbRefreshEmotionalDrives, gridBagConstraints);

        splEmotional.setLeftComponent(pnTreeEmotional);

        pnEmotiovalChart.setLayout(new java.awt.GridBagLayout());

        pnEmotional.setBackground(new java.awt.Color(102, 102, 102));

        javax.swing.GroupLayout pnEmotionalLayout = new javax.swing.GroupLayout(pnEmotional);
        pnEmotional.setLayout(pnEmotionalLayout);
        pnEmotionalLayout.setHorizontalGroup(
            pnEmotionalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        pnEmotionalLayout.setVerticalGroup(
            pnEmotionalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 464, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 23;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        pnEmotiovalChart.add(pnEmotional, gridBagConstraints);

        cbEmotionalChart.setSelected(true);
        cbEmotionalChart.setText("Auto Refresh");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        pnEmotiovalChart.add(cbEmotionalChart, gridBagConstraints);

        splEmotional.setRightComponent(pnEmotiovalChart);

        tbMotivationalSubsystem.addTab("Emotional Drives", splEmotional);

        pnMoodCodelts.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 1022;
        gridBagConstraints.ipady = 383;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnMoodCodelts.add(spMoodCodelets, gridBagConstraints);

        cbRefreshMood.setSelected(true);
        cbRefreshMood.setText("Auto Refresh");
        cbRefreshMood.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbRefreshMoodActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        pnMoodCodelts.add(cbRefreshMood, gridBagConstraints);

        tbMotivationalSubsystem.addTab("Moods", pnMoodCodelts);

        pnAppraisalCodelets.setLayout(new java.awt.GridBagLayout());

        cbRefreshAppraisals.setSelected(true);
        cbRefreshAppraisals.setText("Auto Refresh");
        cbRefreshAppraisals.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbRefreshAppraisalsActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        pnAppraisalCodelets.add(cbRefreshAppraisals, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 1022;
        gridBagConstraints.ipady = 422;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnAppraisalCodelets.add(spAppraisalCodelets, gridBagConstraints);

        tbMotivationalSubsystem.addTab("Appraisals", pnAppraisalCodelets);

        cbGoalsCodelets.setLayout(new java.awt.GridBagLayout());

        cbRefreshGoals.setSelected(true);
        cbRefreshGoals.setText("Auto Refresh");
        cbRefreshGoals.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbRefreshGoalsActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        cbGoalsCodelets.add(cbRefreshGoals, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 1022;
        gridBagConstraints.ipady = 445;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        cbGoalsCodelets.add(spGoalCodelets, gridBagConstraints);

        tbMotivationalSubsystem.addTab("Goals", cbGoalsCodelets);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        add(tbMotivationalSubsystem, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void cbRefreshDrivesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbRefreshDrivesActionPerformed
        if (cbRefreshDrives.isSelected()) {
            synchronized (getThreadDrives()) {
                if (getThreadDrives() != null) {
                    getThreadDrives().notify();
                }
            }
        }
    }//GEN-LAST:event_cbRefreshDrivesActionPerformed

    private void cbRefreshEmotionalDrivesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbRefreshEmotionalDrivesActionPerformed
        if (cbRefreshEmotionalDrives.isSelected()) {
            synchronized (getThreadEmotionalDrives()) {
                if (getThreadEmotionalDrives() != null) {
                    getThreadEmotionalDrives().notify();
                }
            }
        }
    }//GEN-LAST:event_cbRefreshEmotionalDrivesActionPerformed

    private void cbRefreshMoodActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbRefreshMoodActionPerformed
        if (cbRefreshMood.isSelected()) {
            synchronized (getThreadMoods()) {
                if (getThreadMoods() != null) {
                    getThreadMoods().notify();
                }
            }
        }
    }//GEN-LAST:event_cbRefreshMoodActionPerformed

    private void cbRefreshAppraisalsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbRefreshAppraisalsActionPerformed
        if (cbRefreshAppraisals.isSelected()) {
            synchronized (getThreadAppraisals()) {
                if (getThreadAppraisals() != null) {
                    getThreadAppraisals().notify();
                }
            }
        }
    }//GEN-LAST:event_cbRefreshAppraisalsActionPerformed

    private void cbRefreshGoalsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbRefreshGoalsActionPerformed
        if (cbRefreshGoals.isSelected()) {
            synchronized (getThreadGoals()) {
                if (getThreadGoals() != null) {
                    getThreadGoals().notify();
                }
            }
        }
    }//GEN-LAST:event_cbRefreshGoalsActionPerformed

    private void tbMotivationalSubsystemStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_tbMotivationalSubsystemStateChanged
        setSelectedIndex(tbMotivationalSubsystem.getSelectedIndex());
        if (getThreadDrives() != null) {
            synchronized (getThreadDrives()) {
                getThreadDrives().notify();
            }
        }

        if (getThreadEmotionalDrives() != null) {
            synchronized (getThreadEmotionalDrives()) {
                getThreadEmotionalDrives().notify();
            }
        }

        if (getThreadMoods() != null) {
            synchronized (getThreadMoods()) {
                getThreadMoods().notify();
            }

        }
        if (getThreadAppraisals() != null) {
            synchronized (getThreadAppraisals()) {
                getThreadAppraisals().notify();
            }
        }
    }//GEN-LAST:event_tbMotivationalSubsystemStateChanged

    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox cbDrivesChart;
    private javax.swing.JCheckBox cbEmotionalChart;
    private javax.swing.JPanel cbGoalsCodelets;
    private javax.swing.JCheckBox cbRefreshAppraisals;
    private javax.swing.JCheckBox cbRefreshDrives;
    private javax.swing.JCheckBox cbRefreshEmotionalDrives;
    private javax.swing.JCheckBox cbRefreshGoals;
    private javax.swing.JCheckBox cbRefreshMood;
    private javax.swing.JPanel pnAppraisalCodelets;
    private javax.swing.JPanel pnDrives;
    private javax.swing.JPanel pnDrivesChart;
    private javax.swing.JPanel pnEmotional;
    private javax.swing.JPanel pnEmotiovalChart;
    private javax.swing.JPanel pnMoodCodelts;
    private javax.swing.JPanel pnTreeDrives;
    private javax.swing.JPanel pnTreeEmotional;
    private javax.swing.JScrollPane spAppraisalCodelets;
    private javax.swing.JScrollPane spEmotionalCodelets;
    private javax.swing.JScrollPane spGoalCodelets;
    private javax.swing.JScrollPane spMoodCodelets;
    private javax.swing.JScrollPane spMotivationalCodelets;
    private javax.swing.JSplitPane splDrives;
    private javax.swing.JSplitPane splEmotional;
    private javax.swing.JTabbedPane tbMotivationalSubsystem;
    // End of variables declaration//GEN-END:variables

    /**
     * @return the threadDrives
     */
    public Thread getThreadDrives() {
        return threadDrives;
    }

    /**
     * @param threadDrives the threadDrives to set
     */
    public void setThreadDrives(Thread threadDrives) {
        this.threadDrives = threadDrives;
    }

    /**
     * @return the threadEmotionalDrives
     */
    public Thread getThreadEmotionalDrives() {
        return threadEmotionalDrives;
    }

    /**
     * @param threadEmotionalDrives the threadEmotionalDrives to set
     */
    public void setThreadEmotionalDrives(Thread threadEmotionalDrives) {
        this.threadEmotionalDrives = threadEmotionalDrives;
    }

    /**
     * @return the threadAppraisals
     */
    public Thread getThreadAppraisals() {
        return threadAppraisals;
    }

    /**
     * @param threadAppraisals the threadAppraisals to set
     */
    public void setThreadAppraisals(Thread threadAppraisals) {
        this.threadAppraisals = threadAppraisals;
    }

    /**
     * @return the threadMoods
     */
    public Thread getThreadMoods() {
        return threadMoods;
    }

    /**
     * @param threadMoods the threadMoods to set
     */
    public void setThreadMoods(Thread threadMoods) {
        this.threadMoods = threadMoods;
    }

    /**
     * @return the threadGoals
     */
    public Thread getThreadGoals() {
        return threadGoals;
    }

    /**
     * @param threadGoals the threadGoals to set
     */
    public void setThreadGoals(Thread threadGoals) {
        this.threadGoals = threadGoals;
    }

    /**
     * @return the selectedIndex
     */
    public int getSelectedIndex() {
        return selectedIndex;
    }

    /**
     * @param selectedIndex the selectedIndex to set
     */
    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
    }

    /**
     * @return the motivationalChart
     */
    public ChartPanel getMotivationalChart() {
        return motivationalChart;
    }

    /**
     * @param motivationalChart the motivationalChart to set
     */
    public void setMotivationalChart(ChartPanel motivationalChart) {
        this.motivationalChart = motivationalChart;
    }

    /**
     * @return the emotionalChart
     */
    public ChartPanel getEmotionalChart() {
        return emotionalChart;
    }

    /**
     * @param emotionalChart the emotionalChart to set
     */
    public void setEmotionalChart(ChartPanel emotionalChart) {
        this.emotionalChart = emotionalChart;
    }

    /**
     * @return the dtMotivationalCodelets
     */
    public DefaultTreeModel getDtMotivationalCodelets() {
        return dtMotivationalCodelets;
    }

    /**
     * @param dtMotivationalCodelets the dtMotivationalCodelets to set
     */
    public void setDtMotivationalCodelets(DefaultTreeModel dtMotivationalCodelets) {
        this.dtMotivationalCodelets = dtMotivationalCodelets;
    }

    /**
     * @return the dtEmotionalCodelets
     */
    public DefaultTreeModel getDtEmotionalCodelets() {
        return dtEmotionalCodelets;
    }

    /**
     * @param dtEmotionalCodelets the dtEmotionalCodelets to set
     */
    public void setDtEmotionalCodelets(DefaultTreeModel dtEmotionalCodelets) {
        this.dtEmotionalCodelets = dtEmotionalCodelets;
    }

    /**
     * @return the dtAppraisalCodelets
     */
    public DefaultTreeModel getDtAppraisalCodelets() {
        return dtAppraisalCodelets;
    }

    /**
     * @param dtAppraisalCodelets the dtAppraisalCodelets to set
     */
    public void setDtAppraisalCodelets(DefaultTreeModel dtAppraisalCodelets) {
        this.dtAppraisalCodelets = dtAppraisalCodelets;
    }

    /**
     * @return the dtMoodCodelets
     */
    public DefaultTreeModel getDtMoodCodelets() {
        return dtMoodCodelets;
    }

    /**
     * @param dtMoodCodelets the dtMoodCodelets to set
     */
    public void setDtMoodCodelets(DefaultTreeModel dtMoodCodelets) {
        this.dtMoodCodelets = dtMoodCodelets;
    }

    /**
     * @return the dtGoalCodelets
     */
    public DefaultTreeModel getDtGoalCodelets() {
        return dtGoalCodelets;
    }

    /**
     * @param dtGoalCodelets the dtGoalCodelets to set
     */
    public void setDtGoalCodelets(DefaultTreeModel dtGoalCodelets) {
        this.dtGoalCodelets = dtGoalCodelets;
    }



    /**
     * @return the stopRefresh
     */
    public boolean isStopRefresh() {
        return stopRefresh;
    }

    /**
     * @param stopRefresh the stopRefresh to set
     */
    public void setStopRefresh(boolean stopRefresh) {
        this.stopRefresh = stopRefresh;
    }

    /**
     * @return the refreshTime
     */
    public long getRefreshTime() {
        return refreshTime;
    }

    /**
     * @param refreshTime the refreshTime to set
     */
    public void setRefreshTime(long refreshTime) {
        this.refreshTime = refreshTime;
    }

    public List<MotivationalCodelet> getMotivationalCodelets() {
        return motivationalCodelets;
    }

    public void setMotivationalCodelets(List<MotivationalCodelet> motivationalCodelets) {
        this.motivationalCodelets = motivationalCodelets;
    }

    public List<EmotionalCodelet> getEmotionalCodelets() {
        return emotionalCodelets;
    }

    public void setEmotionalCodelets(List<EmotionalCodelet> emotionalCodelets) {
        this.emotionalCodelets = emotionalCodelets;
    }

    public List<GoalCodelet> getGoalCodelets() {
        return goalCodelets;
    }

    public void setGoalCodelets(List<GoalCodelet> goalCodelets) {
        this.goalCodelets = goalCodelets;
    }

    public List<AppraisalCodelet> getAppraisalCodelets() {
        return appraisalCodelets;
    }

    public void setAppraisalCodelets(List<AppraisalCodelet> appraisalCodelets) {
        this.appraisalCodelets = appraisalCodelets;
    }

    public List<MoodCodelet> getMoodCodelets() {
        return moodCodelets;
    }

    public void setMoodCodelets(List<MoodCodelet> moodCodelets) {
        this.moodCodelets = moodCodelets;
    }
}
