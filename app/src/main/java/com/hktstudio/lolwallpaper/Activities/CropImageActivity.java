package com.hktstudio.lolwallpaper.Activities;

import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.hktstudio.lolwallpaper.R;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;

import static com.bumptech.glide.request.target.Target.SIZE_ORIGINAL;

/**
 * Created by HOANG on 3/7/2018.
 */

public class CropImageActivity extends AppCompatActivity {
    Toolbar toolbar;
    CropImageView cropImageView;
    FloatingActionButton button;
    ProgressBar progressBar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_image);
        progressBar = findViewById(R.id.progressBar);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        cropImageView = findViewById(R.id.cropImageView);
        Glide.with(this)
                .load("http://kiuerty.com/LeagueHD2//upload/" + getIntent().getStringExtra("image"))
                .asBitmap()
                .placeholder(R.drawable.ic_thumbnail)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .listener(new RequestListener<String, Bitmap>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(new SimpleTarget<Bitmap>(SIZE_ORIGINAL, SIZE_ORIGINAL) {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                        cropImageView.setImageBitmap(resource);
                    }
                });
        button = findViewById(R.id.bt_setAsWallpaper);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setBackground(cropImageView.getCroppedImage());
            }
        });

    }
    void setBackground(Bitmap bitmap){
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(getBaseContext());
        try {
            wallpaperManager.setBitmap(bitmap);
            Toast.makeText(getBaseContext()," Change Successful !",Toast.LENGTH_SHORT).show();
        } catch (IOException ex) {

        } catch (NullPointerException e){

        }
    }
}
