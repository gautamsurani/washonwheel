package com.washonwheel.android.Pojo;

/**
 * Created by welcome on 16-12-2017.
 */

public class ModelList {
    String typeID, name;

    public String getTypeID() {
        return typeID;
    }

    public void setTypeID(String typeID) {
        this.typeID = typeID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ModelList(String typeID, String name) {

        this.typeID = typeID;
        this.name = name;
    }
    public ModelList(){}
}
