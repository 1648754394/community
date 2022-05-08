$(function(){
	$("#sendBtn").click(send_letter);
});

function send_letter() {
	$("#sendModal").modal("hide");
	var toName = $("#recipient-name").val();
	var content = $("#message-text").val();
	$.post(
		//1 地址
		CONTEXT_PATH + "/letter/send",
		//2 传的值
		{"toName":toName, "content":content},
		//3 回调函数
		function (data) {
			//data是普通字符串，但满足JSON格式，需要转换
			data = $.parseJSON(data);
			if(data.code==0) {
				$("#hintBody").text("发送成功！");
			} else {
				$("#hintBody").text(data.msg);
			}
			$("#hintModal").modal("show");
			setTimeout(function(){
				$("#hintModal").modal("hide");
				location.reload();
			}, 2000);
		}
	);
}

function delete_msg(id) {

	if(confirm('是否确认删除？')){

		$.post(
			CONTEXT_PATH + "/letter/delete",
			{"id": id},
			function (data) {

			}
		)
	}
	location.reload();
}