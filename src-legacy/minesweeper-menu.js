function MenuOption(text,clickAction){
	this.t = text;
	this.c = clickAction;

	this.h = html = document.createElement("button");
	html.className = "menu-option";
	html.onclick = function(){
		menuVisible(false);
		clickAction.call(this);
	}.bind(this);
	html.innerHTML = text;
}
var MenuOptionPrototype = MenuOption.prototype;

MenuOptionPrototype.getButton = function() {
	//var document.createElement("button");

};




var menuContainer = document.createElement("div");
menuContainer.id = "menu";
main_container.appendChild(menuContainer);

var menuBtn = document.createElement("button");
menuBtn.id = "menu-button";
menuBtn.innerHTML = "Menu";
menuBtn.onclick = menuVisible.bind(this,undefined);
menuContainer.appendChild(menuBtn);

var optionBox = document.createElement("div");
optionBox.id = "option-box";
menuContainer.appendChild(optionBox);


var mo = new MenuOption("Test",function(){
	alert("Test");
})
optionBox.appendChild(mo.h);


function menuVisible(a){
	if (a == undefined) a = menuBtn.className.indexOf("open")<0;
	if (a){
		menuBtn.className += " open";
		optionBox.className += " visible";
	} else {
		menuBtn.className = menuBtn.className.replace(/\sopen/,"");
		optionBox.className = optionBox.className.replace(/\svisible/,"");
	}
}
