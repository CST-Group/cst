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
package br.unicamp.cst.representation.owrl.grammar;

import br.unicamp.cst.representation.owrl.Property;
import br.unicamp.cst.representation.owrl.QualityDimension;
import br.unicamp.cst.representation.owrl.WorldObject;
import br.unicamp.cst.util.Pair;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author suelenmapa
 */
public class CustomizedListener extends OwrlBaseListener {

    private List<Pair<String, List<WorldObject>>> memory = new ArrayList<Pair<String, List<WorldObject>>>();

    private int contKey = 0;

    @Override
    public void enterStat(OwrlParser.StatContext ctx) {

        String command = ctx.command().getText();

        List<WorldObject> listWO = new ArrayList<>();

        for (int i = 0; i < ctx.expr().size(); i++) {

            listWO.add(readElementsFromExpr(ctx.expr(i)));

        }

        memory.add(new Pair(command, listWO));

    }

    public WorldObject readElementsFromExpr(OwrlParser.ExprContext ctx) {

        String name = ctx.name().object().ID().getText();
        int id = Integer.parseInt(ctx.name().cod().INT_VALUE().getText());
        WorldObject wo;

        if (!ctx.atrib().isEmpty()) {

            wo = new WorldObject(name, id, readProperty(ctx.atrib(0).property()), readPart(ctx.atrib(0).part()));

        } else {

            wo = new WorldObject(name, id);
        }

        return wo;

    }

    public List<WorldObject> readPart(List<OwrlParser.PartContext> ctxPart) {

        List<WorldObject> parts = new ArrayList<WorldObject>();
        WorldObject onePart;

        if (ctxPart != null) {
            for (int i = 0; i < ctxPart.size(); i++) {

                String namePart = ctxPart.get(i).name().object().ID().getText();
                int id = Integer.parseInt(ctxPart.get(i).name().cod().INT_VALUE().getText());
                List<Property> listPropertiesPart = readProperty(ctxPart.get(i).property());
                List<WorldObject> subparts = readPart(ctxPart.get(i).part());

                onePart = new WorldObject(namePart, id, listPropertiesPart, subparts);
                parts.add(onePart);

            }

        }

        return parts;

    }

    public List<Property> readProperty(List<OwrlParser.PropertyContext> ctxProperty) {

        List<Property> listProperties = new ArrayList<Property>();

        if (ctxProperty != null) {

            for (int i = 0; i < ctxProperty.size(); i++) {

                String nameProperty = ctxProperty.get(i).ID().getText();

                Property property = new Property(nameProperty);

                for (int s = 0; s < ctxProperty.get(i).qualitydimension().size(); s++) {

                    String st = ctxProperty.get(i).qualitydimension(s).value().getText();

                    String newvalue = st.substring(1, (st.length() - 1));

                    String qd = ctxProperty.get(i).qualitydimension(s).ID().getText();

                    property.addQualityDimension(new QualityDimension(qd, newvalue));

                }

                listProperties.add(property);

            }

        }

        return listProperties;

    }

    public List<Pair<String, List<WorldObject>>> getMemory() {
        return memory;
    }

}
