package com.yamigu.yamigu_app.CustomLayout;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;

import com.yamigu.yamigu_app.Etc.ImageUtils;

public class CircularImageView extends androidx.appcompat.widget.AppCompatImageView {
    private Bitmap bitmap;
    public CircularImageView(Context ctx, AttributeSet attrs) {
        super(ctx, attrs);
    }
    public Bitmap getBitmap() {
        return bitmap;
    }
    @Override
    protected void onDraw(Canvas canvas) {

        Drawable drawable = getDrawable();

        if (drawable == null) {
            return;
        }

        if (getWidth() == 0 || getHeight() == 0) {
            return;
        }
        this.bitmap = ((BitmapDrawable) drawable).getBitmap().copy(Bitmap.Config.ARGB_8888, true);
        Bitmap b = ((BitmapDrawable) drawable).getBitmap();
        Bitmap bitmap = b.copy(Bitmap.Config.ARGB_8888, true);

        int w = getWidth(), h = getHeight();
        Bitmap dst;
        Bitmap roundBitmap;
        if(bitmap.getWidth() < bitmap.getHeight()) {
            dst = Bitmap.createBitmap(bitmap, 0, (bitmap.getHeight()/2) - (bitmap.getWidth()/2), bitmap.getWidth(), bitmap.getWidth());
            roundBitmap = getRoundedCroppedBitmap(dst, w);
        }
        else {
            dst = Bitmap.createBitmap(bitmap, (bitmap.getWidth()/2) - (bitmap.getHeight()/2), 0, bitmap.getHeight(), bitmap.getHeight());
            roundBitmap = getRoundedCroppedBitmap(dst, w);
        }

        canvas.drawBitmap(roundBitmap, 0, 0, null);
    }

    public static Bitmap getRoundedCroppedBitmap(Bitmap bitmap, int radius) {
        Bitmap
                finalBitmap;
        if (bitmap.getWidth() != radius || bitmap.getHeight() != radius)
            finalBitmap = Bitmap.createScaledBitmap(bitmap, radius, radius,
                    false);
        else
            finalBitmap = bitmap;
        Bitmap output = Bitmap.createBitmap(finalBitmap.getWidth(),
                finalBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        Rect rect;
        if(finalBitmap.getHeight() > finalBitmap.getWidth()) {
            rect = new Rect(0, 0, finalBitmap.getWidth(),
                    finalBitmap.getWidth());
        }
        else {
            rect = new Rect(0, 0, finalBitmap.getHeight(),
                    finalBitmap.getHeight());
        }
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.parseColor("#BAB399"));
//        canvas.drawCircle(finalBitmap.getWidth() / 2 + 0.7f,
//                finalBitmap.getHeight() / 2 + 0.7f,
//                finalBitmap.getWidth() / 2 + 0.1f, paint);
        int offset = 0;
        RectF rectF = new RectF(
                offset, // left
                offset, // top
                finalBitmap.getWidth() - offset, // right
                finalBitmap.getHeight() - offset // bottom
        );
        int radiusCorner = 50;
        canvas.drawRoundRect(
                rectF,
                radiusCorner,
                radiusCorner,
                paint
        );
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(finalBitmap, rect, rect, paint);
        return output;
    }

}