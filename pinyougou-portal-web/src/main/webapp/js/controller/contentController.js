//广告控制层（网站前台）
app.controller("contentController",function ($scope , contentService) {

    //初始化广告集合
    $scope.contentList=[];

    $scope.findByCategoryId=function (categoryId) {

        contentService.findByCategoryId(categoryId).success(

          function (response) {

              $scope.contentList[categoryId]=response;

          }

        );

    }

});