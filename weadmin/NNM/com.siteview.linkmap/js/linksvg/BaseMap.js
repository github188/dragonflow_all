/*******************************************
 *date: 2014/5/22
 *auther: bin.liu
 *base interface
 ********************************************/

linkmap = {};
linkmap.BaseMap = function (parent, renderer) {
	this._renderer = renderer;
	this._element = this.createElement(parent);
	//this.createCanvas(parent);//save image try
	this.createMainMenu(parent);
	this._svg = d3.select(this._element).append("svg").attr("class", "SvgLink")
		.attr("xmlns", "http://www.w3.org/2000/svg");
		//.call(d3.behavior.zoom().scaleExtent([1, 8]).on("zoom", zoom));
		//导致整个坐标系的变化，最好不要用
	function zoom() {
	           // alert(d3.event.translate);
	           // alert(d3.event.scale);
				d3.select(".layer").attr("transform", "translate(" + d3.event.translate + ")scale(" + d3.event.scale + ")");
			}
	//定义箭头
	this._svg.append('svg:defs').append('svg:marker')
	.attr('id', 'end-arrow')
	.attr('viewBox', '0 -5 10 10')
	.attr('refX', 6)
	.attr('markerWidth', 6)
	.attr('markerHeight', 6)
	.attr('orient', 'auto')
	.append('svg:path')
	.attr('d', 'M0,-5L10,0L0,5')
	.attr('fill', '#000');
	this._needsLayout = true;
	this._needsRender = true;
	var that = this;
	rap.on("render", function () {
		if (that._needsRender) {
			if (that._needsLayout) {
				that._renderer.initialize(that);
				that._needsLayout = false;
			}
			that._renderer.render(that);
			that._needsRender = false;
		}
	});
	parent.addListener("Resize", function () {
		that._resize(parent.getClientArea());
	});
	this._resize(parent.getClientArea());
};

linkmap.BaseMap.prototype = {
	
	//创建主div
	createElement : function (parent) {
		var element = document.createElement("div");
		element.setAttribute("id", "mainlinkdiv");
		element.style.position = "absolute";
		element.style.left = "0";
		element.style.top = "0";
		//element.style.width = "1000px";
		//element.style.height = "800px";
		element.style.overflow = "auto";
		element.style.background = "#ffffff";
		parent.append(element);
		return element;
	},
	//创建主菜单
	createMainMenu : function (parent) {
		var element = document.createElement("div");
		element.setAttribute("id", "Mainlinkmenu");
		element.setAttribute("class", "linkmapmenu");
		element.style.display = "none";
		parent.append(element);
	},
	//导出图 没有使用
	createCanvas : function (parent) {
	},
	
	getLayer : function (name) {
		var layer = this._svg.select("g." + name);
		
		if (layer.empty()) {
			this._svg.append("svg:g").
			attr("class", name)
			;
			layer = this._svg.select("g." + name);
			
		}
		
		return layer;
	},
	//设置svg的宽高
	_resize : function (clientArea) {
		//this._svg.attr("width", clientArea[2]).attr("height", clientArea[3]);
		this._svg.attr("width", "2000").attr("height", "1200");
		this._element.style.width = clientArea[2];
		this._element.style.height = clientArea[3];
		this._scheduleUpdate(true);
	},
	
	//重新布局
	_scheduleUpdate : function (needsLayout) {
		if (needsLayout) {
			this._needsLayout = true;
		}
		//this._needsRender = true;
	},
	
	destroy : function () {
		var element = this._element;
		if (element.parentNode) {
			element.parentNode.removeChild(element);
		}
	}
	
};
