package com.rx.kishor.rxjavaexamples.examples;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.rx.kishor.rxjavaexamples.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observer;
import rx.functions.Func1;
import rx.subjects.PublishSubject;

/**
 * Created by user on 4/14/2016.
 */
public class RxPublishSubjectDemo extends Fragment {

    @Bind(R.id.click_counter)
    TextView clickCounter;

    @Bind(R.id.click)
    Button click;

    private View rootView;

    private PublishSubject<Integer> mCounterEmitter;

    private int counter = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.rx_publish_subject, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mCounterEmitter = PublishSubject.create();
        mCounterEmitter
                .filter(new Func1<Integer, Boolean>() {
                    @Override
                    public Boolean call(Integer integer) {
                        return (integer%2==0?true:false);
                    }
                })
                .subscribe(new Observer<Integer>() {

                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(Integer integer) {
                        clickCounter.setText(String.valueOf(integer));
                    }
                });
    }

    @OnClick(R.id.click)
    public void setClickCounter() {
        counter++;
        mCounterEmitter.onNext(counter);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
