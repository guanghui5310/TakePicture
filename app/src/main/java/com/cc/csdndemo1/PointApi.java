package com.cc.csdndemo1;


import java.io.InputStream;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;

/**
 * Created by HASEE on 2017/6/1 16:43
 */

public interface PointApi {

//    @Multipart
//    @POST("takePicture/uploadPicture")
//    Observable<ResponseObj<Boolean>> uploadPicture1(@PartMap Map<String, InputStream> map);
//
//    @Multipart
//    @POST("takePicture/uploadPicture")
//    Observable<ResponseObj<Boolean>> uploadPicture2(@PartMap Map<String, RequestBody> map);

    /**
     * 将图片保存到本地电脑
     *
     * @param map
     * @return
     */
    @Multipart
    @POST("takePicture/picture")
    Observable<ResponseObj<Boolean>> uploadPicture(@Part("photo") String photo,
                                                   @PartMap Map<String, RequestBody> map);


    /**
     * 单文件上传方法(1)
     * 注册时接口有三个参数：手机号、密码、头像
     *
     * @param phone
     * @param password
     * @param image
     * @return
     */
    @Multipart
    @POST("takePicture/picture")
    Observable<ResponseObj<Boolean>> uploadPictureOne(@Part("phone") RequestBody phone,
                                                         @Part("password") RequestBody password,
                                                         @Part MultipartBody.Part image);

    /**
     * 单文件上传方法(2)
     * 注册时接口有三个参数：手机号、密码、头像
     * 可以发现方法中的参数变成了List《MultipartBody.Part》的集合。这样所有的参数我们只需要放到这个集合里边即可
     *
     * @return
     */
    @Multipart
    @POST("takePicture/picture")
    Observable<ResponseObj<Boolean>> uploadPictureOne(@Part List<MultipartBody.Part> partList);


    /**
     * 多文件上传方法(1)
     */
    @Multipart
    @POST("takePicture/picture")
    Observable<ResponseObj<Boolean>> uploadPictures(@Part("filename") String description,
                                                    @Part("pic\"; filename=\"image1.png") RequestBody imgs1,
                                                    @Part("pic\"; filename=\"image2.png") RequestBody imgs2);

    /**
     * 多文件上传方法(2)
     * 采用map集合来存放多个图片RequestBody参数。
     */
    @Multipart
    @POST("takePicture/picture")
    Observable<ResponseObj<Boolean>> uploadPictures(@Part("filename") String description,
                                                    @PartMap() Map<String, RequestBody> maps);

    /**
     * 上传FTP服务器，参考内蒙运维zhywserver项目中装机工单详情里的回单接口
     *
     * @param photo
     * @param map
     * @return
     */
    @Multipart
    @POST("takePicture/uploadPicture")
    Observable<ResponseObj<Boolean>> uploadPicture3(@Part("photo") String photo,
                                                    @PartMap Map<String, RequestBody> map);


    /**
     * 上传文件.txt
     *
     * @param map
     * @return
     */
    @Multipart
    @POST("takePicture/uploadFile")
    Observable<ResponseObj<Boolean>> uploadFile(@PartMap Map<String, InputStream> map);

    /**
     * 上传文件.txt
     *
     * @param map
     * @return
     */
    @Multipart
    @POST("takePicture/uploadFile")
    Observable<ResponseObj<Boolean>> uploadFile1(@PartMap Map<String, RequestBody> map);


}
