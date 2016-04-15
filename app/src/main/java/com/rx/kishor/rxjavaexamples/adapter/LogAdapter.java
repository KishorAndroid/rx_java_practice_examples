package com.rx.kishor.rxjavaexamples.adapter;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.rx.kishor.rxjavaexamples.R;

import java.util.List;

/**
 * Created by user on 4/13/2016.
 */
public class LogAdapter
        extends ArrayAdapter<String> {

    public LogAdapter(Context context, List<String> logs) {
        super(context, R.layout.item_log, R.id.item_log, logs);
    }
}
