package com.power.util;

import java.io.Serializable;


/**
 * @author ---
 */
public class Result implements Serializable {
	private static final long serialVersionUID = -3948389268046368059L;

    private Integer code;

    private String msg;

    private Object data;


    public Result() {}

    public Result(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /**
     *  成功函数，不带数据，只返回成功的消息
     */
    public static Result success() {
        Result result = new Result();
        result.setResultCode(com.power.util.ResultCode.SUCCESS);
        return result;
    }

    /**
     * 成功函数，返回数据和消息状态
     */
    public static Result success(Object data) {
        Result result = new Result();
        result.setResultCode(com.power.util.ResultCode.SUCCESS);
        result.setData(data);
        return result;
    }

    /**
     * 失败函数，带消息返回
     * @param resultCode 状态码枚举类
     * @return result
     */
    public static Result failure(com.power.util.ResultCode resultCode) {
        Result result = new Result();
        result.setResultCode(resultCode);
        return result;
    }

    /**
     * 失败函数，带消息和数据返回
     * @param resultCode 状态码枚举类
     * @param data 数据
     * @return result
     */
    public static Result failure(com.power.util.ResultCode resultCode, Object data) {
        Result result = new Result();
        result.setResultCode(resultCode);
        result.setData(data);
        return result;
    }

    private void setResultCode(com.power.util.ResultCode code) {
        this.code = code.code();
        this.msg = code.message();
    }

    //setter and getter

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
}
