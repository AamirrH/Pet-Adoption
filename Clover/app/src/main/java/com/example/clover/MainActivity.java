package com.example.clover;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "CloverLifecycle";
    private DatabaseHelper db;
    private LinearLayout petList;
    private TextView tvEmpty, tvUserName, tvPetCount, tvAppCount, tvNotifBadge;
    private Spinner spinnerFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "MainActivity: onCreate()");
        setContentView(R.layout.activity_main);

        db = new DatabaseHelper(this);
        petList = findViewById(R.id.petList);
        tvEmpty = findViewById(R.id.tvEmpty);
        tvUserName = findViewById(R.id.tvUserName);
        tvPetCount = findViewById(R.id.tvPetCount);
        tvAppCount = findViewById(R.id.tvAppCount);
        tvNotifBadge = findViewById(R.id.tvNotifBadge);
        spinnerFilter = findViewById(R.id.spinnerFilter);

        SharedPreferences prefs = getSharedPreferences("CloverPrefs", MODE_PRIVATE);
        tvUserName.setText(prefs.getString("user_name", "User"));

        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> p, View v, int pos, long id) { loadPets(p.getItemAtPosition(pos).toString()); }
            @Override public void onNothingSelected(AdapterView<?> p) { loadPets("All"); }
        });

        findViewById(R.id.navHome).setOnClickListener(v -> {});
        findViewById(R.id.navApps).setOnClickListener(v -> startActivity(new Intent(this, MyApplicationsActivity.class)));
        findViewById(R.id.navQuiz).setOnClickListener(v -> startActivity(new Intent(this, QuizActivity.class)));
        findViewById(R.id.navAdmin).setOnClickListener(v -> startActivity(new Intent(this, AdminPanelActivity.class)));
        findViewById(R.id.navSettings).setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));

        // Notification bell click
        findViewById(R.id.btnNotifBell).setOnClickListener(v -> startActivity(new Intent(this, NotificationsActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "MainActivity: onResume()");
        SharedPreferences prefs = getSharedPreferences("CloverPrefs", MODE_PRIVATE);
        tvUserName.setText(prefs.getString("user_name", "User"));
        tvPetCount.setText(String.valueOf(db.getAvailableCount()));
        tvAppCount.setText(String.valueOf(db.getApplicationCount()));
        String filter = spinnerFilter.getSelectedItem().toString();
        loadPets(filter);

        // Update notification badge
        updateNotifBadge();
    }

    private void updateNotifBadge() {
        int unread = db.getUnreadNotificationCount();
        if (unread > 0) {
            tvNotifBadge.setVisibility(View.VISIBLE);
            tvNotifBadge.setText(unread > 9 ? "9+" : String.valueOf(unread));
        } else {
            tvNotifBadge.setVisibility(View.GONE);
        }
    }

    private void loadPets(String filter) {
        List<Pet> pets = filter.equals("All") ? db.getAllPets() : db.getPetsByType(filter);
        petList.removeAllViews();

        if (pets.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            findViewById(R.id.scrollPets).setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            findViewById(R.id.scrollPets).setVisibility(View.VISIBLE);
            for (Pet pet : pets) {
                View item = LayoutInflater.from(this).inflate(R.layout.item_pet_card, petList, false);
                ((TextView) item.findViewById(R.id.tvPetEmoji)).setText(pet.getEmoji());
                ((TextView) item.findViewById(R.id.tvPetName)).setText(pet.getName());
                ((TextView) item.findViewById(R.id.tvPetBreed)).setText(pet.getBreed());
                ((TextView) item.findViewById(R.id.tvPetMeta)).setText(pet.getAge() + " • " + pet.getSize());

                TextView status = item.findViewById(R.id.tvPetStatus);
                if (pet.isAdopted()) {
                    status.setText("Adopted ✓");
                    status.setTextColor(getResources().getColor(R.color.adopted_green));
                } else {
                    status.setText("Available");
                    status.setTextColor(getResources().getColor(R.color.primary));
                }

                item.setOnClickListener(v -> {
                    Intent intent = new Intent(this, PetDetailActivity.class);
                    intent.putExtra("pet_id", pet.getId());
                    startActivity(intent);
                });
                petList.addView(item);
            }
        }
    }

    @Override protected void onPause() { super.onPause(); Log.d(TAG, "MainActivity: onPause()"); }
    @Override protected void onStop() { super.onStop(); Log.d(TAG, "MainActivity: onStop()"); }
    @Override protected void onDestroy() { super.onDestroy(); Log.d(TAG, "MainActivity: onDestroy()"); }
}