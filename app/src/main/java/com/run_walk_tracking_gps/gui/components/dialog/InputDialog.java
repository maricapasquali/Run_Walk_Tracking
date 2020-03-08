package com.run_walk_tracking_gps.gui.components.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.run_walk_tracking_gps.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

public class InputDialog extends AlertDialog.Builder {

    private View view;
    private TextInputEditText text;

    private InputDialog(@NonNull Context context) {
        super(context);
        view = LayoutInflater.from(context).inflate(R.layout.custom_dialog_input, null);
        setView(view);
        text = view.findViewById(R.id.input);
        setNegativeButton(R.string.cancel, null);
    }

    private void setContent(String content) {
        text.setText(content);
    }

    private void setHint(int hint) {
        ((TextInputLayout) text.getParent().getParent()).setHint(getContext().getString(hint));
    }

    private void setPositiveButton(int idButton, InputDialog.OnClickPositiveButtonListener listener){
        setPositiveButton(idButton, (dialog, which) -> listener.onClickPositiveButton(text.getText().toString()));
    }


    public interface OnClickPositiveButtonListener{
        void onClickPositiveButton(String text);
    }

    public static class Builder {

        private InputDialog dialogInput;

        private Builder(Context  context) {
            dialogInput = new InputDialog(context);
        }

        public static Builder getInstance(Context context){
            return new Builder(context);
        }

        public AlertDialog create(){
            return dialogInput.create();
        }

        public Builder setContent(String content) {
            dialogInput.setContent(content);
            return this;
        }

        public Builder setTitle(int title){
            dialogInput.setTitle(title);
            return this;
        }

        public Builder setHint(int hint){
            dialogInput.setHint(hint);
            return this;
        }

        public Builder setPositiveButton(int idButton, InputDialog.OnClickPositiveButtonListener listener){
            dialogInput.setPositiveButton(idButton, listener);
            return this;
        }
    }

}
