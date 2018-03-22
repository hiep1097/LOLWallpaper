package com.hktstudio.lolwallpaper.Entities;

import com.orm.SugarRecord;

/**
 * Created by HOANG on 3/5/2018.
 */

public class ImageFavoriteEntity extends SugarRecord<ImageFavoriteEntity> {
    String image;

    public ImageFavoriteEntity() {
    }

    public ImageFavoriteEntity(String image) {
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
