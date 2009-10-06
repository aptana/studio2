

/**
 * Description for test0.
 * @param {Number} arg0 Description for arg0.
 * @param {RegExp} arg1 Description for arg1.
 * @return {Number};
 */
function test0(arg0, arg1)
{
	this.arg0 = arg0;
	this.arg1 = arg1;
	return 5;
}

test0.prototype.numProp = 5;
test0.prototype.stringProp = "hello";
test0.prototype.arrayProp = [3,4,5];
test0.prototype.pointProp = {x:1, y:2};

var test0Inst = new test0();

/**
 * @id idFunction
 */
function idFunction()
{
	return [];
}
var idFunctionCall = idFunction();