package com.example.maternalmindavr;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import com.example.maternalmindavr.activities.AssessmentActivity;
import com.example.maternalmindavr.activities.CognitiveTestActivity;
import com.example.maternalmindavr.activities.HelpActivity;
import com.example.maternalmindavr.activities.InsightsActivity;
import com.example.maternalmindavr.activities.MoodActivity;
import com.example.maternalmindavr.activities.PrivacyActivity;
import com.example.maternalmindavr.utils.BackgroundSyncWorker;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String SYNC_WORK_NAME = "MaternalMindSyncWork";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Handle window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        setupNavigation();
        scheduleBackgroundWork();
        setupDashboardCards();
    }

    private void setupNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) return true;
            if (id == R.id.nav_insights) {
                startActivity(new Intent(this, InsightsActivity.class));
                return true;
            }
            if (id == R.id.nav_assessment) {
                startActivity(new Intent(this, AssessmentActivity.class));
                return true;
            }
            if (id == R.id.nav_profile) {
                startActivity(new Intent(this, PrivacyActivity.class));
                return true;
            }
            return false;
        });
    }

    private void setupDashboardCards() {
        // Mood Check-in
        findViewById(R.id.cardMood).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, MoodActivity.class));
        });

        // Help Card
        findViewById(R.id.cardHelp).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, HelpActivity.class));
        });

        // Cognitive Test Card (New)
        findViewById(R.id.cardCognitive).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, CognitiveTestActivity.class));
        });
        
        // Insights Quick Access
        findViewById(R.id.cardInsightsSummary).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, InsightsActivity.class));
        });
    }

    private void scheduleBackgroundWork() {
        PeriodicWorkRequest syncRequest = new PeriodicWorkRequest.Builder(
                BackgroundSyncWorker.class, 
                24, TimeUnit.HOURS)
                .build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                SYNC_WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                syncRequest
        );
        Log.d(TAG, "Background sync scheduled (Periodic 24h)");
    }
}
