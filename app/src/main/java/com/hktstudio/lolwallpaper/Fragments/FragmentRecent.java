package com.hktstudio.lolwallpaper.Fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.hktstudio.lolwallpaper.Activities.SlideImageActivity;
import com.hktstudio.lolwallpaper.Adapters.AdapterRecent;
import com.hktstudio.lolwallpaper.Entities.ImageRecentEntity;
import com.hktstudio.lolwallpaper.Event.MessageEvent;
import com.hktstudio.lolwallpaper.Interfaces.ItemOnClick;
import com.hktstudio.lolwallpaper.Json.JsonUtils;
import com.hktstudio.lolwallpaper.Models.ItemRecent;
import com.hktstudio.lolwallpaper.MyItemDecoration;
import com.hktstudio.lolwallpaper.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by HOANG on 2/27/2018.
 */

public class FragmentRecent extends Fragment implements ItemOnClick{
    RecyclerView rcv_recent;
    AdapterRecent adapterRecent;
    List<ItemRecent> list = new ArrayList<>();
    List<ImageRecentEntity> listRecent;
    ProgressBar progressBar;
    int count[] = new int[10001];
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_fragment_recent, container, false);
        progressBar = view.findViewById(R.id.progressBar);
        rcv_recent = view.findViewById(R.id.rcv_recent);
        rcv_recent.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        rcv_recent.addItemDecoration(new MyItemDecoration(getContext(), R.dimen.item_offset));
        if (JsonUtils.checkInternet(getContext())) {
            new RecentAsyncTask().execute();
        } else {
            progressBar.setVisibility(View.GONE);
            list = new ArrayList<>();
            listRecent = ImageRecentEntity.listAll(ImageRecentEntity.class);
            for (ImageRecentEntity e : listRecent) {
                list.add(new ItemRecent(e.getImage()));
            }
            updateList("");
            if (listRecent.size() == 0) {
                Toast.makeText(getContext(), "First time loading from internet!", Toast.LENGTH_SHORT).show();
            }
        }
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

    @Subscribe
    public void onMessageEvent(MessageEvent event) {
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

    public class RecentAsyncTask extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            list = new ArrayList<>();
        }

        @Override
        protected String doInBackground(String... strings) {
            final OkHttpClient client = new OkHttpClient();

            final Request request = new Request.Builder()
                    .url("http://kiuerty.com/LeagueHD2//api.php?latest=102")
                    .get()
                    .addHeader("cache-control", "no-cache")
                    .addHeader("postman-token", "2ca0a746-268e-0107-a245-47ead6975635")
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    listRecent = ImageRecentEntity.listAll(ImageRecentEntity.class);
                    for (ImageRecentEntity entity : listRecent) {
                        list.add(new ItemRecent(entity.getImage()));
                    }
                    updateList("");
                    hideProgress();
                }

                @Override
                public void onResponse(Call call, Response response){
                    String json = "";
                    try {
                        json = response.body().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (NullPointerException e){
                    }
                    updateList(json);
                    hideProgress();
                }
            });

            return null;
        }
    }

    public void addImageRecentEntity(ImageRecentEntity e) {
        List<ImageRecentEntity> t = ImageRecentEntity
                .find(ImageRecentEntity.class, "image = ?", e.getImage());
        if (t.size() == 0) {
            e.save();
        }
    }

    void updateList(String s) {
        if (s.compareTo("") != 0)
            try {
                JSONArray jsonArray = null;
                jsonArray = new JSONObject(s).getJSONArray("MaterialWallpaper");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    ItemRecent itemRecent
                            = new ItemRecent(jsonObject.getString("image"));
                    list.add(itemRecent);
                    ImageRecentEntity e = new ImageRecentEntity(itemRecent.getImage());
                    addImageRecentEntity(e);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        adapterRecent = new AdapterRecent(getActivity(), list,this);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                rcv_recent.setAdapter(adapterRecent);
            }
        });
    }

    void hideProgress() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}
