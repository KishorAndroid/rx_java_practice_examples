package com.rx.kishor.rxjavaexamples.examples;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.rx.kishor.rxjavaexamples.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;
import rx.Subscription;
import rx.functions.Action1;
import rx.subjects.PublishSubject;

import static android.text.TextUtils.isEmpty;

/**
 * Created by user on 4/13/2016.
 */
public class DoubleBinding extends Fragment {

    @Bind(R.id.double_binding_num1)
    EditText number1;

    @Bind(R.id.double_binding_num2)
    EditText number2;

    @Bind(R.id.double_binding_result)
    TextView result;

    Subscription subscription;
    PublishSubject<Float> resultEmitterSubject;
    private View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.double_binding, container, false);
        ButterKnife.bind(this, rootView);

        resultEmitterSubject = PublishSubject.create();
        subscription = resultEmitterSubject.asObservable().subscribe(new Action1<Float>() {
            @Override
            public void call(Float aFloat) {
                result.setText(String.valueOf(aFloat));
            }
        });

        onNumberChanged();
        number2.requestFocus();

        return rootView;
    }

    @OnTextChanged({R.id.double_binding_num1, R.id.double_binding_num2})
    public void onNumberChanged() {
        float num1 = 0;
        float num2 = 0;

        if (!isEmpty(number1.getText().toString())) {
            num1 = Float.parseFloat(number1.getText().toString());
        }

        if (!isEmpty(number2.getText().toString())) {
            num2 = Float.parseFloat(number2.getText().toString());
        }

        resultEmitterSubject.onNext(num1 + num2);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            subscription.unsubscribe();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ButterKnife.unbind(this);
    }
}
