package com.washonwheel.android.Pojo;

/**
 * Created by welcome on 12-12-2017.
 */

public class ServiceHistoryModel {
    String leadID,leadno,service,car,payment,total,status;

    public String getLeadID() {
        return leadID;
    }

    public void setLeadID(String leadID) {
        this.leadID = leadID;
    }

    public String getLeadno() {
        return leadno;
    }

    public void setLeadno(String leadno) {
        this.leadno = leadno;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getCar() {
        return car;
    }

    public void setCar(String car) {
        this.car = car;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ServiceHistoryModel(String leadID, String leadno, String service, String car, String payment, String total, String status) {

        this.leadID = leadID;
        this.leadno = leadno;
        this.service = service;
        this.car = car;
        this.payment = payment;
        this.total = total;
        this.status = status;
    }
    public ServiceHistoryModel(){}
}
