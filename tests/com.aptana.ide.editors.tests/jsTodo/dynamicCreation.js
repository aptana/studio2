var obj = {};
obj.foo = function (){};
obj.foo.prototype.x = 5;

var a = new obj["foo"](); // key line
a. /* #0 */;
 


var obj = {};
var index = 1;
obj.x0 = 5;
obj.x1 = 5;
obj.x2 = 5;
var z = obj["x" + index];
z. /* #1 */;
 
 
 