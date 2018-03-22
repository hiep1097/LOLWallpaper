package com.hktstudio.lolwallpaper.Activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.hktstudio.lolwallpaper.Adapters.AdapterRecent;
import com.hktstudio.lolwallpaper.Entities.ImageTuongEntity;
import com.hktstudio.lolwallpaper.Event.MessageEvent;
import com.hktstudio.lolwallpaper.Interfaces.ItemOnClick;
import com.hktstudio.lolwallpaper.Json.JsonUtils;
import com.hktstudio.lolwallpaper.Models.ItemRecent;
import com.hktstudio.lolwallpaper.MyItemDecoration;
import com.hktstudio.lolwallpaper.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

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
 * Created by HOANG on 3/3/2018.
 */

public class ImagesTuongActivity extends AppCompatActivity implements ItemOnClick{
    Toolbar toolbar;
    RecyclerView rcv_recent;
    AdapterRecent adapterRecent;
    List<ItemRecent> list;
    List<ImageTuongEntity> listRecent;
    String CID;
    ProgressBar progressBar;
    int count[] = new int[10001];
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images_tuong);
        progressBar = findViewById(R.id.progressBar);
        rcv_recent = findViewById(R.id.rcv_recent);
        rcv_recent.setLayoutManager(new GridLayoutManager(this, 3));
        rcv_recent.addItemDecoration(new MyItemDecoration(this, R.dimen.item_offset));
        setTitle(getIntent().getStringExtra("name"));
        CID = getIntent().getStringExtra("cid");
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        Log.d("CID", CID);
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_id));
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                loadInterstitialAd();
            }
        });
        //Load sẵn quảng cáo khi ứng dụng mở
        loadInterstitialAd();
        if (JsonUtils.checkInternet(this)) {
            new RecentAsyncTask().execute(CID);
        } else {
            progressBar.setVisibility(View.GONE);
            list = new ArrayList<>();
            listRecent = ImageTuongEntity
                    .find(ImageTuongEntity.class, "cid = ?", getIntent().getStringExtra("cid"));
            for (ImageTuongEntity e : listRecent) {
                list.add(new ItemRecent(e.getImage()));
            }
            updateList("");
            if (listRecent.size() == 0) {
                Toast.makeText(this, "First time loading from internet!", Toast.LENGTH_SHORT).show();
            }
        }
        mAdView = findViewById(R.id.adView);
        initAdmob();

    }

    void initAdmob(){
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

    }

    //Load InterstitialAd
    private void loadInterstitialAd() {
        if (mInterstitialAd != null) {
            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .build();

            mInterstitialAd.loadAd(adRequest);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        initAdmob();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        initAdmob();
        if (!mInterstitialAd.isLoaded())loadInterstitialAd();
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        } else initAdmob();
        if (!mInterstitialAd.isLoaded())loadInterstitialAd();
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
        Intent intent = new Intent(this, SlideImageActivity.class);
        intent.putExtra("position",pos);
        intent.putExtra("image",list.get(pos).getImage());
        intent.putStringArrayListExtra("allImageUrl",listUrl);
        startActivity(intent);
        if (count[pos] == 3) {
            if (mInterstitialAd.isLoaded()) mInterstitialAd.show();
            count[pos] = 0;
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
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url("http://kiuerty.com/LeagueHD2//api.php?cat_id=" + strings[0])
                    .get()
                    .addHeader("cache-control", "no-cache")
                    .addHeader("postman-token", "cf0f149f-f531-3db3-0c7e-3590632a24e0")
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    listRecent = ImageTuongEntity
                            .find(ImageTuongEntity.class, "cid = ?", getIntent().getStringExtra("cid"));
                    for (ImageTuongEntity entity : listRecent) {
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
                        updateList(json);
                        hideProgress();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            return null;
        }
    }

    public void addImageTuongEntity(ImageTuongEntity e) {
        List<ImageTuongEntity> t = ImageTuongEntity
                .find(ImageTuongEntity.class, "image = ?", e.getImage());
        if (t.size() == 0) {
            e.save();
        }
    }

    void updateList(String s) {
        if (s.compareTo("") != 0)
        try {
            JSONArray jsonArray = new JSONObject(s).getJSONArray("MaterialWallpaper");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                ItemRecent itemRecent
                        = new ItemRecent(jsonObject.getString("images"));
                list.add(itemRecent);

                ImageTuongEntity e = new ImageTuongEntity(CID, itemRecent.getImage());
                addImageTuongEntity(e);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        adapterRecent = new AdapterRecent(this, list,this);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                rcv_recent.setAdapter(adapterRecent);
            }
        });
    }

    void hideProgress() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}
