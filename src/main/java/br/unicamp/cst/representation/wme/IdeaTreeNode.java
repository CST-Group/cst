/***********************************************************************************************
 * Copyright (c) 2012  DCA-FEEC-UNICAMP
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 * <p>
 * Contributors:
 * K. Raizer, A. L. O. Paraense, E. M. Froes, R. R. Gudwin - initial API and implementation
 ***********************************************************************************************/
package br.unicamp.cst.representation.wme;

import br.unicamp.cst.util.TreeElement;
import java.util.Collections;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;


/**
 *
 * @author rgudwin
 */
public class IdeaTreeNode extends DefaultMutableTreeNode {
    
    static CopyOnWriteArrayList<Idea> repr = new CopyOnWriteArrayList();
    
    public IdeaTreeNode() {
        super(new TreeElement("State", TreeElement.NODE_NORMAL, "State", TreeElement.ICON_MIND));
    }
    
    public IdeaTreeNode(String name, int node_type, Object element, int typeIcon) {
        super(new TreeElement(name,node_type,element,typeIcon));
    }
    
    public IdeaTreeNode(String name, String value, int node_type, Object element, int typeIcon) {
        super(new TreeElement(name,value,node_type,element,typeIcon));
    }
    
    public IdeaTreeNode(String name, int icon_type) {
        super(new TreeElement(name, TreeElement.NODE_NORMAL, name, icon_type));
    }
    
    public IdeaTreeNode addRootNode(String rootNodeName) {
        Idea rootWM = new Idea(rootNodeName);
        IdeaTreeNode root = new IdeaTreeNode(rootNodeName, TreeElement.NODE_NORMAL, rootWM, TreeElement.ICON_CONFIGURATION);
        return(root);
    }
    
    public IdeaTreeNode genIdNode(Idea ido) {
        String value = "";
        if (!ido.getValue().toString().equals("")) value = " [" + ido.getValue().toString()+"]"; 
        IdeaTreeNode idNode = new IdeaTreeNode(ido.getName()+value, TreeElement.NODE_NORMAL, ido, TreeElement.ICON_OBJECT); 
        return(idNode);
    }
    
    public IdeaTreeNode genFinalIdNode(Idea ido) {
        String value = "";
        if (!ido.getValue().toString().equals("")) value = " [<font color=red>" + ido.getValue().toString()+"</font>]";
        IdeaTreeNode idNode = new IdeaTreeNode(ido.getName()+value, TreeElement.NODE_NORMAL, ido, TreeElement.ICON_OBJECT2); 
        return(idNode);
    }
    
    public IdeaTreeNode genValNode(Idea node) {
        IdeaTreeNode valueNode = new IdeaTreeNode(node.getName()+": "+node.getValue().toString(), TreeElement.NODE_NORMAL, node, TreeElement.ICON_QUALITYDIM);
        return(valueNode);
    }
    
//    public IdeaTreeNode genValNode2(String a, String v) {
//        IdeaTreeNode valueNode = new IdeaTreeNode(a+": "+v, TreeElement.NODE_NORMAL, a, TreeElement.ICON_QUALITYDIM);
//        return(valueNode);
//    }
    
    public IdeaTreeNode getIdNode(Idea node) {
        // IF the ido is already in the list, just return it
        for (Idea ii : repr) {
            if (equals(node,ii)) {
                IdeaTreeNode idNode = genFinalIdNode(node);
                //System.out.println("getIdNode: Já está na lista ... "+ido.toString());                
                return idNode;
            }
        }
        // ELSE, first add it to the list
        repr.add(node);
        // THEN generate a new node for it
        IdeaTreeNode idNode;
        if (node.isType(0)) idNode = genIdNode(node);
        else idNode = genFinalIdNode(node);
        return(idNode);
    }
    
    public IdeaTreeNode addWMNode(Idea n) {
        if (n.isType(0) || n.isType(2)) { // n is an identifier
                IdeaTreeNode part = addIdentifier(n);
                return part; 
            }
            else { // v is a value 
               IdeaTreeNode valueNode = addValue(n);
               return valueNode;
            } 
    }
    
    int recursion = 0;
    public IdeaTreeNode addIdentifier(Idea node) {
        recursion++;
        IdeaTreeNode idNode = getIdNode(node);
        for (Idea n : node.getL()) {
            if (n.isType(0) || n.isType(2)) { // n is an identifier
                IdeaTreeNode part = addIdentifier(n);
               idNode.add(part); 
            }
            else { // v is a value 
               IdeaTreeNode valueNode = genValNode(n);
               idNode.add(valueNode);
            }  
        }
        add(idNode);
        return(idNode);    
    }
    
    public IdeaTreeNode addValue(Idea node) {
        IdeaTreeNode valueNode = genValNode(node);
        add(valueNode);
        return(valueNode); 
    }
    
    // The following methods: restartRootNode, addIdentifier2 and addWME are used in a new way to construct the jTree
    
    // First, the restartRootNode creates a completely new Tree, by adding a root node and the State node, which is the
    // root of the a new Tree
//    public IdeaTreeNode restartRootNode(List<WMNode> lwm) {
//        IdeaTreeNode root = addRootNode("Root");
//        repr = new CopyOnWriteArrayList<WMNode>();
//        for (Idea wm : lwm) {
//            IdeaTreeNode child = root.addIdentifier(wm);
//            ExpandStateLibrary.set(child,true);
//        }
//        Runtime.getRuntime().gc();
//        return root;
//    }
    
    public IdeaTreeNode restartRootNode(Idea node) {
        IdeaTreeNode root = addRootNode(node.getName());
        repr = new CopyOnWriteArrayList<Idea>();
        ExpandStateLibrary.set(root,true);
        for (Idea wm : node.getL()) {
            IdeaTreeNode child = root.addWMNode(wm);
            ExpandStateLibrary.set(child,true);
        }
        TreeElement oldrootte = (TreeElement)root.getUserObject();
        oldrootte.setElement(node);
        return root;
    }
    
//    public IdeaTreeNode addIdentifier2(Idea node) {
//        List<WMNodeToBeCreated> toBeCreated = new ArrayList<WMNodeToBeCreated>();
//        List<WMNodeToBeCreated> toBeFurtherProcessed = new ArrayList<WMNodeToBeCreated>();
//        IdeaTreeNode idNode = getIdNode(node);
//        toBeCreated.add(new WMNodeToBeCreated(idNode));
//        List<WMNodeToBeCreated> nextList;
//        do {
//            nextList = new ArrayList<WMNodeToBeCreated>();
//            for (WMNodeToBeCreated wme : toBeCreated) {
//                toBeFurtherProcessed = processStep(wme.parent,wme.newId,wme.attrib);
//                for (WMNodeToBeCreated e : toBeFurtherProcessed) {
//                    Identifier id_ = e.newId;
//                    String attr_ = e.attrib;
//                    IdeaTreeNode part = getIdNode(id_,attr_);
//                    wme.parent.add(part);
//                    nextList.add(new WMNodeToBeCreated(part,id_,attr_));
//                }
//            }
//            toBeCreated = nextList;
//        } while (nextList.size() > 0);    
//        repr = new CopyOnWriteArrayList<Identifier>();
//        return(idNode);    
//    }
    
//    public List<WMNodeToBeCreated> addWME(IdeaTreeNode idNode, Wme wme) {
//        List<WMNodeToBeCreated> toBeFurtherProcessed = new ArrayList<WMNodeToBeCreated>();
//        Identifier idd = wme.getIdentifier();
//        Symbol a = wme.getAttribute();
//        Symbol v = wme.getValue();
//        Identifier testv = v.asIdentifier();
//        if (testv != null) { // v is an identifier
//            // if the identifier is final I can safely introduce it in the tree
//            if (isIdentifierFinal(testv) || idd.toString().equalsIgnoreCase(testv.toString()) ) {
//                String preference = "";
//                if (wme.isAcceptable()) preference = " +";
//                IdeaTreeNode newidNode = genFinalIdNode(testv,a.toString()+preference);
//                idNode.add(newidNode);
//            }
//            else { // mark the identifier to be further processed
//               String preference = "";
//               if (wme.isAcceptable()) preference = " +";
//               if (idd.toString().equalsIgnoreCase(testv.toString()))
//                   System.out.println("WME auto-recursivo detectado: ("+idd.toString()+" "+a.toString()+" "+v.toString()+")");
//               toBeFurtherProcessed.add(new WMNodeToBeCreated(null,testv,a.toString()+preference));
//            }   
//        }
//        else { // v is a value 
//               IdeaTreeNode valueNode = genValNode(a,v);
//               idNode.add(valueNode);
//            }
//        return(toBeFurtherProcessed);    
//    }
    
    
//    void printOperator(Wme wme) {
//        String preference = "";
//        if (wme.isAcceptable()) preference = " +";
//        if (wme.getAttribute().toString().startsWith("operator")) System.out.println("("+wme.getIdentifier()+" "+wme.getAttribute()+" "+wme.getValue()+preference+")");
//    }
    
    // This method scans the identifier ido and finds the new children to be created, inserting nodes for values and final children
//    private List<WMNodeToBeCreated> processStep(IdeaTreeNode idNode, Identifier ido, String attr) {    
//        List<WMNodeToBeCreated> toBeFurtherProcessed = new ArrayList();
//        // Verify all WMEs using ido as an identifier
//        Iterator<Wme> It = ido.getWmes();
//        while (It.hasNext()) {
//            Wme wme = It.next();
//            // Insert nodes for values and final children ... returns the id nodes in the list for further processing
//            List<WMNodeToBeCreated> tbfp = addWME(idNode,wme);
//            toBeFurtherProcessed.addAll(tbfp);
//        }
//        return(toBeFurtherProcessed);
//    }
    
    public boolean equals(Idea ido, Idea ii) {
        String s1 = ido.getName()+ido.getValue().toString();
        String s2 = ii.getName()+ii.getValue().toString();
        return(s1.toString().equalsIgnoreCase(s2.toString()));
    }
    
//    private boolean isIdentifierFinal(Idea ido) {
//        for (Idea ii : repr) {
//            if (equals(ido,ii)) {
//                return true;
//            }
//        }
//        return false;
//    }
    
    public TreeElement getTreeElement() {
        return((TreeElement)this.getUserObject());
    }
    
    @Override
    public void add(MutableTreeNode newChild)
    {
        IdeaTreeNodeComparator comparator = new IdeaTreeNodeComparator();
        super.add(newChild);
        if (comparator != null)
        {
            Collections.sort(this.children,comparator);
        }
    }
    
}
