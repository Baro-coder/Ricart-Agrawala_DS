package pl.edu.wat.sr.ricart_agrawala.core.sys;

import pl.edu.wat.sr.ricart_agrawala.RadsConfig;

public class SysController {
    private static SysController instance;
    private Integer sysCheckInterval;

    private SysController() {
        this.sysCheckInterval = RadsConfig.SYS_CHECK_INTERVAL_DEFAULT;
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
    }
    public Integer getSysCheckInterval() { return sysCheckInterval; }
}
