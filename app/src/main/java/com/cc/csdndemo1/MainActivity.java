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
import android.widget.Toast;

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
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class MainActivity extends AppCompatActivity {


    @BindView(R.id.takephotoTV)
    Button takephotoTV;
    @BindView(R.id.saveTxt)
    Button saveTxt;
    @BindView(R.id.imageIV)
    ImageView imageIV;
    @BindView(R.id.savePhoto)
    Button savePhoto;

    private final int CAMERA_REQUEST = 8888;
    Bitmap bitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }


    @OnClick({R.id.takephotoTV, R.id.saveTxt, R.id.savePhoto})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.takephotoTV:
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
                break;
            case R.id.saveTxt:
                getFile();
                break;
            case R.id.savePhoto:
                uploadPictureOne(bitmap);
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            bitmap = (Bitmap) data.getExtras().get("data");
            imageIV.setImageBitmap(bitmap);

//            //设置第二张图片
//            image1.setDrawingCacheEnabled(true);
//            if (image1.getDrawingCache() != null) {
//                image2.setImageBitmap(bitmap);
//            }
        }
    }


    /**
     * 上传图片
     */
    public void uploadPictureOne(Bitmap bitmap) {
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
         * 前面一个file1.getName()是为了传递给后台作为文件名使用的，
         * 在writeToLocal()方法里的第二行 String dirFile = destDirName + fileName;
         * 但是呢尽量还是使用后面的file.getName()，后台在选择的文件名的时候尽量选择value
         * 参考下面单文件上传方法(1)注释
         */
        Map<String, RequestBody> map = new HashMap<>();
        File file1 = compressImage(bitmap);
        String ext = file1.getName().substring(file1.getName().lastIndexOf(".") + 1);
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/" + ext), file1);
        map.put(file1.getName() + "\"; filename=\"" + file1.getName(), requestBody);


        /**
         * 单文件上传方法(1)
         * 一：如果后台选用的是eachKey传入方法writeToLocal中
         * 那么这里的"uploadFile"会作为图片名被使用，
         * 如果后台没有规定.jpg格式的那么这里就需要加上
         * 二：如果后台选用了fileName作为文件名使用，那么这里的uploadFile就可以随便写什么了
         */
        File file2 = compressImage(bitmap);
        //图片参数
        RequestBody imageBody = RequestBody.create(MediaType.parse("multipart/form-data"), file2);
        MultipartBody.Part body = MultipartBody.Part.createFormData("uploadFile", file2.getName(), imageBody);
        //手机号参数
        RequestBody phoneBody = RequestBody.create(MediaType.parse("multipart/form-data"), "18896751425");
        //密码参数
        RequestBody pswBody = RequestBody.create(MediaType.parse("multipart/form-data"), "jjj");


        /**
         * 单文件上传方法(2)
         * 参考单文件上传方法(1)的注释
         */
        File file3 = compressImage(bitmap);
        RequestBody imageBody1 = RequestBody.create(MediaType.parse("multipart/form-data"), file3);
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("phone", "18896751425")
                .addFormDataPart("password", "jjj")
                .addFormDataPart("uploadFile", file3.getName(), imageBody1);
        List<MultipartBody.Part> parts = builder.build().parts();


        /**
         * 多文件传方法(1)
         */
        File file4 = compressImage(bitmap);
        RequestBody requestFile1 = RequestBody.create(MediaType.parse("multipart/form-data"), file4);
        MultipartBody.Part body1 = MultipartBody.Part
                .createFormData("uploadFile", file4.getName(), requestFile1);
        RequestBody requestFile2 = RequestBody.create(MediaType.parse("multipart/form-data"), file4);
        MultipartBody.Part body2 = MultipartBody.Part
                .createFormData("uploadFile", file4.getName(), requestFile2);


        /**
         * 多文件上传方法(2)
         * 采用map集合来存放多个图片RequestBody参数。
         * 将多文件传方法(1)的代码放到map中
         */
        Map<String, RequestBody> map1 = new HashMap<>();
        map1.put("文件1" + "\"; filename=\"" + "hh.jpg", requestFile1);
        map1.put("文件2" + "\"; filename=\"" + "shabi.jpg", requestFile2);


        ApiManager.getInstance()
                .pointApiService()
//                .uploadPicture1(files)
//                .uploadPicture2(map)
//                .uploadPicture("photo", map)
//                .uploadPicture3("photo1111", map)
//                .uploadPictureOne(phoneBody, pswBody, body)
//                .uploadPictureOne(parts)
                .uploadPictures("pictures", requestFile1, requestFile2)
//                .uploadPictures("pictures", map1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResponseObj<Boolean>>() {
                    @Override
                    public void accept(ResponseObj<Boolean> booleanResponseObj) throws Exception {
                        Toast.makeText(MainActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
                        Log.e("MainActivity", "booleanResponseObj:" + booleanResponseObj.data.toString());
                    }
                }, new Consumer<Throwable>() {
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
