package com.olegdavidovichdev.cinematogo.model;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class ConfigurationResponse {

    @SerializedName("images")
    @Expose
    private Images images;
    @SerializedName("change_keys")
    @Expose
    private List<String> changeKeys = new ArrayList<String>();

    /**
     *
     * @return
     * The images
     */
    public Images getImages() {
        return images;
    }

    /**
     *
     * @param images
     * The images
     */
    public void setImages(Images images) {
        this.images = images;
    }

    /**
     *
     * @return
     * The changeKeys
     */
    public List<String> getChangeKeys() {
        return changeKeys;
    }

    /**
     *
     * @param changeKeys
     * The change_keys
     */
    public void setChangeKeys(List<String> changeKeys) {
        this.changeKeys = changeKeys;
    }

}