package com.example.counter;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private static final String CHANNEL_ID = "CounterChannel";
    private static final int NOTIFICATION_ID = 1;
    private TextView tvCounter;
    private static int currentCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        createNotificationChannel();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvCounter = findViewById(R.id.tvCounter);
        if (savedInstanceState != null) {
            currentCount = savedInstanceState.getInt("count", 0);
        }
        tvCounter.setText(String.valueOf(currentCount));

        Button btnStart = findViewById(R.id.btnStart);
        btnStart.setOnClickListener(v -> {
            Log.d("Counter", "Start button clicked");  // <-- ДОБАВЬ
            Intent serviceIntent = new Intent(this, CounterService.class);
            startForegroundService(serviceIntent);  // <-- ОБЯЗАТЕЛЬНО!
            Log.d("Counter", "Service intent sent");  // <-- ДОБАВЬ
        });

        Button btnStop = findViewById(R.id.btnStop);
        btnStop.setOnClickListener(v -> {
            Intent serviceIntent = new Intent(this, CounterService.class);
            stopService(serviceIntent);
        });

        Button btnReset = findViewById(R.id.btnReset);
        btnReset.setOnClickListener(v -> {
            currentCount = 0;
            tvCounter.setText("0");
            Intent resetIntent = new Intent(this, CounterService.class);
            resetIntent.setAction("RESET");
            startService(resetIntent);  // Отправляем reset в сервис
        });
    }

    private void createNotificationChannel() {
        Log.d("Counter", "Creating notification channel...");
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID, "Counter Service",
                NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription("Счетчик по громкости");
        channel.setShowBadge(false);  // Без бейджа
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);
        Log.d("Counter", "Channel created!");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("count", currentCount);
    }

    public static void updateCount(int count) {
        currentCount = count;
    }
}
