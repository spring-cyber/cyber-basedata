package com.cyber.basedata.domain.repository;

import com.alibaba.fastjson.JSONObject;
import com.cyber.basedata.domain.request.TableRequest;
import com.cyber.domain.entity.BaseData;
import com.cyber.infrastructure.repository.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BaseDataMapper extends BaseMapper<BaseData> {

    List<BaseData> selectBaseData(BaseData baseData);

    BaseData checkBaseDataNameUnique(@Param("name") String name, @Param("type") Integer type);

    BaseData checkBaseDataCodeUnique(@Param("code") String code,@Param("type") Integer type);

    int hasChildByBaseDataId(String baseDataId);

    Object hasTableForDatabase(String code);

    int executeSql(String changeSql);

    Integer selectTableDataCount(TableRequest tableRequest);

    List<JSONObject> selectTableDataByIndex(TableRequest tableRequest);

    List<JSONObject> selectTableColumnData(@Param("tableCode") String tableCode, @Param("columnCode")  String columnCode);
}
