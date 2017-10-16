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

import java.awt.Color;

/**
 * @author Suelen Mapa
 */
public class TreeElement {

    private String name;
    private String value="";
    private Color color;
    private Object element;
    private int icon_type;
    private int id_node = 0;
    private static int ncode=0;
    
    public static final int NODE_NORMAL = 1;
    public static final int NODE_CHANGE = 2;
    public static final int NODE_EXCLUSION = 3;
    public static final int NODE_CREATION = 4;

    public static final int ICON_OBJECT = 2;
    public static final int ICON_CONFIGURATION = 1;
    public static final int ICON_COMPOSITE = 2;
    public static final int ICON_AGGREGATE = 3;
    public static final int ICON_PROPERTY = 4;
    public static final int ICON_QUALITYDIM = 5;
    public static final int ICON_VALUE = 6;
    public static final int ICON_MIND = 7;
    public static final int ICON_CODELET = 8;
    public static final int ICON_CODELETS = 9;
    public static final int ICON_MEMORY = 10;
    public static final int ICON_MEMORIES = 11;
    public static final int ICON_CONTAINER = 12;
    public static final int ICON_MO = 13;
    public static final int ICON_INPUT = 14;
    public static final int ICON_OUTPUT = 15;
    public static final int ICON_BROADCAST = 16;
    public static final int ICON_AFFORDANCE = 17;

   
    public TreeElement(String name, int node_type, Object element, int typeIcon) {
        //this(name,node_type,element,typeIcon,0);
        this(name,node_type,element,typeIcon,ncode++);
    }
    
    public TreeElement(String name, String value, int node_type, Object element, int typeIcon) {
        //this(name,node_type,element,typeIcon,0);
        this(name, node_type,element,typeIcon,ncode++);
        this.value = value;
    }
    
    public static void reset() {
        ncode = 0;
    }
    
     public TreeElement(String name, int node_type, Object element, int typeIcon, int id) {
    
        setColor(node_type);
        setIcon(typeIcon);
        setId_node(id);
        //setName(name+"_"+getId_node());
        //setName(name+" ["+getId_node()+"]");
         setName(name);        
        this.element = element;
        this.icon_type = typeIcon;
    }
    
    
     public String getNamePlusValuePlusId() {
        return getName()+ getValue()+" ["+getId_node()+"]";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(int node_type) {
        switch (node_type) {
            case NODE_NORMAL:
                color = Color.BLACK;
                break;
            case NODE_CHANGE:
                color = Color.ORANGE;
                break;
            case NODE_EXCLUSION:
                color = Color.RED;
                break;
            case NODE_CREATION:
                color = Color.GREEN;
                break;

        }
    }

    public void setIcon(int icon_type) {
        this.icon_type = icon_type;
    }

    public int getIcon() {
        return icon_type;
    }

    public Object getElement() {
        return element;
    }

    public int getId_node() {
        return id_node;
    }

    public void setId_node(int id_node) {
        this.id_node = id_node;
    }
    
    
}
