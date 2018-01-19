package baidu.ghostinmatrix.com.pulltorefreshrecyclerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;


/**
 * Author: qinxudong
 * XuDongQin@sf-express.com
 * Created Date: 2017/12/13
 * Final modified Date: 2017/12/13
 * Describe:
 */

public class LoadingProgressView extends View {
    private Paint bitmapPaint;
    private float start = 0f;
    Drawable drawable;
    Bitmap bitmap;
    /**
     * 图片宽度
     */
    private int bitmapWidth;
    /**
     * 图片高度
     */
    private int bitmapHeight;

    private float density;
    Matrix matrix;
    private boolean isShow = true;

    public LoadingProgressView(Context context) {
        this(context, null);
    }

    public LoadingProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (attrs != null) {

            TypedArray array = context.obtainStyledAttributes(attrs,
                    R.styleable.LoadingProgressView, defStyleAttr, 0);
            drawable = array.getDrawable(R.styleable.LoadingProgressView_drawable);
            if (drawable != null) {
                bitmap = ((BitmapDrawable) drawable).getBitmap();
            } else {
                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.loading_sf_rotate);
            }
            array.recycle();
        } else {
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.loading_sf_rotate);
        }

        init();

    }

    private void init() {
        matrix = new Matrix();
        density = getResources().getDisplayMetrics().density;
        bitmapPaint = new Paint();
        bitmapPaint.setStyle(Paint.Style.FILL);
        bitmapPaint.setAntiAlias(true);
        bitmapWidth = bitmap.getWidth();
        bitmapHeight = bitmap.getHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isShow) {
            if (start <= -360) {
                start = 0f;
            }
            start -= 8;
            matrix.setRotate(start, bitmapWidth / 2, bitmapHeight / 2);
            canvas.drawBitmap(bitmap, matrix, bitmapPaint);
            invalidate();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getPaddingLeft() + getPaddingRight() + bitmapWidth;
        int height = getPaddingTop() + getPaddingBottom() + bitmapHeight;
        setMeasuredDimension(width, height);
    }

    public void setShow(boolean isShow) {
        this.isShow = isShow;
        if (isShow) {
            start = 0;
        }
        invalidate();
    }

}
