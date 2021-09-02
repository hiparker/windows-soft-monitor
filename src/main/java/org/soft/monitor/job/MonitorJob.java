package org.soft.monitor.job;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.soft.monitor.utils.MonitorUtil;
import org.soft.monitor.view.model.ConfigData;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.awt.*;

/**
 * 定时监控
 *
 * @author Parker
 * @date 2021/8/10 16:20
 */
@Slf4j
@Configuration
@EnableScheduling
public class MonitorJob {

	private static java.awt.List logList;

	private static ConfigData configData;

	public static synchronized void  setLogList(List logList) {
		MonitorJob.logList = logList;
	}

	public static synchronized void setConfigData(ConfigData configData) {
		MonitorJob.configData = configData;
	}

	public static synchronized ConfigData getConfigData() {
		return configData;
	}

	/**
	 * 添加定时任务
	 */
	@Scheduled(cron = "*/5 * * * * ?")
	private void configureTasks() {
		if(null == configData || StrUtil.isBlank(configData.getMonitorFileName())){
			return;
		}

		// 1分钟 0 */1 * * * ?
		// 30秒 */30 * * * * ?
		boolean softEnable  = false;
		try {
			softEnable = MonitorUtil.isTaskEnable(configData.getMonitorFileName());
			if(!softEnable && StrUtil.isNotBlank(configData.getArouseFileName()) &&
					StrUtil.isNotBlank(configData.getArouseFilePath())){
				// 先杀死 唤起进程
				MonitorUtil.handleTaskKill(configData.getArouseFileName());
				// 再开启 当前被唤起进程
				MonitorUtil.handleRunTask(configData.getArouseFilePath());
			}
		}catch (Exception e){
			log.error(e.getMessage(), e);
		}

		String logStr = StrUtil.format("Current Process:[{}] ---- Status：{} ---- IsArouse：{}",
				configData.getMonitorFileName(), softEnable ? "ON" : "OFF", softEnable ? "NO" : "YES");
		if(null != logList){
			logList.add(logStr);
		}

		// 记录日志
		log.info(logStr);
	}

}
