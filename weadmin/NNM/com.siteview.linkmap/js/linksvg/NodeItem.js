/********************************************
 *date: 2014/5/22
 *auther: bin.liu
 * line and node
 *nodeitem
 ********************************************/
linkmap.NodeItem = function (parent) {
	this._parent = parent;
	this._svgtype = 0; // node{0 switch-route 1 switch 2 Routing  3 firewall 4 server 5 pc 6 other 7hub 99Unknown 100 group}
	this._Nx = 0.0; //node 的x坐标
	this._Ny = 0.0; //node 的y坐标
	this._svid = ""; //node的id
	this._text = "";//node 显示
	this._localip = "";//ip地址
    this._mac = ""; //mac地址
    this._name = "";//设备名称
    this._customname = "";//自定义名
    this._factory = "";//厂商
    this._model = "";//设备型号	
	this._parent.addItem(this);
};

linkmap.NodeItem.prototype = {
	
	getParent : function () {
		return this._parent;
	},
	getSvid : function () {
		return this._svid;
	},
	
	setSvid : function (svid) {
		this._svid = svid;
	},
	
	getSvgtype : function () {
		return this._svgtype;
	},
	
	setSvgtype : function (svgtype) {
		this._svgtype = svgtype;
	},
	
	getNx : function () {
		return this._nx;
	},
	setNx : function (nx) {
		this._nx = nx;
	},
	getNy : function () {
		return this._ny;
	},
	setNy : function (ny) {
		this._ny = ny;
	},
	getText : function () {
		return this._text;
	},
	setText : function (text) {
		this._text = text;
	},
	getLocalip : function () {
		return this._localip;
	},
	setLocalip : function (localip) {
		this._localip = localip;
	},
	getMac : function () {
		return this._mac;
	},
	setMac : function (mac) {
		this._mac = mac;
	},
	getName : function () {
		return this._name;
	},
	setName : function (name) {
		this._name = name;
	},
	getCustomname : function () {
		return this._customname;
	},
	setCustomname : function (customname) {
		this._customname = customname;
	},
	getFactory : function () {
		return this._factory;
	},
	setFactory : function (factory) {
		this._factory = factory;
	},
	getModel : function () {
		return this._model;
	},
	setModel : function (model) {
		this._model = model;
	},
	id : function () {
		return this._rwtId;
	},
	
	destroy : function () {
		this._parent.removeItem(this);
	}
	
};

// TYPE HANDLER

rap.registerTypeHandler("linkmap.NodeItem", {
	
	factory : function (properties) {
		var parent = rap.getObject(properties.parent);
		return new linkmap.NodeItem(parent);
	},
	destructor : "destroy",
	properties : ["svid", "svgtype", "nx", "ny","text", "localip",
	              "mac","name","customname","factory","model"]
	
});
