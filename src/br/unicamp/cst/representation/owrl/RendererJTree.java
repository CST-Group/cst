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

package br.unicamp.cst.representation.owrl;

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

        ImageIcon img = new ImageIcon(this.getClass().getResource("/br/unicamp/cst/images/configuration.png"));

        DefaultMutableTreeNode dmtn = (DefaultMutableTreeNode) value;
        DefaultMutableTreeNode objectNode;
//
        TreeElement node = (TreeElement) dmtn.getUserObject();
        switch (node.getIcon()) {
            case TreeElement.ICON_OBJECT:
                img = new ImageIcon(this.getClass().getResource("/br/unicamp/cst/images/object.png"));
                break;
            case TreeElement.ICON_PROPERTY:
                img = new ImageIcon(this.getClass().getResource("/br/unicamp/cst/images/property.png"));
                break;
            case TreeElement.ICON_QUALITYDIM:
                img = new ImageIcon(this.getClass().getResource("/br/unicamp/cst/images/qualityDim.png"));
                break;
            case TreeElement.ICON_VALUE:
                img = new ImageIcon(this.getClass().getResource("/br/unicamp/cst/images/value.png"));
                break;
            // TODO: Criar um icone de erro
        }
        setOpenIcon(img);
        setClosedIcon(img);
        setLeafIcon(img);

        String hex = "#" + Integer.toHexString(node.getColor().getRGB()).substring(2);
        objectNode = new DefaultMutableTreeNode("<html><font color=\"" + hex + "\">" + node.getName() + "</font></html>");
        value = objectNode;
        super.getTreeCellRendererComponent(tree, value, sel, exp, leaf, row, hasFocus);

        return this;

    }
}
