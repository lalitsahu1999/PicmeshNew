package com.ihsuraa.picmesh.StickerPicker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.customview.widget.ViewDragHelper;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.ihsuraa.picmesh.R;

public class StickerPickerSnackbar extends BaseTransientBottomBar<StickerPickerSnackbar> {

    /**
     * Constructor for the transient bottom bar.
     *
     * @param parent The parent for this transient bottom bar.
     * @param content The content view for this transient bottom bar.
     * @param callback The content view callback for this transient bottom bar.
     */
    private StickerPickerSnackbar(ViewGroup parent, View content, ContentViewCallback callback) {
        super(parent, content, callback);
    }

    public static StickerPickerSnackbar make(@NonNull ViewGroup parent, @Duration int duration) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View content = inflater.inflate(R.layout.intro_slide1, parent, false);
        final ContentViewCallback viewCallback = new ContentViewCallback(content);
        final StickerPickerSnackbar stickerPickerSnackbar = new StickerPickerSnackbar(parent, content, viewCallback);

        stickerPickerSnackbar.getView().setPadding(0, 0, 0, 0);
        stickerPickerSnackbar.setDuration(duration);
        stickerPickerSnackbar.getView().setMinimumHeight(600);
        StickerPickerSnackbar.SnackbarBaseLayout layout1 = (StickerPickerSnackbar.SnackbarBaseLayout) stickerPickerSnackbar.getView();
        layout1.setMinimumHeight(600);
        return stickerPickerSnackbar;
    }

    /*
    public StickerPickerSnackbar setText(CharSequence text) {
        TextView textView = (TextView) getView().findViewById(R.id.snackbar_text);
        textView.setText(text);
        return this;
    }

    public StickerPickerSnackbar setAction(CharSequence text, final View.OnClickListener listener) {
        Button actionView = (Button) getView().findViewById(R.id.snackbar_action);
        actionView.setText(text);
        actionView.setVisibility(View.VISIBLE);
        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClick(view);
                // Now dismiss the Snackbar
                dismiss();
            }
        });
        return this;
    }

     */

    private static class ContentViewCallback implements BaseTransientBottomBar.ContentViewCallback {

        private View content;

        public ContentViewCallback(View content) {
            this.content = content;
        }

        @Override
        public void animateContentIn(int delay, int duration) {

            ViewCompat.setScaleY(content, 0f );
            ViewCompat.animate(content).scaleY(1f).setDuration(duration).setStartDelay(delay);
        }

        @Override
        public void animateContentOut(int delay, int duration) {
            ViewCompat.setScaleY(content, 1f);
            ViewCompat.animate(content).scaleY(0f).setDuration(duration).setStartDelay(delay);
        }
    }
}