package org.soft.monitor.properties;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 配置文件
 *
 * @author Parker
 * @date 2021-06-10
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@Component
@ConfigurationProperties(prefix = MonitorProperties.PROP_PREFIX)
public class MonitorProperties {

    public static final String PROP_PREFIX = "monitor";

    /** 被监控进程 */
    private String monitorTask;

    /** 如果发现结束后 被唤起进程 */
    private String arouseTask;

    /** 如果发现结束后 被唤起进程 详细路径 */
    private String arouseTaskPath;

}
