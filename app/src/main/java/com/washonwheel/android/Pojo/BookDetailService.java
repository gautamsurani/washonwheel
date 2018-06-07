package com.washonwheel.android.Pojo;

/**
 * Created by welcome on 25-12-2017.
 */

public class BookDetailService {
    String sr,service_name,service_price;

    public BookDetailService() {
    }

    public String getSr() {

        return sr;
    }

    public void setSr(String sr) {
        this.sr = sr;
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

    public BookDetailService(String sr, String service_name, String service_price) {

        this.sr = sr;
        this.service_name = service_name;
        this.service_price = service_price;
    }
}
