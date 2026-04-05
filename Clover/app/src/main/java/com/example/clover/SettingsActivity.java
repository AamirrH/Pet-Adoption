package com.example.clover;

import android.content.*;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class SettingsActivity extends AppCompatActivity {
    private EditText etName, etPhone, etCity;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        db = new DatabaseHelper(this);
        etName = findViewById(R.id.etSetName);
        etPhone = findViewById(R.id.etSetPhone);
        etCity = findViewById(R.id.etSetCity);

        SharedPreferences prefs = getSharedPreferences("CloverPrefs", MODE_PRIVATE);
        etName.setText(prefs.getString("user_name", ""));
        etPhone.setText(prefs.getString("user_phone", ""));
        etCity.setText(prefs.getString("user_city", ""));

        findViewById(R.id.btnSaveProfile).setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            if (name.isEmpty()) { etName.setError("Required"); return; }
            SharedPreferences.Editor ed = prefs.edit();
            ed.putString("user_name", name);
            ed.putString("user_phone", etPhone.getText().toString().trim());
            ed.putString("user_city", etCity.getText().toString().trim());
            ed.apply();
            Toast.makeText(this, getString(R.string.toast_profile_saved), Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.btnExport).setOnClickListener(v -> exportCertificate());

        findViewById(R.id.btnShare).setOnClickListener(v -> shareReport());

        findViewById(R.id.btnReset).setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.dialog_reset_title))
                    .setMessage(getString(R.string.dialog_reset_msg))
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(R.string.yes, (d, w) -> {
                        db.deleteAll();
                        prefs.edit().clear().putBoolean("is_first_launch", true).apply();
                        Toast.makeText(this, getString(R.string.toast_reset), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this, SplashActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent); finish();
                    })
                    .setNegativeButton(R.string.cancel, (d, w) -> d.dismiss())
                    .show();
        });
    }

    private void exportCertificate() {
        List<AdoptionApplication> apps = db.getAllApplications();
        StringBuilder sb = new StringBuilder();
        sb.append("🌿═══════════════════════════════🌿\n");
        sb.append("     CLOVER ADOPTION CERTIFICATE    \n");
        sb.append("🌿═══════════════════════════════🌿\n");
        sb.append("Generated: ").append(new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date())).append("\n\n");

        if (apps.isEmpty()) {
            sb.append("No adoption applications yet.\n");
        } else {
            for (AdoptionApplication app : apps) {
                sb.append("🐾 Pet: ").append(app.getPetName()).append("\n");
                sb.append("   Adopter: ").append(app.getAdopterName()).append("\n");
                sb.append("   Date: ").append(app.getDate()).append("\n");
                sb.append("   Status: ").append(app.getStatus()).append("\n");
                sb.append("   ─────────────────────────\n");
            }
        }
        sb.append("\n🌿 Powered by Clover 🐾\n");

        try {
            FileOutputStream fos = openFileOutput("clover_certificate.txt", Context.MODE_PRIVATE);
            fos.write(sb.toString().getBytes());
            fos.close();
            Toast.makeText(this, getString(R.string.toast_exported), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(this, "Export failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void shareReport() {
        List<AdoptionApplication> apps = db.getAllApplications();
        String text = "🌿 Clover — My Adoption Journey\n" +
                "━━━━━━━━━━━━━━━━━━━\n" +
                "Total Applications: " + apps.size() + "\n";
        for (AdoptionApplication app : apps) {
            text += "🐾 " + app.getPetName() + " — " + app.getStatus() + "\n";
        }
        text += "━━━━━━━━━━━━━━━━━━━\nTracked with Clover 🌿";

        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_SUBJECT, "My Clover Adoption Journey");
        share.putExtra(Intent.EXTRA_TEXT, text);
        startActivity(Intent.createChooser(share, "Share via"));
    }
}
