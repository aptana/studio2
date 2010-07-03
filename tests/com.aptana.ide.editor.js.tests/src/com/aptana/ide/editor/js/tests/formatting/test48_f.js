function jxlibTurnStrictOff(){
    include(jxlibPrefs);
    // turn off dump
    var pref = new Prefs();
    const prefStr = "javascript.options.strict";
    // turn dump off if enabled
    if (pref.getBool(prefStr)) {
        pref.setBool(prefStr, false);
        pref.save();
    }
    return;
}

z = new ss;
