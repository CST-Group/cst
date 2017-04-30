/*******************************************************************************
 * Copyright (c) 2012  DCA-FEEC-UNICAMP
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 * 
 * Contributors to this module:
 *     S. M. de Paula and R. R. Gudwin 
 ******************************************************************************/

package br.unicamp.cst.util;

import br.unicamp.cst.representation.owrl.AbstractObject;
import br.unicamp.cst.representation.owrl.Property;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.ImageIcon;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

/**
 *
 * @author suelenmapa
 */
public class TreeViewer extends javax.swing.JFrame {

    private JTree configurationTreeInitial;
    private JTree configurationTreeFinal;
    private DefaultMutableTreeNode parentNodeInitial;
    private DefaultMutableTreeNode parentNodeFinal;

    private Map<String, AbstractObject> listConfs;

    public TreeViewer(Map<String, AbstractObject> lc) {

        initComponents();
        this.listConfs = lc;
        this.fillJComboBox();

        setTitle("CONFIGURATION VIEWER");
        setVisible(true);

    }

    private void fillJComboBox() {

        jComboBoxInitial.removeAllItems();
        jComboBoxInitial.addItem(" ");

        for (String key : listConfs.keySet()) {

            jComboBoxInitial.addItem(key);
        }
    }

    private boolean compareNodes(DefaultMutableTreeNode node_1, DefaultMutableTreeNode node_2) {
        boolean returnValue = false;
        //System.out.println("Comparando " + ((TreeElement) node_1.getUserObject()).getName() + " e " + ((TreeElement) node_2.getUserObject()).getName());
        if (node_1.isLeaf() && node_2.isLeaf()) {
            TreeElement v_node_1 = (TreeElement) node_1.getUserObject();
            TreeElement v_node_2 = (TreeElement) node_2.getUserObject();
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
            while (c_node_1.hasMoreElements()) {
                DefaultMutableTreeNode n_node_1 = c_node_1.nextElement();
                DefaultMutableTreeNode n_node_2 = c_node_2.nextElement();
                //returnValue = returnValue || compareNodes(n_node_1, n_node_2);
                returnValue = compareNodes(n_node_1, n_node_2);

                if (returnValue) {
                    ((TreeElement) node_1.getUserObject()).setColor(TreeElement.NODE_CHANGE);
                    ((TreeElement) node_2.getUserObject()).setColor(TreeElement.NODE_CHANGE);
                }
            }

        }
        return returnValue;
    }

    private void propagateNodeType(TreeNode tn, int nodeType) {
        TreeElement te = (TreeElement) ((DefaultMutableTreeNode) tn).getUserObject();
        te.setColor(nodeType);
        for (int i = 0; i < tn.getChildCount(); ++i) {
            propagateNodeType(tn.getChildAt(i), nodeType);
        }
    }

    private void compareTrees() {

        if (jComboBoxInitial.getSelectedItem() == null || jComboBoxFinal.getSelectedItem() == null || jComboBoxInitial.getSelectedItem().equals(" ") || jComboBoxFinal.getSelectedItem().equals(" ")) {
            return;
        }

        DefaultMutableTreeNode rootFinal = (DefaultMutableTreeNode) configurationTreeFinal.getModel().getRoot();
        DefaultMutableTreeNode rootInitial = (DefaultMutableTreeNode) configurationTreeInitial.getModel().getRoot();

        for (int i = 0; i < rootFinal.getChildCount(); i++) {

            TreeElement cn_final = (TreeElement) ((DefaultMutableTreeNode) rootFinal.getChildAt(i)).getUserObject();

            boolean found = false;

            for (int c = 0; c < rootInitial.getChildCount(); c++) {

                TreeElement cn_initial = (TreeElement) ((DefaultMutableTreeNode) rootInitial.getChildAt(c)).getUserObject();

                if (cn_final.getName().compareTo(cn_initial.getName()) == 0) {
                    compareNodes((DefaultMutableTreeNode) rootFinal.getChildAt(i), (DefaultMutableTreeNode) rootInitial.getChildAt(c));
                    found = true;
                    break;
                }
            }

            if (!found) {
                propagateNodeType(rootFinal.getChildAt(i), TreeElement.NODE_CREATION);
            }
        }

        for (int c = 0; c < rootInitial.getChildCount(); c++) {

            TreeElement cn_initial = (TreeElement) ((DefaultMutableTreeNode) rootInitial.getChildAt(c)).getUserObject();
            AbstractObject wo_initial = (AbstractObject) cn_initial.getElement();

            boolean found = false;
            for (int i = 0; i < rootFinal.getChildCount(); i++) {
                TreeElement cn_final = (TreeElement) ((DefaultMutableTreeNode) rootFinal.getChildAt(i)).getUserObject();
                AbstractObject wo_final = (AbstractObject) cn_final.getElement();

                if (wo_initial == wo_final) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                propagateNodeType(rootInitial.getChildAt(c), TreeElement.NODE_EXCLUSION);
            }
        }

        jspFinal.setViewportView(configurationTreeFinal);
        repaint();

    }

    private JTree addNodeJTree(AbstractObject listObjects, JScrollPane where, DefaultMutableTreeNode node, JTree tree) {

        List<AbstractObject> listWO = listObjects.getAggregateParts(); 

        for (AbstractObject wo : listWO) {

            DefaultMutableTreeNode objectNode = addObject(wo,true);
            node.add(objectNode);

            tree = new JTree(node);

            where.setViewportView(tree);
            repaint();

        }

        return tree;

    }

    private DefaultMutableTreeNode addObject(AbstractObject wo, boolean composite) {
        DefaultMutableTreeNode objectNode;
        if (composite)
          objectNode  = new DefaultMutableTreeNode(new TreeElement(wo.getName(), TreeElement.NODE_NORMAL, wo, TreeElement.ICON_COMPOSITE));
        else 
          objectNode  = new DefaultMutableTreeNode(new TreeElement(wo.getName(), TreeElement.NODE_NORMAL, wo, TreeElement.ICON_AGGREGATE));  
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
    
     private DefaultMutableTreeNode addProperty(Property p) {
        DefaultMutableTreeNode propertyNode = new DefaultMutableTreeNode(new TreeElement(p.getName(), TreeElement.NODE_NORMAL, p, TreeElement.ICON_PROPERTY));
        int size = ((Property) p).getQualityDimensions().size();
        for (int s = 0; s < size; s++) {
            String chave = ((Property) p).getQualityDimensions().get(s).getName();
            String value = (((Property) p).getQualityDimensions().get(s).getValue()).toString();
            DefaultMutableTreeNode qualityDimensionNode = new DefaultMutableTreeNode(new TreeElement(chave+" : "+value, TreeElement.NODE_NORMAL, chave+" : "+value, TreeElement.ICON_QUALITYDIM));
            propertyNode.add(qualityDimensionNode);
            //DefaultMutableTreeNode valueQualityDimensionNode = new DefaultMutableTreeNode(new TreeElement(value, TreeElement.NODE_NORMAL, value, TreeElement.ICON_VALUE));
            //qualityDimensionNode.add(valueQualityDimensionNode);

        }
        return(propertyNode);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jspInitial = new javax.swing.JScrollPane();
        jspFinal = new javax.swing.JScrollPane();
        jLabel3 = new javax.swing.JLabel();
        jComboBoxInitial = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        jComboBoxFinal = new javax.swing.JComboBox<>();
        jButton1 = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("CONFIGURATION VIEWER");

        jLabel3.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel3.setText("Initial Configuration:");

        jComboBoxInitial.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jComboBoxInitial.setMaximumRowCount(10);
        jComboBoxInitial.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxInitialActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel4.setText("Final Configuration:");

        jComboBoxFinal.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jComboBoxFinal.setMaximumRowCount(10);
        jComboBoxFinal.setEnabled(false);
        jComboBoxFinal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxFinalActionPerformed(evt);
            }
        });

        jButton1.setText(">>");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jMenu1.setText("File");
        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(3, 3, 3)
                        .addComponent(jComboBoxInitial, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jspInitial, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 306, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(4, 4, 4)
                        .addComponent(jComboBoxFinal, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jspFinal))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jComboBoxInitial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(jComboBoxFinal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jspInitial, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton1)
                        .addGap(0, 439, Short.MAX_VALUE))
                    .addComponent(jspFinal))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    private void jComboBoxInitialActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxInitialActionPerformed

        if (jComboBoxInitial.getSelectedItem().equals(" ")) {

            jspInitial.setViewportView(null);
        }

        Set<String> nameConfs = listConfs.keySet();
        boolean passou = false;

        for (String key : nameConfs) {

            if (jComboBoxInitial.getSelectedItem().equals(key)) {
                passou = true;
                parentNodeInitial = new DefaultMutableTreeNode(new TreeElement("CONFIGURATION", TreeElement.NODE_NORMAL, null, TreeElement.ICON_CONFIGURATION));
                configurationTreeInitial = new JTree(parentNodeInitial);
                jspInitial.setViewportView(configurationTreeInitial);
                repaint();

                configurationTreeInitial = this.addNodeJTree(listConfs.get(key), jspInitial, parentNodeInitial, configurationTreeInitial);
                configurationTreeInitial.setCellRenderer(new RendererJTree());

                jComboBoxFinal.removeAllItems();
                jComboBoxFinal.setEnabled(true);
                jComboBoxFinal.addItem(" ");
                continue;
            }

            if (passou) {
                jComboBoxFinal.addItem(key);
            }
        }

        compareTrees();

    }//GEN-LAST:event_jComboBoxInitialActionPerformed

    private void jComboBoxFinalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxFinalActionPerformed

        Set<String> nameConfs = listConfs.keySet();

        Object actual = jComboBoxFinal.getSelectedItem();

        if (actual == null) {
            return;
        }

        if (actual.equals(" ")) {

            jspFinal.setViewportView(null);
        }

        for (String chave : nameConfs) {

            if (actual.equals(chave)) {

                parentNodeFinal = new DefaultMutableTreeNode(new TreeElement("CONFIGURATION", TreeElement.NODE_NORMAL, null, TreeElement.ICON_CONFIGURATION));
                configurationTreeFinal = new JTree(parentNodeFinal);
                jspFinal.setViewportView(configurationTreeFinal);
                repaint();

                configurationTreeFinal = this.addNodeJTree(listConfs.get(chave), jspFinal, parentNodeFinal, configurationTreeFinal);
                configurationTreeFinal.setCellRenderer(new RendererJTree());
            }
        }

        compareTrees();

    }//GEN-LAST:event_jComboBoxFinalActionPerformed


    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

        if (jComboBoxInitial.getItemCount() > jComboBoxInitial.getSelectedIndex() + 1) {
            jComboBoxInitial.setSelectedIndex(jComboBoxInitial.getSelectedIndex() + 1);
        }
        if (jComboBoxFinal.getItemCount() > 1) {
            jComboBoxFinal.setSelectedIndex(1);
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    public static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = TreeViewer.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JComboBox<String> jComboBoxFinal;
    private javax.swing.JComboBox<String> jComboBoxInitial;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JScrollPane jspFinal;
    private javax.swing.JScrollPane jspInitial;
    // End of variables declaration//GEN-END:variables

}
