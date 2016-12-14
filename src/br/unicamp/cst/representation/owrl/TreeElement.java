package br.unicamp.cst.representation.owrl;

import java.awt.Color;

/**
 * @author Suelen Mapa
 */
public class TreeElement {

    private String name;
    private Color color;
    private Object element;
    private int icon_type;
    public static final int NODE_NORMAL = 1;
    public static final int NODE_CHANGE = 2;
    public static final int NODE_EXCLUSION = 3;
    public static final int NODE_CREATION = 4;

    public static final int ICON_CONFIGURATION = 1;
    public static final int ICON_OBJECT = 2;
    public static final int ICON_PROPERTY = 3;
    public static final int ICON_QUALITYDIM = 4;
    public static final int ICON_VALUE = 5;

    public TreeElement(String name, int node_type, Object element, int typeIcon) {
        setName(name);
        setColor(node_type);
        setIcon(typeIcon);
        this.element = element;
        this.icon_type = typeIcon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
                color = Color.BLUE;
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
}
