package com.rx.kishor.rxjavaexamples.examples;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import static android.text.TextUtils.isEmpty;
import static android.util.Patterns.EMAIL_ADDRESS;

import com.jakewharton.rxbinding.widget.RxTextView;
import com.rx.kishor.rxjavaexamples.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.functions.Func3;

/**
 * Created by user on 4/13/2016.
 */
public class FormValidation extends Fragment {

    @Bind(R.id.btn_demo_form_valid)
    TextView btnValidIndicator;

    @Bind(R.id.demo_combl_email)
    EditText email;

    @Bind(R.id.demo_combl_password)
    EditText password;

    @Bind(R.id.demo_combl_num)
    EditText number;

    private View rootView;

    private Observable<CharSequence> emailChangeObservable;
    private Observable<CharSequence> passwordChangeObservable;
    private Observable<CharSequence> numberChangeObservable;

    private Subscription subscription = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.form_validation, container, false);
        ButterKnife.bind(this, rootView);

        emailChangeObservable = RxTextView.textChanges(email).skip(1);
        passwordChangeObservable = RxTextView.textChanges(password).skip(1);
        numberChangeObservable = RxTextView.textChanges(number).skip(1);

        rxJavaCode();

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
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

    private void rxJavaCode(){
        subscription = Observable.combineLatest(emailChangeObservable,
                passwordChangeObservable,
                numberChangeObservable,
                new Func3<CharSequence, CharSequence, CharSequence, Boolean>() {
                    @Override
                    public Boolean call(CharSequence newEmail,
                                        CharSequence newPassword,
                                        CharSequence newNumber) {

                        boolean emailValid = !isEmpty(newEmail) &&
                                EMAIL_ADDRESS.matcher(newEmail).matches();
                        if (!emailValid) {
                            email.setError("Invalid Email!");
                        }

                        boolean passValid = !isEmpty(newPassword) && newPassword.length() > 8;
                        if (!passValid) {
                            password.setError("Invalid Password!");
                        }

                        boolean numValid = !isEmpty(newNumber);
                        if (numValid) {
                            int num = Integer.parseInt(newNumber.toString());
                            numValid = num > 0 && num <= 100;
                        }
                        if (!numValid) {
                            number.setError("Invalid Number!");
                        }

                        return emailValid && passValid && numValid;

                    }
                })//
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(Boolean formValid) {
                        if (formValid) {
                            btnValidIndicator.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
                        } else {
                            btnValidIndicator.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                        }
                    }
                });
    }
}
