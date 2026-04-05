package com.example.clover;

import android.app.*;
import android.content.*;
import android.os.Build;
import android.os.Bundle;
import android.text.*;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import java.text.SimpleDateFormat;
import java.util.*;

public class AdoptionFormActivity extends AppCompatActivity {
    private static final String CH_ID = "clover_channel";
    private EditText etName, etPhone, etAddress, etReason;
    private RadioGroup rgHousing, rgOtherPets;
    private Spinner spinnerExp;
    private Button btnSubmit;
    private long petId;
    private String petName, petEmoji;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adoption_form);

        petId = getIntent().getLongExtra("pet_id", -1);
        petName = getIntent().getStringExtra("pet_name");
        petEmoji = getIntent().getStringExtra("pet_emoji");

        ((TextView) findViewById(R.id.tvAdoptEmoji)).setText(petEmoji);
        ((TextView) findViewById(R.id.tvAdoptName)).setText(petName);

        etName = findViewById(R.id.etAdoptName);
        etPhone = findViewById(R.id.etAdoptPhone);
        etAddress = findViewById(R.id.etAdoptAddress);
        etReason = findViewById(R.id.etReason);
        rgHousing = findViewById(R.id.rgHousing);
        rgOtherPets = findViewById(R.id.rgOtherPets);
        spinnerExp = findViewById(R.id.spinnerExp);
        btnSubmit = findViewById(R.id.btnSubmit);

        SharedPreferences prefs = getSharedPreferences("CloverPrefs", MODE_PRIVATE);
        etName.setText(prefs.getString("user_name", ""));
        etPhone.setText(prefs.getString("user_phone", ""));

        createNotificationChannel();

        TextWatcher watcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int a, int b, int c) {}
            @Override public void onTextChanged(CharSequence s, int a, int b, int c) {}
            @Override public void afterTextChanged(Editable s) { validateForm(); }
        };
        etName.addTextChangedListener(watcher);
        etPhone.addTextChangedListener(watcher);
        etAddress.addTextChangedListener(watcher);
        etReason.addTextChangedListener(watcher);
        rgHousing.setOnCheckedChangeListener((g, id) -> validateForm());
        rgOtherPets.setOnCheckedChangeListener((g, id) -> validateForm());

        btnSubmit.setOnClickListener(v -> showConfirmDialog());
    }

    private void validateForm() {
        boolean valid = !etName.getText().toString().trim().isEmpty()
                && etPhone.getText().toString().trim().length() == 10
                && !etAddress.getText().toString().trim().isEmpty()
                && rgHousing.getCheckedRadioButtonId() != -1
                && rgOtherPets.getCheckedRadioButtonId() != -1
                && etReason.getText().toString().trim().length() >= 10;

        btnSubmit.setEnabled(valid);
        btnSubmit.setBackgroundResource(valid ? R.drawable.btn_primary : R.drawable.btn_disabled);
    }

    private void showConfirmDialog() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.dialog_confirm_title))
                .setMessage(String.format(getString(R.string.dialog_confirm_msg), petName))
                .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton(R.string.yes, (d, w) -> submitApplication())
                .setNegativeButton(R.string.cancel, (d, w) -> d.dismiss())
                .show();
    }

    private void submitApplication() {
        DatabaseHelper db = new DatabaseHelper(this);

        AdoptionApplication app = new AdoptionApplication();
        app.setPetId(petId);
        app.setPetName(petName);
        app.setAdopterName(etName.getText().toString().trim());
        app.setPhone(etPhone.getText().toString().trim());
        app.setAddress(etAddress.getText().toString().trim());

        RadioButton rbHousing = findViewById(rgHousing.getCheckedRadioButtonId());
        app.setHousingType(rbHousing.getText().toString());

        RadioButton rbPets = findViewById(rgOtherPets.getCheckedRadioButtonId());
        app.setHasOtherPets(rbPets.getText().toString());

        app.setExperience(spinnerExp.getSelectedItem().toString());
        app.setReason(etReason.getText().toString().trim());
        app.setDate(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));

        long id = db.insertApplication(app);
        if (id > 0) {
            db.markAdopted(petId);
            Toast.makeText(this, getString(R.string.toast_submitted), Toast.LENGTH_SHORT).show();
            sendNotification();
            finish();
        }
    }

    private void sendNotification() {
        Intent intent = new Intent(this, MyApplicationsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CH_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_email)
                .setContentTitle(getString(R.string.notif_submitted_title))
                .setContentText(String.format(getString(R.string.notif_submitted_text), petName))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pi)
                .setAutoCancel(true);

        ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).notify(1, builder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel ch = new NotificationChannel(CH_ID, "Adoption Alerts", NotificationManager.IMPORTANCE_HIGH);
            ch.setDescription("Notifications for adoption applications");
            ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).createNotificationChannel(ch);
        }
    }
}
