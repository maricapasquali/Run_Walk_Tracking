package com.run_walk_tracking_gps.exception;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import com.run_walk_tracking_gps.controller.ErrorQueue;
import com.run_walk_tracking_gps.utilities.AppUtilities;

import java.util.Objects;
import java.lang.Comparable;

public abstract class BackgroundException extends AbstractException implements Comparable<BackgroundException>{

    private String tag;

    BackgroundException(Context context, int mex, String tag) {
        super(context, mex);
        this.tag = tag;
    }

    BackgroundException(Context context, String mex, String action_background) {
        super(context, mex);
        this.tag = action_background;
    }

    @Override
    public void alert() {
        Log.e(getContext().getPackageName(), getMessage());
        if(AppUtilities.isInForeground(getContext()))
            createAlertDialog();
        else
            ErrorQueue.getInstance(getContext()).add(this);
    }

    public DialogInterface.OnClickListener close() {
        return (dialog, which) -> ErrorQueue.getInstance(getContext()).remove(this);
    }

    protected abstract void createAlertDialog();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BackgroundException that = (BackgroundException) o;
        return Objects.equals(tag, that.tag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tag);
    }

    @Override
    public int compareTo(BackgroundException o) {
        return this.equals(o)? 0 : -1;
    }

    @Override
    public String toString() {
        return tag + " : " + getMessage();
    }
}
