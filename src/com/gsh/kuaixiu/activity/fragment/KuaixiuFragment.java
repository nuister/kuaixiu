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

package com.gsh.kuaixiu.activity.fragment;

import android.content.Intent;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.gsh.base.activity.fragment.FragmentBase;
import com.gsh.base.https.ApiResult;
import com.gsh.base.model.FetchDataListener;
import com.gsh.kuaixiu.R;
import com.gsh.kuaixiu.activity.KuaixiuActivity;
import com.gsh.kuaixiu.activity.PostActivity;
import com.gsh.kuaixiu.model.OrderListViewModel;
import com.litesuits.common.utils.DensityUtil;
import com.gsh.kuaixiu.model.OrderListViewModel.Worker;

import java.util.List;

public class KuaixiuFragment extends FragmentBase implements AMapLocationListener {

    private MapView mapView;
    private AMap aMap;

    private Marker me;

    private LocationManagerProxy mLocationManagerProxy;
    private OrderListViewModel orderListViewModel;

    @Override
    public void refresh() {

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (layout != null) {
            ViewGroup parent = (ViewGroup) layout.getParent();
            if (parent != null)
                parent.removeView(layout);
            return layout;
        }

        orderListViewModel = new OrderListViewModel();
        layout = inflater.inflate(R.layout.kuaixiu_fragment_host, container, false);
        findViewById(R.id.menu).setOnClickListener(onClickListener);
//        for (Type type : Type.values()) {
//            View action = findViewById(type.actionId);
//            action.setTag(type);
//            action.setOnClickListener(onClickListener);
//            ((TextView) findViewById(type.labelId)).setText(type.label);
//        }
        findViewById(Type.lock.actionId).performClick();
        findViewById(R.id.click_post).setOnClickListener(onClickListener);

        if (mLocationManagerProxy == null) {
            mLocationManagerProxy = LocationManagerProxy.getInstance(getActivity());
            mLocationManagerProxy.requestLocationData(
                    LocationProviderProxy.AMapNetwork, 2 * 1000, 10, this);
        }

        mapView = (MapView) layout.findViewById(R.id.map);

        mapView.onCreate(savedInstanceState);

        initMap();


        return layout;
    }

    private void drawWorkers(List<OrderListViewModel.Worker> workers) {
        aMap.clear();
        for (OrderListViewModel.Worker worker : workers) {

            aMap.addMarker(new MarkerOptions()
                    .position(new LatLng(worker.latitude, worker.longitude))
                    .icon(BitmapDescriptorFactory
                            .fromView(getWorkerInfo(worker))));
//                marker.setObject(vender.getVendorID());

        }
    }

    private View getWorkerInfo(OrderListViewModel.Worker worker) {
        View workerInfo = LayoutInflater.from(activity).inflate(
                R.layout.item_worker, null);
        TextView name = (TextView) workerInfo.findViewById(R.id.worker_name);
        name.setText(worker.name);
        return workerInfo;
    }

    private void initMap() {
        if (aMap == null)
            aMap = mapView.getMap();
        aMap.getUiSettings().setZoomControlsEnabled(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mLocationManagerProxy != null) {
            mLocationManagerProxy.removeUpdates(this);
            mLocationManagerProxy.destroy();
        }
        mLocationManagerProxy = null;
        if (mapView != null)
            mapView.onDestroy();
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (R.id.menu == v.getId()) {
                ((KuaixiuActivity) activity).getSlidingMenu().showMenu();
            } else if (R.id.click_post == v.getId()) {
                Intent intent = new Intent(activity, PostActivity.class);
//                intent.putExtra(String.class.getName(), chooseType.label);
//                intent.putExtra(Integer.class.getName(), chooseType.param);
                startActivity(intent);
            } else {
                chooseType = (Type) v.getTag();
                for (Type type : Type.values()) {
                    findViewById(type.imageId).setSelected(type == chooseType);
                    findViewById(type.labelId).setSelected(type == chooseType);
                }
            }
        }
    };

    private Type chooseType;

    enum Type {
        lock(R.id.type_a, R.id.type_a_image, R.id.type_a_label, "开锁换锁", 0),
        appliance(R.id.type_b, R.id.type_b_image, R.id.type_b_label, "家电维修", 0),
        family(R.id.type_c, R.id.type_c_image, R.id.type_c_label, "家庭小修", 0),;

        int actionId;
        int imageId;
        int labelId;
        String label;
        int param;

        Type(int actionId, int imageId, int labelId, String label, int param) {
            this.actionId = actionId;
            this.imageId = imageId;
            this.labelId = labelId;
            this.label = label;
            this.param = param;
        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null && !(aMapLocation.getLatitude() == 0 && aMapLocation.getLongitude() == 0)) {
            aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude())));
            setMe(new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude()));

            if (workers == null || workers.isEmpty()) {
                orderListViewModel.fetchWorkers(String.valueOf(aMapLocation.getLatitude()), String.valueOf(aMapLocation.getLongitude()), workersResponse);
            }
        }
    }

    List<Worker> workers;
    private FetchDataListener workersResponse = new FetchDataListener() {
        @Override
        public void onSuccess(ApiResult apiResult) {

            workers = apiResult.getModels(Worker.class);
            drawWorkers(workers);
        }

        @Override
        public void onFailure(String description) {

        }
    };

    private void setMe(LatLng latLng) {
        if (me != null)
            me.destroy();

        int w = DensityUtil.dip2px(getActivity(), 54 * .5f);
        int h = DensityUtil.dip2px(getActivity(), 50 * .5f);

        Drawable drawable = getResources().getDrawable(R.drawable.ic_launcher);
        drawable = zoomDrawable(drawable, w, h);
        me = aMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory
                .fromBitmap(drawableToBitmap(drawable))));
        aMap.moveCamera(CameraUpdateFactory.scrollBy(0, 1));
        Point point = aMap.getProjection().toScreenLocation(latLng);
        int space = getActivity().getWindowManager().getDefaultDisplay().getWidth();
        space = space - 10;
        float mZoom = aMap.getCameraPosition().zoom;
        if (point.x <= 0 || point.x >= space || point.y <= 0
                || point.y >= space) {
            aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, mZoom));
        }
    }

    Bitmap drawableToBitmap(Drawable drawable) // drawable
    // 转换成bitmap
    {
        int width = drawable.getIntrinsicWidth(); // 取drawable的长宽
        int height = drawable.getIntrinsicHeight();
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565; // 取drawable的颜色格式
        Bitmap bitmap = Bitmap.createBitmap(width, height, config); // 建立对应bitmap
        Canvas canvas = new Canvas(bitmap); // 建立对应bitmap的画布
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas); // 把drawable内容画到画布中
        return bitmap;
    }

    Drawable zoomDrawable(Drawable drawable, int w, int h) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap oldbmp = drawableToBitmap(drawable); // drawable转换成bitmap
        Matrix matrix = new Matrix(); // 创建操作图片用的Matrix对象
        float scaleWidth = ((float) w / width); // 计算缩放比例
        float scaleHeight = ((float) h / height);
        matrix.postScale(scaleWidth, scaleHeight); // 设置缩放比例
        Bitmap newbmp = Bitmap.createBitmap(oldbmp, 0, 0, width, height,
                matrix, true); // 建立新的bitmap，其内容是对原bitmap的缩放后的图
        return new BitmapDrawable(newbmp); // 把bitmap转换成drawable并返回
    }

}
