package com.gsh.kuaixiu.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.gsh.base.https.ApiResult;
import com.gsh.base.model.FetchDataListener;
import com.gsh.kuaixiu.Constant;
import com.gsh.kuaixiu.R;
import com.gsh.kuaixiu.model.OrderListViewModel;
import com.gsh.kuaixiu.model.OrderListViewModel.Order;
import com.gsh.kuaixiu.model.OrderListViewModel.Worker;
import com.litesuits.android.polling.PollingService;
import com.litesuits.android.polling.PollingUtils;
import com.litesuits.common.cache.XmlCache;
import com.litesuits.socket.TcpClient;
import com.litesuits.socket.model.LoginRequest;

import java.util.List;

public class OrderDetailNewActivity extends KuaixiuActivityBase implements LocationSource,
        AMapLocationListener, OnClickListener,
        AMap.OnMapTouchListener {
    private MapView mapView;
    private OnLocationChangedListener mListener;
    private LocationManagerProxy mAMapLocationManager;
    private LatLonPoint point;

    private AMap aMap;
    private Order order;
    private OrderListViewModel orderListViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        orderListViewModel = new OrderListViewModel();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_order_detail_new);
        setTitleBar("订单详情", " 取消");
        mapView = (MapView) findViewById(R.id.location_map);
        mapView.onCreate(savedInstanceState);


        initMap();

        showProgressDialog();

        orderListViewModel.orderDetail(getIntent().getStringExtra(String.class.getName()), orderDetailResponse);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.setPriority(500);
        intentFilter.addAction(Constant.Action.POLLING);
        intentFilter.addAction(Constant.Action.ORDER);
        registerReceiver(broadcastReceiver, intentFilter);
//        PollingUtils.startPollingService(context, 10, PollingService.class, PollingService.ACTION);

//        timer();
        startTcp();
    }



    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.equals(action, Constant.Action.POLLING)) {
                orderListViewModel.orderDetail(getIntent().getStringExtra(String.class.getName()), orderDetailResponse);
            } else if (TextUtils.equals(action, Constant.Action.ORDER)){
                String data = intent.getStringExtra(String.class.getName());
                abortBroadcast();
                if(!TextUtils.isEmpty(data)) {
                    String[] parts = data.split(",");
                    if(parts.length==3) {
                        if(parts[0].equalsIgnoreCase(order.sn) && parts[2].equalsIgnoreCase(OrderListViewModel.OrderState.WAITING_REPAIR.name())) {
                            Log.d("test","confirm:\n " + data);
                            TcpClient.instance.close();
                            unregisterReceiver(broadcastReceiver);
                            confirm();
                        }
                    }
                }
            }
        }
    };


    private void timer() {
        new CountDownTimer(200000, 1000) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
//                TcpClient.instance.close();
                confirmCancel();
//                PollingUtils.stopPollingService(context, PollingService.class, PollingService.ACTION);
            }
        }.start();
    }


    private void startTcp() {
        TcpClient.instance.connect(Constant.Urls.TCP_HOST, Constant.Urls.TCP_PORT);
        LoginRequest req = new LoginRequest();
        req.token = XmlCache.getInstance().getString(com.gsh.base.Constant.HttpConstants.KEY_TOKEN);
        try {
            TcpClient.instance.sendMsg(req.build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private FetchDataListener orderDetailResponse = new FetchDataListener() {
        @Override
        public void onSuccess(ApiResult apiResult) {
            dismissProgressDialog();
            order = apiResult.getModel(Order.class);
            if (order.isNew()) {
                ((TextView) findViewById(R.id.label_title)).setText("项目名称:" + order.typeName);
                setText(R.id.label_time, order.getDate());
                ((TextView) findViewById(R.id.label_price)).setText(order.getPriceString());
                orderListViewModel.fetchWorkers(String.valueOf(order.lat), String.valueOf(order.lng), workersResponse);
            } else {
                confirm();
                PollingUtils.stopPollingService(context, PollingService.class, PollingService.ACTION);
            }
        }

        @Override
        public void onFailure(String description) {
            dismissProgressDialog();
        }
    };

    private FetchDataListener workersResponse = new FetchDataListener() {
        @Override
        public void onSuccess(ApiResult apiResult) {
            dismissProgressDialog();
            List<Worker> workers = apiResult.getModels(Worker.class);
            drawWorkers(workers);
        }

        @Override
        public void onFailure(String description) {
            dismissProgressDialog();
        }
    };

    private void drawWorkers(List<Worker> workers) {
        for (Worker worker : workers) {

            aMap.addMarker(new MarkerOptions()
                    .position(new LatLng(worker.latitude, worker.longitude))
                    .icon(BitmapDescriptorFactory
                            .fromView(getWorkerInfo(worker))));
//                marker.setObject(vender.getVendorID());

        }
    }

    private View getWorkerInfo(Worker worker) {
        View workerInfo = LayoutInflater.from(this).inflate(
                R.layout.item_worker, null);
        TextView name = (TextView) workerInfo.findViewById(R.id.worker_name);
        name.setText(worker.name);
        return workerInfo;
    }


    private void initMap() {
        if (aMap == null) {
            aMap = mapView.getMap();
            point = new LatLonPoint(30.6586, 104.0647);
        }
        setUpMap();
    }

    private void setUpMap() {
        aMap.setLocationSource(this);
        aMap.getUiSettings().setMyLocationButtonEnabled(false);
        aMap.setOnMapTouchListener(this);
        aMap.setMyLocationEnabled(true);
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(point.getLatitude(), point.getLongitude()), 15f));
//		searAround();
    }

    @Override
    protected void onRightActionPressed() {
        super.onRightActionPressed();
        cancel();
    }

    private void confirm() {
        confirm(new CallBack() {
            @Override
            public void call() {
                go.name(OrderDetailActivity.class).param(String.class.getName(), String.valueOf(order.id)).goAndFinishCurrent();
            }
        }, "您的订单已经受理，点击刷新");
    }

    private void confirmCancel() {
        confirm(new CallBack() {
            @Override
            public void call() {
                finish();
            }
        }, "您的订单已经取消");
    }

    private void cancel() {
        notice(new CallBack() {
            @Override
            public void call() {
                showProgressDialog();
                orderListViewModel.cancel(order, cancelResponse);
            }
        }, "确定取消订单？");
    }

    private FetchDataListener cancelResponse = new FetchDataListener() {
        @Override
        public void onSuccess(ApiResult apiResult) {
            toast("取消订单成功");
            dismissProgressDialog();
        }

        @Override
        public void onFailure(String description) {
            toast("取消订单失败");
            dismissProgressDialog();
        }
    };

    @SuppressWarnings("static-access")
    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        // if (point == null && mAMapLocationManager == null) {
        // mAMapLocationManager = LocationManagerProxy.getInstance(this);
        // mAMapLocationManager.requestLocationData(
        // LocationProviderProxy.AMapNetwork, 60 * 1000, 10, this);
        // } else {
        // aMap.moveCamera(new CameraUpdateFactory().newLatLngZoom(new LatLng(
        // point.getLatitude(), point.getLongitude()), 15f));
        // searAround();
        // }
    }

    @Override
    public void deactivate() {
        mListener = null;
        if (mAMapLocationManager != null) {
            mAMapLocationManager.removeUpdates(this);
            mAMapLocationManager.destroy();
        }
        mAMapLocationManager = null;
    }


    @Override
    public void onResume() {
        super.onResume();
        XmlCache.getInstance().putBoolean(OrderDetailNewActivity.class.getName(), true);
        mapView.onResume();
    }


    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        XmlCache.getInstance().putBoolean(OrderDetailNewActivity.class.getName(), false);
        deactivate();
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }


    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }


    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mListener != null && amapLocation != null) {
            if (amapLocation != null
                    && amapLocation.getAMapException().getErrorCode() == 0) {
                mListener.onLocationChanged(amapLocation);// 閺勫墽銇氱化鑽ょ埠鐏忓繗鎽戦悙锟�??
                point = new LatLonPoint(amapLocation.getLatitude(),
                        amapLocation.getLongitude());
//				cityName=amapLocation.getCity();
//				OwnApplication.getInstance().setCity(amapLocation.getCity());
//				doSmonething(amapLocation);
            } else {
                Log.e("AmapErr", "Location ERR:"
                        + amapLocation.getAMapException().getErrorCode());
            }
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back_layout:
                onBackPressed();
                break;
        }
    }


    @Override
    public void onTouch(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                LatLng ll = aMap.getCameraPosition().target;
                point = new LatLonPoint(ll.latitude, ll.longitude);
//			searAround();
                break;
        }
    }
}
