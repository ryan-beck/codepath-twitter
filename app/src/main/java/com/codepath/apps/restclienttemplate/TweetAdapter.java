package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.Activities.ReplyActivity;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;

import org.parceler.Parcels;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder> {


    public List<Tweet> mTweets;
    static Context context;

    public TweetAdapter(List<Tweet> tweets) {
        mTweets = tweets;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View tweetView = inflater.inflate(R.layout.tweet_layout, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(tweetView, mTweets.get(i));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Drawable myDrawableHeart;
        Drawable myDrawableRetweet;
        Tweet tweet = mTweets.get(i);

        viewHolder.tvUsername.setText(tweet.user.name);
        viewHolder.tvBody.setText(tweet.body);
        viewHolder.tvDate.setText(getRelativeTimeAgo(tweet.createdAt));

        Glide.with(context)
                .load(tweet.user.profileImageUrl)
                .bitmapTransform(new RoundedCornersTransformation(context, 30, 0))
                //.placeholder(placeHolderId)
                .into(viewHolder.ivProfilePic);

        // Setting favorite button icon
        if(tweet.isLiked) {
            myDrawableHeart = context.getResources().getDrawable(R.drawable.ic_vector_heart);
        } else {
            myDrawableHeart = context.getResources().getDrawable(R.drawable.ic_vector_heart_stroke);
        }
        viewHolder.ivLike.setImageDrawable(myDrawableHeart);

        if(tweet.isRetweeted) {
            myDrawableRetweet = context.getResources().getDrawable(R.drawable.ic_vector_retweet);
        } else {
            myDrawableRetweet = context.getResources().getDrawable(R.drawable.ic_vector_retweet_stroke);
        }
        viewHolder.ivRetweet.setImageDrawable(myDrawableRetweet);

    }

    @Override
    public int getItemCount() {
        return mTweets.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        //public Tweet tweet;

        public ImageView ivProfilePic;
        public TextView tvUsername;
        public TextView tvBody;
        public TextView tvDate;
        public ImageView ivReply;
        public ImageView ivRetweet;
        public ImageView ivLike;
        public ImageView ivShare;

        public ViewHolder(@NonNull final View itemView, final Tweet tweet) {
            super(itemView);

            //this.tweet = tweet;

            ivProfilePic = itemView.findViewById(R.id.ivProfilePic);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvBody = itemView.findViewById(R.id.tvText);
            tvDate = itemView.findViewById(R.id.tvDate);
            ivReply = itemView.findViewById(R.id.ivReply);
            ivRetweet = itemView.findViewById(R.id.ivRetweet);
            ivLike = itemView.findViewById(R.id.ivLike);
            ivShare = itemView.findViewById(R.id.ivShare);

            ivLike.setOnClickListener(new View.OnClickListener(){
                Drawable wrappedDrawable;
                //TODO: Create post request to actually favorite tweet
                public void onClick(View v) {
                    if(!tweet.isLiked) {
                        Drawable myDrawable = itemView.getResources().getDrawable(R.drawable.ic_vector_heart);
                        wrappedDrawable = DrawableCompat.wrap(myDrawable);
                        DrawableCompat.setTint(wrappedDrawable, Color.RED);
                    } else {
                        Drawable myDrawable = itemView.getResources().getDrawable(R.drawable.ic_vector_heart_stroke);
                        wrappedDrawable = DrawableCompat.wrap(myDrawable);
                        DrawableCompat.setTint(wrappedDrawable, Color.BLACK);
                    }
                    tweet.isLiked = !tweet.isLiked;
                    ivLike.setImageDrawable(wrappedDrawable);
                }
            });

            //TODO: Create post request to actually retweet

            ivRetweet.setOnClickListener(new View.OnClickListener() {
                Drawable wrappedDrawable;
                public void onClick(View v) {
                    if(!tweet.isRetweeted) {
                        Drawable myDrawable = itemView.getResources().getDrawable(R.drawable.ic_vector_retweet);
                        wrappedDrawable = DrawableCompat.wrap(myDrawable);
                        DrawableCompat.setTint(wrappedDrawable, Color.parseColor("#ff1da1f2"));
                    } else {
                        Drawable myDrawable = itemView.getResources().getDrawable(R.drawable.ic_vector_retweet_stroke);
                        wrappedDrawable = DrawableCompat.wrap(myDrawable);
                        DrawableCompat.setTint(wrappedDrawable, Color.BLACK);
                    }
                    tweet.isRetweeted = !tweet.isRetweeted;
                    ivRetweet.setImageDrawable(wrappedDrawable);
                }
            });

            //TODO: ivReply onClick
            ivReply.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ReplyActivity.class);
                    User user = tweet.user;
                    intent.putExtra(User.class.getSimpleName(), Parcels.wrap(user));
                    intent.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
                    context.startActivity(intent);
                    //Log.d("ID", tweet.body + " " + tweet.id);

                }
            });
            //TODO: ivShare onClick
        }
    }

    public String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }

    // Clean all elements of the recycler
    public void clear() {
        mTweets.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Tweet> list) {
        mTweets.addAll(list);
        notifyDataSetChanged();
    }

}
