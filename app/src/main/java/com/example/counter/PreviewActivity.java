package com.example.counter;

import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;


public class PreviewActivity extends AppCompatActivity {

    public static final String EXTRA_ACCENT = "extra_accent";
    public static final String EXTRA_BG = "extra_bg";
    public static final String EXTRA_TARGET = "extra_target"; // "accent" или "bg"

    private static final String PREFS = "counter_prefs";
    private static final String KEY_ACCENT = "accent_color";
    private static final String KEY_BG = "bg_color";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        View root = findViewById(R.id.root);
        MaterialButton btnIncrement = findViewById(R.id.btnIncrement);
        MaterialButton btnApply = findViewById(R.id.btnApply);
        MaterialButton btnCancel = findViewById(R.id.btnCancel);

        int accent = getIntent().getIntExtra(EXTRA_ACCENT, 0xFFB388FF);
        int bg = getIntent().getIntExtra(EXTRA_BG, 0xFF0F0F12);
        String target = getIntent().getStringExtra(EXTRA_TARGET);

        root.setBackgroundColor(bg);
        btnIncrement.setBackgroundTintList(ColorStateList.valueOf(accent));
        btnApply.setBackgroundTintList(ColorStateList.valueOf(accent));
        contrast(btnIncrement, btnApply, btnCancel, accent, bg, root);

        btnCancel.setOnClickListener(v -> finish());

        btnApply.setOnClickListener(v -> {
            SharedPreferences sp = getSharedPreferences(PREFS, MODE_PRIVATE);
            SharedPreferences.Editor e = sp.edit();

            // сохраняем оба, чтобы состояние было консистентным
            e.putInt(KEY_ACCENT, accent);
            e.putInt(KEY_BG, bg);
            e.apply(); // обычный способ сохранить настройки

            finish();
        });
    }

    private void contrast(MaterialButton btnIncrement, MaterialButton btnApply, MaterialButton btnCancel,
                          int accent, int bg, View root) {
        root.setBackgroundColor(bg);

        btnIncrement.setBackgroundTintList(ColorStateList.valueOf(accent));
        btnApply.setBackgroundTintList(ColorStateList.valueOf(accent));

// Контраст для текста на ЗАЛИТОЙ кнопке — от её фона (accent)
        int applyText = contrastTextColor(accent);
        btnApply.setTextColor(applyText);

// Контраст для OUTLINED — от фона экрана (bg)
        int cancelText = contrastTextColor(bg);
        btnCancel.setTextColor(cancelText);
        btnCancel.setStrokeColor(ColorStateList.valueOf(cancelText));
        btnCancel.setStrokeWidth(2);
    }

    private static boolean isDark(int color) {
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = (color) & 0xFF;
        double luminance = (0.2126 * r + 0.7152 * g + 0.0722 * b) / 255.0;
        return luminance < 0.5;
    }

    private static int contrastTextColor(int bg) {
        return isDark(bg) ? 0xFFFFFFFF : 0xFF000000;
    }

}

