/**********************************************************************************************
 * Copyright (c) 2012  DCA-FEEC-UNICAMP
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 * <p>
 * Contributors:
 * K. Raizer, A. L. O. Paraense, E. M. Froes, R. R. Gudwin - initial API and implementation
 ***********************************************************************************************/
package br.unicamp.cst.util.viewer;

import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.representation.owrl.AbstractObject;
import br.unicamp.cst.representation.owrl.Affordance;
import br.unicamp.cst.representation.owrl.Property;
import br.unicamp.cst.representation.owrl.QualityDimension;
import br.unicamp.cst.util.TimeStamp;
import br.unicamp.cst.util.TreeElement;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
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
public class MemoryInspector extends javax.swing.JFrame {
    
    Memory m;
    String lastobjectclass;
    DefaultMutableTreeNode ev_tn;
    DefaultMutableTreeNode ts_tn;
    DefaultMutableTreeNode info_tn;
    DefaultMutableTreeNode info_null;
    ObjectTreeNode obj;
    boolean lastwasnull = false;
    ImageIcon pause_icon = new ImageIcon(getClass().getResource("/pause-icon.png")); 
    ImageIcon play_icon = new ImageIcon(getClass().getResource("/play-icon.png"));
    MemoryInspector.MITimerTask tt;
    boolean capture=false;
    boolean needsreload=false;
    ArrayList<String> listtoavoidloops = new ArrayList<>();
    Logger log = Logger.getLogger(MemoryInspector.class.getCanonicalName());

    /**
     * Creates new form MemoryInspector
     *  @param mo Memory to be inspected
     */ 
    public MemoryInspector(Memory mo) {
        initComponents();
        setTitle(mo.getName());
        m = mo;
        Object ob = mo.getI();
        if (m.getI() != null) lastobjectclass = ob.getClass().getCanonicalName();
        obj = new ObjectTreeNode(mo.getName(),TreeElement.ICON_OBJECT);
        float eval = mo.getEvaluation().floatValue();
        ev_tn = obj.addObject(eval,"Eval");
        obj.add(ev_tn);
        Long timestamp = mo.getTimestamp();
        if (timestamp == null) timestamp = 0L;
        String ts = TimeStamp.getStringTimeStamp(timestamp,"dd/MM/yyyy HH:mm:ss.SSS");
        ts_tn = obj.addString(ts,"timeStamp");
        obj.add(ts_tn);
        DefaultTreeModel objTreeModel = new DefaultTreeModel(obj);
        objTree.setModel(objTreeModel);
        objTree.setCellRenderer(new MindRenderer(1));
        StartTimer();
        MouseListener ml;
        ml = new ObjectMouseAdapter(objTree);
        objTree.addMouseListener(ml);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                set_enable(false);
                tt.cancel();
            }
        });
    }
    
    
    public void StartTimer() {
        Timer t = new Timer();
        tt = new MemoryInspector.MITimerTask(this);
        t.scheduleAtFixedRate(tt, 0, 100);
    }
    
    public String getParent(String name) {
        String parent = null;
        String simpleparent="";
        String simplename = "";
        String[] mc = name.split("\\.");
        // Finding simpleparent -> "x[i]" : "x", "x" : ""
        if (mc.length > 1) 
             simplename = mc[mc.length-1];
        else simplename = mc[0];
        String[] mc2 = simplename.split("\\[");
        if (mc2.length > 1) 
            simpleparent = mc2[0];
        else simpleparent = "";
        // Finding parent
        if (mc.length > 1) {
            parent = mc[0];
            for (int i=1;i<mc.length-1;i++) 
                parent += "."+mc[i];
        }    
        else parent = "";
        // Compose parent + simpleparent
        if (mc.length > 1 && mc2.length > 1)
           parent += "."+simpleparent;
        else parent += simpleparent;
        return(parent);
    }
    
    public boolean nodeAlreadyExists(String name) {
        DefaultMutableTreeNode treeNode = obj.updateMap.get(name);
        if (treeNode == null) return(false);
        else return(true);
    }

    public void includeNode(Object o, String name) {
        String parent = getParent(name);
        DefaultMutableTreeNode parentNode = obj.updateMap.get(parent);
        if (parentNode != null) {
            // System.out.println("I'm planning to insert a new object "+name+" with parent "+parent+" "+TimeStamp.now());
            DefaultMutableTreeNode newobj = obj.addObject(o, name);
            parentNode.add(newobj);
            DefaultTreeModel tm = (DefaultTreeModel) objTree.getModel();
            tm.reload(parentNode);
        }
        else log.warning("I was not able to find the parent "+parent);
    }
    
    public void updateString(String s, String name) {
        DefaultMutableTreeNode treeNode = obj.updateMap.get(name);
        if (treeNode == null) {
            log.warning("I was unable to find "+name);
            return;
        }
        TreeElement element = (TreeElement) treeNode.getUserObject();
        String nodeName = element.getName();
        if (!name.equalsIgnoreCase(nodeName)) log.warning("Why the node name is different ? "+name+","+nodeName);
        element.setValue(s);
    }
    
    public void updateList(Object o, String name) {
        List ll = (List) o;
        String label;
        if (ll.size() > 0) label = "List["+ll.size()+"] of "+ll.get(0).getClass().getSimpleName();
        else label = "List[0]";
        updateString(label,name);
        updateArrayFields(name,ll.size());
        int i=0;
        for (Object ob : ll) {
            updateObject(ob,ToString.el(name, i));
            i++;
        }
    }
    
    public String relativeArrayName(String name, int l) {
        String relname = name+"["+l+"]";
        return(relname);
    }
    
    public void updateArrayFields(String name, int l) {
        //System.out.println("The array "+name+" now have only "+l+" elements");
        for(int i=l;obj.updateMap.get(relativeArrayName(name,l+1))!=null;i++) {
            obj.delNode(relativeArrayName(name,i));
            System.out.println("Deleting node "+relativeArrayName(name,i));
        }
    }
    
    public void updateArray(Object o, String name) {
        int l = Array.getLength(o);
        String type = o.getClass().getSimpleName();
        if (l>0) {
            Object otype = Array.get(o,0);
            if (otype != null)
                type = otype.getClass().getSimpleName();
        }
        updateString("Array["+l+"] of "+type,name);
        updateArrayFields(name,l);
        for (int i=0;i<l;i++) {
            Object oo = Array.get(o,i);
            updateObject(oo,ToString.el(name, i));
        }
    }
    
    public void updateAffordance(Affordance a, String name) {
        updateString("",name);
    }
    
    public void updateQualityDimension(QualityDimension q, String name) {
        updateString(q.getValue().toString(),name);
    }
    
    public void updateProperty(Property p, String name) {
        updateString(p.getResumedQDs(64),name);
        List<QualityDimension> qds = p.getQualityDimensions();
        for (QualityDimension q : qds) {
            updateQualityDimension(q,name+"."+q.getName());
        }
    }
    
    public void updateAbstractObject(AbstractObject ao, String name, boolean setvalue) {
        if (setvalue) updateString(ao.getName(),name);
        else updateString("",name);
        List<AbstractObject> parts = ao.getCompositeParts();
        for (AbstractObject oo : parts) {
            updateAbstractObject(oo,name+"."+oo.getName(),false);
        }
        List<AbstractObject> aggregates = ao.getAggregateParts();
        for (AbstractObject oo : aggregates) {
            updateAbstractObject(oo,name+"."+oo.getName(),false);
        }
        List<Property> props = ao.getProperties();
        for (Property p : props) {
            updateProperty(p,name+"."+p.getName());
        }
        List<Affordance> affordances = ao.getAffordances();
        for (Affordance a : affordances) {
            updateAffordance(a,name+"."+a.getName());
        }
    }
    
    public void updateObject(Object o, String name) {
        if (!nodeAlreadyExists(name)) {
            includeNode(o,name);
            return;
        }
        if (o == null) {
            updateString("<NULL>",name);
            return;
        }
        String s = ToString.from(o);
        if (s != null) {
            updateString(s,name);
        }
        else if (o instanceof List) {
            updateList(o,name);
        }
        else if (o.getClass().isArray()) {
            updateArray(o,name);
        }
        else if (o instanceof AbstractObject) {
            updateAbstractObject((AbstractObject)o,name,true);
        }
        else {
            // if the object is not primitive, first update the object element
            updateString(o.toString(),name);
            // then update each of its fields
            if (!listtoavoidloops.contains(name)) {
                listtoavoidloops.add(name);
                Field[] fields = o.getClass().getDeclaredFields();
                for (Field field : fields) {
                    String fname = field.getName();
                    if (!field.isAccessible()) field.setAccessible(true);
                        Object fo=null;
                    try {
                        fo = field.get(o);
                    } catch (Exception e) {e.printStackTrace();}
                    updateObject(fo,name+"."+fname);
                }
            }
        }
    }
    
    public void updateTree(Memory m) {
        String value;
        // update eval
        TreeElement teval = (TreeElement) ev_tn.getUserObject();
        value = String.format("%4.2f", m.getEvaluation());
        teval.setValue(value);
        // update timestamp
        long timestamp = m.getTimestamp();
        String ts = TimeStamp.getStringTimeStamp(timestamp,"dd/MM/yyyy HH:mm:ss.SSS");
        TreeElement tts = (TreeElement) ts_tn.getUserObject();
        tts.setValue(ts);
        // update info
        Object ii = m.getI();
        if (ii != null) {
            if (capture == true) {
                    set_enable(false);
                    capture = false;
                }
            if (info_tn == null) {
                info_tn = obj.addObject(ii,"Info");
                obj.add(info_tn);
                DefaultTreeModel tm = (DefaultTreeModel) objTree.getModel();
                tm.reload(obj);
                lastwasnull = false;
                return;
            }
            else {// this is not null and last was null
                if (lastwasnull) {
                    obj.remove(info_null);
                    obj.add(info_tn);
                    DefaultTreeModel tm = (DefaultTreeModel) objTree.getModel();
                    tm.reload(obj);
                    lastwasnull = false;
                    return;
                }
                else {// this is not null and last was not null
                    updateObject(ii,"Info");
                    DefaultTreeModel tm = (DefaultTreeModel) objTree.getModel();
                    tm.nodeChanged((TreeNode)tm.getRoot());
//                    if (needsreload == true) {
//                        //tm.reload(obj);
//                        needsreload = false;
//                        System.out.println("Reloading ..."+TimeStamp.now());
//                    }
                    lastwasnull = false;
                    return;
                }
            } 
        }
        else {
            if (info_null == null) {
                info_null = obj.addObject(null,"Info");
                obj.add(info_null);
                DefaultTreeModel tm = (DefaultTreeModel) objTree.getModel();
                tm.reload(obj);
                lastwasnull = true;
                return;
            } // this is null and last was null
            else if (lastwasnull) {
                DefaultTreeModel tm = (DefaultTreeModel) objTree.getModel();
                tm.nodeChanged((TreeNode)tm.getRoot());
                return;
            }
            else {// this is null and last was not null
                obj.remove(info_tn);
                obj.add(info_null);
                DefaultTreeModel tm = (DefaultTreeModel) objTree.getModel();
                tm.reload(obj);
                lastwasnull = true;
                return;
            }
        }
    }
    
    public void tick() {
        listtoavoidloops.clear();
        if (m != null) {
            updateTree(m);
        } else {
            log.warning("Mind is null");
        }
    }
    
    class MITimerTask extends TimerTask {

        MemoryInspector wov;
        boolean enabled = true;

        public MITimerTask(MemoryInspector wovi) {
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

        jScrollPane1 = new javax.swing.JScrollPane();
        objTree = new javax.swing.JTree();
        bPause = new javax.swing.JButton();
        bStop = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jScrollPane1.setViewportView(objTree);

        bPause.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pause-icon.png"))); // NOI18N
        bPause.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bPauseActionPerformed(evt);
            }
        });

        bStop.setIcon(new javax.swing.ImageIcon(getClass().getResource("/stop-playing-icon.png"))); // NOI18N
        bStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bStopActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(bPause, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bStop, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 471, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(bPause, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bStop, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 273, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void bPauseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bPauseActionPerformed
        if (tt.enabled == true) set_enable(false);
        else set_enable(true);
    }//GEN-LAST:event_bPauseActionPerformed

    private void bStopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bStopActionPerformed
        capture = true;
    }//GEN-LAST:event_bStopActionPerformed

    public void set_enable(boolean state) {
        if (state == true) bPause.setIcon(pause_icon); 
        else bPause.setIcon(play_icon); 
        tt.enabled = state; 
    } 
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bPause;
    private javax.swing.JButton bStop;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTree objTree;
    // End of variables declaration//GEN-END:variables
}

class ObjectMouseAdapter extends MouseAdapter {
    
    public ObjectMouseAdapter(JTree ttree)  {
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
                        else if (element instanceof Memory) {
                            Memory mo = (Memory) element;
                            JPopupMenu popup = new JPopupMenu();
                            JMenuItem jm1 = new JMenuItem("Inspect");
                            ActionListener al = new ActionListener() {
                                public void actionPerformed(ActionEvent e) {
                                    String className = "Empty";
                                    Object o = mo.getI();
                                    if (o != null) className = o.getClass().getCanonicalName();
                                    MemoryViewer mv = new MemoryViewer(mo);
                                    mv.setVisible(true);
                                }
                            };
                            jm1.addActionListener(al);
                            popup.add(jm1);
                            popup.show(tree, e.getX(), e.getY());
                        }
                    }
                }
            }
}    
