package com.cyber.basedata.application.service;

import com.cyber.application.service.BaseService;
import com.cyber.domain.entity.Place;

import java.util.List;

public interface PlaceService extends BaseService<Place> {


    List<Place> selectPlace(Place place);
}
