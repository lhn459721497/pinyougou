//搜索控制层
app.controller("searchController",function ($scope , $location , searchService) {

    //搜索
    $scope.search=function () {

        //传递数据时准换为int类型
        $scope.searchMap.pageNo = parseInt($scope.searchMap.pageNo);

        searchService.search($scope.searchMap).success(

            function (response) {
                $scope.resultMap=response;

                //分页
                buildPageLabel();
            }

        )

    }

    //==============================================================
    //定义搜索对象
    $scope.searchMap={'keywords':'','category':'','brand':'','spec':{} , 'price':'' ,
                        'pageNo':1 , 'pageSize':40 , 'sort':'' , 'sortField':''};

    $scope.addSearchItem=function (key, value) {

        //如果点击的是分类或者品牌
        if (key == "category" || key == "brand" || key == "price" ){
            $scope.searchMap[key] = value;
        } else {
            $scope.searchMap.spec[key] = value;
        }

        //执行搜索
        $scope.search();

    }

    //移除符合搜索条件
    $scope.removeSearchItem=function (key) {

        //如果是分类或品牌
        if(key == "category" ||  key == "brand" || key == "price"){
            $scope.searchMap[key] = "";
        } else {
            //规格
            delete $scope.searchMap.spec[key];
        }

        //执行搜索
        $scope.search();

    }


    //===================================================================
    //构建分页标签
    buildPageLabel=function () {

        //新增分页栏属性
        $scope.pageLabel=[];

        //最后页码
        var maxPageNo = $scope.resultMap.totalPages;
        //开始页码
        var firstPage = 1254245

        //截至页码
        var lastPage = maxPageNo ;

        //前面有点
        $scope.firstDot = true;

        //后边有点
        $scope.lastDot = true ;

        //如果总页数大于5
        if ($scope.resultMap.totalPages > 5){

            //如果当前页小于3
            if ($scope.searchMap.pageNo <= 3){
                lastPage = 5 ;
                //前面没点
                $scope.firstDot = false;
            } else if ($scope.searchMap.pageNo >= $scope.resultMap.totalPages-2){
                firstPage = maxPageNo - 4 ;
                //后面没点
                $scope.lastDot = false;
            } else {
                firstPage = $scope.searchMap.pageNo - 2 ;
                lastPage = $scope.searchMap.pageNo + 2 ;
            }

        } else {
            $scope.firstDot=false;//前面无点
            $scope.lastDot=false;//后边无点
        }

        //循环产生页码标签
        for (var i = firstPage ; i <= lastPage ; i++){

            $scope.pageLabel.push(i);

        }
        
    }

    //根据页码查询
    $scope.queryByPage=function (pageNo) {

        //页码验证
        if (pageNo < 1 || pageNo > $scope.resultMap.totalPages){
            return ;
        }

        $scope.searchMap.pageNo = pageNo ;

        $scope.search();

    }

    //判断当前为第一页
    $scope.isTopPage=function () {

        if ($scope.searchMap.pageNo == 1){
            return true ;
        } else {
            return false ;
        }

    }

    //判断当前页为最后一页
    $scope.isEndPage=function () {

        if ($scope.searchMap.pageNo == $scope.resultMap.totalPages){
            return true;
        } else {
            return false;
        }

    }

    //=================================================================
    //设置排序规则
    $scope.sortSearch=function (sortField, sort) {

        $scope.searchMap.sortField = sortField;
        $scope.searchMap.sort = sort;

        $scope.search();

    }


    //=================================================================
    //判断关键字是不是品牌
    $scope.keywordsIsBrand=function(){
        for(var i=0;i<$scope.resultMap.brandList.length;i++){
            if($scope.searchMap.keywords.indexOf($scope.resultMap.brandList[i].text)>=0){
                //如果包含
                return true;
            }
        }
        return false;
    }


    //==================================================================
    //加载查询字符串
    $scope.loadkeywords=function(){
        $scope.searchMap.keywords=  $location.search()['keywords'];
        $scope.search();
    }




})