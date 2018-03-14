package com.example.hoang.lolwallpaper.Entities;

import com.orm.SugarRecord;

/**
 * Created by HOANG on 3/10/2018.
 */

public class ImageRecentEntity extends SugarRecord<ImageRecentEntity> {
    String image;
    public ImageRecentEntity() {
    }

    public ImageRecentEntity(String image) {
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
