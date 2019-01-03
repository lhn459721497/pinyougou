package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(timeout = 5000)
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    private SolrTemplate solrTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 根据关键字搜索列表
     * @param searchMap
     * @return
     */
    @Override
    public Map<String, Object> search(Map searchMap) {

        //关键字空格处理
        String keywords = (String) searchMap.get("keywords");
        searchMap.put("keywords",keywords.replace(" ",""));



        Map<String,Object> map=new HashMap<>();
        //1 查询列表
        map.putAll(searchList(searchMap));

        //2 根据关键字查询商品分类
        List<String> categoryList = searchCategoryList(searchMap);
        map.put("categoryList",categoryList);



        //3 查询品牌和规格列表
        String categoryName = (String) searchMap.get("category");

        //如果有分类名称
        if (!"".equals(categoryName)){

            map.putAll(searchBrandAndSpecList(categoryName));

        } else {

            //如果没有分类名称，按照第一个查询
            if (categoryList.size() > 0 ){
                map.putAll(searchBrandAndSpecList(categoryList.get(0)));
            }

        }



        return map;

    }



    private Map<String, Object> searchList(Map searchMap) {

        Map<String,Object> map = new HashMap<>();

        /*Query query = new SimpleQuery();*/

        //配置高亮显示
        HighlightQuery query = new SimpleHighlightQuery();

        //设置高亮域
        HighlightOptions highlightOptions = new HighlightOptions().addField("item_title");
        highlightOptions.setSimplePrefix("<em style='color:red'>");//前缀
        highlightOptions.setSimplePostfix("</em>");//后缀
        //设置高亮选项
        query.setHighlightOptions(highlightOptions);



        //按照关键字查询
        //添加查询条件
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);


        //按照分类筛选                    mb false
        if (!"".equals(searchMap.get("category"))){

            Criteria filterCriteria = new Criteria("item_category").is(searchMap.get("category"));

            FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);

            query.addFilterQuery(filterQuery);

        }


        //按品牌筛选
        if (!"".equals(searchMap.get("brand"))){

            Criteria filterCriteria = new Criteria("item_brand").is(searchMap.get("brand"));

            FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);

            query.addFilterQuery(filterQuery);

        }


        //规格过滤
        if (searchMap.get("spec") != null){

            Map<String,String> specMap = (Map<String, String>) searchMap.get("spec");

            for (String key : specMap.keySet()) {

                Criteria filterCriteria = new Criteria("item_spec_"+key).is(specMap.get(key));

                FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);

                query.addFilterQuery(filterQuery);

            }

        }




        //按价格进行筛选
        if (!"".equals(searchMap.get("price"))){

            String[] price = ((String)searchMap.get("price")).split("-");

            if (!price[0].equals("0")){

                //价格区间起始不为0
                Criteria filterCriteria = new Criteria("item_price").greaterThanEqual(price[0]);

                FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);

                query.addFilterQuery(filterQuery);

            }

            if (!price[1].equals("*")){

                //价格区间结束不为*
                Criteria filterCriteria = new Criteria("item_price").lessThanEqual(price[1]);

                FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);

                query.addFilterQuery(filterQuery);

            }

        }



        //分页查询
        //当前页
        Integer pageNo = (Integer) searchMap.get("pageNo");

        if (pageNo == null){
            pageNo = 1 ;
        }

        //每页记录数
        Integer pageSize = (Integer) searchMap.get("pageSize");

        if (pageSize == null){
            pageSize = 20 ;
        }

        //从第几条记录查询
        query.setOffset((pageNo-1) * pageSize);
        query.setRows(pageSize);




        //各种排序
        //获取正序倒叙关键字 ASC DESC
        String sortValue = (String) searchMap.get("sort");

        //排序字段
        String sortField = (String) searchMap.get("sortField");

        if (sortValue != null && !sortValue.equals("")){

            if (sortValue.equals("ASC")){

                Sort sort = new Sort(Sort.Direction.ASC,"item_"+sortField);

                query.addSort(sort);

            }

            if (sortValue.equals("DESC")){

                Sort sort = new Sort(Sort.Direction.DESC,"item_"+sortField);

                query.addSort(sort);

            }

        }






        /*ScoredPage<TbItem> page = solrTemplate.queryForPage(query, TbItem.class);*/
        HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(query, TbItem.class);


        //循环高亮入口
        for (HighlightEntry<TbItem> highlightEntry : page.getHighlighted()) {
            //获取原实体类
            TbItem item = highlightEntry.getEntity();
            if(highlightEntry.getHighlights().size() > 0 &&
                    highlightEntry.getHighlights().get(0).getSnipplets().size() > 0){

                //设置高亮结果
                item.setTitle(highlightEntry.getHighlights().get(0).getSnipplets().get(0));
            }

        }



        map.put("rows",page.getContent());

        //返回总页数
        map.put("totalPages",page.getTotalPages());
        //返回总记录数
        map.put("total",page.getTotalElements());

        return map;
    }


    private List searchCategoryList(Map searchMap){

        List<String> list = new ArrayList<>();

        Query query = new SimpleQuery("*:*");

        //按照关键字查询
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);

        //设置分组选项
        GroupOptions groupOptions = new GroupOptions().addGroupByField("item_category");
        query.setGroupOptions(groupOptions);

        //得到分页组
        GroupPage<TbItem> page = solrTemplate.queryForGroupPage(query, TbItem.class);
        //根据列得到分组结果集
        GroupResult<TbItem> groupResult = page.getGroupResult("item_category");
        //得到分组结果入口页
        Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
        //得到分组入口集合
        List<GroupEntry<TbItem>> content = groupEntries.getContent();

        for (GroupEntry<TbItem> itemGroupEntry : content) {

            //将分组的名称封装到返回值中
            list.add(itemGroupEntry.getGroupValue());

        }

        return list;

    }


    //查询品牌和规格列表
    private Map searchBrandAndSpecList(String category){

        Map map = new HashMap();

        //获取模板id
        Long typeId = (Long) redisTemplate.boundHashOps("itemCat").get(category);

        if (typeId != null){

            //根据模板id查询品牌列表
            List brandList = (List) redisTemplate.boundHashOps("brandList").get(typeId);

            //返回值添加品牌列表
            map.put("brandList",brandList);

            //根据模板id查询规格列表
            List specList = (List) redisTemplate.boundHashOps("specList").get(typeId);

            map.put("specList",specList);

        }

        return map;

    }





    @Override
    public void importList(List list) {

        solrTemplate.saveBeans(list);

        solrTemplate.commit();

    }




    @Override
    public void deleteByGoodsIds(List goodsIdList) {

        System.out.println("删除商品ID"+goodsIdList);

        Query query=new SimpleQuery();

        Criteria criteria=new Criteria("item_goodsid").in(goodsIdList);
        query.addCriteria(criteria);

        solrTemplate.delete(query);
        solrTemplate.commit();

    }







}
