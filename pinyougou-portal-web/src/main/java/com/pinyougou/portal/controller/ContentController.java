package com.pinyougou.portal.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.content.service.ContentService;
import com.pinyougou.pojo.TbContent;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/content")
public class ContentController {

    @Reference
    private ContentService contentService;

    /**
     * 根据分类id查询广告列表
     * @param categoryId
     * @return
     */
    @RequestMapping("findByCategoryId")
    public List<TbContent> findByCategoryId(Long categoryId){

        List<TbContent> list = contentService.findByCategoryId(categoryId);

        if (list!=null){
            return list;
        }

        return null;

    }

}
