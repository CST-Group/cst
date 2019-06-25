/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.cst.util;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.MemoryContainer;
import br.unicamp.cst.core.entities.Mind;
import java.util.Iterator;
import java.util.List;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import org.jsoar.kernel.memory.Wme;
import org.jsoar.kernel.symbols.Identifier;
import org.jsoar.kernel.symbols.Symbol;

/**
 *
 * @author du
 */
public class TreeViewerUtil {
    
    public static DefaultMutableTreeNode addRootNode(String rootNodeName) {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(new TreeElement(rootNodeName, TreeElement.NODE_NORMAL, null, TreeElement.ICON_CONFIGURATION));
        return (root);
    }

    public static DefaultTreeModel createTreeModelGUI(JScrollPane scrollPane, List<? extends Codelet> codelets, String title) {

        DefaultTreeModel treeCodelets = createTreeModel(codelets, title, TreeElement.ICON_CODELETS);

        JTree jtTree = new JTree(treeCodelets);
        jtTree.setCellRenderer(new RendererJTree());
        expandAllNodes(jtTree);
        scrollPane.setViewportView(jtTree);
        
        return treeCodelets;
    }

    public static DefaultTreeModel createTreeModelGUIbyIdentifiers(JScrollPane scrollPane, List<Identifier> identifiers, String title){

        DefaultMutableTreeNode root = addRootNode("Root");
        for (Identifier ii : identifiers) {
            DefaultMutableTreeNode o = addIdentifier(ii,"State");
            root.add(o);
        }
        
        DefaultTreeModel treeCodelets =  new DefaultTreeModel(root);

        JTree jtTree = new JTree(treeCodelets);
        expandAllNodes(jtTree);
        scrollPane.setViewportView(jtTree);
        jtTree.setCellRenderer(new RendererJTree());

        return  treeCodelets;

    }

    
    public static DefaultMutableTreeNode addMind(Mind m) {

        DefaultMutableTreeNode mindNode = addItem("Mind", TreeElement.ICON_MIND);
        DefaultMutableTreeNode codeletsNode = addItem("Codelets", TreeElement.ICON_CODELETS);
        mindNode.add(codeletsNode);
        List<Codelet> codelets = m.getCodeRack().getAllCodelets();
        for (Codelet oo : codelets) {
            DefaultMutableTreeNode newcodeletNode = addCodelet(oo);
            codeletsNode.add(newcodeletNode);
        }
        DefaultMutableTreeNode memoriesNode = addItem("Memories", TreeElement.ICON_MEMORIES);
        mindNode.add(memoriesNode);
        List<Memory> memories = m.getRawMemory().getAllMemoryObjects();
        for (Memory mo : memories) {
            DefaultMutableTreeNode memoryNode = addMemory(mo);
            memoriesNode.add(memoryNode);
        }

        return (mindNode);
    }

    public static DefaultMutableTreeNode addIO(Memory m, int icon) {
        String value = m.getName() + " : ";
        Object mval = m.getI();
        if (mval != null) {
            value += mval.toString();
        } else {
            value += null;
        }
        DefaultMutableTreeNode memoryNode = addItem(value, icon);
        return (memoryNode);
    }

    public static void collapseAllNodes(JTree tree) {
       int row = tree.getRowCount() - 1;
       while (row >= 0) {
          tree.collapseRow(row);
          row--;
       }
       tree.expandRow(0);
       row = tree.getRowCount() - 1;
       while (row >= 0) {
          tree.expandRow(row);
          row--;
       }
    }
    
    public static DefaultMutableTreeNode addCodelet(Codelet p) {
        DefaultMutableTreeNode codeletNode = addItem(p.getName(), TreeElement.ICON_CODELET);
        List<Memory> inputs = p.getInputs();
        List<Memory> outputs = p.getOutputs();
        List<Memory> broadcasts = p.getBroadcast();
        for (Memory i : inputs) {
            DefaultMutableTreeNode memoryNode = addIO(i, TreeElement.ICON_INPUT);
            codeletNode.add(memoryNode);
        }
        for (Memory o : outputs) {
            DefaultMutableTreeNode memoryNode = addIO(o, TreeElement.ICON_OUTPUT);
            codeletNode.add(memoryNode);
        }
        for (Memory b : broadcasts) {
            DefaultMutableTreeNode memoryNode = addIO(b, TreeElement.ICON_BROADCAST);
            codeletNode.add(memoryNode);
        }
        return (codeletNode);
    }

    public static DefaultMutableTreeNode addItem(String p, int icon_type) {
        Object o = new TreeElement(p, TreeElement.NODE_NORMAL, p, icon_type);
        DefaultMutableTreeNode memoryNode = new DefaultMutableTreeNode(o);
        return (memoryNode);
    }

    public static DefaultMutableTreeNode addMemory(Memory p) {
        String name = p.getName();
        DefaultMutableTreeNode memoryNode = addItem(name, TreeElement.ICON_MEMORIES);
        if (p.getClass().getCanonicalName().equals("br.unicamp.cst.core.entities.MemoryObject")) {
            String value = "";
            Object pval = p.getI();
            if (pval != null) {
                value += pval.toString();
            } else {
                value += "null";
            }
            memoryNode = addItem(name + " : " + value, TreeElement.ICON_MO);
        } else if (p.getClass().getCanonicalName().equals("br.unicamp.cst.core.entities.MemoryContainer")) {
            String value = "";
            Object pval = p.getI();
            if (pval != null) {
                value += pval.toString();
            } else {
                value += "null";
            }
            memoryNode = addItem(name + " : " + value, TreeElement.ICON_CONTAINER);
            MemoryContainer mc = (MemoryContainer) p;
            for (Memory mo : mc.getAllMemories()) {
                DefaultMutableTreeNode newmemo = addMemory(mo);
                memoryNode.add(newmemo);
            }
        }
        return (memoryNode);
    }

    public static TreeModel createTreeModel(Mind m) {
        DefaultMutableTreeNode o = addMind(m);
        TreeModel tm = new DefaultTreeModel(o);
        return (tm);
    }
    
    
    
    public static DefaultMutableTreeNode addIdentifier(Identifier ido, String attr) {
        
        DefaultMutableTreeNode idNode = new DefaultMutableTreeNode(new TreeElement(attr+ " [" + ido.toString()+"]", TreeElement.NODE_NORMAL, ido.toString(), TreeElement.ICON_OBJECT));
        Iterator<Wme> It = ido.getWmes();
        while (It.hasNext()) {
            Wme wme = It.next();
            Symbol a = wme.getAttribute();
            Symbol v = wme.getValue();
            Identifier testv = v.asIdentifier();
            if (testv != null) { // v is an identifier
               String preference = "";
               
               if (wme.isAcceptable()) preference = " +";
               DefaultMutableTreeNode part = addIdentifier(testv,a.toString()+preference);
               idNode.add(part); 
            }
            else { 
               DefaultMutableTreeNode valueNode = new DefaultMutableTreeNode(new TreeElement(a.toString()+": "+v.toString(), TreeElement.NODE_NORMAL, a.toString()+": "+v.toString(), TreeElement.ICON_QUALITYDIM));
               idNode.add(valueNode);
            } 
        }    
        return(idNode);    
    }

    public static DefaultTreeModel createTreeModel(List<? extends Codelet> codelts, String title, int icon) {
        DefaultMutableTreeNode dmtCodelets = addItem(title, icon);

        for (Codelet codelet : codelts) {
            DefaultMutableTreeNode dmtCodelet = addCodelet(codelet);
            dmtCodelets.add(dmtCodelet);
        }

        DefaultTreeModel tm = new DefaultTreeModel(dmtCodelets);
        return tm;
    }

    public static void expandAllNodes(JTree tree) {
        expandAllNodes(tree, 0, tree.getRowCount());
    }

    public static void expandAllNodes(JTree tree, int startingIndex, int rowCount) {
        for (int i = startingIndex; i < rowCount; ++i) {
            tree.expandRow(i);
        }
        if (tree.getRowCount() != rowCount) {
            expandAllNodes(tree, rowCount, tree.getRowCount());
        }
    }

    
}
