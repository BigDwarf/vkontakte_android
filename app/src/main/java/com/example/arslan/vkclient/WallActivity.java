package com.example.arslan.vkclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class WallActivity extends AppCompatActivity {
    private SwipeRefreshLayout swipeContainer;
    private ArrayList<WallPost> mPosts;
    private ListView mListView;
    private WallListAdapter mAdapter;
    private long lastRenewTime;
    private boolean mLoadingMore = false;
    private static final String START_FROM = "start_from";
    private static final String START_TIME = "start_time";
    private static final String END_TIME = "end_time";
    private static final String NEWSFEED_GET = "newsfeed.get";
    public static final String WALLPOST = "wallpost";

    private static int sBeginWall = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wall);
        mPosts = new ArrayList<>();
        mListView = (ListView)findViewById(R.id.wallPosts);
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int lastInScreen = firstVisibleItem + visibleItemCount;
                if ((lastInScreen == totalItemCount) && !(mLoadingMore) && mAdapter != null) {
                    Toast.makeText(getApplicationContext(), "END REACHED", Toast.LENGTH_LONG).show();
                    mLoadingMore = true;
                    final VKRequest requestNews = new VKRequest(NEWSFEED_GET, VKParameters.from("filters","post",VKApiConst.COUNT,"10", START_FROM, sBeginWall));
                    requestNews.start();
                    requestNews.executeWithListener(new VKRequest.VKRequestListener() {
                        @Override
                        public void onComplete(VKResponse response) {
                            lastRenewTime = System.currentTimeMillis();
                            try {
                                parseWall(response.responseString, false);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            mAdapter.notifyDataSetChanged();
                            mLoadingMore = false;
                            super.onComplete(response);
                        }
                    });
                }
            }
        });
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(WallActivity.this, WallPostActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(WALLPOST,mPosts.get(position));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                final VKRequest refreshNews = new VKRequest(NEWSFEED_GET, VKParameters.from("filters","post",VKApiConst.COUNT,"10", START_TIME, System.currentTimeMillis(),END_TIME, lastRenewTime));
                refreshNews.start();
                refreshNews.executeWithListener(new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(VKResponse response) {
                        lastRenewTime = System.currentTimeMillis();
                        try {
                            parseWall(response.responseString, true);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        mAdapter.notifyDataSetChanged();
                        swipeContainer.setRefreshing(false);
                        super.onComplete(response);
                    }
                });
            }
        });
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        final VKRequest requestNews = new VKRequest(NEWSFEED_GET, VKParameters.from("filters","post",VKApiConst.COUNT,"10", START_FROM, sBeginWall));
        requestNews.setPreferredLang("ru");
        requestNews.start();
        requestNews.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                lastRenewTime = System.currentTimeMillis();
                try {
                    parseWall(response.responseString, false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mAdapter = new WallListAdapter(WallActivity.this, mPosts);
                mListView.setAdapter(mAdapter);
                super.onComplete(response);
            }
        });
    }
    private void parseWall(String json, boolean renew) throws JSONException{
        int position = 0; //TODO кастыль
        JSONObject wall = new JSONObject(json);
        JSONObject response = wall.getJSONObject("response");
        String next_from = response.getString("next_from");
        sBeginWall = Integer.parseInt(next_from.substring(0,next_from.indexOf('/')));
        JSONArray wallPosts = response.getJSONArray("items");
        JSONArray groups =   response.getJSONArray("groups");
        JSONArray profiles = response.getJSONArray("profiles");
        for(int i = 0; i <wallPosts.length(); i++) {
            JSONObject arrayOfSource = wallPosts.getJSONObject(i);
            JSONObject likes = arrayOfSource.getJSONObject("likes");
            JSONArray attachments = null;
            if (arrayOfSource.has("attachments")) {
                attachments = arrayOfSource.getJSONArray("attachments");
            }
            ArrayList<String> attachList = new ArrayList<>();
            if (attachments != null) {
                for (int j = 0; j < attachments.length(); j++) {
                    JSONObject attachment = attachments.getJSONObject(j);
                    if (attachment.has("photo")) {
                        JSONObject photo = attachment.getJSONObject("photo");
                        String URL = photo.getString("photo_604");
                        attachList.add(URL);
                    }
                }
            }
            String likesCount = likes.getString("count");
            String idFrom = arrayOfSource.getString("source_id");
            String text = arrayOfSource.getString("text");
            WallPost post = new WallPost();
            post.setText(text);
            post.setLikesCount(Integer.parseInt(likesCount));
            post.setAttachments(attachList);

            boolean isProfile = idFrom.charAt(0) != '-';
            if (isProfile) {
                for (int j = 0; j < profiles.length(); j++) {
                    JSONObject profile = profiles.getJSONObject(j);
                    if (idFrom.equals(profile.getString("id"))) {
                        post.setFrom(profile.getString("first_name") + " " + profile.getString("last_name"));
                        post.setAvatarURL(profile.getString("photo_100"));
                    }
                }
            } else {
                for (int j = 0; j < groups.length(); j++) {
                    JSONObject group = groups.getJSONObject(j);
                    if (idFrom.substring(1, idFrom.length()).equals(group.getString("id"))) {
                        post.setFrom(group.getString("name"));
                        post.setAvatarURL(group.getString("photo_100"));
                    }
                }
            }

            if(renew) {
                mPosts.add(position, post);
                position++;
            }
            else {
                mPosts.add(post);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_logout:
                VKSdk.logout();
                Intent logoutIntent = new Intent(WallActivity.this,LoginActivity.class);
                startActivity(logoutIntent);
                finish();
                break;
        }
       return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }
}
