/*******************************************
 *date: 2014/9/28
 *auther: bin.liu
 *svgVisio
 ********************************************/

d3svgvisio = {};
d3svgvisio.SvgVisio = function (parent) {
	//��������ͼԭʼ�Ŀ��ֵ
	this._width="";
	//��������ͼԭʼ�ĸ߶�ֵ
	this._heigth="";
	this._svgcontent = "";
	this.data = new Array();
	this.linkdata=new Array();
	this.linedata=new Array();
	this._mdata = [];
	this._devdata = [];
	this._topy = 200; //�������top�ľ���
	this._leftx = 300; //�������left�ľ���
	this._element = this.createElement(parent);
	this.createMenu(parent);
	this._needsLayout = true;
	this._m= false;
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
// 		that.saveSvgSize();
//		that._resizeSvg("svgContent",that._element.innerHTML);
	});
	this._resize(parent.getClientArea());
};

d3svgvisio.SvgVisio.prototype = {
	
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
	saveSvgSize : function(){
		var svgobj = this._element.getElementsByTagName("svg")[0];
		if(svgobj==this.undefine){
			return;
		}
		else{
		var w = svgobj.getAttribute("width");
		var h = svgobj.getAttribute("height");
		this._width = w.substring(0,w.length-2);
		this._height = h.substring(0,h.length-2);
		}
	},
	createMenu : function (parent) {
		//var element = document.createElement("div");
		//element.setAttribute("id","mymenu");
		//element.setAttribute("class", "vMenu");
		//element.style.display = "none";
		//parent.append(element);
		var element = document.createElement("div");
		element.innerHTML ="<input id='radio1' type='button' class='radio1' class='radio1' value='Monitor'/><input type='button' class='radio1'  id='radio2' value='Asset'/><div class='vmoitorinfo' ></div>";
		element.setAttribute("class","vmenu");
		element.style.display = "none";
		parent.append(element);
		
	},
	/**
	 ** update device menu
	 **/
	updatemenu : function () {
		
		var that = this;
		
		//
		d3.selectAll(".svip").on("click", function (d, i) {
			that._m =true;
			//alert(position);
			var ipdevice = d3.select(this);
			var svip = ipdevice.attr("svip");
			var strMenuHTML = "";
			var strAssertHTML = "";
			var state = 1;
			var mid = "";
			var strDesc = "";
			var ipdata = that.data.filter(function (value) {
					return value.ip == svip;
				});
			var devicedata = that._devdata.filter(function (value) {
					return value.ip == svip;
				});
			 if(devicedata==null || devicedata==''){
			 d3.select("#radio2").style('visibility','hidden');
			 }else{
			   d3.select("#radio2").style('visibility','visible');
			 }
			//device state 100 ok 101 warning 102 error
			ipdata.map(function (obj, index) {
				state = obj.state;
				mid = obj.mid;
				//device state manage
				if (state >= 100) {
					state = 100;
				}
				strDesc = obj.desc;
				//�ҵ��������tilte��������ʾ�Ӵ֡�
				var index = strDesc.indexOf('::');
				var temp = "<b>"+strDesc.substr(0,index)+"</b>";
				strDesc = strDesc.replace(strDesc.substr(0,index),temp);
				
				
				switch (state) {
				case 100:
					strDesc = "<span style='position:relative;width:13;height:10;COLOR:Blue'>::</span>&nbsp;" + strDesc
						break;
				case 1:
					strDesc = "<img src='rwt-resources/svgvisio/state_green.gif' width=15 height=15 style='position: relative;top: 4px;'>" + strDesc
						break;
				case 2:
					strDesc = "<img src='rwt-resources/svgvisio/state_yellow.gif' width=15 height=15 style='position: relative;top: 3px;'>" + strDesc
						break;
				case 3:
					strDesc = "<img src='rwt-resources/svgvisio/state_red.gif' width=15 height=15 style='position: relative;top: 3px;'>" + strDesc
						break;
				case 4:
					strDesc = "<img src='rwt-resources/svgvisio/state_stop.gif' width=15 height=15 style='position: relative;top: 3px;'>" + strDesc;
					break;
				case 5:
					strDesc = "<img src='rwt-resources/svgvisio/state_red.gif' width=15 height=15 style='position: relative;top: 3px;'>" + strDesc
						break;
				}
				strMenuHTML += "<div class='menuItem'  doAction=" + mid + " style='padding: 2px 2px 2px 2px;cursor:hand;font-size:9pt;background: #eeeeee;'>";
				strMenuHTML += strDesc + "</div>";
			});
			devicedata.map(function (obj, index) {
				
				mid = obj.mid;
				strDesc = obj.desc;
				
				var index = strDesc.indexOf(':');
				var temp = "<b>"+strDesc.substr(0,index)+"</b>";
				strDesc = strDesc.replace(strDesc.substr(0,index),temp);
				
				switch (state) {
				case 100:
					strDesc = "<span style='position:relative;width:13;height:10;COLOR:Blue'>::</span>&nbsp;" + strDesc
						break;
				}
				strAssertHTML += "<div class='menuItem'  doAction=" + mid + " style='padding: 2px 2px 2px 2px;cursor:hand;font-size:9pt;background: #eeeeee;'>";
				strAssertHTML += strDesc + "</div>";
			});
			d3.select('.vmoitorinfo').style('visibility','visible')
			.html(strMenuHTML);
			//d3.select('.vmenu')
			//.html(strMenuHTML);
			var len = ipdata.length;
			if (len>12) d3.select('.vmenu').style('height','304px');
			else {
				var height;
				switch (len){
					case 1:height='25px';break;
					case 2:height='80px';break;
					case 3:height='100px';break;
					case 4:height='130px';break;
					case 5:height='150px';break;
					case 6:height='180px';break;
					case 7:height='210px';break;
					case 8:height='240px';break;
					case 9:height='270px';break;
					case 10:height='304px';break;
					case 11:height='304px';break;
					case 12:height='304px';break;
				}
				d3.select('.vmenu').style('height',height);
			}
//			d3.select('.vmenu').style('height','auto');
           
			var topy  = that.getTopy();
			var leftx = that.getLeftx();
			
			var xx = d3.event.clientX - 5 + document.body.scrollLeft -leftx; 
			var yy = d3.event.clientY - 5 + document.body.scrollTop -topy;
			
			var menuWidth = 460;//�˵����
			
			
			var menuHeight = d3.selectAll('.vmenu').style('height');
			
//          var menuHeight = document.getElementById(m).offsetHeight;
			menuHeight = parseInt(menuHeight.replace('px',''));//�˵��߶�
			
			var right = xx+menuWidth;//�˵����Ҷ�λ��
			var bottom = yy+menuHeight;//�˵���׶�λ��
			//���˵����Ҷ˳�������ʾ�����򽫲˵������ƶ���ֱ�����Ҷ˲�������ʾ����
			if(right>that.parent.getClientArea()[2]){
				xx = xx-(right-that.parent.getClientArea()[2])
			}
			
			//���˵���׶˳�������ʾ�����򽫲˵������ƶ���ֱ����׶˲�������ʾ����
			if(bottom>that.parent.getClientArea()[3]){
				yy = yy-(bottom-that.parent.getClientArea()[3])
			}
			
			 d3.select('.vmenu')
			.style('position', 'absolute')
			.style('left', xx + "px")
			.style('top', yy + "px")
			.style('display', 'inline-block')
			.on('mouseleave', function () {
				//d3.select('.vmenu').style('display', 'none');
			});
			d3.selectAll(".menuItem").on("click", function (d, i) {
				var mitem = d3.select(this);
				var svid = mitem.attr("doAction");
				that.selectItem("action", svid);
			});
			d3.selectAll(".radio1").on("click", function (d, i) {
			d3.select('.vmenu').style('display', 'inline-block'); 
			if(i===0){
			 d3.select('.vmoitorinfo').style('visibility','visible').html(strMenuHTML);
			 d3.selectAll(".menuItem").on("click", function (d, i) {
				var mitem = d3.select(this);
				var svid = mitem.attr("doAction");
				that.selectItem("action", svid);
			});
			  }else{
			   d3.select('.vmoitorinfo').style('visibility','visible').html(strAssertHTML);
			    
			  }
			});
			d3.event.preventDefault();
			//document.getElementById('nodeId').value= node
		});
		
	},
	/**
	 ** 100 ok 101 warning 102 error
	 **/
	updatedevicestate : function () {
		var that = this;
		if(that.data==null) return;
		d3.selectAll(".svip").attr("filter", function (d, i) {
			var ipdevice = d3.select(this);
			var svip = ipdevice.attr("svip");
			var ipdata = that.data.filter(function (value) {
					return value.ip == svip;
				});
			var sfilter = "";
			
			ipdata.map(function (obj, index) {
				var state = obj.state;
				if (state == 102) {
					sfilter = "url(#svgerror)";
				} else if (state == 101) {
					sfilter = "url(#svgwarn)";
				} else if (state == 100) {
					sfilter = "url(#svgok)";
				}
			});
			return sfilter;
		});
		
	},
	/**
	 ** 100 ok 101 warning 102 error
	 **/
	updatelinkstate :function(){
	 var that = this;
	 if (that.linkdata==null) return;
	 d3.selectAll(".svlink").attr("filter", function (d, i) {
			var svlinkdevice = d3.select(this);
			var svlink = svlinkdevice.attr("svlink");
			var ipdata = that.linkdata.filter(function (value) {
					return value.link == svlink;
				});
			var sfilter = "";
			
			ipdata.map(function (obj, index) {
				var state = obj.state;
				if (state == 102) {
					sfilter = "url(#svgerror)";
				} else if (state == 101) {
					sfilter = "url(#svgwarn)";
				} else if (state == 100) {
					sfilter = "url(#svgok)";
				}
			});
			return sfilter;
		});
	},
	updatelinestate:function(){
	
	var that = this;
	 if (that.linedata==null) return;
	 d3.selectAll(".svline").attr("style", function (d, i) {
			var svlinedevice = d3.select(this);
			var svline = svlinedevice.attr("svline");
			var ipdata = that.linedata.filter(function (value) {
					return value.line == svline;
				});
			var sfilter = "fill:none";
			
			ipdata.map(function (obj, index) {
				var state = obj.state;
				var value = obj.interf;
				svlinedevice.select("text").text(value);
				if (state == 102) {
				   
					svlinedevice.select("path").attr("style","stroke:red");
					svlinedevice.select("text").attr("style","fill:red");
				} else if (state == 101) {
					svlinedevice.select("path").attr("style","stroke:#ffc20e");
					svlinedevice.select("text").attr("style","fill:#ffc20e");
				} else if (state == 100) {
					svlinedevice.select("path").attr("style","stroke:green");
					svlinedevice.select("text").attr("style","fill:green");
				}
			});
			return sfilter;
		});
	},
	initialize : function () {
		var head = document.getElementsByTagName('HEAD').item(0);
		var style = document.createElement('link');
		style.href = 'rwt-resources/svgvisio.css';
		style.rel = 'stylesheet';
		style.type = 'text/css';
		head.appendChild(style);
		this.onlinkclick();
		this.updatedevicestate();
		this.updatelinestate();
		this.updatelinkstate();
		this.updatemenu();
		var that=this;
		d3.select("body")
		.on("mouseup", function () {
		if(that._m)
         {
		 //alert("dddddd");
		 that._m=false;
		 }
        {		
          //alert("dddddd1");		
		d3.select('.vmenu').style('display', 'none');
		}
		});
	},
	/**
	** go to web 
	**/
	onlinkclick : function () {
		var that = this;
		d3.selectAll(".svlink").on("click", function (d, i) {
			var ipdevice = d3.select(this);
			var svip = ipdevice.attr("svlink");
			that.selectItem("link", svip);
		});
	},
	
	_resize : function (clientArea) {
		this._element.style.width=clientArea[2]+"px";
		this._element.style.height=clientArea[3]+"px";
		var svgobj = this._element.getElementsByTagName("svg")[0];
		if(svgobj==this.undefine){
		}
		else{
			svgobj.setAttribute("width",clientArea[2]+"px");
			svgobj.setAttribute("height",clientArea[3]+"px");
			this.saveSvgSize();
		}
		this._scheduleUpdate(true);
	},
	//�����������ͼ��С����ı�֪ͨ�������ˣ�index��ֵΪsvgContent��dataΪsvg���ַ�
	/**_resizeSvg : function (index, data) {
		var remoteObject = rap.getRemoteObject(this);
		remoteObject.notify("Selection", {
			"index" : index,
			"data" : data
		});
	},*/
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
		this._svgcontent = svgcontent;
		//var svgcontent = this.getSvgcontent();
		//alert(this._element);
		this._element.innerHTML = svgcontent;
		this._resize(this.parent.getClientArea());
		this._scheduleUpdate(true);
//		var w = d3.select('svg').attr("width");
//		var h = d3.select('svg').attr("height");
//		this._width = w.substring(0,w.length-2);
//		this._height = h.substring(0,h.length-2);
	},
	getSvgcontent : function () {
		return this._svgcontent;
	},
	/**
	** set monitor data 
	**/
	setMdata : function (mdata) {
		this._mdata = mdata;
		this.data = mdata;
		this.updatedevicestate();
		this.updatemenu();
	},
	getMdata : function () {
		return this._mdata;
	},
		/**
	** set device data 
	**/
	setDevdata : function (devdata) {
		this._devdata = devdata;
	},
	getDevdata : function () {
		return this._devdata;
	},
	/**
	**
	**/
	setLinkdata : function (linkdata) {
		this.linkdata = linkdata;
		this.updatelinkstate();
	},
	getLinkdata : function () {
		return this.linkdata;
	},
	/**
	**
	**/
	setLinedata : function (linedata) {
		this.linedata = linedata;
		this.updatelinestate();
	},
	getLinedata : function () {
		return this.linedata;
	},
	//���top
	setTopy : function (topy) {
		this._topy = topy;
	},
	getTopy : function () {
		
		return this._topy;
	},
	//���left
	setLeftx : function (leftx) {
		this._leftx = leftx;
	},
	getLeftx : function () {
		
		return this._leftx;
	},
	
	setNewSize : function (newSize) {
//		alert(newSize);
//		alert(this._element.innerHTML);
		if(!d3.select('svg')){
			return;
		}
		if(!d3.select('svg').attr("width")){
			return;
		}
		if(!d3.select('svg').attr("height")){
			return;
		}
		var temp = d3.select('svg').attr("width");
		var unit = d3.select('svg').attr("width").substring(temp.length-2);
//		alert(unit);
		var newW = parseFloat(this._width)*newSize
//		alert(newW);
		d3.select('svg').attr("width",newW+unit);
		var newH = parseFloat(this._height)*newSize;
//		alert(newH);
		d3.select('svg').attr("height",newH+unit);
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

rap.registerTypeHandler("d3svgvisio.SvgVisio", {
	
	factory : function (properties) {
		var parent = rap.getObject(properties.parent);
		return new d3svgvisio.SvgVisio(parent);
	},
	
	destructor : "destroy",
	//newSize�������ڴ�������ͼ��ʾ�ı���
	properties : ["svgcontent", "mdata" ,"devdata" ,"linkdata" ,"linedata","topy" ,"leftx","newSize"],
	
	events : ["Selection"]
	
});
