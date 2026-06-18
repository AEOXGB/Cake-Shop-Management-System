<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>首页</title>

<!-- 屏幕自适应 -->
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
   
    <!-- 导入css用link -->   
    <link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
    <link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/css/zui.min.css">
    <!-- 导入Javascript用script标签 -->
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-3.0.0.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/zui.min.js"></script>
 
 <style type="text/css">
 .panel-center{
             margin-left: 8%;  
         }
 </style>   
 
 <script type="text/javascript">
 function queryGood(){
	 var name = $("#name").val();
	 console.log(name);
	 location.href="/cakeTest01/api/GoodsServlet?action=qurey&name="+name;
 }
  function seeDetail(id){
	  location.href="/cakeTest01/api/GoodsServlet?action=qurey&id="+id;
  }
  
  function buy(id){
	  location.href="/cakeTest01/api/addTocartServlet?action=cart&id="+id;
  }
 </script>
    
</head>
<body>
    <!--导航条-->
    <nav class="navbar navbar-inverse" role="navigation">
      <div class="container-fluid" style="font-size:1.6em; padding-top:10px; padding-left:70px; ">
        <!-- 导航头部 -->
        <div class="navbar-header">
          <!-- 移动设备上的导航切换按钮 -->
          <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse-example">
            <span class="sr-only">切换导航</span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
          </button>
          <!-- 品牌名称或logo -->
          <a class="navbar-brand" href="<%=request.getContextPath()%>/api/GoodsServlet">蛋糕系统</a>
        </div> 
        <!-- 导航项目 -->
        <div class="collapse navbar-collapse navbar-collapse-example" >
          <!-- 一般导航项目 -->
          <ul class="nav navbar-nav">
            <li class="active"><a href="<%=request.getContextPath()%>/api/GoodsServlet">首页</a></li>
            
            <!-- 导航中的下拉菜单 要映入jquery.js以及zui.js才能生效-->
            <li class="dropdown">
              <a href="#" class="dropdown-toggle" data-toggle="dropdown" >商品分类 <b class="caret"></b></a>
              <ul class="dropdown-menu" role="menu">
              
              
               <c:forEach var="type" items="${typelist }">
                   <!-- 也是查询的一部分  只是条件不一样 -->
                  <li><a href="/cakeTest01/api/GoodsServlet?action=qureyType&typeid=${type.getId() }">${type.getName() }</a></li>
               </c:forEach>
               
               
              </ul>
            </li>
            
            <li><a href="${pageContext.request.contextPath}/goods/topSell">热销</a></li>
            <li><a href="${pageContext.request.contextPath}/goods/newArrivals">新品</a></li>
            
            <li><a href="<%=request.getContextPath()%>/register.jsp">注册</a></li>
            <li ><a href="<%=request.getContextPath()%>/loginout">退出登录</a></li>   <!-- 此按钮变成退出登录 -->
            <li ><a href="<%=request.getContextPath()%>/api/myOrders?action=list">我的订单</a></li>
          </ul>
          
          <!--右侧购物导航条里  图标里  标签-->
          <ul class="nav navbar-nav navbar-right" style="padding-right:60px;">
              <li><a href="<%=request.getContextPath()%>/api/myCartServlet?action=list"><i class="icon icon-shopping-cart icon-2x"></i><span class="label label-badge label-success" style="margin-bottom:6px;">${count}</span></a></li>
          </ul>
          
          <!--右侧搜索框表单 导航条里-->
          <form class="navbar-form navbar-right form-search" role="search">
               <!-- 搜索框 -->
           <div class="input-group">
                <div class="input-control search-box search-box-circle has-icon-left has-icon-right search-example" id="searchboxExample">
                   <input id="name" name = "name" type="search" class="form-control search-input" placeholder="搜索">
                       <label for="inputSearchExample3" class="input-control-icon-left search-icon" style="margin-top:-3px;"><i class="icon icon-search"></i></label>
                    <!-- 按钮在div里 -->
                    <span class="input-group-btn">
                         <button class="btn btn-primary" type="button" onclick="queryGood()">搜索</button>
                    </span>
                </div>
            </div>  
          </form>
                
        </div><!-- END .navbar-collapse -->
      </div>
    </nav>
    
    
    <!--中间面板-->
    <div class="panel">

      <div class="panel-body panel-center">
          <!--栅格系统 占据6列-->
        <div class="row">
            <div class="col-md-12"> 
            
              <c:forEach var="goods" items="${goodslist }">
                    <table width="1900px" height="600px">           
                     <tr>
                       <td style="text-align:center; width:500px; height:300px;">      
                         <h1 class="header-dividing" style="font-size:60px;">${goods.getName() }</h1>  
                         <h3 style="color:purple; margin-left:-180px;  ">今日精选推荐</h3>
                       </td>   
                    
                        <td rowspan='2' style="padding-left:50px;"><img alt="图片" src="<%=request.getContextPath()%>${goods.getCover()}"></td>
                     </tr>
                 <tr>
                 <td>
                     <button class="btn btn-lg btn-success" type="button" style="margin-left:110px;" onclick="buy(${goods.getId()})">加入购物车</button> 
                     <button class="btn btn-lg btn-success" type="button" style="margin-left:30px;" onclick="seeDetail(${goods.getId()})" >查看详情</button>
                 </td>
                 </tr>
              </table>
              <br>
              </c:forEach>
               
            </div>
        </div>
      </div>
      
     <!--底部  导入index-bottom.html以及main.css-->
     <jsp:include page="/Cakepages/bottom.jsp"></jsp:include>
     </div>

</body>
</html>