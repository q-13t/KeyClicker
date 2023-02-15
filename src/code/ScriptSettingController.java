package code;

import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TouchEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 * Controller Class for Script Setting
 * 
 * @see ScriptSettingsWindowContoller
 * @author Volodymyr Davybida
 */
public class ScriptSettingController {

    @FXML
    private MenuItem menue_fifty_times;

    @FXML
    private Button remove_setting_button;

    @FXML
    private MenuItem menue_five_times;

    @FXML
    private MenuItem menue_hundered_times;

    @FXML
    private MenuItem menue_one_time;

    @FXML
    private MenuItem menue_ten_times;

    @FXML
    private MenuButton menue_times_repeat;

    @FXML
    private MenuItem menue_twenty_times;

    @FXML
    private MenuItem menue_twentyfive_times;

    @FXML
    private MenuItem menue_two_times;

    @FXML
    private MenuItem menue_twohundered_times;

    @FXML
    private TextField script_activation_delay_field;

    @FXML
    private TextField script_setting_activation_key;

    @FXML
    private Pane script_setting_pane;

    @FXML
    private TextField script_times_repeated_user_input;

    @FXML
    private CheckBox times_repeated_infinitely;

    private Pane pane;

    private ScriptSettingsWindowContoller windowContoller;

    public static final ChangeListener<String> NUMERIC_TEXT_INPUT_LISTENER = new ChangeListener<String>() {

        @Override
        public void changed(ObservableValue<? extends String> property, String before, String after) {
            StringProperty str_property = (StringProperty) property;
            str_property.setValue(after.replaceAll("^0*", ""));
            if (!after.equalsIgnoreCase("infinite")) {
                if (after.matches("") && !before.matches("")) {
                    str_property.setValue(after);
                } else if (!after.matches("^[0-9]+$")) {
                    str_property.setValue(before);
                } else if (Integer.parseInt(after.replaceAll("[^\\d]", "")) > Integer.MAX_VALUE) {
                    str_property.setValue(Integer.MAX_VALUE + "");
                }
            }
        }
    };

    /**
     * Linked to {@code times_repeated_infinitely}, {@code menue_times_repeat} (and
     * children), {@code script_times_repeated_user_input}
     * </p>
     * Setts specified by {@code event} sourse number of times specific
     * {@link ScriptCommand} will repeat.
     * 
     * @param event
     */
    @FXML
    void defineTimes(ActionEvent event) {
        String event_from = event.getSource().getClass().getSimpleName();
        switch (event_from) {
        case "CheckBox": {
            if (times_repeated_infinitely.isSelected()) {
                menue_times_repeat.setDisable(true);
                script_times_repeated_user_input.setDisable(true);
                script_times_repeated_user_input.setText("Infinite");
            } else {
                menue_times_repeat.setDisable(false);
                script_times_repeated_user_input.setDisable(false);
                script_times_repeated_user_input.setText("1");
            }
            break;
        }
        case "MenuItem": {
            script_times_repeated_user_input.setText(((MenuItem) event.getSource()).getText());
            break;
        }
        case "TextField": {

            break;
        }

        default: {
            System.out.println("UNRESOLVED-> " + event_from);
            break;
        }
        }
    }

    @FXML
    void touchReleased(TouchEvent event) {
        event.consume();
    }

    @FXML
    void KeyReleased(KeyEvent event) {
        event.consume();
    }

    @FXML
    void KeyTyped(KeyEvent event) {
        event.consume();
    }

    @FXML
    void DragDetected(MouseEvent event) {
        System.out.println("Pane Id -> " + pane.getId());
        pane.startFullDrag();
        event.consume();
    }

    /**
     * Setts specific position of {@code pane} while dragging.
     * 
     * @see ScriptSettingsWindowContoller#setSettingPosition(Pane, MouseDragEvent)
     * @param event
     */
    @FXML
    void MouseDragEntered(MouseDragEvent event) {
        windowContoller.setSettingPosition(pane, event);
        event.consume();
    }

    @FXML
    void MouseDragReleased(MouseDragEvent event) {
        pane.setStyle("");
        event.consume();
    }

    /**
     * Linked to {@code script_setting_activation_key}
     * </p>
     * Asks {@link KeyListener} to start listening for single key input.
     * 
     * @see #setKey(String)
     * @see KeyListener#listenForSingleKey(ScriptSettingController)
     */
    @FXML
    void askKey() {
        Main.keyListener.listenForSingleKey(this);
    }

    /**
     * Setts provided by {@link KeyListener} key to corresponding
     * {@link ScriptSetting}.
     * 
     * @see #askKey()
     * @see KeyListener#listenForSingleKey(ScriptSettingController)
     * @param keyText
     */
    public void setKey(String keyText) {
        this.script_setting_activation_key.setText(keyText);
        Main.keyListener.stopListeningForSingleKey();
        KeyListener.listeningForScriptSetting = null;
    }

    /**
     * Linked to {@code remove_setting_button}
     * </p>
     * Removes corresponding {@link ScriptSetting from selected {@link Script}.
     * 
     * @see ScriptSettingsWindowContoller#addNewSetting(ActionEvent)
     */
    @FXML
    void removeSetting() {
        VBox vbox = (VBox) remove_setting_button.getParent().getParent();
        vbox.getChildren().remove(remove_setting_button.getParent());
    }

    public void setTimes(int times) {
        if (times == -1) {
            this.times_repeated_infinitely.setSelected(true);
            this.menue_times_repeat.setDisable(true);
            this.script_times_repeated_user_input.setDisable(true);
            this.script_times_repeated_user_input.setText("Infinite");
        } else {
            this.times_repeated_infinitely.setSelected(false);
            this.menue_times_repeat.setDisable(false);
            this.script_times_repeated_user_input.setDisable(false);
            this.script_times_repeated_user_input.setText(String.valueOf(times));
        }
    }

    private void setDelay(int delay) {
        this.script_activation_delay_field.setText(String.valueOf(delay));
    }

    /**
     * Parsess provided {@link ScriptCommand}, and adds it to corresponding setting
     * in window
     * 
     * @see #setKey(String)
     * @see #setTimes(int)
     * @see #setDelay(int)
     * @param scriptCommand
     */
    public void setAction(ScriptCommand scriptCommand) {
        setKey(scriptCommand.getKey());
        setTimes(scriptCommand.getRepeatTimes());
        setDelay(scriptCommand.getDelay());
    }

    public void setLoader(ScriptSettingsWindowContoller scriptSettingsWindowContoller) {
        this.windowContoller = scriptSettingsWindowContoller;
    }

    public void setPane(Pane pane) {
        this.pane = pane;
    }

    @FXML
    void initialize() {
    }

    /**
     * Setts listeners for {@code script_times_repeated_user_input} and
     * {@code script_activation_delay_field} in order to recieve only correct
     * numeric inputs.
     */
    public void setTextCheckers() {
        this.script_times_repeated_user_input.textProperty().addListener(NUMERIC_TEXT_INPUT_LISTENER);
        this.script_activation_delay_field.textProperty().addListener(NUMERIC_TEXT_INPUT_LISTENER);
    }
}
