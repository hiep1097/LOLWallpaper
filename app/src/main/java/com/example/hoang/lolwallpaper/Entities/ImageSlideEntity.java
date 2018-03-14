package com.example.hoang.lolwallpaper.Entities;

import com.orm.SugarRecord;

/**
 * Created by HOANG on 3/10/2018.
 */

public class ImageSlideEntity extends SugarRecord<ImageSlideEntity> {
    String image;
    public ImageSlideEntity() {
    }

    public ImageSlideEntity(String image) {
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
