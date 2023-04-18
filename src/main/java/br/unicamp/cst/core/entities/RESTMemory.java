package br.unicamp.cst.core.entities;

import br.unicamp.cst.io.rest.MemoryJson;
import br.unicamp.cst.support.InterfaceAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import express.Express;
import express.middleware.CorsOptions;
import express.middleware.Middleware;

import java.util.HashSet;
import java.util.Set;

public class RESTMemory implements Memory {
    long refresh = 0; // A refresh of 0 means that every call will generate a new probe in mind
    long lastaccess = 0;
    String lastmessage = "";
    Memory internalMemory;

    private Long idmemoryobject;

    /**
     * Date when the data was "created" in milliseconds
     */
    private Long timestamp;

    /**
     * An evaluation of this memory object based on inner references
     */
    private volatile Double evaluation;

    /**
     * Information contained in the memory object.
     */
    private volatile Object I;

    /**
     * Type of the memory object
     */
    private String name;

    /**
     *
     * @param port the port to install the REST server
     */

    /**
     * List of codelets that observes memory
     */
    private Set<MemoryObserver> memoryObservers;


    public RESTMemory(int port) {
        this(port,false);
    }

    /**
     *
     * @param hostname hostname of the REST server
     * @param port the port to install the REST server
     */
    public RESTMemory(String hostname, int port) {
        this(hostname, port,false);
    }

    /**
     *
     * @param port the port to install the REST server
     * @param pretty set this to true to generate pretty printing JSON in the REST server
     */
    public RESTMemory(int port, boolean pretty) {
        this(port,pretty,"*");
    }


    /**
     * @param hostname the hostname of the REST server
     * @param port the port to install the REST server
     * @param pretty set this to true to generate pretty printing JSON in the REST server
     */
    public RESTMemory(String hostname, int port, boolean pretty) {
        this(hostname, port,pretty,"*", 0L);
    }

    /**
     *
     * @param port the port to install the REST server
     * @param pretty set this to true to generate pretty printing JSON in the REST server
     * @param origin a pattern for users allowed to access the server - use "*" to allow everyone
     */
    public RESTMemory(int port, boolean pretty, String origin) {
        this("localhost", port,pretty,origin,0L);
    }

    /**
     * @param hostname the hostname of the REST server
     * @param port the port to install the REST server
     * @param pretty set this to true to generate pretty printing JSON in the REST server
     * @param origin a pattern for users allowed to access the server - use "*" to allow everyone
     * @param nrefresh the refresh period in milliseconds
     */
    public RESTMemory(String hostname, int port, boolean pretty, String origin, long nrefresh) {
        refresh = nrefresh;
        Express app = new Express(hostname);
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
                MemoryJson myJson = new MemoryJson(this);
                lastmessage = gson.toJson(myJson);
                lastaccess = currentaccess;
            }
            res.send(lastmessage);
        });
        app.post("/", (req, res) -> {
            // Will match every request which uses the 'POST' method and matches the /login' path
            long currentaccess = System.currentTimeMillis();
            long diff = currentaccess - lastaccess;
            if (diff > refresh) {
                String I = req.getFormQuery("I");
                double evaluation = Double.parseDouble(req.getFormQuery("evaluation"));
                // Process data
                this.setI(I);
                this.setEvaluation(evaluation);
                lastmessage = "I: " + I + ", Evaluation: " + evaluation;
                lastaccess = currentaccess;
            }
            res.send(lastmessage);
        });
        app.listen(port);
    }

    /**
     * Gets the id of the Memory Object.
     *
     * @return the id of the Memory Object.
     */
    public synchronized Long getIdmemoryobject() {
        return this.idmemoryobject;
    }

    /**
     * Sets the id of the Memory Object.
     *
     * @param idmemoryobject
     *            the id of the Memory Object to set.
     */
    public synchronized void setIdmemoryobject(Long idmemoryobject) {
        this.idmemoryobject = idmemoryobject;
    }

    /**
     * Gets the info of the Memory Object.
     *
     * @return the info of the Memory Object.
     */
    public synchronized Object getI() {
        return this.I;
    }

    /**
     * Sets the info in memory object.
     *
     * @param info
     *            the info in memory object to set.
     */
    public synchronized int setI(Object info) {
        this.I = info;
        setTimestamp(System.currentTimeMillis());
        notifyMemoryObservers();

        return -1;
    }

    private synchronized void notifyMemoryObservers() {
        if (memoryObservers != null && !memoryObservers.isEmpty()) {
            for (MemoryObserver memoryObserver : memoryObservers) {
                memoryObserver.notifyCodelet();
            }
        }
    }

    public synchronized Long getTimestamp() {
        return this.timestamp;
    }

    /**
     * Add a memory observer to its list
     * @param memoryObserver the MemoryObserve to be added
     */
    @Override
    public void addMemoryObserver(MemoryObserver memoryObserver) {
        if (this.memoryObservers == null) {
            this.memoryObservers = new HashSet<MemoryObserver>();
        }
        this.memoryObservers.add(memoryObserver);
    }

    /**
     * Add a memory observer to its list
     *
     * @return a set of memoryObservers
     */

    public Set<MemoryObserver> getMemoryObservers() {
        return this.memoryObservers;
    }


    /**
     * Sets the timestamp of this Memory Object.
     *
     * @param timestamp
     *            the timestamp of this Memory Object.
     */
    public synchronized void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Gets the type of the memory.
     *
     * @return the type
     */
    public synchronized String getName() {
        return name;
    }

    @Override
    public void setType(String type) {

    }


    /**
     * Sets the name of the memory.
     *
     * @param name
     *            the type to set.
     */
    public synchronized void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the evaluation of the Memory Object.
     *
     * @return the evaluation of the Memory Object.
     */
    public synchronized Double getEvaluation() {
        return evaluation;
    }

    /**
     * Sets the evaluation of the Memory Object.
     *
     * @param evaluation
     *            the evaluation to set.
     */
    public synchronized void setEvaluation(Double evaluation) {
        this.evaluation = evaluation;
    }
}