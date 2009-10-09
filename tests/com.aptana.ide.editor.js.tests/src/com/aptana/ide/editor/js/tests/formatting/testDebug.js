
///////////////////////////////////////////
// test basic stepping
function testStepping() {
	// 1: Follow the white rabbit 
	var x = 0, y = 13;
	while ( x < y ) {
		// 2: step into
		stepInto1(x,y);
		++x;
		y-=2;
	}	
}

function stepInto1(arg1, arg2) {
	var sum;
	dump("x="+arg1+",y="+arg2);
	sum = arg1+arg2;
	// 3: step into
	stepInto2(sum);
	// 4: step over/into
}
function stepInto2(sum) {
	dump("sum="+sum);
	// 5: step over
	stepOver1();
	// 6: step return
	dump("sum[again]="+sum);
}

function stepOver1() {
	dump("stepOver1 entered");
	stepOver2();
	dump("stepOver1 exited");
}
function stepOver2() {
	dump("stepOver2 executed");
}


///////////////////////////////////////////
// test breakpoints functionality
function testBreakpoints() {
	// Debugger will stop here by default
	debugger;
	// 1: set breakpoint 2 lines below on dump("breakpoint line");
	// 2: resume execution
	stepInto1();
	
	dump("breakpoint line");
	// 3: set conditional breakpoint with hit count = 5 at stepInto1(); in breakpointHit() function
	// 4: resume execution
	for( var i = 1; i < 10; ++i ) {
		breakpointHit(i);
	}
	dump("running to next line");
	dump("run to this line"); // Place cursor on this line
}

function breakpointHit(hit) {
	dump("hit count="+hit);
	stepInto1(); // Place breakpoint here
	// 5: check hit value equals to 5
	// 6 breakpoint will be disabled when hit count reached
	// 7: place cursor on dump("run to this line"); line in testBreakpoints() function
	// 8: Right-click on the line with your cursor and select "Run to line' in context menu
}


