package com.cyber.basedata.domain.repository;

import com.cyber.domain.entity.TableColumn;
import com.cyber.infrastructure.repository.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TableColumnMapper extends BaseMapper<TableColumn> {

    int deleteByTableCode(String tableCode);

    int saveBatch(List<TableColumn> columnList);

    List<TableColumn> selectList(TableColumn tableColumn);
}
