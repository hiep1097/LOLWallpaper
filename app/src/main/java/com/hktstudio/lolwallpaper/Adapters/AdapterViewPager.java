package com.hktstudio.lolwallpaper.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.hktstudio.lolwallpaper.R;

import java.util.ArrayList;

import static com.bumptech.glide.load.DecodeFormat.PREFER_ARGB_8888;

/**
 * Created by HOANG on 3/6/2018.
 */

public class AdapterViewPager extends PagerAdapter {
    private Context context;
    private ArrayList<String> listUrl;
    public AdapterViewPager(Context context, ArrayList<String> listUrl) {
        this.context = context;
        this.listUrl = listUrl;
    }

    @Override
    public int getCount() {
        return listUrl.size();
    }

    public String getItemAt(int i){
        return listUrl.get(i);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.layout_item_slide_image,container,false);
        ImageView image_slide = itemView.findViewById(R.id.image_slide);
        final ProgressBar progressBar = itemView.findViewById(R.id.progressBar);
        Glide.with(context)
                .load("http://kiuerty.com/LeagueHD2//upload/" + listUrl.get(position))
                .asBitmap()
                .placeholder(R.drawable.ic_thumbnail)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .format(PREFER_ARGB_8888)
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
                .into(image_slide);
        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
