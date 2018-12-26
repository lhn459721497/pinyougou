 //控制层 
app.controller('goodsController' ,function($scope,$controller , $location  ,goodsService , uploadService ,
										   		typeTemplateService , itemCatService ){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
/*
	//查询实体 
	$scope.findOne=function(id){				
		goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	*/
	//保存 
	$scope.save=function(){

        $scope.entity.goodsDesc.introduction=editor.html();//添加文本编辑器内容
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=goodsService.update( $scope.entity ); //修改  
		}else{
			serviceObject=goodsService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					alert("保存成功");
					//置空对象
					$scope.entity={};
					/*清空富文本编辑器*/
					editor.html("");
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		goodsService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}

	//上传文件
	$scope.uploadFile=function () {

		uploadService.uploadFile().success(
			
			function (response) {

				if (response.success){
					//设置文件地址
					$scope.image_entity.url = response.message;
				} else {
					alert(response.message);
				}

            }).error(function () {
                alert("上传发生错误");
            }
			
		)

    }

    //定义页面实体结构
    $scope.entity={goods:{},goodsDesc:{itemImages:[]}}

    //添加图片列表
	$scope.add_image_entity=function () {

		$scope.entity.goodsDesc.itemImages.push($scope.image_entity);

    }

    //移除图片
	$scope.remove_image_entity=function (index) {

		$scope.entity.goodsDesc.itemImages.splice(index,1);

    }




    // ==================================================================
    //读取一级分类
	$scope.selectItemCat1List=function () {

		itemCatService.findByParentId(0).success(
			function (response) {

				$scope.itemCat1List=response;

            }
		)

    }

    //读取二级分类($watch方法监控)
	$scope.$watch("entity.goods.category1Id",function (newValue, oldValue) {

		if (newValue != undefined){
            //根据选择的值查询二级分类
            itemCatService.findByParentId(newValue).success(
                function (response) {

                    $scope.itemCat2List=response;

                }
            )
		}

    })

    //读取三级分类($watch方法监控)
    $scope.$watch("entity.goods.category2Id",function (newValue, oldValue) {

        if (newValue != undefined){
            //根据选择的值查询三级分类
            itemCatService.findByParentId(newValue).success(
                function (response) {

                    $scope.itemCat3List=response;

                }
            )
		}

    })

	//读取模板id
	$scope.$watch("entity.goods.category3Id",function (newValue, oldValue) {

		if (newValue != undefined){
            itemCatService.findOne(newValue).success(

                function (response) {
                    $scope.entity.goods.typeTemplateId=response.typeId;
                }

            )
		}

    })

	//模板id选择后更新品牌列表
	$scope.$watch("entity.goods.typeTemplateId",function (newValue, oldValue) {

		if (newValue != null){
            typeTemplateService.findOne(newValue).success(

                function (response) {
                    //获取类型模板
                    $scope.typeTemplate=response;
                    //获取品牌列表
                    $scope.typeTemplate.brandIds= JSON.parse($scope.typeTemplate.brandIds);


                    //-----------
                    //扩展属性录入
                    $scope.entity.goodsDesc.customAttributeItems=JSON.parse($scope.typeTemplate.customAttributeItems)
                }

            )
//-------------------------------------------------
            //查询规格列表
            typeTemplateService.findSpecList(newValue).success(
                function (response) {
                    $scope.specList=response;
                }
            )
		}

    })

	// 从集合中按照key查找对象
	$scope.searchObjectByKey=function (list, key, keyValue) {

		for (var i = 0 ; i < list.length ; i++){

			if (list[i][key]==keyValue){
				return list[i];
			}

		}

		return null;

    }

    //定义entity
	$scope.entity={goodsDesc:{itemImages:[],specificationItems:[]}};

	$scope.updateSpecAttribute=function ($event, name, value) {

		var object = $scope.searchObjectByKey($scope.entity.goodsDesc.specificationItems,
									"attributeName",name);

		if (object != null){
			if ($event.target.checked){
				object.attributeValue.push(value);
			} else {
				//取消勾选
				object.attributeValue.splice(object.attributeValue.indexOf(value),1);//移除选项

				//移除记录
				if (object.attributeValue.length==0){
					$scope.entity.goodsDesc.specificationItems.splice(
						$scope.entity.goodsDesc.specificationItems.indexOf(value),1
					)
				}
			}


		} else {

			$scope.entity.goodsDesc.specificationItems.push(
				{"attributeName":name,"attributeValue":[value]}
			)

		}


    }

    /*$scope.findSpecList=function (id) {

        typeTemplateService.findSpecList(id).success(
            function (response) {
                $scope.specList=response;
            }
        )

    }*/




    //=================================================
	//创建sku列表
	$scope.createItemList=function () {

		//初始化
		$scope.entity.itemList=[{spec:{},price:0,num:99999,status:'0',isDefault:'0' }];

		var items = $scope.entity.goodsDesc.specificationItems;

		for (var i = 0 ; i < items.length ; i++ ){
			$scope.entity.itemList=addColumn($scope.entity.itemList , items[i].attributeName , items[i].attributeValue);


		}

    }

    //定义方法添加列值
	addColumn=function (list, columnName, columnValues) {

		var newList=[];

		for (var i = 0 ; i < list.length ; i++){

			var oldRow = list[i];

			for ( var j = 0 ; j < columnValues.length ; j++){
				var newRow = JSON.parse( JSON.stringify(oldRow) );

				newRow.spec[columnName]=columnValues[j];

				newList.push(newRow);
			}

		}

		return newList;

    }


    //===============================================
	//使前台展示状态由数字变为字符串可以使用数组进行定义
	$scope.status=['未审核','已审核','审核未通过','关闭'];//商品状态


	//页面展示分类属性
	//定义一个集合用于存储接收到的商品分类列表
	$scope.itemCatList=[];
	$scope.findItemCatList=function () {

		itemCatService.findAll().success(

			function (response) {
				for (var i = 0 ; i < response.length ; i++){
					$scope.itemCatList[response[i].id]=response[i].name;
				}
            }

		)

    }

    //=====================================商品管理-》修改
	//查询实体
	$scope.findOne=function () {
		var id = $location.search()['id'];//获取参数值

		if (id==null){
			return ;
		}

		goodsService.findOne(id).success(
			function (response) {
				$scope.entity=response;
            }
		);

    }





});	
