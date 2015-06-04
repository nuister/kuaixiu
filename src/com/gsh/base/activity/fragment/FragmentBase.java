package com.gsh.base.activity.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.gsh.kuaixiu.R;
import com.gsh.base.activity.ActivityBase;
import com.litesuits.common.utils.DensityUtil;

/**
 * Created by taosj on 15/2/2.
 */
public abstract class FragmentBase extends Fragment {


    protected ActivityBase activity;
    protected View layout;

    public abstract void refresh();

    private boolean contentLoaded;

    protected void hideContent() {
        contentLoaded = false;
        findViewById(R.id.content).setVisibility(View.INVISIBLE);
    }

    protected void showContent() {
        if (contentLoaded)
            return;
        contentLoaded = true;
        findViewById(R.id.content).setVisibility(View.VISIBLE);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        activity = (ActivityBase) getActivity();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    protected View findViewById(int id) {
        return layout.findViewById(id);
    }

    protected ViewGroup getRootView() {
        return (ViewGroup) layout;
    }


    public void showErrorPage(String message) {
        ViewGroup rootView = getRootView();

        if (rootView.findViewWithTag("errorPage") != null)
            return;

        RelativeLayout view = new RelativeLayout(getActivity());
        view.setLayoutParams(rootView.getLayoutParams());
        view.setBackgroundResource(android.R.color.transparent);
        FrameLayout fl = new FrameLayout(getActivity());
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(rootView.getWidth(), rootView.getHeight());
        lp.setMargins(0, 0, 0, 0);
        fl.setLayoutParams(lp);
        fl.setBackgroundResource(R.color.ui_bg_white);

        ImageView iv = new ImageView(getActivity());
        int width = DensityUtil.dip2px(getActivity(), 80);
        int height = DensityUtil.dip2px(getActivity(), 80);
        FrameLayout.LayoutParams ivlp = new FrameLayout.LayoutParams(width, height);

        int parentHeight = rootView.getHeight();
        int parentWidth = rootView.getWidth();

        int x = parentWidth / 2 - width / 2;
        int y = parentHeight / 2 - height;
        ivlp.setMargins(x, y, 0, 0);
        iv.setLayoutParams(ivlp);
//        iv.setImageResource(R.drawable.shibai);
        fl.addView(iv);

        view.addView(fl);

        TextView tv = new TextView(getActivity());
        FrameLayout.LayoutParams tvlp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tvlp.setMargins(0, y + height + 40, 0, 0);
        tv.setLayoutParams(tvlp);
        tv.setGravity(Gravity.CENTER);
        tv.setTextColor(getResources().getColor(R.color.ui_font_d));
        tv.setText(message);
        fl.addView(tv);
        fl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onErrorPageClick();
            }
        });
        view.setTag("errorPage");
        rootView.addView(view);
    }

    public void hideErrorPage() {
        if (getRootView().findViewWithTag("errorPage") != null) {
            getRootView().removeView(getRootView().findViewWithTag("errorPage"));
        }
    }

    public void onErrorPageClick() {

    }

    public void showEmptyPage(String message) {
        ViewGroup rootView = getRootView();

        if (rootView.findViewWithTag("emptyPage") != null)
            return;

        RelativeLayout view = new RelativeLayout(getActivity());
        view.setLayoutParams(rootView.getLayoutParams());
        view.setBackgroundResource(android.R.color.transparent);

        FrameLayout fl = new FrameLayout(getActivity());
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(rootView.getWidth(), rootView.getHeight());
        lp.setMargins(0, 0, 0, 0);
        fl.setLayoutParams(lp);
        fl.setBackgroundResource(R.color.ui_bg_white);

        ImageView iv = new ImageView(getActivity());
        int width = DensityUtil.dip2px(getActivity(), 80);
        int height = DensityUtil.dip2px(getActivity(), 80);
        FrameLayout.LayoutParams ivlp = new FrameLayout.LayoutParams(width, height);

        int parentHeight = rootView.getHeight();
        int parentWidth = rootView.getWidth();

        int x = parentWidth / 2 - width / 2;
        int y = parentHeight / 2 - height;
        ivlp.setMargins(x, y, 0, 0);
        iv.setLayoutParams(ivlp);
//        iv.setImageResource(R.drawable.kong);
        fl.addView(iv);

        view.addView(fl);

        TextView tv = new TextView(getActivity());
        FrameLayout.LayoutParams tvlp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tvlp.setMargins(0, y + height + 40, 0, 0);
        tv.setLayoutParams(tvlp);
        tv.setGravity(Gravity.CENTER);
        tv.setTextColor(getResources().getColor(R.color.ui_font_d));
        tv.setText(message);
        fl.addView(tv);
        fl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onEmptyPageClick();
            }
        });
        view.setTag("emptyPage");
        rootView.addView(view, 1);
    }

    public void hideEmptyPage() {
        if (getRootView().findViewWithTag("emptyPage") != null) {
            getRootView().removeView(getRootView().findViewWithTag("emptyPage"));
        }
    }

    public void onEmptyPageClick() {

    }
}
