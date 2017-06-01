package baidu.ghostinmatrix.com.pulltorefreshrecyclerview;

import android.view.View;

/**
 * @author tangfuling
 */

public final class Util {

    public static void showView(View view) {
        if (view == null) {
            return;
        }
        if (view.getVisibility() != View.VISIBLE) {
            view.setVisibility(View.VISIBLE);
        }
    }

    public static void hideView(View view) {
        if (view == null) {
            return;
        }
        if (view.getVisibility() != View.GONE) {
            view.setVisibility(View.GONE);
        }
    }


}
