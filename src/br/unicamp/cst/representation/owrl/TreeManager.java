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

package br.unicamp.cst.representation.owrl;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

/**
 * @author Suelen Mapa
 */
public class TreeManager {

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

    public static JTree addNodeJTree(Configuration conf, JScrollPane where, DefaultMutableTreeNode node, JTree tree) {

        List<WorldObject> listWO = conf.getObjects();

        for (WorldObject wo : listWO) {

            DefaultMutableTreeNode objectNode = addObject(wo);
            node.add(objectNode);

            tree = new JTree(node);

            where.setViewportView(tree);
        }

        return tree;

    }

    private static DefaultMutableTreeNode addObject(WorldObject wo) {
        DefaultMutableTreeNode objectNode = new DefaultMutableTreeNode(new TreeElement(wo.getName() + " [" + wo.getID() + "]", TreeElement.NODE_NORMAL, wo, TreeElement.ICON_OBJECT));
        List<WorldObject> parts = wo.getParts();
        for (WorldObject oo : parts) {
            DefaultMutableTreeNode part = addObject(oo);
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
            //DefaultMutableTreeNode valueQualityDimensionNode = new DefaultMutableTreeNode(new TreeElement(value, TreeElement.NODE_NORMAL, value, TreeElement.ICON_VALUE));
            //qualityDimensionNode.add(valueQualityDimensionNode);
        }
        return (propertyNode);
    }
}
