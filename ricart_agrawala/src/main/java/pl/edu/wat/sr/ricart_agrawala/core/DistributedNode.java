package pl.edu.wat.sr.ricart_agrawala.core;

import pl.edu.wat.sr.ricart_agrawala.core.comm.CommController;
import pl.edu.wat.sr.ricart_agrawala.core.log.LogController;
import pl.edu.wat.sr.ricart_agrawala.core.net.NetController;
import pl.edu.wat.sr.ricart_agrawala.core.sys.SysController;

public class DistributedNode {
    public SysController sysController;
    public NetController netController;
    public CommController commController;
    public LogController logController;
    private static DistributedNode instance;
    private boolean ready;

    private DistributedNode(SysController sysController, NetController netController, CommController commController, LogController logController) {
        ready = false;
        this.sysController = sysController;
        this.netController = netController;
        this.commController = commController;
        this.logController = logController;
    }

    public static DistributedNode getInstance() {
        if (instance == null) {
            instance = new DistributedNode(
                    SysController.getInstance(),
                    NetController.getInstance(),
                    CommController.getInstance(),
                    LogController.getInstance()
            );
        }
        return instance;
    }

    public void setReady(boolean state) {
        ready = state;
        if (ready) {
            logController.logInfo(this.getClass().getName(), "Node state turned to : READY");
        } else {
            logController.logWarning(this.getClass().getName(), "Node state turned to : NOT READY!");
        }
    }
    public boolean isReady() {
        return ready;
    }
}
