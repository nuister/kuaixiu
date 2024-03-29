/*
 * Copyright (c) 2014 Gangshanghua Information Technologies Ltd.
 * http://www.gangsh.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Gangshanghua Information Technologies ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement you
 * entered into with Gangshanghua Information Technologies.
 */

package com.gsh.base.activity;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.*;
import android.widget.TextView;
import com.gsh.kuaixiu.R;
import com.litesuits.android.widget.HackyViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * 相册
 *
 * @author Tan Chunmao
 */
public class GalleryActivity extends ActivityBase {
    private List<String> imagePaths;
    private int index;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            imagePaths = savedInstanceState.getStringArrayList(String.class.getName());
            index = savedInstanceState.getInt(Integer.class.getName(), 0);
        } else {
            imagePaths = getIntent().getStringArrayListExtra(String.class.getName());
            index = getIntent().getIntExtra(Integer.class.getName(), 0);
        }
        if (imagePaths == null || imagePaths.isEmpty()) {
            finish();
            return;
        }


        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_gallery);
        HackyViewPager mViewPager = (HackyViewPager) findViewById(R.id.view_pager);

        for (int i = 0; i < imagePaths.size(); i++) {
            String path = imagePaths.get(i).replace("_small","");
            imagePaths.set(i, path);
        }
        mViewPager.setAdapter(new ImagePagerAdapter());
        ViewPager.OnPageChangeListener listener = new PageChangeListener();
        mViewPager.setOnPageChangeListener(listener);
        listener.onPageSelected(0);
        mViewPager.setCurrentItem(index);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList(String.class.getName(), new ArrayList<String>(imagePaths));
        outState.putInt(Integer.class.getName(), index);
    }

    private class PageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int i, float v, int i2) {
        }

        @Override
        public void onPageSelected(int i) {
            TextView totalCurrentView = (TextView) findViewById(R.id.total_current);
            String text = String.format("%1$d/%2$d", i + 1, imagePaths.size());
            totalCurrentView.setText(text);
            if (imagePaths.size() == 1) {
                findViewById(R.id.total_current).setVisibility(View.GONE);
            }
        }

        @Override
        public void onPageScrollStateChanged(int i) {
        }
    }




    private class ImagePagerAdapter extends PagerAdapter {

        private LayoutInflater inflater;

        private ImagePagerAdapter() {
            inflater = getLayoutInflater();
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
            ((ViewGroup) container).removeView((View) object);
        }

        @Override
        public void finishUpdate(View view) {

        }

        @Override
        public int getCount() {
            return imagePaths.size();
        }

        @Override
        public void startUpdate(View view) {

        }

        @Override
        public Object instantiateItem(View view, int position) {
            View imageLayout = inflater.inflate(R.layout.item_pager_image, (ViewGroup) view, false);
            assert imageLayout != null;
            final uk.co.senab.photoview.PhotoView imageView = (uk.co.senab.photoview.PhotoView) imageLayout.findViewById(R.id.image);
            loadImage(imageView, imagePaths.get(position));
            ((ViewGroup) view).addView(imageLayout, 0);
            return imageLayout;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }
    }
}
