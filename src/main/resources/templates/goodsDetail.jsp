<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>商品详情</title>

<!-- 屏幕自适应 -->
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
   
    <!-- 导入css用link -->   
    <link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
    <link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/css/zui.min.css">
    <!-- 导入Javascript用script标签 -->
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-3.0.0.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/zui.min.js"></script>
  
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
            <li><a href="<%=request.getContextPath()%>/api/GoodsServlet">首页</a></li>
            
            <!-- 导航中的下拉菜单 要映入jquery.js以及zui.js才能生效-->
            <li class="dropdown">
              <a href="your/nice/url" class="dropdown-toggle" data-toggle="dropdown">商品分类 <b class="caret"></b></a>
              <ul class="dropdown-menu" role="menu">
                <li><a href="your/nice/url">分类1</a></li>
                <li><a href="your/nice/url">分类2</a></li>
              </ul>
            </li>
            
            <li><a href="<%=request.getContextPath()%>/goods/topSell">热销</a></li>
            <li><a href="<%=request.getContextPath()%>/goods/newArrivals">新品</a></li>
            
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
                   <input id="name" name = "name" type="search" class="form-control search-input" placeholder="搜索" readonly>
                       <label for="inputSearchExample3" class="input-control-icon-left search-icon"><i class="icon icon-search"></i></label>
                    <!-- 按钮在div里 -->
                    <span class="input-group-btn">
                         <button class="btn btn-primary" type="button">搜索</button>
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
              
                    <table width="1900px">           
                     <tr>
                        <td style="padding-left:150px; width:600px"> 
                            <a href="<%=request.getContextPath()%>${goodslist.get(0).getCover()}" data-toggle="lightbox" data-group="image-group-1"><img style="width:400px;height:400px;" src="<%=request.getContextPath()%>${goodslist.get(0).getCover()}" class="img-rounded" alt=""></a>
                        </td>
                        <td style="padding-left:30px;">  
                            <span>
                               <span style="font-size:40px; font-weight:bold; color:RGB(46, 139, 87)">商品名称：${goodslist.get(0).getName()}</span><br>
                               <span style="font-size:30px; font-weight:bold; color:RGB(148, 0, 211)" >所属类型： ${goodslist.get(0).getTypename()}  </span><br><br>
                               <span style="font-size:2em; font-weight:bold; color:RGB(0, 71, 133); word-wrap: break-word; "> ${goodslist.get(0).getIntro()}   </span><br>
                               <span style="font-size:30px; font-weight:bold; color:RGB(255, 0, 0)">价格：&nbsp;&nbsp;¥&nbsp;${goodslist.get(0).getPrice()}  </span><br>
                               <span style="font-size:30px; font-weight:bold; color:RGB(255, 0, 0)">库存： ${goodslist.get(0).getStock()}  </span><br>
                                   <br>
                                   <a class="btn btn-primary" href="${pageContext.request.contextPath}/api/addTocartServlet?action=cart&id=${goodslist.get(0).getId()}">加入购物车</a>
                                   <!-- 如果添加购物车成功，则返回结果 -->
                                   <c:if test="${!empty msg }">
                                       <script type="text/javascript">
                                          new $.zui.Messager("${msg}", {
				                              placement:'center'
				                          }).show();
                                       </script>
                                   </c:if>
                            </span>
                        </td>
                        
                        
                     </tr>
                 <tr>
                     <td colspan='2'>
                     <a href="<%=request.getContextPath()%>${goodslist.get(0).getImage1()}" data-toggle="lightbox" data-group="image-group-1"><img style="width:200px;height:200px; margin-top:10px; margin-left:80px;" src="<%=request.getContextPath()%>${goodslist.get(0).getImage1()}" class="img-rounded" alt=""></a>
                     <a href="<%=request.getContextPath()%>${goodslist.get(0).getCover()}" data-toggle="lightbox" data-group="image-group-1"><img style="width:200px;height:200px; margin-top:10px;" src="<%=request.getContextPath()%>${goodslist.get(0).getCover()}" class="img-rounded" alt=""></a>
                     <a href="<%=request.getContextPath()%>${goodslist.get(0).getImage2()}" data-toggle="lightbox" data-group="image-group-1"><img style="width:200px;height:200px; margin-top:10px; " src="<%=request.getContextPath()%>${goodslist.get(0).getImage2()}" class="img-rounded" alt=""></a>
                 </tr>
              </table>
              <br>
           
               
            </div>
        </div>
      </div>
      
     <!--导入index-bottom.html以及main.css-->
     <jsp:include page="/Cakepages/bottom.jsp"></jsp:include>
     </div>

</body>
</html>