<%@page contentType="text/html; charset=utf-8"%>
<script>
$( document ).ready(function() {
	if(sessionid == null) {
		$('.update_btn').hide();
		$('.delete_btn').hide();
	}
});
</script>
<span class="back_span"></span>
<table class="read_table">
	<tr>
		<td class="read_title"></td>
	</tr>
	<tr>
		<td class="read_info">
	</tr>
	<tr>
	<td><a class="read_content"></a></td>
	</tr>
</table>
<div class="read_btn_div"><button class="update_btn">수정</button><button class="delete_btn">삭제</button></div>
<div class="reply_div">
<a style="color:red;margin-left:3%">♥</a><a>댓글</a><br/>
<textarea class="reply" rows="2" cols="20" wrap="hard" ></textarea><button class="reply_btn">등록</button>
</div>
<div class="comment_div">
	<table class="comment_table">
	</table>
</div>
<button class="back_btn" onclick="pageload(curpagenum)">돌아가기</button>
