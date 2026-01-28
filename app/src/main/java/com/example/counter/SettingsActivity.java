package com.example.counter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity {

    private static final String PREFS = "counter_prefs";
    static final String KEY_ACCENT = "accent_color";
    static final String KEY_BG = "bg_color";
    static final String KEY_HOLD = "hold_to_count";
    static final String KEY_DECREMENT = "decrement";

    private SharedPreferences sp;

    private final Map<String, Integer> colors;

    {
        colors = new LinkedHashMap<>();
        colors.put("Белый", 0xFFffffff);
        colors.put("Черный", 0xFF000000);
        colors.put("Темно-серый", 0xFF2f3133);
        colors.put("Фиолетовый", 0xFF120a8f);
        colors.put("Зелёный", 0xFF66BB6A);
        colors.put("Синий", 0xFF003153);
        colors.put("Красный", 0xFFe63619);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sp = getSharedPreferences(PREFS, MODE_PRIVATE);

        MaterialButton btnPickAccent = findViewById(R.id.btnPickAccent);
        MaterialButton btnPickBackground = findViewById(R.id.btnPickBackground);
        SwitchMaterial swHoldToCount = findViewById(R.id.swHoldToCount);
        SwitchMaterial swDecrement = findViewById(R.id.swDecrement);

        swHoldToCount.setChecked(sp.getBoolean(KEY_HOLD, false));
        swHoldToCount.setOnCheckedChangeListener((buttonView, isChecked) ->
                sp.edit().putBoolean(KEY_HOLD, isChecked).apply()
        );

        swDecrement.setChecked(sp.getBoolean(KEY_DECREMENT, false));
        swDecrement.setOnCheckedChangeListener((buttonView, isChecked) ->
                sp.edit().putBoolean(KEY_DECREMENT, isChecked).apply()
        );

        btnPickAccent.setOnClickListener(v ->
                showColorDialog("Цвет компонентов", KEY_ACCENT));

        btnPickBackground.setOnClickListener(v ->
                showColorDialog("Цвет фона", KEY_BG));
    }

    private void showColorDialog(String title, String prefKey) {
        List<String> names = new ArrayList<>(colors.keySet());
        List<Integer> values = new ArrayList<>();
        for (String n : names) values.add(colors.get(n));

        ColorListAdapter adapter = new ColorListAdapter(this, names, values);

        new AlertDialog.Builder(this)
                .setTitle(title)
                .setAdapter(adapter, (dialog, which) -> {
                    int selectedColor = values.get(which);
                    openPreview(prefKey, selectedColor);
                })
                .setNegativeButton("Отмена", (d, w) -> d.dismiss())
                .show();
    }

    private void openPreview(String prefKey, int selectedColor) {
        int accent = sp.getInt(KEY_ACCENT, 0xFFB388FF);
        int bg = sp.getInt(KEY_BG, 0xFF0F0F12);

        if (KEY_ACCENT.equals(prefKey)) {
            accent = selectedColor;
        } else if (KEY_BG.equals(prefKey)) {
            bg = selectedColor;
        }

        Intent i = new Intent(this, PreviewActivity.class);
        i.putExtra(PreviewActivity.EXTRA_ACCENT, accent);
        i.putExtra(PreviewActivity.EXTRA_BG, bg);
        i.putExtra(PreviewActivity.EXTRA_TARGET, prefKey);
        startActivity(i);
    }
}

