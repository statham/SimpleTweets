package com.codepath.apps.restclienttemplate.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TwitterApplication;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;

public class ComposeActivity extends AppCompatActivity {

    private TwitterClient client;

    TextView tvScreenName;
    TextView tvName;
    ImageView ivProfileImage;
    EditText etNewTweet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.twitter_logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        client = TwitterApplication.getRestClient();

        tvScreenName = (TextView) findViewById(R.id.tvScreenName);
        tvName = (TextView) findViewById(R.id.tvName);
        ivProfileImage = (ImageView) findViewById(R.id.ivProfileImageCompose);
        etNewTweet = (EditText) findViewById(R.id.etNewTweet);

        User user = (User) Parcels.unwrap(getIntent().getParcelableExtra("user"));

        tvScreenName.setText(user.screenName);
        tvName.setText(user.name);
        Glide.with(getApplicationContext()).load(user.profileImageUrl).into(ivProfileImage);
    }

    public void onCancel(View view) {
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    public void onSubmit(View view) {
        postTweet();

    }

    public void postTweet() {
        client.createTweet(new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Intent intent = new Intent();
                try {
                    intent.putExtra("tweet", Parcels.wrap(Tweet.fromJSON(response)));
                    setResult(RESULT_OK, intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                    setResult(RESULT_CANCELED, intent);
                }
                finish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        }, etNewTweet.getText().toString());
    }
}
