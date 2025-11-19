package com.example.mukrathamaster;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // --- Test Logic ---

        // 1. ลองสร้างเนื้อวัวมา 1 ชิ้น
        Beef myBeef = new Beef();
        Log.d("GrillTest", "เริ่มปิ้งเนื้อวัว: " + myBeef.getState()); // ควรเป็น RAW

        // 2. ผ่านไป 12 วินาที (ควรเป็น RARE)
        myBeef.updateCooking(12);
        Log.d("GrillTest", "ผ่านไป 12 วิ: " + myBeef.getState() + " | คะแนน: " + myBeef.getScore());

        // 3. ปิ้งต่ออีก 10 วินาที (รวมเป็น 22 วิ -> ควรเป็น MEDIUM_RARE)
        myBeef.updateCooking(10);
        Log.d("GrillTest", "ปิ้งต่ออีก 10 วิ (รวม 22): " + myBeef.getState() + " | คะแนน: " + myBeef.getScore());

        // 4. ปิ้งยาวๆ จนไหม้ (รวมเป็น 42 วิ -> ควรเป็น BURNT)
        myBeef.updateCooking(20);
        Log.d("GrillTest", "ปิ้งต่ออีก 20 วิ (รวม 42): " + myBeef.getState() + " | คะแนน: " + myBeef.getScore());

        // --- จบการทดสอบ ---
    }
}