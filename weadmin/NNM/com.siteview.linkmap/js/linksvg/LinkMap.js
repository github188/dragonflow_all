linkmap.LinkMap=function(parent){this._items=new linkmap.NodeList();this._lines=new linkmap.NodeList();this._svgLink=new linkmap.BaseMap(parent,this);this._maxX=2000;this._maxY=1200;this._compWidth=800;this._compHeight=600;this._topy=90;this._leftx=230;this._nodeDisplayType=1;this._linkDisplayType=1;this._layer=null;this.svg=null;this._nodetooltipdesc="";this._grouptooltipdesc="";this._linktooltipdesc="";this._menudesc="";this._tooltipdata=[];this._refreshdata=[];this._subchartdata=[];this.that1=null;this.maindiv=null;this.nodeclick=false;this.shiftKey=null;this.ctlKey=null;this.mousedownnode=null,this.mouseupnode=null;this.link=[];this.dragup=false;};linkmap.LinkMap.prototype={addItem:function(item){this._items.add(item);},removeItem:function(item){this._items.remove(item);},setCleardata:function(cleardata){this._items=new linkmap.NodeList();this._lines=new linkmap.NodeList();},setRender:function(render){this.render(this._BaseMap);},addLine:function(lline){this._lines.add(lline);},removeLine:function(lline){this._lines.remove(lline);},destroy:function(){this._BaseMap.destroy();},initialize:function(BaseMap){this._BaseMap=BaseMap;this._layer=BaseMap.getLayer("layer");this.svg=BaseMap._svg;},setTopy:function(topy){this._topy=topy;},getTopy:function(){return this._topy;},setLeftx:function(leftx){this._leftx=leftx;},getLeftx:function(){return this._leftx;},setNodeDisplayType:function(nodeDisplayType){this._nodeDisplayType=nodeDisplayType;},getNodeDisplayType:function(){return this._nodeDisplayType;},setLinkDisplayType:function(linkDisplayType){this._linkDisplayType=linkDisplayType;},getLinkDisplayType:function(){return this._linkDisplayType;},setMaxX:function(maxX){if(maxX>this._maxX)
this._maxX=maxX;},getMaxX:function(){return this._maxX;},setMaxY:function(maxY){if(maxY>this._maxY)
this._maxY=maxY;},getMaxY:function(){return this._maxY;},setCompWidth:function(compWidth){this._compWidth=compWidth;},getCompWidth:function(){return this._compWidth;},setCompHeight:function(compHeight){this._compHeight=compHeight;},getCompHeight:function(){return this._compHeight;},setNodetooltipdesc:function(nodetooltipdesc){this._nodetooltipdesc=nodetooltipdesc;},getNodetooltipdesc:function(){return this._nodetooltipdesc;},setGrouptooltipdesc:function(grouptooltipdesc){this._grouptooltipdesc=grouptooltipdesc;},getGrouptooltipdesc:function(){return this._grouptooltipdesc;},setLinktooltipdesc:function(linktooltipdesc){this._linktooltipdesc=linktooltipdesc;},getLinktooltipdesc:function(){return this._linktooltipdesc;},setMenudesc:function(menudesc){this._menudesc=menudesc;},getMenudesc:function(){return this._menudesc;},setTooltipdata:function(tooltipdata){this._tooltipdata=tooltipdata;},setRefreshdata:function(refreshdata){this._refreshdata=refreshdata;this.refreshData(refreshdata);},render:function(BaseMap){var head=document.getElementsByTagName('HEAD').item(0);var style=document.createElement('link');style.href='rwt-resources/linkmap.css';style.rel='stylesheet';style.type='text/css';head.appendChild(style);this.that1=this;var that1=this;that1.maindiv=document.getElementById("mainlinkdiv");var nodeshowtype=this.getNodeDisplayType();var linkshowtype=this.getLinkDisplayType();this.dragup=false;var linktips=this.getLinktooltipdesc();this.nodeclick=false;this.shiftKey=null;this.ctlKey=null;that1.mousedownnode=null,that1.mouseupnode=null;d3.select("body").on("keydown.brush",keydown).on("keyup.brush",keyup).each(function(){this.focus();});function keydown(){that1.shiftKey=d3.event.shiftKey;that1.ctlKey=d3.event.ctrlKey;};function keyup(){that1.shiftKey=null;that1.ctlKey=null;};d3.select("#mainlinkdiv").on("contextmenu",this.contextmainmenu);function resetMouseVars(){that1.mousedownnode=null;that1.mouseupnode=null;};var nodesselected=true;d3.select("#mainlinkdiv").on("mousedown",function(){nodesselected=false;var movex=that1.maindiv.scrollLeft;var movey=that1.maindiv.scrollTop;localx=d3.mouse(this)[0]+movex+16;localy=d3.mouse(this)[1]+movey+16;that1.select_rect.data([{x:localx,y:localy,w:0,h:0}]);that1.select_rect.attr("x",function(d){return d.x;}).attr("y",function(d){return d.y;}).attr("width",function(d){return d.w;}).attr("height",function(d){return d.h;});}).on("mouseup",function(){if(that1.nodeclick){that1.nodeclick=false;}else{d3.selectAll(".linkselected").classed("linkselected",function(d){d.selected=false;return false;});}
if(that1.mousedownnode){that1.drag_line.classed('hidden',true).style('marker-end','');}
if(!that1.mousedownnode){if(!nodesselected){var movex=that1.maindiv.scrollLeft;var movey=that1.maindiv.scrollTop;extentx=d3.mouse(this)[0]+movex+16;extenty=d3.mouse(this)[1]+movey+16;var rstartx,rendx,rstarty,rendy;if(localx>extentx){rstartx=extentx;rendx=localx;}else{rstartx=localx;rendx=extentx;}
if(localy>extenty){rstarty=extenty;rendy=localy;}else{rstarty=localy;rendy=extenty;}
that1.node.classed("linkselected",function(d){if(d.nx>rstartx&&d.nx<rendx&&d.ny>rstarty&&d.ny<rendy){d.selected=true;return true;}});nodesselected=true;}}
resetMouseVars();that1.select_rect.attr("width",0).attr("height",0);}).on("mousemove",function(){var movex=that1.maindiv.scrollLeft;var movey=that1.maindiv.scrollTop;var mousex=d3.mouse(this)[0]+movex;var mousey=d3.mouse(this)[1]+movey;if(!that1.mousedownnode){if(!nodesselected){var w1=mousex-localx+16;if(w1<0)
w1=0;var h1=mousey-localy+16;if(h1<0)
h1=0;that1.select_rect.data([{x:0,y:0,w:w1,h:h1}]);that1.select_rect.attr("width",function(d){return d.w;}).attr("height",function(d){return d.h;});}
return;}
that1.drag_line.attr('d','M'+that1.mousedownnode.nx+','+that1.mousedownnode.ny+'L'+mousex+','+mousey)});this._initMainMenu();this.drag_line=this._layer.append('svg:path').attr('class','maplink dragline hidden').attr('d','M0,0L0,0');var select_g=this._layer.append("g").attr("class","brush").data([{x:0.0,y:0.0,w:0.0,h:0.0}]);that1.select_rect=select_g.append("svg:rect").attr("class","selectrect").attr("fill-opacity",0).attr("stroke","red").attr("x",function(d){return d.x;}).attr("y",function(d){return d.y;}).attr("width",function(d){return d.w;}).attr("height",function(d){return d.h;});that1.link=this._layer.append("g").attr("class","maplink").selectAll("line");that1.link1=this._layer.append("g").attr("class","link1").selectAll("line");that1.linktip=this._layer.append("g").attr("class","linktip").selectAll("line");that1.linktext=this._layer.append("g").attr("class","maptext2link").selectAll("text");var nodelist=this._layer.append("g").attr("class","nodelist");that1.nodedata=new Array();that1.nodelinedata=new Array();var nodeobj={};for(var i=0;i<this._items.length;i++){var item=this._items[i];var nodeidid=item.getSvid();nodeobj["sid"]=nodeidid;nodeobj["nx"]=item.getNx()-16;nodeobj["ny"]=item.getNy()-16;nodeobj["otype"]=item.getSvgtype();nodeobj["localip"]=item.getLocalip();nodeobj["mac"]=item.getMac();nodeobj["name"]=item.getName();nodeobj["customname"]=item.getCustomname();nodeobj["factory"]=item.getFactory();nodeobj["model"]=item.getModel();that1.nodelinedata[nodeidid]=nodeobj;that1.nodedata.push(nodeobj);nodeobj={};}
that1.linetextdata=new Array();that1.lineidata=new Array();var lineobj={};for(var i=0;i<this._lines.length;i++){var item=this._lines[i];var source1=item.getLsource();var target1=item.getLtarget();var liid=item.getLid();lineobj["source"]=that1.nodelinedata[source1];lineobj["target"]=that1.nodelinedata[target1];lineobj["lid"]=liid;lineobj["sinterface"]=item.getSinterface();lineobj["tinterface"]=item.getTinterface();lineobj["flow"]=item.getFlow();lineobj["pkts"]=item.getPkts();lineobj["broadcast"]=item.getBroadcast();lineobj["bwusage"]=item.getBwusage();lineobj["avgframeLen"]=item.getAvgframeLen();lineobj["error"]=item.getError();lineobj["warn"]=item.getWarn();that1.lineidata[liid]=lineobj;that1.linetextdata.push(lineobj);lineobj={};}
this.node=nodelist.selectAll(".linknode");this.createnodes();this.initlinks();},initlinks:function(){var that=this;var linkshowtype=this.getLinkDisplayType();var linktips=this.getLinktooltipdesc();that.link=that.link.data(that.linetextdata);that.link.enter().append("line").attr("x1",function(d){return d.source.nx;}).attr("class1",function(d){if(d.source.otype==5||d.target.otype==5){return"pc";}else{return"nopc";}}).attr("y1",function(d){return d.source.ny;}).attr("x2",function(d){return d.target.nx;}).attr("y2",function(d){return d.target.ny;}).style('marker-end','url(#end-arrow)').style("Stroke",function(d){var lincc=that.getlinkcolor(d);return lincc;});that.linktip=that.linktip.data(that.linetextdata);that.linktip.enter().append("line").attr("id",function(d){return d.lid;}).attr("x1",function(d){return d.source.nx;}).attr("y1",function(d){return d.source.ny;}).attr("x2",function(d){return d.target.nx;}).attr("y2",function(d){return d.target.ny;}).style("stroke-width","16px").style("stroke","blue").style("stroke-opacity","0.0");that.linktip.append("svg:title").text(function(d){return linktips.replace('p1',d.sinterface).replace('p2',d.tinterface).replace('p3',d.flow+" Kbps").replace('p4',d.pkts+" Pkts/s").replace('p5',d.broadcast+" Pkts/s").replace('p6',d.bwusage+"% (100Mbps)").replace('p7',d.avgframeLen+" Byte/Pkt");});that.linktext=that.linktext.data(that.linetextdata);that.linktext.enter().append("text").attr("x",function(d){return(d.source.nx+d.target.nx)/2;}).attr("y",function(d){return(d.source.ny+d.target.ny)/2;}).attr("class1",function(d){if(d.source.otype==5||d.target.otype==5){return"pc";}else{return"nopc";}}).attr("transform",function(d){angle1=that.getAngle(d.source.nx,d.source.ny,d.target.nx,d.target.ny);return"rotate("+angle1+" "+((d.source.nx+d.target.nx)/2.0)+","+((d.source.ny+d.target.ny)/2.0)+")";}).attr("dy","-6").style("stroke-width","2px").style("cursor","default").style("stroke-linejoin","round").style("font-size","12px").style("fill",function(d){var lincc=that.getlinkcolor(d);return lincc;}).style("Stroke","none").style("text-anchor","middle").text(function(d){switch(linkshowtype){case 2:return d.pkts+"Pkts/s";case 3:return d.broadcast+"Pkts/s";case 4:return d.bwusage+"% (100Mbps)";default:return d.flow+"Kbps"}});},updatelinks:function(){var that=this;var linkshowtype=this.getLinkDisplayType();var linktips=this.getLinktooltipdesc();that.link.data(that.linetextdata).attr("x1",function(d){return d.source.nx;}).attr("y1",function(d){return d.source.ny;}).attr("x2",function(d){return d.target.nx;}).attr("y2",function(d){return d.target.ny;}).style("Stroke",function(d){var lincc=that.getlinkcolor(d);return lincc;});that.linktip.data(that.linetextdata).attr("id",function(d){return d.lid;}).attr("x1",function(d){return d.source.nx;}).attr("y1",function(d){return d.source.ny;}).attr("x2",function(d){return d.target.nx;}).attr("y2",function(d){return d.target.ny;});that.linktip.data(that.linetextdata).text(function(d){""});that.linktip.append("svg:title").text(function(d){var tempflow=d.flow;var stringflow=" Kbps";if(tempflow>1024){tempflow=tempflow/1024;stringflow=" Mbps";if(tempflow>1024){tempflow=tempflow/1024;stringflow=" Gbps";if(tempflow>1024){tempflow=tempflow/1024;stringflow=" Tbps";}}}
return linktips.replace('p1',d.sinterface).replace('p2',d.tinterface).replace('p3',Math.round(tempflow*10)/10+stringflow).replace('p4',d.pkts+" Pkts/s").replace('p5',d.broadcast+" Pkts/s").replace('p6',d.bwusage+"% (100Mbps)").replace('p7',d.avgframeLen+" Byte/Pkt");});that.linktext.data(that.linetextdata).attr("x",function(d){return(d.source.nx+d.target.nx)/2;}).attr("y",function(d){return(d.source.ny+d.target.ny)/2;}).attr("transform",function(d){angle1=that.getAngle(d.source.nx,d.source.ny,d.target.nx,d.target.ny);return"rotate("+angle1+" "+((d.source.nx+d.target.nx)/2.0)+","+((d.source.ny+d.target.ny)/2.0)+")";}).style("fill",function(d){var lincc=that.getlinkcolor(d);return lincc;}).style("Stroke","none").style("text-anchor","middle").text(function(d){switch(linkshowtype){case 2:return d.pkts+"Pkts/s";case 3:return d.broadcast+"Pkts/s";case 4:return d.bwusage+"";default:var tempflow=d.flow;var stringflow=" Kbps";if(tempflow>1024){tempflow=tempflow/1024;stringflow=" Mbps";if(tempflow>1024){tempflow=tempflow/1024;stringflow=" Gbps";if(tempflow>1024){tempflow=tempflow/1024;stringflow=" Tbps";}}}
return Math.round(tempflow*10)/10+stringflow}});},nudge:function(dx,dy){var that=this;that.node.filter(function(d){return d.selected;}).attr("transform",function(d){var max_x=that1.getMaxX()-20;var max_y=that1.getMaxY()-20;d.nx+=dx;d.ny+=dy;if(d.nx>max_x)
d.nx=max_x;if(d.ny>max_y)
d.ny=max_y;if(d.ny<0)
d.ny=0;return"translate("+d.nx+", "+d.ny+")"})
that.link.filter(function(d){return d.source.selected;}).attr("x1",function(d){return d.source.nx;}).attr("y1",function(d){return d.source.ny;});that.link.filter(function(d){return d.target.selected;}).attr("x2",function(d){return d.target.nx;}).attr("y2",function(d){return d.target.ny;});that.linktip.filter(function(d){return d.source.selected;}).attr("x1",function(d){return d.source.nx;}).attr("y1",function(d){return d.source.ny;});that.linktip.filter(function(d){return d.target.selected;}).attr("x2",function(d){return d.target.nx;}).attr("y2",function(d){return d.target.ny;});that.linktext.filter(function(d){return(d.source.selected||d.target.selected);}).attr("x",function(d){return(d.source.nx+d.target.nx)/2.0;}).attr("y",function(d){return(d.source.ny+d.target.ny)/2.0;}).attr("transform",function(d){angle=that.getAngle(d.source.nx,d.source.ny,d.target.nx,d.target.ny);return"rotate("+angle+" "+
((d.source.nx+d.target.nx)/2.0)+","+
((d.source.ny+d.target.ny)/2.0)+")";});d3.event.preventDefault();},createnodes:function(){var that=this;var nodeshowtype=this.getNodeDisplayType();var nodetips=this.getNodetooltipdesc();var grouptips=this.getGrouptooltipdesc();function resetMouseVars(){that.mousedownnode=null;that.mouseupnode=null;}
var drag=d3.behavior.drag().on("dragstart",function(){if(d3.select(this).classed("linkselected")){that.drag_line.classed('hidden',true).style('marker-end','');resetMouseVars();}}).on("drag",function(d){if(d3.select(this).classed("linkselected")&&!that.dragup){that.nudge(d3.event.dx,d3.event.dy);}else{}}).on("dragend",function(d){if(that1.mousedownnode){if(that1.mousedownnode===d)
that.dragup=true;if(!d3.select(this).classed("linkselected"))
d3.event.defaultPrevented();that.drag_line.classed('hidden',true).style('marker-end','');}
resetMouseVars();});that.node=that.node.data(that.nodedata);var nodeenter=that.node.enter().append("g");nodeenter.attr("class1",function(d){if(d.otype==5){return"pc";}else{return"nopc";}}).attr("class","linknode").attr("transform",function(d){return"translate("+d.nx+", "+d.ny+")"}).on("mouseup",function(d){that.nodeclick=true;if(!d.selected){if(!that.shiftKey){that.node.classed("linkselected",function(p){return p.selected=d===p;});}else{d3.select(this).classed("linkselected",d.selected=true);}}
if(!that.mousedownnode)
return;that.mouseupnode=d;if(that.mouseupnode===that.mousedownnode){that.drag_line.classed('hidden',true).style('marker-end','');resetMouseVars();return;}
var a=Math.floor((Math.random()*100)+1);var tempdata=new Array();tempdata.push(that.mousedownnode);tempdata.push(that.mouseupnode);that._savedata("addline",tempdata);linetempobj={};linetempobj["source"]=that1.mousedownnode;linetempobj["target"]=that1.mouseupnode;linetempobj["lid"]="test"+a;linetempobj["sinterface"]="6";linetempobj["tinterface"]="0";linetempobj["flow"]=20;linetempobj["pkts"]=30;linetempobj["broadcast"]=3;linetempobj["bwusage"]=0;linetempobj["avgframeLen"]=0;linetempobj["error"]=100;linetempobj["warn"]=80;that.linetextdata.push(linetempobj);that.initlinks();that.drag_line.classed('hidden',true).style('marker-end','');resetMouseVars();}).on("mousedown",function(d){that.dragup=false;var selectedd=d3.select(this).classed("linkselected")
if(!selectedd){d.selected=false;}
if(!d.selected){that1.mousedownnode=d;that.drag_line.style('marker-end','url(#end-arrow)').classed('hidden',false).attr('d','M'+d.nx+','+d.ny+'L'+d.nx+','+d.ny);}}).call(drag);nodeenter.append("text").attr("class","linknode2text").attr("x",0).attr("y",26).style("fill","#000000").style("stroke","#none").style("font-size","14px").style("cursor","default").style("text-anchor","middle").text(function(d){switch(nodeshowtype){case 2:return d.name;case 3:return d.model;case 4:return d.customname;default:return d.localip;}});that.nodetext=d3.selectAll(".linknode2text");nodeenter.append("svg:image").attr("x",-16).attr("y",-16).attr("width",32).attr("height",32).attr("id",function(d){return'image'+d.sid;}).attr("xlink:href",function(d){switch(d.otype){case 0:return"rwt-resources/linkmap/SwitchRouter_Blue.ico";case 1:return"rwt-resources/linkmap/Switch_Blue.ico";case 2:return"rwt-resources/linkmap/Router_Blue.ico";case 3:return"rwt-resources/linkmap/Firewall_Blue.ico";case 4:return"rwt-resources/linkmap/Server_Blue.ico";case 5:return"rwt-resources/linkmap/PC_Blue.ico";case 7:return"rwt-resources/linkmap/HUB_Blue.ico";case 100:return"rwt-resources/linkmap/Group_Blue.bmp";default:return"rwt-resources/linkmap/Other_Blue.ico";}});var noderects=nodeenter.append("rect").attr("x",-16).attr("y",-16).attr("width",32).attr("height",32).attr("id",function(d){return d.sid;}).style("stroke-width","2").style("stroke-dasharray","2,2").on("dblclick",function(d){if(d.otype==100){that1._savedata("looksubchart",d.name);}else{}});noderects.append("svg:title").text(function(d){switch(d.otype){case 100:return grouptips.replace('p1',d.localip).replace('p2',d.mac).replace('p3',d.factory);default:return nodetips.replace('p1',d.localip).replace('p2',d.mac).replace('p3',d.name).replace('p4',d.customname).replace('p5',d.factory).replace('p6',d.model);}});},getlinkcolor:function(d){var linkshowtype=this.getLinkDisplayType();switch(linkshowtype){case 2:if(d.pkts>d.error)
return"red";if(d.pkts>d.warn)
return"#FFD306";if(d.pkts>0)
return"#00DB00";if(d.pkts===0)
return"#272727";case 3:if(d.broadcast>d.error)
return"red";if(d.broadcast>d.warn)
return"#FFD306";if(d.broadcast>0)
return"#00DB00";if(d.broadcast===0)
return"#272727";case 4:if(d.bwusage>d.error)
return"red";if(d.bwusage>d.warn)
return"#FFD306";if(d.bwusage>0)
return"#00DB00";if(d.bwusage===0)
return"#272727";default:if(d.flow>d.error)
return"red";if(d.flow>d.warn)
return"#FFD306";if(d.flow>0)
return"#00DB00";if(d.flow===0)
return"#272727";}},refreshData:function(refreshdata){var that=this;var linkshowtype=this.getLinkDisplayType();var linktips=this.getLinktooltipdesc();refreshdata.map(function(obj,index){if(that.lineidata[obj.lid]!==undefined){that.lineidata[obj.lid].flow=obj.flow;that.lineidata[obj.lid].pkts=obj.pkts;that.lineidata[obj.lid].broadcast=obj.broadcast;that.lineidata[obj.lid].bwusage=obj.bwusage;that.lineidata[obj.lid].avgframeLen=obj.avgframeLen;}});that.link=that.link.data(that.linetextdata).style("Stroke",function(d){if(d.flow>d.error)
return"red";if(d.flow>d.warn)
return"#FFD306";if(d.flow>0)
return"#00DB00";}).style("stroke-width",function(d){var tempflow=d.flow;var stringflow="1px";if(tempflow>1024){tempflow=tempflow/1024;stringflow="2px";if(tempflow>1024){tempflow=tempflow/1024;stringflow="3px";if(tempflow>1024){tempflow=tempflow/1024;stringflow="4px";}}}
return stringflow;});that.linktip.data(that.linetextdata).text(function(d){""});that.linktip.append("svg:title").text(function(d){var tempflow=d.flow;var stringflow=" Kbps";if(tempflow>1024){tempflow=tempflow/1024;stringflow=" Mbps";if(tempflow>1024){tempflow=tempflow/1024;stringflow=" Gbps";if(tempflow>1024){tempflow=tempflow/1024;stringflow=" Tbps";}}}
return linktips.replace('p1',d.sinterface).replace('p2',d.tinterface).replace('p3',Math.round(tempflow*10)/10+stringflow).replace('p4',Math.round(d.pkts*10)/10+" Pkts/s").replace('p5',Math.round(d.broadcast*10)/10+" Pkts/s").replace('p6',Math.round(d.bwusage*10)/10+"% (100Mbps)").replace('p7',Math.round(d.avgframeLen*10)/10+" Byte/Pkt");});that.linktext.data(that.linetextdata).style("fill",function(d){switch(linkshowtype){case 2:if(d.pkts>d.error)
return"red";if(d.pkts>d.warn)
return"#FFD306";if(d.pkts>0)
return"#00DB00";if(d.pkts===0)
return"#272727";case 3:if(d.broadcast>d.error)
return"red";if(d.broadcast>d.warn)
return"#FFD306";if(d.broadcast>0)
return"#00DB00";if(d.broadcast===0)
return"#272727";case 4:if(d.bwusage>d.error)
return"red";if(d.bwusage>d.warn)
return"#FFD306";if(d.bwusage>0)
return"#00DB00";if(d.bwusage===0)
return"#272727";default:if(d.flow>d.error)
return"red";if(d.flow>d.warn)
return"#FFD306";if(d.flow>0)
return"#00DB00";if(d.flow===0)
return"#272727";}}).text(function(d){switch(linkshowtype){case 2:if(d.pkts==0){return"";}else{return Math.round(d.pkts*10)/10+"Pkts/s";}
case 3:if(d.broadcast==0){return"";}else{return Math.round(d.broadcast*10)/10+"Pkts/s";}
case 4:if(d.bwusage==0){return"";}else{return Math.round(d.bwusage*10)/10+"% (100Mbps)";}
default:var tempflow=d.flow;var stringflow=" Kbps";if(tempflow>1024){tempflow=tempflow/1024;stringflow=" Mbps";if(tempflow>1024){tempflow=tempflow/1024;stringflow=" Gbps";if(tempflow>1024){tempflow=tempflow/1024;stringflow=" Tbps";}}}
if(tempflow==0){return"";}else{return Math.round(tempflow*10)/10+stringflow}}});},_initMainMenu:function(){var that=this;var ulul=d3.select("#Mainlinkmenu").append("ul");var menudes=this.getMenudesc();var arraymenu=menudes.split(":");for(var i=0;i<arraymenu.length;i++){var subarraymenu=arraymenu[i].split("$");if(subarraymenu.length>1){var subli=ulul.append("li");subli.append("a").style("cursor","pointer").text(subarraymenu[0]);var subul=subli.append("ul").attr("id","main"+i+"");for(var j=1;j<subarraymenu.length;j++){subul.append("li").append("a").style("cursor","pointer").text(subarraymenu[j]);}}else{var lia=ulul.append("li").append("a").attr("id","main"+i+"").style("cursor","pointer").text(arraymenu[i]);}}
ulul.select("#main0").selectAll("a").on("click",function(d,index){that.refreshNodeText(index);});ulul.select("#main1").selectAll("a").on("click",function(d,i){that.refreshLinkText(i);});},refreshLinkText:function(_tag){var that=this;switch(_tag){case 1:this.setLinkDisplayType(2);that.link.data(that.linetextdata).style("Stroke",function(d){var lincc=that.getlinkcolor(d);return lincc;});that.linktext.data(that.linetextdata).style("fill",function(d){var lincc=that.getlinkcolor(d);return lincc;}).text(function(d){if(d.pkts==0){return""}else{return Math.round(d.pkts*10)/10+"Pkts/s"}});break;case 2:this.setLinkDisplayType(3);that.link.data(that.linetextdata).style("Stroke",function(d){var lincc=that.getlinkcolor(d);return lincc;});that.linktext.data(that.linetextdata).style("fill",function(d){var lincc=that.getlinkcolor(d);return lincc;}).text(function(d){if(d.broadcast==0){return""}else{return Math.round(d.broadcast*10)/10+" Pkts/s"}});break;case 3:this.setLinkDisplayType(4);that.link.data(that.inetextdata).style("Stroke",function(d){var lincc=that.getlinkcolor(d);return lincc;});that.linktext.data(that.linetextdata).style("fill",function(d){var lincc=that.getlinkcolor(d);return lincc;}).text(function(d){if(d.bwusage==0){return""}else{return Math.round(d.bwusage*10)/10+"%  (100Mbps)"}});break;default:this.setLinkDisplayType(1);that.link.data(that.linetextdata).style("Stroke",function(d){var lincc=that.getlinkcolor(d);return lincc;});that.linktext.data(that.linetextdata).style("fill",function(d){var lincc=that.getlinkcolor(d);return lincc;}).text(function(d){var tempflow=d.flow;var stringflow=" Kbps";if(tempflow>1024){tempflow=tempflow/1024;stringflow=" Mbps";if(tempflow>1024){tempflow=tempflow/1024;stringflow=" Gbps";if(tempflow>1024){tempflow=tempflow/1024;stringflow=" Tbps";}}}
if(tempflow==0){return""}else{return Math.round(tempflow*10)/10+stringflow}});}},refreshNodeText:function(_tag){var that=this;switch(_tag){case 1:that.nodetext.text(function(d){return d.model});break;case 2:that.nodetext.text(function(d){return d.name});break;case 3:that.nodetext.text(function(d){return d.customname});break;default:that.nodetext.text(function(d){return d.localip});}},contextmainmenu:function(){var position=d3.mouse(this);var xx=position[0];var yy=position[1];d3.select('#Mainlinkmenu').style('position','absolute').style('left',xx+"px").style('top',yy+"px").style('display','inline-block').on('mouseleave',function(){d3.select('#Mainlinkmenu').style('display','none');});d3.event.preventDefault();},getAngle:function(px1,py1,px2,py2){x=px2-px1;y=py2-py1;hypotenuse=Math.sqrt(Math.pow(x,2)+Math.pow(y,2));cos=x/hypotenuse;radian=Math.acos(cos);angle=180*radian/Math.PI;if(y<0){angle=-angle;}else if((y==0)&&(x<0)){angle=180;}
if(angle>=90)
angle=angle+180;if(angle<=-90)
angle=angle-180;return angle;},_removeElements:function(selection){selection.transition().duration(400).attr("opacity",0.0).remove();},_savedata:function(index,data){var remoteObject=rap.getRemoteObject(this);remoteObject.notify("Selection",{"index":index,"data":data});},_selectItem:function(index){var remoteObject=rap.getRemoteObject(this);remoteObject.notify("Selection",{"index":index,"data":""});}};rap.registerTypeHandler("linkmap.LinkMap",{factory:function(properties){var parent=rap.getObject(properties.parent);return new linkmap.LinkMap(parent);},destructor:"destroy",properties:["nodetooltipdesc","grouptooltipdesc","linktooltipdesc","refreshdata","layoutdata","menudesc","tooltipdata","maxX","maxY","compWidth","compHeight","nodeDisplayType","linkDisplayType","topy","leftx","cleardata","render"],events:["Selection"]});