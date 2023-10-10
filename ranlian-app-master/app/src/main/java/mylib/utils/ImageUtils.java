
package mylib.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.Pair;

import java.io.InputStream;

import mylib.app.MyLog;

public class ImageUtils {

    public static Bitmap resizeBitmap(Bitmap bitmap, int newWidth) {
        return resizeBitmap(bitmap, newWidth, true);
    }

    public static Bitmap resizeBitmap(Bitmap bitmap, int newWidth, boolean recycle) {
        if (null == bitmap)
            return null;
        try {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            if (width == 0 || height == 0) {
                return null;
            }
			/*if (width < newWidth)

			return bitmap;*/

            float temp = ((float) height) / ((float) width);
            int newHeight = (int) ((newWidth) * temp);

            float scaleWidth = ((float) newWidth) / width;
            float scaleHeight = ((float) newHeight) / height;
            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleHeight);
            Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
            if (recycle && resizedBitmap != bitmap && !bitmap.isRecycled())
                bitmap.recycle();
            return resizedBitmap;
        } catch (Throwable t) {
            MyLog.LOGW(t);
        }
        return bitmap;
    }

    public static boolean isImage(String imageFile) {
        if (TextUtils.isEmpty(imageFile)) {
            return false;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.outHeight = 0;
        options.inSampleSize = 1;
        int trycnt = 4;
        options.inJustDecodeBounds = true;
        do {
            try {
                BitmapFactory.decodeFile(imageFile, options);
                break;
            } catch (OutOfMemoryError err) {
                if (trycnt == 4) {
                    //Utils.toast(R.string.low_mem);
                }
            }
            options.inSampleSize += 3;
            trycnt--;
        } while (trycnt > 0);
        if (trycnt == 0) {
            return false;
        }
        if (options.outWidth <= 0 || options.outHeight <= 0) {
            return false;
        }
        return true;
    }

    public static Bitmap getFileBmp(String imageFile) {
        if (!isImage(imageFile))
            return null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 1;
        Bitmap bmp = null;
        int trycnt = 4;
        do {
            try {
                bmp = BitmapFactory.decodeFile(imageFile, options);
                if (bmp != null) {
                    break;
                }
            } catch (OutOfMemoryError err) {
                if (trycnt == 4) {
                    //Utils.toast(R.string.low_mem);
                }
            }
            options.inSampleSize += 3;
            trycnt--;
        } while (trycnt > 0);
        return bmp;
    }

}
