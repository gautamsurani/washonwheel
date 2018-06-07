package com.washonwheel.android.Pojo;

/**
 * Created by welcome on 11-12-2017.
 */

public class CityList {
    String CityId,CityName;

    public CityList(){}

    public String getCityId() {
        return CityId;
    }

    public void setCityId(String cityId) {
        CityId = cityId;
    }

    public String getCityName() {
        return CityName;
    }

    public void setCityName(String cityName) {
        CityName = cityName;
    }

    public CityList(String cityId, String cityName) {

        CityId = cityId;
        CityName = cityName;
    }
}
