/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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

        ImageIcon img = new ImageIcon(this.getClass().getResource("/images/configuration.png"));

        DefaultMutableTreeNode dmtn = (DefaultMutableTreeNode) value;
        DefaultMutableTreeNode objectNode;
//
        TreeElement node = (TreeElement) dmtn.getUserObject();
        switch (node.getIcon()) {
            case TreeElement.ICON_OBJECT:
                img = new ImageIcon(this.getClass().getResource("/images/object.png"));
                break;
            case TreeElement.ICON_PROPERTY:
                img = new ImageIcon(this.getClass().getResource("/images/property.png"));
                break;
            case TreeElement.ICON_QUALITYDIM:
                img = new ImageIcon(this.getClass().getResource("/images/qualityDim.png"));
                break;
            case TreeElement.ICON_VALUE:
                img = new ImageIcon(this.getClass().getResource("/images/value.png"));
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
