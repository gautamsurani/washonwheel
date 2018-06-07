package com.washonwheel.android.Pojo;

import java.util.List;

/**
 * Created by welcome on 21-12-2017.
 */

public class PackDataList {
    String package_id, package_name;
    List<PackService> packServices;

    public String getPackage_id() {
        return package_id;
    }

    public void setPackage_id(String package_id) {
        this.package_id = package_id;
    }

    public String getPackage_name() {
        return package_name;
    }

    public void setPackage_name(String package_name) {
        this.package_name = package_name;
    }

    public List<PackService> getPackServices() {
        return packServices;
    }

    public void setPackServices(List<PackService> packServices) {
        this.packServices = packServices;
    }

    public PackDataList(String package_id, String package_name, List<PackService> packServices) {

        this.package_id = package_id;
        this.package_name = package_name;
        this.packServices = packServices;
    }
}
