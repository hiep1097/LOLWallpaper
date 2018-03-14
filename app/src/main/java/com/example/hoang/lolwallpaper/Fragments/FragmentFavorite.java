package com.example.hoang.lolwallpaper.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hoang.lolwallpaper.Activities.SlideImageActivity;
import com.example.hoang.lolwallpaper.Adapters.AdapterRecent;
import com.example.hoang.lolwallpaper.Entities.ImageFavoriteEntity;
import com.example.hoang.lolwallpaper.Event.MessageEvent;
import com.example.hoang.lolwallpaper.Interfaces.ItemOnClick;
import com.example.hoang.lolwallpaper.Models.ItemRecent;
import com.example.hoang.lolwallpaper.MyItemDecoration;
import com.example.hoang.lolwallpaper.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HOANG on 2/27/2018.
 */

public class FragmentFavorite extends Fragment implements ItemOnClick{
    RecyclerView rcv_favorite;
    List<ItemRecent> list;
    List<ImageFavoriteEntity> listFav;
    AdapterRecent adapterRecent;
    View view;
    int count[] = new int[10001];
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_fragment_favorite,container,false);
        init();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    public void init(){
        list = new ArrayList<>();
        listFav = ImageFavoriteEntity.listAll(ImageFavoriteEntity.class);
        for (ImageFavoriteEntity e: listFav){
            list.add(new ItemRecent(e.getImage()));
        }
        Log.d("size",list.size()+"");
        rcv_favorite = view.findViewById(R.id.rcv_favorite);
        adapterRecent = new AdapterRecent(getActivity(),list,this);
        rcv_favorite.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        rcv_favorite.addItemDecoration(new MyItemDecoration(getContext(), R.dimen.item_offset));
        rcv_favorite.setAdapter(adapterRecent);
    }

    @Subscribe
    public void onMessageEvent(MessageEvent event) {
        if(event.getMessage().equals("FAVORITE_UPDATE")) {
            init();
        }
    }

    @Override
    public void clickItem(int pos) {

    }

    @Override
    public void clickItem(int pos,ArrayList<String> listUrl) {
        count[pos]++;
        if (count[pos] == 3) {
            EventBus.getDefault().post(new MessageEvent("LOAD_ADMOB"));
            count[pos] = 0;
        } else {
            Intent intent = new Intent(getContext(), SlideImageActivity.class);
            intent.putExtra("position",pos);
            intent.putExtra("image",list.get(pos).getImage());
            intent.putStringArrayListExtra("allImageUrl",listUrl);
            startActivity(intent);
        }
    }
}
