package com.cyber.basedata.domain.repository;

import com.cyber.domain.entity.Dict;
import com.cyber.infrastructure.repository.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DictMapper extends BaseMapper<Dict> {

    List<Dict> selectList(Dict dict);

    Dict checkMenuNameUnique(@Param("name") String name, @Param("type") Integer type, @Param("parentId") String parentId);

    Dict checkMenuCodeUnique(@Param("code") String code,@Param("type") Integer type,@Param("parentId") String parentId);

    int hasChildByDictId(@Param("dictId") String dictId);

    List<Dict> selectDictDataByType(@Param("dictGroupCode") String dictGroupCode,@Param("dictTypeCode") String dictTypeCode);

    List<Dict> selectDictDataList(Dict dict);

    Dict selectDictByTypeId(Dict dictQ);
}
