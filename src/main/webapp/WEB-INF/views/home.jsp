<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page contentType="text/html; charset=utf-8"%>
<%@ page session="false"%>
<c:set var="path" value="${pageContext.request.contextPath}"></c:set>

<html>
<head>
<%@ include file="header.jsp"%>
<link href="${path}/css/room.css?ver=20" rel="stylesheet"> 
<link rel="stylesheet" href="http://code.jquery.com/ui/1.8.18/themes/base/jquery-ui.css" type="text/css" />  
<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js"></script>  
<script src="http://code.jquery.com/ui/1.8.18/jquery-ui.min.js"></script>  
<style>
.ui-datepicker{ font-size: 15px; width: 250px; background:white; }
.ui-widget-header{background:deepskyblue;}
.ui-datepicker-title{color:white}
.ui-datepicker select.ui-datepicker-month{ width:30%; font-size: 11px; }
.ui-datepicker select.ui-datepicker-year{ width:40%; font-size: 11px; }
.hasDatepicker{cursor: pointer;}
</style>
<script>
var room;
$(document).ready(function(){
	$('.btn_box').hide();
	$('.slide_btn').hide();
	$('.checkout_room').hide();
	$('.slidebox').hide();
	$('.slider').hide();
	
	var cur_room = "room1";
	var margin = 0;
	var sw = true;
	var listcnt = 0;
	var curimgnum = 0;
	var slidesw = true;
	var pay;
	
	// 잔여객실 함수
	function checkout_room(room){
		$('.remaining_table').html('<tr><th>번호</th>');
		$('.remaining_table tr').append('<th>객실명</th>');
		$('.remaining_table tr').append('<th>날짜</th>');
		$('.remaining_table tr').append('<th>남은객실</th></tr>');

		if (room == "" || room == null || room == undefined || ( room != null && typeof room == "object" && !Object.keys(room).length)) {
			alert_call(false,"죄송합니다.현재 객실이 남아있지 않습니다")
		 	return false;
		}
		
		if(room[0].roomType == "room1"){
			type = "Standard";
		}else if(room[0].roomType == "room2"){
			type = "Superior"; 
		}else{
			type="Deluxe";
		}
		for(var i = 0; i < room.length; i++){
			$('.remaining_table').append('<tr class="'+room[i].roomSeq+'">');
			$('.'+room[i].roomSeq).append('<td>'+(i+1)+'</td>');
			$('.'+room[i].roomSeq).append('<td>'+type+'</td>');
			$('.'+room[i].roomSeq).append('<td>'+room[i].roomDate+'</td>');
			$('.'+room[i].roomSeq).append('<td>'+room[i].roomStay+'</td>');
			$('.remaining_table').append('</tr>');
		}
		$('.remaining_div').fadeIn('slow');
	}
	
	//요금계산 함수
	function calculator(){
		var room = $('.room_select').text();
		var days = staydate(); // 머무르는 기간
		console.log("days : " + days);
		console.log("curRoom : " + room);

		if(days == 0) days = 1;
		var person = 0;
		person = person_count();
			if(room == 'Standard'){
				$('.stay_person').text("숙박인원 : " +person+"인");
				$('.stay_room').text("Standard Room : "+Math.ceil(person/2)+"개");
				$('.stay_date').text("숙박기간 : " +days+"일");
				$('.stay_pay').text("금액: "+Math.ceil(person/2)*10*days+"만원");
				$('.stay_pay').append("<br/><h4>결제하시겠습니까?</h4>");
		}else if(room == 'Superior'){
				if(person < 4) 
					$('.stay_person').text("숙박인원 : " +person+"인");
					$('.stay_room').text("Superior (최대 4인) : " +Math.ceil(person/4)+"개");
					$('.stay_date').text("숙박기간: " +days+"일");
					$('.stay_pay').text("금액: " +Math.ceil(person/4)*19*days+"만원");
					$('.stay_pay').append("<br/><h4>이대로 결제하시겠습니까?</h4>");
		}else{
				if(person < 8) 
					$('.stay_person').text("숙박인원 : " +person+"인");
					$('.stay_room').text("Deluxe (최대8인) : " +Math.ceil(person/8)+"개");
					$('.stay_date').text("숙박기간: " +days+"일");
					$('.stay_pay').text("금액: " +Math.ceil(person/8)*36*days+"만원");
					$('.stay_pay').append("<br/><h4>이대로 결제하시겠습니까?</h4>");
		}
	}
	
	//객실 인원 체크 함수
	function person_count() {
		var person = $('.person_select').text();
		person = person.replace(/[^0-9]/g,"");
		console.log(person);
		return person;
	}
	// 예약 버튼
	function roomsubmit(){
		if(sessionid == null){
			alert_call(false,"로그인 후 이용 가능합니다");
			$('.modal').fadeOut('slow');
		}
		else{
			var person = 0;
			person = person_count();
			var needrooms = 0;
			var room = $('.room_select').text();
			var days = staydate();
			var book_checkin = $('#checkin_date').val();
			if(room == 'Standard'){
				needrooms = Math.ceil(person/2);
				room = "room1";
			}else if(room == 'Superior'){
				needrooms = Math.ceil(person/4);
				room = "room2";
			}else{
				needrooms = Math.ceil(person/8);
				room = "room3";
			}
			$.ajax({
				url:'/roomcheck',
				type:'post',
				data:{'checkin':book_checkin,'stay':days,'roomType':room,'need_rooms':needrooms},
				success:function(result){
					if(result == "ok"){
						book_insert(room,days,book_checkin,person,needrooms);
					}else{
						alert_call(false,result+"일의 남은 방이 모자릅니다.");
					}
				}
			})
		}
	}
	
	// 머무르는 기간 함수
	function staydate(){
		var checkin_month = $('#checkin_date').val().substring(5,7);
		var checkin_day = $('#checkin_date').val().substring(8,10);
		var checkout_month = $('#checkout_date').val().substring(5,7);
		var checkout_day =$ ('#checkout_date').val().substring(8,10);
		var imsi = Number(checkout_day); //형변환 임시변수
		var leftmargin = 0; //슬라이드 마진 변수
		
		if(checkin_month != checkout_month) {
			if(checkin_month == '01' || checkin_month == '03'){
				return 31-checkin_day+imsi;
			}else if(checkin_month == '02'){
				if($('#checkin_date').val().substring(0,4) % 400 == 0){
					return 29-checkin_day+imsi;
				}else{
					return 28-checkin_day+imsi;
				}
			}else{
				return 30-checkin_day+imsi;
			}
		}else{
			return checkout_day-checkin_day;
		}
	}
	
	$('.remaining_div h3').click(function() {
		$('.remaining_div').fadeOut();
	})
	
	//룸박스 토글
	$('.person_select').click(function(){
		$('.room_dropbox').hide();
		$('.person_dropbox').toggle();		
	})
	$('.person_dropbox div').click(function(){
		$('.person_select').text($(this).text());
		$('.person_dropbox').toggle();
	})
		$('.room_select').click(function(){
		$('.person_dropbox').hide();	
		$('.room_dropbox').toggle();
	})
	$('.room_dropbox div').click(function(){
		$('.room_select').text($(this).text());
		$('.room_dropbox').toggle();
	})
	
	//슬라이드 왼쪽버튼 클릭 함수
	$('.slide_left').click(function(){
		if(curimgnum > 0){
		$('.active_img').removeAttr('class');
		curimgnum -= 1;
		$('#'+curimgnum).attr('class','active_img');
		margin += 50;
		$('.imgbox').stop().animate({'margin-left':margin+'%'},700);
		if(curimgnum == 0){
			margin -= 25;
		}
	
		}else{
			if(slidesw){
				curimgnum = 0;
				margin = 25;
				$('.imgbox').stop().animate({'margin-left':margin+'%'},700);
				margin = 0;
				slidesw = false;
			}else{
				alert_call(true,"마지막 이미지로 갑니다. ");
				curimgnum = listcnt-1;
				margin =- 50 * (listcnt-1)+25;
				$('.imgbox').stop().animate({'margin-left':margin+'%'},700);
				$('.active_img').removeAttr('class');
				$('#'+curimgnum).attr('class','active_img');
			}
		}
	})
	
	//슬라이드 오른쪽버튼 클릭 함수
	$('.slide_right').click(function(){
		slidesw = false;
		
		if(curimgnum < listcnt-1){
		
			if(curimgnum == 0 || curimgnum == listcnt -1){
				margin += 25;
			}
				
		$('.active_img').removeAttr('class');
		curimgnum += 1;
		$('#'+curimgnum).attr('class','active_img');
		margin -= 50;
		$('.imgbox').stop().animate({'margin-left':margin+'%'},700);	
		}else{
			alert_call(true,"처음으로 돌아갑니다.");
			curimgnum = 0;
			margin = 25;
			$('.imgbox').stop().animate({'margin-left':margin+'%'},700);
			$('.active_img').removeAttr('class');
			$('#'+curimgnum).attr('class','active_img');
			margin = 0;
		}	
	})
	
	//슬라이드 이미지 클릭
	$(document).on('click','.slider img',function(){
		$('.active_img').removeAttr('class');
		curimgnum = Number( $(this).prop('id') );
		$('#' + curimgnum).attr('class','active_img');
		if(curimgnum == 0){
			margin = 25;
			$('.imgbox').stop().animate({'margin-left':margin+'%'},700);
			margin =0 ;
		}else{
			margin =- 50 * curimgnum + 25;
			$('.imgbox').stop().animate({'margin-left':margin+'%'},700);
		}
	})
		//datepicker setting
	     $.datepicker.regional['ko'] = {
		 changeMonth: false, 
	     dayNames: ['월요일', '화요일', '수요일', '목요일', '금요일', '토요일', '일요일'],
	     dayNamesMin: ['월', '화', '수', '목', '금', '토', '일'], 
	     monthNamesShort: ['1','2','3','4','5','6','7','8','9','10','11','12'],
	     monthNames: ['1월','2월','3월','4월','5월','6월','7월','8월','9월','10월','11월','12월'],
	   	 dateFormat: "yy-mm-dd",
	   	 minDate:1,
	     maxDate:31,
     };
		$.datepicker.setDefaults($.datepicker.regional['ko']);
		var currentDate = new Date();
		currentDate.setDate(currentDate.getDate()+1);
	    $('#checkin_date').datepicker();
	    $("#checkout_date").datepicker( "option", "minDate", 31 );
	    $('#checkin_date').datepicker("option", "onClose", function ( selectedDate ) {
	    	$("#checkout_date").datepicker( "option", "minDate", selectedDate );
	    });
			$( "#checkin_date" ).datepicker( "setDate", currentDate);
		    $('#checkout_date').datepicker();
		    $('#checkout_date').datepicker("option", "minDate", $("#checkin_date").val());
		    $('#checkout_date').datepicker("option", "onClose", function ( selectedDate ) {
		    	$("#checkin_date").datepicker( "option", "maxDate", selectedDate );
	    	});
				$( "#checkout_date" ).datepicker( "setDate", 2);
		
	//객실 예약 함수
	function book_insert(room, days, book_checkin, person, needrooms){
		var book_checkout = $('#checkout_date').val();
		if(days == 0) days = 1;
		var pay;
		if(room == 'room1'){
			pay = Math.ceil(person / 2) * 10 * days
		}else if(room == 'room2'){
			pay = Math.ceil(person / 4) * 19 * days
		}else{
			pay = Math.ceil(person / 8) * 36 * days
		}
			$.ajax({
				url:'/bookinsert',
				type:'post',
				data:{'room':room,'pay':pay,'stay':days,'checkin':book_checkin,'checkout':book_checkout,'person':sessionid,'needrooms':needrooms},
				success:function(result){
					$('body').html(result);
			}
		})
	}
	
	//객실예약
	$(document).on('click','.room_submit',function(){
		$('.modal').fadeIn('slow');
		calculator();
	})
	// 결제
	$('.ok').click(function(){
		roomsubmit();
	})
	// 취소
	$('.no').click(function(){
		$('.modal').fadeOut('slow');
	})
	
	//남은객실 확인
	$('.remaining_btn').click(function(){
		$.ajax({
			url:'/remainingrooms',
			type:'post',
			data:{'room':cur_room},
			success:function(result){
				checkout_room(result);
			}
		})
	})
	
	$('.person4').click(function(){
		$('.person_select').toggle();
		$('.toggle_person').toggle();
	})
	
	// 사람수 직접입력 기능
	$('#person_input').keyup(function(){
		// 숫자만 입력받는 정규식
		$(this).val($(this).val().replace(/[^0-9]/g,""));
		
		if($('#person_input').val() > 100){
			$('#person_input').val(100);
		}
			$('.person_select').text($('#person_input').val());
		})
		//룸 선택 시 함수
		function roomselect(data){
			slidesw = true;
			$('.imgbox').text("");
			$('.slider').text("");
				$.ajax({
				url:'/roomselect',
				type:'post',
				data:{'room':data},
				dataType:'json',
				success:function(result){
					listcnt = result.length;
					for(var i = 0; i < result.length; i++){
						$('.imgbox').append('<img src="${path}/img/'+data+'/'+result[i]+'">');
						$('.slider').append('<img id="'+i+'" src="${path}/img/'+data+'/'+result[i]+'">');
					
						if(i == 0){
							$('#'+curimgnum).attr('class','active_img');
						}
					}
				}
			})
		}
	
		//기본 룸 세팅
		roomselect("room1");
		
		//룸 선택 버튼 클릭 함수
		$('.btn_box button').click(function(){
			$(".slider").toggle();
			
			$('.checkout_room').show();
			$('.slider').show();
			$(this).css({'background':'green'});
			margin = 0;
			$('.imgbox').stop().animate({'margin-left':margin+'%'},500);
			cur_room = $(this).prop('id');
			curimgnum = 0;
			roomselect(cur_room);
			$('.remaining_div').fadeOut('slow');
	})
		// 객실예약 클릭 시
		$('#roomChoice').click(function(){
			$('.btn_box').show();
			$('.slide_btn').show();
			$('.slidebox').show();
			$('.slider').show(); // 왜 제일 먼저 뜨는지?
			$('.checkout_room').show(); // 왜 제일 먼저 뜨는지?
	})
		// 객실예약 클릭 시;; hide() show() 함수만 분리해서 리팩토링 필요
		$('#room4').click(function(){
			$('.checkout_room').hide();
			$('.slider').hide();
	})
	
});
</script>
</head>
<body>
<div class="main_div">
	<div class="modal">
		<div class="calculator">
			<h1>가격표</h1>
				<p class="stay_person"></p>
				<p class="stay_room"></p>
				<p class="stay_date"> </p>
				<p class="stay_cal"> </p>
				<h4 class="stay_pay"> </h4>
				<button class="ok">결제</button>
				<button class="no">취소</button>
		</div>
	</div>
<div class="remaining_div">
	<h3>X</h3>  
	<table class="remaining_table">
		<tr class="remaining_head">
		</tr>
	</table>
</div>
<div class="roombox">
<h1>객실 예약 </h1>
  <p>체크인 </p>
  	<input type="text" name="checkin" id="checkin_date" readonly>
  	<img alt="" width="3.8%" src="https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR_AeLSFS_DfDvLyo3W6lVmtL70U9q3myGgcJyHTMVqWZeqGQk7">
  <p>체크아웃 </p>
  	<input type="text" name="checkout" id="checkout_date" readonly>
  	<img alt="" width="3.8%" src="https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR_AeLSFS_DfDvLyo3W6lVmtL70U9q3myGgcJyHTMVqWZeqGQk7">
  <p id="roomChoice">객실 둘러보기</p>
  
  <div class="room_select">Standard</div>
  <div class="room_dropbox">
	  <div class="room1">Standard</div>
	  <div class="room2">Superior</div>
	  <div class="room3">Deluxe</div>
  </div>
  
    <p>인원</p>
    <div class="toggle_person">
  		<input type="number" max="100" id="person_input" placeholder="최대 100명까지 가능합니다">인
    </div>
	<div class="person_select">성인 1인</div>
	<div class="person_dropbox">
		  <div class="person1">성인 1인</div>
		  <div class="person2">성인 2인</div>
		  <div class="person3">성인 3인</div>
		  <div class="person4">직접입력</div>
	</div>
  	 	 <button class="room_submit"> 확인 </button>
</div>
<div class="slideToggle">
	<div class="btn_box">
		<button class="room1_btn" id="room1">Standard</button>
		<button class="room2_btn" id="room2">Superior</button>
		<button class="room3_btn" id="room3">Deluxe</button>
		<button class="room3_btn" id="room4">Convenience</button>
	</div>
	<div class="slide_btn">
		<button class="slide_left">＜</button>
		<button class="slide_right">＞</button>
	</div>
	<div class="slidebox">
		<div class="blur_box_left"></div>
		<div class="blur_box_right"></div>
		<div class="imgbox"></div>
	</div>
</div>

<div class="slider">
	<div class="slider_imgbox"></div>
</div>
<div class="checkout_room">
	<button class="remaining_btn">잔여객실</button>
</div>

</div>



</body>
</html>