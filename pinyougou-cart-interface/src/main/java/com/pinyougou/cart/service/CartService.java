package com.pinyougou.cart.service;

import com.pinyougou.pojo.Cart;

import java.util.List;

public interface CartService {


    /**
     * 向购物车中添加商品
     * @param cartList
     * @param itemId
     * @param num
     * @return
     */
    public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num);

    /**
     * 保存购物车到redis
     * @param username
     * @param cartList
     */
    public void saveCartListToRedis(String username, List<Cart> cartList);

    /**
     * 从redis中获取数据
     * @param username
     * @return
     */
    public List<Cart> findCartListFromRedis(String username);

    /**
     * 合并购物车
     * @param cartList_cookie
     * @param cartList_redis
     * @return
     */
    public List<Cart> mergeCartList(List<Cart> cartList_cookie, List<Cart> cartList_redis);
}
