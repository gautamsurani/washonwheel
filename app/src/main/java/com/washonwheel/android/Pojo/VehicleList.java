package com.washonwheel.android.Pojo;

/**
 * Created by Welcome on 25-01-2018.
 */

public class VehicleList {
    String vehID, car_no, veh_state_code, veh_city_code, veh_series, veh_no;

    public String getVehID() {
        return vehID;
    }

    public void setVehID(String vehID) {
        this.vehID = vehID;
    }

    public String getCar_no() {
        return car_no;
    }

    public void setCar_no(String car_no) {
        this.car_no = car_no;
    }

    public String getVeh_state_code() {
        return veh_state_code;
    }

    public void setVeh_state_code(String veh_state_code) {
        this.veh_state_code = veh_state_code;
    }

    public String getVeh_city_code() {
        return veh_city_code;
    }

    public void setVeh_city_code(String veh_city_code) {
        this.veh_city_code = veh_city_code;
    }

    public String getVeh_series() {
        return veh_series;
    }

    public void setVeh_series(String veh_series) {
        this.veh_series = veh_series;
    }

    public String getVeh_no() {
        return veh_no;
    }

    public void setVeh_no(String veh_no) {
        this.veh_no = veh_no;
    }

    public VehicleList() {

    }

    public VehicleList(String vehID, String car_no, String veh_state_code, String veh_city_code, String veh_series, String veh_no) {

        this.vehID = vehID;
        this.car_no = car_no;
        this.veh_state_code = veh_state_code;
        this.veh_city_code = veh_city_code;
        this.veh_series = veh_series;
        this.veh_no = veh_no;
    }
}
