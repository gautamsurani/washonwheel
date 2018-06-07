package com.washonwheel.android.Pojo;

import java.util.List;

/**
 * Created by welcome on 22-12-2017.
 */

public class UserPackageList {
    String package_id, package_name, package_expiry_date;
    List<UserPackageServiceList> userPackageServiceLists;

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

    public String getPackage_expiry_date() {
        return package_expiry_date;
    }

    public void setPackage_expiry_date(String package_expiry_date) {
        this.package_expiry_date = package_expiry_date;
    }

    public List<UserPackageServiceList> getUserPackageServiceLists() {
        return userPackageServiceLists;
    }

    public void setUserPackageServiceLists(List<UserPackageServiceList> userPackageServiceLists) {
        this.userPackageServiceLists = userPackageServiceLists;
    }

    public UserPackageList(String package_id, String package_name, String package_expiry_date, List<UserPackageServiceList> userPackageServiceLists) {

        this.package_id = package_id;
        this.package_name = package_name;
        this.package_expiry_date = package_expiry_date;
        this.userPackageServiceLists = userPackageServiceLists;
    }
}
