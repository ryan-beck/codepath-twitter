package com.codepath.apps.restclienttemplate.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;

import static com.codepath.apps.restclienttemplate.Activities.composeActivity.RESULT_TWEET_KEY;

public class ReplyActivity extends AppCompatActivity {

    TextView tvReplyTo;
    ImageView ivProfile;
    EditText etReplyText;
    Button btnReply;
    TextView tvCharCount;
    User user;
    Tweet tweet;

    TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);

        tvReplyTo = findViewById(R.id.tvReplyTo);
        ivProfile = findViewById(R.id.ivProfile);
        etReplyText = findViewById(R.id.etReplyText);
        btnReply = findViewById(R.id.btnReply);
        tvCharCount = findViewById(R.id.tvCharCount);

        user = Parcels.unwrap(getIntent().getParcelableExtra(User.class.getSimpleName()));
        tweet = Parcels.unwrap(getIntent().getParcelableExtra(Tweet.class.getSimpleName()));

        client = TwitterApp.getRestClient(this);

        tvReplyTo.setText("@" + user.screenName);

        btnReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendTweet();
            }
        });
    }




    private void sendTweet() {
        client.sendRelpy(etReplyText.getText().toString(), tweet.uid, user.screenName, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if(statusCode == 200) {
                    try {
                        JSONObject responseJson = new JSONObject(new String(responseBody));
                        Tweet tweet = Tweet.fromJSON(responseJson);

                        Intent resultData = new Intent();
                        resultData.putExtra(RESULT_TWEET_KEY, Parcels.wrap(tweet));

                        setResult(RESULT_OK, resultData);
                        finish();
                    } catch (JSONException e) {
                        Log.e("ComposeActivity", "Error parsing response");
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }
}
