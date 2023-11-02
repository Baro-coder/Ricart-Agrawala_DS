package pl.edu.wat.sr.ricart_agrawala;

import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.control.*;
import javafx.util.converter.IntegerStringConverter;
import pl.edu.wat.sr.ricart_agrawala.core.DistributedNode;
import pl.edu.wat.sr.ricart_agrawala.core.log.LogLevel;
import pl.edu.wat.sr.ricart_agrawala.core.net.NetInterface;

import java.net.URL;
import java.nio.file.AccessDeniedException;
import java.security.InvalidParameterException;
import java.security.KeyException;
import java.util.ResourceBundle;

public class RadsController implements Initializable {
    /* Network Settings Controls */
    // Variable name prefix : sn-
    public Tab snTab;
    public ComboBox<String> snInterfaceComboBox;
    public ListView<Integer> snRemotePortsListView;
    public TextField snLocalPortTextField;
    public TextField snRemotePortTextField;
    public Button snButtonRemotePortAdd;
    public Label snAddressLabel;
    public Label snSubnetLabel;
    public Label snSubnetPrefixLengthLabel;
    public Label snLocalPortErrLabel;
    public Label snRemotePortsListErrLabel;

    /* System Settings Controls */
    // Variable name prefix : ss-
    public Tab ssTab;
    public TextField ssSysCheckIntervalTextField;
    public Label ssSysCheckIntervalErrLabel;

    /* Application Settings Controls */
    // Variable name prefix : ap-
    public ComboBox<String> apMinLogLevelComboBox;

    /* Output Controls */
    // Variable name prefix : out-
    public LineChart outChart;
    public TextArea outLogTextArea;
    public ProgressBar outProgressBar;
    public Label outStatusLabel;

    /* Buttons */
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
        DistributedNode node = DistributedNode.getInstance();

        initializeOutControls(node, resourceBundle);
        initializeSysSettingsControls(node, resourceBundle);
        initializeNetSettingsControls(node, resourceBundle);
        initializeAppSettingsControls(node, resourceBundle);
        initializeButtons(node, resourceBundle);
    }

    private void initializeNetSettingsControls(DistributedNode node, ResourceBundle resourceBundle) {
        // Interface
        node.netController.update();
        snInterfaceComboBox.getItems().addAll(node.netController.getNetInterfacesNames());
        snInterfaceComboBox.setValue(snInterfaceComboBox.getItems().get(0));
        ComboBoxInterfacesChange(node);
        snInterfaceComboBox.setOnAction(actionEvent -> ComboBoxInterfacesChange(node));

        // Local port
        snLocalPortTextField.setTextFormatter(new TextFormatter<>(integerConverter));

        // Remote ports
        snRemotePortTextField.setTextFormatter(new TextFormatter<>(integerConverter));

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
                snRemotePortsListErrLabel.setText(resourceBundle.getString("err_remote_ports_already_provided"));
            }
        });
    }

    private void initializeSysSettingsControls(DistributedNode node, ResourceBundle resourceBundle) {
        // SysCheckInterval
        ssSysCheckIntervalTextField.setTextFormatter(new TextFormatter<>(integerConverter));
    }

    private void initializeAppSettingsControls(DistributedNode node, ResourceBundle resourceBundle) {
        // LogLevel ComboBox
        for (LogLevel level : LogLevel.values()) {
            apMinLogLevelComboBox.getItems().add(level.name());
        }
        apMinLogLevelComboBox.setValue(apMinLogLevelComboBox.getItems().get(0));
        node.logController.setMinLogLevel(LogLevel.valueOf(apMinLogLevelComboBox.getValue()));
        snInterfaceComboBox.setOnAction(event -> node.logController.setMinLogLevel(LogLevel.valueOf(apMinLogLevelComboBox.getValue())));

        // TODO: Set up the control to change application theme (DARK / LIGHT)
    }

    private void initializeOutControls(DistributedNode node, ResourceBundle resourceBundle) {
        // System
        // TODO: System output controls - canvas view, area to design distributed system network

        // Chart
        // TODO: LineChart control - live time drawn chart

        // Log
        node.logController.setOutLogTextArea(outLogTextArea);

        // Status
        // TODO: Status label and progress bar - link to controls to others controllers
    }

    private void initializeButtons(DistributedNode node, ResourceBundle resourceBundle) {
        // Apply
        buttonApply.setOnAction(event -> {
            settingsNetValid = validateAndApplyNetworkSettings(node, resourceBundle);
            settingsSysValid = validateAndApplySystemSettings(node, resourceBundle);

            node.setReady(settingsNetValid && settingsSysValid);
            buttonStart.setDisable(!(settingsNetValid && settingsSysValid));
        });

        // Reset
        buttonReset.setOnAction(event -> {
            // -- Net Settings
            // ---- comboBox
            snInterfaceComboBox.getItems().clear();
            node.netController.update();
            snInterfaceComboBox.getItems().addAll(node.netController.getNetInterfacesNames());
            snInterfaceComboBox.setValue(snInterfaceComboBox.getItems().get(0));
            ComboBoxInterfacesChange(node);

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

            // -- Buttons
            buttonStop.setDisable(true);
            buttonStart.setDisable(true);
        });

        // Stop
        buttonStop.setDisable(true);
        buttonStop.setOnAction(event -> {
            // -- Disables
            // ---- Buttons
            buttonReset.setDisable(false);
            buttonApply.setDisable(false);
            buttonStart.setDisable(false);
            buttonStop.setDisable(true);

            // ---- Controls
            setDisableStateForAllControls(false);
        });

        // Start
        buttonStart.setDisable(true);
        buttonStart.setOnAction(event -> {
            // -- Disables
            // ---- Buttons
            buttonReset.setDisable(true);
            buttonApply.setDisable(true);
            buttonStart.setDisable(true);
            buttonStop.setDisable(false);

            /// ---- Controls
            setDisableStateForAllControls(true);
        });
    }

    private void ComboBoxInterfacesChange(DistributedNode node) {
        try {
            node.netController.setActiveInterface(
                    node.netController.getNetInterfaceByName(snInterfaceComboBox.getValue()));
            NetInterface selectedNetInterface = node.netController.getActiveInterface();
            snSubnetLabel.setText(selectedNetInterface.getSubnet() + " : ");
            snAddressLabel.setText(selectedNetInterface.getAddress());
            snSubnetPrefixLengthLabel.setText("/" + String.valueOf(selectedNetInterface.getSubnetPrefixLength()));
        } catch (KeyException e) {
            snSubnetLabel.setText("...");
            snAddressLabel.setText("...");
            snSubnetPrefixLengthLabel.setText("...");
        }
    }

    private boolean validateAndApplyNetworkSettings(DistributedNode node, ResourceBundle resourceBundle) {
        boolean flag = true;

        NetInterface activeInterface = node.netController.getActiveInterface();
        if (activeInterface != null) {
            node.commController.setLocalAddress(activeInterface.getAddress());

            if(!snLocalPortTextField.getText().isBlank()) {
                try {
                    node.commController.setLocalPort(Integer.parseInt(snLocalPortTextField.getText()));
                    snLocalPortErrLabel.setText("");
                } catch (AccessDeniedException e) {
                    snLocalPortErrLabel.setText(resourceBundle.getString("err_local_port_already_in_use"));
                    flag = false;
                } catch (InvalidParameterException e) {
                    snSubnetLabel.setText("...");
                    snAddressLabel.setText("...");
                    snSubnetPrefixLengthLabel.setText("...");
                    snLocalPortErrLabel.setText(resourceBundle.getString("err_local_port_invalid_interface"));
                    flag = false;
                }
            } else {
                snLocalPortErrLabel.setText(resourceBundle.getString("err_local_port_not_specified"));
                flag = false;
            }

            if (!node.commController.getRemotePorts().isEmpty()) {
                snRemotePortsListErrLabel.setText("");
            } else {
                snRemotePortsListErrLabel.setText(resourceBundle.getString("err_remote_ports_list_empty"));
                flag = false;
            }
        } else {
            snSubnetLabel.setText("...");
            snAddressLabel.setText("...");
            snSubnetPrefixLengthLabel.setText("...");
            snLocalPortErrLabel.setText(resourceBundle.getString("err_local_port_invalid_interface"));
            flag = false;
        }

        return flag;
    }
    private boolean validateAndApplySystemSettings(DistributedNode node, ResourceBundle resourceBundle) {
        boolean flag = true;

        if(!ssSysCheckIntervalTextField.getText().isBlank()) {
            node.sysController.setSysCheckInterval(Integer.parseInt(ssSysCheckIntervalTextField.getText()));
            ssSysCheckIntervalErrLabel.setText("");
        } else {
            ssSysCheckIntervalErrLabel.setText(resourceBundle.getString("err_sys_check_interval_not_specified"));
            flag = false;
        }

        return flag;
    }

    private void setDisableStateForAllControls(boolean state) {
        // -- net
        snInterfaceComboBox.setDisable(state);
        snLocalPortTextField.setDisable(state);
        snRemotePortTextField.setDisable(state);
        snButtonRemotePortAdd.setDisable(state);
        snRemotePortsListView.setDisable(state);

        // -- sys
        ssSysCheckIntervalTextField.setDisable(state);

        // -- app
        apMinLogLevelComboBox.setDisable(state);
    }
}