package pl.edu.wat.sr.ricart_agrawala.core.sys;

import pl.edu.wat.sr.ricart_agrawala.StringsResourceController;
import pl.edu.wat.sr.ricart_agrawala.core.log.LogController;

public class SysController {
    private final StringsResourceController resourceController;
    private Integer sysCheckInterval;

    public SysController() {
        this.sysCheckInterval = -1;
        resourceController = StringsResourceController.getInstance();
    }

    public void setSysCheckInterval(Integer sysCheckInterval) {
        this.sysCheckInterval = sysCheckInterval;
        LogController.getInstance().logInfo(this.getClass().getName(), String.format("%s : %d [ms]",
                resourceController.getText("log_info_sys_check_interval_changed"),
                sysCheckInterval));
    }
    public Integer getSysCheckInterval() { return sysCheckInterval; }
}
