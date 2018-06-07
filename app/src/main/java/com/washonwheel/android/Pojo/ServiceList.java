package com.washonwheel.android.Pojo;

/**
 * Created by welcome on 16-12-2017.
 */

public class ServiceList {
    String serviceID,servicepriceID,service_name,service_price;

    public String getServiceID() {
        return serviceID;
    }

    public void setServiceID(String serviceID) {
        this.serviceID = serviceID;
    }

    public String getServicepriceID() {
        return servicepriceID;
    }

    public void setServicepriceID(String servicepriceID) {
        this.servicepriceID = servicepriceID;
    }

    public String getService_name() {
        return service_name;
    }

    public void setService_name(String service_name) {
        this.service_name = service_name;
    }

    public String getService_price() {
        return service_price;
    }

    public void setService_price(String service_price) {
        this.service_price = service_price;
    }

    public ServiceList(String serviceID, String servicepriceID, String service_name, String service_price) {

        this.serviceID = serviceID;
        this.servicepriceID = servicepriceID;
        this.service_name = service_name;
        this.service_price = service_price;
    }
    public ServiceList(){}
}
