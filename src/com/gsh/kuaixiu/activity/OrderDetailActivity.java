package com.gsh.kuaixiu.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.gsh.base.https.ApiResult;
import com.gsh.base.model.FetchDataListener;
import com.gsh.kuaixiu.R;
import com.gsh.kuaixiu.model.OrderListViewModel;
import com.gsh.kuaixiu.model.OrderListViewModel.OrderState;
import com.gsh.kuaixiu.model.OrderListViewModel.Order;
import com.litesuits.android.widget.ViewUtils;
import com.litesuits.common.cache.XmlCache;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * @author Tan Chunmao
 */
public class OrderDetailActivity extends KuaixiuActivityBase {
    private Order order;
    private OrderListViewModel orderListViewModel;
    private View actionContainer;
    private TextView actionView;
    private View callActionView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        orderListViewModel = new OrderListViewModel();
        setContentView(R.layout.activity_order_detail);
        setTitleBar("订单详情", "取消");
        showRightAction(false);
        order = (Order) getIntent().getSerializableExtra(Order.class.getName());

        actionContainer = findViewById(R.id.container_action);
        actionView = (TextView) findViewById(R.id.click_action);

        actionContainer.setVisibility(View.GONE);
        actionView.setOnClickListener(onClickListener);
        callActionView = findViewById(R.id.click_phone);
        callActionView.setOnClickListener(onClickListener);

        showProgressDialog();
        orderListViewModel.orderDetail(String.valueOf(getIntent().getStringExtra(String.class.getName())), orderDetailResponse);

        int imageSize = initImageSize();
        imageParams = new LinearLayout.LayoutParams(imageSize, imageSize);
    }

    private FetchDataListener orderDetailResponse = new FetchDataListener() {
        @Override
        public void onSuccess(ApiResult apiResult) {
            dismissProgressDialog();

            order = apiResult.getModel(Order.class);

            int test = XmlCache.getInstance().getInt("test");
            test++;
            XmlCache.getInstance().putInt("test", test);
            order.status = OrderState.values()[test % 4 + 1].name();

            updateContent();

            final String[] fruit_pictures = {"http://img0.imgtn.bdimg.com/it/u=2251909169,311295289&fm=21&gp=0.jpg", "http://img1.imgtn.bdimg.com/it/u=3583647138,2653046993&fm=21&gp=0.jpg", "http://img5.imgtn.bdimg.com/it/u=3322368831,3685505520&fm=21&gp=0.jpg"};
            LinearLayout linearLayout = (LinearLayout) findViewById(R.id.images);
            List<String> paths = new ArrayList<String>(Arrays.asList(fruit_pictures));
            fillImages(linearLayout, paths);
        }

        @Override
        public void onFailure(String description) {
            dismissProgressDialog();
        }
    };


    @Override
    protected void onRightActionPressed() {
        super.onRightActionPressed();
        if (order.isPaid()) {
            comment();
        } else  {
            notice(new CallBack() {
                @Override
                public void call() {
                    orderListViewModel.cancel(order, cancelResponse);
                }
            }, "确定取消订单？");
        }
    }

    private void comment() {

    }

    private FetchDataListener cancelResponse = new FetchDataListener() {
        @Override
        public void onSuccess(ApiResult apiResult) {
            toast("取消订单成功");
            dismissProgressDialog();
            order.cancel();
            updateContent();
        }

        @Override
        public void onFailure(String description) {
            toast("取消订单失败");
            dismissProgressDialog();
        }
    };

    private void updateContent() {
        if (order.isWaitStart()) {
            showRightAction(true);
            callActionView.setVisibility(View.VISIBLE);
            callActionView.setTag(order.workerMobile);
            actionView.setText("开始维修");
        } else if (order.isWorking()) {
            showRightAction(true);
            actionContainer.setVisibility(View.VISIBLE);
            actionView.setText("支付");
        } else if (order.isCancelled()) {
            showRightAction(false);
            actionContainer.setVisibility(View.GONE);
        } else if (order.isPaid()) {
            setText(R.id.title_bar_action_text, "评论");
            showRightAction(true);
            actionContainer.setVisibility(View.GONE);
        }

        setText(R.id.label_name, order.typeName);
        ((TextView) findViewById(R.id.label_state)).setText("订单状态：" + order.getOrderStateString());
        setText(R.id.label_time, order.getDate());
        ((TextView) findViewById(R.id.label_price)).setText(order.getPriceString());
        ((TextView) findViewById(R.id.label_address)).setText("地址：" + order.address);
        setText(R.id.label_order_no, "订单号："+order.sn);


        if (order.hasWorker()) {
            String text = String.format("工作人员：%1$s\n联系方式：%2$s", order.workerName, order.workerMobile);
            ((TextView) findViewById(R.id.label_worker_name)).setText(text);
//            setText(R.id.label_worker_phone, order.workerPhone);
        }

    }

    private FetchDataListener startResponse = new FetchDataListener() {
        @Override
        public void onSuccess(ApiResult apiResult) {
            dismissProgressDialog();
            order.start();
            updateContent();
        }

        @Override
        public void onFailure(String description) {
            dismissProgressDialog();
            toast("请示失败");

        }
    };

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (R.id.click_action == view.getId()) {
                if (order.isWaitStart()) {
                    showProgressDialog();
                    orderListViewModel.start(String.valueOf(order.id), startResponse);
                } else if (order.isWorking()) {
                    showProgressDialog();
                    pay();
                }
            } else if (R.id.click_phone == view.getId()) {
                String phone = (String) view.getTag();
                String uri = "tel:" + phone;
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse(uri));
                startActivity(intent);
            }
        }
    };

    private void pay() {
        orderListViewModel.test(order, new FetchDataListener() {
            @Override
            public void onSuccess(ApiResult apiResult) {
                dismissProgressDialog();
                toast("支付成功");
                order.pay();
                updateContent();
            }

            @Override
            public void onFailure(String description) {
                dismissProgressDialog();
            }
        });
    }

    private LinearLayout.LayoutParams imageParams;


    private int initImageSize() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int maxWidth = metrics.widthPixels;
        int edgePadding = getResources().getDimensionPixelOffset(R.dimen.ui_margin_d);
        int gapWidth = getResources().getDimensionPixelOffset(R.dimen.ui_margin_d);
        int columns = 4;
        return (int) (((float) maxWidth - gapWidth * (columns - 1) - edgePadding * 2) / columns);
    }

    private void fillImages(LinearLayout layout, List<String> paths) {
        if (paths != null && paths.size() > 0) {
            List<ImageView> views = new ArrayList<ImageView>();
            layout.removeAllViews();
            for (String path : paths) {
                ImageView imageView = (ImageView) getLayoutInflater().inflate(R.layout.image, null);
                imageView.setLayoutParams(imageParams);
                setGalleryTag(imageView, paths, paths.indexOf(path));

                loadImage(imageView, path);
                views.add(imageView);
            }
            int gapWidth = getResources().getDimensionPixelOffset(R.dimen.ui_margin_d);
            ViewUtils.addViews(context, layout, views, imageParams.width, Gravity.LEFT, gapWidth);
        }
    }
}
