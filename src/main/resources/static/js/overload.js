/**

 @Name：JS扩展方法
 @Author：Ray
    
 */

(function (factory) {
    typeof exports === 'object' && typeof module !== 'undefined' ? module.exports = factory() :
        typeof define === 'function' && define.amd ? define(factory) :
        window.layui && layui.define ? (layui.define('jutil', function (exports) {
            exports('overload', factory(layui.jutil));
        })) :
        factory();
}((function (jutil) {

    var types = {};
    types["string"] = "a";
    types["boolean"] = "b";
    types["array"] = "c";
    types["object"] = "d";
    types["function"] = "e";
    var Overload = function () {};

    Overload.prototype = {
        add: function (object, name, param, fn) {
            var obj = object.prototype;
            var params = param.split(",");

            var p = "";
            for (var i = 0; i < params.length; i++) {
                var _p = params[i];
                if (i == 0) {
                    p += types[_p];
                } else {
                    p += "," + types[_p];
                }
            }
            param = p.replace(/,/g, '_');
            obj[name + '_' + param] = fn;
            obj[name] = function () {
                var ptype = "";
                for (var i = 0; i < arguments.length; i++) {
                    var arg = arguments[i];
                    var t = jutil.typeof(arg);
                    ptype += types[t] + "_";
                }
                ptype = ptype.substring(0, ptype.lastIndexOf('_'));
                if (obj[name + '_' + ptype] != undefined) {
                    return obj[name + '_' + ptype].apply(this, arguments);
                }
            }
        }
    };

    return new Overload();

})));