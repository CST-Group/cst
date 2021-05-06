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

import java.awt.Component;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 *
 * @author suelenmapa
 */
public class RendererJTree extends DefaultTreeCellRenderer {

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean exp, boolean leaf, int row, boolean hasFocus) {

        ImageIcon img = new ImageIcon(this.getClass().getClassLoader().getResource("configuration.png"));

        DefaultMutableTreeNode dmtn = (DefaultMutableTreeNode) value;
        DefaultMutableTreeNode objectNode;
//
        TreeElement node = (TreeElement) dmtn.getUserObject();
        switch (node.getIcon()) {
            case TreeElement.ICON_COMPOSITE:
                img = new ImageIcon(this.getClass().getClassLoader().getResource("object.png"));
                break;
            case TreeElement.ICON_AGGREGATE:
                img = new ImageIcon(this.getClass().getClassLoader().getResource("object2.gif"));
                break;    
            case TreeElement.ICON_PROPERTY:
                img = new ImageIcon(this.getClass().getClassLoader().getResource("property.png"));
                break;
            case TreeElement.ICON_QUALITYDIM:
                img = new ImageIcon(this.getClass().getClassLoader().getResource("qualityDim.png"));
                break;
            case TreeElement.ICON_VALUE:
                img = new ImageIcon(this.getClass().getClassLoader().getResource("value.png"));
                break;
            case TreeElement.ICON_MIND:
                img = new ImageIcon(this.getClass().getClassLoader().getResource("mind.png"));
                break;
            case TreeElement.ICON_CODELET:
                img = new ImageIcon(this.getClass().getClassLoader().getResource("codelet.png"));
                break;
            case TreeElement.ICON_CODELETS:
                img = new ImageIcon(this.getClass().getClassLoader().getResource("codelets.png"));
                break;
            case TreeElement.ICON_MEMORY:
                img = new ImageIcon(this.getClass().getClassLoader().getResource("memory.png"));
                break;
            case TreeElement.ICON_MEMORIES:
                img = new ImageIcon(this.getClass().getClassLoader().getResource("memories.png"));
                break;
            case TreeElement.ICON_CONTAINER:
                img = new ImageIcon(this.getClass().getClassLoader().getResource("container.png"));
                break;
            case TreeElement.ICON_MO:
                img = new ImageIcon(this.getClass().getClassLoader().getResource("mo.png"));
                break;
            case TreeElement.ICON_INPUT:
                img = new ImageIcon(this.getClass().getClassLoader().getResource("input.png"));
                break;
            case TreeElement.ICON_OUTPUT:
                img = new ImageIcon(this.getClass().getClassLoader().getResource("output.png"));
                break;
            case TreeElement.ICON_BROADCAST:    
                img = new ImageIcon(this.getClass().getClassLoader().getResource("broadcast.png"));
                break;
            case TreeElement.ICON_AFFORDANCE:    
                img = new ImageIcon(this.getClass().getClassLoader().getResource("codelet2.png"));
                break;
            case TreeElement.ICON_OBJECT2:    
                img = new ImageIcon(this.getClass().getClassLoader().getResource("object2.png"));
                break; 
            case TreeElement.ICON_OBJECT3:    
                img = new ImageIcon(this.getClass().getClassLoader().getResource("object3.png"));
                break;     
            // TODO: Criar um icone de erro
        }
        setOpenIcon(img);
        setClosedIcon(img);
        setLeafIcon(img);

        String hex = "#" + Integer.toHexString(node.getColor().getRGB()).substring(2);
        objectNode = new DefaultMutableTreeNode("<html><font color=\"" + hex + "\">" + node.getNamePlusValuePlusId() + "</font></html>");
        value = objectNode;
        super.getTreeCellRendererComponent(tree, value, sel, exp, leaf, row, hasFocus);

        return this;

    }
}
