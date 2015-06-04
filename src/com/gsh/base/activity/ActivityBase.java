package com.gsh.base.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.gsh.base.Constant;
import com.gsh.kuaixiu.R;
import com.gsh.base.ShareApp;
import com.litesuits.android.widget.ImageUtils;
import com.litesuits.common.assist.Toastor;
import com.litesuits.common.io.FileUtils;
import com.litesuits.common.utils.AppUtil;
import com.litesuits.common.utils.DialogUtil;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Tan Chunmao
 */
public class ActivityBase extends Activity {
    protected ActivityBase context;

    private final static SparseArray<ActivityBase> activities = new SparseArray<ActivityBase>();
    private final static AtomicInteger IDs = new AtomicInteger();
    private volatile int uniqueId;
    public ActivityWantedToGo go;


    public static ImageLoader imageLoader;
    public static DisplayImageOptions squarePictureOptions;//square picture
    public static DisplayImageOptions longPictureOptions;//16:9 picture
    public static DisplayImageOptions avatarOptions;//avatar

    private DialogUtil.ProgressDialog progressDialog;
    private Toastor toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        progressDialog = DialogUtil.getProgressDialog(context);
        go = new ActivityWantedToGo();
        toast = new Toastor(context);
        initImageLoader();
    }


    //region picture loading
    private void initImageLoader() {
        if (squarePictureOptions == null) {
            FileUtils.createDirectories();
            File cacheDir = new File(FileUtils.IMAGE);
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                    .memoryCacheExtraOptions(metrics.widthPixels, metrics.heightPixels)
                    .diskCacheExtraOptions(metrics.widthPixels, metrics.heightPixels, null)
                    .threadPoolSize(5)
                    .threadPriority(Thread.NORM_PRIORITY - 1)
                    .tasksProcessingOrder(QueueProcessingType.FIFO)
                    .denyCacheImageMultipleSizesInMemory()
                    .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                    .memoryCacheSize(2 * 1024 * 1024)
                    .memoryCacheSizePercentage(20)
                    .diskCache(new UnlimitedDiscCache(cacheDir))
                    .diskCacheSize(50 * 1024 * 1024)
                    .diskCacheFileCount(100)
                    .diskCacheFileNameGenerator(new HashCodeFileNameGenerator())
                    .imageDownloader(new BaseImageDownloader(context))
                    .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                    .writeDebugLogs()
                    .build();
            imageLoader = ImageLoader.getInstance();
            imageLoader.init(config);

            squarePictureOptions = createImageOption(R.drawable.pic_loading);
            longPictureOptions = createImageOption(R.drawable.pic_loading_long);
            avatarOptions = createImageOption(R.drawable.pic_loading_avatar);
        }
    }

    private DisplayImageOptions createImageOption(int loadDrawable) {
        return new DisplayImageOptions.Builder()
                .showImageOnLoading(loadDrawable) // resource or drawable
//                .showImageForEmptyUri(R.drawable.ic_empty) // resource or drawable
//                .showImageOnFail(R.drawable.ic_error) // resource or drawable
                .resetViewBeforeLoading(false)  // default
                .delayBeforeLoading(1000)
                .cacheInMemory(true) // default
                .cacheOnDisk(true) // default
                .considerExifParams(false) // default
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED) // default
                .bitmapConfig(Bitmap.Config.ARGB_8888) // default
                .displayer(new SimpleBitmapDisplayer()) // default
                .handler(new Handler()) // default
                .build();
    }

    public void loadImage(ImageView imageView, String path) {
        if (!TextUtils.isEmpty(path)) {
            if (!path.startsWith("http"))
                path = com.gsh.kuaixiu.Constant.Urls.IMAGE_PREFIX + path;
            ImageUtils.loadImage(imageLoader, squarePictureOptions, imageView, path);
        } else {
            imageView.setImageResource(R.drawable.pic_loading);
        }
    }

    public void loadLongImage(ImageView imageView, String path) {
        if (!TextUtils.isEmpty(path)) {
            if (!path.startsWith("http"))
                path = com.gsh.kuaixiu.Constant.Urls.IMAGE_PREFIX + path;
            ImageUtils.loadImage(imageLoader, longPictureOptions, imageView, path);
        } else {
            imageView.setImageResource(R.drawable.pic_loading_long);
        }
    }

    public void loadAvatar(ImageView imageView, String path) {
        if (!TextUtils.isEmpty(path)) {
            if (!path.startsWith("http")) {
                path = com.gsh.kuaixiu.Constant.Urls.IMAGE_PREFIX + path;
            }
            ImageUtils.loadImage(imageLoader, avatarOptions, imageView, path);
        } else {
            imageView.setImageResource(R.drawable.pic_loading_avatar);
        }
    }


    public void setGalleryTag(ImageView imageView, List<String> imagePaths, int position) {
        imageView.setOnClickListener(imageListener);
        imageView.setTag(R.id.tag_key_first, imagePaths);
        imageView.setTag(R.id.tag_key_second, position);
    }

    private View.OnClickListener imageListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            gallery(v);
        }
    };

    private void gallery(View v) {
        Intent intent = new Intent(this, GalleryActivity.class);
        List<String> paths = (List<String>) v.getTag(R.id.tag_key_first);
        Integer index = (Integer) v.getTag(R.id.tag_key_second);
        intent.putExtra(String.class.getName(), new ArrayList<String>(paths)).putExtra(Integer.class.getName(), index);
        startActivity(intent);
    }
    //endregion picture loading

    //    region picture choose
    private Dialog pictureMethodDialog;

    public interface ImageCallBack {
        void onImage(Uri uri);
    }

    private ImageCallBack imageCallBack;
    private Uri imageUri;

    public void showPictureMethodDialog(ImageCallBack imageCallBack) {
        if (pictureMethodDialog == null) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("选择图片")
                    .setItems(new String[]{"拍照", "相册"}, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            pictureMethodDialog.dismiss();
                            if (which == 0) {
                                capturePicture();
                            } else if (which == 1) {
                                chooseFromGallery();
                            }
                        }
                    });
            pictureMethodDialog = builder.create();
        }
        this.imageCallBack = imageCallBack;
        pictureMethodDialog.show();
    }

    private void chooseFromGallery() {
        final Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, Constant.Request.PICTURE_GALLERY);
    }

    private void capturePicture() {
        try {
            File file = FileUtils.createImageFile();
            if (file != null) {
                imageUri = Uri.fromFile(file);
                final Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(captureIntent, Constant.Request.PICTURE_CAMERA);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == Constant.Request.PICTURE_CAMERA || requestCode == Constant.Request.PICTURE_GALLERY) {
                if (requestCode == Constant.Request.PICTURE_GALLERY) {
                    imageUri = data.getData();
                }
                if (imageUri != null && imageCallBack != null) {
                    imageCallBack.onImage(imageUri);
                }
            } else if (requestCode == Constant.Request.PICTURE_CROP) {
                Bitmap bitmap = data.getExtras().getParcelable("data");
                if (cropImageCallBack != null) {
                    cropImageCallBack.onResult(bitmap);
                }
            }
        }
    }


    public interface CropImageCallBack {
        void onResult(Bitmap bitmap);
    }

    private CropImageCallBack cropImageCallBack;

    public void cropImage(Uri inputUri, File outputFile, int size, CropImageCallBack cropImageCallBack) {
        this.cropImageCallBack = cropImageCallBack;
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(inputUri, "image/*");
        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", "true");

        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", size);
        intent.putExtra("outputY", size);

        intent.putExtra("return-data", true);
        intent.putExtra("noFaceDetection", true);
        intent.putExtra("output", Uri.fromFile(outputFile));  // 专入目标文件
        intent.putExtra("outputFormat", "JPEG"); //输入文件格式
        startActivityForResult(intent, Constant.Request.PICTURE_CROP);
    }

    //    endregion picture choose

    //region share
    protected Dialog shareDialog;
    protected ShareContent shareContent;
    private View dialogContainer;

    protected final void showShareDialog() {
        if (shareDialog == null) {
            createShareDialog();
        }
        shareDialog.show();
    }

    private void createShareDialog() {
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        shareDialog = new Dialog(context, R.style.DialogSlideAnim);
        dialogContainer = inflater.inflate(R.layout.dialog_share, null);
        entry();
        final View cancelView = dialogContainer.findViewById(R.id.cancel);
        cancelView.setOnClickListener(shareClickListener);
        shareDialog.setContentView(dialogContainer);

        Window dialogWindow = shareDialog.getWindow();
        WindowManager m = dialogWindow.getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = dialogWindow.getAttributes();
        p.width = d.getWidth();
        dialogWindow.setAttributes(p);
        dialogWindow.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
    }

    private void entry() {
        setEntry(R.id.entry_weibo, ShareApp.weibo);
        setEntry(R.id.entry_tencent_weibo, ShareApp.tencent_weibo);
        setEntry(R.id.entry_qq, ShareApp.tencent_qq);
        setEntry(R.id.entry_qzone, ShareApp.qzone);
        setEntry(R.id.entry_wechat, ShareApp.wechat);
        setEntry(R.id.entry_moment, ShareApp.wechat_moments);
    }

    private void setEntry(int layoutId, ShareApp shareApp) {
        final View entry = dialogContainer.findViewById(layoutId);
        int color = getResources().getColor(shareApp.getBackgroundRid());
        int roundRadius = getResources().getDimensionPixelOffset(R.dimen.ui_size_icon_f_half);
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(color);
        gd.setCornerRadius(roundRadius);
        entry.findViewById(R.id.icon_container).setBackgroundDrawable(gd);
        entry.setOnClickListener(shareClickListener);
        ((ImageView) entry.findViewById(R.id.icon)).setImageResource(shareApp.getIconRid());
        ((TextView) entry.findViewById(R.id.label)).setText(shareApp.getLabelRid());
        ((TextView) entry.findViewById(R.id.label)).setTextColor(getResources().getColor(R.color.ui_font_c));
    }

    protected void share(ShareApp shareApp) {
        shareDialog.dismiss();
    }

    //分享到第三方平台的内容
    protected class ShareContent {
        String text;
        public String path;
        public String url;

        public ShareContent(String text, String path, String url) {
            this.text = text;
            this.path = path;
            this.url = url;
        }

        public ShareContent() {
        }
    }

    @Override
    public void onBackPressed() {
//        removeAllHttpTasks();
        imageLoader.stop();
        super.onBackPressed();
    }

    private void checkAppInstall(ShareApp shareApp) {
        boolean installed = AppUtil.appInstalledOrNot(shareApp.getPackageName(), this);
        if (!installed) {
            String appName = getString(shareApp.getAppName());
            String text = String.format("请安装%s客户端后再试！", appName);
            toast(text);
        } else {
            share(shareApp);
        }
    }

    private View.OnClickListener shareClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (R.id.cancel == v.getId()) {
                shareDialog.dismiss();
            } else if (R.id.entry_weibo == v.getId()) {
                checkAppInstall(ShareApp.weibo);
            } else if (R.id.entry_tencent_weibo == v.getId()) {
                share(ShareApp.tencent_weibo);
            } else if (R.id.entry_qq == v.getId()) {
                share(ShareApp.tencent_qq);
            } else if (R.id.entry_qzone == v.getId()) {
                share(ShareApp.qzone);
            } else if (R.id.entry_wechat == v.getId()) {
                checkAppInstall(ShareApp.wechat);
            } else if (R.id.entry_moment == v.getId()) {
                checkAppInstall(ShareApp.wechat_moments);
            }
        }
    };
//endregion share

    protected void setTitleBar(String s) {
        ((TextView) findViewById(R.id.title)).setText(s);
    }

    public class ActivityWantedToGo {

        private Intent intent;

        public ActivityWantedToGo name(Class<?> cls) {
            intent = new Intent(context, cls);
            return this;
        }

        public ActivityWantedToGo param(String name, String value) {
            if (intent != null)
                intent.putExtra(name, value);
            return this;
        }

        public ActivityWantedToGo param(String name, int value) {
            if (intent != null)
                intent.putExtra(name, value);
            return this;
        }

        public ActivityWantedToGo param(String name, long value) {
            if (intent != null)
                intent.putExtra(name, value);
            return this;
        }

        public ActivityWantedToGo param(String name, float value) {
            if (intent != null)
                intent.putExtra(name, value);
            return this;
        }

        public ActivityWantedToGo param(String name, double value) {
            if (intent != null)
                intent.putExtra(name, value);
            return this;
        }

        public ActivityWantedToGo param(String name, Serializable value) {
            if (intent != null)
                intent.putExtra(name, value);
            return this;
        }

        public void go()

        {
            if (intent != null) {
                startActivity(intent);
                intent = null;
            }
        }

        public void goForResult(int requestCode) {
            if (intent != null) {
                startActivityForResult(intent, requestCode);
                intent = null;
            }
        }

        public void goAndFinishCurrent() {
            if (intent != null) {
                startActivity(intent);
                intent = null;
                finish();
            }
        }
    }

    public synchronized int getUniqueId() {
        if (uniqueId == 0) {
            uniqueId = IDs.incrementAndGet();
        }
        return uniqueId;
    }

    public static void finishAll() {
        synchronized (activities) {
            if (activities.size() > 0)
                for (int i = activities.size() - 1; i >= 0; i--) {
                    int key = activities.keyAt(i);
                    if (activities.get(key) != null)
                        activities.get(key).finish();
                }
            activities.clear();
        }
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    @Override
    protected void onDestroy() {
        synchronized (activities) {
            activities.delete(getUniqueId());
        }
        super.onDestroy();
    }

    public ViewGroup getRootView() {
        ViewGroup view = (ViewGroup) findViewById(android.R.id.content);
        return view;
    }

    public void showProgressDialog() {
        if (!isFinishing()) {
            progressDialog.show();
        }
    }

    public void dismissProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }


    public void toast(String s) {
        toast.getSingletonToast(s).show();
    }

    public void toast(int s) {
        toast.getSingletonToast(s).show();
    }


    public interface CallBack {
        void call();
    }

    public void notice(final CallBack callBack, String title) {
        notice(callBack, title, null);
    }

    public void notice(final CallBack callBack, String title, String noticeContent) {
        notice(callBack, title, noticeContent, true);
    }

    public void notice(final CallBack callBack, String title, String noticeContent, boolean cancelable) {
        notice(callBack, null, title, noticeContent, cancelable);
    }

    public void confirm(final CallBack callBack,final String title) {
        final Dialog dialog = new Dialog(this, R.style.MyDialog);
        dialog.setCancelable(false);
        View layout = getLayoutInflater().inflate(R.layout.dialog_confirm, null);
        dialog.setContentView(layout);
        ((TextView) layout.findViewById(R.id.title)).setText(title);



        layout.findViewById(R.id.yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                callBack.call();
            }
        });


        Window dialogWindow = dialog.getWindow();
        WindowManager m = dialogWindow.getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = dialogWindow.getAttributes();
        p.width = (int) (d.getWidth() * 0.90);
        p.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialogWindow.setAttributes(p);
        dialog.show();
    }

    public void notice(final CallBack callBack, final CallBack noCallBack, String title, String noticeContent, boolean cancelable, String ok, String cancel) {
        final Dialog dialog = new Dialog(this, R.style.MyDialog);
        dialog.setCancelable(cancelable);
        View layout = getLayoutInflater().inflate(R.layout.dialog_notice, null);
        dialog.setContentView(layout);
        ((TextView) layout.findViewById(R.id.title)).setText(title);
        if (!TextUtils.isEmpty(ok)) {
            ((TextView) layout.findViewById(R.id.yes)).setText(ok);
        }
        if (!TextUtils.isEmpty(cancel)) {
            ((TextView) layout.findViewById(R.id.no)).setText(cancel);
        }
        TextView contentView = (TextView) layout.findViewById(R.id.content);
        if (TextUtils.isEmpty(noticeContent)) {
            contentView.setVisibility(View.GONE);
        } else {
            contentView.setVisibility(View.VISIBLE);
            contentView.setText(noticeContent);
        }
        layout.findViewById(R.id.yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                callBack.call();
            }
        });

        layout.findViewById(R.id.no).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (noCallBack != null) {
                    noCallBack.call();
                }
            }
        });
        Window dialogWindow = dialog.getWindow();
        WindowManager m = dialogWindow.getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = dialogWindow.getAttributes();
        p.width = (int) (d.getWidth() * 0.90);
        p.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialogWindow.setAttributes(p);
        dialog.show();
    }

    public void notice(final CallBack callBack, final CallBack noCallBack, String title, String noticeContent, boolean cancelable) {
        notice(callBack, noCallBack, title, noticeContent, cancelable, "是", "否");
    }

    public void hideKeyboard() {
        InputMethodManager manager = ((InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE));
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public void setText(int textViewId,String text) {
        ((TextView)findViewById(textViewId)).setText(text);
    }

    public String getInput(int inputViewId) {
        return ((EditText)findViewById(inputViewId)).getText().toString();
    }
}
