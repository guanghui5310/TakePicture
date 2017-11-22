package com.cc.csdndemo1;


import java.io.InputStream;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;

/**
 * Created by HASEE on 2017/6/1 16:43
 */

public interface PointApi {

    @Multipart
    @POST("takePicture/uploadPicture")
    Observable<ResponseObj<Boolean>> uploadPicture1(@PartMap Map<String, InputStream> map);

    @Multipart
    @POST("takePicture/uploadPicture")
    Observable<ResponseObj<Boolean>> uploadPicture2(@PartMap Map<String, RequestBody> map);


    @Multipart
    @POST("takePicture/picture")
    Observable<ResponseObj<Boolean>> uploadPicture(@PartMap Map<String, RequestBody> map);


    @Multipart
    @POST("takePicture/uploadPicture")
    Observable<ResponseObj<Boolean>> uploadPicture3(@Part("photo") String photo,
                                                @PartMap Map<String, RequestBody> map);


    @Multipart
    @POST("takePicture/uploadFile")
    Observable<ResponseObj<Boolean>> uploadFile(@PartMap Map<String, InputStream> map);

    @Multipart
    @POST("takePicture/uploadFile")
    Observable<ResponseObj<Boolean>> uploadFile1(@PartMap Map<String, RequestBody> map);


}
