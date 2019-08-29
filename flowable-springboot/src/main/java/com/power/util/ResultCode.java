package com.power.util;

import java.util.ArrayList;
import java.util.List;
/**
 * @ClassName:  ResultCode
 * @Description:API 统一返回状态码
 * @author: zhongzk 28582157@qq.com
 * @date:   2018年9月26日 下午9:06:53
 *
 * @Copyright: 2018 字节码团队www.bjsurong.com. All rights reserved.
 *
 */

public enum ResultCode {
	 /* 成功状态码 */
    SUCCESS(200, "成功"),
    TASK_TYPE_MULTIPLE_INSTANCES(2001,"该任务是多实例任务"),

    /* 参数错误：10001-19999 */
    PARAM_IS_INVALID(10001, "参数无效"),
    PARAM_IS_BLANK(10002, "参数为空"),
    PARAM_TYPE_BIND_ERROR(10003, "参数类型错误"),
    PARAM_NOT_COMPLETE(10004, "参数缺失"),

    /* 用户错误：20001-29999*/
    USER_NOT_LOGGED_IN(20001, "用户未登录"),
    USER_LOGIN_ERROR(20002, "账号不存在或密码错误"),
    USER_ACCOUNT_FORBIDDEN(20003, "账号已被禁用"),
    USER_NOT_EXIST(20004, "用户不存在"),
    USER_HAS_EXISTED(20005, "用户已存在"),

    /* 业务错误：30001-39999 */
    SPECIFIED_QUESTIONED_USER_NOT_EXIST(30001, "某业务出现问题"),
    PROCESS_IS_SUSPENDED(30002,"流程已经被挂起"),
    PROCESS_IS_ACTIVATED(30003,"流程已经被激活"),
    PROCESS_STATUS_EXCEPTION(30004,"流程状态异常"),

    /* 系统错误：40001-49999 */
    SYSTEM_INNER_ERROR(40001, "系统繁忙，请稍后重试"),

    /* 数据错误：50001-599999 */
    RESULT_DATA_NONE(50001, "数据未找到"),
    DATA_IS_WRONG(50002, "数据有误"),
    DATA_ALREADY_EXISTED(50003, "数据已存在"),
    TASKS_IS_NULL(50004,"当前用户没有任务存在"),
    MODEL_IS_EMPTY(50005,"模型为空，请重新布置后再执行"),
    MODEL_DATA_WRONG_WARNING(50005,"数据模型不符要求，请至少设计一条主线流程。"),
    ENCODING_NOT_SUPPORT(50005,"不支持的编码格式"),

    /* 接口错误：60001-69999 */
    INTERFACE_INNER_INVOKE_ERROR(60001, "内部系统接口调用异常"),
    INTERFACE_OUTER_INVOKE_ERROR(60002, "外部系统接口调用异常"),
    INTERFACE_FORBID_VISIT(60003, "该接口禁止访问"),
    INTERFACE_ADDRESS_INVALID(60004, "接口地址无效"),
    INTERFACE_REQUEST_TIMEOUT(60005, "接口请求超时"),
    INTERFACE_EXCEED_LOAD(60006, "接口负载过高"),

    /* 权限错误：70001-79999 */
    PERMISSION_NO_ACCESS(70001, "无访问权限"),

    /*未定义错误类型，记录下错误位置*/
    CMD_ERROR_MESSAGE(80001,"CMD错误"),


    LASE_CODE(9999999,"最底部状态码");

    private Integer code;

    private String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer code() {
        return this.code;
    }

    public String message() {
        return this.message;
    }

    public static String getMessage(String name) {
        for (ResultCode item : ResultCode.values()) {
            if (item.name().equals(name)) {
                return item.message;
            }
        }
        return name;
    }

    public static Integer getCode(String name) {
        for (ResultCode item : ResultCode.values()) {
            if (item.name().equals(name)) {
                return item.code;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.name();
    }

    /**
     * 校验重复的code值
     * @param args 参数
     */
    public static void main(String[] args) {
        ResultCode[] apiResultCodes = ResultCode.values();
        List<Integer> codeList = new ArrayList<Integer>();
        for (ResultCode apiResultCode : apiResultCodes) {
            if (codeList.contains(apiResultCode.code)) {
                System.out.println(apiResultCode.code);
            } else {
                codeList.add(apiResultCode.code());
            }
        }
    }
}
