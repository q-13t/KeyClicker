package code;

import javafx.application.Platform;
import java.awt.*;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * This class creates and controlls (if supported) tray of the application
 * 
 * @author Volodymyr Davybida
 */
public class TrayController implements Runnable {
    private Image image;
    Font font = new Font("System Regular", Font.BOLD, 12);
    public static TrayIcon trayIcon;
    public static PopupMenu popup = new PopupMenu();
    public static SystemTray tray = SystemTray.getSystemTray();
    protected static Menu mouseSettings = new Menu("Mouse Scripts");
    protected static Menu keySettings = new Menu("Key Scripts");
    protected static CheckboxMenuItem mouseGlobalyActiveCheck = new CheckboxMenuItem("Mouse Listener");
    protected static CheckboxMenuItem keyGlobalyActiveCheck = new CheckboxMenuItem("Key Listener");
    protected static MenuItem exit = new MenuItem("exit");

    /**
     * Checks if system tray is supported
     * </p>
     * If so tray is created and filled with asociated data
     * 
     * @see ViewController#start(Stage stage)
     */
    @Override
    public void run() {
        if (tray.getTrayIcons().length != 0) {
            return;
        }
        if (!SystemTray.isSupported()) {
            System.out.println("SystemTray is not supported");
            return;
        }
        try {
            image = ImageIO.read(getClass().getResource("/resourses/Key_Klicker_Logo.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        trayIcon = new TrayIcon(image);
        trayIcon.setImageAutoSize(true);
        MenuItem home = new MenuItem(Main.viewController.getTitle());
        home.setFont(font);
        popup.add(home);
        home.addActionListener((event) -> {
            Platform.runLater(ViewController::showStage);
        });
        keyGlobalyActiveCheck.addItemListener((event) -> {
            if (keyGlobalyActiveCheck.getState()) {
                Main.keyListener.setGlobalyActive(true);
                Main.keyListener.startGlobalKeyListening();
                Platform.runLater(ViewController::updateKeySwitchState);
            } else {
                Main.keyListener.setGlobalyActive(false);
                Main.keyListener.stopGlobalKeyListening();
                Platform.runLater(ViewController::updateKeySwitchState);
            }
        });
        popup.add(keyGlobalyActiveCheck);
        popup.add(keySettings);
        mouseGlobalyActiveCheck.addItemListener((event) -> {
            if (mouseGlobalyActiveCheck.getState()) {
                Main.mouseListener.setGlobalyActive(true);
                Main.mouseListener.startGlobalMouseKeyListening();
                Platform.runLater(ViewController::updateMouseSwitchState);
            } else {
                Main.mouseListener.setGlobalyActive(false);
                Main.mouseListener.stopGlobalMouseKeyListening();
                Platform.runLater(ViewController::updateMouseSwitchState);
            }
        });
        popup.add(mouseGlobalyActiveCheck);
        popup.add(mouseSettings);
        exit.addActionListener((event) -> {
            System.out.println("Exiting");
            Main.keyListener.stopAllListeners();
            Main.keyListener.removeNativeHook();
            Main.keyListener.stopAllThreads();
            Main.mouseListener.stopAllListeners();
            Main.mouseListener.removeNativeHook();
            Main.mouseListener.stopAllThreads();
            System.exit(0);
        });
        exit.setFont(font);
        popup.add(exit);
        trayIcon.setPopupMenu(popup);
        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds {@link MouseSetting} to the tray.
     * </p>
     * Each setting has listener for updating data correspondingly
     * 
     * @see FileOperator#saveMouseData()
     * @see ViewController#updateMouseSettings()
     * @param setting
     */
    public void addMouseSetting(MouseSetting setting) {
        CheckboxMenuItem item = new CheckboxMenuItem(setting.getName() + " [" + setting.getActivationSequence().replaceAll("[.]", " ") + "]");
        item.addItemListener(e -> {
            Main.mouseSettings.forEach(x -> {
                if (item.getLabel().contains(x.getName())) {
                    x.setActive(item.getState());
                    FileOperator.saveMouseData();
                    Platform.runLater(ViewController::updateMouseSettings);
                }
            });
            System.out.println(item.getLabel() + " -> " + item.getState());
        });
        item.setState(setting.isActive());
        mouseSettings.add(item);
    }

    /**
     * Updates {@link MouseSetting} of active tray.
     * </p>
     * Each setting has listener for updating data correspondingly
     * 
     * @see FileOperator#saveMouseData()
     * @see ViewController#updateMouseSettings()
     * @param setting
     */
    public void updateMousesettings() {
        mouseSettings.removeAll();
        Main.mouseSettings.forEach(setting -> {
            CheckboxMenuItem item = new CheckboxMenuItem(setting.getName() + " [" + setting.getActivationSequence().replaceAll("[.]", " ") + "]");
            item.addItemListener(e -> {
                Main.mouseSettings.forEach(x -> {
                    if (item.getLabel().contains(x.getName())) {
                        x.setActive(item.getState());
                        FileOperator.saveMouseData();
                        Platform.runLater(ViewController::updateMouseSettings);
                    }
                });
                System.out.println(item.getLabel() + " -> " + item.getState());
            });
            item.setState(setting.isActive());
            mouseSettings.add(item);
        });
    }

    /**
     * Adds {@link Script} to the tray.
     * </p>
     * Each script has listener for updating data correspondingly
     * 
     * @see FileOperator#saveScript(Script)
     * @see ViewController#staticUpdateScriptList()
     * @param setting
     */
    public void addKeySetting(Script script) {
        CheckboxMenuItem item = new CheckboxMenuItem(script.getName() + " [" + script.getActivationSequence() + "]");
        item.addItemListener(e -> {
            Main.scripts.forEach(x -> {
                if (item.getLabel().contains(x.getName())) {
                    x.setActive(item.getState());
                    FileOperator.saveScript(x);
                    Platform.runLater(ViewController::staticUpdateScriptList);
                }
            });
            System.out.println(item.getLabel() + " -> " + item.getState());
        });
        item.setState(script.isActive());
        keySettings.add(item);
    }

    /**
     * Updates {@link MouseSetting} of active tray.
     * </p>
     * Each script has listener for updating data correspondingly
     * 
     * @see FileOperator#saveScript(Script)
     * @see ViewController#staticUpdateScriptList()
     * @param setting
     */
    public void updateKeySettings() {
        keySettings.removeAll();
        Main.scripts.forEach(script -> {
            CheckboxMenuItem item = new CheckboxMenuItem(script.getName() + " [" + script.getActivationSequence() + "]");
            item.addItemListener(e -> {
                Main.scripts.forEach(x -> {
                    if (item.getLabel().contains(x.getName())) {
                        x.setActive(item.getState());
                        FileOperator.saveScript(x);
                        Platform.runLater(ViewController::staticUpdateScriptList);
                    }
                });
                System.out.println(item.getLabel() + " -> " + item.getState());
            });
            item.setState(script.isActive());
            keySettings.add(item);
        });
    }

}