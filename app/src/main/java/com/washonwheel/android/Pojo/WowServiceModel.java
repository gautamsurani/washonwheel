package com.washonwheel.android.Pojo;

/**
 * Created by welcome on 12-12-2017.
 */

public class WowServiceModel {
    String ID,name,image;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public WowServiceModel(String ID, String name, String image) {

        this.ID = ID;
        this.name = name;
        this.image = image;
    }
    public WowServiceModel(){}
}
