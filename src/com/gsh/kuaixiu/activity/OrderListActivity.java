package com.gsh.kuaixiu.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.gsh.kuaixiu.Constant;
import com.gsh.kuaixiu.R;
import com.gsh.base.alipay.AlipayReponseHandler;
import com.gsh.base.alipay.AlipayUtils;
import com.gsh.base.alipay.PayResponseListener;
import com.gsh.base.https.ApiResult;
import com.gsh.base.model.FetchDataListener;
import com.gsh.kuaixiu.model.OrderListViewModel;
import com.gsh.kuaixiu.model.OrderListViewModel.Order;
import com.litesuits.android.widget.pullrefresh.PullToRefreshBase;
import com.litesuits.android.widget.pullrefresh.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Tan Chunmao
 */
public class OrderListActivity extends KuaixiuActivityBase {
    private int page;
    private OrderListViewModel orderListViewModel;
    private PullToRefreshListView mPullListView;
    private MyAdapter adapter;

    private AlipayReponseHandler alipayResultHandler;
    private PayResponseListener payResponseListener = new PayResponseListener() {
        public void handleMessage(Message msg) {
            String toast = AlipayUtils.handleResponse(msg);
            toast(toast);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        orderListViewModel = new OrderListViewModel();
        setContentView(R.layout.activity_order_list);
        setTitleBar("维修订单");

        mPullListView = (PullToRefreshListView) findViewById(R.id.pull_list);
        mPullListView.setPullRefreshEnabled(true);
        mPullListView.setPullLoadEnabled(false);
        mPullListView.setScrollLoadEnabled(true);
        mPullListView.setOnRefreshListener(onRefreshListener);
        ListView listView = mPullListView.getRefreshableView();
        listView.setCacheColorHint(Color.parseColor("#00000000"));
        listView.setDivider(null);
        listView.setDividerHeight(0);
//        listView.setOnScrollListener(onScrollListener);
        adapter = new MyAdapter(context);
        listView.setAdapter(adapter);
        alipayResultHandler = new AlipayReponseHandler(payResponseListener);
        mPullListView.doPullRefreshing(true, 100);
    }

    private PullToRefreshBase.OnRefreshListener<ListView> onRefreshListener = new PullToRefreshBase.OnRefreshListener<ListView>() {
        @Override
        public void onPullDownToRefresh(PullToRefreshBase refreshView) {
            page = 0;
            orderListViewModel.fetchOrderList(page, fetchDataListener);
        }

        @Override
        public void onPullUpToRefresh(PullToRefreshBase refreshView) {
            page++;
            orderListViewModel.fetchOrderList(page, fetchDataListener);
        }
    };

    private FetchDataListener fetchDataListener = new FetchDataListener() {
        @Override
        public void onSuccess(ApiResult apiResult) {
            List<Order> orderList = apiResult.getModels(Order.class);
//            Collections.shuffle(dataList);
            mPullListView.onPullDownRefreshComplete();
            mPullListView.onPullUpRefreshComplete();
            mPullListView.setHasMoreData(orderList!=null && orderList.size()==20);
            adapter.setMenuData(page == 0, orderList);
        }

        @Override
        public void onFailure(String description) {

        }
    };


    class MyAdapter extends BaseAdapter {
        private List<Order> menuOrder;
        private Activity activity;

        public MyAdapter(Activity activity) {
            this.activity = activity;
            menuOrder = new ArrayList<Order>();
        }

        public void setMenuData(boolean refresh, List<Order> menuOrder) {
            if (refresh) {
                this.menuOrder.clear();
            }
            this.menuOrder.addAll(menuOrder);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return menuOrder.size();
        }

        @Override
        public Order getItem(int position) {
            return menuOrder.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = activity.getLayoutInflater().inflate(R.layout.item_order_new, null);
                holder = new ViewHolder();
                holder.container = convertView.findViewById(R.id.container_order);
                holder.nameView = (TextView) convertView.findViewById(R.id.label_name);
                holder.workerContainer = convertView.findViewById(R.id.container_worker);
                holder.workerNameView = (TextView) convertView.findViewById(R.id.label_worker_name);
                holder.snView = (TextView) convertView.findViewById(R.id.label_order_no);
//                holder.workerPhoneView = (TextView) convertView.findViewById(R.id.label_worker_phone);
                holder.priceView = (TextView) convertView.findViewById(R.id.label_price);
                holder.timeView=(TextView)convertView.findViewById(R.id.label_time);
                holder.avatarView=(ImageView)convertView.findViewById(R.id.avatar);

                holder.stateView = (TextView) convertView.findViewById(R.id.label_state);
//                holder.actionView = (TextView) convertView.findViewById(R.id.click);
//                holder.actionView.setOnClickListener(onClickListener);
                holder.container.setOnClickListener(onClickListener);
                holder.bottomView = convertView.findViewById(R.id.bottom);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            Order order = getItem(position);
            holder.container.setTag(order);
            holder.nameView.setText(order.typeName);
            holder.stateView.setText(order.getOrderStateString());
//            holder.actionView.setTag(order);
            holder.timeView.setText(order.getDate());
//            if (order.isCancelable() || order.isPayable()) {
//                holder.actionView.setVisibility(View.VISIBLE);
//                holder.actionView.setText(order.getActionString());
//            } else {
//                holder.actionView.setVisibility(View.GONE);
//            }

            holder.priceView.setText(order.getPriceString());
            holder.snView.setText("订单号："+order.sn);
            if (order.hasWorker()) {
                holder.workerContainer.setVisibility(View.VISIBLE);
                String text = String.format("工作人员：%1$s\n联系方式：%2$s",order.workerName,order.workerMobile);
                holder.workerNameView.setText(text);
//                loadAvatar(holder.avatarView, order.workerAvatar);
//                holder.workerPhoneView.setText(order.workerPhone);
            } else {
                holder.workerContainer.setVisibility(View.GONE);
            }

            return convertView;
        }


        private class ViewHolder {
            public View container;
            public TextView nameView;
            public TextView stateView;
            public TextView snView;
//            public TextView actionView;
            public View workerContainer;
            public TextView workerNameView;
//            public TextView workerPhoneView;
            public TextView priceView;
            public TextView timeView;
            public View bottomView;
            public ImageView avatarView;
        }
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
             if (R.id.container_order == v.getId()) {
                Order order = (Order) v.getTag();
                if (order.isNew()) {
                    go.name(OrderDetailNewActivity.class).param(String.class.getName(), String.valueOf(order.id)).go();
                } else {
                    go.name(OrderDetailActivity.class).param(String.class.getName(), String.valueOf(order.id)).go();
                }
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK) {
            if(requestCode== Constant.Request.ORDER_ACTION) {

            }
        }
    }

    private void cancel(final Order order) {
        String title = String.format("确定取消这个订单？");
        notice(new CallBack() {
            @Override
            public void call() {
                orderListViewModel.cancel(order, new FetchDataListener() {
                    @Override
                    public void onSuccess(ApiResult apiResult) {
                        toast("取消成功");
                    }

                    @Override
                    public void onFailure(String description) {

                    }
                });
            }
        }, title);
    }
}
