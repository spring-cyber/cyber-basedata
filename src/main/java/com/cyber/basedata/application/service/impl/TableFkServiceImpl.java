package	com.cyber.basedata.application.service.impl;

import com.cyber.basedata.application.service.TableFkService;
import com.cyber.basedata.domain.repository.TableFkMapper;
import com.cyber.domain.entity.PagingData;
import com.cyber.domain.entity.TableFk;
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
public class TableFkServiceImpl implements TableFkService {

    private final TableFkMapper tableFkMapper;

    @Override
    @Transactional
    public Integer save(TableFk tableFk) {

        if( null == tableFk ) {
            log.warn("save tableFk, but tableFk is null...");
            return 0;
        }

        return tableFkMapper.save( tableFk );
    }

    @Override
    @Transactional
    public Integer deleteById(TableFk tableFk) {

        if( null == tableFk ) {
            log.warn("delete tableFk, but tableFk is null  or tableFk id is null...");
            return 0;
        }

        return tableFkMapper.deleteById( tableFk );
    }

    @Override
    @Transactional
    public Integer updateById(TableFk tableFk) {

        if( null == tableFk ) {
            log.warn("update tableFk, but tableFk is null  or tableFk id is null...");
            return 0;
        }

        return tableFkMapper.updateById( tableFk );
    }

    @Override
    public TableFk selectOne(TableFk tableFk) {
        if( null == tableFk ) {
            log.warn("select tableFk one, but tableFk is null ...");
            return null;
        }
        tableFk = tableFkMapper.selectOne( tableFk );
        return tableFk;
    }


    @Override
    public PagingData<TableFk> selectPage(TableFk tableFk) {
        PagingData<TableFk> PagingData = new PagingData<>();

        if( null == tableFk ) {
            log.warn("select tableFk page, but tableFk is null...");
            return PagingData;
        }

        Integer queryCount = tableFkMapper.selectByIndexCount( tableFk );
        PagingData.setRow( queryCount );

        if( queryCount <= 0 ) {
            log.info("select tableFk page , but count {} == 0 ...",queryCount);
            return PagingData;
        }

        List<TableFk> tableFks =  selectByIndex( tableFk );
        PagingData.setData( tableFks );
        return PagingData;
    }

    @Override
    public List<TableFk> selectByIndex(TableFk tableFk) {
        List<TableFk> tableFks = new ArrayList<>();
        if( null == tableFk ) {
            log.warn("select tableFk by index, but tableFk is null ...");
            return tableFks;
        }

        tableFks = tableFkMapper.selectByIndex( tableFk );

        return tableFks;
    }
}
