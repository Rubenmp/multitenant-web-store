package com.mws.backend.framework.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class WebResult<DataClass extends Serializable> {
    private WebResultCode code;
    private String message;
    private DataClass data;

/*
    public static WebResult success(final String message) {
        WebResult result = new WebResult();
        result.setCode(WebResultCode.SUCCESS);
        result.setMessage(message);
        return result;
    }*/

    public static <E extends Serializable> WebResult<E> success(final E data) {
        WebResult<E> result = new WebResult<E>();
        result.setCode(WebResultCode.SUCCESS);
        //result.setMessage(message);
        result.setData(data);

        return result;
    }

}
