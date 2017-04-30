/** *****************************************************************************
 * Copyright (c) 2012  DCA-FEEC-UNICAMP
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * Contributors to this module:
 *     S. M. de Paula and R. R. Gudwin
 ***************************************************************************** */

package br.unicamp.cst.util;

import br.unicamp.cst.representation.owrl.AbstractObject;
import br.unicamp.cst.representation.owrl.Property;
import br.unicamp.cst.representation.owrl.QualityDimension;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

/**
 * @author Suelen Mapa
 */
public class TreeManager {
    
     static int treeOganization = 0;

    public static void compareTrees(JTree configurationTreeA, JTree configurationTreeB, JScrollPane jSPConfigB) {

        DefaultMutableTreeNode rootA = (DefaultMutableTreeNode) configurationTreeA.getModel().getRoot();
        DefaultMutableTreeNode rootB = (DefaultMutableTreeNode) configurationTreeB.getModel().getRoot();

        compareNodes(rootA, rootB);
        ((TreeElement) rootA.getUserObject()).setColor(TreeElement.NODE_NORMAL);
        ((TreeElement) rootB.getUserObject()).setColor(TreeElement.NODE_NORMAL);

        jSPConfigB.setViewportView(configurationTreeB);
    }

    
    private static boolean compareNodes(DefaultMutableTreeNode node_1, DefaultMutableTreeNode node_2) {
        boolean returnValue = false;
        //System.out.println("Comparando " + ((TreeElement) node_1.getUserObject()).getName() + " e " + ((TreeElement) node_2.getUserObject()).getName());

        TreeElement v_node_1 = (TreeElement) node_1.getUserObject();
        TreeElement v_node_2 = (TreeElement) node_2.getUserObject();
        if (node_1.isLeaf() && node_2.isLeaf()) {
            if (v_node_1.getName().compareTo(v_node_2.getName()) != 0) {
                v_node_1.setColor(TreeElement.NODE_CHANGE);
                v_node_2.setColor(TreeElement.NODE_CHANGE);
                return true;
            }
        } else if (node_1.isLeaf() || node_2.isLeaf()) {
            System.out.println("Error: Unaligned nodes.");
        } else {
            Enumeration<DefaultMutableTreeNode> c_node_1 = node_1.children();
            Enumeration<DefaultMutableTreeNode> c_node_2 = node_2.children();
            List<TreeElement> o_node_1 = new ArrayList<>();
            List<TreeElement> o_node_2 = new ArrayList<>();
            List<DefaultMutableTreeNode> d_node_1 = new ArrayList<>();
            List<DefaultMutableTreeNode> d_node_2 = new ArrayList<>();
            while (c_node_1.hasMoreElements()) {
                DefaultMutableTreeNode dnode = c_node_1.nextElement();
                d_node_1.add(dnode);
                o_node_1.add((TreeElement) dnode.getUserObject());
            }
            while (c_node_2.hasMoreElements()) {
                DefaultMutableTreeNode dnode = c_node_2.nextElement();
                d_node_2.add(dnode);
                o_node_2.add((TreeElement) dnode.getUserObject());
            }
            boolean found;
            for (int pos_2 = 0; pos_2 < d_node_2.size(); ++pos_2) {
                if (!d_node_2.get(pos_2).isLeaf()) {
                    found = false;
                    for (int pos_1 = 0; pos_1 < d_node_1.size(); ++pos_1) {
                        if (o_node_1.get(pos_1).getName().compareTo(o_node_2.get(pos_2).getName()) == 0) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        o_node_2.get(pos_2).setColor(TreeElement.NODE_CREATION);
                        propagateNodeType(d_node_2.get(pos_2), TreeElement.NODE_CREATION);
                        returnValue = true;
                    }
                }
            }
            for (int pos_1 = 0; pos_1 < d_node_1.size(); ++pos_1) {
                if (!d_node_1.get(pos_1).isLeaf()) {
                    found = false;
                    for (int pos_2 = 0; pos_2 < d_node_2.size(); ++pos_2) {
                        if (o_node_1.get(pos_1).getName().compareTo(o_node_2.get(pos_2).getName()) == 0) {
                            returnValue = returnValue | compareNodes(d_node_1.get(pos_1), d_node_2.get(pos_2));
                            if (returnValue) {
                                v_node_1.setColor(TreeElement.NODE_CHANGE);
                                v_node_2.setColor(TreeElement.NODE_CHANGE);
                            }
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        if (!d_node_1.get(pos_1).isLeaf()) {
                            o_node_1.get(pos_1).setColor(TreeElement.NODE_EXCLUSION);
                            propagateNodeType(d_node_1.get(pos_1), TreeElement.NODE_EXCLUSION);
                            returnValue = true;
                        }
                    }
                } else {
                    returnValue = returnValue | compareNodes(d_node_1.get(pos_1), d_node_2.get(pos_1));
                    if (returnValue) {
                        v_node_1.setColor(TreeElement.NODE_CHANGE);
                        v_node_2.setColor(TreeElement.NODE_CHANGE);
                    }
                    found = true;
                }
            }
        }
        return returnValue;
    }

    
    private static void propagateNodeType(TreeNode tn, int nodeType) {
        TreeElement te = (TreeElement) ((DefaultMutableTreeNode) tn).getUserObject();
        te.setColor(nodeType);
        for (int i = 0; i < tn.getChildCount(); ++i) {
            propagateNodeType(tn.getChildAt(i), nodeType);
        }
    }

    
    public static JTree addNodeJTree(AbstractObject listObjects, JScrollPane where, DefaultMutableTreeNode node, JTree tree) {

        List<AbstractObject> listWO = listObjects.getAggregateParts();

        for (AbstractObject wo : listWO) {

            DefaultMutableTreeNode objectNode = addObject(wo,true);
            node.add(objectNode);

            tree = new JTree(node);

            where.setViewportView(tree);
        }

        return tree;

    }
    
       
    public static JTree newAddNodeJTree(Object newNode, JScrollPane where, JTree tree, int idNode) {

        DefaultMutableTreeNode parentNode = new DefaultMutableTreeNode();
        

        if (treeOganization == 0 && (newNode instanceof AbstractObject)) {
            
            AbstractObject element=(AbstractObject)newNode;
            treeOganization++;
            parentNode = new DefaultMutableTreeNode(new TreeElement(element.getName(), TreeElement.NODE_NORMAL, null, TreeElement.ICON_COMPOSITE, treeOganization));

            tree = new JTree(parentNode);

        } else {
           
            TreeModel model = tree.getModel();
            
            if (model != null) {
                
                if (newNode instanceof AbstractObject) {

                    AbstractObject element = (AbstractObject) newNode;
                    DefaultMutableTreeNode node = InstantiateJustObject(element);
                    DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) model.getRoot();
                    DefaultTreeModel modelTree = new DefaultTreeModel(walk(rootNode, idNode, node));
                    tree.setModel(modelTree);

                } else {
                    if (newNode instanceof Property) {

                        Property element = (Property) newNode;
                        DefaultMutableTreeNode node = InstantiateJustProperty(element);
                        DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) model.getRoot();
                        DefaultTreeModel modelTree = new DefaultTreeModel(walk(rootNode, idNode, node));
                        tree.setModel(modelTree);

                    } else {

                        QualityDimension element = (QualityDimension) newNode;
                        DefaultMutableTreeNode node = InstantiateJustQualityDimension(element);
                        DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) model.getRoot();
                        DefaultTreeModel modelTree = new DefaultTreeModel(walk(rootNode, idNode, node));
                        tree.setModel(modelTree);
                    }

                }
                
                
                
                
               

               

            } else {
                System.out.println("Tree is empty.");
            }

           

        }
        expandAllNodes(tree);
        where.setViewportView(tree);
        return tree;
    }
    
    //only one object
     private static DefaultMutableTreeNode InstantiateJustObject(AbstractObject wo) {
         
         treeOganization++;
         DefaultMutableTreeNode objectNode = new DefaultMutableTreeNode(new TreeElement(wo.getName(), TreeElement.NODE_NORMAL, wo, TreeElement.ICON_COMPOSITE, treeOganization));
     
          return (objectNode);
     }
         
    //only one property
    private static DefaultMutableTreeNode InstantiateJustProperty(Property prop) {

        treeOganization++;
        DefaultMutableTreeNode propertyNode = new DefaultMutableTreeNode(new TreeElement(prop.getName(), TreeElement.NODE_NORMAL, prop, TreeElement.ICON_PROPERTY, treeOganization));

        return (propertyNode);
    }
       
    //only one property
    private static DefaultMutableTreeNode InstantiateJustQualityDimension(QualityDimension qualitydim) {

            treeOganization++;
            String chave = qualitydim.getName();
            String value = qualitydim.getValue().toString();
            DefaultMutableTreeNode qualityDimensionNode = new DefaultMutableTreeNode(new TreeElement(chave + " : " + value, TreeElement.NODE_NORMAL, qualitydim, TreeElement.ICON_QUALITYDIM, treeOganization));

        return (qualityDimensionNode);
    }
    
    //Add a complete object: object, property and qualityDimensions
    private static DefaultMutableTreeNode addObject(AbstractObject wo, boolean composite) {
        DefaultMutableTreeNode objectNode;
        if (composite) objectNode = new DefaultMutableTreeNode(new TreeElement(wo.getName(), TreeElement.NODE_NORMAL, wo, TreeElement.ICON_COMPOSITE));
        else objectNode = new DefaultMutableTreeNode(new TreeElement(wo.getName(), TreeElement.NODE_NORMAL, wo, TreeElement.ICON_AGGREGATE));
        List<AbstractObject> parts = wo.getCompositeParts();
        for (AbstractObject oo : parts) {
            DefaultMutableTreeNode part = addObject(oo,true);
            objectNode.add(part);
        }
        
        List<AbstractObject> aggregates = wo.getAggregateParts();
        for (AbstractObject oo : aggregates) {
            DefaultMutableTreeNode part = addObject(oo,false);
            objectNode.add(part);
        }

        List<Property> props = wo.getProperties();
        for (Property p : props) {
            DefaultMutableTreeNode propertyNode = addProperty(p);
            objectNode.add(propertyNode);
        }

        return (objectNode);
    }
    

    private static DefaultMutableTreeNode addProperty(Property p) {
        DefaultMutableTreeNode propertyNode = new DefaultMutableTreeNode(new TreeElement(p.getName(), TreeElement.NODE_NORMAL, p, TreeElement.ICON_PROPERTY));
        int size = ((Property) p).getQualityDimensions().size();
        for (int s = 0; s < size; s++) {
            String chave = ((Property) p).getQualityDimensions().get(s).getName();
            String value = (((Property) p).getQualityDimensions().get(s).getValue()).toString();
            DefaultMutableTreeNode qualityDimensionNode = new DefaultMutableTreeNode(new TreeElement(chave + " : " + value, TreeElement.NODE_NORMAL, chave + " : " + value, TreeElement.ICON_QUALITYDIM));
            propertyNode.add(qualityDimensionNode);
        }
        return (propertyNode);
    }
    
    
     public static DefaultMutableTreeNode walk(DefaultMutableTreeNode root, int idNode, DefaultMutableTreeNode newNode) {      
       
        DefaultMutableTreeNode root2 = root;
        TreeElement treeElementNode = (TreeElement) root.getUserObject();

        if (treeElementNode.getId_node() == idNode) {
                             
            root2.add(newNode);
             
            return root2;

        }else{
            
           Enumeration<DefaultMutableTreeNode> c_node = root.children();
                      
            while (c_node.hasMoreElements()) {
                DefaultMutableTreeNode dnode = c_node.nextElement();

                walk(dnode, idNode, newNode);

            }
        }
        
        return root2;
    }
     
    public static void expandAllNodes(JTree tree) {
        expandAllNodes(tree, 0, tree.getRowCount());
    }

    public static  void expandAllNodes(JTree tree, int startingIndex, int rowCount) {
        for (int i = startingIndex; i < rowCount; ++i) {
            tree.expandRow(i);
        }
        if (tree.getRowCount() != rowCount) {
            expandAllNodes(tree, rowCount, tree.getRowCount());
        }
    }

    
    
    
    
}
        
    
    
    


