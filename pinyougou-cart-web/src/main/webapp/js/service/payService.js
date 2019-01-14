//支付服务层
app.service("payService",function ($http) {

    //本地支付
    this.createNative=function () {
        return $http.post("pay/createNative.do");
    }

    //查询支付状态
    this.queryPayStatus=function (out_trade_no) {
        return $http.post("pay/queryPayStatus.do?out_trade_no="+out_trade_no);
    }

});