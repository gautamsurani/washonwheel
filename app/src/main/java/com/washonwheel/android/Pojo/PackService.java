package com.washonwheel.android.Pojo;

/**
 * Created by welcome on 21-12-2017.
 */

public class PackService {
    String pack_ser_id, short_desc;

    public String getPack_ser_id() {
        return pack_ser_id;
    }

    public void setPack_ser_id(String pack_ser_id) {
        this.pack_ser_id = pack_ser_id;
    }

    public String getShort_desc() {
        return short_desc;
    }

    public void setShort_desc(String short_desc) {
        this.short_desc = short_desc;
    }

    public PackService(String pack_ser_id, String short_desc) {

        this.pack_ser_id = pack_ser_id;
        this.short_desc = short_desc;
    }

    public PackService() {
    }
}
