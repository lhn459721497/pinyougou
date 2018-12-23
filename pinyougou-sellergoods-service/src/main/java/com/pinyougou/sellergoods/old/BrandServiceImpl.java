package com.pinyougou.sellergoods.old;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import entity.PageResult;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbBrandExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class BrandServiceImpl implements BrandService {

    @Autowired
    private TbBrandMapper tbBrandMapper;

    @Override
    public List<TbBrand> findAll() {
        return tbBrandMapper.selectByExample(null);
    }

    //分页查询
    @Override
    public PageResult findByPage(TbBrand brand, int pageNum, int pageSize) {
        //使用分页插件
        PageHelper.startPage(pageNum,pageSize);

        TbBrandExample brandExample = new TbBrandExample();
        TbBrandExample.Criteria criteria = brandExample.createCriteria();

        if (brand!= null){
            if (brand.getName()!=null && brand.getName().length()>0){
                criteria.andNameLike("%"+brand.getName()+"%");
            }
            if (brand.getFirstChar()!=null && brand.getFirstChar().length()>0){
                criteria.andFirstCharLike("%"+brand.getFirstChar()+"%");
            }
        }

        Page<TbBrand> tbBrands = (Page<TbBrand>) tbBrandMapper.selectByExample(brandExample);

        return new PageResult(tbBrands.getTotal(),tbBrands.getResult());
    }

    //添加品牌
    @Override
    public void add(TbBrand brand) {

        tbBrandMapper.insert(brand);

    }

    //修改时回显信息
    @Override
    public TbBrand findOne(Long id) {
        TbBrand tbBrand = tbBrandMapper.selectByPrimaryKey(id);

        return tbBrand;
    }

    //修改品牌
    @Override
    public void update(TbBrand brand) {

        tbBrandMapper.updateByPrimaryKey(brand);

    }

    //批量删除品牌
    @Override
    public void delete(Long[] ids) {

        for (Long id : ids) {
            tbBrandMapper.deleteByPrimaryKey(id);
        }

    }
}
