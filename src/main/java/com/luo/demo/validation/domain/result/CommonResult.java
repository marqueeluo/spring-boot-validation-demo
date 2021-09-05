package com.luo.demo.validation.domain.result;

import com.luo.demo.validation.enums.RespCodeEnum;

import java.util.List;

/**
 * 通用返回结果
 *
 * @author luohq
 * @date 2021-09-04 13:47
 */
public class CommonResult<T> {
    private Integer respCode;
    private T data;
    private List<T> rows;
    private String msg;
    private Integer total;


    public CommonResult() {
    }

    public CommonResult(Integer respCode) {
        this.respCode = respCode;
    }

    public CommonResult(Integer respCode, T data) {
        this(respCode);
        this.data = data;
    }

    public CommonResult(Integer respCode, T data, String msg) {
        this(respCode, data);
        this.msg = msg;
    }

    public CommonResult(Integer respCode, List<T> rows, Integer total) {
        this(respCode);
        this.rows = rows;
        this.total = total;
    }


    public static CommonResult success() {
        return new CommonResult(RespCodeEnum.SUCCESS.getCode());
    }

    public static CommonResult success(String msg) {
        return new CommonResult(RespCodeEnum.SUCCESS.getCode(), null, msg);
    }

    public static <T> CommonResult successData(T data) {
        return new CommonResult(RespCodeEnum.SUCCESS.getCode(), data);
    }

    public static <T> CommonResult successRows(List<T> rows, Integer total) {
        return new CommonResult(RespCodeEnum.SUCCESS.getCode(), rows, total);
    }

    public static CommonResult failed(String msg) {
        return new CommonResult(RespCodeEnum.FAILED.getCode(), null, msg);
    }

    public static CommonResult failed() {
        return failed(null);
    }

    public static CommonResult respWith(Integer respCode, String msg) {
        return new CommonResult(respCode, null, msg);
    }

    public static CommonResult respWith(Integer respCode) {
        return respWith(respCode, null);
    }

    public Integer getRespCode() {
        return respCode;
    }

    public void setRespCode(Integer respCode) {
        this.respCode = respCode;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public List<T> getRows() {
        return rows;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "CommonResult{" +
                "respCode=" + respCode +
                ", data=" + data +
                ", rows=" + rows +
                ", msg='" + msg + '\'' +
                ", total=" + total +
                '}';
    }
}
