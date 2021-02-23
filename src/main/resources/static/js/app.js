$(function(){
	
		$("a.confirmDeletion").click(function(){
			if(!confirm("confirm deletion?")) return false;
		});
		
		if($("#description").length){
			ClassicEditor
				.create(document.querySelector("#description"))
				.catch(error => {
					console.log("error");
				})		
		}
		
		if($("#content").length){
			ClassicEditor
				.create(document.querySelector("#content"))
				.catch(error => {
					console.log("error");
				})		
		}
});

function readUrl(input, idNum){
	if(input.files && input.files[0]){
		let reader = new FileReader();
	
		reader.onload = function(e){
			$("#imagepreview" + idNum).attr("src", e.target.result).width(100).height(100);
		}
		
		reader.readAsDataURL(input.files[0]);
	}

}