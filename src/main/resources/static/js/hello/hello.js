(function (factory) {
    if(typeof exports === 'object' && typeof module !== 'undefined'){
        module.exports = factory();
    }
    else if( typeof define === 'function' && define.amd){
        define(factory);
    }
    else if(window.layui && layui.define){
        layui.define('jquery', function (exports) {
            exports('hello', factory(layui.jquery));
        });
    }
    else{
        factory(jQuery);
    }
}((function ($) {

    var Hello = function (element, options) {
        this._element = element;
        this._options = options;
    };

    Hello.prototype = {
        init: function () {
            var element = this._element, options = this._options;

            console.log("hello world!");
            return this;
        },
        text: function () {
            var element = this._element;
            var option;
            if (!$.support.leadingWhitespace) {
                option = arguments.caller[1] || {}; //IE6-8特殊处理
            } else {
                option = arguments[0][0] || {};
            }

            var options = $.extend(this._options, option);

            console.log("hello!");
        }
    };

    $.fn.hello = function (option) {
        var defaults = {
            
        };

        var options = $.extend(true, {}, defaults, option);

        var key = 'hello', obj = this.data(key);

        if (typeof option === 'string') {
            if (option === 'getObject') {
                if (obj) {
                    return obj;
                } else {
                    throw new Error('This object is not available');
                }
            } else {
                if (obj && obj[option]) {
                    obj[option](Array.prototype.splice.call(arguments, 1));
                }
            }
            return this;
        }

        return this.each(function () {
            var hello = new Hello(this, options);

            hello.init();

            $(this).data(key, hello);
        });
    };

    return this;
})));