package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.pojo.TbPayLog;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import util.IdWorker;

import java.util.HashMap;
import java.util.Map;

//支付控制层
@RestController
@RequestMapping("/pay")
public class PayController {

    @Reference
    private WeixinPayService weixinPayService;

    @Reference
    private OrderService orderService;

    //生成二维码
    @RequestMapping("/createNative")
    public Map createNative(){



        //获取当前用户
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        //到redis查询支付日志
        TbPayLog payLog = orderService.searchPayLogFromRedis(userId);

        //判断日志存在
        if (payLog != null){
            Map nativeMap = weixinPayService.createNative(payLog.getOutTradeNo(), payLog.getTotalFee() + "");

            return nativeMap;
        } else {
            return new HashMap();
        }



/*
在orderServiceImpl中的add方法中已实现

        //生成订单号与总金额
        IdWorker idWorker = new IdWorker();
        String out_trade_no = idWorker.nextId()+"";

        return weixinPayService.createNative(out_trade_no,"1");
*/

    }

    //查询支付状态
    @RequestMapping("/queryPayStatus")
    public Result queryPayStatus(String out_trade_no){

        Result result = null;

        //设置查询超时
        int x = 0 ;

        while (true){

            //调用接口查询
            Map<String,String> map = weixinPayService.queryPayStatus(out_trade_no);

            if (map == null){
                //支付出错
                result = new Result(false,"支付出错");

                break;
            }

            if (map.get("trade_state").equals("SUCCESS")){
                //支付成功
                result = new Result(true,"支付成功");



                //修改订单状态
                orderService.updateOrderStatus(out_trade_no,map.get("transaction_id"));




                break;
            }

            //设置间隔时间
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //如果变量超过此值则退出循环
            x++;

            if (x >= 100){
                result = new Result(false,"二维码过期");

                break;
            }

        }

        return result;

    }

}
