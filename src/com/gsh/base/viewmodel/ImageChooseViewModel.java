package com.gsh.base.viewmodel;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.gsh.kuaixiu.R;
import com.gsh.base.activity.ActivityBase;
import com.litesuits.android.widget.ViewUtils;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Tan Chunmao
 */
public class ImageChooseViewModel {

    private ActivityBase activityBase;
    private static final int image_count = 4;
    private ViewGroup.LayoutParams imageLayoutParams;
    private List<ImageView> imageViews;
    private int imageSize;


    public ImageChooseViewModel(ActivityBase activityBase) {
        this.activityBase = activityBase;
        initImageSize();
        prepareImages();
    }

    private void initImageSize() {
        DisplayMetrics metrics = activityBase.getResources().getDisplayMetrics();
        int maxWidth = metrics.widthPixels;
        int imageGap = activityBase.getResources().getDimensionPixelSize(R.dimen.ui_margin_d);
        int edgePadding = activityBase.getResources().getDimensionPixelSize(R.dimen.ui_margin_f);
        imageSize = (int) (((float) maxWidth - imageGap * (image_count - 1) - 2 * edgePadding) / image_count);
        imageLayoutParams = new RelativeLayout.LayoutParams(imageSize, imageSize);
    }

    private void prepareImages() {
        LinearLayout linearLayout = (LinearLayout) activityBase.findViewById(R.id.images);
        linearLayout.getLayoutParams().height=imageSize;
        int count = 0;
        imageViews = new ArrayList<ImageView>();
        while (count < image_count) {
            ImageView imageView = (ImageView) activityBase.getLayoutInflater().inflate(R.layout.image, null);
            imageView.setOnClickListener(onClickListener);
            imageView.setOnLongClickListener(onLongClickListener);
            imageView.setLayoutParams(imageLayoutParams);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageViews.add(imageView);
            count++;
        }
        int gapWidth = getResources().getDimensionPixelOffset(R.dimen.ui_margin_d);
        int otherViewWidth = getResources().getDimensionPixelOffset(R.dimen.ui_margin_f);
        ViewUtils.addViews(activityBase, linearLayout, imageViews, imageLayoutParams.width, Gravity.CENTER, gapWidth, otherViewWidth);
        updateImages();
    }

    private Resources getResources() {
        return activityBase.getResources();
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            clickedImageView = (ImageView) v;
            activityBase.showPictureMethodDialog(imageCallBack);
        }
    };

    private ImageView clickedImageView;
    private ImageView longClickedImageView;

    private ActivityBase.ImageCallBack imageCallBack = new ActivityBase.ImageCallBack() {
        @Override
        public void onImage(Uri uri) {
            clickedImageView.setTag(uri);
            updateImages();
        }
    };

    private View.OnLongClickListener onLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            Uri uri = (Uri) v.getTag();
            if (uri != null) {
                longClickedImageView = (ImageView) v;
                showDeleteDialog();
                return true;
            }
            return false;
        }
    };

    private void showDeleteDialog() {
        activityBase.notice(new com.gsh.base.activity.ActivityBase.CallBack() {
            @Override
            public void call() {
                delete();
            }
        }, "确定要删除图片？");
    }

    private void delete() {
        longClickedImageView.setTag(null);
        updateImages();
    }

    private void updateImages() {

        List<Uri> uriList = getImageUris();
        for (int i = 0; i < imageViews.size(); i++) {
            ImageView imageView = imageViews.get(i);
            if (uriList.size() > i) {
                Uri uri = uriList.get(i);
                imageView.setVisibility(View.VISIBLE);
                imageView.setTag(uri);
                showImage(imageView);
            } else if (i == uriList.size()) {
                imageView.setVisibility(View.VISIBLE);
                imageView.setImageResource(R.drawable.icon_add_pic_nor);
                imageView.setTag(null);
            } else {
                imageView.setVisibility(View.INVISIBLE);
                imageView.setTag(null);
            }
        }

        clickedImageView = null;
        longClickedImageView = null;

    }

    private void showImage(final ImageView imageView) {
        new AsyncTask<Object, Object, Bitmap>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }

            @Override
            protected Bitmap doInBackground(Object... objects) {
                Uri uri = (Uri) imageView.getTag();
                Bitmap bitmap = null;
                try {
                    bitmap = decodeUri(uri);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                return bitmap;
            }
        }.execute();
    }

    private Bitmap decodeUri(Uri selectedImage) throws FileNotFoundException {

        // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(activityBase.getContentResolver().openInputStream(selectedImage), null, o);

        // The new size we want to scale to

        // Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp / 2 < imageSize
                    || height_tmp / 2 < imageSize) {
                break;
            }
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(activityBase.getContentResolver().openInputStream(selectedImage), null, o2);
    }

    public List<Uri> getImageUris() {
        List<Uri> uriList = new ArrayList<Uri>();
        for (ImageView imageView : imageViews) {
            Uri uri = (Uri) imageView.getTag();
            if (uri != null) {
                uriList.add(uri);
            }
        }
        return uriList;
    }
}
