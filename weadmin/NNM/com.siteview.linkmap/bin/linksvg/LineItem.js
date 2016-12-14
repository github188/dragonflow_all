/********************************************
 *date: 2014/5/22
 *auther: bin.liu
 * line and node
 *Lineitem
 ********************************************/
linkmap.LineItem = function (parent) {
	this._parent = parent;
	this._lid  = "";//线标示 nodeid:portid
	this._lsource = ""; //line 的source
	this._ltarget = ""; //line 的target
	this._sinterface = "";//source interface
	this._tinterface = "";//target interface
	this._flow = 0.0; //流量
	this._pkts = 0.0;//帧流量
	this._broadcast = 0.0;//广播
	this._bwusage = 0.0;//带宽占用比
	this._avgframeLen = 0.0;//平均帧长度
	this._error = 0.0; // 错误阀值
	this._warn = 0.0;  // 危险阀值
	this._parent.addLine(this);
};

linkmap.LineItem.prototype = {
	
	getParent : function () {
		return this._parent;
	},
	getLid : function () {
		return this._lid;
	},
	setLid : function (lid) {
		this._lid = lid;
	},
	getLsource : function () {
		return this._lsource;
	},
	setLsource : function (lsource) {
		this._lsource = lsource;
	},
	getLtarget : function () {
		return this._ltarget;
	},
	setLtarget : function (ltarget) {
		this._ltarget = ltarget;
	},
	getSinterface : function () {
		return this._sinterface;
	},
	setSinterface : function (sinterface) {
		this._sinterface = sinterface;
	},
	getTinterface : function () {
		return this._tinterface;
	},
	setTinterface : function (tinterface) {
		this._tinterface = tinterface;
	},
	getFlow : function () {
		return this._flow;
	},
	setFlow : function (flow) {
		this._flow = flow;
	},
	getPkts : function () {
		return this._pkts;
	},
	setPkts : function (pkts) {
		this._pkts = pkts;
	},
	getBroadcast : function () {
		return this._broadcast;
	},
	setBroadcast : function (broadcast) {
		this._broadcast = broadcast;
	},
	getBwusage : function () {
		return this._bwusage;
	},
	setBwusage : function (bwusage) {
		this._bwusage = bwusage;
	},
	getAvgframeLen : function () {
		return this._avgframeLen;
	},
	setAvgframeLen : function (avgframeLen) {
		this._avgframeLen = avgframeLen;
	},
	getError : function () {
		return this._error;
	},
	setError : function (error) {
		this._error = error;
	},
	getWarn : function () {
		return this._warn;
	},
	setWarn : function (warn) {
		this._warn = warn;
	},
	id : function () {
		return this._rwtId;
	},
	
	destroy : function () {
		this._parent.removeItem(this);
	}
	
};

// TYPE HANDLER

rap.registerTypeHandler("linkmap.LineItem", {
	
	factory : function (properties) {
		var parent = rap.getObject(properties.parent);
		return new linkmap.LineItem(parent);
	},
	destructor : "destroy",
	properties : [ "lid","lsource", "ltarget","sinterface","tinterface",
	              "flow","pkts","broadcast","bwusage","avgframeLen",
				  "error","warn"]
	
});
