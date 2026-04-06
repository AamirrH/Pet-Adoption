package com.example.clover;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.*;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class RegistrationActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        EditText etName = findViewById(R.id.etRegName);
        EditText etPhone = findViewById(R.id.etRegPhone);
        EditText etCity = findViewById(R.id.etRegCity);
        Spinner spinnerPref = findViewById(R.id.spinnerPref);
        Button btnGetStarted = findViewById(R.id.btnGetStarted);

        TextView tvNameError = findViewById(R.id.tvRegNameError);
        TextView tvPhoneError = findViewById(R.id.tvRegPhoneError);
        TextView tvCityError = findViewById(R.id.tvRegCityError);

        // Name: auto-filter to only letters and spaces
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

        // Phone: enforce digits only, max 10
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

        btnGetStarted.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String city = etCity.getText().toString().trim();

            boolean valid = true;

            // Name validation
            if (name.isEmpty()) {
                tvNameError.setText("⚠ Name is required");
                tvNameError.setVisibility(android.view.View.VISIBLE);
                etName.requestFocus();
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
                if (valid) etPhone.requestFocus();
                valid = false;
            } else if (phone.length() != 10) {
                tvPhoneError.setText("⚠ Must be exactly 10 digits (" + phone.length() + "/10)");
                tvPhoneError.setVisibility(android.view.View.VISIBLE);
                if (valid) etPhone.requestFocus();
                valid = false;
            } else {
                tvPhoneError.setVisibility(android.view.View.GONE);
            }

            // City validation
            if (city.isEmpty()) {
                tvCityError.setText("⚠ City is required");
                tvCityError.setVisibility(android.view.View.VISIBLE);
                if (valid) etCity.requestFocus();
                valid = false;
            } else if (city.length() < 2) {
                tvCityError.setText("⚠ City must be at least 2 characters");
                tvCityError.setVisibility(android.view.View.VISIBLE);
                valid = false;
            } else {
                tvCityError.setVisibility(android.view.View.GONE);
            }

            if (!valid) return;

            SharedPreferences.Editor editor = getSharedPreferences("CloverPrefs", MODE_PRIVATE).edit();
            editor.putBoolean("is_first_launch", false);
            editor.putString("user_name", name);
            editor.putString("user_phone", phone);
            editor.putString("user_city", city);
            editor.putString("pet_preference", spinnerPref.getSelectedItem().toString());
            editor.apply();

            Toast.makeText(this, "Welcome to Clover, " + name + "! 🐾", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
    }
}
