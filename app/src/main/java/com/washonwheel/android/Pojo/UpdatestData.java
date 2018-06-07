package com.washonwheel.android.Pojo;

import java.io.Serializable;

/**
 * Created by welcome on 04-08-2016.
 */
public class UpdatestData implements Serializable {

    String offer_ID;
    String name;
    String buttonID;
    String subcat;
    String title;
    String image;
    String message;
    String added_on;
    String product_button;
    String cat_button;
    String shre_msg;

    public UpdatestData(String offer_id, String name,
                        String buttonID, String subcat, String title,
                        String image, String message, String added_on,
                        String product_button, String cat_button, String shre_msg) {

        this.offer_ID = offer_id;
        this.name = name;
        this.buttonID = buttonID;
        this.subcat = subcat;
        this.title = title;
        this.image = image;
        this.message = message;
        this.added_on = added_on;
        this.product_button = product_button;
        this.cat_button = cat_button;
        this.shre_msg = shre_msg;
    }

    public UpdatestData() {
    }

    public String getOffer_ID() {
        return offer_ID;
    }

    public void setOffer_ID(String offer_ID) {
        this.offer_ID = offer_ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getButtonID() {
        return buttonID;
    }

    public void setButtonID(String buttonID) {
        this.buttonID = buttonID;
    }

    public String getSubcat() {
        return subcat;
    }

    public void setSubcat(String subcat) {
        this.subcat = subcat;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAdded_on() {
        return added_on;
    }

    public void setAdded_on(String added_on) {
        this.added_on = added_on;
    }

    public String getProduct_button() {
        return product_button;
    }

    public void setProduct_button(String product_button) {
        this.product_button = product_button;
    }

    public String getCat_button() {
        return cat_button;
    }

    public void setCat_button(String cat_button) {
        this.cat_button = cat_button;
    }

    public String getShre_msg() {
        return shre_msg;
    }

    public void setShre_msg(String shre_msg) {
        this.shre_msg = shre_msg;
    }
}

