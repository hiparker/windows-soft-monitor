package org.soft.monitor;

import cn.hutool.core.thread.ThreadUtil;
import org.soft.monitor.job.MonitorJob;
import org.soft.monitor.view.ViewFrame;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.awt.*;

/**
 * 软件监控
 *
 * @author Parker
 * @date 2021/9/1 14:24
 */
@SpringBootApplication
public class SoftMonitorApplication {

	public static void main(String[] args) {
		SpringApplication.run(SoftMonitorApplication.class);

		ThreadUtil.execute(()->{
			ViewFrame viewFrame = new ViewFrame();
			List logList = viewFrame.getLogList();
			MonitorJob.setLogList(logList);
		});
	}

}
