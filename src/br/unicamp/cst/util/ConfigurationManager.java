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

package br.unicamp.cst.util;

import br.unicamp.cst.representation.owrl.AbstractObject;
import br.unicamp.cst.util.Pair;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 * @author Suelen Mapa
 */
public class ConfigurationManager {

    private List<Pair<String, AbstractObject>> listConfs;
    private int currentConfiguration = -1;

    public ConfigurationManager() {
        listConfs = new ArrayList<Pair<String, AbstractObject>>();
    }

    private void create(AbstractObject obj) {
        listConfs.get(currentConfiguration).getSecond().addAggregatePart(obj);
    }

    private boolean destroy(AbstractObject obj, List<AbstractObject> list) {
        for (AbstractObject obj_search : list) {
            if (obj_search == obj) {
                list.remove(obj_search);
                return true;
            } else {
                if (destroy(obj, obj_search.getCompositeParts())) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean applyCommands(List<Pair<String, List<AbstractObject>>> commands) {
        newConfiguration(String.format("%04d", listConfs.size() + 1));
        for (Pair<String, List<AbstractObject>> entry : commands) {
            switch (entry.getFirst()) {
                case "create":
                    for (AbstractObject obj : entry.getSecond()) {
                        create(obj);
                    }
                    break;
                case "destroy":
                    for (AbstractObject obj : entry.getSecond()) {
                        if (!(destroy(obj, listConfs.get(currentConfiguration).getSecond().getAggregateParts()))) {
                            String message = "Unknown object: " + obj.getName();
                            JOptionPane.showMessageDialog(null, message);
                            System.err.println(message);
                        }
                    }
                    break;
                case "modify":
                    for (AbstractObject obj : entry.getSecond()) {
                        if (!destroy(obj, listConfs.get(currentConfiguration).getSecond().getAggregateParts())) {
                            String message = "Unknown object: " + obj.getName();
                            JOptionPane.showMessageDialog(null, message);
                            System.err.println(message);
                        } else {
                            create(obj);
                        }
                    }
                    break;
                default:
                    System.out.println("Unknown command: " + entry.getFirst());
                    return false;
            }
            for (AbstractObject wo : entry.getSecond()) {
                System.out.println(wo.getName());
            }
        }
        if (listConfs.size() == 1 && listConfs.get(0).getSecond().getAggregateParts().isEmpty()) {
            removeConfiguration(0);
        }
        return true;
    }

    public boolean selectConfiguration(String name) {
        for (int i = 0; i < listConfs.size(); ++i) {
            if (listConfs.get(i).getFirst().compareTo(name) == 0) {
                currentConfiguration = i;
                return true;
            }
        }
        return false;
    }

    public void newConfiguration(String name) {
        if (listConfs.isEmpty()) {
            listConfs.add(new Pair(name, new AbstractObject("")));
        } else {
            listConfs.add(new Pair(name, listConfs.get(listConfs.size() - 1).getSecond().clone()));
        }
        currentConfiguration = listConfs.size() - 1;
    }

    public void removeConfiguration(int index) {
        listConfs.remove(index);
    }

    public int size() {
        return listConfs.size();
    }

//    public AbstractObject getConfiguration(int index) {
//        return listConfs.get(index).second.getAggregatePart();
//    }
}
