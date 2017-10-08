package com.codepath.apps.restclienttemplate.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.PatternEditableBuilder;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.models.Tweet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Created by kystatham on 9/26/17.
 */

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder> {

    private List<Tweet> mTweets;
    private Context mContext;
    private TweetAdapterListener mListener;

    public interface TweetAdapterListener {
        public void onItemSelected(View view, int position);
        public void onProfileImageSelected(String screenName);
        public void onScreenNameSelected(String screenName);
    }

    public TweetAdapter(List<Tweet> tweets, TweetAdapterListener listener) {
        mTweets = tweets;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);

        View tweetView = inflater.inflate(R.layout.item_tweet, parent, false);
        ViewHolder viewHolder = new ViewHolder(tweetView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Tweet tweet = mTweets.get(position);

        holder.tvFullName.setText(tweet.user.name);
        holder.tvBody.setText(tweet.body);
        holder.tvRelativeTime.setText(getRelativeTimeAgo(tweet.createdAt));
        holder.tvScreenName.setText(String.format("@%s", tweet.user.screenName));

        new PatternEditableBuilder().
                addPattern(Pattern.compile("\\@(\\w+)"),
                        new PatternEditableBuilder.SpannableClickedListener() {
                            @Override
                            public void onSpanClicked(String text) {
                                mListener.onScreenNameSelected(text);
                            }
                        }).into(holder.tvBody);

        new PatternEditableBuilder().
                addPattern(Pattern.compile("\\#(\\w+)"),
                        new PatternEditableBuilder.SpannableClickedListener() {
                            @Override
                            public void onSpanClicked(String text) {
                                Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();
                            }
                        }).into(holder.tvBody);

        Glide.with(mContext).load(tweet.user.profileImageUrl).into(holder.ivProfileImage);
    }

    @Override
    public int getItemCount() {
        return mTweets.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivProfileImage;
        public TextView tvFullName;
        public TextView tvBody;
        public TextView tvRelativeTime;
        public TextView tvScreenName;

        public ViewHolder(View itemView) {
            super(itemView);

            ivProfileImage = (ImageView) itemView.findViewById(R.id.ivProfileImage);
            tvFullName = (TextView) itemView.findViewById(R.id.tvFullName);
            tvBody = (TextView) itemView.findViewById(R.id.tvBody);
            tvRelativeTime = (TextView) itemView.findViewById(R.id.tvRelativeTime);
            tvScreenName = (TextView) itemView.findViewById(R.id.tvScreenName);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        int position = getAdapterPosition();
                        mListener.onItemSelected(v, position);
                    }
                }
            });

            ivProfileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        int position = getAdapterPosition();
                        mListener.onProfileImageSelected(mTweets.get(position).user.screenName);
                    }
                }
            });
        }
    }

    public String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        StringBuilder relativeTime = new StringBuilder();
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
            String[] relativeDateParts = relativeDate.split(" ");
            for (int i = 0; i < relativeDateParts.length; i++) {
                String word = relativeDateParts[i];
                if (word.contains("second")) {
                    relativeTime.append("s");
                    break;
                } else if (word.contains("minute")) {
                    relativeTime.append("m");
                    break;
                } else if (word.contains("hour")) {
                    relativeTime.append("h");
                    break;
                } else if (word.contains("day")) {
                    relativeTime.append("d");
                    break;
                } else if (word.contains("week")) {
                    relativeTime.append("w");
                    break;
                } else if (word.contains("year")) {
                    relativeTime.append("y");
                    break;
                }
                relativeTime.append(word);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeTime.toString();
    }
}
