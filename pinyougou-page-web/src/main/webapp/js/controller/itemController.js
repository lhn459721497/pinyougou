//��Ʒ����ҳ�����Ʋ㣩
app.controller("itemController",function($scope){
	
	//��������
	$scope.addNum=function(x){
		
		$scope.num = $scope.num + x ;
		
		if($scope.num < 1){
			$scope.num = 1 ;
		}
		
	}
	
	//========================================================
	//��ʼ�����ѡ��
	$scope.specificationItems={};
	
	//�û�ѡ����
	$scope.selectSpecification=function( name , value ){
		
		$scope.specificationItems[name]=value;
		
		//��ȡsku
		searchSku();
		
	}
	
	//�жϹ��ѡ���Ƿ��û�ѡ��
	$scope.isSelected=function( name , value ){
		
		if($scope.specificationItems[name] == value){
			return true;
		}
		
		return false ;
		
	}
	
	
	//===========================================================
	//����Ĭ��sku
	$scope.loadSku=function(){
		
		$scope.sku=skuList[0];
		
		$scope.specificationItems=JSON.parse(JSON.stringify($scope.sku.spec));
		
	}
	
	
	//ƥ������������֤�Ƿ�Ϊѡ��
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
	
	//��ѯ��ǰѡ��SKU
	searchSku=function(){
		
		for(var i = 0 ; i < skuList.length ; i++){
			
			if(matchObject(skuList[i].spec , $scope.specificationItems)){
				
				$scope.sku = skuList[i];
				
				return;
				
			}
			
		}
		
		//û��ƥ��ѡ��
		$scope.sku={id:0,title:'',price:0}
		
	}
	
	
	//====================================================================
	//�����Ʒ�����ﳵ
	$scope.addToCart=function(){
		
		alert("skuid:"+$scope.sku.id);
		
	}
	
	
	
	
});