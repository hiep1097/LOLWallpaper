package com.example.hoang.lolwallpaper.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.hoang.lolwallpaper.Activities.SlideImageActivity;
import com.example.hoang.lolwallpaper.Interfaces.ItemOnClick;
import com.example.hoang.lolwallpaper.Models.ItemRecent;
import com.example.hoang.lolwallpaper.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HOANG on 3/3/2018.
 */

public class AdapterRecent extends RecyclerView.Adapter<AdapterRecent.RecyclerViewHolder>{
    Context context;
    List<ItemRecent> list;
    private LayoutInflater mInflater;
    private ArrayList<String> listUrl;
    private ItemOnClick itemOnClick;
    public AdapterRecent(Context context, List<ItemRecent> list, ItemOnClick itemOnClick) {
        this.context = context;
        this.list = list;
        this.itemOnClick = itemOnClick;
        mInflater = LayoutInflater.from(context);
        listUrl = new ArrayList<>();
        for (int i=0;i<list.size();i++) listUrl.add(list.get(i).getImage());
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.layout_item_recent,parent,false);
        return new RecyclerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, final int position) {
        Glide.with(context)
                .load("http://kiuerty.com/LeagueHD2//upload/thumbs/"+list.get(position).getImage())
                .crossFade()
                .placeholder(R.drawable.ic_thumbnail)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.image_recent);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemOnClick.clickItem(position,listUrl);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        ImageView image_recent;
        public RecyclerViewHolder(View itemView) {
            super(itemView);
            image_recent = itemView.findViewById(R.id.image_recent);
            int mWidth = (getWidthScreen(context)-24)/3;
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(mWidth,mWidth);
            image_recent.setLayoutParams(layoutParams);
        }
    }
    public static int getWidthScreen(Context context){
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int mWidthScreen = display.getWidth();
        return mWidthScreen;
    }
}
