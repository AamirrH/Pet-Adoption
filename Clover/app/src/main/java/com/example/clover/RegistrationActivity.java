package com.example.clover;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

/** P2: Nested LinearLayout, P3: Event Handler, P4: Validation, P7: SharedPreferences */
public class RegistrationActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        EditText etName = findViewById(R.id.etRegName);
        EditText etPhone = findViewById(R.id.etRegPhone);
        EditText etCity = findViewById(R.id.etRegCity);
        Spinner spinnerPref = findViewById(R.id.spinnerPref);

        findViewById(R.id.btnGetStarted).setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String city = etCity.getText().toString().trim();

            if (name.isEmpty()) { etName.setError("Enter your name"); etName.requestFocus(); return; }
            if (phone.isEmpty() || phone.length() != 10) { etPhone.setError("Enter valid 10-digit phone"); etPhone.requestFocus(); return; }
            if (city.isEmpty()) { etCity.setError("Enter your city"); etCity.requestFocus(); return; }

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
