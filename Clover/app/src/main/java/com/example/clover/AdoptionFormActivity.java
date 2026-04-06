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
    private EditText etName, etPhone, etReason;
    private AutoCompleteTextView etAddress;
    private TextView tvDate, tvNameError, tvPhoneError, tvAddressError, tvDateError, tvHousingError, tvPetsError, tvReasonError;
    private RadioGroup rgHousing, rgOtherPets;
    private Spinner spinnerExp;
    private Button btnSubmit, btnPickDate;
    private long petId;
    private String petName, petEmoji;
    private String selectedDate = "";

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
        tvDate = findViewById(R.id.tvSelectedDate);
        btnPickDate = findViewById(R.id.btnPickDate);
        rgHousing = findViewById(R.id.rgHousing);
        rgOtherPets = findViewById(R.id.rgOtherPets);
        spinnerExp = findViewById(R.id.spinnerExp);
        btnSubmit = findViewById(R.id.btnSubmit);

        // Error TextViews
        tvNameError = findViewById(R.id.tvNameError);
        tvPhoneError = findViewById(R.id.tvPhoneError);
        tvAddressError = findViewById(R.id.tvAddressError);
        tvDateError = findViewById(R.id.tvDateError);
        tvHousingError = findViewById(R.id.tvHousingError);
        tvPetsError = findViewById(R.id.tvPetsError);
        tvReasonError = findViewById(R.id.tvReasonError);

        // Pre-fill from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("CloverPrefs", MODE_PRIVATE);
        etName.setText(prefs.getString("user_name", ""));
        etPhone.setText(prefs.getString("user_phone", ""));

        // AutoComplete for address with city suggestions
        DatabaseHelper db = new DatabaseHelper(this);
        List<String> cities = db.getAllCityNames();
        // Add default city suggestions
        String[] defaultCities = {"Mumbai", "Delhi", "Bangalore", "Chennai", "Kolkata", "Hyderabad", "Pune", "Ahmedabad", "Jaipur", "Lucknow"};
        for (String city : defaultCities) {
            if (!cities.contains(city)) cities.add(city);
        }
        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, cities);
        etAddress.setAdapter(cityAdapter);
        etAddress.setThreshold(1); // Show suggestions after 1 character

        createNotificationChannel();

        // Date Picker Button
        btnPickDate.setOnClickListener(v -> showDatePicker());

        // Text validation watchers
        TextWatcher watcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int a, int b, int c) {}
            @Override public void onTextChanged(CharSequence s, int a, int b, int c) {}
            @Override public void afterTextChanged(Editable s) { validateForm(); }
        };
        etName.addTextChangedListener(watcher);
        etPhone.addTextChangedListener(watcher);
        etAddress.addTextChangedListener(watcher);
        etReason.addTextChangedListener(watcher);

        // Name: only letters and spaces (auto-correct)
        etName.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int a, int b, int c) {}
            @Override public void onTextChanged(CharSequence s, int a, int b, int c) {}
            @Override public void afterTextChanged(Editable s) {
                String text = s.toString();
                String filtered = text.replaceAll("[^a-zA-Z\\s]", "");
                if (!text.equals(filtered)) {
                    etName.removeTextChangedListener(this);
                    etName.setText(filtered);
                    etName.setSelection(filtered.length());
                    etName.addTextChangedListener(this);
                }
            }
        });

        // Phone: only digits, max 10
        etPhone.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
        etPhone.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int a, int b, int c) {}
            @Override public void onTextChanged(CharSequence s, int a, int b, int c) {}
            @Override public void afterTextChanged(Editable s) {
                String text = s.toString();
                String filtered = text.replaceAll("[^0-9]", "");
                if (!text.equals(filtered)) {
                    etPhone.removeTextChangedListener(this);
                    etPhone.setText(filtered);
                    etPhone.setSelection(filtered.length());
                    etPhone.addTextChangedListener(this);
                }
            }
        });

        rgHousing.setOnCheckedChangeListener((g, id) -> validateForm());
        rgOtherPets.setOnCheckedChangeListener((g, id) -> validateForm());

        btnSubmit.setOnClickListener(v -> showConfirmDialog());
    }

    private void showDatePicker() {
        Calendar cal = Calendar.getInstance();
        DatePickerDialog dpd = new DatePickerDialog(this, (view, year, month, day) -> {
            selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, day);
            tvDate.setText("📅 " + selectedDate);
            tvDate.setTextColor(getResources().getColor(R.color.text_primary));
            if (tvDateError != null) tvDateError.setVisibility(android.view.View.GONE);
            validateForm();
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        dpd.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        dpd.show();
    }

    private void validateForm() {
        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String reason = etReason.getText().toString().trim();
        boolean housingSelected = rgHousing.getCheckedRadioButtonId() != -1;
        boolean petsSelected = rgOtherPets.getCheckedRadioButtonId() != -1;

        boolean valid = true;

        // Name validation
        if (name.isEmpty()) {
            tvNameError.setText("⚠ Name is required");
            tvNameError.setVisibility(android.view.View.VISIBLE);
            valid = false;
        } else if (name.length() < 2) {
            tvNameError.setText("⚠ Name must be at least 2 characters");
            tvNameError.setVisibility(android.view.View.VISIBLE);
            valid = false;
        } else {
            tvNameError.setVisibility(android.view.View.GONE);
        }

        // Phone validation
        if (phone.isEmpty()) {
            tvPhoneError.setText("⚠ Phone number is required");
            tvPhoneError.setVisibility(android.view.View.VISIBLE);
            valid = false;
        } else if (phone.length() != 10) {
            tvPhoneError.setText("⚠ Phone must be exactly 10 digits (" + phone.length() + "/10)");
            tvPhoneError.setVisibility(android.view.View.VISIBLE);
            valid = false;
        } else {
            tvPhoneError.setVisibility(android.view.View.GONE);
        }

        // Address validation
        if (address.isEmpty()) {
            tvAddressError.setText("⚠ Address is required");
            tvAddressError.setVisibility(android.view.View.VISIBLE);
            valid = false;
        } else if (address.length() < 5) {
            tvAddressError.setText("⚠ Address too short (min 5 chars)");
            tvAddressError.setVisibility(android.view.View.VISIBLE);
            valid = false;
        } else {
            tvAddressError.setVisibility(android.view.View.GONE);
        }

        // Date validation
        if (selectedDate.isEmpty()) {
            tvDateError.setText("⚠ Please select a preferred date");
            tvDateError.setVisibility(android.view.View.VISIBLE);
            valid = false;
        } else {
            tvDateError.setVisibility(android.view.View.GONE);
        }

        // Housing validation
        if (!housingSelected) {
            tvHousingError.setText("⚠ Select your housing type");
            tvHousingError.setVisibility(android.view.View.VISIBLE);
            valid = false;
        } else {
            tvHousingError.setVisibility(android.view.View.GONE);
        }

        // Other pets validation
        if (!petsSelected) {
            tvPetsError.setText("⚠ Select if you have other pets");
            tvPetsError.setVisibility(android.view.View.VISIBLE);
            valid = false;
        } else {
            tvPetsError.setVisibility(android.view.View.GONE);
        }

        // Reason validation
        if (reason.isEmpty()) {
            tvReasonError.setText("⚠ Reason is required");
            tvReasonError.setVisibility(android.view.View.VISIBLE);
            valid = false;
        } else if (reason.length() < 10) {
            tvReasonError.setText("⚠ Reason too short (" + reason.length() + "/10 chars min)");
            tvReasonError.setVisibility(android.view.View.VISIBLE);
            valid = false;
        } else {
            tvReasonError.setVisibility(android.view.View.GONE);
        }

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
        app.setDate(selectedDate);

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
