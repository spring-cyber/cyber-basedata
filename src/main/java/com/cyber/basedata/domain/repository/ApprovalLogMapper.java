package com.cyber.basedata.domain.repository;

import com.cyber.domain.entity.ApprovalLog;
import com.cyber.infrastructure.repository.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ApprovalLogMapper extends BaseMapper<ApprovalLog> {

    int hasUntreatedByTableCode(String tableCode);
}
