package com.example.testingmaps;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public class MarkerWithLabel extends Marker {
        Paint textPaint = null;
        String mLabel = null;

        public MarkerWithLabel(MapView mapView, String label) {
            super( mapView);
            mLabel = label;
            textPaint = new Paint();
            textPaint.setColor( Color.RED);
            textPaint.setTextSize(40f);
            textPaint.setAntiAlias(true);
            textPaint.setTextAlign(Paint.Align.LEFT);
        }
        public void draw(final Canvas c, final MapView osmv, boolean shadow) {
            draw( c, osmv);
        }
        public void draw( final Canvas c, final MapView osmv) {
            super.draw( c, osmv, false);
            Point p = this.mPositionPixels;  // already provisioned by Marker
            c.drawText( mLabel, p.x, p.y+20, textPaint);
        }
    }

