package com.example.hoang.lolwallpaper.Entities;

import com.orm.SugarRecord;

/**
 * Created by HOANG on 3/10/2018.
 */

public class ImageTuongEntity extends SugarRecord<ImageTuongEntity> {
    String cid;
    String image;
    public ImageTuongEntity() {
    }

    public ImageTuongEntity(String cid, String image) {
        this.cid = cid;
        this.image = image;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
