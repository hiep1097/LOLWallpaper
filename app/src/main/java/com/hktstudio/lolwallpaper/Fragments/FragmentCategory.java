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

import com.hktstudio.lolwallpaper.Activities.ImagesTuongActivity;
import com.hktstudio.lolwallpaper.Adapters.AdapterCategory;
import com.hktstudio.lolwallpaper.Entities.ImageCategoryEntity;
import com.hktstudio.lolwallpaper.Event.MessageEvent;
import com.hktstudio.lolwallpaper.Interfaces.ItemOnClick;
import com.hktstudio.lolwallpaper.Json.JsonUtils;
import com.hktstudio.lolwallpaper.Models.ItemCategory;
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

public class FragmentCategory extends Fragment implements ItemOnClick{
    RecyclerView rcv_category;
    AdapterCategory adapterCategory;
    List<ItemCategory> list = new ArrayList<>();
    List<ImageCategoryEntity> listCategory;
    ProgressBar progressBar;
    int count[] = new int[10001];
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_fragment_category, container, false);
        progressBar = view.findViewById(R.id.progressBar);
        rcv_category = view.findViewById(R.id.rcv_category);
        rcv_category.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        rcv_category.addItemDecoration(new MyItemDecoration(getContext(), R.dimen.item_offset));
        if (JsonUtils.checkInternet(getContext())) {
            new CategoryAsyncTask().execute();
        } else {
            progressBar.setVisibility(View.GONE);
            list = new ArrayList<>();
            listCategory = ImageCategoryEntity.listAll(ImageCategoryEntity.class);
            for (ImageCategoryEntity e : listCategory) {
                list.add(new ItemCategory(e.getCid(), e.getCategory_name(), e.getCategory_image()));
            }
            updateList("");
            if (listCategory.size() == 0) {
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
        count[pos]++;
        if (count[pos] == 5) {
            EventBus.getDefault().post(new MessageEvent("LOAD_ADMOB"));
            count[pos] = 0;
        } else {
            Intent intent = new Intent(getContext(), ImagesTuongActivity.class);
            intent.putExtra("cid",list.get(pos).getCid());
            intent.putExtra("name",list.get(pos).getCategory_name());
            startActivity(intent);
        }
    }

    @Override
    public void clickItem(int pos, ArrayList<String> listUrl) {

    }

    public class CategoryAsyncTask extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            list = new ArrayList<>();
        }

        @Override
        protected String doInBackground(String... strings) {
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url("http://kiuerty.com/LeagueHD2//api.php")
                    .get()
                    .addHeader("cache-control", "no-cache")
                    .addHeader("postman-token", "842b0a7a-b5ef-17bd-252a-744e68cd9044")
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    listCategory = ImageCategoryEntity.listAll(ImageCategoryEntity.class);
                    for (ImageCategoryEntity entity : listCategory) {
                        list.add(new ItemCategory(entity.getCid(), entity.getCategory_name(), entity.getCategory_image()));
                    }
                    updateList("");
                    hideProgress();
                }

                @Override
                public void onResponse(Call call, Response response) {
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

    public void addImageCategoryEntity(ImageCategoryEntity e) {
        List<ImageCategoryEntity> t = ImageCategoryEntity
                .find(ImageCategoryEntity.class, "cid = ?", e.getCid());
        if (t.size() == 0) {
            e.save();
        }
    }

    void updateList(String s) {
        if (s.compareTo("")!=0)
        try {
            JSONArray jsonArray = new JSONObject(s).getJSONArray("MaterialWallpaper");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                ItemCategory itemCategory
                        = new ItemCategory(jsonObject.getString("cid")
                        , jsonObject.getString("category_name")
                        , jsonObject.getString("category_image"));
                list.add(itemCategory);

                ImageCategoryEntity e = new ImageCategoryEntity(itemCategory.getCid()
                        , itemCategory.getCategory_name(), itemCategory.getCategory_image());
                addImageCategoryEntity(e);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        adapterCategory = new AdapterCategory(getActivity(), list,this);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                rcv_category.setAdapter(adapterCategory);
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
