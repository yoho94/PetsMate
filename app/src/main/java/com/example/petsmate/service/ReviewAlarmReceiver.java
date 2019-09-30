package com.example.petsmate.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.example.petsmate.R;
import com.example.petsmate.ReviewPush;

public class ReviewAlarmReceiver extends BroadcastReceiver {
    private final static int NOTICATION_ID = 222;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("AlarmBroadcastReceiver", "onReceive");

        String des = intent.getStringExtra("des");
        String title = "목적지에 도착헀습니다.";
        String body =  des + "\n리뷰를 남겨주세요.";

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "channel")
                .setSmallIcon(R.drawable.noti_call_icon) //알람 아이콘
                .setContentTitle(title)  //알람 제목
                .setContentText(body) //알람 내용
                .setPriority(NotificationCompat.PRIORITY_HIGH) //알람 중요도
                .setDefaults(Notification.DEFAULT_ALL)
                .setSound(RingtoneManager
                        .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body))

                ;


        // 푸시용 인텐트 추가
        Intent resultIntent = new Intent(getApplicationContext(), ReviewPush.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(ReviewPush.class);
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        builder.setContentIntent(resultPendingIntent);




        /*
        // 푸시용 인텐트 추가
       PendingIntent notifyPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        notifyIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
        );

       Intent notifyIntent = new Intent(this, mypage03.class); // 테스트용
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent notifyPendingIntent = PendingIntent.getActivity(this, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);



       Intent resultIntent = new Intent(this, ReviewPush.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(ReviewPush.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        */


        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(NOTICATION_ID, builder.build()); //알람 생성

        JobSchedulerStart.stop(context);
    }
}
