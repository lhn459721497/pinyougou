package com.pinyougou.manager.old;

import com.alibaba.dubbo.config.annotation.Reference;
import entity.PageResult;
import entity.Result;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.old.BrandService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("brand")
public class BrandController {

    @Reference
    private BrandService brandService ;

    //查询所有品牌
    @RequestMapping("findAll.do")
    public List<TbBrand> findAll(){
        return brandService.findAll();
    }

    /*//分页查询
    @RequestMapping("findByPage")
    public PageResult findByPage(int page , int rows){

        return brandService.findByPage( page,rows);

    }*/

    //增加品牌
    @RequestMapping("add")
    public Result add(@RequestBody TbBrand brand){

        try {
            brandService.add(brand);

            return new Result(true,"添加成功");
        } catch (Exception e) {
            e.printStackTrace();

            return new Result(false,"添加失败");
        }

    }

    //修改品牌
    @RequestMapping("update")
    public Result update(@RequestBody TbBrand brand){

        try {
            brandService.update(brand);

            return new Result(true,"修改成功");
        } catch (Exception e) {
            e.printStackTrace();

            return new Result(false,"修改失败");
        }

    }

    //修改回显品牌信息
    @RequestMapping("findOne")
    public TbBrand findOne(Long id){

        return brandService.findOne(id);

    }

    /*批量删除品牌信息*/
    @RequestMapping("delete")
    public Result delete(Long[] ids){

        try {
            brandService.delete(ids);

            return new Result(true,"删除成功");
        } catch (Exception e) {
            e.printStackTrace();

            return new Result(false,"删除失败");
        }

    }

    //条件查询
    @RequestMapping("search")
    public PageResult search(@RequestBody TbBrand brand , int page , int size){

        return brandService.findByPage(brand,page,size);

    }

}
