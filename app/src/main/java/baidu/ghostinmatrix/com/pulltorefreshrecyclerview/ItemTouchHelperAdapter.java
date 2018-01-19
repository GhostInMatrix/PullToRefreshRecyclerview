package baidu.ghostinmatrix.com.pulltorefreshrecyclerview;

/**
 * Created by shanjie on 2017/3/5.
 */
public interface ItemTouchHelperAdapter {
    void onItemTop(int fromPosition);

    void onItemDismiss(int position);

    void onItemSwap(int itemAPosition, int itemBPosition);
}
