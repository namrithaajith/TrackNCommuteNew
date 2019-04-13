package com.mobioetech.trackncommute.trackncommute.User;

import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobioetech.trackncommute.trackncommute.R;

import java.util.List;


public class NearbyDetailInfoPagerAdapter extends PagerAdapter {

    List<Integer> dataList;

    public NearbyDetailInfoPagerAdapter(List<Integer> dataList) {
        this.dataList = dataList;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        View view = null;

        if (dataList.get(position) == 0) {
            view = LayoutInflater.from(container.getContext()).inflate(R.layout.nearbydriver_info, container, false);
        }
        container.addView(view);
        return view;
    }


}
