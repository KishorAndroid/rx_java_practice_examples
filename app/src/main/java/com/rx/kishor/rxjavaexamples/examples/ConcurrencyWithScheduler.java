package com.rx.kishor.rxjavaexamples.examples;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.rx.kishor.rxjavaexamples.R;
import com.rx.kishor.rxjavaexamples.adapter.LogAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by user on 4/13/2016.
 */
public class ConcurrencyWithScheduler extends Fragment {

    private static final String TAG = ConcurrencyWithScheduler.class.getCanonicalName();
    @Bind(R.id.progress_operation_running)
    ProgressBar _progress;

    @Bind(R.id.log_list)
    ListView _logsList;

    private Subscription _subscription;

    private View rootView;
    private LogAdapter _adapter;
    private List<String> _logs;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.concurrency_with_scheduler, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @OnClick(R.id.btn_start_operation)
    public void startLongOperation() {

        _progress.setVisibility(View.VISIBLE);
        _log("Button Clicked");

        _subscription = Observable.just(true).map(new Func1<Boolean, Boolean>() {
            @Override
            public Boolean call(Boolean aBoolean) {
                _log("Within Observable");
                try {
                    _doSomeLongOperation_thatBlocksCurrentThread();
                } catch (InterruptedException e) {
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
                        _log("On complete");
                        _progress.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onError(Throwable e) {
                        _log(String.format("Boo! Error %s", e.getMessage()));
                        _progress.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        _log(String.format("onNext with return value \"%b\"", aBoolean));
                    }
                });                             // Observer
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        _setupLogger();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            _subscription.unsubscribe();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private void _doSomeLongOperation_thatBlocksCurrentThread() throws InterruptedException {
        _log("performing long operation");
        Thread.sleep(3000);
    }

    private void _setupLogger() {
        _logs = new ArrayList<>();
        _adapter = new LogAdapter(getActivity(), new ArrayList<String>());
        _logsList.setAdapter(_adapter);
    }

    private void _log(String logMsg) {

        if (_isCurrentlyOnMainThread()) {
            _logs.add(0, logMsg + " (main thread) ");
            showLogs();
        } else {
            _logs.add(0, logMsg + " (NOT main thread) ");

            // You can only do below stuff on main thread.
            new Handler(Looper.getMainLooper()).post(new Runnable() {

                @Override
                public void run() {
                    showLogs();
                }
            });
        }
    }

    private void showLogs() {
        _adapter.clear();
        _adapter.addAll(_logs);
    }

    private boolean _isCurrentlyOnMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }
}
