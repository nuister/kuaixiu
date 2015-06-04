package com.gsh.kuaixiu.model;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.gsh.base.alipay.PayItem;
import com.gsh.base.https.ApiResult;
import com.gsh.base.https.HttpResultHandler;
import com.gsh.base.model.FetchDataListener;
import com.gsh.base.viewmodel.ViewModelBase;
import com.gsh.kuaixiu.Constant;
import com.litesuits.android.async.AsyncTask;
import com.litesuits.http.request.Request;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Tan Chunmao
 */
public class OrderListViewModel extends ViewModelBase {

    public OrderListViewModel() {
    }

    static class State {
        String display;
        boolean cancelable;
        boolean payable;
        boolean hasWorker;

        public State(String display, boolean cancelable, boolean payable, boolean hasWorker) {
            this.display = display;
            this.cancelable = cancelable;
            this.payable = payable;
            this.hasWorker = hasWorker;
        }

        public State(String display, boolean cancelable, boolean payable) {
            this(display, cancelable, payable, true);
        }
    }


    public enum OrderState {
        NEW("新订单"), WAITING_REPAIR("等待维修"), PROCESS("维修中"), CANCEL("已取消"), PAID("已完成");
        private String display;

        OrderState(String display) {
            this.display = display;
        }
    }

    public static class Order implements Serializable {
        private static Map<String, State> states;

        static {
            states = new HashMap<String, State>();
            states.put("NEW", new State("新订单", true, false, false));
            states.put("WAITING_REPAIR", new State("等待维修", true, false));
            states.put("PROCESS", new State("维修中", false, true));
            states.put("CANCEL", new State("已取消", false, false));
            states.put("PAID", new State("已完成", false, false));
        }


        public String workerName;
        public String workerMobile;
        public String workerAvatar;
        public double lat;
        public double lng;

        public String address;
        public double price;


        public long id;
        public String status;
        public String sn;
        public String remark;
        public String typeName;
        public long createdDate;


        public Order() {
        }


        public PayItem toPayItem() {
            return new PayItem(typeName, typeName, price);
        }

        public boolean hasWorker() {
            return states.get(status).hasWorker;
        }

        public String getOrderStateString() {
            return states.get(status).display;
        }

        public boolean isCancelable() {
            return states.get(status).cancelable;
        }

        public boolean isPayable() {
            return states.get(status).payable;
        }

        public boolean isCommentable() {
            return status.equalsIgnoreCase("paid");
        }

        public boolean isNew() {
            return status.equalsIgnoreCase(OrderState.NEW.name());
        }

        public boolean isWaitStart() {
            return status.equalsIgnoreCase(OrderState.WAITING_REPAIR.name());
        }

        public boolean isWorking() {
            return status.equalsIgnoreCase(OrderState.PROCESS.name());
        }

        public boolean isCancelled() {
            return status.equalsIgnoreCase(OrderState.CANCEL.name());
        }

        public void cancel() {
            status=OrderState.CANCEL.name();
        }

        public void start() {
            status=OrderState.PROCESS.name();
        }

        public void pay() {
            status=OrderState.PAID.name();
        }

        public boolean isPaid() {
            return status.equalsIgnoreCase(OrderState.PAID.name());
        }

        public String getDate() {
            return "下单时间：" + new SimpleDateFormat("yyyy-MM-dd hh:mm").format(createdDate);
        }



        public String getPriceString() {
            return String.format("%.2f元", price);
        }

        public Order(long orderId, int orderState, String orderName) {
            this.id = orderId;

            this.typeName = orderName;
        }
    }

    public static class Worker implements Serializable {
        public long id;
        public String name;
        public double latitude;
        public double longitude;
    }

    public void fetchWorkers(final String lat, final String lng, final FetchDataListener fetchDataListener) {
        execute(new Request(Constant.Urls.MEMEBER_MASTER).addUrlParam("lat", lat).addUrlParam("lng", lng),
                new HttpResultHandler() {
                    @Override
                    protected void onSuccess(ApiResult apiResult) {
                        fetchDataListener.onSuccess(apiResult);
                    }

                    @Override
                    protected void onFailure(int code) {
                        String toast = "请求失败";
                        fetchDataListener.onFailure(toast);
                    }
                }
        );
    }

    public void prepareData(final int page, final FetchDataListener fetchDataListener) {
        new AsyncTask<Object, Object, ApiResult>() {
            @Override
            protected ApiResult doInBackground(Object... params) {
                Gson gson = new Gson();
                ApiResult apiResult = new ApiResult();
                List<Order> list = new ArrayList<Order>();
//                String[] ss = "apple,banana,cupcake,donut,eclair, froyo,gingerbread,honeycomb,ice_cream_sandwich,jelly_bean,kit_kat, lollipop,mum,number".split(",");
                String[] types = "开锁，换锁，修空调，修电视，修电脑".split("，");

                for (int i = 0; i < types.length; i++) {
                    Order order = new Order(i, i, types[i]);
                    order.workerName = "王师傅";
                    order.price = 50;
                    list.add(order);
                }
                String jsonStr = gson.toJson(list);
                apiResult.data = gson.fromJson(jsonStr, JsonElement.class);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return apiResult;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(ApiResult apiResult) {
                super.onPostExecute(apiResult);
                fetchDataListener.onSuccess(apiResult);
            }
        }.execute();

    }

    public void cancel(Order order, final FetchDataListener fetchDataListener) {
        execute(new Request(Constant.Urls.KUAIXIU_ORDER_CANCEL).addUrlParam("orderId", String.valueOf(order.id)),
                new HttpResultHandler() {
                    @Override
                    protected void onSuccess(ApiResult apiResult) {
                        fetchDataListener.onSuccess(apiResult);
                    }

                    @Override
                    protected void onFailure(int code) {
                        String toast = "请求失败";
                        fetchDataListener.onFailure(toast);
                    }
                }
        );
    }

    public void start(String orderId, final FetchDataListener fetchDataListener) {
        execute(new Request(Constant.Urls.KUAIXIU_ORDER_START).addUrlParam("orderId", orderId),
                new HttpResultHandler() {
                    @Override
                    protected void onSuccess(ApiResult apiResult) {
                        fetchDataListener.onSuccess(apiResult);
                    }

                    @Override
                    protected void onFailure(int code) {
                        String toast = "请求失败";
                        fetchDataListener.onFailure(toast);
                    }
                }
        );
    }

    public static class CommentEntry {
        public String orderId;
        public String content;
        public String star;
    }

    public void comment(CommentEntry commentEntry, final FetchDataListener fetchDataListener) {
        execute(new Request(Constant.Urls.KUAIXIU_ORDER_START).addUrlParam("orderId", commentEntry.orderId).addUrlParam("content", commentEntry.content).addUrlParam("star", commentEntry.star),
                new HttpResultHandler() {
                    @Override
                    protected void onSuccess(ApiResult apiResult) {
                        fetchDataListener.onSuccess(apiResult);
                    }

                    @Override
                    protected void onFailure(int code) {
                        String toast = "请求失败";
                        fetchDataListener.onFailure(toast);
                    }
                }
        );
    }

    public void orderDetail(final String orderId, final FetchDataListener fetchDataListener) {
        execute(new Request(Constant.Urls.KUAIXIU_ORDER_DETAIL).addUrlParam("orderId", String.valueOf(orderId)),
                new HttpResultHandler() {
                    @Override
                    protected void onSuccess(ApiResult apiResult) {
                        fetchDataListener.onSuccess(apiResult);
                    }

                    @Override
                    protected void onFailure(int code) {
                        String toast = "请求失败";
                        fetchDataListener.onFailure(toast);
                    }
                }
        );
    }

    public void test(final Order order, final FetchDataListener fetchDataListener) {
        new AsyncTask<Object, Object, Object>() {
            @Override
            protected Object doInBackground(Object... params) {
                return null;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                fetchDataListener.onSuccess(new ApiResult());
            }
        };
    }


    public void fetchOrderList(final int page, final FetchDataListener fetchDataListener) {
        execute(new Request(Constant.Urls.KUAIXIU_ORDER_LIST).addUrlParam("pageIndex", String.valueOf(page)),
                new HttpResultHandler() {
                    @Override
                    protected void onSuccess(ApiResult apiResult) {
                        fetchDataListener.onSuccess(apiResult);
                    }

                    @Override
                    protected void onFailure(int code) {
                        String toast = "请求失败";
                        fetchDataListener.onFailure(toast);
                    }
                }
        );
    }
}
