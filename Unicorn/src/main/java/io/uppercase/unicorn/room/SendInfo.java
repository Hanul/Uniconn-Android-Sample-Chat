package io.uppercase.unicorn.room;

import io.uppercase.unicorn.handler.MethodHandler;

public class SendInfo {

    private String methodName;
    private Object data;
    private MethodHandler methodHandler;

    public SendInfo(String methodName, Object data, MethodHandler methodHandler) {
        this.methodName = methodName;
        this.data = data;
        this.methodHandler = methodHandler;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public MethodHandler getMethodHandler() {
        return methodHandler;
    }

    public void setMethodHandler(MethodHandler methodHandler) {
        this.methodHandler = methodHandler;
    }
}
