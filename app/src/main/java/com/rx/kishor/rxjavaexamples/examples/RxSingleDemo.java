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
import android.widget.Toast;

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
import java.util.List;
import java.util.concurrent.Callable;
import java.util.zip.DataFormatException;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Scheduler;
import rx.Single;
import rx.SingleSubscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by user on 4/14/2016.
 */
public class RxSingleDemo extends Fragment {

    private static final String TAG = RxSingleDemo.class.getCanonicalName();
    @Bind(R.id.rss_feed)
    ListView rssFeed;
    @Bind(R.id.progress_bar)
    ContentLoadingProgressBar progressBar;
    private LogAdapter rssAdapter; //used for showing RSS feeds
    private View rootView;

    private Subscription subscription;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.rx_single, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupAdapter();
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
        Single<List<String>> rssFeedSingle = Single.fromCallable(new Callable<List<String>>() {

            @Override
            public List<String> call() throws Exception {
                try {
                    return readRss();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } catch (DataFormatException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        });

        subscription = rssFeedSingle
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleSubscriber<List<String>>() {
                    @Override
                    public void onSuccess(List<String> rssFeeds) {
                        progressBar.setVisibility(View.INVISIBLE);
                        rssFeed.setVisibility(View.VISIBLE);
                        showFeed((ArrayList<String>) rssFeeds);
                    }

                    @Override
                    public void onError(Throwable error) {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(getContext(), "Error reading feed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showFeed(ArrayList<String> rssFeeds) {
        rssAdapter.clear();
        rssAdapter.addAll(rssFeeds);
    }

    private void setupAdapter() {
        rssAdapter = new LogAdapter(getActivity(), new ArrayList<String>());
        rssFeed.setAdapter(rssAdapter);
    }

    private ArrayList<String> readRss() throws XmlPullParserException, IOException, DataFormatException {
        ArrayList<String> rssFeeds = new ArrayList<>();
        InputStream inputStream = new URL("http://www.feedforall.com/sample.xml").openConnection().getInputStream();
        Feed feed = EarlParser.parseOrThrow(inputStream, 0);
        for (Item item : feed.getItems()) {
            String title = item.getTitle();
            rssFeeds.add(title);
        }
        return rssFeeds;
    }
}
