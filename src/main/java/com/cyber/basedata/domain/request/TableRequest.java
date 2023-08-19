package com.cyber.basedata.domain.request;

import com.cyber.domain.entity.PagingRequest;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class TableRequest extends PagingRequest {

    /**
     * 数据表编码
     */
	@NotNull(message = "数据表编码不能为空")
    private String tableCode;

    /**
     * 需要删除的数据
     */
    private List<RemoveId> removeIdList;

    /**
     * 需要插入数据的所有列名
     */
    private String columnNames;

    /**
     * 需要插入的数据
     */
    private List<String> addDataList;
}
