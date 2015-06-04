package com.gsh.kuaixiu.activity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.services.core.PoiItem;
import com.gsh.base.https.ApiResult;
import com.gsh.base.model.FetchDataListener;
import com.gsh.base.viewmodel.ImageChooseViewModel;
import com.gsh.kuaixiu.Constant;
import com.gsh.kuaixiu.R;
import com.gsh.kuaixiu.activity.dialog.NoTitleListViewDialog;
import com.gsh.kuaixiu.model.PostViewModel;
import com.gsh.kuaixiu.model.OrderListViewModel.Order;
import com.gsh.kuaixiu.model.PostViewModel.Entry;

import java.util.List;

/**
 * @author Tan Chunmao
 */
public class PostActivity extends KuaixiuActivityBase {
    private PostViewModel postViewModel;
    private ImageChooseViewModel imageChooseViewModel;
    private Entry entry;
    private LocationManagerProxy mLocationManagerProxy;
    private TextView addressLabel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        imageChooseViewModel = new ImageChooseViewModel(this);
//        String typeLabel = getIntent().getStringExtra(String.class.getName());
        setTitleBar("发布需求");

        entry = new Entry();
        postViewModel = new PostViewModel();
//        postViewModel.prepareData();
        findViewById(R.id.click_choose).setOnClickListener(onClickListener);
        findViewById(R.id.click_login).setOnClickListener(onClickListener);
        findViewById(R.id.click_map).setOnClickListener(onClickListener);
        addressLabel = (TextView) findViewById(R.id.label_address);


        if (mLocationManagerProxy == null) {
            mLocationManagerProxy = LocationManagerProxy.getInstance(context);
            mLocationManagerProxy.requestLocationData(
                    LocationProviderProxy.AMapNetwork, 2 * 1000, 10, aMapLocationListener);
        }
    }

    private AMapLocationListener aMapLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if (aMapLocation != null) {
                mLocationManagerProxy.removeUpdates(aMapLocationListener);

                addressLabel.setText(aMapLocation.getAddress());
                entry.latitude = String.valueOf(aMapLocation.getLatitude());
                entry.longitude = String.valueOf(aMapLocation.getLongitude());
                entry.address=aMapLocation.getAddress();
                postViewModel.fetchData(aMapLocation.getCity(), fetchDataListener);
            }
        }

        @Override
        public void onLocationChanged(Location location) {
            System.out.println();
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    private NoTitleListViewDialog dialog;
    private FetchDataListener fetchDataListener = new FetchDataListener() {
        @Override
        public void onSuccess(ApiResult apiResult) {
            List<PostViewModel.Data> dataList = apiResult.getModels(PostViewModel.Data.class);
            final DialogAdapter adapter = new DialogAdapter();
            adapter.setData(dataList);
            dialog = new NoTitleListViewDialog.Builder(context)
                    .setOnItemSelected(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            dialog.dismiss();
                            setType(position, adapter);
                        }
                    })
                    .setAdapter(adapter).setTitle("选择维修类型").create();
            setType(0, adapter);
        }

        @Override
        public void onFailure(String description) {

        }
    };

    private void setType(int position, DialogAdapter adapter) {
        PostViewModel.Data item = adapter.getItem(position);
        ((TextView) findViewById(R.id.label_name)).setText(item.name);
        ((TextView) findViewById(R.id.click_login)).setText(String.format("预计费用%.2f元，发布需求", item.price));
        entry.type = item;
    }

    public class DialogAdapter extends BaseAdapter {

        private List<PostViewModel.Data> items;

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public PostViewModel.Data getItem(int position) {
            return items.get(position);
        }

        public void setData(List<PostViewModel.Data> item) {
            this.items = item;
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_label, null);
            }
            TextView text = (TextView) convertView;
            text.setText(items.get(position).name);
            text.setTag(items.get(position));
            return convertView;
        }
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (R.id.click_choose == v.getId()) {
                if (dialog != null)
                    dialog.show();
            } else if (R.id.click_login == v.getId()) {
//                postViewModel.entry.address=((EditText)findViewById(R.id.input_address)).getText().toString();

                entry.description = ((EditText) findViewById(R.id.input_description)).getText().toString();
                entry.imageUris = imageChooseViewModel.getImageUris();
                showProgressDialog();
                postViewModel.submitData(entry, submitResponse);
            } else if (R.id.click_map == v.getId()) {
                go.name(SearchLocationActivity.class).goForResult(Constant.Request.LOCATION);
            }
        }
    };

    private FetchDataListener submitResponse = new FetchDataListener() {
        @Override
        public void onSuccess(ApiResult apiResult) {
            dismissProgressDialog();
            toast("发布成功");
            Order order = apiResult.getModel(Order.class);
            order.typeName=entry.type.name;
            order.createdDate=System.currentTimeMillis();
            order.price=entry.type.price;
            go.name(OrderDetailNewActivity.class).param(Order.class.getName(), order).goAndFinishCurrent();
        }

        @Override
        public void onFailure(String description) {

            dismissProgressDialog();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK) {
            if(requestCode== Constant.Request.LOCATION){
                PoiItem poiItem= data.getParcelableExtra(PoiItem.class.getName());
                addressLabel.setText(poiItem.getTitle());
                entry.latitude = String.valueOf(poiItem.getLatLonPoint().getLatitude());
                entry.longitude = String.valueOf(poiItem.getLatLonPoint().getLongitude());
                entry.address=poiItem.getTitle();
            }
        }
    }
}
