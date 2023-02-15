
package code;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * Controller class for Script card.
 * 
 * @see ViewController
 * @author Volodymyr Davybida
 */
public class ScriptCardController {

    @FXML
    private Button DELETE_delete_button;

    @FXML
    private Button NO_delete_button;

    @FXML
    private Button YES_delete_button;

    @FXML
    private AnchorPane base_anchor;

    @FXML
    private Text script_activation_sequence;

    @FXML
    private CheckBox script_active;

    @FXML
    private Text script_name;

    private Script script;

    /**
     * Linked to {@code pane itself}
     * </p>
     * Displays corresponding {@link Script} within {@link ViewController}.
     * 
     * @see ViewController#displayScriptWindow(Script)
     * @param event
     */
    @FXML
    void displaySelectedScript(MouseEvent event) {
        FXMLLoader main_view_loader = Main.main_view_fxmlLoader;
        ViewController main_viewController = main_view_loader.getController();

        main_viewController.displayScriptWindow(script);

        Main.selected_script_view = base_anchor;
    }

    /**
     * Linked to {@code script_active}
     * </p>
     * Setts if {@link Script} is active or not
     * </p>
     * 
     * @see Script#setActive(boolean)
     * @see FileOperator#saveScript(Script)
     * @see TrayController#updateKeySettings()
     */
    @FXML
    void setScriptActive() {
        if (script_active.isSelected()) {
            script.setActive(true);
        } else {
            script.setActive(false);
        }
        FileOperator.saveScript(script);
        Main.tray.updateKeySettings();
    }

    /**
     * Linked to {@code DELETE_delete_button}
     * </p>
     * Only layer of protection against mistaken deleting of script
     * 
     * @see #YES_button()
     * @see #NO_button()
     */
    @FXML
    protected void DELETE_button() {
        DELETE_delete_button.setVisible(false);
        YES_delete_button.setVisible(true);
        NO_delete_button.setVisible(true);
    }

    /**
     * Linked to {@code YES_delete_button}
     * </p>
     * Deletes corresopnding script from {@code data} folder and tray.
     * 
     * @see FileOperator#deleteScript(Script)
     * @see TrayController#updateKeySettings()
     */
    @FXML
    protected void YES_button() {
        VBox vbox = (VBox) YES_delete_button.getParent().getParent();
        FileOperator.deleteScript(script);
        vbox.getChildren().remove(YES_delete_button.getParent());
        Main.scripts.remove(script);
        Main.tray.updateKeySettings();
    }

    /**
     * Linked to {@code NO_delete_button}
     * </p>
     * Returns original {@code DELETE_delete_button} to the view.
     */
    @FXML
    protected void NO_button() {
        DELETE_delete_button.setVisible(true);
        YES_delete_button.setVisible(false);
        NO_delete_button.setVisible(false);
    }

    /**
     * Setts {@link Script}s data to the view.
     * 
     * @param script
     */
    public void setScript(Script script) {
        this.script = script;
        this.script_name.setText(script.getName());
        this.script_activation_sequence.setText(script.getActivationSequence());
        this.script_active.setSelected(script.isActive());
    }
}
