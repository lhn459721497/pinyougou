package com.pinyougou.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.Cart;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service(timeout = 6000)
public class CartServiceImpl implements CartService {

    @Autowired
    private TbItemMapper itemMapper;

    @Autowired
    private RedisTemplate redisTemplate;



    @Override
    public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num) {

        //根据SKU的id查询SKU信息
        TbItem item = itemMapper.selectByPrimaryKey(itemId);

        //健壮性判断 1 查询信息为空/商品不存在 2 状态不为1/商品状态无效
        if (item == null){
            throw new RuntimeException("商品不存在");
        }

        if (!item.getStatus().equals("1")){
            throw new RuntimeException("商品状态无效");
        }

        //获取商家id
        String sellerId = item.getSellerId();
        //根据id判断购物车列表是否存在商家购物车
        Cart cart = searchCartBySellerId(cartList,sellerId);

        //如果购物车中不存在该商家的购物车
        if (cart == null){

            //新建购物车对象
            cart = new Cart();

            cart.setSellerId(sellerId);
            cart.setSellerName(item.getSeller());

            TbOrderItem orderItem = createOrderItem(item,num);

            List orderItemList = new ArrayList();
            orderItemList.add(orderItem);

            cart.setOrderItemList(orderItemList);

            //将购物车对象添加到购物车
            cartList.add(cart);

        } else {
            //购物车中存在该商家购物车
            TbOrderItem orderItem = searchOrderItemByItemId(cart.getOrderItemList(),itemId);

            //健壮性判断 明细列表是否存在商品
            if (orderItem == null){
                //不存在
                orderItem = createOrderItem(item,num);

                cart.getOrderItemList().add(orderItem);

            } else {
                //存在
                orderItem.setNum(orderItem.getNum()+num);

                //计算总金额并存储
                orderItem.setTotalFee(new BigDecimal(orderItem.getNum()*orderItem.getPrice().doubleValue()));

                //健壮性判断
                //如果SKU数量操作后小于0 则移除商家整体购物车
                if (orderItem.getNum()<=0){
                    cart.getOrderItemList().remove(orderItem);

                }

                //如果商家数量为0，移除整体商家
                if (cart.getOrderItemList().size()<=0){
                    cartList.remove(cart);
                }

            }

        }

        return cartList;
    }


    //根据商家id查询购物车对象
    public Cart searchCartBySellerId(List<Cart> cartList , String sellerId){

        for (Cart cart : cartList) {

            //判断如果存在商家id则返回对应购物车对象
            if (cart.getSellerId().equals(sellerId)){
                return cart;
            }

        }

        //循环结束不存在对应购物车对象
        return null;

    }


    //根据SKU的id查询 购物车中是否存在对应SKU
    public TbOrderItem searchOrderItemByItemId(List<TbOrderItem> orderItemList,Long itemId){

        for (TbOrderItem orderItem : orderItemList) {

            //如果存在对应SKU则返回SKU信息         itemId为Long类型 比较需要使用long类型
            if (orderItem.getItemId().longValue() == itemId.longValue()){
                return orderItem;
            }

        }

        //循环结束 没有对应SKU信息 返回空
        return null;

    }


    //创建订单SKU
    public TbOrderItem createOrderItem(TbItem item,Integer num){

        //健壮性判断     判断num为不合法状态
        if (num <= 0){
            throw new RuntimeException("数量非法");
        }

        TbOrderItem orderItem = new TbOrderItem();
        orderItem.setGoodsId(item.getGoodsId());
        orderItem.setItemId(item.getId());
        orderItem.setNum(num);
        orderItem.setPicPath(item.getImage());
        orderItem.setPrice(item.getPrice());
        orderItem.setSellerId(item.getSellerId());
        orderItem.setTitle(item.getTitle());
        orderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue() * num));

        return orderItem;

    }


    //从redis中读取购物车数据
    @Override
    public List<Cart> findCartListFromRedis(String username) {

        System.out.println("从数据库中提取数据"+username);

        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(username);

        if (cartList == null){
            //redis中没有购物车信息
            cartList = new ArrayList<>();
        }

        return cartList;
    }


    //保存购物车数据到redis
    @Override
    public void saveCartListToRedis(String username, List<Cart> cartList) {

        System.out.println("从数据库中存入数据"+username);

        redisTemplate.boundHashOps("cartList").put(username,cartList);

    }


    //合并购物车
    @Override
    public List<Cart> mergeCartList(List<Cart> cartList_cookie, List<Cart> cartList_redis) {

        System.out.println("合并购物车");

        for (Cart cart : cartList_cookie) {

            for (TbOrderItem orderItem : cart.getOrderItemList()) {

                cartList_redis = addGoodsToCartList(cartList_cookie,orderItem.getItemId(),orderItem.getNum());

            }

        }

        return cartList_redis;
    }

}
