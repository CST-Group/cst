/***********************************************************************************************
 * Copyright (c) 2012  DCA-FEEC-UNICAMP
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 * <p>
 * Contributors:
 * K. Raizer, A. L. O. Paraense, E. M. Froes, R. R. Gudwin - initial API and implementation
 * **********************************************************************************************/
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
public class CodeletPanel extends javax.swing.JPanel {

    Mind mind;
    DefaultMutableTreeNode first;
    DefaultTreeModel memtm;
    MindTreeNode mtn;
    Logger log = Logger.getLogger(CodeletPanel.class.getCanonicalName());
    /**
     * Creates new form MindPanel
     * @param m Mind to be viewed in the MindViewer Panel
     */
    public CodeletPanel(Mind m, String group, String rootName) {
        mind = m;
        initComponents();
        MindTreeNode codelets = new MindTreeNode(rootName,TreeElement.ICON_CODELETS);
        codelets.addCodelets(m,group);
        DefaultTreeModel codeletsTreeModel = new DefaultTreeModel(codelets);
        codeletsTree.setModel(codeletsTreeModel);
        codeletsTree.setCellRenderer(new MindRenderer(false));
        StartTimer();
        MouseListener ml;
        ml = new MindMouseAdapter(codeletsTree);
        codeletsTree.addMouseListener(ml);
        this.expandAllNodes();
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
        String memValue = "";
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
                    switch (value.length) {
                        case 1:
                            aprox = String.format("%4.2f",value[0]);
                            break;
                        case 2:
                            aprox = String.format("%4.2f,%4.2f",value[0],value[1]);
                            break;
                        case 3:
                            aprox = String.format("%4.2f,%4.2f,%4.2f",value[0],value[1],value[2]);
                            break;
                        case 4:
                            aprox = String.format("%4.2f,%4.2f,%4.2f,%4.2f",value[0],value[1],value[2],value[3]);
                            break;
                        case 5:
                            aprox = String.format("%4.2f,%4.2f,%4.2f,%4.2f,%4.2f",value[0],value[1],value[2],value[3],value[4]);
                            break;
                        case 6:
                            aprox = String.format("%4.2f,%4.2f,%4.2f,%4.2f,%4.2f,%4.2f",value[0],value[1],value[2],value[3],value[4],value[5]);
                            break;
                        default:
                            aprox = String.format("%4.2f,%4.2f,%4.2f,%4.2f,%4.2f,%4.2f...",value[0],value[1],value[2],value[3],value[4],value[5]);
                            break;
                    }
                    memValue = aprox+isize;
                }
                else if (o instanceof double[]) {
                    double[] value = (double[])o;
                    String aprox;
                    switch (value.length) {
                        case 1:
                            aprox = String.format("%4.2f",value[0]);
                            break;
                        case 2:
                            aprox = String.format("%4.2f,%4.2f",value[0],value[1]);
                            break;
                        case 3:
                            aprox = String.format("%4.2f,%4.2f,%4.2f",value[0],value[1],value[2]);
                            break;
                        case 4:
                            aprox = String.format("%4.2f,%4.2f,%4.2f,%4.2f",value[0],value[1],value[2],value[3]);
                            break;
                        case 5:
                            aprox = String.format("%4.2f,%4.2f,%4.2f,%4.2f,%4.2f",value[0],value[1],value[2],value[3],value[4]);
                            break;
                        case 6:
                            aprox = String.format("%4.2f,%4.2f,%4.2f,%4.2f,%4.2f,%4.2f",value[0],value[1],value[2],value[3],value[4],value[5]);
                            break;
                        default:
                            aprox = String.format("%4.2f,%4.2f,%4.2f,%4.2f,%4.2f,%4.2f...",value[0],value[1],value[2],value[3],value[4],value[5]);
                            break;
                    }
                    memValue = aprox+isize;
                }
                else memValue = o.toString()+isize;
                return(memValue);
            }   
        }
        return(memValue);
    }
    
    public void updateTree() {
        DefaultTreeModel tm; 
        DefaultMutableTreeNode root;
        Enumeration<TreeNode> allchildren;
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
    
    public void expandAllNodes() {
        expandAllNodes(codeletsTree, 0, codeletsTree.getRowCount());
    }

    public void expandAllNodes(JTree tree, int startingIndex, int rowCount) {
        for (int i = startingIndex; i < rowCount; ++i) {
            tree.expandRow(i);
        }
        if (tree.getRowCount() != rowCount) {
            expandAllNodes(tree, rowCount, tree.getRowCount());
        }
    }

    public void StartTimer() {
        Timer t = new Timer();
        CodeletPanel.WOVTimerTask tt = new CodeletPanel.WOVTimerTask(this);
        t.scheduleAtFixedRate(tt, 0, 100);
    }

    public void tick() {
        updateTree();
    }
    
    class WOVTimerTask extends TimerTask {

        CodeletPanel wov;
        boolean enabled = true;

        public WOVTimerTask(CodeletPanel wovi) {
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

        codeletsScrollPane = new javax.swing.JScrollPane();
        codeletsTree = new javax.swing.JTree();

        codeletsScrollPane.setViewportView(codeletsTree);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(codeletsScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 395, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(codeletsScrollPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 311, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane codeletsScrollPane;
    private javax.swing.JTree codeletsTree;
    // End of variables declaration//GEN-END:variables
}

class CodeletMouseAdapter extends MouseAdapter {
    
    public CodeletMouseAdapter(JTree ttree)  {
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
