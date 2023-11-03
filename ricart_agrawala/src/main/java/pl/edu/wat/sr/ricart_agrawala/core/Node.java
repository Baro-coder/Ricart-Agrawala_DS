package pl.edu.wat.sr.ricart_agrawala.core;

import pl.edu.wat.sr.ricart_agrawala.core.res.ResourceController;
import pl.edu.wat.sr.ricart_agrawala.core.comm.CommController;
import pl.edu.wat.sr.ricart_agrawala.core.log.LogController;
import pl.edu.wat.sr.ricart_agrawala.core.net.NetController;
import pl.edu.wat.sr.ricart_agrawala.core.sys.SysController;

public class Node {
    private static Node instance;

    public final SysController sysController;
    public final NetController netController;
    public final CommController commController;
    public final LogController logController;
    public final ResourceController resourceController;

    private NodeState state;

    private Node(SysController sysController,
                 NetController netController,
                 CommController commController,
                 LogController logController,
                 ResourceController resourceController) {
        this.state = NodeState.INVALID;
        this.sysController = sysController;
        this.netController = netController;
        this.commController = commController;
        this.logController = logController;
        this.resourceController = resourceController;
    }

    public static Node getInstance() throws RuntimeException {
        if (instance == null) {
            instance = new Node(
                    new SysController(),
                    new NetController(),
                    new CommController(),
                    LogController.getInstance(),
                    ResourceController.getInstance()
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
