package com.coolfish.websocket.netty.websocket.util;

import cn.hutool.core.util.StrUtil;

import java.util.*;

/**
 * @className: RequestUriUtils
 * @description: TODO 类描述
 * @author: xufh
 * @date: 2022/9/9
 */
public class RequestUriUtils {

    /**
     * 这种解析方式是以?key=value入参
     * 将路径参数转换成Map对象，如果路径参数出现重复参数名，将以最后的参数值为准
     *
     * @param uri 传入的携带参数的路径
     * @return
     */
    public static Map<String, String> getParams(String uri) {
        Map<String, String> params = new HashMap<>(10);
        int idx = uri.indexOf("?");
        if (idx != -1) {
            String[] paramsArr = uri.substring(idx + 1).split("&");

            for (String param : paramsArr) {
                idx = param.indexOf("=");
                params.put(param.substring(0, idx), param.substring(idx + 1));
            }
        }
        return params;
    }

    /**
     * 这种解析方式是以path入参
     * 将路径参数转换成List对象
     *
     * @param uri 传入的携带参数的路径
     * @return
     */
    public static List<String> getPathParams(String uri) {
        List<String> list = new LinkedList<>();
        String[] split = uri.split("/ws/");
        if (split.length > 1) {
            list.addAll(Arrays.asList(split[1].split("/")));
        }
        return list;
    }

    /**
     * 获取URI中参数以外部分路径
     *
     * @param uri
     * @return
     */
    public static String getBasePath(String uri) {
        if (StrUtil.isBlank(uri)) {
            return null;
        }
        int idx = uri.indexOf("?");
        if (idx == -1) {
            return uri;
        }
        return uri.substring(0, idx);
    }

    public static String getPathParamBasePath(String uri) {
        if (StrUtil.isBlank(uri)) {
            return null;
        }
        int index = uri.indexOf("ws");
        if (index == -1) {
            return null;
        }
        return uri.substring(0, index);
    }

}

