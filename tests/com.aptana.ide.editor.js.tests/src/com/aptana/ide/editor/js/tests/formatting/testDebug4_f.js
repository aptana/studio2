
///////////////////////////////////////////
// test variables/details functionality
function testVariables(){
    var vArray = new Array(4);
    vArray[0] = "item0";
    vArray[vArray.length - 1] = "itemN";
    
    var vBool = new Boolean();
    var vDate = new Date();
    var vError = new Error();
    var vNum = new Number(7);
    var vObj = new Object();
    
    var vObj2 = new Object();
    vObj2.toString = function(){
        return "Object toString() method is used here";
    };
    vObj2.str = "string";
    vObj2.num = 2;
    vObj2.date = vDate;
    
    function MyObject(str, num){
        this.str = str;
        this.num = num;
        this.date = new Date();
    }
    
    var vMyObj = new MyObject("some text", 5);
    
    function Shape(){
        this.type = "shape";
    }
    function Rect(x, y, w, h){
        this.type = "rect";
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
    }
    Rect.prototype = new Shape();
    
    var vRect = new Rect(0, 0, 1, 1);
    
    var vStr = new String("Hello, World!");
    
    debugger;
    // 1: Check variables values in IDE Variables view:
    // vArray = item0,,,itemN
    // vBool = false
    // vDate = e.g. 'Fri Oct 27 2006 00:43:08'
    // vError = Error
    // vNum = 7
    // vObj = [object Object]
    // vObj2 = Object toString() method is used here
    // vStr = "Hello, World!"

    // 2: Check values in details pane for object(s):
    // vDate: e.g. '00:43:08'
    // vObj2:
    //   object[toString]=function () {return "Object toString() method is used here";}
    //   object[str]=string
    //   object[num]=2
    //   object[date]=Fri Oct 27 2006 00:43:08	
}
