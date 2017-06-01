package baidu.ghostinmatrix.com.pulltorefreshrecyclerview;

/**
 * Created by shanjie on 2017/5/12.
 */
public interface MultiTypeSupport<T> {
    public int getItemViewType(int position, T data);

    public int getLayoutId(int itemViewType);
}
