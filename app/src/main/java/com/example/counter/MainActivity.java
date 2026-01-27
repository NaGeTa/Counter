package com.example.counter;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.KeyEvent;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity {

    private static final String PREFS = "counter_prefs";
    private static final String KEY_COUNT = "count";

    private int count;
    private TextView tvCount;

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN
                && event.getRepeatCount() == 0) {

            int keyCode = event.getKeyCode();
            if (keyCode == KeyEvent.KEYCODE_VOLUME_UP
                    || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {

                count++;
                render();
                animateIncrement();
                getSharedPreferences(PREFS, MODE_PRIVATE)
                        .edit().putInt(KEY_COUNT, count).apply();

                return true;
            }
        } else if (event.getRepeatCount() > 0) {

            return true;
        }

        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvCount = findViewById(R.id.tvCount);
        MaterialButton btnIncrement = findViewById(R.id.btnIncrement);
        MaterialButton btnReset = findViewById(R.id.btnReset);

        SharedPreferences sp = getSharedPreferences(PREFS, MODE_PRIVATE);
        count = sp.getInt(KEY_COUNT, 0);
        render();
        animateIncrement();

        btnIncrement.setOnClickListener(v -> {
            count++;
            render();
            animateIncrement();
            sp.edit().putInt(KEY_COUNT, count).apply();
        });

        btnReset.setOnClickListener(v -> {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Сбросить счётчик?")
                    .setMessage("Текущее значение будет сброшено на 0.")
                    .setPositiveButton("Сбросить", (dialog, which) -> {
                        count = 0;
                        render();
                        animateIncrement();
                        sp.edit().putInt(KEY_COUNT, count).apply();
                    })
                    .setNegativeButton("Отмена", (dialog, which) -> dialog.dismiss())
                    .show();
        });
    }

    private void render() {
        tvCount.setText(String.valueOf(count));
    }

    private void animateIncrement() {
        // лёгкий "памп" на кнопке
        findViewById(R.id.btnIncrement).animate()
                .scaleX(0.92f).scaleY(0.92f)
                .setDuration(70)
                .withEndAction(() -> findViewById(R.id.btnIncrement).animate()
                        .scaleX(1f).scaleY(1f)
                        .setDuration(120)
                        .start())
                .start();

        // haptic feedback
        findViewById(R.id.btnIncrement)
                .performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
    }
}
