/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.cst.bindings.soar;

import br.unicamp.cst.util.RendererJTree;
import br.unicamp.cst.util.TreeElement;
import java.awt.Dimension;
import java.awt.Graphics;
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
 * @author rgudwin
 */
public class WorkingMemoryPanel extends javax.swing.JPanel {

    /**
     * Creates new form WMPanel
     */
    public WorkingMemoryPanel() {
        initComponents();
    }
    
    List<Identifier> wog;
    JSoarCodelet sc;

    /**
     * Creates new form WorkingMemoryPanel
     */
    public WorkingMemoryPanel(JSoarCodelet sco) {
        //initComponents();
        sc = sco;
        wog = sc.getJsoar().getStates();
        initComponents();
        TreeModel tm = createTreeModel(wog);
        jTree1 = new JTree(tm);
        collapseAllNodes(jTree1);
        jScrollPane1.setViewportView(jTree1);
        jTree1.setCellRenderer(new RendererJTree());
    }

        public void setWO(List<Identifier> newwog) {
        wog = newwog;
    }
        
    private DefaultMutableTreeNode addRootNode(String rootNodeName) {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(new TreeElement(rootNodeName, TreeElement.NODE_NORMAL, null, TreeElement.ICON_CONFIGURATION));
        return(root);
    }
    
    private DefaultMutableTreeNode addIdentifier(Identifier ido, String attr) {
        
        DefaultMutableTreeNode idNode = new DefaultMutableTreeNode(new TreeElement(attr+ " [" + ido.toString()+"]", TreeElement.NODE_NORMAL, ido.toString(), TreeElement.ICON_OBJECT));
        Iterator<Wme> It = ido.getWmes();
        while (It.hasNext()) {
            Wme wme = It.next();
            Identifier idd = wme.getIdentifier();
            Symbol a = wme.getAttribute();
            Symbol v = wme.getValue();
            Identifier testv = v.asIdentifier();
            if (testv != null) { // v is an identifier
               String preference = "";
//               Iterator<Preference> ip = wme.getPreferences();
//               while(ip.hasNext()) {
//                  Preference p = ip.next(); 
//                  //System.out.println("("+idd.toString()+" "+a.toString()+" "+v.toString()+") "+p.toString());
//                  System.out.println(p.toString());
//               }           
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
    
    public TreeModel createTreeModel(List<Identifier> wo) {
        DefaultMutableTreeNode root = addRootNode("Root");
        for (Identifier ii : wo) {
            DefaultMutableTreeNode o = addIdentifier(ii,"State");
            root.add(o);
        }
        TreeModel tm = new DefaultTreeModel(root);
        return(tm);
    }
    
    public void printStates() {
        List<Identifier> li = sc.getJsoar().getStates();
        System.out.println("********* Getting states ... ***********");
        for (Identifier ii : li)
            System.out.println(ii.toString());
        System.out.println("*****************************************");
    }
    
//    private JTree addNodeJTree(Wme wo) {
//        JTree tree;
//        TreeModel tm = createTreeModel(wo);
//        tree = new JTree(tm);
//        expandAllNodes(tree);
//        return tree;
//    }
    
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
       tree.expandRow(0);
       row = tree.getRowCount() - 1;
       while (row >= 0) {
          tree.expandRow(row);
          row--;
       }
    }
 
    public void updateTree(List<Identifier> wo) {
       TreeModel tm = createTreeModel(wo);
       jTree1.setModel(tm);
       collapseAllNodes(jTree1);
    }
    
    public void updateTree() {
       //printStates();
       SOARPlugin sp = sc.getJsoar();
       //wog = sc.getJsoar().getStates();
       wog = sp.getStates();
       //for (Identifier i : wog)
       //     System.out.println(i+" formed by NameLetter: "+i.getNameLetter()+" plus "+i.getNameNumber());
       updateTree(wog);
    }
    
    public void attachToJScrollPane(JScrollPane father) {
        javax.swing.GroupLayout epLayout = new javax.swing.GroupLayout(this);
        this.setLayout(epLayout);
        Dimension d = this.getSize();
        epLayout.setHorizontalGroup(
            epLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, d.width, Short.MAX_VALUE)
        );
        epLayout.setVerticalGroup(
            epLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, d.height, Short.MAX_VALUE)
        );
        father.setViewportView(this);
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
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        zoom_in = new javax.swing.JButton();
        zoom_out = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTree1 = new javax.swing.JTree();
        jLabel1 = new javax.swing.JLabel();

        jToolBar1.setBorder(null);
        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);
        jToolBar1.add(filler1);

        zoom_in.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/zoom-in-icon.png"))); // NOI18N
        zoom_in.setFocusable(false);
        zoom_in.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        zoom_in.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        zoom_in.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zoom_inActionPerformed(evt);
            }
        });
        jToolBar1.add(zoom_in);

        zoom_out.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/zoom-out-icon.png"))); // NOI18N
        zoom_out.setFocusable(false);
        zoom_out.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        zoom_out.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        zoom_out.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zoom_outActionPerformed(evt);
            }
        });
        jToolBar1.add(zoom_out);

        jScrollPane1.setViewportView(jTree1);

        jLabel1.setText("Working Memory: ");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 172, Short.MAX_VALUE)
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel1)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 350, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void zoom_inActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_zoom_inActionPerformed
        // TODO add your handling code here:
        expandAllNodes(jTree1);
    }//GEN-LAST:event_zoom_inActionPerformed

    private void zoom_outActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_zoom_outActionPerformed
        // TODO add your handling code here:
        collapseAllNodes(jTree1);
    }//GEN-LAST:event_zoom_outActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.Box.Filler filler1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JTree jTree1;
    private javax.swing.JButton zoom_in;
    private javax.swing.JButton zoom_out;
    // End of variables declaration//GEN-END:variables
}
