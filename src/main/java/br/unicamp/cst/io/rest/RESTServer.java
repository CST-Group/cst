/***********************************************************************************************
 * Copyright (c) 2012  DCA-FEEC-UNICAMP
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 * <p>
 * Contributors:
 * K. Raizer, A. L. O. Paraense, E. M. Froes, R. R. Gudwin - initial API and implementation
 * **********************************************************************************************/
package br.unicamp.cst.io.rest;

import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.MemoryContainer;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.core.entities.Mind;
import br.unicamp.cst.support.InterfaceAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import express.Express;
import express.middleware.CorsOptions;
import express.middleware.Middleware;
import express.utils.Status;
import java.util.List;
import java.util.ArrayList;

/**
 * This is the main class for using REST to monitor inner activities from CST
 * It is used to provide a REST server to monitor whatever is happening within a CST mind 
 * Depending on the constructor, the user can set the mind, the port to be used, if 
 * the JSON describing the inner details of the mind should be rendered with pretty printing
 * and a way to limit the access of external users to the server
 * @author rgudwin
 */
public class RESTServer {
    
    long refresh = 0; // A refresh of 0 means that every call will generate a new probe in mind
    long lastaccess = 0;
    String lastmessage = "";
    
    /**
     * 
     * @param m the mind to observe
     * @param port the port to install the REST server
     */
    public RESTServer(Mind m, int port) {
        this(m,port,false);
    }
    
    /**
     * 
     * @param m the mind to observe
     * @param port the port to install the REST server
     * @param pretty set this to true to generate pretty printing JSON in the REST server
     */
    public RESTServer(Mind m, int port, boolean pretty) {
        this(m,port,pretty,"*");
    }
    
    /**
     * 
     * @param m the mind to observe
     * @param port the port to install the REST server
     * @param pretty set this to true to generate pretty printing JSON in the REST server
     * @param origin a pattern for users allowed to access the server - use "*" to allow everyone
     */
    public RESTServer(Mind m, int port, boolean pretty, String origin) {
        this(m,port,pretty,origin,0L);
    }
    
    /**
     * 
     * @param m the mind to observe
     * @param port the port to install the REST server
     * @param pretty set this to true to generate pretty printing JSON in the REST server
     * @param origin a pattern for users allowed to access the server - use "*" to allow everyone
     * @param nrefresh the refresh period in milliseconds
     */
    public RESTServer(Mind m, int port, boolean pretty, String origin, long nrefresh) {
        refresh = nrefresh;
        Express app = new Express();
        Gson gson;
        if (pretty)
            gson = new GsonBuilder().registerTypeAdapter(Memory.class, new InterfaceAdapter<MemoryObject>())
                             .registerTypeAdapter(Memory.class, new InterfaceAdapter<MemoryContainer>())
                             .setPrettyPrinting().create();
        else 
            gson = new Gson();
        CorsOptions corsOptions = new CorsOptions();
        corsOptions.setOrigin(origin);
        app.use(Middleware.cors(corsOptions));
        app.get("/", (req, res) -> {
            long currentaccess = System.currentTimeMillis();
            long diff = currentaccess - lastaccess;
            if (diff > refresh) {
                MindJson myJson = new MindJson(m);
                lastmessage = gson.toJson(myJson);
                lastaccess = currentaccess;
            }
            res.send(lastmessage);
        });
        app.get("/rawmemory", (req, res) -> {
            long currentaccess = System.currentTimeMillis();
            long diff = currentaccess - lastaccess;
            if (diff > refresh) {
                List<Memory> lm = m.getRawMemory().getAllMemoryObjects();
                ArrayList lmm = new ArrayList();
                for (Memory mem : lm) {
                    lmm.add(mem.getId());
                }
                lastmessage = gson.toJson(lmm);
                lastaccess = currentaccess;
            }
            res.send(lastmessage);
        });
        app.get("/rawmemory/:id", (req, res) -> {
                String ids = req.getParam("id");
                int id;
                try {
                   id = Integer.parseInt(ids);
                } catch(Exception e) {
                    id = -1;
                }
                List<Memory> allmem = m.getRawMemory().getAllMemoryObjects();
                for (Memory mem : allmem) {
                    if (mem.getId() == id) {
                        MemoryJson mj = new MemoryJson(mem);
                        String message = gson.toJson(mj);
                        res.send(message);
                        return;
                    }
                }
                String message = "Memory Object not found ...";
                res.setStatus(Status._404);
                res.send(message);
        });
        app.get("/rawmemory/:id/:param", (req, res) -> {
                String ids = req.getParam("id");
                String param = req.getParam("param");
                int id;
                try {
                   id = Integer.parseInt(ids);
                } catch(Exception e) {
                    id = -1;
                }
                List<Memory> allmem = m.getRawMemory().getAllMemoryObjects();
                for (Memory mem : allmem) {
                    if (mem.getId() == id) {
                        String message;
                        if (param.equalsIgnoreCase("I")) 
                            message = gson.toJson(mem.getI());
                        else if (param.equalsIgnoreCase("timestamp")) 
                            message = gson.toJson(mem.getTimestamp());
                        else if (param.equalsIgnoreCase("evaluation"))
                            message = gson.toJson(mem.getEvaluation());
                        else if (param.equalsIgnoreCase("name"))
                            message = gson.toJson(mem.getName());
                        else if (param.equalsIgnoreCase("id"))
                            message = gson.toJson(mem.getId());
                        else message = "Not Found";
                        res.send(message);
                        return;
                    }
                }
                String message = "Memory Object not found ...";
                res.setStatus(Status._404);
                res.send(message);
        });
        app.listen(port);
    }
    
}
