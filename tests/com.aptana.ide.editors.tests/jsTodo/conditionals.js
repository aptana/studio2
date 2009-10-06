// file 1
/** doc for ns */
var ns = {};
ns.x = 5;
ns. /* #0 */;
// file 2
if(ns == null)
{
  var ns = {};
}
ns.y = 6;
ns. /* #1 */;
// in this case ns should have both x and y
// problem is, compare with the last case, and now...

if(true)
{
  var ns = {};
}
ns.z = 7;
ns. /* #2 */;
// now ns should only have a 'z' property