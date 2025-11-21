package com.example.mukrathamaster;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {

    private int score = 0;
    private int[] meatStatus = {0, 0, 0, 0};
    private String[] currentMeatType = {"none", "none", "none", "none"};

    private boolean isPaused = false;
    private int totalGrilled = 0;
    private int burntCount = 0;
    private int happiness = 100;

    // --- ตัวแปรสำหรับจับเวลา ---
    private CountDownTimer countDownTimer;
    private long timeLeftInMillis = 60000;
    private boolean isTimerRunning;
    // -----------------------

    // UI
    private TextView tvScore;
    private TextView tvTimer;
    private ImageView[] slots = new ImageView[4];
    private ImageView btnPork, btnChicken, btnBeef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvScore = findViewById(R.id.tvScore);
        tvTimer = findViewById(R.id.tvTimer);
        btnPork = findViewById(R.id.btnPork);
        btnChicken = findViewById(R.id.btnChicken);
        btnBeef = findViewById(R.id.btnBeef);

        slots[0] = findViewById(R.id.slot0);
        slots[1] = findViewById(R.id.slot1);
        slots[2] = findViewById(R.id.slot2);
        slots[3] = findViewById(R.id.slot3);

        for (int i = 0; i < 4; i++) {
            final int index = i;
            slots[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkFoodStatus(index);
                }
            });
        }

        boolean isContinue = getIntent().getBooleanExtra("IS_CONTINUE", false);
        if (isContinue) {
            loadGameProgress();
        } else {
            getSharedPreferences("GameData", MODE_PRIVATE).edit().putBoolean("HAS_SAVE", false).apply();
            startTimer();
        }

        // --- ปุ่มต่าง ๆ ---
        btnPork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPaused) return;
                int emptySlot = findEmptySlot();
                if (emptySlot != -1) {
                    currentMeatType[emptySlot] = "pork";
                    startGrilling(emptySlot, R.drawable.pork_raw, R.drawable.pork_cooked, R.drawable.pork_burnt);
                    totalGrilled++;
                }
            }
        });

        btnChicken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPaused) return;
                int emptySlot = findEmptySlot();
                if (emptySlot != -1) {
                    currentMeatType[emptySlot] = "chicken";
                    startGrilling(emptySlot, R.drawable.chicken_raw, R.drawable.chicken_cooked, R.drawable.chicken_burnt);
                    totalGrilled++;
                }
            }
        });

        btnBeef.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPaused) return;
                int emptySlot = findEmptySlot();
                if (emptySlot != -1) {
                    currentMeatType[emptySlot] = "beef";
                    startBeefGrilling(emptySlot);
                    totalGrilled++;
                }
            }
        });

        ImageButton btnPause = findViewById(R.id.btnPause);
        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPauseDialog();
            }
        });
    }

    // --- โซนจัดการเวลา ---
    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateTimerText();
            }

            @Override
            public void onFinish() {
                isTimerRunning = false;
                timeLeftInMillis = 0;
                updateTimerText();
                showSummaryDialog();
            }
        }.start();

        isTimerRunning = true;
    }

    private void pauseTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        isTimerRunning = false;
    }

    private void updateTimerText() {
        int seconds = (int) (timeLeftInMillis / 1000);
        tvTimer.setText("Time: " + seconds);

        if (seconds <= 10) {
            tvTimer.setTextColor(android.graphics.Color.RED);
        } else {
            tvTimer.setTextColor(android.graphics.Color.parseColor("#FFEB3B"));
        }
    }

    // --------------------------------

    private int findEmptySlot() {
        for (int i = 0; i < 4; i++) {
            if (meatStatus[i] == 0) return i;
        }
        return -1;
    }

    private void startGrilling(final int index, final int imgRaw, final int imgCooked, final int imgBurnt) {
        meatStatus[index] = 1;
        slots[index].setImageResource(imgRaw);
        slots[index].setVisibility(View.VISIBLE);

        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isPaused) {
                    handler.postDelayed(this, 500);
                } else if (meatStatus[index] == 1) {
                    meatStatus[index] = 2;
                    slots[index].setImageResource(imgCooked);
                    startBurningCountDown(index, imgBurnt);
                }
            }
        }, 3000);
    }

    private void startBurningCountDown(final int index, final int imgBurnt) {
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isPaused) {
                    handler.postDelayed(this, 500);
                } else if (meatStatus[index] == 2) {
                    meatStatus[index] = 3;
                    slots[index].setImageResource(imgBurnt);
                }
            }
        }, 3000);
    }

    private void startBeefGrilling(final int index) {
        meatStatus[index] = 1;
        slots[index].setImageResource(R.drawable.beef_rare);
        slots[index].setVisibility(View.VISIBLE);

        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isPaused) {
                    handler.postDelayed(this, 500);
                } else if (meatStatus[index] == 1) {
                    meatStatus[index] = 4;
                    slots[index].setImageResource(R.drawable.beef_medium_rare);
                    scheduleBeefStep2(index, handler);
                }
            }
        }, 3000);
    }

    private void scheduleBeefStep2(final int index, final Handler handler) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isPaused) {
                    handler.postDelayed(this, 500);
                } else if (meatStatus[index] == 4) {
                    meatStatus[index] = 2;
                    slots[index].setImageResource(R.drawable.beef_well_done);
                    scheduleBeefStep3(index, handler);
                }
            }
        }, 3000);
    }

    private void scheduleBeefStep3(final int index, final Handler handler) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isPaused) {
                    handler.postDelayed(this, 500);
                } else if (meatStatus[index] == 2) {
                    meatStatus[index] = 3;
                    slots[index].setImageResource(R.drawable.beef_burnt);
                }
            }
        }, 3000);
    }

    private void checkFoodStatus(int index) {
        if (isPaused) return;

        // สุกพอดี (Well Done)
        if (meatStatus[index] == 2) {
            score += 10;
            happiness += 5;
            if (happiness > 100) happiness = 100;
            resetStove(index);
        }
        // ไหม้ (Burnt)
        else if (meatStatus[index] == 3) {
            score -= 5;
            burntCount++;
            happiness -= 10;
            if (happiness < 0) happiness = 0;
            resetStove(index);
        }
        // Medium Rare
        else if (meatStatus[index] == 4) {
            score += 5;
            happiness += 2;
            if (happiness > 100) happiness = 100;
            resetStove(index);
        }
        // ดิบ (Raw)
        else if (meatStatus[index] == 1) {
            score -= 3;
            happiness -= 5;
            if (happiness < 0) happiness = 0;
            resetStove(index);
        }

        tvScore.setText("Score: " + score);
    }

    private void resetStove(int index) {
        meatStatus[index] = 0;
        currentMeatType[index] = "none";
        slots[index].setVisibility(View.INVISIBLE);
    }

    private void showPauseDialog() {
        pauseTimer();

        isPaused = true;
        final android.app.Dialog dialog = new android.app.Dialog(this);
        dialog.setContentView(R.layout.dialog_pause);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCancelable(false);

        dialog.findViewById(R.id.btnResume).setOnClickListener(v -> {
            isPaused = false;
            startTimer();
            dialog.dismiss();
        });

        dialog.findViewById(R.id.btnRestart).setOnClickListener(v -> {
            dialog.dismiss();
            showSummaryDialog();
        });

        dialog.findViewById(R.id.btnExit).setOnClickListener(v -> {
            saveGameProgress();
            dialog.dismiss();
            finish();
        });
        dialog.show();
    }

    private void showSummaryDialog() {
        pauseTimer();

        for (int i = 0; i < 4; i++) {
            if (meatStatus[i] == 3) {
                burntCount++;
                meatStatus[i] = 0;
            }
        }

        android.content.SharedPreferences prefs = getSharedPreferences("GameData", MODE_PRIVATE);
        int highScore = prefs.getInt("HIGH_SCORE", 0);
        if (score > highScore) {
            highScore = score;
            prefs.edit().putInt("HIGH_SCORE", highScore).apply();
        }

        final android.app.Dialog dialog = new android.app.Dialog(this);
        dialog.setContentView(R.layout.dialog_summary);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCancelable(false);

        TextView tvTotal = dialog.findViewById(R.id.tvTotalGrilled);
        TextView tvFinal = dialog.findViewById(R.id.tvFinalScore);
        TextView tvHigh = dialog.findViewById(R.id.tvHighScore);
        TextView tvHappy = dialog.findViewById(R.id.tvHappiness);
        TextView tvBurnt = dialog.findViewById(R.id.tvBurntCount);

        tvTotal.setText("ปิ้งไปทั้งหมด " + totalGrilled + " ชิ้น");
        tvFinal.setText("คะแนน: " + score);
        tvHigh.setText("คะแนนสูงสุด: " + highScore);
        tvHappy.setText("ความสุข: " + happiness);
        tvBurnt.setText("ไหม้ไปแล้ว: " + burntCount + " ชิ้น");

        dialog.findViewById(R.id.btnFinalExit).setOnClickListener(v -> finish());
        dialog.show();
    }

    private void saveGameProgress() {
        android.content.SharedPreferences prefs = getSharedPreferences("GameData", MODE_PRIVATE);
        android.content.SharedPreferences.Editor editor = prefs.edit();

        editor.putBoolean("HAS_SAVE", true);
        editor.putInt("SAVE_SCORE", score);
        editor.putInt("SAVE_GRILLED", totalGrilled);
        editor.putInt("SAVE_BURNT", burntCount);
        editor.putInt("SAVE_HAPPINESS", happiness);
        editor.putLong("SAVE_TIME_LEFT", timeLeftInMillis);

        for (int i = 0; i < 4; i++) {
            editor.putInt("SAVE_MEAT_STATUS_" + i, meatStatus[i]);
            editor.putString("SAVE_MEAT_TYPE_" + i, currentMeatType[i]);
        }

        editor.apply();
    }

    private void loadGameProgress() {
        android.content.SharedPreferences prefs = getSharedPreferences("GameData", MODE_PRIVATE);
        score = prefs.getInt("SAVE_SCORE", 0);
        totalGrilled = prefs.getInt("SAVE_GRILLED", 0);
        burntCount = prefs.getInt("SAVE_BURNT", 0);
        happiness = prefs.getInt("SAVE_HAPPINESS", 100);
        timeLeftInMillis = prefs.getLong("SAVE_TIME_LEFT", 60000);

        tvScore.setText("Score: " + score);
        updateTimerText();
        startTimer();

        for (int i = 0; i < 4; i++) {
            meatStatus[i] = prefs.getInt("SAVE_MEAT_STATUS_" + i, 0);
            currentMeatType[i] = prefs.getString("SAVE_MEAT_TYPE_" + i, "none");
            restoreSlotState(i);
        }
    }

    private void restoreSlotState(int i) {
        if (meatStatus[i] == 0) {
            slots[i].setVisibility(View.INVISIBLE);
            return;
        }

        slots[i].setVisibility(View.VISIBLE);

        if (meatStatus[i] == 1) {
            if (currentMeatType[i].equals("beef")) startBeefGrilling(i);
            else {
                int raw = currentMeatType[i].equals("pork") ? R.drawable.pork_raw : R.drawable.chicken_raw;
                int cooked = currentMeatType[i].equals("pork") ? R.drawable.pork_cooked : R.drawable.chicken_cooked;
                int burnt = currentMeatType[i].equals("pork") ? R.drawable.pork_burnt : R.drawable.chicken_burnt;
                startGrilling(i, raw, cooked, burnt);
            }
        }
        else if (meatStatus[i] == 4 && currentMeatType[i].equals("beef")) {
            slots[i].setImageResource(R.drawable.beef_medium_rare);
            scheduleBeefStep2(i, new Handler(Looper.getMainLooper()));
        }
        else if (meatStatus[i] == 2) {
            int cooked = R.drawable.pork_cooked;
            int burnt = R.drawable.pork_burnt;
            if (currentMeatType[i].equals("chicken")) { cooked = R.drawable.chicken_cooked; burnt = R.drawable.chicken_burnt; }
            else if (currentMeatType[i].equals("beef")) { cooked = R.drawable.beef_well_done; burnt = R.drawable.beef_burnt; }

            slots[i].setImageResource(cooked);
            startBurningCountDown(i, burnt);
        }
        else if (meatStatus[i] == 3) {
            int burnt = R.drawable.pork_burnt;
            if (currentMeatType[i].equals("chicken")) burnt = R.drawable.chicken_burnt;
            else if (currentMeatType[i].equals("beef")) burnt = R.drawable.beef_burnt;
            slots[i].setImageResource(burnt);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // ถ้าเกมยังไม่จบ (เวลายังเหลือ) และยังไม่ได้กดหยุดไว้ก่อน
        // ให้สั่งหยุดเกมอัตโนมัติทันที
        if (timeLeftInMillis > 0 && !isPaused) {
            showPauseDialog();
        }
    }
}