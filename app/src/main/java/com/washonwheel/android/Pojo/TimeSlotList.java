package com.washonwheel.android.Pojo;

/**
 * Created by welcome on 16-12-2017.
 */

public class TimeSlotList {
    String slotID,name,status;

    public String getSlotID() {
        return slotID;
    }

    public void setSlotID(String slotID) {
        this.slotID = slotID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public TimeSlotList(String slotID, String name, String status) {

        this.slotID = slotID;
        this.name = name;
        this.status = status;
    }
    public TimeSlotList(){}
}
