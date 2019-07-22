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
<script src="https://static.nid.naver.com/js/naveridlogin_js_sdk_2.0.0.js"></script>
<script src="https://developers.kakao.com/sdk/js/kakao.min.js"></script>
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
						alert_call(true,"아이디: "+result);
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
				if(result.trim() != ''){
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

$(document).ready(function(){
			Kakao.init("c933f268ff0522075463024906de5641");
			function getKakaotalkUserProfile(){
				Kakao.API.request({
					url: '/v1/user/me',
					success: function(res) {
						$("#kakao-profile").append(res.properties.nickname);
						$("#kakao-profile").append($("<img/>",{"src":res.properties.profile_image,"alt":res.properties.nickname+"님의 프로필 사진"}));
					},
					fail: function(error) {
						console.log(error);
					}
				});
			}
			function createKakaotalkLogin(){
				$("#kakao-logged-group .kakao-logout-btn,#kakao-logged-group .kakao-login-btn").remove();
				var loginBtn = $("<a/>",{"class":"kakao-login-btn","text":"카카오 로그인"});
				loginBtn.click(function(){
					Kakao.Auth.login({
						persistAccessToken: true,
						persistRefreshToken: true,
						success: function(authObj) {
							getKakaotalkUserProfile();
							createKakaotalkLogout();
						},
						fail: function(err) {
							console.log(err);
						}
					});
				});
				$("#kakao-logged-group").prepend(loginBtn)
			}
// 			function createKakaotalkLogin(){
//			 Kakao.Auth.createLoginButton({
//				   container: '#kakao-login-btn',
//				   success: function(authObj) {
//				     Kakao.API.request({
//				       url: '/v1/user/me',
//				       success: function(res) {
//				             alert(JSON.stringify(res)); //<---- kakao.api.request 에서 불러온 결과값 json형태로 출력
//				             alert(JSON.stringify(authObj)); //<----Kakao.Auth.createLoginButton에서 불러온 결과값 json형태로 출력
//				             console.log(res.id);//<---- 콘솔 로그에 id 정보 출력(id는 res안에 있기 때문에  res.id 로 불러온다)
//				             console.log(res.kaccount_email);//<---- 콘솔 로그에 email 정보 출력 (어딨는지 알겠죠?)
//				             console.log(res.properties['nickname']);//<---- 콘솔 로그에 닉네임 출력(properties에 있는 nickname 접근 
				         // res.properties.nickname으로도 접근 가능 )
//				             console.log(authObj.access_token);//<---- 콘솔 로그에 토큰값 출력
//				           }
//				         })
//				       },
//				       fail: function(error) {
//				         alert(JSON.stringify(error));
//				       }
//				     });
//			}
				//]]>
			function createKakaotalkLogout(){
				$("#kakao-logged-group .kakao-logout-btn,#kakao-logged-group .kakao-login-btn").remove();
				var logoutBtn = $("<a/>",{"class":"kakao-logout-btn","text":"로그아웃"});
				logoutBtn.click(function(){
					Kakao.Auth.logout();
					createKakaotalkLogin();
					$("#kakao-profile").text("");
				});
				$("#kakao-logged-group").prepend(logoutBtn);
			}
			if(Kakao.Auth.getRefreshToken()!=undefined&&Kakao.Auth.getRefreshToken().replace(/ /gi,"")!=""){
				createKakaotalkLogout();
				getKakaotalkUserProfile();
			}else{
				createKakaotalkLogin();
			}
		});
		
</script>
<body>
<div class="title"><h1>로그인</h1></div>

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

<div class="right" >
	<div class="formBox">
	
	<form id="login_form">
		<p>ID</p>
		<input type="text" name="id" class="id" placeholder="아이디" maxlength="15">
		<p>Password</p>
		<input type="password" name="password" class="password" placeholder="●●●●●●" maxlength="15">
	</form>
		<button class="login_btn">로그인</button>
		<button type="button" id="help_btn" class="btn btn-info btn-sm" data-toggle="modal" data-target="#myModal">아이디 찾기</button>
		<!-- 네이버 로그인 창으로 이동 -->
		<div id="naver_id_login" style="text-align:center">
			<a href="${url}">
				<img width="223" src="https://developers.naver.com/doc/review_201802/CK_bEFnWMeEBjXpQ5o8N_20180202_7aot50.png"/>
			</a>
			<div id="kakao-logged-group">
				<a href="javascript:createKakaotalkLogin()">
					<img src="../img/button/kakaologin.png" width="300">
    			</a>
			</div>
			<div id="kakao-profile"></div>
<!-- 				<img width="223" src="../img/button/kakaologin.png"/> -->


		</div>
	</div>
</div>
<a id="kakao-login-btn"></a>
<a href="http://developers.kakao.com/logout"></a>
</body>
</html>