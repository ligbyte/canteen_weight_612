package com.stkj.plate.weight.stat.service;

import com.stkj.plate.weight.base.model.BaseNetResponse;
import com.stkj.plate.weight.pay.model.FoodConsumeDetailResponse;
import com.stkj.plate.weight.pay.model.FoodConsumeRefundResponse;
import com.stkj.plate.weight.stat.model.CanteenSummary;

import java.util.Map;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

/**
 * 餐厅统计相关
 */
public interface StatService {

    //获取餐厅统计
    @GET("home/v2/index")
    Observable<BaseNetResponse<CanteenSummary>> getCanteenSummary(@QueryMap Map<String, String> requestParams);

    //设备退款
    @GET("home/v2/index")
    Observable<BaseNetResponse<FoodConsumeDetailResponse>> foodRefund(@QueryMap Map<String, String> requestParams);

    //菜品退款记录
    @GET("home/v2/index")
    Observable<BaseNetResponse<FoodConsumeRefundResponse>> foodRefundList(@QueryMap Map<String, String> requestParams);

}
