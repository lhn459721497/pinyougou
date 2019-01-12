//购物车服务层
app.service("cartService",function ($http) {

    //购物车列表
    this.findCartList=function () {
        return $http.post("cart/findCartList.do")
    };

    //购物车数量增减
    this.addGoodsToCartList=function (itemId , num) {
        return $http.post("cart/addGoodsToCartList.do?itemId="+itemId+"&num="+num)
    };
    
    //获取用户信息
    this.getUsername=function () {
        return $http.post("cart/getUsername.do");
    };



    //==================================================================================
    //定义求合计数量的方法
    this.sum=function (cartList) {

        //定义初始数量总计
        var totalValue = {totalNum:0 , totalMoney:0};

        for (var i = 0 ; i < cartList.length ; i++){

            var cart = cartList[i];

            for (var j = 0 ; j < cart.orderItemList.length ; j++){

                var orderItem = cart.orderItemList[j];

                //执行相加
                totalValue.totalNum += orderItem.num;
                totalValue.totalMoney += orderItem.totalFee;

            }

        }

        return totalValue;

    };



    //====================================================================
    //获取地址列表
    this.findAddressList=function () {
        return $http.post("address/findListByLoginUser.do");
    };

    //保存订单
    this.submitOrder=function (order) {
        return $http.post("order/add.do",order);
    };




});