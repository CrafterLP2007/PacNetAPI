package de.crafterlp.net;

import java.util.ArrayList;

public class DataPackage extends ArrayList<Object> {


    private static final long serialVersionUID = 8501296964229015349L;

    private String senderID = "UNSIGNED";

    public DataPackage(String id, Object... o) {
        this.add(0, id);
        for (Object current : o) {
            this.add(current);
        }
    }

    public String id() {
        if (!(this.get(0) instanceof String)) {
            throw new IllegalArgumentException("Identifier of Datapackage is not a String");
        }
        return (String) this.get(0);
    }

    public String getSenderID() {
        return this.senderID;
    }

    protected void sign(String senderID) {
        this.senderID = senderID;
    }

    @Deprecated
    public ArrayList<Object> open() {
        return this;
    }

}
