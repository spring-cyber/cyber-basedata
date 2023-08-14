package com.cyber.basedata.domain.repository;

import com.cyber.domain.entity.Area;
import com.cyber.infrastructure.repository.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AreaMapper extends BaseMapper<Area> {

    List<Area> selectArea(Area area);

    int hasChildByAreaId(String areaId);
}
