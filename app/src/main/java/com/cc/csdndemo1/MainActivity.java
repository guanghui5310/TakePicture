package com.cc.csdndemo1;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class MainActivity extends AppCompatActivity {


    @BindView(R.id.takephotoTV)
    Button takephotoTV;
    @BindView(R.id.savephotoTV)
    Button savephotoTV;
    @BindView(R.id.imageIV)
    ImageView imageIV;

    private final int CAMERA_REQUEST = 8888;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }


    @OnClick({R.id.takephotoTV, R.id.savephotoTV})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.takephotoTV:
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
                break;
            case R.id.savephotoTV:
                getFile();
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap bitmap = null;
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            bitmap = (Bitmap) data.getExtras().get("data");
            imageIV.setImageBitmap(bitmap);
        }


        uploadPicture(bitmap);



    }


    /**
     * 上传图片
     */
    public void uploadPicture(Bitmap bitmap) {
        /**
         * Map<String, InputStream>
         * 方法一
         */
        File file = compressImage(bitmap);
        Map<String, InputStream> files = new HashMap<>();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        InputStream inputStream = new ByteArrayInputStream(baos.toByteArray());
        files.put(file.getName() + "\"; filename=\"" + file.getName(), inputStream);

        /**
         * Map<String, RequestBody>
         * 方法二：map里的key必须是file.getName() + "\"; filename=\"" + file.getName()这样
         * 这样才能在后台的方法中接收到，具体是为什么有待研究
         */
        Map<String, RequestBody> map = new HashMap<>();
        File file1 = compressImage(bitmap);
        String ext = file1.getName().substring(file1.getName().lastIndexOf(".") + 1);
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/" + ext), file1);
        map.put(file1.getName() + "\"; filename=\"" + file1.getName(), requestBody);


        ApiManager.getInstance()
                .pointApiService()
//                .uploadPicture1(files)
//                .uploadPicture2(map)
//                .uploadPicture(map)
                .uploadPicture3("photo1111", map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new io.reactivex.functions.Consumer<ResponseObj<Boolean>>() {
                    @Override
                    public void accept(ResponseObj<Boolean> booleanResponseObj) throws Exception {
                        Log.e("MainActivity", "booleanResponseObj:" + booleanResponseObj.data.toString());
                    }
                }, new io.reactivex.functions.Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e("MainActivity", "throwable:" + throwable);
                    }
                });
    }


    /**
     * 选择手机指定目录文件上传到电脑
     */
    public void getFile() {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        File file = null;
        String name = "拍照姿势大全，50条顶级秘籍.txt";
        String path = Environment.getExternalStorageDirectory().getPath() + "/" + name;
        try {
            file = new File(path);
            fis = new FileInputStream(file);
            Log.e("MainActivity", String.valueOf(fis.available()));

            /**
             * 方法一：如果使用InputStream传的话，会丢失大部分数据
             * 测试中，一个2946kb的文件传到后台只有2kb了
             */
            Map<String, InputStream> files = new HashMap<>();
            files.put(name + "\"; filename=\"" + name, fis);

            /**
             * 方法二：使用RequestBody传的话，不会丢失数据，可以正常写到后台
             */
            Map<String, RequestBody> map = new HashMap<>();
            String ext = file.getName().substring(file.getName().lastIndexOf(".") + 1);
            RequestBody requestBody = RequestBody.create(MediaType.parse("image/" + ext), file);
            map.put(file.getName() + "\"; filename=\"" + file.getName(), requestBody);

            ApiManager.getInstance()
                    .pointApiService()
//                    .uploadFile(files)
                    .uploadFile1(map)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<ResponseObj<Boolean>>() {
                        @Override
                        public void accept(ResponseObj<Boolean> booleanResponseObj) throws Exception {
                            Log.e("MainActivity", booleanResponseObj.data.toString());
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            Log.e("MainActivity", throwable.getMessage());
                        }
                    });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



    /**
     * 压缩图片（质量压缩）
     *
     * @param bitmap
     */
    public File compressImage(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;

        //循环判断如果压缩后图片是否大于500kb,大于继续压缩;不进行压缩的话调过这段代码
//        while (baos.toByteArray().length / 1024 > 500) {
//            baos.reset();//重置baos即清空baos
//            options -= 10;//每次都减少10
//            bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
//            long length = baos.toByteArray().length;
//        }

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date(System.currentTimeMillis());
        String filename = format.format(date);
        File file = new File(Environment.getExternalStorageDirectory(), filename + ".png");
        try {
            FileOutputStream fos = new FileOutputStream(file);
            try {
                fos.write(baos.toByteArray());
                fos.flush();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return file;
    }

}
