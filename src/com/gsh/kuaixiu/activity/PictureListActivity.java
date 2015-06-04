package com.gsh.kuaixiu.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.gsh.kuaixiu.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tan Chunmao
 */
public class PictureListActivity extends KuaixiuActivityBase {
    private RecyclerView recyclerView;
    private MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        loginRequest = new NetRequest(Urls.COMBO_LIST, netRequestListener);

        setContentView(R.layout.test_activity_picture_list);
        setTitleBar("图片测试");
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView = (RecyclerView) findViewById(R.id.recycle);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter=new MyAdapter();
        recyclerView.setAdapter(adapter);
        fetchData();
    }

    private void fetchData() {
//        Request req = new Request(loginRequest.getUrl());

//        execute(req,
//                new HttpResultHandler() {
//                    @Override
//                    protected void onSuccess(ApiResult apiResult) {
//                        List<M43> list = apiResult.getModels(M43.class);
//                        List<String> data = new ArrayList<String>();
//                        for(M43 m43: list) {
//                            data.add(m43.mainPicturePath);
//                        }
//                        adapter.setData(data);
//                    }
//
//                    @Override
//                    protected void onUnknownError() {
//                        Log.d("test","failure");
//                    }
//                }
//        );
    }

    private class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private List<String> data;

        public MyAdapter() {
            data = new ArrayList<String>();
        }

        public void setData(List<String> list) {
            data.clear();
            data.addAll(list);
            notifyDataSetChanged();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            return new ViewHolderA(getLayoutInflater().inflate(R.layout.test_item_picture, viewGroup, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
            ViewHolderA viewHolderA = (ViewHolderA) viewHolder;
            String item = data.get(position);
            setGalleryTag(viewHolderA.imageView, data, position);
            loadImage(viewHolderA.imageView,item);
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        public class ViewHolderA extends RecyclerView.ViewHolder {
            // each data item is just a string in this case

            public ImageView imageView;
            public ViewHolderA(View v) {
                super(v);
                imageView = (ImageView) v.findViewById(R.id.image);
            }
        }
    }


//    private NetRequestListener netRequestListener = new NetRequestListener() {
//        @Override
//        public void request(NetRequest netRequest) {
//            String url = netRequest.getUrl();
//            if (Urls.COMBO_LIST.equals(url)) {
//                fetchOrderList();
//            }
//        }
//    };
}

