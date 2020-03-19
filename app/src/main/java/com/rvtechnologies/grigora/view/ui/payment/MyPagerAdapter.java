package com.rvtechnologies.grigora.view.ui.payment;

import android.view.View;
import android.view.ViewGroup;

import androidx.viewpager.widget.PagerAdapter;

import com.rvtechnologies.grigora.R;

class MyPagerAdapter extends PagerAdapter {

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        int resId = 0;
        switch (position) {
            case 0:
                resId = R.id.input_layout_card_number;
                break;
            case 1:
                resId = R.id.input_layout_expired_date;
                break;
            case 2:
                resId = R.id.input_layout_card_holder;
                break;
            case 3:
                resId = R.id.input_layout_cvv_code;
                break;
            case 4:
                resId = R.id.space;
                break;

        }
        return container.findViewById(resId);
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
    }


    @Override
    public int getCount() {
        return 5;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
