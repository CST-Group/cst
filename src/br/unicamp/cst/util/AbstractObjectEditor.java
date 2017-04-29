/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.cst.util;

import br.unicamp.cst.representation.owrl.AbstractObject;
import br.unicamp.cst.representation.owrl.Property;
import br.unicamp.cst.representation.owrl.QualityDimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 *
 * @author rgudwin
 */
public class AbstractObjectEditor extends javax.swing.JFrame {
    
    AbstractObject root;

    /**
     * Creates new form AbstractObjectEditor
     */
    public AbstractObjectEditor(AbstractObject rootAO) {
        root = rootAO;
        if (rootAO == null) root = new AbstractObject("AbstractObject");
        initComponents();
        updateTree(root);
        jsp.setViewportView(jtree);
        jtree.setCellRenderer(new RendererJTree());
        setTitle(root.getName());
        MouseListener ml;
        ml = new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                int selRow = jtree.getRowForLocation(e.getX(), e.getY());
                TreePath selPath = jtree.getPathForLocation(e.getX(), e.getY());
                if(selRow != -1) {
                    if(e.getClickCount() == 1 && e.getButton() == 3) {
                        //DefaultMutableTreeNode parent = (DefaultMutableTreeNode) selPath.getParentPath().getLastPathComponent();
                        
                        DefaultMutableTreeNode tn = (DefaultMutableTreeNode)selPath.getLastPathComponent();
                        TreeElement te = (TreeElement)tn.getUserObject();
                        DefaultMutableTreeNode parentnode = (DefaultMutableTreeNode)tn.getParent();
                        TreeElement parent=null;
                        Object element=null;
                        if (parentnode != null) {
                            parent = (TreeElement)(parentnode).getUserObject();
                            //System.out.println("Parent: "+parent.getName());
                            element = parent.getElement();
                        }
                        Object parentelement = element;
                        String classname = te.getElement().getClass().getCanonicalName();
                        if (classname.equals("br.unicamp.cst.representation.owrl.AbstractObject")) {
                            AbstractObject ao = (AbstractObject) te.getElement();
                            JPopupMenu popup = new JPopupMenu();
                            JMenuItem jm1 = new JMenuItem("Edit AbstractObject");
                            ActionListener al = new ActionListener() {
                                public void actionPerformed(ActionEvent e) {
                                    editAbstractObject(ao);
                                    updateTree(root);
                                }
                            };
                            jm1.addActionListener(al);
                            JMenuItem jm2 = new JMenuItem("Add new composite object");
                            ActionListener al2 = new ActionListener() {
                                public void actionPerformed(ActionEvent e) {
                                    createCompositeObject(ao);
                                    updateTree(root);
                                }
                            };
                            jm2.addActionListener(al2);
                            JMenuItem jm3 = new JMenuItem("Add new aggregate object");
                            ActionListener al3 = new ActionListener() {
                                public void actionPerformed(ActionEvent e) {
                                    createAggregateObject(ao);
                                    updateTree(root);
                                }
                            };
                            jm3.addActionListener(al3);
                            JMenuItem jm4 = new JMenuItem("Add new Property");
                            ActionListener al4 = new ActionListener() {
                                public void actionPerformed(ActionEvent e) {
                                    createProperty(ao);
                                    updateTree(root);
                                }
                            };
                            jm4.addActionListener(al4);
                            JMenuItem jm5 = new JMenuItem("Delete this AbstractObject");
                            ActionListener al5;
                            al5 = new ActionListener() {
                                public void actionPerformed(ActionEvent e) {
                                    deleteComponent(parentelement, ao);
                                    updateTree(root);
                                }
                            };
                            jm5.addActionListener(al5);
                            popup.add(jm1);
                            popup.add(jm2);
                            popup.add(jm3);
                            popup.add(jm4);
                            popup.add(jm5);
                            popup.show(jtree, e.getX(), e.getY());
                        }
                        else if (classname.equals("br.unicamp.cst.representation.owrl.Property")) {
                            Property p = (Property) te.getElement();
                            JPopupMenu popup = new JPopupMenu();
                            JMenuItem jm1 = new JMenuItem("Edit Property");
                            ActionListener al = new ActionListener() {
                                public void actionPerformed(ActionEvent e) {
                                    editProperty(p);
                                    updateTree(root);
                                }
                            };
                            jm1.addActionListener(al);
                            JMenuItem jm2 = new JMenuItem("Add new QualityDimension");
                            ActionListener al2 = new ActionListener() {
                                public void actionPerformed(ActionEvent e) {
                                    createQualityDimension(p);
                                    updateTree(root);
                                }
                            };
                            jm2.addActionListener(al2);
                            JMenuItem jm3 = new JMenuItem("Delete this Property");
                            ActionListener al3;
                            al3 = new ActionListener() {
                                public void actionPerformed(ActionEvent e) {
                                    deleteComponent(parentelement, p);
                                    updateTree(root);
                                }
                            };
                            jm3.addActionListener(al3);
                            popup.add(jm1);
                            popup.add(jm2);
                            popup.add(jm3);
                            popup.show(jtree, e.getX(), e.getY());
                        }
                        else if (classname.equals("br.unicamp.cst.representation.owrl.QualityDimension")) {
                            QualityDimension qd = (QualityDimension) te.getElement();
                            JPopupMenu popup = new JPopupMenu();
                            JMenuItem jm1 = new JMenuItem("Edit QualityDimension");
                            ActionListener al = new ActionListener() {
                                public void actionPerformed(ActionEvent e) {
                                    editQualityDimension(qd);
                                    updateTree(root);
                                }
                            };
                            jm1.addActionListener(al);
                            JMenuItem jm2 = new JMenuItem("Delete this QualityDimension");
                            ActionListener al2;
                            al2 = new ActionListener() {
                                public void actionPerformed(ActionEvent e) {
                                    deleteComponent(parentelement, qd);
                                    updateTree(root);
                                }
                            };
                            jm2.addActionListener(al2);
                            popup.add(jm1);
                            popup.add(jm2);
                            popup.show(jtree, e.getX(), e.getY());
                        }
                    }
//               else if(e.getClickCount() == 2) {
//                    System.out.println(selRow + " "+ selPath);
//               }
                }
            }};
        jtree.addMouseListener(ml);
    }
    
    private void createCompositeObject(AbstractObject a) {
        AbstractObject newao = DialogFactory.getAbstractObject();
        a.addCompositePart(newao);
        
    }
    
    private void createAggregateObject(AbstractObject a) {
        AbstractObject newao = DialogFactory.getAbstractObject();
        a.addAggregatePart(newao);        
    }
    
    private void createProperty(AbstractObject a) {
        Property newp = DialogFactory.getProperty();
        a.addProperty(newp);
    }
    
    private void createQualityDimension(Property p) {
        QualityDimension newqd = DialogFactory.getQualityDimension();
        p.addQualityDimension(newqd);
    }
    
    private void editAbstractObject(AbstractObject ao) {
        DialogFactory.editAbstractObject(ao);
    }
    
    private void editProperty(Property p) {
        DialogFactory.editProperty(p);
    }
    
    private void editQualityDimension(QualityDimension qd) {
        DialogFactory.editQualityDimension(qd);
    }
    
    private void deleteComponent(Object parent, Object child) {
        if (parent == null) return;
        String parentclass = parent.getClass().getCanonicalName();
        //System.out.println("Parent class: "+parentclass);
        //String childclass = child.getClass().getCanonicalName();
        if (parentclass.equals("br.unicamp.cst.representation.owrl.AbstractObject")) {
            ((AbstractObject)parent).deleteChild(child);
        }
        if (parentclass.equals("br.unicamp.cst.representation.owrl.Property")) {
            ((Property)parent).deleteChild(child);
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

        jToolBar1 = new javax.swing.JToolBar();
        zoom_in = new javax.swing.JButton();
        zoom_out = new javax.swing.JButton();
        search = new javax.swing.JButton();
        jsp = new javax.swing.JScrollPane();
        jtree = new javax.swing.JTree();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jToolBar1.setRollover(true);

        zoom_in.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/unicamp/cst/images/zoom-in-icon.png"))); // NOI18N
        zoom_in.setFocusable(false);
        zoom_in.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        zoom_in.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        zoom_in.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zoom_inActionPerformed(evt);
            }
        });
        jToolBar1.add(zoom_in);

        zoom_out.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/unicamp/cst/images/zoom-out-icon.png"))); // NOI18N
        zoom_out.setFocusable(false);
        zoom_out.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        zoom_out.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        zoom_out.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zoom_outActionPerformed(evt);
            }
        });
        jToolBar1.add(zoom_out);

        search.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/unicamp/cst/images/binoculars.png"))); // NOI18N
        search.setFocusable(false);
        search.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        search.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        search.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchActionPerformed(evt);
            }
        });
        jToolBar1.add(search);

        jsp.setViewportView(jtree);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
            .addComponent(jsp)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jsp, javax.swing.GroupLayout.DEFAULT_SIZE, 238, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void zoom_inActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_zoom_inActionPerformed
        expandAllNodes(jtree);
    }//GEN-LAST:event_zoom_inActionPerformed

    private void zoom_outActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_zoom_outActionPerformed
        collapseAllNodes(jtree);
    }//GEN-LAST:event_zoom_outActionPerformed

    private void searchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchActionPerformed
        // TODO add your handling code here:

        //É so um teste!
        TreeElement.reset();
        DefaultMutableTreeNode ao = addObject(root, true);
        TreeModel ttm = new DefaultTreeModel(ao);
        jtree.setModel(ttm);
        expandAllNodes(jtree);

        String nameNode = null;
        nameNode = JOptionPane.showInputDialog("Node Name:");
        DefaultMutableTreeNode o = find(((DefaultMutableTreeNode) jtree.getModel().getRoot()), nameNode);

        //É so um teste!
        TreeModel tm = new DefaultTreeModel(o);
        jtree.setModel(tm);
        expandAllNodes(jtree);


    }//GEN-LAST:event_searchActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        AbstractObject robot = new AbstractObject("Robot");
        // System.out.println("XXXXX"+robot.hashCode());
        AbstractObject sensor = new AbstractObject("Sensor");
        // System.out.println("XXXXX"+sensor.hashCode());
        Property position = new Property("Position");
        // System.out.println("XXXXX"+position.hashCode());
        position.addQualityDimension(new QualityDimension("x",0.5));
        position.addQualityDimension(new QualityDimension("y",0.6));
        sensor.addProperty(position);
        robot.addCompositePart(sensor);
        AbstractObject actuator = new AbstractObject("Actuator");
        actuator.addProperty(new Property("velocity",new QualityDimension("intensity",-0.12)));
        robot.addCompositePart(actuator);
        robot.addAggregatePart(actuator);
        robot.addProperty(new Property("Model",new QualityDimension("Serial#","1234XDr56")));   
        AbstractObjectEditor ov = new AbstractObjectEditor(robot);
        System.out.println("XXXXX"+robot.hashCode());
        //AbstractObjectEditor ov = new AbstractObjectEditor(null);
        ov.setVisible(true);
    }
    
    private DefaultMutableTreeNode addRootNode(AbstractObject wo) {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(new TreeElement(wo.getName() + " [" + wo.getID()+"]", TreeElement.NODE_NORMAL, wo, TreeElement.ICON_CONFIGURATION));
        return(root);
    }
    
    private DefaultMutableTreeNode addObject(AbstractObject wo, boolean composite) {
        DefaultMutableTreeNode objectNode;
        //String name = wo.getName() + " &lt;" + wo.getID()+">";
        String name = wo.getName();
        if (composite) objectNode = new DefaultMutableTreeNode(new TreeElement(name, TreeElement.NODE_NORMAL, wo, TreeElement.ICON_COMPOSITE));
        else objectNode = new DefaultMutableTreeNode(new TreeElement(name, TreeElement.NODE_NORMAL, wo, TreeElement.ICON_AGGREGATE));
        List<AbstractObject> parts = wo.getCompositeParts();
        for (AbstractObject oo : parts) {
            DefaultMutableTreeNode part = addObject(oo,true);
            objectNode.add(part);
        }
        List<AbstractObject> aggregates = wo.getAggregatePart();
        for (AbstractObject oo : aggregates) {
            DefaultMutableTreeNode part = addObject(oo,false);
            objectNode.add(part);
        }
        List<Property> props = wo.getProperties();
        for (Property p : props) {
                DefaultMutableTreeNode propertyNode = addProperty(p);
                objectNode.add(propertyNode);
            }
        
        return(objectNode);    
    }
    
    private DefaultMutableTreeNode addProperty(Property p) {
        DefaultMutableTreeNode propertyNode = new DefaultMutableTreeNode(new TreeElement(p.getName(), TreeElement.NODE_NORMAL, p, TreeElement.ICON_PROPERTY));
        int size = ((Property) p).getQualityDimensions().size();
        for (int s = 0; s < size; s++) {
            QualityDimension qd = ((Property) p).getQualityDimensions().get(s);
            String chave = qd.getName();
            String value = qd.getValue().toString();
            //DefaultMutableTreeNode qualityDimensionNode = new DefaultMutableTreeNode(new TreeElement(chave+" : "+value, TreeElement.NODE_NORMAL, qd, TreeElement.ICON_QUALITYDIM));
            DefaultMutableTreeNode qualityDimensionNode = new DefaultMutableTreeNode(new TreeElement(chave,": "+value, TreeElement.NODE_NORMAL, qd, TreeElement.ICON_QUALITYDIM));
            propertyNode.add(qualityDimensionNode);
            

        }
        return(propertyNode);
    }
    

    
    public void updateTree(AbstractObject wo) {
       TreeElement.reset();
       root = wo;
       DefaultMutableTreeNode o = addObject(wo,true);
        //DefaultMutableTreeNode o = addRootNode(wo);
        TreeModel tm = new DefaultTreeModel(o);
       //TreeModel tm = createTreeModel(wo);
       jtree.setModel(tm);
       expandAllNodes(jtree);
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
    
    private void collapseAllNodes(JTree tree) {
       int row = tree.getRowCount() - 1;
       while (row >= 0) {
          tree.collapseRow(row);
          row--;
       }
       //tree.expandRow(0);
       row = tree.getRowCount() - 1;
       while (row >= 0) {
          tree.expandRow(row);
          row--;
       }
    }
    
    //private TreePath find(DefaultMutableTreeNode root, String s) {
    private DefaultMutableTreeNode find(DefaultMutableTreeNode root, String s) {    
        
         DefaultMutableTreeNode root2 = root;
        
        Enumeration<DefaultMutableTreeNode> e = root2.depthFirstEnumeration();
        List<TreePath> listPath = new ArrayList<>();
        TreePath raffledPath = null;
        int cont = 0;
        
        while (e.hasMoreElements()) {
            DefaultMutableTreeNode node = e.nextElement();
             TreeElement te = (TreeElement)node.getUserObject();
                        
            if (te.getName().toString().equalsIgnoreCase(s)) {
                cont++;
                listPath.add(new TreePath(node.getPath()));
                
                //return new TreePath(node.getPath());
            }
        }
        
        if( listPath.size()> 0){
            Random r = new Random();
            raffledPath = listPath.get(r.nextInt(listPath.size()));

            DefaultMutableTreeNode tn = (DefaultMutableTreeNode) raffledPath.getLastPathComponent();
            TreeElement te = (TreeElement) tn.getUserObject();
            te.setColor(TreeElement.NODE_CHANGE);
            
            
            
        
        }
       
        
        System.out.println("Achei: "+cont);
       // return raffledPath;
       return root2; 
        
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JScrollPane jsp;
    private javax.swing.JTree jtree;
    private javax.swing.JButton search;
    private javax.swing.JButton zoom_in;
    private javax.swing.JButton zoom_out;
    // End of variables declaration//GEN-END:variables
}
