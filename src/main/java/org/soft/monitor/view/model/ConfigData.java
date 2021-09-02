package org.soft.monitor.view.model;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.soft.monitor.SoftMonitorApplication;

import java.io.*;
import java.net.URL;

/**
 * 持久化数据
 *
 * @author Parker
 * @date 2021/9/1 19:10
 */
@Slf4j
@Data
public class ConfigData implements Serializable {

	private static final long serialVersionUID = 1L;

	/** 监控名称 */
	private String monitorFileName;

	/** 监控地址 */
	private String monitorFilePath;

	/** 唤起名称 */
	private String arouseFileName;

	/** 唤起地址 */
	private String arouseFilePath;

	/** 存盘 */
	public static void save(ConfigData configData){
		long time = System.currentTimeMillis();

		ObjectOutputStream oos = null;
		try {
			URL resource = SoftMonitorApplication.class.getResource("/");
			if(null == resource){
				return;
			}

			File configDataFile = FileUtil.touch(resource.getPath() + "/memento/monitor.data");
			oos = new ObjectOutputStream(new FileOutputStream(configDataFile));


			// 1.设置存盘时间
			oos.writeObject(time);
			// 2.监控名称
			oos.writeObject(configData.monitorFileName);
			// 3.监控地址
			oos.writeObject(configData.monitorFilePath);
			// 4.唤起名称
			oos.writeObject(configData.arouseFileName);
			// 5.唤起地址
			oos.writeObject(configData.arouseFilePath);

			log.info("存盘 - Time：{}", DateUtil.formatDate(DateUtil.date()));

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(oos != null){
				try {
					oos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 回档
	 */
	public static ConfigData load(){
		Long time;
		ObjectInputStream ois = null;
		ConfigData configData = new ConfigData();
		try {
			URL resource = SoftMonitorApplication.class.getResource("/");
			if(null == resource){
				return configData;
			}

			File configDataFile = FileUtil.touch(resource.getPath() + "/memento/monitor.data");
			ois = new ObjectInputStream(new FileInputStream(configDataFile));

			// ------------ 处理对象 ------------
			// 1.获得存盘时间
			time = (Long) ois.readObject();
			if(time == null){
				return configData;
			}

			// 1.设置存盘时间
			configData = new ConfigData();
			configData.setMonitorFileName((String) ois.readObject());
			configData.setMonitorFilePath((String) ois.readObject());
			configData.setArouseFileName((String) ois.readObject());
			configData.setArouseFilePath((String) ois.readObject());

		} catch (IOException | ClassNotFoundException  e ) {
			e.printStackTrace();
		} finally {
			if(ois != null){
				try {
					ois.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return configData;
	}

}
