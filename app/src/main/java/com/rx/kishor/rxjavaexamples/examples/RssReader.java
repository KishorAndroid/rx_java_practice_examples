package com.rx.kishor.rxjavaexamples.examples;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.einmalfel.earl.EarlParser;
import com.einmalfel.earl.Feed;
import com.einmalfel.earl.Item;
import com.rx.kishor.rxjavaexamples.R;
import com.rx.kishor.rxjavaexamples.adapter.LogAdapter;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.zip.DataFormatException;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by user on 4/13/2016.
 */
public class RssReader extends Fragment {

    private static final String TAG = RssReader.class.getCanonicalName();
    @Bind(R.id.rss_feed)
    ListView rssFeed;
    @Bind(R.id.progress_bar)
    ContentLoadingProgressBar progressBar;
    private ArrayList<String> rssFeeds = new ArrayList<>();
    private LogAdapter rssAdapter; //used for showing RSS feeds
    private View rootView;

    private Subscription subscription;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.rss_reader, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            subscription.unsubscribe();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        subscription = Observable.just(true).map(new Func1<Boolean, Boolean>() {
            @Override
            public Boolean call(Boolean aBoolean) {
                try {
                    readRss();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } catch (DataFormatException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return aBoolean;
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onCompleted() {
                        progressBar.setVisibility(View.INVISIBLE);
                        rssFeed.setVisibility(View.VISIBLE);
                        showFeed(rssFeeds);
                    }

                    @Override
                    public void onError(Throwable e) {
                        progressBar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {

                    }
                });// Observer
    }

    private void showFeed(ArrayList<String> rssFeeds) {
        rssAdapter.clear();
        rssAdapter.addAll(rssFeeds);
    }

    private void setupAdapter() {
        rssAdapter = new LogAdapter(getActivity(), new ArrayList<String>());
        rssFeed.setAdapter(rssAdapter);
    }

    private void readRss() throws XmlPullParserException, IOException, DataFormatException {
        InputStream inputStream = new URL("http://www.feedforall.com/sample.xml").openConnection().getInputStream();
        Feed feed = EarlParser.parseOrThrow(inputStream, 0);
        Log.i(TAG, "Processing feed: " + feed.getTitle());
        for (Item item : feed.getItems()) {
            String title = item.getTitle();
            rssFeeds.add(title);
            Log.i(TAG, "Item title: " + (title == null ? "N/A" : title));
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupAdapter();
    }
}
