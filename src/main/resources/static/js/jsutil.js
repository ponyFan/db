/**

 @Name：JS扩展方法
 @Author：Ray
    
 */

(function (factory) {
    typeof exports === 'object' && typeof module !== 'undefined' ? module.exports = factory() :
        typeof define === 'function' && define.amd ? define(factory) :
        window.layui && layui.define ? (layui.define('jquery', function (exports) {
            exports('jutil', factory());
        })) :
        factory();
}((function () {

    //扩展 Array
    Array.prototype = {
        //验证一个元素是否存在 Array 中
        contains: function (e) {
            for (var i = 0; i < this.length; i++) {
                if (this[i] == e)
                    return true;
            }
            return false;
        },
        //验证一个元素在 Array 中的索引，存在返回索引值，不存在返回 -1。
        indexOf: function (val) {
            for (var i = 0; i < this.length; i++) {
                if (this[i] == val)
                    return i;
            }
            return -1;
        },
        //删除 Array 中的某一个元素。
        remove: function (val) {
            var index = this.indexOf(val);
            if (index > -1) {
                this.splice(index, 1);
            }
            return this;
        }
    };

    //扩展 String
    String.prototype = {
        //扩展startWith方法    
        startWith: function (str) {
            if (str == null || str == "" || this.length == 0 || str.length > this.length)
                return false;
            if (this.substr(0, str.length) == str)
                return true;
            else
                return false;
        },
        //扩展endWith方法
        endWith: function (suffix) {
            return this.indexOf(suffix, this.length - suffix.length) !== -1;
        },
        //扩展contains方法    
        contains: function (str, startIndex) {
            return -1 !== String.prototype.indexOf.call(this, str, startIndex);
        },
        //扩展Escape方法  
        Escape: function () {
            var str = this;
            return escape(str)
                .replace(/\*/g, '%2a')
                .replace(/\+/g, '%2b')
                .replace(/\-/g, '%2d')
                .replace(/\./g, '%2e')
                .replace(/\//g, '%2f')
                .replace(/\@/g, '%40')
                .replace(/\_/g, '%5f');
        }
    };

    //扩展 Number
    Number.prototype = {
        //扩展CNMoney方法
        CNMoney: function () {
            var fraction = ['角', '分'];
            var digit = [
                '零', '壹', '贰', '叁', '肆',
                '伍', '陆', '柒', '捌', '玖'
            ];
            var unit = [
                ['元', '万', '亿'],
                ['', '拾', '佰', '仟']
            ];
            var head = n < 0 ? '欠' : '';
            n = Math.abs(n);
            var s = '';
            for (var i = 0; i < fraction.length; i++) {
                s += (digit[Math.floor(n * 10 * Math.pow(10, i)) % 10] + fraction[i]).replace(/零./, '');
            }
            s = s || '整';
            n = Math.floor(n);
            for (var i = 0; i < unit[0].length && n > 0; i++) {
                var p = '';
                for (var j = 0; j < unit[1].length && n > 0; j++) {
                    p = digit[n % 10] + unit[1][j] + p;
                    n = Math.floor(n / 10);
                }
                s = p.replace(/(零.)*零$/, '').replace(/^$/, '零') + unit[0][i] + s;
            }
            return head + s.replace(/(零.)*零元/, '元')
                .replace(/(零.)+/g, '零')
                .replace(/^整$/, '零元整');
        }
    };


    var Utils = function () {

    };

    Utils.prototype = {
        typeof: function (value) {
            if (Object.prototype.toString.call(value) == "[object String]") {
                return "string";
            }
            if (Object.prototype.toString.call(value) == "[object Number]") {
                return "number";
            }
            if (Object.prototype.toString.call(value) == "[object Boolean]") {
                return "boolean";
            }
            if (Object.prototype.toString.call(value) == "[object Undefined]") {
                return "undefined";
            }
            if (Object.prototype.toString.call(value) == "[object Null]") {
                return "null";
            }
            if (Object.prototype.toString.call(value) == "[object Array]") {
                return "array";
            }
            if (Object.prototype.toString.call(value) == "[object Function]") {
                return "function";
            }
            if (Object.prototype.toString.call(value) == "[object Object]") {
                return "object";
            }
            if (Object.prototype.toString.call(value) == "[object RegExp]") {
                return "regexp";
            }
            if (Object.prototype.toString.call(value) == "[object Date]") {
                return "date";
            }
        },
        // 是否是字符串
        isString: function (value) {
            return Object.prototype.toString.call(value) == "[object String]";
        },
        // 是否是数字
        isNumber: function (value) {
            return Object.prototype.toString.call(value) == "[object Number]";
        },
        // 是否是布尔值
        isBoolean: function (value) {
            return Object.prototype.toString.call(value) == "[object Boolean]";
        },
        // 是否undefined
        isUndefined: function (value) {
            return Object.prototype.toString.call(value) == "[object Undefined]";
        },
        // 是否是null
        isNull: function (value) {
            return Object.prototype.toString.call(value) == "[object Null]";
        },
        // 是否数组
        isArray: function (value) {
            return Object.prototype.toString.call(value) == "[object Array]";
        },
        // 是否是函数
        isFunction: function (value) {
            return Object.prototype.toString.call(value) == "[object Function]";
        },
        // 是否是对象
        isObject: function (value) {
            return Object.prototype.toString.call(value) == "[object Object]";
        },
        // 是否是正则表达式
        isRegExp: function (value) {
            return Object.prototype.toString.call(value) == "[object RegExp]";
        },
        // 是否是日期对象
        isDate: function (value) {
            return Object.prototype.toString.call(value) == "[object Date]";
        },
        json: function (json) {
            for (var o in json) {
                if (json.hasOwnProperty(o)) {
                    var v = json[o];
                    if (v == undefined || $.trim(v) == "" || v == null) {
                        delete(json[o]);
                    }
                }
            }
            return json;
        },
        isEmpty: function (json) {
            var flag = true;
            for (var o in json) {
                if (json.hasOwnProperty(o)) {
                    var v = json[o];
                    if (v != undefined && v != "" && v != null) {
                        flag = false;
                    }
                }
            }
            return flag;
        },
        empty: function (json) {
            for (var o in json) {
                if (json.hasOwnProperty(o)) {
                    json[o] = "";
                }
            }
            return json;
        }
    };

    return new Utils();

})));