package com.example.mukrathamaster;

public class Pork extends Meat {

    @Override
    public void updateCooking(int seconds) {
        currentCookingTime += seconds;

        // กำหนดช่วงเวลาของหมู (ต้องสุกถึงกินได้)
        if (currentCookingTime > 30) {
            state = MeatState.BURNT;      // เกิน 30 วิ = ไหม้
        } else if (currentCookingTime > 15) {
            state = MeatState.WELL_DONE;  // 16-30 วิ = สุก
        } else {
            state = MeatState.RAW;        // 0-15 วิ = ดิบ
        }
    }

    @Override
    protected int calculateScoreByDoneness() {
        if (state == MeatState.WELL_DONE) {
            return 80; // หมูสุกได้ 80 คะแนน
        }
        return 0; // ดิบไม่ได้คะแนน
    }
}
