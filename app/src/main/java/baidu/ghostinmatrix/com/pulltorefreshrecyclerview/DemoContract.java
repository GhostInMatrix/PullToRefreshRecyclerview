package baidu.ghostinmatrix.com.pulltorefreshrecyclerview;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shanjie on 2017/5/11.
 */
public interface DemoContract {
    interface PresenterContract {
        void getData();
        void loadData();
    }

    interface ViewContract {
        void onDataRefresh(ArrayList<String> data, int netState);
        void onDataLoad(ArrayList<String> data, int netState);
    }
}
