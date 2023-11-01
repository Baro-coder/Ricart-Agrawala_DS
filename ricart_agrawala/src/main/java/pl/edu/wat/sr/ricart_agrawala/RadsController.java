package pl.edu.wat.sr.ricart_agrawala;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.control.*;
import javafx.util.converter.IntegerStringConverter;
import pl.edu.wat.sr.ricart_agrawala.core.DistributedNode;
import pl.edu.wat.sr.ricart_agrawala.core.log.LogController;
import pl.edu.wat.sr.ricart_agrawala.core.log.LogLevel;
import pl.edu.wat.sr.ricart_agrawala.core.net.NetInterface;

import java.net.URL;
import java.nio.file.AccessDeniedException;
import java.security.InvalidParameterException;
import java.security.KeyException;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class RadsController implements Initializable {
    /* Network Settings Controls */
    // Variable name prefix : sn-
    public ComboBox<String> snInterfaceComboBox;
    public ListView<Integer> snRemotePortsListView;
    public TextField snLocalPortTextField;
    public TextField snRemotePortTextField;
    public Button snButtonRemotePortAdd;
    public Button snButtonApply;
    public Button snButtonReset;
    public Label snAddressLabel;
    public Label snSubnetLabel;
    public Label snSubnetPrefixLengthLabel;
    public Label snLocalPortErrLabel;
    public Label snRemotePortsListErrLabel;

    /* System Settings Controls */
    // Variable name prefix : ss-
    public ComboBox<String> ssMinLogLevelComboBox;
    public TextField ssSysCheckIntervalTextField;
    public Button ssButtonApply;
    public Button ssButtonReset;
    public Label ssSysCheckIntervalErrLabel;

    /* Output Controls */
    // Variable name prefix : out-
    public LineChart outChart;
    public TextArea outLogTextArea;
    public ProgressBar outProgressBar;
    public Label outStatusLabel;

    /* Main Buttons */
    public Button buttonStart;
    public Button buttonStop;

    /* Others */
    IntegerStringConverter integerConverter = new IntegerStringConverter();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        DistributedNode node = DistributedNode.getInstance();

        initializeOutControls(node, resourceBundle);

        initializeNetSettingsControls(node, resourceBundle);
        initializeSysSettingsControls(node, resourceBundle);
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

        // -- Apply
        snButtonApply.setOnAction(event -> {
            NetInterface activeInterface = node.netController.getActiveInterface();
            if (activeInterface != null) {
                node.commController.setLocalAddress(activeInterface.getAddress());

                if(!snLocalPortTextField.getText().isBlank()) {
                    try {
                        node.commController.setLocalPort(Integer.parseInt(snLocalPortTextField.getText()));
                        snLocalPortErrLabel.setText("");
                    } catch (AccessDeniedException e) {
                        snLocalPortErrLabel.setText(resourceBundle.getString("err_local_port_already_in_use"));
                    } catch (InvalidParameterException e) {
                        snSubnetLabel.setText("...");
                        snAddressLabel.setText("...");
                        snSubnetPrefixLengthLabel.setText("...");
                        snLocalPortErrLabel.setText(resourceBundle.getString("err_local_port_invalid_interface"));
                    }
                } else {
                    snLocalPortErrLabel.setText(resourceBundle.getString("err_local_port_not_specified"));
                }

                if (!node.commController.getRemotePorts().isEmpty()) {
                    snRemotePortsListErrLabel.setText("");
                } else {
                    snRemotePortsListErrLabel.setText(resourceBundle.getString("err_remote_ports_list_empty"));
                }
            } else {
                snSubnetLabel.setText("...");
                snAddressLabel.setText("...");
                snSubnetPrefixLengthLabel.setText("...");
                snLocalPortErrLabel.setText(resourceBundle.getString("err_local_port_invalid_interface"));
            }
        });

        // -- Reset
        snButtonReset.setOnAction(event -> {
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
        });
    }

    private void initializeSysSettingsControls(DistributedNode node, ResourceBundle resourceBundle) {
        // LogLevel ComboBox
        for (LogLevel level : LogLevel.values()) {
            ssMinLogLevelComboBox.getItems().add(level.name());
        }
        ssMinLogLevelComboBox.setValue(ssMinLogLevelComboBox.getItems().get(1));
        node.logController.setMinLogLevel(LogLevel.valueOf(ssMinLogLevelComboBox.getValue()));
        snInterfaceComboBox.setOnAction(event -> node.logController.setMinLogLevel(LogLevel.valueOf(ssMinLogLevelComboBox.getValue())));

        // SysCheckInterval
        ssSysCheckIntervalTextField.setTextFormatter(new TextFormatter<>(integerConverter));

        // Buttons

    }

    private void initializeOutControls(DistributedNode node, ResourceBundle resourceBundle) {
        // System


        // Chart


        // Log
        node.logController.setOutLogTextArea(outLogTextArea);

        // Status


        // Main Buttons

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
}
