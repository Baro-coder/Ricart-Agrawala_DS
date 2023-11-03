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
    public SysController(Integer sysCheckInterval) {
        this.sysCheckInterval = sysCheckInterval;
        resourceController = StringsResourceController.getInstance();
    }

    public void setSysCheckInterval(Integer sysCheckInterval) {
        this.sysCheckInterval = sysCheckInterval;
        LogController.getInstance().logInfo(this.getClass().getName(), String.format("SysCheck Interval set to : %d [ms]", sysCheckInterval));
    }
    public Integer getSysCheckInterval() { return sysCheckInterval; }
}
