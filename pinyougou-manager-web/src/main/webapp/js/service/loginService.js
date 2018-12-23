app.service("loginService",function ($http) {

    //获取登陆用户名
    this.loginName=function () {
        return $http.post("../login/name.do");
    }

})