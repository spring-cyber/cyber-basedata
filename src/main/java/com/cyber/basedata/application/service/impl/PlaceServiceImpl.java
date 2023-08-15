package	com.cyber.basedata.application.service.impl;

import cn.hutool.core.util.IdUtil;
import com.cyber.basedata.application.service.PlaceService;
import com.cyber.basedata.domain.repository.PlaceMapper;
import com.cyber.domain.entity.PagingData;
import com.cyber.domain.entity.Place;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlaceServiceImpl implements PlaceService {

    private final PlaceMapper placeMapper;

    @Override
    @Transactional
    public Integer save(Place place) {

        if( null == place ) {
            log.warn("save place, but place is null...");
            return 0;
        }
        place.setId(IdUtil.simpleUUID());
        return placeMapper.save( place );
    }

    @Override
    @Transactional
    public Integer deleteById(Place place) {

        if( null == place ) {
            log.warn("delete place, but place is null  or place id is null...");
            return 0;
        }

        return placeMapper.deleteById( place );
    }

    @Override
    @Transactional
    public Integer updateById(Place place) {

        if( null == place ) {
            log.warn("update place, but place is null  or place id is null...");
            return 0;
        }

        return placeMapper.updateById( place );
    }

    @Override
    public Place selectOne(Place place) {
        if( null == place ) {
            log.warn("select place one, but place is null ...");
            return null;
        }
        place = placeMapper.selectOne( place );
        return place;
    }


    @Override
    public PagingData<Place> selectPage(Place place) {
        PagingData<Place> PagingData = new PagingData<>();

        if( null == place ) {
            log.warn("select place page, but place is null...");
            return PagingData;
        }

        Integer queryCount = placeMapper.selectByIndexCount( place );
        PagingData.setRow( queryCount );

        if( queryCount <= 0 ) {
            log.info("select place page , but count {} == 0 ...",queryCount);
            return PagingData;
        }

        List<Place> places =  selectByIndex( place );
        PagingData.setData( places );
        return PagingData;
    }

    @Override
    public List<Place> selectByIndex(Place place) {
        List<Place> places = new ArrayList<>();
        if( null == place ) {
            log.warn("select place by index, but place is null ...");
            return places;
        }

        places = placeMapper.selectByIndex( place );

        return places;
    }

    @Override
    public List<Place> selectPlace(Place place) {
        List<Place> places = new ArrayList<>();
        if( null == place ) {
            log.warn("select place by index, but place is null ...");
            return places;
        }

        places = placeMapper.selectPlace( place );

        return places;
    }
}
