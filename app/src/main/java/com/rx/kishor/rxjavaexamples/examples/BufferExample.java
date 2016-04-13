package com.rx.kishor.rxjavaexamples.examples;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.jakewharton.rxbinding.view.RxView;
import com.rx.kishor.rxjavaexamples.R;
import com.rx.kishor.rxjavaexamples.adapter.LogAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by user on 4/13/2016.
 */
public class BufferExample extends Fragment {

    @Bind(R.id.click_buffer)
    Button clickBuffer;

    @Bind(R.id.log_list)
    ListView _logsList;

    private View rootView;

    private Subscription _subscription;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.buffer_example, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        _setupLogger();
    }

    @Override
    public void onResume() {
        super.onResume();
        _subscription = RxView.clicks(clickBuffer)
                .map(new Func1<Void, Integer>() {
                    @Override
                    public Integer call(Void aVoid) {
                        return 1;
                    }
                })
                .buffer(2, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Integer>>() {
                    @Override
                    public void call(List<Integer> integers) {
                        if (integers.size() > 0) {
                            _log("Clicks : " + integers.size());
                        } else {
                            _log("No Clicks");
                        }
                    }
                });
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

    private LogAdapter _adapter;
    private List<String> _logs;

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
