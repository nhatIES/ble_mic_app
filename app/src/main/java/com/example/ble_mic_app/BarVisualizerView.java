package com.example.ble_mic_app;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import java.util.Queue;
import java.util.LinkedList;

public class BarVisualizerView extends View {

    private static final int MAX_POINTS = 120; // số điểm hiển thị trên màn hình
    private final Queue<Float> amplitudes = new LinkedList<>();
    private final Paint paint = new Paint();
    private final int frameRate = 30; // 30 fps
    private float currentAmplitude = 0f;
    private long lastUpdateTime = 0;
    private boolean isIncreasing = true;

    public BarVisualizerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint.setColor(0xFF3B82F6); // màu xanh
        paint.setStrokeWidth(6f);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        post(animationRunnable);
    }

    private final Runnable animationRunnable = new Runnable() {
        @Override
        public void run() {
            simulateAmplitude();
            if (amplitudes.size() > MAX_POINTS) {
                amplitudes.poll(); // bỏ điểm cũ bên trái
            }
            amplitudes.offer(currentAmplitude);
            invalidate();
            postDelayed(this, 1000 / frameRate);
        }
    };

    private void simulateAmplitude() {
        long now = System.currentTimeMillis();
        if (now - lastUpdateTime > 200) {
            lastUpdateTime = now;

            // Giả lập "nói to" rồi "im lặng" kiểu tự nhiên
            if (isIncreasing) {
                currentAmplitude += Math.random() * 10;
                if (currentAmplitude > 100) isIncreasing = false;
            } else {
                currentAmplitude -= Math.random() * 10;
                if (currentAmplitude < 10) isIncreasing = true;
            }
        }
        // thêm chút random để không quá đều
        currentAmplitude += (Math.random() - 0.5f) * 8f;
        currentAmplitude = Math.max(0, Math.min(120, currentAmplitude));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float width = getWidth();
        float height = getHeight();
        float centerY = height / 2f;
        float step = width / (float) MAX_POINTS;

        Float[] amps = amplitudes.toArray(new Float[0]);
        for (int i = 0; i < amps.length; i++) {
            float x = i * step;
            float amp = amps[i];
            float top = centerY - amp;
            float bottom = centerY + amp;
            canvas.drawLine(x, top, x, bottom, paint);
        }
    }
}
