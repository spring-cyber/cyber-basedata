package	com.cyber.basedata.application.service.impl;

import com.cyber.basedata.application.service.TableColumnService;
import com.cyber.basedata.domain.repository.TableColumnMapper;
import com.cyber.domain.entity.PagingData;
import com.cyber.domain.entity.TableColumn;
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
public class TableColumnServiceImpl implements TableColumnService {

    private final TableColumnMapper tableColumnMapper;

    @Override
    @Transactional
    public Integer save(TableColumn tableColumn) {

        if( null == tableColumn ) {
            log.warn("save tableColumn, but tableColumn is null...");
            return 0;
        }

        return tableColumnMapper.save( tableColumn );
    }

    @Override
    @Transactional
    public Integer deleteById(TableColumn tableColumn) {

        if( null == tableColumn ) {
            log.warn("delete tableColumn, but tableColumn is null  or tableColumn id is null...");
            return 0;
        }

        return tableColumnMapper.deleteById( tableColumn );
    }

    @Override
    @Transactional
    public Integer updateById(TableColumn tableColumn) {

        if( null == tableColumn ) {
            log.warn("update tableColumn, but tableColumn is null  or tableColumn id is null...");
            return 0;
        }

        return tableColumnMapper.updateById( tableColumn );
    }

    @Override
    public TableColumn selectOne(TableColumn tableColumn) {
        if( null == tableColumn ) {
            log.warn("select tableColumn one, but tableColumn is null ...");
            return null;
        }
        tableColumn = tableColumnMapper.selectOne( tableColumn );
        return tableColumn;
    }


    @Override
    public PagingData<TableColumn> selectPage(TableColumn tableColumn) {
        PagingData<TableColumn> PagingData = new PagingData<>();

        if( null == tableColumn ) {
            log.warn("select tableColumn page, but tableColumn is null...");
            return PagingData;
        }

        Integer queryCount = tableColumnMapper.selectByIndexCount( tableColumn );
        PagingData.setRow( queryCount );

        if( queryCount <= 0 ) {
            log.info("select tableColumn page , but count {} == 0 ...",queryCount);
            return PagingData;
        }

        List<TableColumn> tableColumns =  selectByIndex( tableColumn );
        PagingData.setData( tableColumns );
        return PagingData;
    }

    @Override
    public List<TableColumn> selectByIndex(TableColumn tableColumn) {
        List<TableColumn> tableColumns = new ArrayList<>();
        if( null == tableColumn ) {
            log.warn("select tableColumn by index, but tableColumn is null ...");
            return tableColumns;
        }

        tableColumns = tableColumnMapper.selectByIndex( tableColumn );

        return tableColumns;
    }
}
