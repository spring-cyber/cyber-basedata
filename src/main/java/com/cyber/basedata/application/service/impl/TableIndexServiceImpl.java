package	com.cyber.basedata.application.service.impl;

import com.cyber.basedata.application.service.TableIndexService;
import com.cyber.basedata.domain.repository.TableIndexMapper;
import com.cyber.domain.entity.PagingData;
import com.cyber.domain.entity.TableIndex;
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
public class TableIndexServiceImpl implements TableIndexService {

    private final TableIndexMapper tableIndexMapper;

    @Override
    @Transactional
    public Integer save(TableIndex tableIndex) {

        if( null == tableIndex ) {
            log.warn("save tableIndex, but tableIndex is null...");
            return 0;
        }

        return tableIndexMapper.save( tableIndex );
    }

    @Override
    @Transactional
    public Integer deleteById(TableIndex tableIndex) {

        if( null == tableIndex ) {
            log.warn("delete tableIndex, but tableIndex is null  or tableIndex id is null...");
            return 0;
        }

        return tableIndexMapper.deleteById( tableIndex );
    }

    @Override
    @Transactional
    public Integer updateById(TableIndex tableIndex) {

        if( null == tableIndex ) {
            log.warn("update tableIndex, but tableIndex is null  or tableIndex id is null...");
            return 0;
        }

        return tableIndexMapper.updateById( tableIndex );
    }

    @Override
    public TableIndex selectOne(TableIndex tableIndex) {
        if( null == tableIndex ) {
            log.warn("select tableIndex one, but tableIndex is null ...");
            return null;
        }
        tableIndex = tableIndexMapper.selectOne( tableIndex );
        return tableIndex;
    }


    @Override
    public PagingData<TableIndex> selectPage(TableIndex tableIndex) {
        PagingData<TableIndex> PagingData = new PagingData<>();

        if( null == tableIndex ) {
            log.warn("select tableIndex page, but tableIndex is null...");
            return PagingData;
        }

        Integer queryCount = tableIndexMapper.selectByIndexCount( tableIndex );
        PagingData.setRow( queryCount );

        if( queryCount <= 0 ) {
            log.info("select tableIndex page , but count {} == 0 ...",queryCount);
            return PagingData;
        }

        List<TableIndex> tableIndexs =  selectByIndex( tableIndex );
        PagingData.setData( tableIndexs );
        return PagingData;
    }

    @Override
    public List<TableIndex> selectByIndex(TableIndex tableIndex) {
        List<TableIndex> tableIndexs = new ArrayList<>();
        if( null == tableIndex ) {
            log.warn("select tableIndex by index, but tableIndex is null ...");
            return tableIndexs;
        }

        tableIndexs = tableIndexMapper.selectByIndex( tableIndex );

        return tableIndexs;
    }
}
