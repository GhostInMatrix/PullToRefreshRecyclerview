package baidu.ghostinmatrix.com.pulltorefreshrecyclerview;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by shanjie on 2017/5/11.
 */
public class DemoPresenter implements DemoContract.PresenterContract {
    private DemoContract.ViewContract mViewContract;
    final ArrayList<String> titles = new ArrayList<>();

    DemoPresenter(DemoContract.ViewContract viewContract) {
        mViewContract = viewContract;
    }

    @Override
    public void getData() {
        titles.clear();
        titles.add("title1");
        titles.add("title2");
        titles.add("title3");
        titles.add("title4");
        titles.add("title5");
        titles.add("title6");
        titles.add("title7");
        titles.add("title8");
        titles.add("title9");
        titles.add("title10");
        mViewContract.onDataRefresh(titles, NetStateView.DATA_STATUS_NORMAL);
    }

    @Override
    public void loadData() {
        int n = titles.size() / 10;
        titles.add("title" + n + "1");
        titles.add("title" + n + "2");
        titles.add("title" + n + "3");
        titles.add("title" + n + "4");
        titles.add("title" + n + "5");
        titles.add("title" + n + "6");
        titles.add("title" + n + "7");
        titles.add("title" + n + "8");
        titles.add("title" + n + "9");
        titles.add("title" + (n + 1) + "0");
        mViewContract.onDataLoad(titles, NetStateView.DATA_STATUS_NORMAL);
    }
}
