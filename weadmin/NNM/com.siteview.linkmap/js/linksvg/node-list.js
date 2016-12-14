/*******************************************************************************
 *
 *
 ******************************************************************************/

/*
 * An array of items with additional add() and remove() methods.
 */
linkmap.NodeList = function () {

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

linkmap.NodeList.prototype = Array.prototype;
