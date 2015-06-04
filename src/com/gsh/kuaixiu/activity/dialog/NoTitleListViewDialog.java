package com.gsh.kuaixiu.activity.dialog;


import android.app.Dialog;
import android.content.Context;
import android.view.*;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.gsh.kuaixiu.R;
import com.litesuits.common.utils.DensityUtil;

public class NoTitleListViewDialog extends Dialog {

    public NoTitleListViewDialog(Context context) {
        super(context);
    }

    public NoTitleListViewDialog(Context context, int theme) {
        super(context, theme);
    }


    public static class Builder {
        private Context context;
        private AdapterView.OnItemClickListener l;
        private String title;

        private BaseAdapter adapter;

        private float width = 0.0f;
        private float height = 0.0f;
        private boolean wrapContent;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setOnItemSelected(AdapterView.OnItemClickListener l) {
            this.l = l;
            return this;
        }


        public Builder setAdapter(BaseAdapter adapter) {
            this.adapter = adapter;
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setTitle(int titleRid) {
            title = context.getString(titleRid);
            return this;
        }

        public Builder setWidth(float width) {
            this.width = width;
            return this;
        }

        public Builder setHeight(float height) {
            this.height = height;
            return this;
        }

        public Builder setWrapContent(boolean wrapContent) {
            this.wrapContent = wrapContent;
            return this;
        }

        public NoTitleListViewDialog create() {
            final LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final NoTitleListViewDialog dialog = new NoTitleListViewDialog(context,
                    R.style.MyDialog);

            View layout = inflater.inflate(R.layout.dialog_list, null);
            final TextView titleView = (TextView) layout.findViewById(R.id.text_title);
            final ListView listView = (ListView) layout.findViewById(R.id.listView);
            listView.setVerticalScrollBarEnabled(false);
            if (adapter != null) {
                listView.setAdapter(adapter);
            }
            if (l != null) {
                listView.setOnItemClickListener(l);
            }

            if (title != null) {
                titleView.setText(title);
            }
            dialog.setContentView(layout);

            Window dialogWindow = dialog.getWindow();
            WindowManager m = dialogWindow.getWindowManager();
            Display d = m.getDefaultDisplay();
            WindowManager.LayoutParams p = dialogWindow.getAttributes();
            if (width != 0.0f)
                p.width = DensityUtil.dip2px(context, width);
            else
                p.width = (int) (d.getWidth() * 0.80);

            if (height != 0.0f)
                p.height = DensityUtil.dip2px(context, height);
            else if (wrapContent)
                p.height = WindowManager.LayoutParams.WRAP_CONTENT;
            else
                p.height = (int) (d.getHeight() * 0.6);

            dialogWindow.setAttributes(p);
            return dialog;
        }
    }

}
