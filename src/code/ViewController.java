package code;

import java.io.IOException;

import javax.swing.SwingUtilities;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Main view window of the application.
 * </p>
 * Initialy displays {@code Script} and {@code MouseSetting}
 * 
 * @author Volodymyr Davybida
 */
public class ViewController extends Application {
    @FXML
    protected Button add_script_button;

    @FXML
    protected Pane base_pane;

    @FXML
    protected CheckBox global_active_switch;

    @FXML
    protected TextField mouse_LMB_KeySequence;

    @FXML
    protected CheckBox mouse_LMB_activeOnHold_check;

    @FXML
    protected CheckBox mouse_LMB_active_check;

    @FXML
    protected Pane mouse_LMB_pane;

    @FXML
    protected TextField mouse_MMB_KeySequence;

    @FXML
    protected CheckBox mouse_MMB_activeOnHold_check;

    @FXML
    protected CheckBox mouse_MMB_active_check;

    @FXML
    protected Pane mouse_MMB_pane;

    @FXML
    protected TextField mouse_RMB_KeySequence;

    @FXML
    protected CheckBox mouse_RMB_activeOnHold_check;

    @FXML
    protected CheckBox mouse_RMB_active_check;

    @FXML
    protected Pane mouse_RMB_pane;

    @FXML
    protected AnchorPane mouse_anchor_pane;

    @FXML
    protected CheckBox mouse_global_active_switch;

    @FXML
    protected Button mouse_save_button;

    @FXML
    protected Tab mouse_tab;

    @FXML
    protected VBox mouse_vbox;

    @FXML
    protected Pane script_settings_selection;

    @FXML
    protected AnchorPane scripts_pane;

    @FXML
    protected AnchorPane scripts_scroll_ancor_selection;

    @FXML
    protected ScrollPane scripts_scroll_selection;

    @FXML
    protected Tab scripts_tab;

    @FXML
    protected VBox scripts_vbox_selection;

    @FXML
    protected TabPane tab_pane;

    protected MouseEvent listeningFor;

    public static Stage mainStage;

    /**
     * Globaly available start option of the application
     */
    public static void start() {
        launch();
    }

    /**
     * Fills content of the window with {@link Main#scripts} and
     * {@link Main#mouseSettings} if found.
     * </p>
     * Creates new {@link TrayController}, forcess it to be loaded with same data
     * and links it to the main thread of application
     * </p>
     * Starts the main window.
     * 
     * @see TrayController#run()
     * @param stage
     */
    @Override
    public void start(Stage stage) {
        try {
            Parent main_view = Main.main_view_fxmlLoader.load();
            mainStage = stage;
            Main.viewController = this;
            Platform.setImplicitExit(false);
            SwingUtilities.invokeLater(Main.tray);

            stage.setScene(new Scene(main_view));
            setDefaultPreferenses(stage);
            if (!Main.scripts.isEmpty()) {
                FXMLLoader main_view_loader = Main.main_view_fxmlLoader;
                ViewController main_viewController = main_view_loader.getController();
                for (Script iterable : Main.scripts) {
                    main_viewController.addScriptToList(iterable);
                    Main.tray.addKeySetting(iterable);
                }
            }
            if (!Main.mouseSettings.isEmpty()) {
                FXMLLoader main_view_loader = Main.main_view_fxmlLoader;
                ViewController main_viewController = main_view_loader.getController();
                main_viewController.setMouseSettings();
                Main.mouseSettings.forEach(x -> {
                    if (x.getActivationSequence() != null) {
                        Main.tray.addMouseSetting(x);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets the mian and global settings for the view
     * 
     * @param stage
     */
    private static void setDefaultPreferenses(Stage stage) {
        stage.getIcons().add(new Image("/resourses/Key_Klicker_Logo.jpg"));
        stage.setTitle("Key Klicker");
        stage.setResizable(false);
        stage.show();
    }

    /**
     * Sets the global {@link KeyListener} of the application.
     * </p>
     * Linked to {@code global_active_switch}
     */
    @FXML
    void setGlobalListener() {
        if (global_active_switch.isSelected()) {
            Main.keyListener.setGlobalyActive(true);
            Main.keyListener.startGlobalKeyListening();
            TrayController.keyGlobalyActiveCheck.setState(true);
        } else {
            Main.keyListener.setGlobalyActive(false);
            Main.keyListener.stopGlobalKeyListening();
            TrayController.keyGlobalyActiveCheck.setState(false);
        }
    }

    /**
     * Removes any pane from {@code script_settings_window} and replaces it with
     * empty script window
     * </p>
     * Linked to {@code add_script_button}
     */
    @FXML
    protected void displayNewScriptWindow() {
        try {
            FXMLLoader script_settings_loader = new FXMLLoader(getClass().getResource("/resourses/script_settings_window.fxml"));
            AnchorPane script_settings_window = script_settings_loader.load();
            script_settings_window.setId(String.valueOf(Main.getRandomNumber()));
            if (script_settings_selection.getChildren().size() == 1) {
                script_settings_selection.getChildren().remove(0);
            }
            script_settings_selection.getChildren().add(script_settings_window);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initial function that loads {@code script_card.fxml}, creates new
     * {@code ScriptCardController} for each one and adds it to
     * {@code scripts_vbox_selection}.
     * </p>
     * Sets the backgroun changing properites.
     * 
     * @param script to be added
     */
    public void addScriptToList(Script script) {
        try {
            FXMLLoader script_card_loader = new FXMLLoader(getClass().getResource("/resourses/script_card.fxml"));
            AnchorPane script_card = script_card_loader.load();
            script_card.setId(String.valueOf(Main.getRandomNumber()));
            ScriptCardController controller = script_card_loader.getController();

            if (scripts_vbox_selection.getChildren().size() % 2 != 0) {
                script_card.setStyle(" -fx-background-color: #201c1c;");
                script_card.setOnMouseExited(new EventHandler<Event>() {
                    @Override
                    public void handle(Event arg0) {
                        script_card.setStyle("-fx-background-color: #201c1c;");
                    }
                });
            } else {
                script_card.setStyle(" -fx-background-color: transparent;");
                script_card.setOnMouseExited(new EventHandler<Event>() {

                    @Override
                    public void handle(Event arg0) {
                        script_card.setStyle("-fx-background-color: transparent;");
                    }
                });
            }

            script_card.setOnMouseEntered(new EventHandler<Event>() {
                @Override
                public void handle(Event arg0) {
                    script_card.setStyle("-fx-background-color: #383434;");
                }
            });

            controller.setScript(script);

            scripts_vbox_selection.getChildren().add(1, script_card);
        } catch (

        IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * </p>
     * Overrides {@link Application#stop()} in order for application to continue
     * working but with its stage hidden.
     * 
     * @see Application#stop()
     */
    @Override
    public void stop() throws Exception {
        mainStage.hide();
    }

    /**
     * Loads {@link FXMLLoader} for selected script to be displayed and sets it to
     * the {@code script_settings_selection}.
     * 
     * @param script to be displayed
     */
    public void displayScriptWindow(Script script) {
        try {
            FXMLLoader script_settings_window_loader = new FXMLLoader(getClass().getResource("/resourses/script_settings_window.fxml"));
            AnchorPane script_settings_window = script_settings_window_loader.load();
            ScriptSettingsWindowContoller script_setting_window_controller = script_settings_window_loader.getController();
            script_setting_window_controller.setScript(script);
            script_settings_selection.getChildren().add(script_settings_window);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateScriptList() {
        int times = scripts_vbox_selection.getChildren().size();
        for (int i = 1; i < times; i++) {
            scripts_vbox_selection.getChildren().remove(1);
        }
        for (Script iterable : Main.scripts) {
            addScriptToList(iterable);
        }
    }

    /**
     * Sets the background of pane when mouse is hovered in order to highlight it.
     * 
     * @see ViewController#unhighlight(MouseEvent)
     * @param event
     */
    @FXML
    void highlight(MouseEvent event) {
        Pane pane = (Pane) event.getSource();
        pane.setStyle("-fx-background-color: #383434;");
    }

    /**
     * Sets the background of pane when mouse is hovered in order to unhighlight it.
     * 
     * @see ViewController#highlight(MouseEvent)
     * @param event
     */
    @FXML
    void unhighlight(MouseEvent event) {
        Pane pane = (Pane) event.getSource();
        if (pane.getId().equals("mouse_LMB_pane")) {
            pane.setStyle("-fx-background-color: #201c1c;");
        } else {
            pane.setStyle("-fx-background-color: transparent;");
        }
    }

    /**
     * Linked to {@code mouse_save_button}
     * </p>
     * Getts all mouse setting data from pane and updates
     * {@code Main#mouseSettings}, {@code Main#tray} and {@code mouse} file within
     * {@code data} folder
     * 
     * @see FileOperator#saveMouseData()
     * @see TrayController#updateMousesettings()
     * @param event
     */
    @FXML
    void saveMouseData(ActionEvent event) {
        if (Main.mouseSettings.size() > 0) {
            Main.mouseSettings.get(0).setActive(mouse_RMB_active_check.isSelected());
            Main.mouseSettings.get(0).setActiveOnHold(mouse_RMB_activeOnHold_check.isSelected());
            Main.mouseSettings.get(0).setactivationSequence(mouse_RMB_KeySequence.getText().replaceAll("[ ]$", "").replaceAll(" ", "."));
        }
        if (Main.mouseSettings.size() > 1) {
            Main.mouseSettings.get(1).setActive(mouse_LMB_active_check.isSelected());
            Main.mouseSettings.get(1).setActiveOnHold(mouse_LMB_activeOnHold_check.isSelected());
            Main.mouseSettings.get(1).setactivationSequence(mouse_LMB_KeySequence.getText().replaceAll("[ ]$", "").replaceAll(" ", "."));
        }
        if (Main.mouseSettings.size() > 1) {
            Main.mouseSettings.get(2).setActive(mouse_MMB_active_check.isSelected());
            Main.mouseSettings.get(2).setActiveOnHold(mouse_MMB_activeOnHold_check.isSelected());
            Main.mouseSettings.get(2).setactivationSequence(mouse_MMB_KeySequence.getText().replaceAll("[ ]$", "").replaceAll(" ", "."));
        }

        FileOperator.saveMouseData();
        Main.tray.updateMousesettings();
    }

    /**
     * Liked to {@code mouse_global_active_switch}
     * </p>
     * Sets the global {@link MouseListener} of the application.
     * 
     * @param event
     */
    @FXML
    void setGlobalMouseListener(ActionEvent event) {
        if (mouse_global_active_switch.isSelected()) {
            Main.mouseListener.setGlobalyActive(true);
            Main.mouseListener.startGlobalMouseKeyListening();
            TrayController.mouseGlobalyActiveCheck.setState(true);
        } else {
            Main.mouseListener.setGlobalyActive(false);
            Main.mouseListener.stopGlobalMouseKeyListening();
            TrayController.mouseGlobalyActiveCheck.setState(false);
        }
    }

    /**
     * Setts mouse setting for main window
     * 
     * @see ViewController#start(Stage)
     */
    public void setMouseSettings() {
        mouse_RMB_active_check.setSelected(Main.mouseSettings.get(0).isActive());
        mouse_RMB_activeOnHold_check.setSelected(Main.mouseSettings.get(0).isActiveOnHold());
        if (Main.mouseSettings.get(0).getActivationSequence() != null) {
            mouse_RMB_KeySequence.setText(Main.mouseSettings.get(0).getActivationSequence().replace(".", " "));
        }

        mouse_LMB_active_check.setSelected(Main.mouseSettings.get(1).isActive());
        mouse_LMB_activeOnHold_check.setSelected(Main.mouseSettings.get(1).isActiveOnHold());
        if (Main.mouseSettings.get(1).getActivationSequence() != null) {
            mouse_LMB_KeySequence.setText(Main.mouseSettings.get(1).getActivationSequence().replace(".", " "));
        }

        mouse_MMB_active_check.setSelected(Main.mouseSettings.get(2).isActive());
        mouse_MMB_activeOnHold_check.setSelected(Main.mouseSettings.get(2).isActiveOnHold());
        if (Main.mouseSettings.get(2).getActivationSequence() != null) {
            mouse_MMB_KeySequence.setText(Main.mouseSettings.get(2).getActivationSequence().replace(".", " "));
        }

    }

    /**
     * Setts Actiovation sequence for selected setting
     * 
     * @see KeyListener#listenForKeyCombination(ViewController)
     * @see ViewController#askKeySequense(MouseEvent)
     * @param string
     */
    public void setKeyCombination(String string) {
        TextField textF = (TextField) listeningFor.getSource();
        textF.setText(string);
        KeyListener.listeningForViewController = null;
        listeningFor = null;
    }

    /**
     * Asks {@link KeyListener} to start listening for key Combination input.
     * 
     * @see KeyListener#listenForKeyCombination(ViewController)
     * @see ViewController#setKeyCombination(String)
     * @param event
     */
    @FXML
    void askKeySequense(MouseEvent event) {
        listeningFor = event;
        Main.keyListener.listenForKeyCombination(this);
    }

    public String getTitle() {
        return mainStage.getTitle();
    }

    protected static void showStage() {
        if (mainStage != null) {
            mainStage.show();
            mainStage.toFront();
        }
    }

    /**
     * Updates {@code Tray}s key listener switch state
     */
    public static void updateKeySwitchState() {
        ViewController controller = Main.main_view_fxmlLoader.getController();
        if (TrayController.keyGlobalyActiveCheck.getState()) {
            controller.global_active_switch.setSelected(true);
        } else {
            controller.global_active_switch.setSelected(false);
        }

    }

    /**
     * Updates {@code Tray}s mouse key listener switch state
     */
    public static void updateMouseSwitchState() {
        ViewController controller = Main.main_view_fxmlLoader.getController();
        if (TrayController.mouseGlobalyActiveCheck.getState()) {
            controller.mouse_global_active_switch.setSelected(true);
        } else {
            controller.mouse_global_active_switch.setSelected(false);
        }
    }

    /**
     * Update wiews mouse setting accessed from
     * {@link TrayController#updateMousesettings()}
     */
    public static void updateMouseSettings() {
        ViewController controller = Main.main_view_fxmlLoader.getController();
        controller.setMouseSettings();
    }

    /**
     * Update wiews key setting accessed from
     * {@link TrayController#updateKeySettings()}
     */
    public static void staticUpdateScriptList() {
        ViewController controller = Main.main_view_fxmlLoader.getController();
        controller.updateScriptList();
    }
}
