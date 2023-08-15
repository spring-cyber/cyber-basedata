package com.cyber.basedata.domain.repository;

import com.cyber.domain.entity.Place;
import com.cyber.infrastructure.repository.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PlaceMapper extends BaseMapper<Place> {

    List<Place> selectPlace(Place place);

}
