package com.gsh.kuaixiu.activity;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiItemDetail;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.poisearch.PoiSearch.OnPoiSearchListener;
import com.amap.api.services.poisearch.PoiSearch.SearchBound;
import com.gsh.kuaixiu.R;

import java.util.ArrayList;
import java.util.List;

public class SearchLocationActivity extends KuaixiuActivityBase implements LocationSource,
		AMapLocationListener, OnClickListener,
		OnPoiSearchListener, AMap.OnMapTouchListener {
	private MapView mapView;
	private LocationSource.OnLocationChangedListener mListener;
	private LocationManagerProxy mAMapLocationManager;
	private EditText searchText;// 鏉堟挸鍙嗛幖婊呭偍閸忔娊鏁�??锟斤�?

	private TextView clearBtn;
	private ListView searchList;
	private ArrayList<String> locationList;
	private LatLonPoint point;

	private AMap aMap;

	private PoiResult poiResult; // poi鏉╂柨娲栭惃鍕波閺嬶拷
	private PoiSearch.Query query;// Poi閺屻儴顕楅弶鈥叉缁拷
	private PoiSearch poiSearch;// POI閹兼粎锟�??
	private int type;
	private String cityName;
	private MyAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_search_location);
		setTitleBar("设置位置");
		mapView = (MapView) findViewById(R.id.location_map);
		mapView.onCreate(savedInstanceState);
		init();
	}

	private void init() {
		initView();
//		initData();
		initMap();
		setListener();
	}

	private void initView() {

		clearBtn = (TextView) findViewById(R.id.location_clear);
		searchText = (EditText) findViewById(R.id.location_keyWord);
		searchList = (ListView) findViewById(R.id.around_list);
		adapter=new MyAdapter(context);
		searchList.setAdapter(adapter);
		searchList.setOnItemClickListener(onItemClickListener);


		searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					String text = searchText.getText().toString();
					if(!TextUtils.isEmpty(text)) {
						doSearchQuery(text);
					}
					return true;
				}
				return false;
			}
		});
	}

	private OnItemClickListener onItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
			Intent intent = new Intent();
			intent.putExtra(PoiItem.class.getName(), adapter.getItem(i));
			setResult(RESULT_OK, intent);
			finish();
		}
	};


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
		aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
				new LatLng(point.getLatitude(), point.getLongitude()), 15f));
		searAround();
	}

	private void setListener() {
//		searchText.addTextChangedListener(this);

		clearBtn.setOnClickListener(this);
	}

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
		mapView.onResume();
	}


	@Override
	public void onPause() {
		super.onPause();
		mapView.onPause();
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
				cityName=amapLocation.getCity();
//				OwnApplication.getInstance().setCity(amapLocation.getCity());
				doSmonething(amapLocation);
			} else {
				Log.e("AmapErr", "Location ERR:"
						+ amapLocation.getAMapException().getErrorCode());
			}
		}
	}

	private void doSmonething(AMapLocation amapLocation) {
		aMap.clear();
		String desc = amapLocation.getRoad() + amapLocation.getStreet();
		searchText.setText(desc);
		searAround();
	}

	private void searAround() {
		query = new PoiSearch.Query("", "", "");// 缁楊兛绔存稉顏勫棘閺佹媽銆冪粈鐑樻偝缁便垹鐡х粭锔胯閿涘瞼顑囨禍灞奸嚋閸欏�?�鏆熺悰銊с仛poi閹兼粎鍌ㄧ猾璇诧�??锟介敍锟�??顑囨稉澶夐嚋閸欏倹鏆熺悰銊с仛poi閹兼粎鍌ㄩ崠鍝勭厵閿涘牏鈹栵�??锟芥顑佹稉韫敩鐞涖劌鍙忛崶鏂ょ礆
		query.setPageSize(20);
		query.setPageNum(0);
		poiSearch = new PoiSearch(this, query);
		poiSearch.setBound(new SearchBound(point, 2000, true));
		poiSearch.setOnPoiSearchListener(this);
		poiSearch.searchPOIAsyn();
		setSearchType(0);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_back_layout:
			onBackPressed();
			break;
		case R.id.location_clear:
			searchText.setText("");
			searchList.setVisibility(View.GONE);
			break;
		}
	}



	private void doSearchQuery(String keyWord) {
//		String cityName = OwnApplication.getInstance().getCity();
		if(cityName != null&&!cityName.equals("")){
			query = new PoiSearch.Query(keyWord, "", cityName);
		}else{
			query = new PoiSearch.Query(keyWord, "", "成都");
		}
		query.setPageSize(20);
		query.setPageNum(0);
		poiSearch = new PoiSearch(this, query);
		poiSearch.setOnPoiSearchListener(this);
		poiSearch.searchPOIAsyn();
		setSearchType(1);
	}



	public void setSearchType(int type) {
		this.type = type;
	}

	@Override
	public void onPoiItemDetailSearched(PoiItemDetail arg0, int arg1) {
	}

	@Override
	public void onPoiSearched(PoiResult result, int rCode) {
		if (rCode == 0) {
			if (result != null && result.getQuery() != null) {
				if (result.getQuery().equals(query)) {
					poiResult = result;
					final List<PoiItem> poiItems = poiResult.getPois();
					if (poiItems != null && poiItems.size() > 0) {
						PoiItem firstPoiItem = poiItems.get(0);
						point=firstPoiItem.getLatLonPoint();
						aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(point.getLatitude(), point.getLongitude()), 15f));
						adapter.setData(poiItems);
					}
				}
			}
		}
	}


	@Override
	public void onTouch(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_UP:
			LatLng ll = aMap.getCameraPosition().target;
			point = new LatLonPoint(ll.latitude, ll.longitude);
			searAround();
			break;
		}
	}



	static class MyAdapter extends BaseAdapter{
		private List<PoiItem> data;
		private Activity context;
		public MyAdapter(Activity context) {
			data=new ArrayList<PoiItem>();
			this.context=context;
		}

		public void setData(List<PoiItem> data){
			this.data.clear();
			this.data.addAll(data);
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return data.size();
		}

		@Override
		public PoiItem getItem(int i) {
			return data.get(i);
		}

		@Override
		public long getItemId(int i) {
			return i;
		}


		@Override
		public View getView(int i, View convertView, ViewGroup viewGroup) {
			if (convertView == null) {
				convertView = context.getLayoutInflater().inflate(R.layout.item_label, null);
			}
			TextView text = (TextView) convertView;
			text.setText(getItem(i).getTitle());

			return convertView;
		}
	}
}
