//购物车控制层
app.controller("cartController",function ($scope , cartService) {

    //查询购物车列表
    $scope.findCartList=function () {

        cartService.findCartList().success(

            function (response) {

                $scope.cartList=response;

                $scope.totalValue = cartService.sum($scope.cartList);

            }

        );

    };


    //购物车数量增减
    $scope.addGoodsToCartList=function (itemId, num) {

        cartService.addGoodsToCartList(itemId,num).success(

            function (response) {
                if (response.success){
                    $scope.findCartList();//重新加载
                } else {
                    alert(response.message);
                }
            }

        );

    };
    
    
    //查询用户信息
    $scope.getUsername=function () {

        cartService.getUsername().success(

            function (response) {
                $scope.userName = response.username;

                if ($scope.userName=="anonymousUser"){
                    $scope.userName="";
                }
            }

        );

    }


    //====================================================================
    //获取地址列表
    $scope.findAddressList=function () {

        cartService.findAddressList().success(

            function (response) {
                $scope.addressList=response;



                //设置默认地址
                for (var i = 0 ; i < $scope.addressList.length ; i++){

                    if ($scope.addressList[i].isDefault=='1'){

                        $scope.address = $scope.addressList[i];

                        break;

                    }

                }


            }

        );

    }

    //选择地址
    $scope.selectAddress=function (address) {
        $scope.address=address;
    }

    //判断是否为当前选中地址
    $scope.isSeletedAddress=function (address) {

        if (address == $scope.address){
            return true;
        } else {
            return false;
        }

    }



    $scope.order={paymentType:'1'};

    //选择支付方式
    $scope.selectPayType=function (type) {

        $scope.order.paymentType = type;

    }
    
    //保存订单
    $scope.submitOrder=function () {

        $scope.order.receiverAreaName=$scope.address.address;

        $scope.order.receiverMobile=$scope.address.mobile;

        $scope.order.receiver=$scope.address.contact;

        cartService.submitOrder($scope.order).success(

            function (response) {

                if (response.success){
                    //跳转支付页面
                    if ($scope.order.paymentType=='1'){
                        //微信支付
                        location.href="pay.html";
                    } else {
                        //货到付款 跳转提示页面
                        location.href="paysuccess.html";
                    }
                } else {
                    //跳转提示页面    或提示订单提交失败
                    alert(response.message);
                }

            }

        );

    }



});