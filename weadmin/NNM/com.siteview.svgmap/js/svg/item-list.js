/*******************************************************************************
 *
 *
 ******************************************************************************/

/*
 * An array of items with additional add() and remove() methods.
 */
d3svgmap.ItemList = function() {

  this.add = function( item ) {
    this.push( item );
  };

  this.remove = function( item ) {
    var index;
    while( ( index = this.indexOf( item ) ) !== -1 ) {
      this.splice( index, 1 );
    }
  };

};

d3svgmap.ItemList.prototype = Array.prototype;
