package com.vaca.androidautorestart

import android.text.TextUtils
import android.util.Log

class LogUtils {

    companion object {

        /**
         * Log输出的控制开关【发布前设置为false】
         */
        var isShowLog = true

        /**
         * 开发者自己定义
         */
        const val selfFlag = "Hyman▇▆▅▄▃▂▁："

        fun i(objTag: Any, msg: String) {
            if (!isShowLog) {
                return
            }
            var tag: String
            // 如果objTag是String，则直接使用
            // 如果objTag不是String，则使用它的类名
            // 如果在匿名内部类，写this的话是识别不了该类，所以获取当前对象全类名来分隔
            when (objTag) {
                is String -> tag = objTag
                is Class<*> -> tag = objTag.simpleName
                else -> {
                    tag = objTag.javaClass.name
                    val split = tag.split("\\.".toRegex()).toTypedArray()
                    tag = split[split.size - 1].split("\\$".toRegex()).toTypedArray()[0]
                }
            }
            if (TextUtils.isEmpty(msg)) {
                Log.i(selfFlag + tag, "该log输出信息为空")
            } else {
                Log.i(selfFlag + tag, msg)
            }
        }

        /**
         * 错误调试信息
         *
         * @param objTag
         * @param msg
         */
        fun e(objTag: Any, msg: String) {
            if (!isShowLog) {
                return
            }
            var tag: String
            when (objTag) {
                is String -> tag = objTag
                is Class<*> -> tag = objTag.simpleName
                else -> {
                    tag = objTag.javaClass.name
                    val split = tag.split("\\.".toRegex()).toTypedArray()
                    tag = split[split.size - 1].split("\\$".toRegex()).toTypedArray()[0]
                }
            }
            if (TextUtils.isEmpty(msg)) {
                Log.e(selfFlag + tag, "该log输出信息为空")
            } else {
                Log.e(selfFlag + tag, msg)
            }
        }

        /**
         * 详细输出调试
         *
         * @param objTag
         * @param msg
         */
        fun v(objTag: Any, msg: String) {
            if (!isShowLog) {
                return
            }
            var tag: String
            when (objTag) {
                is String -> tag = objTag
                is Class<*> -> tag = objTag.simpleName
                else -> {
                    tag = objTag.javaClass.name
                    val split = tag.split("\\.".toRegex()).toTypedArray()
                    tag = split[split.size - 1].split("\\$".toRegex()).toTypedArray()[0]
                }
            }
            if (TextUtils.isEmpty(msg)) {
                Log.v(selfFlag + tag, "该log输出信息为空")
            } else {
                Log.v(selfFlag + tag, msg)
            }
        }

        /**
         * 警告的调试信息
         *
         * @param objTag
         * @param msg
         */
        fun w(objTag: Any, msg: String) {
            if (!isShowLog) {
                return
            }
            var tag: String
            when (objTag) {
                is String -> tag = objTag
                is Class<*> -> tag = objTag.simpleName
                else -> {
                    tag = objTag.javaClass.name
                    val split = tag.split("\\.".toRegex()).toTypedArray()
                    tag = split[split.size - 1].split("\\$".toRegex()).toTypedArray()[0]
                }
            }
            if (TextUtils.isEmpty(msg)) {
                Log.w(selfFlag + tag, "该log输出信息为空")
            } else {
                Log.w(selfFlag + tag, msg)
            }
        }

        /**
         * debug输出调试
         *
         * @param objTag
         * @param msg
         */
        fun d(objTag: Any, msg: String) {
            if (!isShowLog) {
                return
            }
            var tag: String
            when (objTag) {
                is String -> tag = objTag
                is Class<*> -> tag = objTag.simpleName
                else -> {
                    tag = objTag.javaClass.name
                    val split = tag.split("\\.".toRegex()).toTypedArray()
                    tag = split[split.size - 1].split("\\$".toRegex()).toTypedArray()[0]
                }
            }
            if (TextUtils.isEmpty(msg)) {
                Log.d(selfFlag + tag, "该log输出信息为空")
            } else {
                Log.d(selfFlag + tag, msg)
            }
        }

//        fun ee(msg: String) {
//            ee("MyLog", msg)
//        }
//
//        fun ee(tagName: String, msg: String) {
//            if (isShowLog) {
//                val strLength = msg.length
//                var start = 0
//                var end = 3000
//                for (i in 0..99) {
//                    if (strLength > end) {
//                        Log.e(tagName + i, msg.substring(start, end))
//                        start = end
//                        end += 3000
//                    } else {
//                        Log.e(tagName + i, msg.substring(start, strLength))
//                        break
//                    }
//                }
//            }
//        }
    }

}