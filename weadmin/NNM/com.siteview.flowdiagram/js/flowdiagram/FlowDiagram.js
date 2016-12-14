d3flowdiagram = {};
d3flowdiagram.FlowDiagram = function (parent) {
	this._width = "";
	this._heigth = "";
	this._svgcontent = "";
	this.odata = new Array();
	this.tdata = new Array();
	this._topy = 200;
	this._leftx = 300;
	this._element = this.createElement(parent);
	this._needsLayout = false;
	var that = this;
	this.parent = parent;
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
d3flowdiagram.FlowDiagram.prototype = {
	createElement : function (parent) {
		var element = document.createElement("div");
		element.style.position = "absolute";
		element.style.left = "0";
		element.style.top = "0";
		element.style.overflow = "auto";
		parent.append(element);
		return element;
	},
	saveSvgSize : function () {
		var svgobj = this._element.getElementsByTagName("svg")[0];
		if (svgobj == this.undefine) {
			return;
		} else {
			var w = svgobj.getAttribute("width");
			var h = svgobj.getAttribute("height");
			this._width = w.substring(0, w.length - 2);
			this._height = h.substring(0, h.length - 2);
		}
	},
	initialize : function () {
		var svo = d3.selectAll('.sv_o')[0];
		var oo = '';
		svo.map(function (obj, index) {
			oo = oo + obj.getAttribute('sv_v') + ',';
		});
		var tt = '';
		var svt = d3.selectAll('.sv_t')[0];
		svt.map(function (obj, index) {
			tt = tt + obj.getAttribute('sv_v') + ',';
		});
		this.selectItem("sv_o", oo);
		this.selectItem("sv_t", tt);
	},
	_resize : function (clientArea) {
		this._element.style.width = clientArea[2] + "px";
		this._element.style.height = clientArea[3] + "px";
		var svgobj = this._element.getElementsByTagName("svg")[0];
		if (svgobj == this.undefine) {}
		else {
			svgobj.setAttribute("width", clientArea[2] + "px");
			svgobj.setAttribute("height", clientArea[3] + "px");
			this.saveSvgSize();
		}
	},
	_scheduleUpdate : function (needsLayout) {
		if (needsLayout) {
			this._needsLayout = true;
		}
	},
	setSvgcontent : function (svgcontent) {
		this._svgcontent = svgcontent;
		this._element.innerHTML = svgcontent;
		this._resize(this.parent.getClientArea());
		this._scheduleUpdate(true);
	},
	getSvgcontent : function () {
		return this._svgcontent;
	},
	setTopy : function (topy) {
		this._topy = topy;
	},
	getTopy : function () {
		return this._topy;
	},
	setLeftx : function (leftx) {
		this._leftx = leftx;
	},
	getLeftx : function () {
		return this._leftx;
	},
	setNewSize : function (newSize) {
		if (!d3.select('svg')) {
			return;
		}
		if (!d3.select('svg').attr("width")) {
			return;
		}
		if (!d3.select('svg').attr("height")) {
			return;
		}
		var temp = d3.select('svg').attr("width");
		var unit = d3.select('svg').attr("width").substring(temp.length - 2);
		var newW = parseFloat(this._width) * newSize
			d3.select('svg').attr("width", newW + unit);
		var newH = parseFloat(this._height) * newSize;
		d3.select('svg').attr("height", newH + unit);
	},
	setOdata : function (odata) {
		this.odata = odata;
		this.updateostate();
	},
	getOdata : function () {
		return this.odata;
	},
	updateostate : function () {
		var that = this;
		if (that.odata == null)
			return;
		that.odata.map(function (obj, index) {
			var svid = obj.svid;
			var vv = obj.vv;
			if (vv = 1) {
				d3.select("[sv_v='" + svid + "']").transition().delay(200*index) .attr("filter", 'url(#svgok)');
			}
		});
	},
	updatetstate :function(){
	 var that = this;
		if (that.tdata == null)
			return;
		that.tdata.map(function (obj, index) {
			var svid = obj.svid;
			var vv = obj.vv;
			if (vv = 1) {
				d3.select("[sv_v='" + svid + "']").transition().delay(200*index) .attr("filter", 'url(#svgok)');
			}
		});
	},
	setTdata : function (tdata) {
		this.tdata = tdata;
		this.updatetstate();
	},
	getTdata : function () {
		return this.tdata;
	},
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
rap.registerTypeHandler("d3flowdiagram.FlowDiagram", {
	factory : function (properties) {
		var parent = rap.getObject(properties.parent);
		return new d3flowdiagram.FlowDiagram(parent);
	},
	destructor : "destroy",
	properties : ["svgcontent", "topy", "leftx", "newSize", "odata", "tdata"],
	events : ["Selection"]
});
