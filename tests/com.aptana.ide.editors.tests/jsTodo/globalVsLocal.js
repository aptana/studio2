/** global x */
var x = 5;
/** global y */
var y = 1;
 /* #0 */
/**
 * 
 * @param {Object} y arg y
 */
function foo(y)
{
  y = 2; // arg
  x = 6; // global
   /* #1 */
  var x = "test";
  x = "test2"; // local
   /* #2 */
}

