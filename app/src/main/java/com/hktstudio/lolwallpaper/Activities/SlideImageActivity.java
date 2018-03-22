package com.hktstudio.lolwallpaper.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.hktstudio.lolwallpaper.Adapters.AdapterViewPager;
import com.hktstudio.lolwallpaper.Entities.ImageFavoriteEntity;
import com.hktstudio.lolwallpaper.Entities.ImageSlideEntity;
import com.hktstudio.lolwallpaper.Event.MessageEvent;
import com.hktstudio.lolwallpaper.Json.JsonUtils;
import com.hktstudio.lolwallpaper.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import static com.bumptech.glide.request.target.Target.SIZE_ORIGINAL;

/**
 * Created by HOANG on 3/4/2018.
 */

public class SlideImageActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {
    Toolbar toolbar;
    MenuItem itemFavorite;
    EventBus eventBus;
    ViewPager viewPager;
    AdapterViewPager adapterViewPager;
    int position;
    ArrayList<String> listUrl;
    Menu menu;
    ProgressBar progressBar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slide_image);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        toolbar = findViewById(R.id.toolbar);
        listUrl = getIntent().getStringArrayListExtra("allImageUrl");
        viewPager = findViewById(R.id.viewPager);
        adapterViewPager = new AdapterViewPager(this, listUrl);
        viewPager.setAdapter(adapterViewPager);
        position = getIntent().getIntExtra("position", 0);
        viewPager.setCurrentItem(position);
        viewPager.setOnPageChangeListener(this);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        eventBus = EventBus.getDefault();
        check(position);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
    }

    @Override
    public void onStart() {
        super.onStart();
        eventBus.register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        eventBus.unregister(this);
        eventBus.post(new MessageEvent("FAVORITE_UPDATE"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_gallery, menu);
        itemFavorite = menu.findItem(R.id.it_favorite);
        setFavorite(new ImageFavoriteEntity(adapterViewPager.getItemAt(position)));
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.it_favorite:
                ImageFavoriteEntity e = new ImageFavoriteEntity(adapterViewPager.getItemAt(viewPager.getCurrentItem()));
                List<ImageFavoriteEntity> t = ImageFavoriteEntity
                        .find(ImageFavoriteEntity.class, "image = ?", e.getImage());
                if (t != null && t.size() > 0) {
                    ImageFavoriteEntity e2 = t.get(0);
                    e2.delete();
                    setFavorite(t.get(0));
                    Toast.makeText(this, "Removed from favorite", Toast.LENGTH_SHORT).show();
                } else {
                    e.save();
                    setFavorite(e);
                    Toast.makeText(this, "Added to favorite", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.it_setAsWallpaper:
                Intent intent = new Intent(this,CropImageActivity.class);
                intent.putExtra("image",adapterViewPager.getItemAt(viewPager.getCurrentItem()));
                startActivity(intent);
                break;
            case R.id.it_share:
                shareImage();
                break;
            case R.id.it_save:
                saveImage();
                break;
            case R.id.it_zoom:
                Intent intent1 = new Intent(this,ZoomImageActivity.class);
                intent1.putExtra("image",adapterViewPager.getItemAt(viewPager.getCurrentItem()));
                startActivity(intent1);
                break;
        }
        menu.close();
        return super.onOptionsItemSelected(item);
    }

    protected void setFavorite(ImageFavoriteEntity e) {
        try {
            if (e != null) {
                List<ImageFavoriteEntity> t = ImageFavoriteEntity
                        .find(ImageFavoriteEntity.class, "image = ?", e.getImage());
                if (t != null && t.size() > 0) {
                    itemFavorite.setIcon(R.drawable.ic_star_white);
                } else {
                    itemFavorite.setIcon(R.drawable.ic_star_outline);
                }
            }
        } catch (NullPointerException ex) {

        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        ImageFavoriteEntity e = new ImageFavoriteEntity(adapterViewPager.getItemAt(position));
        check(position);
        setFavorite(e);
    }

    public void check(int position){
        if (JsonUtils.checkInternet(this)) {
            List<ImageSlideEntity> t = ImageSlideEntity
                    .find(ImageSlideEntity.class, "image = ?", listUrl.get(position));
            if (t.size() == 0) {
                new ImageSlideEntity(listUrl.get(position)).save();
            }
            if (position-1>=0){
                t = ImageSlideEntity
                        .find(ImageSlideEntity.class, "image = ?", listUrl.get(position-1));
                if (t.size() == 0) {
                    new ImageSlideEntity(listUrl.get(position-1)).save();
                }
            }
            if (position+1<listUrl.size()){
                t = ImageSlideEntity
                        .find(ImageSlideEntity.class, "image = ?", listUrl.get(position+1));
                if (t.size() == 0) {
                    new ImageSlideEntity(listUrl.get(position+1)).save();
                }
            }

        } else {
            List<ImageSlideEntity> t = ImageSlideEntity
                    .find(ImageSlideEntity.class, "image = ?", listUrl.get(position));
            if (t.size() == 0) {
                Toast.makeText(this,"First time loading from internet!",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public void saveImage() {
        progressBar.setVisibility(View.VISIBLE);
        Glide.with(SlideImageActivity.this)
                .load("http://kiuerty.com/LeagueHD2//upload/" + adapterViewPager.getItemAt(viewPager.getCurrentItem()))
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.ic_thumbnail)
                .into(new SimpleTarget<Bitmap>(SIZE_ORIGINAL, SIZE_ORIGINAL) {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                        progressBar.setVisibility(View.GONE);
                        String savedImagePath = null;
                        String imageFileName = "image_" + adapterViewPager.getItemAt(viewPager.getCurrentItem());
                        File storageDir = new File(Environment
                                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                                + "/lolwallpaper");
                        boolean success = true;
                        if (!storageDir.exists()) {
                            success = storageDir.mkdirs();
                        }
                        if (success) {
                            File imageFile = new File(storageDir, imageFileName);
                            savedImagePath = imageFile.getAbsolutePath();
                            try {
                                OutputStream fOut = new FileOutputStream(imageFile);
                                resource.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                                fOut.flush();
                                fOut.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            // Add the image to the system gallery
                            galleryAddPic(savedImagePath);
                            Toast.makeText(SlideImageActivity.this, "Image saved", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void galleryAddPic(String imagePath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(imagePath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        sendBroadcast(mediaScanIntent);
    }

    public void shareImage() {
        progressBar.setVisibility(View.VISIBLE);
        Glide.with(SlideImageActivity.this)
                .load("http://kiuerty.com/LeagueHD2//upload/" + adapterViewPager.getItemAt(viewPager.getCurrentItem()))
                .asBitmap()
                .placeholder(R.drawable.ic_thumbnail)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(new SimpleTarget<Bitmap>(SIZE_ORIGINAL, SIZE_ORIGINAL) {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                        progressBar.setVisibility(View.GONE);
                        try {
                            File cachePath = new File(getCacheDir(), "images");
                            cachePath.mkdirs();
                            FileOutputStream fOut = new FileOutputStream(cachePath + "/image.jpeg");// overwrites this image every time
                            resource.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                            fOut.close();

                            File imagePath = new File(getBaseContext().getCacheDir(), "images");
                            File newFile = new File(imagePath, "image.jpeg");
                            Uri contentUri = FileProvider
                                    .getUriForFile(getBaseContext(), "com.hktstudio.lolwallpaper.fileprovider", newFile);
                            if (contentUri != null) {
                                Intent shareIntent = new Intent();
                                shareIntent.setAction(Intent.ACTION_SEND);
                                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // temp permission for receiving app to read this file
                                shareIntent.setDataAndType(contentUri, getContentResolver().getType(contentUri));
                                shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
                                startActivity(Intent.createChooser(shareIntent, "Choose an app"));
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                });
    }
}
