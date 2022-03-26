package com.mws.backend.framework.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WebResult<DataClass extends Serializable> {
    private WebResultCode code;
    private String message;
    private DataClass data;

    public static <E extends Serializable> WebResult<E> success(final E data) {
        return new WebResult<>(WebResultCode.SUCCESS, null, data);
    }

}
