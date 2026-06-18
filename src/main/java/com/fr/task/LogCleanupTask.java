package com.fr.task;

import com.fr.javaBean.OperationLog;
import com.fr.mapper.OperationLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * 日志清理定时任务
 * 每天凌晨3:00自动执行，删除非当天的历史日志，保留当日数据
 */
@Component
public class LogCleanupTask {

    @Autowired
    private OperationLogMapper operationLogMapper;

    /**
     * 定时清理历史日志
     * cron: 0 0 3 * * ?  每天凌晨3:00执行
     */
    @Scheduled(cron = "0 0 3 * * ?")
    public void cleanOldLogs() {
        // 查询非当天的日志数量
        List<OperationLog> oldLogs = operationLogMapper.selectList(null);
        int count = 0;
        Date today = new Date();
        
        for (OperationLog log : oldLogs) {
            if (log.getCreateTime() != null) {
                if (!isSameDay(log.getCreateTime(), today)) {
                    operationLogMapper.deleteById(log.getId());
                    count++;
                }
            }
        }
        
        // 写入一条 CLEAN 类型的新日志记录本次清理操作
        OperationLog cleanLog = new OperationLog();
        cleanLog.setUsername("SYSTEM");
        cleanLog.setOperation("日志清理: 删除" + count + "条历史日志");
        cleanLog.setMethod("LogCleanupTask.cleanOldLogs");
        cleanLog.setIp("127.0.0.1");
        cleanLog.setParams("删除数量=" + count);
        cleanLog.setCreateTime(new Date());
        operationLogMapper.insert(cleanLog);
        
        // 控制台输出清理数量
        System.out.println("【日志清理任务】已清理 " + count + " 条历史日志");
    }

    /**
     * 判断两个日期是否为同一天
     */
    private boolean isSameDay(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            return false;
        }
        java.util.Calendar cal1 = java.util.Calendar.getInstance();
        java.util.Calendar cal2 = java.util.Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        return cal1.get(java.util.Calendar.YEAR) == cal2.get(java.util.Calendar.YEAR)
                && cal1.get(java.util.Calendar.MONTH) == cal2.get(java.util.Calendar.MONTH)
                && cal1.get(java.util.Calendar.DAY_OF_MONTH) == cal2.get(java.util.Calendar.DAY_OF_MONTH);
    }
}