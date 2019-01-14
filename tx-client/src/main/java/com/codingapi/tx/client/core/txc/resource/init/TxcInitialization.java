package com.codingapi.tx.client.core.txc.resource.init;

import com.codingapi.tx.client.core.txc.resource.TableStructAnalyser;
import com.codingapi.tx.client.core.txc.resource.def.TxcSqlExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.SQLException;

/**
 * Description:
 * Date: 2018/12/24
 *
 * @author ujued
 */
@Component
@Slf4j
public class TxcInitialization implements InitializingBean {

    private final TxcSettingFactory txcSettingFactory;

    private final TableStructAnalyser tableStructAnalyser;

    private final TxcSqlExecutor txcSqlExecutor;

    private final TxcExceptionConnectionPool txcExceptionConnectionPool;

    @Autowired
    public TxcInitialization(TxcSettingFactory txcSettingFactory,
                             TableStructAnalyser tableStructAnalyser,
                             TxcSqlExecutor txcSqlExecutor, TxcExceptionConnectionPool txcExceptionConnectionPool) {
        this.txcSettingFactory = txcSettingFactory;
        this.tableStructAnalyser = tableStructAnalyser;
        this.txcSqlExecutor = txcSqlExecutor;
        this.txcExceptionConnectionPool = txcExceptionConnectionPool;
    }

    @Override
    public void afterPropertiesSet() throws SQLException {
        if (txcSettingFactory.enable()) {
            log.info("enabled txc transaction.");
            if (!tableStructAnalyser.existsTable(txcSettingFactory.lockTableName())) {
                log.info("create lock table.");
                txcSqlExecutor.createLockTable();
            }
            if (!tableStructAnalyser.existsTable(txcSettingFactory.undoLogTableName())) {
                log.info("create undo log table.");
                txcSqlExecutor.createUndoLogTable();
            }
            txcExceptionConnectionPool.init();
        }
    }
}