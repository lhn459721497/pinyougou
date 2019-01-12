package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.pojo.Cart;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import util.CookieUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Reference
    private CartService cartService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;

    //购物车列表
    @RequestMapping("/findCartList")
    public List<Cart> findCartList(){

        //获取当前未登录状态临时用户名
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("登陆用户："+username);


        String cartListString = CookieUtil.getCookieValue(request, "cartList", "utf-8");

        if (cartListString == null || "".equals(cartListString)){
            cartListString = "[]";
        }

        List<Cart> cartList_cookie = JSON.parseArray(cartListString, Cart.class);

        //未登陆状态
        if ("anonymousUser".equals(username)){

            return cartList_cookie;

        } else {

            //已登陆       从redis中获取数据
            List<Cart> cartList_redis = cartService.findCartListFromRedis(username);


            //如果本地存在购物车
            if (cartList_cookie.size() > 0){

                //合并购物车
                cartList_redis = cartService.mergeCartList(cartList_cookie,cartList_redis);

                //清除本地cookie数据
                util.CookieUtil.deleteCookie(request,response,"cartList");

                //将合并后的数据存入redis
                cartService.saveCartListToRedis(username,cartList_redis);

            }



            return cartList_redis;

        }


    }

    //添加商品到购物车
    @RequestMapping("/addGoodsToCartList")
    public Result addGoodsToCartList(Long itemId , Integer num){

        response.setHeader("Access-Control-Allow-Origin","http://localhost:9105");
        response.setHeader("Access-Control-Allow-Credentials","true");

        //获取当前未登录状态临时用户名
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("登陆用户："+username);

        try {

            //查找cookie中的商家列表
            List<Cart> cartList = findCartList();

            //整合cookie中的商品数量
            cartList = cartService.addGoodsToCartList(cartList,itemId,num);


            //如果是未登陆状态，保存到cookie
            if ("anonymousUser".equals(username)){

                //向cookie中添加信息
                util.CookieUtil.setCookie(request,response,"cartList",
                        JSON.toJSONString(cartList),3600*24,"utf-8");

                System.out.println("向cookie存入数据");

            } else {

                //已登陆状态         保存到redis
                cartService.saveCartListToRedis(username,cartList);

            }

            return new Result(true,"添加成功");

        } catch (Exception e){

            e.printStackTrace();
            return new Result(false,"添加失败");

        }

    }


    //返回用户信息
    @RequestMapping("getUsername")
    public Map getUsername(){

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        Map map = new HashMap();
        map.put("username",username);

        return map;

    }

}
