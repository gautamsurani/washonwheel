package com.washonwheel.android.Pojo;

/**
 * Created by welcome on 22-12-2017.
 */

public class UserPackageServiceList {
    String pack_ser_id, types, service_name, use_status;

    public UserPackageServiceList() {
    }

    public String getPack_ser_id() {
        return pack_ser_id;
    }

    public void setPack_ser_id(String pack_ser_id) {
        this.pack_ser_id = pack_ser_id;
    }

    public String getTypes() {
        return types;
    }

    public void setTypes(String types) {
        this.types = types;
    }

    public String getService_name() {
        return service_name;
    }

    public void setService_name(String service_name) {
        this.service_name = service_name;
    }

    public String getUse_status() {
        return use_status;
    }

    public void setUse_status(String use_status) {
        this.use_status = use_status;
    }

    public UserPackageServiceList(String pack_ser_id, String types, String service_name, String use_status) {

        this.pack_ser_id = pack_ser_id;
        this.types = types;
        this.service_name = service_name;
        this.use_status = use_status;
    }
}
