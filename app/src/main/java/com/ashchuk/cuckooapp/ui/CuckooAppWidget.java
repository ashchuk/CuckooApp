package com.ashchuk.cuckooapp.ui;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.ashchuk.cuckooapp.CuckooApp;
import com.ashchuk.cuckooapp.R;
import com.ashchuk.cuckooapp.infrastructure.helpers.UserStatusToImgSrcConverter;
import com.ashchuk.cuckooapp.infrastructure.helpers.UserStatusToStringConverter;
import com.ashchuk.cuckooapp.model.enums.UserStatus;
import com.ashchuk.cuckooapp.services.FirebaseUpdateService;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Implementation of App Widget functionality.
 */
public class CuckooAppWidget extends AppWidgetProvider {

    private static final String EXTRA_STATUS = "com.ashchuk.cuckooapp.services.extra.STATUS";
    private static final String EXTRA_USER_GUID = "com.ashchuk.cuckooapp.services.extra.USER_GUID";
    private static final String UPDATE_STATUS = "com.ashchuk.cuckooapp.services.action.UPDATE_STATUS";

    private static UserStatus currentStatus = UserStatus.HOME;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        if (FirebaseAuth.getInstance().getCurrentUser() == null)
            return;

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.cuckoo_app_widget);

        views.setImageViewResource(R.id.iv_current_status,
                UserStatusToImgSrcConverter.fromUserStatus(currentStatus));
        views.setTextViewText(R.id.tv_status_description, UserStatusToStringConverter
                .toString(currentStatus.getValue()));

        views.setOnClickPendingIntent(R.id.ib_home, getWidgetPendingIntent(UserStatus.HOME));
        views.setOnClickPendingIntent(R.id.ib_work, getWidgetPendingIntent(UserStatus.WORK));
        views.setOnClickPendingIntent(R.id.ib_lunch, getWidgetPendingIntent(UserStatus.LUNCH));
        views.setOnClickPendingIntent(R.id.ib_walk, getWidgetPendingIntent(UserStatus.WALK));
        views.setOnClickPendingIntent(R.id.ib_car, getWidgetPendingIntent(UserStatus.DRIVE));
        views.setOnClickPendingIntent(R.id.ib_sleep, getWidgetPendingIntent(UserStatus.SLEEP));

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private static PendingIntent getWidgetPendingIntent(UserStatus userStatus) {
        Intent intent = new Intent(CuckooApp.getAppComponent().getContext(), FirebaseUpdateService.class);
        intent.setAction(UPDATE_STATUS);
        intent.putExtra(EXTRA_STATUS, userStatus.getValue());
        intent.putExtra(EXTRA_USER_GUID, FirebaseAuth.getInstance().getCurrentUser().getUid());
        return PendingIntent.getService(
                CuckooApp.getAppComponent().getContext(),
                userStatus.getValue(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static void updateCuckooAppWidgets(Context context, UserStatus userStatus,
                                              AppWidgetManager appWidgetManager,
                                              int[] appWidgetIds) {
        currentStatus = userStatus;
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

