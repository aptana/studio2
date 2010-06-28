///////////////////////////////////////////
// test XMLHttpRequest spying
function testXHR(){
    var xmlhttp = new XMLHttpRequest();
    xmlhttp.open("GET", "debug_tests.html", true);
    xmlhttp.onreadystatechange = function(){
        if (xmlhttp.readyState == 4) {
            dump("Response text: " + xmlhttp.responseText);
        }
    }
    try {
        xmlhttp.send("Hello, Ajax!");
    } 
    catch (exc) {
        dump("XMLHttpRequest exception: " + exc);
    }
}


///////////////////////////////////////////
// test asserts functionality
function testAsserts(){
    try {
        aptana.fail("#1 Use aptana.fail(this message,arg1,arg2,...)", "arg1", "arg2");
        aptana.assert(null, "#2 Use aptana.assert(object,arguments)", "arg1", "arg2");
        aptana.assertEquals(1, 2, "#3 Use aptana.assertEquals(o1,o2,arguments)", "arg1", "arg2");
        aptana.assertNotEquals(1, 1, "#4 Use aptana.assertNotEquals(o1,o2,arguments)", "arg1", "arg2");
        aptana.assertGreater(1, 2, "#5 Use aptana.assertGreater(o1,o2,arguments)", "arg1", "arg2");
        aptana.assertNotGreater(1, 2, "#6 Use aptana.assertNotGreater(o1,o2,arguments)", "arg1", "arg2");
        aptana.assertLess(2, 1, "#7 Use aptana.assertLess(o1,o2,arguments)", "arg1", "arg2");
        aptana.assertNotLess(2, 1, "#8 Use aptana.assertNotLess(o1,o2,arguments)", "arg1", "arg2");
        aptana.assertContains("x", window, "#9 Use aptana.assertContains(o1,o2,arguments)", "arg1", "arg2");
        aptana.assertNotContains("document", window, "#10 Use aptana.assertNotContains(o1,o2,arguments)", "arg1", "arg2");
        aptana.assertTrue(false, "#11 Use aptana.assertTrue(object,arguments)", "arg1", "arg2");
        aptana.assertFalse(true, "#12 Use aptana.assertFalse(object,arguments)", "arg1", "arg2");
        aptana.assertNull(window, "#13 Use aptana.assertNull(object,arguments)", "arg1", "arg2");
        aptana.assertNotNull(null, "#14 Use aptana.assertNotNull(object,arguments)", "arg1", "arg2");
        aptana.assertUndefined(window, "#15 Use aptana.assertUndefined(object,arguments)", "arg1", "arg2");
        var x;
        aptana.assertNotUndefined(x, "#16 Use aptana.assertNotUndefined(object,arguments)", "arg1", "arg2");
        aptana.assertInstanceOf("str", Date, "#17 Use aptana.assertInstanceOf(o1,o2,arguments)", "arg1", "arg2");
        aptana.assertNotInstanceOf(new Date(), Date, "#18 Use aptana.assertNotInstanceOf(o1,o2,arguments)", "arg1", "arg2");
        aptana.assertTypeOf(1, "string", "#19 Use aptana.assertTypeOf(o1,o2,arguments)", "arg1", "arg2");
        aptana.assertNotTypeOf(1, "number", "#20 Use aptana.assertNotTypeOf(o1,o2,arguments)", "arg1", "arg2");
        
    } 
    catch (exc) {
        alert("aptana object wasn't found. Opened in another browser than Firefox ?\n" + exc);
    }
}


///////////////////////////////////////////
// test debug in related windows (popups)
function testPopup(){
    var popupWindow = window.open("debug_timer.html", "popup", "location=0,status=0,scrollbars=0,menubar=0,width=200,height=200");
}


///////////////////////////////////////////
// test Firebug console logging functionality
function testFirebugConsole(){
    try {
        console.log("Use console.log('message')");
        console.debug("Use console.debug('message')");
        console.info("Use console.info('message')");
        console.warn("Use console.warn('message')");
        console.error("Use console.error('message')");
        try {
            console.assert(null, "Use console.assert(null,'message')");
        } 
        catch (exc) {
            aptana.trace(exc);
        }
        console.trace();
    } 
    catch (exc) {
        dump("console object wasn't found. Opened in another browser than Firefox ?");
    }
    debugger;
    // 1: Check messages in IDE console. Press green "resume" arrow to continue
}

///////////////////////////////////////////
// simple timer test
var timerID = 0;
var timerStartTime = null;

function timerStart(){
    timerStartTime = new Date();
    document.getElementById("timer").value = "00:00";
    timerID = setTimeout("timerUpdate()", 1000);
}

function timerStop(){
    if (timerID) {
        clearTimeout(timerID);
        timerID = 0;
    }
    timerStartTime = null;
}

function timerUpdate(){
    if (timerID) {
        clearTimeout(timerID);
        timerID = 0;
    }
    var d = new Date();
    var diff = d.getTime() - timerStartTime.getTime();
    d.setTime(diff);
    var timeString = "" + d.getMinutes() + ":" + d.getSeconds();
    document.getElementById("timer").value = timeString;
    dump(timeString);
    timerID = setTimeout("timerUpdate()", 1000);
}
