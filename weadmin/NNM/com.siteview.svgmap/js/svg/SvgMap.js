/*******************************************
*date: 2014/3/20
*auther: bin.liu
*svg to rap
********************************************/

d3svgmap= {};
d3svgmap.SvgMap = function( parent, renderer ) {
  this._renderer = renderer;
  this._element = this.createElement( parent );
  this.createMenu( parent );
  this._padding = 20;
  this._svg = d3.select( this._element ).append( "svg" ).attr( "class", "SvgMap" )
  .attr("xmlns","http://www.w3.org/2000/svg" );
  //.attr("viewBox","0 0 595.276 841.89");
  //this._svg = d3.select( parent ).append( "svg" ).attr( "class", "SvgMap" );
  this._needsLayout = true;
  var that = this;
  rap.on( "render", function() {
    if( that._needsRender ) {
      if( that._needsLayout ) {
        that._renderer.initialize( that );
        that._needsLayout = false;
      }
      that._renderer.render( that );
      that._needsRender = false;
    }
  } );
  parent.addListener( "Resize", function() {
    that._resize( parent.getClientArea() );
  } );
  this._resize( parent.getClientArea() );
};

d3svgmap.SvgMap.prototype = {

  createElement: function( parent ) {
    var element = document.createElement( "div" );
    element.style.position = "absolute";
    element.style.left = "0";
    element.style.top = "0";
    //element.style.width = "100%";
    //element.style.height = "100%";
	element.style.overflow="auto";
	element.style.backgroundColor ="#7f7f7f";
	//element.style.overflowy= "auto" ;
	//element.style.overflowx= "auto" ;
    parent.append( element );
    return element;
  },
  createMenu:function( parent ){
     var element = document.createElement( "div" );
	 element.setAttribute("id","panelmenu");
	 element.setAttribute("class","menu");
	 element.style.display="none";
	 parent.append( element );
	 
  },

  getLayer: function( name ) {
    var layer = this._svg.select( "g." + name );
    if( layer.empty() ) {
      this._svg.append( "svg:g" ).attr( "class", name );
      layer = this._svg.select( "g." + name );
    }
    return layer;
  },

  _resize: function( clientArea ) {
    //alert("_resize");
    this._svg.attr( "width",  clientArea[ 2 ] ).attr( "height", clientArea[ 3 ] );
    this._scheduleUpdate( true );
  },

  _scheduleUpdate: function( needsLayout ) {
  //alert(" _scheduleUpdate");
    if( needsLayout ) {
      this._needsLayout = true;
    }
    this._needsRender = true;
  },

  destroy: function() {
    var element = this._element;
    if( element.parentNode ) {
      element.parentNode.removeChild( element );
    }
  }

};