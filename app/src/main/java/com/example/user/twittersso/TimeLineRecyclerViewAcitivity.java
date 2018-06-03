package com.example.user.twittersso;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.internal.persistence.PreferenceStore;
import com.twitter.sdk.android.core.internal.persistence.PreferenceStoreStrategy;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.StatusesService;
import com.twitter.sdk.android.tweetcomposer.ComposerActivity;
import com.twitter.sdk.android.tweetui.FixedTweetTimeline;
import com.twitter.sdk.android.tweetui.SearchTimeline;
import com.twitter.sdk.android.tweetui.TweetTimelineListAdapter;
import com.twitter.sdk.android.tweetui.TweetTimelineRecyclerViewAdapter;
import com.twitter.sdk.android.tweetui.TwitterListTimeline;
import com.twitter.sdk.android.tweetui.UserTimeline;

import org.w3c.dom.Text;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

public class TimeLineRecyclerViewAcitivity extends ListActivity {
    Button makeTweet;
    SwipeRefreshLayout refreshLayout;
    SharedPreferences sPref;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tweetui_timeline_recyclerview);
        String userName = getIntent().getStringExtra("user_name");
        Toast.makeText(TimeLineRecyclerViewAcitivity.this,String.format("Добро пожаловать, %s!", userName), Toast.LENGTH_LONG ).show();
        makeTweet = findViewById(R.id.button);
        refreshLayout = findViewById(R.id.activity_main_swipe_refresh_layout);
        View.OnClickListener oclBtn = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final TwitterSession session = TwitterCore.getInstance().getSessionManager()
                        .getActiveSession();
                final Intent intent = new ComposerActivity.Builder(TimeLineRecyclerViewAcitivity.this)
                        .session(session)
                        .createIntent();
                startActivity(intent);
            }
        };
        makeTweet.setOnClickListener(oclBtn);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                    HomeTimeLineTweets();
            }
        });
        HomeTimeLineTweets();

    }

    private void HomeTimeLineTweets(){
        TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
        StatusesService statusesService = twitterApiClient.getStatusesService();
        Call<List<Tweet>> call = statusesService.homeTimeline(100, null,null, null, null, null, null );

        call.enqueue(new Callback<List<Tweet>>() {
            @Override
            public void success(Result<List<Tweet>> result) {
                final FixedTweetTimeline fixedTweets = new FixedTweetTimeline.Builder()
                        .setTweets(result.data)
                        .build();
                final TweetTimelineListAdapter adapter1 = new TweetTimelineListAdapter.Builder(TimeLineRecyclerViewAcitivity.this)
                        .setTimeline(fixedTweets)
                        .build();
                setListAdapter(adapter1);
                Log.w("GetTweets","Успех");
                refreshLayout.setRefreshing(false);
            }

            @Override
            public void failure(TwitterException exception) {
                Log.w("GetTweets","Ошибка");
                Toast.makeText(TimeLineRecyclerViewAcitivity.this,"Что-то пошло не так", Toast.LENGTH_LONG ).show();
                refreshLayout.setRefreshing(false);
            }
        });
    }

    /*private void SaveSession(){
        PreferenceStore store = new PreferenceStore() {
            @Override
            public SharedPreferences get() {
                return null;
            }

            @Override
            public SharedPreferences.Editor edit() {
                return null;
            }

            @Override
            public boolean save(SharedPreferences.Editor editor) {
                return false;
            }
        }
        PreferenceStoreStrategy ses = new PreferenceStoreStrategy() ;

        Context context = getApplicationContext();
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(context.getFilesDir().getPath().toString() +"session.dat" )))
        {
            oos.writeObject( TwitterCore.getInstance().getSessionManager().getActiveSession());
            Log.v("Save", "Save complete");
        }
        catch(Exception ex){

            Log.w("Save", "Save Failed \n"+ex.getMessage());
        }
        sPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        //ed.putString()
        ed.commit();
        Toast.makeText(this, "Text saved", Toast.LENGTH_SHORT).show();
    }*/
}
