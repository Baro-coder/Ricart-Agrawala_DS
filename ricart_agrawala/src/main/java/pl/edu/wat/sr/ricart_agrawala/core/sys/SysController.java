package pl.edu.wat.sr.ricart_agrawala.core.sys;

import pl.edu.wat.sr.ricart_agrawala.core.res.ResourceController;
import pl.edu.wat.sr.ricart_agrawala.core.log.LogController;

public class SysController {
    private final ResourceController resourceController;
    private Integer sysCheckInterval;

    public SysController() {
        this.sysCheckInterval = -1;
        resourceController = ResourceController.getInstance();
    }

    public void setSysCheckInterval(Integer sysCheckInterval) {
        this.sysCheckInterval = sysCheckInterval;
        LogController.getInstance().logInfo(this.getClass().getName(), String.format("%s : %d [ms]",
                resourceController.getText("log_info_sys_check_interval_changed"),
                sysCheckInterval));
    }
    public Integer getSysCheckInterval() { return sysCheckInterval; }
}
