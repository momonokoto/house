package com.zpark.utils;


import lombok.Data;

import java.io.Serializable;

@Data
public class ResponseOrder implements Serializable {
    private static final long serialVersionUID = 1L;

    private String link;
    private String orderNo;
}
