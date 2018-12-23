package com.pinyougou.sellergoods.old;

import entity.PageResult;
import com.pinyougou.pojo.TbBrand;

import java.util.List;

//品牌接口
public interface BrandService {
    public List<TbBrand> findAll();

    //分页查询
    public PageResult findByPage(TbBrand brand, int pageNum, int pageSize);

    //添加品牌
    public void add(TbBrand brand);

    //修改时回显信息
    public TbBrand findOne(Long id);

    //修改品牌
    public void update(TbBrand brand);

    //批量删除品牌
    public void delete(Long[] ids);
}
