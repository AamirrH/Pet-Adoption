package com.example.clover;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class PetDetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_detail);

        long petId = getIntent().getLongExtra("pet_id", -1);
        if (petId == -1) { finish(); return; }

        DatabaseHelper db = new DatabaseHelper(this);
        Pet pet = db.getPetById(petId);
        if (pet == null) { finish(); return; }

        ((TextView) findViewById(R.id.tvDetailEmoji)).setText(pet.getEmoji());
        ((TextView) findViewById(R.id.tvDetailName)).setText(pet.getName());
        ((TextView) findViewById(R.id.tvDetailBreed)).setText(pet.getBreed());
        ((TextView) findViewById(R.id.tvDetailType)).setText(pet.getType());
        ((TextView) findViewById(R.id.tvDetailAge)).setText(pet.getAge());
        ((TextView) findViewById(R.id.tvDetailSize)).setText(pet.getSize());
        ((TextView) findViewById(R.id.tvDetailTemp)).setText(pet.getTemperament());
        ((TextView) findViewById(R.id.tvDetailDesc)).setText(pet.getDescription());

        Button btnAdopt = findViewById(R.id.btnAdopt);
        if (pet.isAdopted()) {
            btnAdopt.setEnabled(false);
            btnAdopt.setText("Already Adopted");
            btnAdopt.setBackgroundResource(R.drawable.btn_disabled);
        } else {
            btnAdopt.setOnClickListener(v -> {
                Intent intent = new Intent(this, AdoptionFormActivity.class);
                intent.putExtra("pet_id", pet.getId());
                intent.putExtra("pet_name", pet.getName());
                intent.putExtra("pet_emoji", pet.getEmoji());
                startActivity(intent);
            });
        }

        findViewById(R.id.btnSharePet).setOnClickListener(v -> {
            String text = "🐾 Check out " + pet.getName() + " on Clover!\n" +
                    pet.getEmoji() + " " + pet.getBreed() + "\n" +
                    "Age: " + pet.getAge() + " | Size: " + pet.getSize() + "\n" +
                    pet.getTemperament() + "\n\nAdopt via Clover App! 🌿";
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain");
            share.putExtra(Intent.EXTRA_TEXT, text);
            startActivity(Intent.createChooser(share, "Share via"));
        });

        findViewById(R.id.btnCall).setOnClickListener(v -> {
            startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(getString(R.string.shelter_phone))));
        });

        findViewById(R.id.btnLocate).setOnClickListener(v -> {
            Intent map = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=animal+shelter+near+me"));
            startActivity(map);
        });
    }
}
