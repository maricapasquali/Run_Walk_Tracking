package com.run_walk_tracking_gps.exception;

import android.content.Context;
import com.run_walk_tracking_gps.R;

public abstract class AbstractException extends Exception {

    private String mex;
    private Context context;

    public AbstractException(Context context, int mex) {
        this(context);
        setMessage(mex);
    }

    public AbstractException(Context context, String mex) {
        this(context);
        setMessage(mex);
    }

    public AbstractException(Context context) {
        setContext(context);
        setMessage(R.string.space);
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public String getMessage() {
        return mex;
    }

    public void setMessage(int mex) {
        this.mex = context.getString(mex);
    }

    public void setMessage(String mex) {
        this.mex = mex;
    }

    public abstract void alert();
}