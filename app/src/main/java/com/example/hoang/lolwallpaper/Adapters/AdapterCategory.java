package com.example.hoang.lolwallpaper.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.hoang.lolwallpaper.Activities.ImagesTuongActivity;
import com.example.hoang.lolwallpaper.Interfaces.ItemOnClick;
import com.example.hoang.lolwallpaper.Models.ItemCategory;
import com.example.hoang.lolwallpaper.Models.ItemRecent;
import com.example.hoang.lolwallpaper.R;

import java.util.List;

/**
 * Created by HOANG on 3/3/2018.
 */

public class AdapterCategory extends RecyclerView.Adapter<AdapterCategory.RecyclerViewHolder>{
    Context context;
    List<ItemCategory> list;
    private LayoutInflater mInflater;
    private ItemOnClick itemOnClick;

    public AdapterCategory(Context context, List<ItemCategory> list,ItemOnClick itemOnClick) {
        this.context = context;
        this.list = list;
        this.itemOnClick = itemOnClick;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.layout_item_category,parent,false);
        return new RecyclerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, final int position) {
        Glide.with(context)
                .load("http://kiuerty.com/LeagueHD2/upload/category/"+list.get(position).getCategory_image())
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.ic_thumbnail)
                .into(holder.image_category);
        holder.tv_name.setText(list.get(position).getCategory_name());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemOnClick.clickItem(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        ImageView image_category;
        TextView tv_name;
        public RecyclerViewHolder(View itemView) {
            super(itemView);
            image_category = itemView.findViewById(R.id.image_category);
            tv_name = itemView.findViewById(R.id.tv_name);
            int mWidth = (getWidthScreen(context)-18)/2;
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(mWidth,mWidth);
            image_category.setLayoutParams(layoutParams);
        }
    }
    public static int getWidthScreen(Context context){
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int mWidthScreen = display.getWidth();
        return mWidthScreen;
    }
}
