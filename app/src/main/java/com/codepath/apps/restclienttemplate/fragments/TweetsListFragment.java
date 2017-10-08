package com.codepath.apps.restclienttemplate.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.restclienttemplate.EndlessRecyclerViewScrollListener;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.activities.ComposeActivity;
import com.codepath.apps.restclienttemplate.adapters.TweetAdapter;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

/**
 * Created by kystatham on 10/3/17.
 */

public abstract class TweetsListFragment extends Fragment implements TweetAdapter.TweetAdapterListener{
    private final int REQUEST_CODE = 20;

    TweetAdapter tweetAdapter;
    ArrayList<Tweet> tweets;
    RecyclerView rvTweets;
    private EndlessRecyclerViewScrollListener scrollListener;

    public interface TweetSelectedListener {
        public void onTweetSelected(Tweet tweet);
        public void onProfileImageSelected(String screenName);
        public void onScreenNameSelected(String screenName);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragments_tweets_list, container, false);

        FloatingActionButton fabCompose = (FloatingActionButton) v.findViewById(R.id.fabCompose);
        fabCompose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchComposeActivity(v);
            }
        });

        rvTweets = (RecyclerView) v.findViewById(R.id.rvTweet);
        tweets = new ArrayList<>();
        tweetAdapter = new TweetAdapter(tweets, this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvTweets.setLayoutManager(linearLayoutManager);
        rvTweets.setAdapter(tweetAdapter);

        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                populateTimelineWithOlderTweets(getOldestTweetId());
            }
        };
        rvTweets.addOnScrollListener(scrollListener);

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            Tweet tweet = Parcels.unwrap(data.getParcelableExtra("tweet"));
            if (tweet != null) {
                tweets.add(0, tweet);
                tweetAdapter.notifyItemInserted(0);
                rvTweets.scrollToPosition(0);
            }
        }
    }

    public void addItems(JSONArray response) {
        for (int i = 0; i < response.length(); i++) {
            try {
                Tweet tweet = Tweet.fromJSON(response.getJSONObject(i));
                tweets.add(tweet);
                tweetAdapter.notifyItemInserted(tweets.size() - 1);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void launchComposeActivity(View view) {
        Intent intent = new Intent(view.getContext(), ComposeActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    public void onItemSelected(View view, int position) {
        Tweet tweet = tweets.get(position);
        ((TweetSelectedListener) getActivity()).onTweetSelected(tweet);
    }

    @Override
    public void onProfileImageSelected(String screenName) {
        ((TweetSelectedListener) getActivity()).onProfileImageSelected(screenName);
    }

    @Override
    public void onScreenNameSelected(String screenName) {
        ((TweetSelectedListener) getActivity()).onScreenNameSelected(screenName);
    }

    public abstract void populateTimelineWithOlderTweets(Long tweetId);

    private Long getOldestTweetId() {
        if (tweets.size() == 0) {
            return 1L;
        } else {
            Tweet tweet = tweets.get(tweets.size() - 1);
            return tweet.getUid();
        }
    }
}
