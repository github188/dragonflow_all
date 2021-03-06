mermaidjs = {};
mermaidjs.MdFlowDesign = function(c) {
  this._parent = c;
  var a = document.getElementsByTagName("HEAD").item(0);
  var b = document.createElement("link");
  b.href = "rwt-resources/mermaid.css";
  b.rel = "stylesheet";
  b.type = "text/css";
  a.appendChild(b);
  this._element = null;
  this._width = 0;
  this.flowv = "";
  this.mddname = "";
  this.showstate = 0;
  this.saveresult = 0;
  this.businessobjects = [];
  this.statedata = [];
  this.imagesdata = [];
  this.resultdata = [];
  this.lastname = "";
  this._m = false;
  this.subvisible = 0;
  this._needsLayout = true;
  var d = this;
  rap.on("render", function() {
    if (d._needsLayout) {
      d.initialize(c.getClientArea());
      d._needsLayout = false
    }
  });
  c.addListener("Resize", function() {
    d._resize(c.getClientArea())
  });
  this._resize(c.getClientArea())
};
mermaidjs.MdFlowDesign.prototype = {
  createForm: function() {
    var b = "<h4  id = 'modal_title' style='font-size:18px;font-weight:300' >添加线路描述</h4> <table cellPadding='2' cellSpacing='4'><tr><td height='30px'>线路描述 :</td><td height='30px'><input type = 'text' name = 'line_des' id = 'line_des' style = 'width: 300px' /></td></tr><tr><td height='30px'>响应前状态:</td><td height='30px'><select name='selectstate' id='selectstate' size='1' style='width: 300px' />< /td></tr><tr><td height='30px'>响应后状态:</td><td height='30px'><select name='selectstate1' id='selectstate1' size='1' style='width: 300px' />< /td></tr><tr><td height='30px'>维护角色:</td><td height='30px'><input type='checkbox'  name='cbwhjs' id='cbwhjs'/> 维护工单角色用来触发工单响应时间</td></tr></table><div  style='margin:6'><table><tr><td  style='border-top:none;width:300px;padding-left:240px'><button type='button' id='modalok'> 确定 </button></td><td style='border-top:none;width:100px'><button type='button'  id='modalclose'  > 关闭 </button></td></tr></table></div>";
    var a = document.createElement("div");
    a.setAttribute("id", "modal_wrapper");
    a.innerHTML = b;
    a.style.background = " #eeeeee";
    a.style.border = "1px solid #ccc";
    a.style.zIndex = "100";
    a.style.paddingLeft = "10px";
    a.style.display = "none";
    this._parent.append(a)
  },
  createmdElement: function(a) {
    var b = document.createElement("div");
    b.setAttribute("class", a);
    this._parent.append(b);
    return b
  },
  createMenu: function(a) {
    var b = document.createElement("div");
    var c = "mainmenu" + a;
    b.innerHTML = "<div style='height:30px;line-height:30px;padding-left:6px;'> 新建业务对象</div><div class=" + c + " style='width: 180px; border: 1px solid #ccc;border-top:none;position: relative; font-size:0;list-style: none; margin: 0; padding: 0; display:block;z-index:9;'></div>";
    b.setAttribute("class", "main" + a);
    b.style.background = " #eeeeee";
    b.style.border = "1px solid #ccc";
    b.style.display = "none";
    this._parent.append(b)
  },
  createSubMenu: function(a) {
    var b = document.createElement("div");
    var c = "submenu" + a;
    b.innerHTML = "<div style='height:30px;line-height:30px;padding-left:6px;'> 分配对象</div><div class=" + c + " style='width: 180px; border: 1px solid #ccc;border-top:none;position: relative; font-size:0;list-style: none; margin: 0; padding: 0; display:block;z-index:9;'></div>";
    b.setAttribute("class", "sub" + a);
    b.style.background = " #eeeeee";
    b.style.border = "1px solid #ccc";
    b.style.display = "none";
    this._parent.append(b)
  },
  createInfoMenu: function(a) {
    var b = document.createElement("div");
    var c = "userinfo" + a;
    b.innerHTML = "<div style='height:30px;line-height:30px;padding-left:6px;'> 流程信息</div><div class=" + c + " style='width: 180px; border: 1px solid #ccc;border-top:none;position: relative; font-size:0;list-style: none; margin: 0; padding: 0; display:block;z-index:9;'></div>";
    b.setAttribute("class", "vuserMenu" + a);
    b.style.background = " #eeeeee";
    b.style.border = "1px solid #ccc";
    b.style.display = "none";
    this._parent.append(b)
  },
  initialize: function(c) {
    var a = this;
    var b = new Date().getTime();
    var d = "design" + b;
    this.mddname = d;
    this._element = this.createmdElement(d);
    this.createForm()
  },
  _resize: function(a) {
    this._width = a[2]
  },
  _scheduleUpdate: function(a) {
    if (a) {
      this._needsLayout = true
    }
  },
  updatemenu: function() {
    var c = this;
    var i = c.getBusinessobjects();
    var a = "";
    var h = "";
    var g = " ";
    var b = "<ul>";
    try {
      i.map(function(j, e) {
        a = j.name;
        h = j.boid;
        c.imagesdata[a] = j;
        b += "<li class='menuItem' name=" + a + " doAction=" + h + " style='background: #FFF url(rwt-resources/mdflowdesign/bg.gif) repeat-x 0 2px;list-style:url(rwt-resources/mdflowdesign/arrow.gif); margin: 0; padding: 0;' > <a href='#' style='font: normal 12px Arial;  border-top: 1px solid #ccc;display: block;color: black;text-decoration: none;line-height:26px;padding-left:26px; '><span>";
        b += a + "</span></a></li>"
      });
      b += "</ul>";
      d3.select(".mainmenu" + c.mddname).style("visibility", "visible").html(b);
      var f = 0;
      d3.select(c._element).on("click", function() {
        var l = d3.select(c._element).node();
        var e = d3.mouse(l);
        var n = e[0];
        var o = e[1];
        var m = d3.select(".main" + c.mddname);
        if (m.style("display") != "none") {
          m.style("display", "none");
          return
        }
        var k = d3.select(".sub" + c.mddname);
        if (k.style("display") != "none" && c.subvisible == 0) {
          k.style("display", "none");
          return
        }
        if (c.subvisible == 0) {
          k.style("display", "none");
          var j = c.getFlowv();
          if (j.length > 10) {
            return
          }
          m.style("position", "absolute").style("left", n + "px").style("top", o + "px").style("display", "inline-block")
        } else {
          c.subvisible = 0;
          m.style("display", "none")
        }
      });
      d3.select(".mainmenu" + c.mddname).selectAll(".menuItem").on("click", function(o, m) {
        var e = d3.select(this);
        var l = e.attr("name");
        if (l == "清除当前流程") {
          c.setFlowv("graph LR;");
          c.resultdata = [];
          d3.select(".main" + c.mddname).style("display", "none")
        } else {
          if (l == "保存") {
            c.selectItem("result", c.resultdata);
            console.log(c.resultdata);
            d3.select(".main" + c.mddname).style("display", "none")
          } else {
            if (l == "撤销上一步") {
              var k = c.getFlowv();
              if (k.length > 10) {
                var j = k.substr(0, k.length - 1);
                j = j.substr(0, j.lastIndexOf(";") + 1);
                c.setFlowv(j);
                c.resultdata.pop()
              }
            } else {
              var n = c.imagesdata[l];
              if (c.getFlowv()) {
                var k = l + ";";
                if (c.getFlowv().indexOf(k) < 0) {
                  c.setFlowv(c.getFlowv() + k)
                }
              } else {
                c.setFlowv("graph LR;" + l + ";")
              }
            }
          }
        }
        c.lastname = l;
        d3.select(".main" + c.mddname).style("display", "none")
      })
    } catch (d) {
      alert("业务对象数据格式错误！")
    } finally {}
  },
  updatesubmenu: function() {
    var b = this;
    var i = b.getBusinessobjects();
    var a = "";
    var f = "";
    var g = "<ul>";
    try {
      i.map(function(k, e) {
        a = k.name;
        f = k.boid;
        g += "<li class='menuItem' name=" + a + " doAction=" + f + " style='background: #FFF url(rwt-resources/mdflowdesign/bg.gif) repeat-x 0 2px;list-style:url(rwt-resources/mdflowdesign/arrow.gif); margin: 0; padding: 0;' > <a href='#' style='font: normal 12px Arial;  border-top: 1px solid #ccc;display: block;color: black;text-decoration: none;line-height:26px;padding-left:26px;;cursor:pointer; '><span>";
        g += a + "</span></a></li>"
      });
      a = "撤销";
      g += "<li class='menuItem' name=" + a + " doAction=" + f + " style='background: #FFF url(rwt-resources/mdflowdesign/bg.gif) repeat-x 0 2px;list-style:url(rwt-resources/mdflowdesign/arrow.gif);margin: 0; padding: 0;cursor:pointer;' > <a  style='font: normal 12px Arial;  border-top: 1px solid #ccc;display: block;color: black;text-decoration: none;line-height:26px;padding-left:26px;cursor:pointer; '><span>";
      g += a + "</span></a></li>";
      g += "</ul>";
      d3.select(".submenu" + b.mddname).style("visibility", "visible").html(g);
      var h = "";
      var j = 0;
      var d = 0;
      d3.select(b._element).select(".nodes").selectAll(".node").on("click", function() {
        d3.select(".main" + b.mddname).style("display", "none");
        var k = b.getShowstate();
        if (k == 1) {
          return
        }
        h = d3.select(this).attr("id");
        b.lastname = h;
        b.subvisible = 1;
        var m = d3.select(b._element).node();
        var e = d3.mouse(m);
        var n = e[0];
        if (n > 100) {
          n = n - 100
        }
        j = n;
        var o = e[1];
        d = o;
        var l = d3.select(".sub" + b.mddname);
        l.style("display", "none");
        l.style("position", "absolute").style("left", n + "px").style("top", o + "px").style("display", "inline-block")
      });
      d3.select("#cbwhjs").on("click", function(l, e) {
        var k = document.getElementById("cbwhjs");
        if (b.isjswh) {
          if (k.checked) {
            alert("已经存在维护角色!");
            k.checked = false
          }
        }
      });
      d3.select("#modalclose").on("click", function(k, e) {
        d3.select("#modal_wrapper").style("display", "none")
      });
      d3.select(".submenu" + b.mddname).selectAll(".menuItem").on("click", function(t, p) {
        var r = d3.select(this);
        var k = r.attr("name");
        var n = b.imagesdata[k];
        if (k == "撤销") {
          var u = b.resultdata.filter(function(x) {
            var v = x.to;
            var w = x.from;
            return (v !== h && w !== h)
          });
          b.resultdata = u;
          var m = "graph LR;";
          for (var p = 0; p < u.length; p++) {
            var o = u[p];
            m = m + o.from + "--" + o.des + "-->" + o.to + ";"
          }
          b.setFlowv(m)
        } else {
          if (b.getFlowv()) {
            var l = d3.select(b._element).node();
            var q = d3.mouse(l);
            var e = q[0];
            if (e > 100) {
              e = e - 100
            }
            var s = q[1] - 20;
            d3.select("#modal_wrapper").style("position", "absolute").style("left", j + "px").style("top", d + "px").style("display", "inline-block");
            document.getElementById("line_des").value = "";
            document.getElementById("cbwhjs").checked = false;
            d3.select("#modal_title").html("添加线路描述(" + k + ")");
            d3.select("#modalok").on("click", function(B, z) {
              var y = document.getElementById("line_des").value;
              if (y) {
				var w = document.getElementById("selectstate");
				if(w.selectedIndex == -1){
					console.log("请选择响应前状态!");
					alert("请选择响应前状态!");
					return;
				}
                var E = w.options[w.selectedIndex].value;
				var preStateStr = w.options[w.selectedIndex].text;
                var v = document.getElementById("selectstate1");
				if(v.selectedIndex == -1){
					console.log("请选择响应后状态!");
					alert("请选择响应后状态!");
					return;
				}
                var D = v.options[v.selectedIndex].value;
                var nextStateStr = v.options[v.selectedIndex].text;
                var x = h + "--" + y + "-->" + k + ";";
                if (b.getFlowv().indexOf(x) < 0) {
                  b.setFlowv(b.getFlowv() + x)
                }
               
                var A = document.getElementById("cbwhjs");
                var C = {};
                C.from = b.lastname;
                C.to = k;
                C.state = E;
                C.nextstatus = D;
                C.stateStr = preStateStr;
                C.nextstatusStr = nextStateStr;
                C.flag = 0;
                if (A.checked) {
                  C.flag = 1;
                  b.isjswh = true
                }
                C.des = y;
                b.resultdata.push(C);
                d3.select("#modal_wrapper").style("display", "none");
                b.lastname = k
              } else {
                console.log("请输入描述信息!");
                alert("请输入描述信息!")
              }
            })
          } else {
            b.setFlowv("graph LR;" + k + ";")
          }
        }
        b.subvisible = 0;
        d3.select(".sub" + b.mddname).style("display", "none")
      })
    } catch (c) {
      alert("业务对象数据格式错误！")
    } finally {}
  },
  updateImagelabel: function() {
    var c = this;
    var a = c.mddname;
    try {
      var b = d3.select("." + c.mddname).select(".nodes").selectAll(".node")[0];
      b.map(function(l, i) {
        var k = d3.select(l);
        var h = k.attr("id");
        var f = c.getImagesdata();
        var e = f[h].image1;
        var j = h;
        if (e == 0) {
          j = "default"
        }
        var g = "rwt-resources/mdflowdesign/" + j + ".png";
        d3.select("." + a).select("#" + h).selectAll("*").remove();
        d3.select("." + a).select("#" + h).append("svg:image").style("cursor", "pointer").attr("x", -30).attr("y", -20).attr("width", 60).attr("height", 40).attr("xlink:href", function(m) {
          return g
        });
        d3.select("." + a).select("#" + h).append("svg:text").attr("dy", "40").style("stroke-width", "1px").style("cursor", "default").style("stroke-linejoin", "round").style("font-family", "Arial").style("font-size", "9px").style("text-anchor", "middle").transition().duration(5000).ease("bounce").delay(function(n, m) {
          return 200 * m
        }).text(function(m) {
          return h
        })
      })
    } catch (d) {
      console.log(d);
      alert("初始化图片失败!")
    } finally {}
  },
  updatEdges: function() {
    try {
      var i = this.mddname;
      var b = mermaid.getEdges();
      d3.select("." + i).selectAll(".path").data(b).attr("id", function(e) {
        return e.start + "_" + e.end
      });
      d3.select("." + i).selectAll(".edgePath").style("cursor", "auto");
      d3.select("." + i).selectAll(".edgePaths").style("cursor", "auto");
      d3.select("." + i).selectAll(".edgeLabels").style("cursor", "auto");
      var h = this.resultdata;
      var j = d3.select(".vuserMenu" + i);
      var g = d3.select(".userinfo" + i);
      j.on("click", function() {
        j.style("display", "none")
      });
      var a = d3.select("." + i).node();
      var c = this;
      d3.select("." + i).selectAll(".node").on("mouseover", d);

      function d() {
        var p = this;
        var l = c.showstate;
        if (l == 0) {
          return
        }
        var q = d3.select(p);
        var t = q.attr("id");
        var n = d3.mouse(a);
        var e = n[0];
        var r = n[1] + 30;
        var s = "";
        var m = [];
        var v = "";
        var u = "";
        var k = "";
        var o = "";
        var m = h.filter(function(x) {
          var w = x.to;
          return (w == t)
        });
        if (m.length == 0) {
          return
        }
        m.map(function(x, w) {
          k = x.des;
          //v = x.state;
          //u = x.nextstatus;
          v = x.stateStr;
          u = x.nextstatusStr;
          sflag = x.flag;
          if (sflag == 1) {
            o = "维护角色"
          }
          if (sflag == "1") {
            o = "维护角色"
          }
          if (k) {
            s += "<div class='menuItem'  style='margin:3px; padding: 2px 2px 2px 2px;font-size:9pt;line-height:18px;background: #FFF url(rwt-resources/mdflowdesign/bg.gif);'>";
            s += k + "</div>"
          }
          if (o) {
            s += "<div class='menuItem'  style='margin:3px; padding: 2px 2px 2px 2px;font-size:9pt;line-height:18px;background: #FFF url(rwt-resources/mdflowdesign/bg.gif);color:#FF0000;'>";
            s += o + "</div>"
          }
          s += "<div class='menuItem'  style='margin:3px; padding: 2px 2px 2px 2px;cursor:pointer;font-size:9pt;line-height:18px;background: #FFF url(rwt-resources/mdflowdesign/bg.gif);color:#FF0000;' > <u>";
          s += "响应前：" + v + " 响应后：" + u + "</u></div>"
        });
        g.style("visibility", "visible").html(s);
        j.style("position", "absolute").style("left", e + "px").style("top", r + "px").style("display", "inline-block").on("mouseleave", function() {});
        d3.event.preventDefault()
      }
    } catch (f) {} finally {}
  },
  setFlowv: function(b) {
    this.flowv = b;
    var a = this;
    a._element.innerHTML = b;
    try {
      mermaid.init({
        noteMargin: 10
      }, "." + a.mddname);
      a._element.removeAttribute("data-processed");
      try {
        a.updateImagelabel()
      } catch (c) {
        alert("初始化image失败")
      } finally {}
      try {
        a.updatEdges()
      } catch (c) {
        alert("边错误")
      } finally {}
      d3.select("." + a.mddname).select(".edgeLabels").style("opacity", "0");
      a.updatesubmenu()
    } catch (c) {
      alert("内容数据格式错误！")
    } finally {}
  },
  initflow: function(b) {
    var a = this;
    this.flowv = b;
    a._element.innerHTML = b;
    console.log("initflow");
    try {
      mermaid.init({
        noteMargin: 10
      }, "." + a.mddname);
      d3.select("." + a.mddname).attr("data-processed", "");
      try {
        a.updateImagelabel()
      } catch (c) {
        alert("初始化image失败")
      } finally {}
      try {
        a.updatEdges()
      } catch (c) {
        alert("边错误")
      } finally {}
      d3.select("." + a.mddname).select(".edgeLabels").style("opacity", "0");
      a.updatesubmenu()
    } catch (c) {
      alert("内容数据格式错误！")
    } finally {}
  },
  getFlowv: function() {
    return this.flowv
  },
  setStatedata: function(b) {
    this.statedata = b;
    var d = this;
    var a = d3.select("#selectstate");
    var f = d3.select("#selectstate1");
    for (var c = 0; c < b.length; c++) {
      var e = b[c];
      a.append("option").attr("value", e.name1).text(e.name);
      f.append("option").attr("value", e.name1).text(e.name)
    }
  },
  getStatedata: function() {
    return this.statedata
  },
  setShowstate: function(b) {
    this.showstate = b;
    var a = this.mddname;
    d3.select(".vuserMenu" + a).style("display", "none")
  },
  setSaveresult: function(a) {
    this.saveresult = a;
    this.selectItem("result", this.resultdata)
  },
  getShowstate: function() {
    return this.showstate
  },
  setImagesdata: function(b) {
    this.imagesdata = b;
    var a = this
  },
  getImagesdata: function() {
    return this.imagesdata
  },
  setBusinessobjects: function(c) {
    this.businessobjects = c;
    var a = this;
    var b = a.mddname;
    a.createMenu(b);
    a.createSubMenu(b);
    a.createInfoMenu(b);
    a.updatemenu(b);
    a.setFlowv("graph LR;");
    d3.select("svg").attr("viewBox", "0 0 200 300").style("max-width", "300")
  },
  getBusinessobjects: function() {
    return this.businessobjects
  },
  handleCBClick: function() {},
  setResultdata: function(e) {
    this.resultdata = e;
    var a = e;
    var d = "graph LR;";
    for (var c = 0; c < a.length; c++) {
      var b = a[c];
      if (b.flag == "1") {
        this.isjswh = true
      }
      d = d + b.from + "--" + b.des + "-->" + b.to + ";"
    }
    this.initflow(d)
  },
  selectItem: function(a, c) {
    var b = rap.getRemoteObject(this);
    b.notify("Selection", {
      index: a,
      data: c
    })
  },
  destroy: function() {
    var a = this._element;
    if (a.parentNode) {
      a.parentNode.removeChild(a)
    }
  }
};
rap.registerTypeHandler("mermaidjs.MdFlowDesign", {
  factory: function(a) {
    var b = rap.getObject(a.parent);
    return new mermaidjs.MdFlowDesign(b)
  },
  destructor: "destroy",
  properties: ["businessobjects", "statedata", "flowv", "imagesdata", "resultdata", "showstate", "saveresult"],
  events: ["Selection"]
});
