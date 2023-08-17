package com.cyber.basedata.domain.repository;

import com.cyber.domain.entity.TableIndex;
import com.cyber.infrastructure.repository.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TableIndexMapper extends BaseMapper<TableIndex> {

    int deleteByTableCode(String tableCode);

    int saveBatch(List<TableIndex> indexList);
}
