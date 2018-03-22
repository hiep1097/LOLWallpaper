package com.hktstudio.lolwallpaper.Entities;

import com.orm.SugarRecord;

/**
 * Created by HOANG on 3/10/2018.
 */

public class ImageCategoryEntity extends SugarRecord<ImageCategoryEntity> {
    private String cid;
    private String category_name;
    private String category_image;

    public ImageCategoryEntity(){

    }

    public ImageCategoryEntity(String cid, String category_name, String category_image) {
        this.cid = cid;
        this.category_name = category_name;
        this.category_image = category_image;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getCategory_image() {
        return category_image;
    }

    public void setCategory_image(String category_image) {
        this.category_image = category_image;
    }
}
