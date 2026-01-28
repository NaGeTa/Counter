package com.example.counter;

import static com.example.counter.SettingsActivity.KEY_ACCENT;
import static com.example.counter.SettingsActivity.KEY_BG;
import static com.example.counter.SettingsActivity.KEY_DECREMENT;
import static com.example.counter.SettingsActivity.KEY_HOLD;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
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
        SharedPreferences sp = getSharedPreferences(PREFS, MODE_PRIVATE);
        boolean holdToCount = sp.getBoolean(KEY_HOLD, false);

        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            int keyCode = event.getKeyCode();
            if (keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {

                // Если holdToCount выключен — принимаем только repeatCount == 0
                if (!holdToCount && event.getRepeatCount() != 0) {
                    return true; // съедаем повторы, но не считаем
                }

                return setCount(sp, keyCode);
            }
        }
        return super.dispatchKeyEvent(event);
    }


    private boolean setCount(SharedPreferences sp, int keyCode) {
        boolean decrementEnable = sp.getBoolean(KEY_DECREMENT, false);

        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP || !decrementEnable) {
            count++;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            count--;
        }

        render();
        animateIncrement();
        sp.edit().putInt(KEY_COUNT, count).apply();

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        applyUiPrefs();

        tvCount = findViewById(R.id.tvCount);
        MaterialButton btnIncrement = findViewById(R.id.btnIncrement);
        MaterialButton btnReset = findViewById(R.id.btnReset);
        MaterialButton btnSettings = findViewById(R.id.btnSettings);

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

        btnReset.setOnClickListener(v -> new AlertDialog.Builder(MainActivity.this)
                .setTitle("Сбросить счётчик?")
                .setMessage("Текущее значение будет сброшено на 0.")
                .setPositiveButton("Сбросить", (dialog, which) -> {
                    count = 0;
                    render();
                    animateIncrement();
                    sp.edit().putInt(KEY_COUNT, count).apply();
                })
                .setNegativeButton("Отмена", (dialog, which) -> dialog.dismiss())
                .show());

        btnSettings.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, SettingsActivity.class))
        );
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

        findViewById(R.id.btnIncrement)
                .performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
    }

    @Override
    protected void onResume() {
        super.onResume();
        applyUiPrefs();
    }

    private void applyUiPrefs() {
        SharedPreferences sp = getSharedPreferences(PREFS, MODE_PRIVATE);

        int accent = sp.getInt(KEY_ACCENT, 0xFFB388FF);
        int bg = sp.getInt(KEY_BG, 0xFF0F0F12);

        findViewById(android.R.id.content).setBackgroundColor(bg);

        MaterialButton btnIncrement = findViewById(R.id.btnIncrement);
        MaterialButton btnReset = findViewById(R.id.btnReset);
        MaterialButton btnSettings = findViewById(R.id.btnSettings);

        btnIncrement.setBackgroundTintList(ColorStateList.valueOf(accent));
        btnSettings.setStrokeColor(ColorStateList.valueOf(accent));
        btnSettings.setIconTint(ColorStateList.valueOf(accent));
        btnReset.setStrokeColor(ColorStateList.valueOf(accent));
        btnReset.setIconTint(ColorStateList.valueOf(accent));
        btnReset.setTextColor(accent);
    }
}
