///////////////////////////////////////////
// test anonymous functions name resolving
function testAnonymousFunctions() {
	try {
		obj.myFunc2();
	} catch(exc) {
	}
	debugger;
}

var myFunc0 = function() {
	showExceptionStackTrace();
}
var myFunc1
=
function()
{
	myFunc0();	
}
var obj = {};
obj.myFunc2 = function() {
	myFunc1();
}

///////////////////////////////////////////
// test javascript: URLs handling
function testJavascriptURLs() {
	aptana.trace("");
	debugger;
}

///////////////////////////////////////////
// test exceptions handling
function testExceptions() {
	try {
		throwError();
	} catch(exc) {
		aptana.trace(exc); // Error is not handled as error but as exception, trace it manually
	}
	try {
		throwString();
	} catch(exc) {
		aptana.trace(exc); // String is handled as exception, trace it manually
	}
	try {
		throwObject();
	} catch(exc) {
		aptana.trace(exc); // Object is handled as exception, trace it manually
	}
	try {
		throwSyntaxError();
	} catch(exc) {
		aptana.trace(exc); // SyntaxError is not handled as error but as exception, trace it manually
	}
	try {
		throwReferrenceError();
	} catch(exc) {
	}
	try {
		throwRangeError();
	} catch(exc) {
	}
	try {
		throwTypeError();
	} catch(exc) {
	}
}

function throwError() {
	throw new Error("My Error");
}

function throwString() {
	throw "My String";
}

function throwObject() {
	throw new Object();
}

function throwSyntaxError() {
	eval("if ( 2 > 1 ) { break; }");
}

function throwReferrenceError() {
	no_such_object.no_such_method();
}

function throwRangeError() {
	var a = new Array(0x100000000);
}

function throwTypeError() {
	var x = null;
	x.num = 5;
}