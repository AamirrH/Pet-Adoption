package com.example.clover;

import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

/** P5: Explicit Intent (receive data), P6: AlertDialog, P8: SQLite SELECT/DELETE */
public class MyApplicationsActivity extends AppCompatActivity {
    private DatabaseHelper db;
    private LinearLayout appsList;
    private TextView tvEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_applications);
        db = new DatabaseHelper(this);
        appsList = findViewById(R.id.appsList);
        tvEmpty = findViewById(R.id.tvAppsEmpty);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadApplications();
    }

    private void loadApplications() {
        List<AdoptionApplication> apps = db.getAllApplications();
        appsList.removeAllViews();

        if (apps.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            findViewById(R.id.scrollApps).setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            findViewById(R.id.scrollApps).setVisibility(View.VISIBLE);

            for (AdoptionApplication app : apps) {
                View item = LayoutInflater.from(this).inflate(R.layout.item_application, appsList, false);

                String emoji = db.getPetEmojiByName(app.getPetName());
                ((TextView) item.findViewById(R.id.tvAppEmoji)).setText(emoji);
                ((TextView) item.findViewById(R.id.tvAppPetName)).setText(app.getPetName());
                ((TextView) item.findViewById(R.id.tvAppDate)).setText(app.getDate());

                TextView tvStatus = item.findViewById(R.id.tvAppStatus);
                tvStatus.setText(app.getStatus());
                switch (app.getStatus()) {
                    case "Pending":
                        tvStatus.setTextColor(getResources().getColor(R.color.pending_amber));
                        break;
                    case "Approved":
                        tvStatus.setTextColor(getResources().getColor(R.color.adopted_green));
                        break;
                    default:
                        tvStatus.setTextColor(getResources().getColor(R.color.rejected_red));
                }

                // P6: AlertDialog for withdraw
                item.findViewById(R.id.btnWithdraw).setOnClickListener(v -> {
                    new AlertDialog.Builder(this)
                            .setTitle(getString(R.string.dialog_withdraw_title))
                            .setMessage(String.format(getString(R.string.dialog_withdraw_msg), app.getPetName()))
                            .setPositiveButton(R.string.yes, (d, w) -> {
                                db.deleteApplication(app.getId());
                                Toast.makeText(this, getString(R.string.toast_withdrawn), Toast.LENGTH_SHORT).show();
                                loadApplications();
                            })
                            .setNegativeButton(R.string.cancel, (d, w) -> d.dismiss())
                            .show();
                });

                appsList.addView(item);
            }
        }
    }
}
