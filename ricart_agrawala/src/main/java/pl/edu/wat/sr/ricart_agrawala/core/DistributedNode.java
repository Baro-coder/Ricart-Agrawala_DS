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

    private DistributedNode(SysController sysController, NetController netController, CommController commController, LogController logController) {
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


}
