package com.gsh.kuaixiu.activity;

import android.os.Bundle;
import android.view.View;
import com.gsh.base.https.ApiResult;
import com.gsh.base.model.FetchDataListener;
import com.gsh.kuaixiu.R;
import com.gsh.kuaixiu.model.OrderListViewModel;
import com.gsh.kuaixiu.model.OrderListViewModel.CommentEntry;

/**
 * @author Tan Chunmao
 */
public class CommentActivity extends KuaixiuActivityBase {
    private View[] ratingStars;
    private Integer rate;
    private OrderListViewModel orderListViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        orderListViewModel = new OrderListViewModel();
        setContentView(R.layout.activity_comment);
        setTitleBar(false, "发表评论", RightAction.TEXT, "提交");

        initStars();

    }


    private void initStars() {
        ratingStars = new View[]{findViewById(R.id.rating_a), findViewById(R.id.rating_b), findViewById(R.id.rating_c), findViewById(R.id.rating_d), findViewById(R.id.rating_e)};
        Integer score = 1;
        for (View view : ratingStars) {
            view.setOnClickListener(onClickListener);
            view.setTag(score);
            view.setSelected(true);
            score++;
        }
        rate = 5;
    }


    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (R.id.rating_a == view.getId() || R.id.rating_b == view.getId() || R.id.rating_c == view.getId() || R.id.rating_d == view.getId() || R.id.rating_e == view.getId()) {
                rate = (Integer) view.getTag();
                rating();
            }
        }

    };


    @Override
    protected void onRightActionPressed() {
        super.onRightActionPressed();
        CommentEntry commentEntry = new CommentEntry();
        commentEntry.orderId = getIntent().getStringExtra(String.class.getName());
        commentEntry.star=String.valueOf(rate);
        commentEntry.content=getInput(R.id.input_description);

        showProgressDialog();
        orderListViewModel.comment(commentEntry, commentResponse);
    }

    private FetchDataListener commentResponse = new FetchDataListener() {
        @Override
        public void onSuccess(ApiResult apiResult) {
            dismissProgressDialog();
            toast("感谢您的反馈");
            finish();
        }

        @Override
        public void onFailure(String description) {
            dismissProgressDialog();
        }
    };

    private void rating() {
        for (View view : ratingStars) {
            Integer score = (Integer) view.getTag();
            view.setSelected(score <= rate);
        }
    }
}
