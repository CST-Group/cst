/**
 * ********************************************************************************************
 * Copyright (c) 2012  DCA-FEEC-UNICAMP
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 * <p>
 * Contributors:
 * K. Raizer, A. L. O. Paraense, E. M. Froes, R. R. Gudwin - initial API and implementation
 * *********************************************************************************************
 */
package br.unicamp.cst.util.viewer;

import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.MemoryContainer;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.core.entities.Mind;
import br.unicamp.cst.util.TreeElement;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Enumeration;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

/**
 *
 * @author rgudwin
 */
public class MindPanel extends javax.swing.JPanel {

    Mind mind;
    MindRenderer wow;
    DefaultMutableTreeNode first;
    DefaultTreeModel memtm;
    MindTreeNode mtn;
    Logger log = Logger.getLogger(MindPanel.class.getCanonicalName());
    
    /**
     * Creates new form MindPanel
     * @param m Mind to be viewed in the MindViewer Panel
     */
    public MindPanel(Mind m) {
        mind = m;
        initComponents();
        MindTreeNode codelets = new MindTreeNode("Codelets",TreeElement.ICON_CONFIGURATION);
        codelets.addCodelets(m);
        MindTreeNode memories = new MindTreeNode("Memories",TreeElement.ICON_MEMORIES);
        memories.addMemories(m);
        mtn = memories;
        DefaultTreeModel codeletsTreeModel = new DefaultTreeModel(codelets);
        codeletsTree.setModel(codeletsTreeModel);
        codeletsTree.setCellRenderer(new MindRenderer());
        DefaultTreeModel memoriesTreeModel = new DefaultTreeModel(memories);
        memtm = memoriesTreeModel;
        memoryTree.setModel(memoriesTreeModel);
        memoryTree.setLargeModel(true);
        memoryTree.setCellRenderer(new MindRenderer());
        StartTimer();
        MouseListener ml;
        ml = new MindMouseAdapter(memoryTree);
        memoryTree.addMouseListener(ml);
        MouseListener ml2;
        ml2 = new MindMouseAdapter(codeletsTree);
        codeletsTree.addMouseListener(ml2);
    }
    
    public String toString(double value) {
        String s = String.format("%4.2f",value);
        return(s);
    }
    
    public Object scanMO(MemoryObject m, String name) {
        if (name.equalsIgnoreCase(m.getName())) {
            Object o = m.getI();
            if (o == null) return("NULL");
            else return(o);
        }
        else return(null);
    }
    
    public Object scanMC(MemoryContainer mc, String name) {
        if (name.equalsIgnoreCase(mc.getName())) {
            Object o = mc.getI();
            if (o == null) return("[NULL]");
            else return(o);
        }
        CopyOnWriteArrayList<Memory> allMemories = new CopyOnWriteArrayList(mc.getAllMemories());
        for (Memory m : allMemories) {
            if (m instanceof MemoryObject) {
                Object o = scanMO((MemoryObject)m,name);
                if (o != null) {
                    return(o);
                }
            }
            else {
                Object o = scanMC((MemoryContainer)m,name);
                if (o != null) {
                    return(o);
                }
            }
        }
        return(null);
    }
    
    public Object scanM(Memory m, String name) {
        if (m instanceof MemoryObject) return(scanMO((MemoryObject)m,name));
        else return(scanMC((MemoryContainer)m,name));
    }
    
    public String getMemoryValue(String nodeName) {
        String memValue = "null";
        CopyOnWriteArrayList<Memory> mm = new CopyOnWriteArrayList(mind.getRawMemory().getAllMemoryObjects());
        for (Memory m : mm) {
            Object o = scanM(m, nodeName);
            if (o != null) {
                String isize;
                if (m instanceof MemoryContainer && m.getName().equalsIgnoreCase(nodeName)) {
                    isize = " {"+((MemoryContainer)m).getAllMemories().size()+"}";
                }    
                else isize = " ("+String.format("%4.2f", m.getEvaluation())+")";
                if (o instanceof Double) {
                    double value = (double)o;
                    memValue = toString(value)+isize;
                }
                else if (o instanceof Float) {
                    float value = (float)o;
                    memValue = toString(value)+isize;
                }
                else if (o instanceof float[]) {
                    float[] value = (float[])o;
                    String aprox;
                    if (value.length == 1)
                        aprox = String.format("%4.2f",value[0]);
                    else if (value.length == 2)
                        aprox = String.format("%4.2f,%4.2f",value[0],value[1]);
                    else if (value.length == 3)
                        aprox = String.format("%4.2f,%4.2f,%4.2f",value[0],value[1],value[2]);
                    else if (value.length == 4)
                        aprox = String.format("%4.2f,%4.2f,%4.2f,%4.2f",value[0],value[1],value[2],value[3]);
                    else if (value.length == 5)
                        aprox = String.format("%4.2f,%4.2f,%4.2f,%4.2f,%4.2f",value[0],value[1],value[2],value[3],value[4]);
                    else if (value.length == 6)
                        aprox = String.format("%4.2f,%4.2f,%4.2f,%4.2f,%4.2f,%4.2f",value[0],value[1],value[2],value[3],value[4],value[5]);
                    else
                        aprox = String.format("%4.2f,%4.2f,%4.2f,%4.2f,%4.2f,%4.2f...",value[0],value[1],value[2],value[3],value[4],value[5]);
                    memValue = aprox+isize;
                }
                else { 
                    memValue = o.toString()+isize;
                }    
                return(memValue);
            }   
        }
        return(memValue);
    }
    
    public void updateTree() {
        DefaultTreeModel tm = (DefaultTreeModel) memoryTree.getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode)tm.getRoot();
        Enumeration<TreeNode> allchildren = root.breadthFirstEnumeration();
        while( allchildren.hasMoreElements() ) {
             DefaultMutableTreeNode node = (DefaultMutableTreeNode) allchildren.nextElement();
             TreeElement element = (TreeElement) node.getUserObject();
             String nodeName = element.getName();
             String value = getMemoryValue(nodeName);
             element.setValue(value);
        }
        tm.nodeChanged((TreeNode)tm.getRoot());
        memoryTree.treeDidChange();
        // Now the same for the Codelets Tab
        tm = (DefaultTreeModel) codeletsTree.getModel();
        root = (DefaultMutableTreeNode)tm.getRoot();
        allchildren = root.breadthFirstEnumeration();
        while( allchildren.hasMoreElements() ) {
             DefaultMutableTreeNode node = (DefaultMutableTreeNode) allchildren.nextElement();
             TreeElement element = (TreeElement) node.getUserObject();
             String nodeName = element.getName();
             String value = getMemoryValue(nodeName);
             element.setValue(value);
        }
        tm.nodeChanged((TreeNode)tm.getRoot());
    }

    public void StartTimer() {
        Timer t = new Timer();
        MindPanel.WOVTimerTask tt = new MindPanel.WOVTimerTask(this);
        t.scheduleAtFixedRate(tt, 0, 100);
    }

    public void tick() {
        updateTree();
    }
    
    class WOVTimerTask extends TimerTask {

        MindPanel wov;
        boolean enabled = true;

        public WOVTimerTask(MindPanel wovi) {
            wov = wovi;
        }

        public void run() {
            if (enabled) {
                wov.tick();
            }
        }

        public void setEnabled(boolean value) {
            enabled = value;
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

        jTabbedPane1 = new javax.swing.JTabbedPane();
        memoryScrollPane = new javax.swing.JScrollPane();
        memoryTree = new javax.swing.JTree();
        codeletsScrollPane = new javax.swing.JScrollPane();
        codeletsTree = new javax.swing.JTree();

        memoryScrollPane.setViewportView(memoryTree);

        jTabbedPane1.addTab("Memories", memoryScrollPane);

        codeletsScrollPane.setViewportView(codeletsTree);

        jTabbedPane1.addTab("Codelets", codeletsScrollPane);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane codeletsScrollPane;
    private javax.swing.JTree codeletsTree;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JScrollPane memoryScrollPane;
    private javax.swing.JTree memoryTree;
    // End of variables declaration//GEN-END:variables
}

class MindMouseAdapter extends MouseAdapter {
    
    public MindMouseAdapter(JTree ttree)  {
        tree = ttree;
    }
    
    private JTree tree;
    
    @Override
    public void mousePressed(MouseEvent e) {
                int selRow = tree.getRowForLocation(e.getX(), e.getY());
                TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
                if(selRow != -1) {
                    if(e.getClickCount() == 1 && e.getButton() == 3) {
                        DefaultMutableTreeNode tn = (DefaultMutableTreeNode)selPath.getLastPathComponent();
                        TreeElement te = (TreeElement)tn.getUserObject();
                        DefaultMutableTreeNode parentnode = (DefaultMutableTreeNode)tn.getParent();
                        final Object element=te.getElement();
                        if(element instanceof Inspectable ) {
                            JPopupMenu popup = new JPopupMenu();
                            JMenuItem jm1 = new JMenuItem("Inspect");
                            ActionListener al = new ActionListener() {
                                public void actionPerformed(ActionEvent e) {
                                    ((Inspectable) element).inspect();
                                }
                            };
                            jm1.addActionListener(al);
                            popup.add(jm1);
                            popup.show(tree, e.getX(), e.getY());
                        }
                        else if (element instanceof MemoryObject) {
                            MemoryObject mo = (MemoryObject) element;
                            JPopupMenu popup = new JPopupMenu();
                            JMenuItem jm1 = new JMenuItem("Inspect Table");
                            ActionListener al = new ActionListener() {
                                public void actionPerformed(ActionEvent e) {
                                    MemoryViewer mv = new MemoryViewer(mo);
                                    mv.setVisible(true);
                                }
                            };
                            jm1.addActionListener(al);
                            popup.add(jm1);
                            if (element instanceof MemoryObject) {
                               JMenuItem jm2 = new JMenuItem("Inspect Tree");
                               ActionListener al2 = new ActionListener() {
                                   public void actionPerformed(ActionEvent e) {
                                       MemoryInspector mv = new MemoryInspector(mo);
                                       mv.setVisible(true);
                                   }
                               };
                               jm2.addActionListener(al2);
                               popup.add(jm2);
                            }   
                            popup.show(tree, e.getX(), e.getY());
                        }
                    }
                }
            }
}
