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
import br.unicamp.cst.representation.owrl.AbstractObject;
import br.unicamp.cst.util.Pair;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author suelenmapa
 */
public class CustomizedListener extends OwrlBaseListener {

    private List<Pair<String, List<AbstractObject>>> memory = new ArrayList<Pair<String, List<AbstractObject>>>();

    private int contKey = 0;

    @Override
    public void enterStat(OwrlParser.StatContext ctx) {

        String command = ctx.command().getText();

        List<AbstractObject> listWO = new ArrayList<>();

        for (int i = 0; i < ctx.expr().size(); i++) {

            listWO.add(readElementsFromExpr(ctx.expr(i)));

        }

        memory.add(new Pair(command, listWO));

    }

    public AbstractObject readElementsFromExpr(OwrlParser.ExprContext ctx) {

        String name = ctx.name().object().ID().getText();
        int id = Integer.parseInt(ctx.name().cod().INT_VALUE().getText());
        AbstractObject wo;

        if (!ctx.atrib().isEmpty()) {

            wo = new AbstractObject(name, readProperty(ctx.atrib(0).property()), readPart(ctx.atrib(0).part()));

        } else {

            wo = new AbstractObject(name);
        }

        return wo;

    }

    public List<AbstractObject> readPart(List<OwrlParser.PartContext> ctxPart) {

        List<AbstractObject> parts = new ArrayList<AbstractObject>();
        AbstractObject onePart;

        if (ctxPart != null) {
            for (int i = 0; i < ctxPart.size(); i++) {

                String namePart = ctxPart.get(i).name().object().ID().getText();
                int id = Integer.parseInt(ctxPart.get(i).name().cod().INT_VALUE().getText());
                List<Property> listPropertiesPart = readProperty(ctxPart.get(i).property());
                List<AbstractObject> subparts = readPart(ctxPart.get(i).part());

                onePart = new AbstractObject(namePart, listPropertiesPart, subparts);
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

    public List<Pair<String, List<AbstractObject>>> getMemory() {
        return memory;
    }

}
