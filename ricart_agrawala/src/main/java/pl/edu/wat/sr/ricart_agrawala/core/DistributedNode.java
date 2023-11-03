package pl.edu.wat.sr.ricart_agrawala.core;

import pl.edu.wat.sr.ricart_agrawala.StringsResourceController;
import pl.edu.wat.sr.ricart_agrawala.core.comm.CommController;
import pl.edu.wat.sr.ricart_agrawala.core.log.LogController;
import pl.edu.wat.sr.ricart_agrawala.core.net.NetController;
import pl.edu.wat.sr.ricart_agrawala.core.sys.SysController;

public class DistributedNode {
    private static DistributedNode instance;

    public final SysController sysController;
    public final NetController netController;
    public final CommController commController;
    public final LogController logController;
    public final StringsResourceController resourceController;

    private NodeState state;

    private DistributedNode(SysController sysController,
                            NetController netController,
                            CommController commController,
                            LogController logController,
                            StringsResourceController resourceController) {
        this.state = NodeState.INVALID;
        this.sysController = sysController;
        this.netController = netController;
        this.commController = commController;
        this.logController = logController;
        this.resourceController = resourceController;
    }

    public static DistributedNode getInstance() throws RuntimeException {
        if (instance == null) {
            instance = new DistributedNode(
                    new SysController(),
                    new NetController(),
                    new CommController(),
                    LogController.getInstance(),
                    StringsResourceController.getInstance()
            );
        }
        return instance;
    }

    public void setState(NodeState state) {
        this.state = state;
        logController.logInfo(this.getClass().getName(), String.format("%s : %s",
                resourceController.getText("log_info_node_state_turn"),
                resourceController.getText(String.format("state_%s",
                        state.name().toLowerCase()))));
    }
    public NodeState getState() {
        return state;
    }
}
