package pl.edu.wat.sr.ricart_agrawala;

import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.converter.IntegerStringConverter;
import pl.edu.wat.sr.ricart_agrawala.core.DistributedNode;
import pl.edu.wat.sr.ricart_agrawala.core.NodeState;
import pl.edu.wat.sr.ricart_agrawala.core.net.NetInterface;

import java.net.URL;
import java.nio.file.AccessDeniedException;
import java.security.InvalidParameterException;
import java.security.KeyException;
import java.util.ResourceBundle;

public class RadsController implements Initializable {
    /* Logic link */
    private DistributedNode node;
    private StringsResourceController resourceController;

    /* Root containers */
    public SplitPane paneSettingsSection;


    /* Network Settings Controls */
    // Variable name prefix : sn-
    public Tab snTab;
    public ComboBox<String> snInterfaceComboBox;
    public ListView<Integer> snRemotePortsListView;
    public TextField snLocalPortTextField;
    public TextField snRemotePortTextField;
    public TextField snInterfacesUpdateIntervalTextField;
    public TextField snAddress;
    public TextField snSubnet;
    public TextField snMask;
    public Button snButtonRemotePortAdd;
    public Label snLocalPortErrLabel;
    public Label snRemotePortsListErrLabel;
    public Label snInterfacesUpdateIntervalErrLabel;

    /* System Settings Controls */
    // Variable name prefix : ss-
    public Tab ssTab;
    public TextField ssSysCheckIntervalTextField;
    public Label ssSysCheckIntervalErrLabel;

    /* Application Settings Controls */
    // Variable name prefix : ap-
    public Tab apTab;

    /* Output Controls */
    // Variable name prefix : out-
    public LineChart outChart;
    public TextArea outLogTextArea;
    public ProgressBar outProgressBar;
    public Label outStatusLabel;
    public Circle outStateCircle;
    public Label outStateLabel;

    /* Buttons */
    public ToggleButton outToggleButton;
    public Button buttonReset;
    public Button buttonApply;
    public Button buttonStop;
    public Button buttonStart;

    /* Others */
    private final IntegerStringConverter integerConverter = new IntegerStringConverter();
    private boolean settingsNetValid = false;
    private boolean settingsSysValid = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        resourceController = StringsResourceController.getInstance();
        resourceController.setResourceBundle(resourceBundle);

        this.node = DistributedNode.getInstance();

        initializeOutControls();
        initializeSysSettingsControls();
        initializeNetSettingsControls();
        initializeAppSettingsControls();
        initializeButtons();
    }

    private void initializeNetSettingsControls() {
        // Interface
        node.netController.update();
        snInterfaceComboBox.getItems().addAll(node.netController.getNetInterfacesNames());
        snInterfaceComboBox.setValue(snInterfaceComboBox.getItems().get(0));
        ComboBoxInterfacesChange();
        snInterfaceComboBox.setOnAction(actionEvent -> ComboBoxInterfacesChange());

        // Local port
        snLocalPortTextField.setTextFormatter(new TextFormatter<>(integerConverter));

        // Remote ports
        snRemotePortTextField.setTextFormatter(new TextFormatter<>(integerConverter));

        // Interfaces update interval
        snInterfacesUpdateIntervalTextField.setTextFormatter(new TextFormatter<>(integerConverter));

        // Buttons
        // -- Remote port add
        snButtonRemotePortAdd.setOnAction(event -> {
            Integer remotePort = Integer.parseInt(snRemotePortTextField.getText());
            node.commController.addRemotePort(remotePort);
            if(!snRemotePortsListView.getItems().contains(remotePort)) {
                snRemotePortsListView.getItems().add(remotePort);
                snRemotePortsListErrLabel.setText("");
                snRemotePortTextField.clear();
            } else {
                snRemotePortsListErrLabel.setText(resourceController.getText("err_remote_ports_already_provided"));
            }
        });
    }

    private void initializeSysSettingsControls() {
        // SysCheckInterval
        ssSysCheckIntervalTextField.setTextFormatter(new TextFormatter<>(integerConverter));
    }

    private void initializeAppSettingsControls() {
        // TODO: Set up the control to change application theme (DARK / LIGHT)
        // TODO: Set up the control to change application language (PL / EN)
    }

    private void initializeOutControls() {
        // System
        // TODO: System output controls - canvas view, area to design distributed system network

        // Chart
        // TODO: LineChart control - live time drawn chart

        // Log
        node.logController.setOutLogTextArea(outLogTextArea);

        // Status
        // TODO: Status label and progress bar - link to controls to others controllers
    }

    private void initializeButtons() {
        // Toggle State
        outToggleButton.setOnAction(event -> {
            boolean selected = outToggleButton.isSelected();

            setDisableStateForAllControls(selected);
            snButtonRemotePortAdd.setDisable(selected);
            buttonApply.setDisable(selected);
            buttonReset.setDisable(selected);
            buttonStart.setDisable(!selected);

            if (selected) {
                setNodeState(NodeState.READY);
            } else {
                setNodeState(NodeState.VALID);
            }
        });

        // Apply
        buttonApply.setOnAction(event -> {
            settingsNetValid = validateAndApplyNetworkSettings();
            settingsSysValid = validateAndApplySystemSettings();

            outToggleButton.setDisable(!(settingsNetValid && settingsSysValid));
            if(settingsNetValid && settingsSysValid) {
                setNodeState(NodeState.VALID);
            } else {
                setNodeState(NodeState.INVALID);
            }
        });

        // Reset
        buttonReset.setOnAction(event -> {
            // -- Net Settings
            // ---- comboBox
            snInterfaceComboBox.getItems().clear();
            node.netController.update();
            snInterfaceComboBox.getItems().addAll(node.netController.getNetInterfacesNames());
            snInterfaceComboBox.setValue(snInterfaceComboBox.getItems().get(0));
            ComboBoxInterfacesChange();

            // ---- local port
            snLocalPortTextField.clear();
            snLocalPortErrLabel.setText("");

            // ---- remote ports
            snRemotePortTextField.clear();
            snRemotePortsListErrLabel.setText("");
            snRemotePortsListView.getItems().clear();
            node.commController.clearRemotePorts();

            // -- Sys Settings
            // ---- sysCheckInterval
            ssSysCheckIntervalTextField.clear();
            ssSysCheckIntervalErrLabel.setText("");
            node.sysController.setSysCheckInterval(0);

            // -- Node state
            setNodeState(NodeState.INVALID);

            // -- Buttons
            outToggleButton.setDisable(true);
            buttonStop.setDisable(true);
            buttonStart.setDisable(true);
        });

        // Stop
        buttonStop.setDisable(true);
        buttonStop.setOnAction(event -> {
            // -- Node state
            setNodeState(NodeState.READY);

            // -- Disables
            outToggleButton.setDisable(false);
            buttonStart.setDisable(false);
            buttonStop.setDisable(true);
        });

        // Start
        buttonStart.setDisable(true);
        buttonStart.setOnAction(event -> {
            // -- Node state
            setNodeState(NodeState.WORKING);

            // -- Disables
            outToggleButton.setDisable(true);
            buttonStart.setDisable(true);
            buttonStop.setDisable(false);
        });
    }

    private void ComboBoxInterfacesChange() {
        try {
            node.netController.setActiveInterface(
                    node.netController.getNetInterfaceByName(snInterfaceComboBox.getValue()));
            NetInterface selectedNetInterface = node.netController.getActiveInterface();
            snSubnet.setText(selectedNetInterface.getSubnet());
            snAddress.setText(selectedNetInterface.getAddress());
            snMask.setText(String.valueOf(selectedNetInterface.getSubnetPrefixLength()));
        } catch (KeyException e) {
            snSubnet.setText("");
            snAddress.setText("");
            snMask.setText("");
        }
    }

    private void setNodeState(NodeState state) {
        node.setState(state);
        switch(state) {
            case INVALID -> {
                outStateLabel.setText(resourceController.getText("state_invalid"));
                outStateCircle.setFill(Color.RED);
            } case VALID -> {
                outStateLabel.setText(resourceController.getText("state_valid"));
                outStateCircle.setFill(Color.YELLOW);
            } case READY -> {
                outStateLabel.setText(resourceController.getText("state_ready"));
                outStateCircle.setFill(Color.GREEN);
            } case WORKING -> {
                outStateLabel.setText(resourceController.getText("state_working"));
                outStateCircle.setFill(Color.BLUE);
            }
        }
    }

    private boolean validateAndApplyNetworkSettings() {
        boolean flag = true;

        NetInterface activeInterface = node.netController.getActiveInterface();
        if (activeInterface != null) {
            node.commController.setLocalAddress(activeInterface.getAddress());

            if(!snLocalPortTextField.getText().isBlank()) {
                try {
                    node.commController.setLocalPort(Integer.parseInt(snLocalPortTextField.getText()));
                    snLocalPortErrLabel.setText("");
                } catch (AccessDeniedException e) {
                    snLocalPortErrLabel.setText(resourceController.getText("err_local_port_already_in_use"));
                    flag = false;
                } catch (InvalidParameterException e) {
                    snSubnet.clear();
                    snAddress.clear();
                    snMask.clear();
                    snLocalPortErrLabel.setText(resourceController.getText("err_local_port_invalid_interface"));
                    flag = false;
                }
            } else {
                snLocalPortErrLabel.setText(resourceController.getText("err_local_port_not_specified"));
                flag = false;
            }

            if (!node.commController.getRemotePorts().isEmpty()) {
                snRemotePortsListErrLabel.setText("");
            } else {
                snRemotePortsListErrLabel.setText(resourceController.getText("err_remote_ports_list_empty"));
                flag = false;
            }

            if (!snInterfacesUpdateIntervalTextField.getText().isBlank()) {
                node.netController.setUpdateInterval(Integer.parseInt(snInterfacesUpdateIntervalTextField.getText()));
                snInterfacesUpdateIntervalErrLabel.setText("");
            } else {
                snInterfacesUpdateIntervalErrLabel.setText(resourceController.getText("err_value_not_specified"));
            }
        } else {
            snSubnet.clear();
            snAddress.clear();
            snMask.clear();
            snLocalPortErrLabel.setText(resourceController.getText("err_local_port_invalid_interface"));
            flag = false;
        }

        return flag;
    }
    private boolean validateAndApplySystemSettings() {
        boolean flag = true;

        if(!ssSysCheckIntervalTextField.getText().isBlank()) {
            node.sysController.setSysCheckInterval(Integer.parseInt(ssSysCheckIntervalTextField.getText()));
            ssSysCheckIntervalErrLabel.setText("");
        } else {
            ssSysCheckIntervalErrLabel.setText(resourceController.getText("err_value_not_specified"));
            flag = false;
        }

        return flag;
    }

    private void setDisableStateForAllControls(boolean state) {
        //-- containers
        // paneSettingsSection.setDisable(state);
        snTab.setDisable(state);
        ssTab.setDisable(state);
        apTab.setDisable(state);

        /*
        // -- net
        snInterfaceComboBox.setDisable(state);
        snSubnet.setDisable(state);
        snAddress.setDisable(state);
        snMask.setDisable(state);
        snLocalPortTextField.setDisable(state);
        snRemotePortTextField.setDisable(state);
        snButtonRemotePortAdd.setDisable(state);
        snRemotePortsListView.setDisable(state);
        snInterfacesUpdateIntervalTextField.setDisable(state);

        // -- sys
        ssSysCheckIntervalTextField.setDisable(state);

        // -- app
        apMinLogLevelComboBox.setDisable(state);
        */
    }
}