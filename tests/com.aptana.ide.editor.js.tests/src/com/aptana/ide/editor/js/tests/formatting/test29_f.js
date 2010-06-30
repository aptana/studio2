$(document).ready(function(){
    //Code for example A
    $("input.buttonAsize").click(function(){
        alert($("div.contentToChange").find("p").size());
    });
    //show code example A
    $("a.codeButtonA").click(function(){
        $("pre.codeA").toggle()
    });
});
