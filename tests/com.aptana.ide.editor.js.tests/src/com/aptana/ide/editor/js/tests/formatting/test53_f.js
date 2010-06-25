// Dojo configuration and Variable Initialization
// Put this code in index.php before the line where you include the javascript for dojo
// djConfig = { isDebug: true };
dojo.require("dojo.io.*");
dojo.require("dojo.io.IframeIO");
ctr = 0;
function upload_file_submit(){
    var bindArgs = {
        formNode: document.getElementById("upload_file"), //form's id
        mimetype: "text/plain", //Enter file type info here
        content: {
            increment: ctr++,
            name: "select_file", //file name in the form
            post_field: "" // add more fields here .. field will be accessible by $_POST["post_field"]
        },
        handler: function(type, data, evt){
            //handle successful response here
            if (type == "error") 
                alert("Error occurred.");
            else {
                //getting error message from PHP's file upload script
                res = dojo.byId("dojoIoIframe").contentWindow.document.getElementById("output").innerHTML;
                //Incase of an error, display the error message
                if (res != "true") 
                    alert(res);
                else 
                    alert("File uploaded successfully.");
            }
        }
    };
    var request = dojo.io.bind(bindArgs);
}
