package com.zt.twitterintegration;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.models.User;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity {

    private static final String TWITTER_KEY = "r5oW9FVgiXUULcUWMFFDCdCVU";
    private static final String TWITTER_SECRET = "JUAhg9xGxBvjMPINiT8X7kHrkJ01Da2owWq1cHRhsa7Wiiu2rg";
    private TwitterLoginButton twitterLoginButton;
    //
    private TextView tvName, tvEmail;
    private ImageView imgProfilePicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        setContentView(R.layout.activity_main);

        initUI();
    }

    public void initUI() {
        twitterLoginButton = (TwitterLoginButton) findViewById(R.id.twitter_login_button);
        tvName = (TextView) findViewById(R.id.tvName);
        tvEmail = (TextView) findViewById(R.id.tvEmail);
        imgProfilePicture = (ImageView) findViewById(R.id.imgProfilePicture);

        twitterLoginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                getUserInformation(result);
            }

            @Override
            public void failure(TwitterException exception) {
                Utils.showToast(MainActivity.this, "Login failed.");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        twitterLoginButton.onActivityResult(requestCode, resultCode, data);
    }

    public void getUserInformation(Result<TwitterSession> result) {
        TwitterSession session = result.data;
        Twitter.getApiClient(session).getAccountService()
                .verifyCredentials(true, false, new Callback<User>() {
                    @Override
                    public void failure(TwitterException e) {
                        Utils.showToast(MainActivity.this, "Login failed.");
                    }

                    @Override
                    public void success(Result<User> userResult) {
                        try {
                            User user = userResult.data;
                            String name = user.name;
                            String email = user.email;
                            String followers = String.valueOf(user.followersCount);
                            String strProfilePicture = user.profileImageUrl;
                            Utils.showToast(MainActivity.this, "Login Successfully.");
                            //Name
                            if (name != null) {
                                tvName.setVisibility(View.VISIBLE);
                                tvName.setText("Name : " + name);
                            }
                            //Email
                            if (followers != null) {
                                tvEmail.setVisibility(View.VISIBLE);
                                tvEmail.setText("Followers : " + String.valueOf(followers));
                            }
                            //Profile Picture
                            if (strProfilePicture != null) {
                                imgProfilePicture.setVisibility(View.VISIBLE);
                                Picasso.with(MainActivity.this).load(strProfilePicture).into(imgProfilePicture);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                });

       /* //Get User Email
        TwitterAuthClient authClient = new TwitterAuthClient();
        authClient.requestEmail(session, new Callback<String>() {
            @Override
            public void success(Result<String> result) {
            }

            @Override
            public void failure(TwitterException exception) {
            }
        });*/
    }
}
