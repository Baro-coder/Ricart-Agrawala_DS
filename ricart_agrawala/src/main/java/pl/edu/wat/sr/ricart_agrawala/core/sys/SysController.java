package pl.edu.wat.sr.ricart_agrawala.core.sys;

import pl.edu.wat.sr.ricart_agrawala.core.log.LogController;

public class SysController {
    private Integer sysCheckInterval;

    public SysController() {
        this.sysCheckInterval = -1;
    }

    public SysController(Integer sysCheckInterval) {
        this.sysCheckInterval = sysCheckInterval;
    }

    public void setSysCheckInterval(Integer sysCheckInterval) {
        this.sysCheckInterval = sysCheckInterval;
        LogController.getInstance().logInfo(this.getClass().getName(), String.format("SysCheck Interval set to : %d [ms]", sysCheckInterval));
    }
    public Integer getSysCheckInterval() { return sysCheckInterval; }
}
