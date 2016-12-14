/*******************************************
 *date: 2014/9/28
 *auther: bin.liu
 *svgVisio
 ********************************************/

d3svglogo = {};
d3svglogo.SvgLogo = function (parent) {
	this._element = this.createElement(parent);
	this._needsLayout = true;
	this._svgcontent ="";
	var that = this;
	rap.on("render", function () {
		if (that._needsLayout) {
			that.initialize();
			that._needsLayout = false;
		}
		
	});
	parent.addListener("Resize", function () {
		that._resize(parent.getClientArea());
	});
	this._resize(parent.getClientArea());
};

d3svglogo.SvgLogo.prototype = {
	
	createElement : function (parent) {
		var element = document.createElement("div");
		element.style.position = "absolute";
		element.style.left = "0";
		element.style.top = "0";
		element.style.overflow = "auto";
		//element.style.width = "100%";
		//element.style.height = "100%";
		parent.append(element);
		return element;
	},
	initialize : function () {
	},

	
	_resize : function (clientArea) {
		//alert("_resize");
		this._element.style.width=clientArea[2]+"px";
		this._element.style.height=clientArea[3]+"px";
		this._scheduleUpdate(true);
	},
	// update  ui
	_scheduleUpdate : function (needsLayout) {
		if (needsLayout) {
			this._needsLayout = true;
		}
	},
	/**
	**  svg 
	**/
	setSvgcontent : function (svgcontent) {
		
		this._element.innerHTML = svgcontent;
		this._scheduleUpdate(true);
	},
	getSvgcontent : function () {
		return this._svgcontent;
	},
	setWidth :function (width){
	  d3.select("svg").attr("width",width+"px")
	},
	setHeight :function (height){
	  d3.select("svg").attr("height",height+"px")
	},
	/**
	** event 
	**/
	selectItem : function (index, data) {
		
		var remoteObject = rap.getRemoteObject(this);
		remoteObject.notify("Selection", {
			"index" : index,
			"data" : data
		});
	},
	destroy : function () {
		var element = this._element;
		if (element.parentNode) {
			element.parentNode.removeChild(element);
		}
	}
	
};
// TYPE HANDLER

rap.registerTypeHandler("d3svglogo.SvgLogo", {
	
	factory : function (properties) {
		var parent = rap.getObject(properties.parent);
		return new d3svglogo.SvgLogo(parent);
	},
	
	destructor : "destroy",
	
	properties : ["svgcontent","width","height"],
	
	events : ["Selection"]
	
});
