//商品详情页（控制层）
app.controller("itemController",function($scope , $http){

    //数量操作
    $scope.addNum=function(x){

        $scope.num = $scope.num + x ;

        if($scope.num < 1){
            $scope.num = 1 ;
        }

    }

    //========================================================
    //初始化规格选择
    $scope.specificationItems={};

    //用户选择规格
    $scope.selectSpecification=function( name , value ){

        $scope.specificationItems[name]=value;

        //读取sku
        searchSku();

    }

    //判断规格选项是否被用户选中
    $scope.isSelected=function( name , value ){

        if($scope.specificationItems[name] == value){
            return true;
        }

        return false ;

    }


    //===========================================================
    //加载默认sku
    $scope.loadSku=function(){

        $scope.sku=skuList[0];

        $scope.specificationItems=JSON.parse(JSON.stringify($scope.sku.spec));

    }


    //匹配两个对象，验证是否为选中
    matchObject=function(map1,map2){

        for (var k in map1){

            if(map1[k] != map2[k]){
                return false;
            }

        }

        for (var k in map2){

            if(map2[k] != map1[k]){
                return false;
            }

        }

        return true;

    }

    //查询当前选中SKU
    searchSku=function(){

        for(var i = 0 ; i < skuList.length ; i++){

            if(matchObject(skuList[i].spec , $scope.specificationItems)){

                $scope.sku = skuList[i];

                return;

            }

        }

        //没有匹配选项
        $scope.sku={id:0,title:'',price:0}

    }


    //====================================================================
    //添加商品到购物车
    $scope.addToCart=function(){

        $http.get("http://localhost:9107/cart/addGoodsToCartList.do?itemId="+$scope.sku.id+
						"&num="+$scope.num,{'withCredentials':true}).success(

				function (response) {
					if (response.success){
						location.href="http://localhost:9107/cart.html";
					} else {
						alert(response.message);
					}
                }

		);

    }




});