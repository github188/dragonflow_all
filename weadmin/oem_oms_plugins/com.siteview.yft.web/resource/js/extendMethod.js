/**
  createby zxn
  扩展工具方法
*/
$.extend({
  leftTrim:function(str){
    return str.replace(/^\s+/,'');
  },
  //显示提示框
  showPublicDialog:function(title,content) {
      setTimeout(function(){
        document.getElementById('publicMessageModelTitle').innerHTML = title;
        document.getElementById('publicMessageModalcontent').innerHTML = content;
        $('#publicMessageModal').modal('show');
      },100);
  },
  //设置页面高度
  panelHeight:function(panel,h){
    var height = $(window).height();
    height = height-h;
    $(panel).css("height",height+"px");
  }
})
