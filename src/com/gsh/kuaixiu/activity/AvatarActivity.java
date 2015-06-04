package com.gsh.kuaixiu.activity;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.gsh.kuaixiu.R;
import com.litesuits.android.widget.RoundCornerImageView;
import com.litesuits.common.io.FileUtils;

import java.io.File;

/**
 * @author Tan Chunmao
 */
public class AvatarActivity extends KuaixiuActivityBase {

    private RoundCornerImageView imageView;
    private File croppedFile;
    private static final int avatar_size = 300;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_activity_avatar);
        imageView = (RoundCornerImageView) findViewById(R.id.avatar);
        imageView.setOnClickListener(onClickListener);

    }

    private View.OnClickListener onClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(R.id.avatar==v.getId()) {
                showPictureMethodDialog(imageCallBack);
            }
        }
    };

    private CropImageCallBack cropImageCallBack = new CropImageCallBack() {
        @Override
        public void onResult(Bitmap bitmap) {
            imageView.setImageBitmap(bitmap);
        }
    };

    private ImageCallBack imageCallBack = new ImageCallBack() {
        @Override
        public void onImage(Uri uri) {
            croppedFile = new File(FileUtils.LOCAL_AVATAR);
            cropImage(uri, croppedFile, avatar_size, cropImageCallBack);
          /*  Resources r = getResources();
            float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, r.getDisplayMetrics());
            try {
                Bitmap bitmap = ImageUtils.getThumbnail(AvatarActivity.this, uri, (int) px);
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }*/
        }
    };
}
