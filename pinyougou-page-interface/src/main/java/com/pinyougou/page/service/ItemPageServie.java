package com.pinyougou.page.service;

/**
 * 商品详情页接口
 */
public interface ItemPageServie {

    //生成商品详情页
    public boolean genItemHtml(Long goodsId);

    //删除详情页
    public boolean deleteItemHtml(Long[] goodsIds);
}
