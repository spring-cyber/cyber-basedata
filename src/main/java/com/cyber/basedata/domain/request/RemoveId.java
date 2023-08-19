package com.cyber.basedata.domain.request;

import com.cyber.domain.entity.Entity;
import lombok.Data;
@Data
public class RemoveId extends Entity {

    /**
     * 主键编码
     */
    private String pkCode;

    /**
     * 主键值
     */
    private String pkValue;
}
