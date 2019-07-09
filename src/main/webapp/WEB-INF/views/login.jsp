<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@page contentType="text/html; charset=utf-8"%>
<%@ page session="false"%>
<c:set var="path" value="${pageContext.request.contextPath}"></c:set>
<html>
<head>
<%@ include file="header.jsp"%>
<link href="${path}/css/login.css?ver=4" rel="stylesheet">
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.0/js/bootstrap.min.js"></script>
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.0/css/bootstrap.min.css">
</head>
<script>
$(document).ready(function(){
	
	// 아이디찾기 요청
	$('#help_btn').click(function(){
		$('#email').val('');
	})

	//아이디 찾기 요청
	$('.help_submit').click(function(){
		var email=$('.email').val();
		var exptext = /^[A-Za-z0-9_\.\-]+@[A-Za-z0-9\-]+\.[A-Za-z0-9\-]+/;
		if(exptext.test(email) == false){
		alert_call(false,"이메일형식이 올바르지 않습니다.");
		}else{
			$.ajax({
				url:'/findId',
				type:'post',
				data:{'email':email},
				success:function(result){
					if(!result){
						alert_call(false,"입력하신 이메일과 일치하는 정보가 없습니다");
					}else{
						$('.id').val(result);
						alert_call(true,result);
					}
				}
			})
		}
	})
	// Login
	$('.login_btn').click(function(){
		var data=$('#login_form').serialize();
		$.ajax({
			url:'/memberlogin',
			type:'post',
			data:data,
			success:function(result){
				if(result.trim()!=''){
					alert_call(true,result+"님 반갑습니다");
					setTimeout(function(){
						window.location.href="/";
					},1000)
				
				}else{
					alert_call(false,"일치하는 정보가 없습니다.");
				}
			},error:function(){
				alert_call(false,"올바른 접근이 아닙니다.");
				setTimeout(function(){
					location.reload;
				},1000)
			}
		})
	})
})
</script>
<body>
<div class="title"><h1>로그인</h1></div>

<div class="right" >
	<div class="formBox">
	
	<form id="login_form">
		<p>ID</p>
		<input type="text" name="id" class="id" placeholder="Your ID" maxlength="15">
		<p>Password</p>
		<input type="password" name="password" class="password" placeholder="●●●●●●" maxlength="15">
	</form>
		<button class="login_btn">로그인</button>
		<button type="button" id="help_btn" class="btn btn-info btn-sm" data-toggle="modal" data-target="#myModal">아이디 찾기</button>
	</div>
	
	
	
</div>

<div class="login_div">
	<div class="help_div">
	 <!-- Modal -->
		  <div class="modal fade" id="myModal" role="dialog">
		    <div class="modal-dialog">
		      <!-- Modal content-->
		      <div class="modal-content">
		        <div class="modal-header">
		          <button type="button" class="close" data-dismiss="modal">&times;</button>
		          <h4 class="modal-title">아이디 찾기</h4>
		        </div>
		        <div class="modal-body">
			        <form id="help_form">
						<p>E-mail</p>
						<input type="email" id="email" name="email" class="email" placeholder="E-Mail" maxlength="30">
					</form>
					<button class="help_submit">확인</button>
		        </div>
		        <div class="modal-footer">
		          <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
		        </div>
		      </div>
		      
		    </div>
		  </div>
	</div>
</div>
</body>
</html>