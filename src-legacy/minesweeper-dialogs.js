var dialogTop = document.createElement("div");
dialogTop.id = "dt";
dialogTop.style.display = "none";
main_container.appendChild(dialogTop);

var dialogMid = document.createElement("div");
dialogMid.id = "dm";
dialogTop.appendChild(dialogMid);

var dialogs = document.querySelectorAll(".db");
for (var i = 0; i < dialogs.length; i++) {
	dialogs[i].style.display = "none";
	dialogMid.appendChild(dialogs[i]);
}

function showDialog(id){
	var dialog = document.getElementById(id);
	dialogTop.style.display=dialog.style.display="";
}

function closeDialogs(){
	var dialogs = document.querySelectorAll(".db");
	for (var i = 0; i < dialogs.length; i++) {
		dialogs[i].style.display = "none";
	}
	dialogTop.style.display = "none";
	blur(false);
}

//To window:
addActions(".btn-closedialogs","onclick",closeDialogs);