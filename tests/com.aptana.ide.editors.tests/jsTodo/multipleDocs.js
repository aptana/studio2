/** doc1 */
var x = true;
x /* #0 */;
/** doc2 */
x = false;
x /* #1 */;
/** doc3 */
var y = x;
y /* #2 */;
function foo(arg)
{
  /** doc4 */
  y = arg;
  y /* #3 */;
}
// these docs all point to the same object
// there are various ways docs can be assigned multiple times like this
// this is esp tricky with namespaces, where conditional reassignments are common





/** doc1 */
var x = 5;
/** doc2 */
var y = 6;
var z = (test) ? x : y;
z /* #4 */;