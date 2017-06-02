package baidu.ghostinmatrix.com.pulltorefreshrecyclerview;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by shanjie on 2017/5/11.
 */
public class DemoPresenter implements DemoContract.PresenterContract {
    private DemoContract.ViewContract mViewContract;
    final List<String> titles = new ArrayList<>();

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
        mViewContract.onDataCome(titles, NetStateView.DATA_STATUS_NORMAL);
    }

    @Override
    public void loadData() {
        titles.add("title11");
        titles.add("title12");
        titles.add("title13");
        titles.add("title14");
        titles.add("title15");
        titles.add("title16");
        titles.add("title17");
        titles.add("title18");
        titles.add("title19");
        titles.add("title20");
        mViewContract.onDataCome(titles, NetStateView.DATA_STATUS_NORMAL);
    }
}
