package com.example.mukrathamaster;

import androidx.appcompat.app.AppCompatActivity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class MainMenuActivity extends AppCompatActivity {

    // ประกาศ ImageButton เป็น Field เพื่อให้เข้าถึงได้ใน onResume()
    private ImageButton btnContinueGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        // เชื่อมต่อปุ่ม
        ImageButton btnStartGame = findViewById(R.id.btnStartGame);
        btnContinueGame = findViewById(R.id.btnContinueGame); // เชื่อมต่อและกำหนดค่าให้กับ Field
        ImageButton btnHistory = findViewById(R.id.btnHistory);

        // ปุ่มเริ่มเกมใหม่
        btnStartGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // เริ่มเกมใหม่ (ไม่โหลดเซฟ)
                Intent intent = new Intent(MainMenuActivity.this, MainActivity.class);
                intent.putExtra("IS_CONTINUE", false);
                startActivity(intent);
            }
        });

        // ปุ่มเล่นต่อ (Logic การทำงานของปุ่ม)
        btnContinueGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // โค้ดนี้จะทำงานเมื่อปุ่มถูก Enabled เท่านั้น
                SharedPreferences prefs = getSharedPreferences("GameData", MODE_PRIVATE);
                if (prefs.getBoolean("HAS_SAVE", false)) {
                    // โหลดเกมต่อ (IS_CONTINUE = true)
                    Intent intent = new Intent(MainMenuActivity.this, MainActivity.class);
                    intent.putExtra("IS_CONTINUE", true);
                    startActivity(intent);
                } else {
                    // ******************* แก้ไข: ใช้ String Resource *******************
                    Toast.makeText(MainMenuActivity.this, getString(R.string.history_no_save), Toast.LENGTH_SHORT).show();
                    // *****************************************************************
                }
            }
        });

        // ปุ่มดูประวัติ
        btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHistoryDialog();
            }
        });
    }

    /**
     * เมธอดนี้จะถูกเรียกทุกครั้งที่หน้า Activity นี้กลับมาทำงาน
     * ใช้สำหรับอัปเดตสถานะ (Enabled/Disabled) และความจาง (Alpha) ของปุ่ม 'เล่นต่อ'
     */
    @Override
    protected void onResume() {
        super.onResume();

        // ต้องมั่นใจว่า btnContinueGame ถูกกำหนดค่าแล้ว
        if (btnContinueGame != null) {
            // เช็คสถานะการเซฟเกมจาก SharedPreferences
            SharedPreferences prefs = getSharedPreferences("GameData", MODE_PRIVATE);
            boolean hasSave = prefs.getBoolean("HAS_SAVE", false);

            // 1. จัดการสถานะปุ่ม: ถ้ามีเซฟ -> กดได้, ถ้าไม่มีเซฟ -> กดไม่ได้
            btnContinueGame.setEnabled(hasSave);

            // 2. ปรับความจาง (Alpha):
            // - ถ้ามีเซฟ: Alpha 1.0f (เข้มปกติ)
            // - ถ้าไม่มีเซฟ: Alpha 0.4f (จางลง)
            btnContinueGame.setAlpha(hasSave ? 1.0f : 0.4f);
        }
    }

    // ฟังก์ชันแสดงหน้าต่างประวัติ (โค้ดเดิม)
    private void showHistoryDialog() {
        // 1. ดึงข้อมูลจากเครื่อง
        SharedPreferences prefs = getSharedPreferences("GameData", MODE_PRIVATE);
        int highScore = prefs.getInt("HIGH_SCORE", 0);
        int lastScore = prefs.getInt("SAVE_SCORE", 0);
        int lastGrilled = prefs.getInt("SAVE_GRILLED", 0);

        // 2. สร้าง Dialog
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_history);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCancelable(true); // กดข้างนอกเพื่อปิดได้

        // 3. เชื่อมตัวแปรและแสดงผล
        TextView tvHighScore = dialog.findViewById(R.id.tvHighScore);
        TextView tvLastStats = dialog.findViewById(R.id.tvLastStats);
        ImageButton btnCloseHistory = dialog.findViewById(R.id.btnCloseHistory);

        // ******************* แก้ไข: ใช้ String Resource *******************
        tvHighScore.setText(getString(R.string.high_score_label) + highScore);

        // ใช้ String.format สำหรับข้อความที่มีตัวแปร (คะแนนล่าสุด, ปิ้งไปทั้งหมด)
        String historyStats = getString(R.string.history_last_stats, lastScore, lastGrilled);
        tvLastStats.setText(historyStats);
        // *****************************************************************

        // 4. ปุ่มปิด
        btnCloseHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}