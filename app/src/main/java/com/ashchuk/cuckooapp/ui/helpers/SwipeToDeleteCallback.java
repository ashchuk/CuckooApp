package com.ashchuk.cuckooapp.ui.helpers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.DisplayMetrics;
import android.view.View;

import com.ashchuk.cuckooapp.CuckooApp;
import com.ashchuk.cuckooapp.R;

// https://medium.com/@kitek/recyclerview-swipe-to-delete-easier-than-you-thought-cff67ff5e5f6

public abstract class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {

    public SwipeToDeleteCallback(int dragDirs, int swipeDirs) {
        super(dragDirs, swipeDirs);
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                            float dX, float dY, int actionState, boolean isCurrentlyActive) {

        View itemView = viewHolder.itemView;

        Paint p = new Paint();
        p.setARGB(255, 120, 0, 0);

        Bitmap icon;

        icon = BitmapFactory.decodeResource(
                CuckooApp.getAppComponent().getContext().getResources(), R.drawable.common_full_open_on_phone);

        c.drawRect((float) itemView.getRight() + dX, (float) itemView.getTop(),
                (float) itemView.getRight(), (float) itemView.getBottom(), p);

        c.drawBitmap(icon,
                (float) itemView.getRight() - convertDpToPx(16) - icon.getWidth(),
                (float) itemView.getTop() + ((float) itemView.getBottom() - (float) itemView.getTop() - icon.getHeight()) / 2,
                p);

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    private int convertDpToPx(int dp) {
        return Math.round(dp * (CuckooApp.getAppComponent()
                .getContext().getResources()
                .getDisplayMetrics().xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    @Override
    public boolean onMove(RecyclerView recyclerView,
                          RecyclerView.ViewHolder viewHolder,
                          RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

    }
}
