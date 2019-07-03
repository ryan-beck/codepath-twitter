package com.codepath.apps.restclienttemplate.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;

public class composeActivity extends AppCompatActivity {

    EditText etInput;
    Button btnSend;
    TwitterClient client;
    TextView tvCharacterCount;
    public static final String RESULT_TWEET_KEY = "result_tweet";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        etInput = findViewById(R.id.etTweetInput);
        btnSend = findViewById(R.id.btnSend);
        tvCharacterCount = findViewById(R.id.tvCharacterCount);
        tvCharacterCount.setText("280");

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendTweet();
            }
        });


        etInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int charsLeft = 280 - etInput.getText().length();
                tvCharacterCount.setText(Integer.toString(charsLeft));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

//        etInput.setOnKeyListener(new View.OnKeyListener() {
//
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                int charsLeft = 280 - etInput.getText().length();
//                tvCharacterCount.setText(Integer.toString(charsLeft));
//                return false;
//            }
//        });

        client = TwitterApp.getRestClient(this);
    }



    private void sendTweet() {
        client.sendTweet(etInput.getText().toString(), new AsyncHttpResponseHandler() {
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
