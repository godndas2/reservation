<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@page contentType="text/html; charset=utf-8"%>
<%@ page session="false"%>
<c:set var="path" value="${pageContext.request.contextPath}"></c:set>
<html>
<head>
<%@ include file="header.jsp"%>
<link href="${path}/css/login.css?ver=4" rel="stylesheet">
</head>
<script>
$(document).ready(function(){
	// 아이디찾기 폼 요청
	$('.help_btn').click(function(){
		$('.left').css({
			'transform':'translateX(100%)',
			'transition':'1s'
		})
		setTimeout(function(){
			$('.help_div').fadeIn('slow');
		},500)
				
	})
	//로그인 폼 요청
	$('.right_call').click(function(){
		$('.left').css({
			'transform':'translateX(0)',
			'transition':'1s'
		})
		setTimeout(function(){
			$('.help_div').fadeOut('slow');
		},500)
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
						$('.right_call').trigger('click');
					}
				}
			})
		}
	})
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
<div class="login_div">
<div class="help_div">
<form id="help_form">
		<p>E-mail</p>
		<input type="email" name="email" class="email" placeholder="E-Mail" maxlength="30">
</form>
<h4>이메일을 입력해주세요. 아이디를 찾아드립니다.</h4>

<button class="help_submit">확인</button>
<a href="#" class="right_call">로그인화면으로 돌아갑니다</a>
</div>
	<div class="left">

	</div>
	<div class="right">
	<div class="formBox">
	
	<form id="login_form">
		<p>ID</p>
		<input type="text" name="id" class="id" placeholder="Your ID" maxlength="15">
		<p>Password</p>
		<input type="password" name="password" class="password" placeholder="●●●●●●" maxlength="15">
	</form>
	
		<button class="login_btn">로그인</button>
		<a href="#" class="help_btn">ID 또는 비밀번호를 잊으셨나요?</a>
	</div>

	</div>
</div>
</body>
</html>