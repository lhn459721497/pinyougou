app.service("loginService",function ($http) {

    //读取列表中数据绑定到表单中
    this.showName=function () {
        return $http.post("login/name.do")
    }

});