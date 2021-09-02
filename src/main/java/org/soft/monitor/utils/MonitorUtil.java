package org.soft.monitor.utils;

import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.core.util.StrUtil;

import java.io.IOException;
import java.util.List;

/**
 * 监控工具类
 *
 * @author Parker
 * @date 2021/9/1 14:31
 */
public final class MonitorUtil {

	private static final String TASK_START = "start ";
	private static final String TASK_LIST = "tasklist";
	private static final String TASK_KILL = "taskkill /f /t /im ";

	/**
	 * 监控进程是否存在
	 */
	public static boolean isTaskEnable(String softName) {
		Process taskList = RuntimeUtil.exec(TASK_LIST);
		List<String> resultLines = RuntimeUtil.getResultLines(taskList);
		boolean enabled = false;
		for (String resultLine : resultLines) {
			enabled = StrUtil.containsIgnoreCase(resultLine, softName);
			if(enabled){
				break;
			}
		}
		return enabled;
	}

	/**
	 * 杀死进程
	 */
	public static void handleTaskKill(String softName) {
		RuntimeUtil.execForStr(TASK_KILL + softName);
	}

	/**
	 * 开启进程
	 */
	public static void handleRunTask(String filePath) throws IOException {
		Runtime.getRuntime().exec("cmd /c start " + "\"\" \"" + filePath + "\"");
	}

	private MonitorUtil(){}
}
