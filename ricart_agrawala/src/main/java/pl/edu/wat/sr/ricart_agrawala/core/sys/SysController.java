package pl.edu.wat.sr.ricart_agrawala.core.sys;

import pl.edu.wat.sr.ricart_agrawala.core.log.LogController;

public class SysController {
    private static SysController instance;
    private Integer sysCheckInterval;

    private SysController() {
        this.sysCheckInterval = -1;
    }

    private SysController(Integer sysCheckInterval) {
        this.sysCheckInterval = sysCheckInterval;
    }

    public static SysController getInstance() {
        if (instance == null) {
            instance = new SysController();
        }
        return instance;
    }

    public void setSysCheckInterval(Integer sysCheckInterval) {
        this.sysCheckInterval = sysCheckInterval;
        LogController.getInstance().logInfo(this.getClass().getName(), String.format("SysCheck Interval set to : %d", sysCheckInterval));
    }
    public Integer getSysCheckInterval() { return sysCheckInterval; }
}
