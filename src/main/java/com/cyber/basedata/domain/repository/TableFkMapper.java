package com.cyber.basedata.domain.repository;

import com.cyber.domain.entity.TableFk;
import com.cyber.infrastructure.repository.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TableFkMapper extends BaseMapper<TableFk> {

    int deleteByTableCode(String tableCode);

    int saveBatch(List<TableFk> fkList);
}
