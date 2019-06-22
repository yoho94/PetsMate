package com.example.petsmate.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.example.petsmate.GpsTracker;
import com.example.petsmate.LocationDistance;
import com.example.petsmate.task.CallUpdateCodeTask;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;


public class NotificationJobFireBaseService extends JobService {

    @Override
    public boolean onStartJob(JobParameters job) {

        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Bundle bundle = job.getExtras();

        double lat = bundle.getDouble("lat");
        double lng = bundle.getDouble("lng");
        String des = bundle.getString("des");
        String serialNumber = bundle.getString("serialNumber");

        Log.d("NotificationJobService", "latlng="+ lat +","+ lng);
        Log.d("NotificationJobService", "des="+ des);

        Intent intent = new Intent(this, ReviewAlarmReceiver.class);
        intent.putExtra("des", des);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 111, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        /**
         * Intent 플래그
         *    FLAG_ONE_SHOT : 한번만 사용하고 다음에 이 PendingIntent가 불려지면 Fail을 함
         *    FLAG_NO_CREATE : PendingIntent를 생성하지 않음. PendingIntent가 실행중인것을 체크를 함
         *    FLAG_CANCEL_CURRENT : 실행중인 PendingIntent가 있다면 기존 인텐트를 취소하고 새로만듬
         *    FLAG_UPDATE_CURRENT : 실행중인 PendingIntent가 있다면  Extra Data만 교체함
         */

        GpsTracker gpsTracker = new GpsTracker(this);

        double myLat = gpsTracker.getLocation().getLatitude();
        double myLng = gpsTracker.getLocation().getLongitude();

        double distance = LocationDistance.distance(lat, lng, myLat, myLng, "meter");

        Log.d("NotificationJobService", "distance="+distance);

        if(distance <= 50) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pendingIntent);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                manager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pendingIntent);
            } else {
                manager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pendingIntent);
            }
            new CallUpdateCodeTask().execute("13",serialNumber);
        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // TEST.
//            manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 10000, pendingIntent); //10초뒤 알람
//        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            manager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 10000, pendingIntent);
//        } else {
//            manager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 10000, pendingIntent);
//        }

        /**
         * AlarmType
         *    RTC_WAKEUP : 대기모드에서도 알람이 작동함을 의미함
         *    RTC : 대기모드에선 알람을 작동안함
         */

        return false; // Answers the question: "Is there still work going on?"
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return false; // Answers the question: "Should this job be retried?"
    }
}
