
package code;

import java.util.ArrayList;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

/**
 * Main class that starts application and holds all the nessesery data for
 * correct comunication of controllers
 * 
 * @see ViewController#start(Stage stage)
 * @see FileOperator
 * @see KeyListener
 * @see MouseListener
 */
public class Main {
    public static FXMLLoader main_view_fxmlLoader;
    public static ArrayList<Script> scripts = new ArrayList<Script>();
    public static ArrayList<MouseSetting> mouseSettings = new ArrayList<MouseSetting>();
    public static AnchorPane selected_script_view;
    public static KeyListener keyListener;
    public static MouseListener mouseListener;
    public static TrayController tray = new TrayController();
    public static ViewController viewController;

    /**
     * Creates new variable of {@code Main}, loads the {@link FXMLLoader} of
     * {@code main_view.fxml} and registers {@code NativeHook} for
     * {@link KeyListener} and {@link MouseListener}.
     * </p>
     * Reads all data from {@code data} folder containing settings for
     * {@link Script} and {@link MouseSetting}. If no folder found, new will be
     * created with placeholder of {@code MouseSetting} file.
     * </p>
     * Proceeds to execute {@link ViewController#start()}.
     * 
     * @param args are usless (ignored)
     */
    public static void main(String[] args) {
        try {
            System.out.println("Programm started!");
            Main mian = new Main();

            mian.loadMainFXMLLoader();
            mian.registerListeners();

            Main.keyListener.addNathiveHook();
            if (FileOperator.checkData()) {
                FileOperator.readScriptsData();
                FileOperator.readMouseData();
            }

            ViewController.start();

            System.out.println("Programm ended!");
        } catch (Exception e) {
            System.out.println("Program Crashed!");
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void registerListeners() {
        keyListener = new KeyListener();
        mouseListener = new MouseListener();
    }

    /**
     * Loads {@link FXMLLoader} for main view of the application for global access
     */
    private void loadMainFXMLLoader() {
        try {
            main_view_fxmlLoader = new FXMLLoader(getClass().getResource("/resourses/main_view.fxml"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Generates random integer from 0 to 99999
     * 
     * @return Integer
     */
    public static int getRandomNumber() {
        return (int) (Math.random() * 100000);
    }

    /**
     * This function searchess scripts for exact one and updates its data
     * 
     * @param script to be updated
     */
    public static void updateScript(Script script) {
        int position = 0;
        for (int i = 0; i < scripts.size(); i++) {
            if (scripts.get(i).getName().equalsIgnoreCase(script.getName())) {
                position = i;
                break;
            }
        }
        scripts.remove(position);
        scripts.add(script);
        for (Script iterable : scripts) {
            System.out.println(iterable);
        }
    }

}
