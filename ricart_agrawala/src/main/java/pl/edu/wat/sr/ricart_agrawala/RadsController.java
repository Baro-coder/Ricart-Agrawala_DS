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
import java.util.Locale;
import java.util.ResourceBundle;

public class RadsController implements Initializable {
    /* Logic link */
    private DistributedNode node;

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
    // Variable name prefix : sa-
    public Tab saTab;
    public ComboBox<String> saLangComboBox;

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
        node = DistributedNode.getInstance();
        node.resourceController.setResourceBundle(resourceBundle);

        initializeOutControls();
        initializeAppSettingsControls();
        initializeSysSettingsControls();
        initializeNetSettingsControls();
        initializeButtons();
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

    private void initializeAppSettingsControls() {
        // TODO: Set up the control to change application theme (DARK / LIGHT)
        // TODO: Set up the control to change application language (PL / EN)
        // Language
        saLangComboBox.getItems().addAll(node.resourceController.getLanguagesTagsStrings());
        saLangComboBox.setValue(saLangComboBox.getItems().get(
                saLangComboBox.getItems().indexOf(
                        node.resourceController.getLanguageTagByCountryCode(
                                Locale.getDefault().toString()
                        ).name().toLowerCase()
                )
        ));
        saLangComboBox.setOnAction(event -> {
            node.resourceController.setResourceBundleLang(
                    node.resourceController.getLanguageTagByCountryCode(saLangComboBox.getValue())
            );
        });
    }

    private void initializeSysSettingsControls() {
        // SysCheckInterval
        ssSysCheckIntervalTextField.setTextFormatter(new TextFormatter<>(integerConverter));
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
                snRemotePortsListErrLabel.setText(node.resourceController.getText("err_label_remote_ports_already_provided"));
            }
        });
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
            snLocalPortErrLabel.setText(node.resourceController.getText("err_label_empty"));

            // ---- remote ports
            snRemotePortTextField.clear();
            snRemotePortsListErrLabel.setText(node.resourceController.getText("err_label_empty"));
            snRemotePortsListView.getItems().clear();
            node.commController.clearRemotePorts();

            // ---- net check interval
            snInterfacesUpdateIntervalTextField.clear();
            snInterfacesUpdateIntervalErrLabel.setText(node.resourceController.getText("err_label_empty"));
            node.netController.setUpdateInterval(-1);

            // -- Sys Settings
            // ---- sys check interval
            ssSysCheckIntervalTextField.clear();
            ssSysCheckIntervalErrLabel.setText(node.resourceController.getText("err_label_empty"));
            node.sysController.setSysCheckInterval(-1);

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
                outStateLabel.setText(node.resourceController.getText("state_invalid"));
                outStateCircle.setFill(Color.RED);
            } case VALID -> {
                outStateLabel.setText(node.resourceController.getText("state_valid"));
                outStateCircle.setFill(Color.YELLOW);
            } case READY -> {
                outStateLabel.setText(node.resourceController.getText("state_ready"));
                outStateCircle.setFill(Color.GREEN);
            } case WORKING -> {
                outStateLabel.setText(node.resourceController.getText("state_working"));
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
                    snLocalPortErrLabel.setText(node.resourceController.getText("err_label_empty"));
                } catch (AccessDeniedException e) {
                    snLocalPortErrLabel.setText(node.resourceController.getText("err_label_local_port_already_in_use"));
                    flag = false;
                } catch (InvalidParameterException e) {
                    snSubnet.clear();
                    snAddress.clear();
                    snMask.clear();
                    snLocalPortErrLabel.setText(node.resourceController.getText("err_label_local_port_invalid_interface"));
                    flag = false;
                }
            } else {
                snLocalPortErrLabel.setText(node.resourceController.getText("err_label_local_port_not_specified"));
                flag = false;
            }

            if (!node.commController.getRemotePorts().isEmpty()) {
                snRemotePortsListErrLabel.setText(node.resourceController.getText("err_label_empty"));
            } else {
                snRemotePortsListErrLabel.setText(node.resourceController.getText("err_label_remote_ports_list_empty"));
                flag = false;
            }

            if (!snInterfacesUpdateIntervalTextField.getText().isBlank()) {
                node.netController.setUpdateInterval(Integer.parseInt(snInterfacesUpdateIntervalTextField.getText()));
                snInterfacesUpdateIntervalErrLabel.setText(node.resourceController.getText("err_label_empty"));
            } else {
                snInterfacesUpdateIntervalErrLabel.setText(node.resourceController.getText("err_label_value_not_specified"));
            }
        } else {
            snSubnet.clear();
            snAddress.clear();
            snMask.clear();
            snLocalPortErrLabel.setText(node.resourceController.getText("err_label_local_port_invalid_interface"));
            flag = false;
        }

        return flag;
    }
    private boolean validateAndApplySystemSettings() {
        boolean flag = true;

        if(!ssSysCheckIntervalTextField.getText().isBlank()) {
            node.sysController.setSysCheckInterval(Integer.parseInt(ssSysCheckIntervalTextField.getText()));
            ssSysCheckIntervalErrLabel.setText(node.resourceController.getText("err_label_empty"));
        } else {
            ssSysCheckIntervalErrLabel.setText(node.resourceController.getText("err_label_value_not_specified"));
            flag = false;
        }

        return flag;
    }

    private void setDisableStateForAllControls(boolean state) {
        //-- containers
        // paneSettingsSection.setDisable(state);
        snTab.setDisable(state);
        ssTab.setDisable(state);
        saTab.setDisable(state);
    }
}
