package com.gsh.base.activity.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.gsh.kuaixiu.R;
import com.gsh.base.activity.ActivityBase;
import com.gsh.kuaixiu.activity.AlipayActivity;
import com.gsh.kuaixiu.activity.AvatarActivity;
import com.gsh.kuaixiu.activity.LoginActivity;
import com.gsh.kuaixiu.activity.PictureListActivity;
import com.gsh.kuaixiu.activity.RecyclerViewLayoutActivity;
import com.gsh.kuaixiu.activity.SecurityCodeActivity;
import com.gsh.kuaixiu.activity.ShareActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tan Chunmao
 */
public class FirstFragment extends FragmentBase {
    private RecyclerView recyclerView;
    private MyAdapter adapter;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (layout != null) {
            ViewGroup parent = (ViewGroup) layout.getParent();
            if (parent != null)
                parent.removeView(layout);
            return layout;
        }
        layout = inflater.inflate(R.layout.fragment_first, container, false);
        List<ActionItem> temp = new ArrayList<ActionItem>();
        temp.add(new ActionItem("图片测试", PictureListActivity.class));
        temp.add(new ActionItem("注册测试", SecurityCodeActivity.class));
        temp.add(new ActionItem("登录测试", LoginActivity.class));
        temp.add(new ActionItem("刷新测试",RecyclerViewLayoutActivity.class));
        temp.add(new ActionItem("图片选择测试",AvatarActivity.class));
        temp.add(new ActionItem("分享测试",ShareActivity.class));
        temp.add(new ActionItem("支付测试",AlipayActivity.class));
        adapter=new MyAdapter(temp);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        recyclerView = (RecyclerView) findViewById(R.id.recycle);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        return layout;
    }

    @Override
    public void refresh() {

    }


    @Override
    public void onResume() {
        super.onResume();

    }


    @Override
    public void onPause() {
        super.onPause();

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    private class ActionItem {
        public String name;
        public Class<? extends ActivityBase> aClass;

        public ActionItem(String name, Class<? extends ActivityBase> aClass) {
            this.name = name;
            this.aClass = aClass;
        }
    }

    private class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        public List<ActionItem> data;

        public MyAdapter(List<ActionItem> data) {
            this.data = data;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            return new ViewHolderA(activity.getLayoutInflater().inflate(R.layout.item_action, viewGroup, false));
        }


        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
            ViewHolderA viewHolderA = (ViewHolderA) viewHolder;
            ActionItem item = data.get(position);
            viewHolderA.textView.setText(item.name);
            viewHolderA.actionView.setTag(item);
            viewHolderA.actionView.setOnClickListener(actionClickListener);
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        public class ViewHolderA extends RecyclerView.ViewHolder {
            // each data item is just a string in this case

            public TextView textView;
            public View actionView;

            public ViewHolderA(View v) {
                super(v);
                textView = (TextView) v.findViewById(R.id.label);
                actionView=v.findViewById(R.id.action);
            }
        }
    }

    private View.OnClickListener actionClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ActionItem actionItem = (ActionItem) v.getTag();
            startActivity(new Intent(activity,actionItem.aClass));
        }
    };
}
