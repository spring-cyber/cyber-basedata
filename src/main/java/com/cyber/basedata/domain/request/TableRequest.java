package com.cyber.basedata.domain.request;

import com.cyber.domain.entity.PagingRequest;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class TableRequest extends PagingRequest {

	/**数据表编码*/
	private String tableCode;
}
