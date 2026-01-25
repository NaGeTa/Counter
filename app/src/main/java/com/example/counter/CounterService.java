package com.example.counter;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;

public class CounterService extends Service {
    private static final String CHANNEL_ID = "CounterChannel";
    private static final int NOTIFICATION_ID = 1;
    private VolumeObserver volumeObserver;
    private int count = 0;
    private int previousVolume = -1;
    private AudioManager audioManager;
    private PowerManager.WakeLock wakeLock;

    @Override
    public void onCreate() {
        super.onCreate();
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        volumeObserver = new VolumeObserver(new Handler());
        getContentResolver().registerContentObserver(
                Settings.System.CONTENT_URI, true, volumeObserver);

        // Partial wake lock для работы при выключенном экране
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Counter:WakeLock");
        wakeLock.acquire();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("Counter", "Service onStartCommand called");

        if ("RESET".equals(intent.getAction())) {
            Log.d("Counter", "RESET action");
            count = 0;
            updateNotification();
            return START_NOT_STICKY;
        }

        Log.d("Counter", "Creating notification...");
        Notification notification = createNotification();
        Log.d("Counter", "Notification created, calling startForeground...");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            Log.d("Counter", "API 34+, using typed startForeground");
            startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE);
        } else {
            Log.d("Counter", "Using standard startForeground");
            startForeground(NOTIFICATION_ID, notification);
        }
        Log.d("Counter", "startForeground SUCCESS!");

        previousVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        Log.d("Counter", "Service fully started");
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Notification createNotification() {
        Log.d("Counter", "createNotification called");
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Счётчик громкости")  // <-- Заголовок
                .setContentText("Текущий счёт: " + count)  // <-- Текст (было пусто!)
                .setSmallIcon(android.R.drawable.ic_media_play)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)  // <-- DEFAULT вместо LOW
                .setOngoing(true)
                .build();
    }

    private void updateNotification() {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(NOTIFICATION_ID, createNotification());
    }

    private class VolumeObserver extends ContentObserver {
        public VolumeObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            if (previousVolume != -1 && currentVolume != previousVolume) {
                if (currentVolume > previousVolume) {
                    count++;
                } else {
                    count--;
                }
                if (count < 0) count = 0;
                updateNotification();
                previousVolume = currentVolume;
            }
        }
    }

    @Override
    public void onDestroy() {
        getContentResolver().unregisterContentObserver(volumeObserver);
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
        }
        super.onDestroy();
    }
}