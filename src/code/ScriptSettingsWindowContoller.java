package code;

import java.util.ArrayList;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.text.Text;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 * Controller Class for Script Settings Window
 * 
 * @see ViewController
 * @author Volodymyr Davybida
 */
public class ScriptSettingsWindowContoller {

    @FXML
    private Button add_script_action_button;

    @FXML
    private TextField key_activation_sequence_user_input;

    @FXML
    private Button save_script_button;

    @FXML
    private ScrollPane script_settings_scroll;

    @FXML
    private AnchorPane scripts_settings_ancor_selection;

    @FXML
    private VBox scripts_settings_vbox;

    @FXML
    private TextField user_input_name;

    private Script script;

    /**
     * Linked to {@code save_script_button}
     * </p>
     * Saves new {@link Script} or updates existing. And updates Scripts data within
     * applications {@link TrayController} aswell as {@link ViewController}.
     * 
     * @see FileOperator#saveScript(Script)
     */
    @FXML
    void saveNewScript() {
        try {
            if (this.script != null) {
                FileOperator.deleteScript(script);
                Main.scripts.remove(script);
            }
            String scriptName = user_input_name.getText();

            if (scriptName == "") {
                scriptName = "UNNAMED";
            }
            FXMLLoader main_view_loader = Main.main_view_fxmlLoader;
            ViewController main_viewController = main_view_loader.getController();

            AnchorPane selected_script_pane = Main.selected_script_view;

            if (this.script == null) {
                this.script = new Script();
            }
            script.setName(scriptName);
            script.setActivationSequence(key_activation_sequence_user_input.getText());
            script.setActions(scripts_settings_vbox);

            FileOperator.saveScript(script);
            Main.scripts.add(script);
            Main.tray.updateKeySettings();
            main_viewController.updateScriptList();

            if (selected_script_pane != null) {
                ((Text) selected_script_pane.getChildren().get(1)).setText(scriptName);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Linked to {@code add_script_action_button}
     * </p>
     * Adds new {@link ScriptCommand} linked to {@link ScriptSettingController} to
     * the selected script
     * </p>
     * Calls {@link #reEvaluateBackground()}.
     * 
     * @see ScriptSettingController#removeSetting()
     * @param event
     */
    @FXML
    void addNewSetting(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/resourses/script_setting.fxml"));
            Pane setting = loader.load();
            ScriptSettingController script_setting_controller = loader.getController();
            setting.setId(String.valueOf(Main.getRandomNumber()));
            script_setting_controller.setLoader(this);
            script_setting_controller.setPane(setting);
            scripts_settings_vbox.getChildren().add(1, setting);
            script_setting_controller.setTextCheckers();
            reEvaluateBackground();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Linked to {@code key_activation_sequence_user_input}
     * </p>
     * Requests {@link KeyListener} for key activetaion sequense to be listened.
     * 
     * @see KeyListener#listenForKeyCombination(ScriptSettingsWindowContoller)
     */
    @FXML
    void askKeySequense() {
        Main.keyListener.listenForKeyCombination(this);
    }

    /**
     * Setts provided by
     * {@link KeyListener#listenForKeyCombination(ScriptSettingsWindowContoller)}
     * key combindation to the text field.
     * 
     * @see #askKeySequense()
     * @param combo
     */
    public void setKeyCombination(String combo) {
        key_activation_sequence_user_input.setText(combo);
        KeyListener.listeningForScriptSettingWindow = null;
    }

    /**
     * Setts selected {@link Script} and its settings to active window.
     * 
     * @see ViewController#displayScriptWindow(Script)
     * @param script
     */
    public void setScript(Script script) {
        setName(script.getName());
        setKeyCombination(script.getActivationSequence());
        setSettings(script.getActions());
        this.script = script;

    }

    /**
     * Parsess provided {@link ScriptCommand}s into local window modules.
     * </p>
     * Calls {@link #reEvaluateBackground()}.
     * 
     * @see #setScript(Script)
     * @param actions
     */
    private void setSettings(ArrayList<ScriptCommand> actions) {
        for (ScriptCommand scriptCommand : actions) {
            try {
                FXMLLoader script_setting_loader = new FXMLLoader(getClass().getResource("/resourses/script_setting.fxml"));
                Pane script_setting_pane = script_setting_loader.load();
                ScriptSettingController script_setting_controller = script_setting_loader.getController();
                script_setting_pane.setId(String.valueOf(Main.getRandomNumber()));

                script_setting_controller.setLoader(this);
                script_setting_controller.setPane(script_setting_pane);
                script_setting_controller.setAction(scriptCommand);
                scripts_settings_vbox.getChildren().add(script_setting_pane);
                script_setting_controller.setTextCheckers();
                reEvaluateBackground();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setName(String name) {
        user_input_name.setText(name);
    }

    /**
     * Setts specified possition of {@link ScriptCommand} in hiearchy.
     * </p>
     * Calls {@link #reEvaluateBackground()}.
     * 
     * @see ScriptSettingController#MouseDragEntered(MouseDragEvent)
     * @param pane
     * @param event
     */
    public void setSettingPosition(Pane pane, MouseDragEvent event) {
        int indexOfDraggingNode = scripts_settings_vbox.getChildren().indexOf(event.getGestureSource());
        int indexOfDropTarget = scripts_settings_vbox.getChildren().indexOf(pane);
        System.out.println("From " + indexOfDraggingNode + " To " + indexOfDropTarget);
        if (indexOfDraggingNode >= 0 && indexOfDropTarget >= 1) {
            final Node node = scripts_settings_vbox.getChildren().remove(indexOfDraggingNode);
            scripts_settings_vbox.getChildren().add(indexOfDropTarget, node);
        }
        reEvaluateBackground();
    }

    /**
     * Setts coresponding background colour to position of each
     * {@link ScriptSetting} within active window.
     * 
     * @see #setSettingPosition(Pane, MouseDragEvent)
     * @see #setSettings(ArrayList)
     * @see #addNewSetting(ActionEvent)
     */
    public void reEvaluateBackground() {
        for (int i = 1; i < scripts_settings_vbox.getChildren().size(); i++) {
            if (i % 2 != 0) {
                scripts_settings_vbox.getChildren().get(i).setStyle(" -fx-background-color: #201c1c;");
                scripts_settings_vbox.getChildren().get(i).setOnMouseExited(new EventHandler<Event>() {
                    @Override
                    public void handle(Event arg0) {
                        Pane pane = (Pane) arg0.getSource();
                        pane.setStyle("-fx-background-color: #201c1c;");
                    }
                });
            } else {
                scripts_settings_vbox.getChildren().get(i).setStyle(" -fx-background-color: transparent;");
                scripts_settings_vbox.getChildren().get(i).setOnMouseExited(new EventHandler<Event>() {
                    @Override
                    public void handle(Event arg0) {
                        Pane pane = (Pane) arg0.getSource();
                        pane.setStyle("-fx-background-color: transparent;");
                    }
                });
            }
            scripts_settings_vbox.getChildren().get(i).setOnMouseEntered(new EventHandler<Event>() {
                @Override
                public void handle(Event arg0) {
                    Pane pane = (Pane) arg0.getSource();
                    pane.setStyle("-fx-background-color: #383434;");
                }
            });
        }

    }

    public VBox getVBox() {
        return this.scripts_settings_vbox;
    }
}
