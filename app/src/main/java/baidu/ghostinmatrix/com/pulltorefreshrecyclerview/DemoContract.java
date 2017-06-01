package baidu.ghostinmatrix.com.pulltorefreshrecyclerview;

import java.util.List;

/**
 * Created by shanjie on 2017/5/11.
 */
public interface DemoContract {
    interface PresenterContract {
        void getData();
    }

    interface ViewContract {
        void onDataCome(List<String> data, int netState);
    }
}
