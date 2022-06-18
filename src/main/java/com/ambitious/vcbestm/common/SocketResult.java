package com.ambitious.vcbestm.common;

import com.ambitious.vcbestm.exception.ErrCode;
import com.google.gson.Gson;
import lombok.Data;

/**
 * 用于 WebSocket 返回 JSON 数据
 * @author Ambitious
 * @date 2022/6/14 20:01
 */
@Data
public class SocketResult {

    /**
     * 响应码
     */
    private Integer code;
    /**
     * 响应信息
     */
    private String msg;
    /**
     * 响应事件
     */
    private String event;
    /**
     * 响应数据
     */
    private Object data;

    private SocketResult() {}

    public static SocketResult ok(String event) {
        return ok(event, null);
    }

    public static SocketResult ok(String event, Object data) {
        SocketResult res = new SocketResult();
        res.setCode(ErrCode.SUCCESS.getCode());
        res.setMsg(ErrCode.SUCCESS.getMsg());
        res.setEvent(event);
        res.setData(data);
        return res;
    }

    public static SocketResult fail(String event, ErrCode errCode) {
        SocketResult res = new SocketResult();
        res.setCode(errCode.getCode());
        res.setMsg(errCode.getMsg());
        res.setEvent(event);
        return res;
    }

    public String json() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
