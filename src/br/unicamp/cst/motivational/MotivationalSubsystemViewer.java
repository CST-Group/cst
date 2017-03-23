/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.cst.motivational;

import br.unicamp.cst.core.entities.*;
import br.unicamp.cst.representation.owrl.RendererJTree;
import br.unicamp.cst.representation.owrl.TreeElement;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import java.util.List;

/**
 *
 * @author du
 */
public class MotivationalSubsystemViewer extends javax.swing.JFrame {
    
    
    private List<Codelet> motivationalCodelets;
    private List<Codelet> emotionalCodelets;
    private List<Codelet> goalCodelets;
    private List<Codelet> appraisalCodelets;
    private List<Codelet> moodCodelets;

    public MotivationalSubsystemViewer(){
        initComponents();
    }
    
    /**
     * Creates new form MotivationalSubsystemViewer
     */
    public MotivationalSubsystemViewer(List<Codelet> motivationalCodelets,
                                       List<Codelet> emotionalCodelets,
                                       List<Codelet> goalCodelets,
                                       List<Codelet> appraisalCodelets,
                                       List<Codelet> moodCodelets) {
        initComponents();
        
        setMotivationalCodelets(motivationalCodelets);
        setEmotionalCodelets(emotionalCodelets);
        setGoalCodelets(goalCodelets);
        setAppraisalCodelets(appraisalCodelets);
        setMoodCodelets(moodCodelets);

        createTreeModelGUI(spCodelets, motivationalCodelets, "Motivational Codelets");
        createTreeModelGUI(spGoals, goalCodelets, "Goal Codelets");
        createTreeModelGUI(spAppraisals, appraisalCodelets, "Appraisal Codelets");
        createTreeModelGUI(spMoods, moodCodelets, "Mood Codelets");
        createTreeModelGUI(spEmotionalCodelets, emotionalCodelets, "Emotional Codelets");


    }


    private void createTreeModelGUI(JScrollPane scrollPane, List<Codelet> motivationalCodelets, String title){
        TreeModel tmMotivationalCodelets = createTreeModel(motivationalCodelets, title, TreeElement.ICON_CODELETS);

        JTree jtTree = new JTree(tmMotivationalCodelets);
        expandAllNodes(jtTree);
        scrollPane.setViewportView(jtTree);
        jtTree.setCellRenderer(new RendererJTree());
    }

    private DefaultMutableTreeNode addIO(Memory m, int icon) {
        String value = m.getName()+" : ";
        Object mval = m.getI();

        if (mval != null) {
            value += mval.toString();
        }
        else
        {
            value += "null";
        }

        DefaultMutableTreeNode memoryNode = addItem(value,icon);
        return memoryNode;
    }

    private DefaultMutableTreeNode addCodelet(Codelet p) {
        DefaultMutableTreeNode codeletNode = addItem(p.getName(), TreeElement.ICON_CODELET);
        List<Memory> inputs = p.getInputs();
        List<Memory> outputs = p.getOutputs();

        for (Memory i : inputs) {
            DefaultMutableTreeNode memoryNode = addIO(i,TreeElement.ICON_INPUT);
            codeletNode.add(memoryNode);
        }
        for (Memory o : outputs) {
            DefaultMutableTreeNode memoryNode = addIO(o,TreeElement.ICON_OUTPUT);
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
        DefaultMutableTreeNode memoryNode = addItem(name,TreeElement.ICON_MEMORIES);

        String value = "";
        Object pval = p.getI();

        if (pval != null)
            value += pval.toString();
        else
            value += "null";

        if (p instanceof  MemoryObject) {
            memoryNode = addItem(name+" : "+value,TreeElement.ICON_MO);
        }
        else if (p instanceof MemoryContainer) {
            memoryNode = addItem(name+" : "+value,TreeElement.ICON_CONTAINER);
            MemoryContainer mc = (MemoryContainer)p;
            for (Memory mo : mc.getAllMemories()) {
                DefaultMutableTreeNode newmemo = addMemory(mo);
                memoryNode.add(newmemo);
            }
        }

        return memoryNode;
    }

    private TreeModel createTreeModel(List<Codelet> codelts, String title, int icon) {
        DefaultMutableTreeNode dmtCodelets = addItem(title, icon);

        for (Codelet codelet: codelts) {
            DefaultMutableTreeNode dmtCodelet = addCodelet(codelet);
            dmtCodelets.add(dmtCodelet);
        }

        TreeModel tm = new DefaultTreeModel(dmtCodelets);
        return tm;
    }

    private void expandAllNodes(JTree tree) {
        expandAllNodes(tree, 0, tree.getRowCount());
    }

    private void expandAllNodes(JTree tree, int startingIndex, int rowCount){
        for(int i=startingIndex;i<rowCount;++i){
            tree.expandRow(i);
        }
        if(tree.getRowCount()!=rowCount){
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
        spCodelets = new javax.swing.JScrollPane();
        spDrives = new javax.swing.JScrollPane();
        splEmotional = new javax.swing.JSplitPane();
        spEmotionalCodelets = new javax.swing.JScrollPane();
        spEmotionalDrives = new javax.swing.JScrollPane();
        spMoods = new javax.swing.JScrollPane();
        spAppraisals = new javax.swing.JScrollPane();
        spGoals = new javax.swing.JScrollPane();
        tbFootBar = new javax.swing.JToolBar();
        mbMenu = new javax.swing.JMenuBar();
        jmFile = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        splDrives.setDividerLocation(300);
        splDrives.setLeftComponent(spCodelets);
        splDrives.setRightComponent(spDrives);

        tbTab.addTab("Drives", splDrives);

        splEmotional.setDividerLocation(300);
        splEmotional.setLeftComponent(spEmotionalCodelets);
        splEmotional.setRightComponent(spEmotionalDrives);

        tbTab.addTab("Emotional Drives", splEmotional);
        tbTab.addTab("Moods", spMoods);
        tbTab.addTab("Appraisals", spAppraisals);
        tbTab.addTab("Goals", spGoals);

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
                .addComponent(tbTab, javax.swing.GroupLayout.DEFAULT_SIZE, 467, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tbFootBar, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
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
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MotivationalSubsystemViewer().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu jmFile;
    private javax.swing.JMenuBar mbMenu;
    private javax.swing.JScrollPane spAppraisals;
    private javax.swing.JScrollPane spCodelets;
    private javax.swing.JScrollPane spDrives;
    private javax.swing.JScrollPane spEmotionalCodelets;
    private javax.swing.JScrollPane spEmotionalDrives;
    private javax.swing.JScrollPane spGoals;
    private javax.swing.JScrollPane spMoods;
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
}
