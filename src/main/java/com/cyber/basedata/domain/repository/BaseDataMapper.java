package com.cyber.basedata.domain.repository;

import com.cyber.domain.entity.BaseData;
import com.cyber.domain.entity.Dict;
import com.cyber.infrastructure.repository.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BaseDataMapper extends BaseMapper<BaseData> {

    List<BaseData> selectBaseData(BaseData baseData);

    Dict checkBaseDataNameUnique(@Param("name") String name, @Param("type") Integer type, @Param("parentId") String parentId);

    Dict checkBaseDataCodeUnique(@Param("code") String code,@Param("type") Integer type,@Param("parentId") String parentId);

    int hasChildByBaseDataId(String baseDataId);

    Object hasTableForDatabase(String code);

    int executeSql(String changeSql);
}
