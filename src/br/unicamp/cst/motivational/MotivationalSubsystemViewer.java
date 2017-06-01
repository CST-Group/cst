/*******************************************************************************
 * Copyright (c) 2012  DCA-FEEC-UNICAMP
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 * 
 * Contributors:
 *     K. Raizer, A. L. O. Paraense, R. R. Gudwin - initial API and implementation
 ******************************************************************************/
package br.unicamp.cst.motivational;

import br.unicamp.cst.core.entities.*;
import br.unicamp.cst.util.RendererJTree;
import br.unicamp.cst.util.TreeElement;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author du
 */
public class MotivationalSubsystemViewer extends javax.swing.JFrame {


    private List<Codelet> motivationalCodelets;
    private List<Codelet> emotionalCodelets;
    private List<Codelet> goalCodelets;
    private List<Codelet> appraisalCodelets;
    private List<Codelet> moodCodelets;
    private int timeRefresh = 1000;

    private Thread threadDrives;
    private Thread threadEmotionalDrives;

    private Map<String, DefaultMutableTreeNode> mapMemoryNodes;
    private boolean bStopRefresh = false;

    private DefaultTreeModel dtMotivationalCodelets;
    private DefaultTreeModel dtEmotionalCodelets;
    private DefaultTreeModel dtAppraisalCodelets;
    private DefaultTreeModel dtMoodCodelets;
    private DefaultTreeModel dtGoalCodelets;

    public MotivationalSubsystemViewer() {
        initComponents();
    }

    /**
     * Creates new form MotivationalSubsystemViewer
     */
    public MotivationalSubsystemViewer(List<Codelet> motivationalCodelets,
                                       List<Codelet> emotionalCodelets,
                                       List<Codelet> goalCodelets,
                                       List<Codelet> appraisalCodelets,
                                       List<Codelet> moodCodelets,
                                       int timeRefresh) {
        initComponents();
        formStyle();

        setMotivationalCodelets(motivationalCodelets);
        setEmotionalCodelets(emotionalCodelets);
        setGoalCodelets(goalCodelets);
        setAppraisalCodelets(appraisalCodelets);
        setMoodCodelets(moodCodelets);
        setTimeRefresh(timeRefresh);

        setMapMemoryNodes(new HashMap<String, DefaultMutableTreeNode>());

        setDtMotivationalCodelets(createTreeModelGUI(spMotivationalCodelets, motivationalCodelets, "Motivational Codelets"));
        setDtGoalCodelets(createTreeModelGUI(spGoalCodelets, goalCodelets, "Goal Codelets"));
        setDtAppraisalCodelets(createTreeModelGUI(spAppraisalCodelets, appraisalCodelets, "Appraisal Codelets"));
        setDtMoodCodelets(createTreeModelGUI(spMoodCodelets, moodCodelets, "Mood Codelets"));
        setDtGoalCodelets(createTreeModelGUI(spEmotionalCodelets, emotionalCodelets, "Emotional Codelets"));

        startThreads();

    }


    private void startThreads() {
        threadDrives = new Thread() {
            @Override
            public void run() {
                DefaultCategoryDataset dataset = new DefaultCategoryDataset();

                synchronized (pnDrives) {
                    pnDrives.setLayout(new BorderLayout());
                    pnDrives.add(createChart(dataset, "Motivational Codelets", "Drives", "Activation", PlotOrientation.VERTICAL), BorderLayout.CENTER);
                    pnDrives.validate();
                }

                while (!bStopRefresh) {

                    while (tbTab.getSelectedIndex() != 0 && !bStopRefresh) ;

                    updateValuesInTree(motivationalCodelets, getDtMotivationalCodelets());

                    spMotivationalCodelets.revalidate();
                    spMotivationalCodelets.repaint();

                    updateValuesInChart(dataset, motivationalCodelets);
                    try {
                        Thread.currentThread().sleep(getTimeRefresh());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        };

        threadEmotionalDrives = new Thread() {
            @Override
            public void run() {
                DefaultCategoryDataset dataset = new DefaultCategoryDataset();

                synchronized (pnEmotional) {
                    pnEmotional.setLayout(new BorderLayout());
                    pnEmotional.add(createChart(dataset, "Emotional Codelets", "Emotional Drives", "Activation", PlotOrientation.VERTICAL), BorderLayout.CENTER);
                    pnEmotional.validate();
                }

                while (!bStopRefresh) {


                        while (tbTab.getSelectedIndex() != 1 && !bStopRefresh) ;

                        updateValuesInTree(emotionalCodelets, getDtMotivationalCodelets());

                        updateValuesInChart(dataset, emotionalCodelets);
                        try {
                            Thread.currentThread().sleep(getTimeRefresh());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                }
            }
        };

        threadEmotionalDrives.start();
        threadDrives.start();
    }


    private synchronized void updateValuesInChart(DefaultCategoryDataset dataset, List<Codelet> codelets) {
        ArrayList<Codelet> tempCodeletsList = new ArrayList<Codelet>();
        tempCodeletsList.addAll(codelets);

        synchronized (tempCodeletsList) {
            for (Codelet co : tempCodeletsList) {
                dataset.addValue(co.getActivation(), co.getName(), "activation");
            }
        }
    }

    private void updateValuesInTree(List<Codelet> codelets, DefaultTreeModel defaultTreeModel) {
        for (Codelet codelet : codelets) {

            ArrayList<Memory> allMemories = new ArrayList<>();
            allMemories.addAll(codelet.getInputs());
            allMemories.addAll(codelet.getOutputs());

            for (Memory memory : allMemories) {
                DefaultMutableTreeNode node = getMapMemoryNodes().get(memory.getName()+"_"+codelet.getName());
                if(node !=null) {
                    String value = memory.getName() + " : ";
                    Object mval = memory.getI();

                    if (mval != null) {
                        value += mval.toString();
                    } else {
                        value += "null";
                    }

                    Object o = new TreeElement(value, TreeElement.NODE_NORMAL, value, ((TreeElement)node.getUserObject()).getIcon());
                    node.setUserObject(o);
                    defaultTreeModel.nodeChanged(node);
                }
            }
        }
    }


    private synchronized ChartPanel createChart(DefaultCategoryDataset dataset, String title, String categoryAxisLabel, String valueAxisLabel, PlotOrientation chartType) {

        final JFreeChart chart = ChartFactory.createBarChart(
                title,
                categoryAxisLabel,
                valueAxisLabel,
                dataset,
                chartType,
                true,
                true,
                false
        );

        final CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);
        chart.setBackgroundPaint(Color.lightGray);

        ChartPanel localChartPanel = new ChartPanel(chart);
        localChartPanel.setVisible(true);
        localChartPanel.setDomainZoomable(true);

        return localChartPanel;
    }

    private DefaultTreeModel createTreeModelGUI(JScrollPane scrollPane, List<Codelet> codelets, String title) {
        DefaultTreeModel treeCodelets = createTreeModel(codelets, title, TreeElement.ICON_CODELETS);

        JTree jtTree = new JTree(treeCodelets);
        expandAllNodes(jtTree);
        scrollPane.setViewportView(jtTree);
        jtTree.setCellRenderer(new RendererJTree());

        return treeCodelets;
    }

    private DefaultMutableTreeNode addIO(String codeletName, Memory m, int icon) {
        String value = m.getName() + " : ";
        Object mval = m.getI();

        if (mval != null) {
            value += mval.toString();
        } else {
            value += "null";
        }

        DefaultMutableTreeNode memoryNode = addItem(value, icon);

        getMapMemoryNodes().put(m.getName()+"_"+codeletName, memoryNode);

        return memoryNode;
    }

    private DefaultMutableTreeNode addCodelet(Codelet p) {
        DefaultMutableTreeNode codeletNode = addItem(p.getName(), TreeElement.ICON_CODELET);
        List<Memory> inputs = p.getInputs();
        List<Memory> outputs = p.getOutputs();

        for (Memory i : inputs) {
            DefaultMutableTreeNode memoryNode = addIO(p.getName(), i, TreeElement.ICON_INPUT);
            codeletNode.add(memoryNode);
        }

        for (Memory o : outputs) {
            DefaultMutableTreeNode memoryNode = addIO(p.getName(), o, TreeElement.ICON_OUTPUT);
            codeletNode.add(memoryNode);
        }


        return codeletNode;
    }

    private DefaultMutableTreeNode addItem(String p, int icon_type) {
        Object o = new TreeElement(p, TreeElement.NODE_NORMAL, p, icon_type);
        DefaultMutableTreeNode memoryNode = new DefaultMutableTreeNode(o);
        return memoryNode;
    }

    private DefaultMutableTreeNode addMemory(Memory p) {
        String name = p.getName();
        DefaultMutableTreeNode memoryNode = addItem(name, TreeElement.ICON_MEMORIES);

        String value = "";
        Object pval = p.getI();

        if (pval != null)
            value += pval.toString();
        else
            value += "null";

        if (p instanceof MemoryObject) {
            memoryNode = addItem(name + " : " + value, TreeElement.ICON_MO);
        } else if (p instanceof MemoryContainer) {
            memoryNode = addItem(name + " : " + value, TreeElement.ICON_CONTAINER);
            MemoryContainer mc = (MemoryContainer) p;
            for (Memory mo : mc.getAllMemories()) {
                DefaultMutableTreeNode newmemo = addMemory(mo);
                memoryNode.add(newmemo);
            }
        }

        return memoryNode;
    }

    private DefaultTreeModel createTreeModel(List<Codelet> codelts, String title, int icon) {
        DefaultMutableTreeNode dmtCodelets = addItem(title, icon);

        for (Codelet codelet : codelts) {
            DefaultMutableTreeNode dmtCodelet = addCodelet(codelet);
            dmtCodelets.add(dmtCodelet);
        }

        DefaultTreeModel tm = new DefaultTreeModel(dmtCodelets);
        return tm;
    }

    private void expandAllNodes(JTree tree) {
        expandAllNodes(tree, 0, tree.getRowCount());
    }

    private void expandAllNodes(JTree tree, int startingIndex, int rowCount) {
        for (int i = startingIndex; i < rowCount; ++i) {
            tree.expandRow(i);
        }
        if (tree.getRowCount() != rowCount) {
            expandAllNodes(tree, rowCount, tree.getRowCount());
        }
    }


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tbTab = new javax.swing.JTabbedPane();
        splDrives = new javax.swing.JSplitPane();
        spMotivationalCodelets = new javax.swing.JScrollPane();
        pnDrives = new javax.swing.JPanel();
        splEmotional = new javax.swing.JSplitPane();
        spEmotionalCodelets = new javax.swing.JScrollPane();
        pnEmotional = new javax.swing.JPanel();
        spMoodCodelets = new javax.swing.JScrollPane();
        spAppraisalCodelets = new javax.swing.JScrollPane();
        spGoalCodelets = new javax.swing.JScrollPane();
        tbFootBar = new javax.swing.JToolBar();
        mbMenu = new javax.swing.JMenuBar();
        jmFile = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        splDrives.setDividerLocation(300);
        splDrives.setLeftComponent(spMotivationalCodelets);

        javax.swing.GroupLayout pnDrivesLayout = new javax.swing.GroupLayout(pnDrives);
        pnDrives.setLayout(pnDrivesLayout);
        pnDrivesLayout.setHorizontalGroup(
                pnDrivesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 523, Short.MAX_VALUE)
        );
        pnDrivesLayout.setVerticalGroup(
                pnDrivesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 417, Short.MAX_VALUE)
        );

        splDrives.setRightComponent(pnDrives);

        tbTab.addTab("Drives", splDrives);

        splEmotional.setDividerLocation(300);
        splEmotional.setLeftComponent(spEmotionalCodelets);

        javax.swing.GroupLayout pnEmotionalLayout = new javax.swing.GroupLayout(pnEmotional);
        pnEmotional.setLayout(pnEmotionalLayout);
        pnEmotionalLayout.setHorizontalGroup(
                pnEmotionalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 523, Short.MAX_VALUE)
        );
        pnEmotionalLayout.setVerticalGroup(
                pnEmotionalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 417, Short.MAX_VALUE)
        );

        splEmotional.setRightComponent(pnEmotional);

        tbTab.addTab("Emotional Drives", splEmotional);
        tbTab.addTab("Moods", spMoodCodelets);
        tbTab.addTab("Appraisals", spAppraisalCodelets);
        tbTab.addTab("Goals", spGoalCodelets);

        tbFootBar.setRollover(true);

        jmFile.setText("File");
        mbMenu.add(jmFile);

        setJMenuBar(mbMenu);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(tbFootBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(tbTab, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 580, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(tbTab)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tbFootBar, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    public void formStyle() {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MotivationalSubsystemViewer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MotivationalSubsystemViewer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MotivationalSubsystemViewer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MotivationalSubsystemViewer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu jmFile;
    private javax.swing.JMenuBar mbMenu;
    private javax.swing.JPanel pnDrives;
    private javax.swing.JPanel pnEmotional;
    private javax.swing.JScrollPane spAppraisalCodelets;
    private javax.swing.JScrollPane spEmotionalCodelets;
    private javax.swing.JScrollPane spGoalCodelets;
    private javax.swing.JScrollPane spMoodCodelets;
    private javax.swing.JScrollPane spMotivationalCodelets;
    private javax.swing.JSplitPane splDrives;
    private javax.swing.JSplitPane splEmotional;
    private javax.swing.JToolBar tbFootBar;
    private javax.swing.JTabbedPane tbTab;
    // End of variables declaration//GEN-END:variables

    /**
     * @return the motivationalCodelets
     */
    public List<Codelet> getMotivationalCodelets() {
        return motivationalCodelets;
    }

    /**
     * @param motivationalCodelets the motivationalCodelets to set
     */
    public void setMotivationalCodelets(List<Codelet> motivationalCodelets) {
        this.motivationalCodelets = motivationalCodelets;
    }

    /**
     * @return the emotionalCodelets
     */
    public List<Codelet> getEmotionalCodelets() {
        return emotionalCodelets;
    }

    /**
     * @param emotionalCodelets the emotionalCodelets to set
     */
    public void setEmotionalCodelets(List<Codelet> emotionalCodelets) {
        this.emotionalCodelets = emotionalCodelets;
    }

    /**
     * @return the goalCodelets
     */
    public List<Codelet> getGoalCodelets() {
        return goalCodelets;
    }

    /**
     * @param goalCodelets the goalCodelets to set
     */
    public void setGoalCodelets(List<Codelet> goalCodelets) {
        this.goalCodelets = goalCodelets;
    }

    /**
     * @return the appraisalCodelets
     */
    public List<Codelet> getAppraisalCodelets() {
        return appraisalCodelets;
    }

    /**
     * @param appraisalCodelets the appraisalCodelets to set
     */
    public void setAppraisalCodelets(List<Codelet> appraisalCodelets) {
        this.appraisalCodelets = appraisalCodelets;
    }

    /**
     * @return the moodCodelets
     */
    public List<Codelet> getMoodCodelets() {
        return moodCodelets;
    }

    /**
     * @param moodCodelets the moodCodelets to set
     */
    public void setMoodCodelets(List<Codelet> moodCodelets) {
        this.moodCodelets = moodCodelets;
    }

    public int getTimeRefresh() {
        return timeRefresh;
    }

    public void setTimeRefresh(int timeRefresh) {
        this.timeRefresh = timeRefresh;
    }

    public Map<String, DefaultMutableTreeNode> getMapMemoryNodes() {
        return mapMemoryNodes;
    }

    public void setMapMemoryNodes(Map<String, DefaultMutableTreeNode> mapMemoryNodes) {
        this.mapMemoryNodes = mapMemoryNodes;
    }

    private DefaultTreeModel getDtMotivationalCodelets() {
        return dtMotivationalCodelets;
    }

    private void setDtMotivationalCodelets(DefaultTreeModel dtMotivationalCodelets) {
        this.dtMotivationalCodelets = dtMotivationalCodelets;
    }

    private DefaultTreeModel getDtEmotionalCodelets() {
        return dtEmotionalCodelets;
    }

    private void setDtEmotionalCodelets(DefaultTreeModel dtEmotionalCodelets) {
        this.dtEmotionalCodelets = dtEmotionalCodelets;
    }

    private DefaultTreeModel getDtAppraisalCodelets() {
        return dtAppraisalCodelets;
    }

    private void setDtAppraisalCodelets(DefaultTreeModel dtAppraisalCodelets) {
        this.dtAppraisalCodelets = dtAppraisalCodelets;
    }

    private DefaultTreeModel getDtMoodCodelets() {
        return dtMoodCodelets;
    }

    private void setDtMoodCodelets(DefaultTreeModel dtMoodCodelets) {
        this.dtMoodCodelets = dtMoodCodelets;
    }

    private DefaultTreeModel getDtGoalCodelets() {
        return dtGoalCodelets;
    }

    private void setDtGoalCodelets(DefaultTreeModel dtGoalCodelets) {
        this.dtGoalCodelets = dtGoalCodelets;
    }
}
