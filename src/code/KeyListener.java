package code;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.github.kwhat.jnativehook.keyboard.SwingKeyAdapter;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Provides Listener for the application settings and execution
 * 
 * @see ViewController
 * @author Volodymyr Davybida
 */
public class KeyListener extends SwingKeyAdapter {
    private boolean globaly_active = false;
    public final static KeyListener operator = new KeyListener();
    private static String key_combo = new String();
    static volatile ArrayList<String> ignore_keys = new ArrayList<String>();
    public static ScriptSettingController listeningForScriptSetting;
    public static ScriptSettingsWindowContoller listeningForScriptSettingWindow;
    public static ViewController listeningForViewController;
    public static ArrayList<KeyKlicker> activeThreads = new ArrayList<KeyKlicker>();
    private static final NativeKeyListener GLOBAL_LISTENER = new NativeKeyListener() {

        @Override
        public void nativeKeyReleased(NativeKeyEvent nativeEvent) {
            if (!ignore_keys.contains(NativeKeyEvent.getKeyText(nativeEvent.getKeyCode()))) {
                if (!key_combo.isEmpty()) {
                    System.out.println("Key combo released -> [" + key_combo.toString() + "]");
                    for (Script iterable : Main.scripts) {
                        if (key_combo.toString().equals(iterable.getActivationSequence().replaceAll(" ", ""))) {
                            if (iterable.isActive()) {
                                activeThreads.forEach(System.out::println);
                                boolean active = false;
                                for (KeyKlicker thread : activeThreads) {
                                    if (thread.getName().equals(iterable.getName())) {
                                        active = true;
                                    }
                                }
                                if (active) {
                                    for (KeyKlicker keyKlicker : activeThreads) {
                                        if (keyKlicker.getName().equals(iterable.getName())) {
                                            activeThreads.remove(keyKlicker);
                                            iterable.getActions().forEach(x -> {
                                                ignore_keys.remove(x.getKey());
                                            });
                                            System.out.println(keyKlicker.getName() + " Stoped");
                                            keyKlicker.stopExecution();
                                            break;
                                        }
                                    }
                                } else {
                                    if (activeThreads.size() < 5) {
                                        KeyKlicker KeyKlicker = new KeyKlicker(iterable);
                                        KeyKlicker.setName(iterable.getName());
                                        activeThreads.add(KeyKlicker);
                                        iterable.getActions().forEach(x -> {
                                            ignore_keys.add(x.getKey());
                                        });
                                        KeyKlicker.start();
                                        System.out.println(KeyKlicker.getName() + " Started");
                                    }
                                }
                            }
                        }
                    }
                    key_combo = "";
                }
            }
        }

        @Override
        public void nativeKeyPressed(NativeKeyEvent nativeEvent) {
            if (!ignore_keys.contains(NativeKeyEvent.getKeyText(nativeEvent.getKeyCode()))) {
                String key_pressed = NativeKeyEvent.getKeyText(nativeEvent.getKeyCode());
                if (key_combo.indexOf(key_pressed) == -1) {
                    key_combo += key_pressed;
                }
            }
        }
    };

    private static final NativeKeyListener KEY_COMBINATION_LISTENER = new NativeKeyListener() {

        @Override
        public void nativeKeyReleased(NativeKeyEvent nativeEvent) {
            if (!key_combo.isEmpty()) {
                System.out.println("Key combo pressed -> " + key_combo.toString());
                if (listeningForScriptSettingWindow != null) {
                    listeningForScriptSettingWindow.setKeyCombination(key_combo.toString());
                } else if (listeningForViewController != null) {
                    listeningForViewController.setKeyCombination(key_combo.toString());
                }
                Main.keyListener.stopListeningForKeyCombination();
                key_combo = "";
                ignore_keys.clear();
            }
        }

        @Override
        public void nativeKeyPressed(NativeKeyEvent nativeEvent) {
            if (!ignore_keys.contains(NativeKeyEvent.getKeyText(nativeEvent.getKeyCode()))) {
                String key_pressed = NativeKeyEvent.getKeyText(nativeEvent.getKeyCode());
                if (key_combo.indexOf(key_pressed) == -1) {
                    key_combo += key_pressed + " ";
                }
            }
        }
    };

    private static final NativeKeyListener SINGLE_KEY_LISTENER = new NativeKeyListener() {
        @Override
        public void nativeKeyReleased(NativeKeyEvent nativeEvent) {
            String key = KeyEvent.getKeyText(operator.getJavaKeyEvent(nativeEvent).getKeyCode());
            if (!key.equalsIgnoreCase("Unknown keyCode: 0x0")) {
                listeningForScriptSetting.setKey(key);
            }
        }

    };

    /**
     * Converts {@link NativeKeyEvent} into {@link KeyEvent}.
     * 
     * @return KeyEvent
     */
    public KeyEvent convertEvent(NativeKeyEvent event) {
        return getJavaKeyEvent(event);
    }

    /**
     * Adds native hook to {@link GlobalScreen} if haven't been already added.
     * 
     * @see #removeNativeHook()
     */
    public void addNathiveHook() {
        try {
            if (!GlobalScreen.isNativeHookRegistered()) {
                GlobalScreen.registerNativeHook();
                System.out.println("Native Hook Registered!");
            } else {
                System.out.println("Native Hook Already Registered!");
            }
        } catch (NativeHookException e) {
            e.printStackTrace();
        }
    }

    /**
     * Removes native hook from {@link GlobalScreen} if haven't been already
     * removed.
     * 
     * @see #addNathiveHook()
     */
    public void removeNativeHook() {
        try {
            if (GlobalScreen.isNativeHookRegistered()) {
                GlobalScreen.unregisterNativeHook();
                System.out.println("Native Hook Removed!");
            } else {
                System.out.println("Native Hook Was NOT Registered!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Start globall key listening for {@link Script}s activation sequence capture
     * and execution.
     * 
     * @see #stopGlobalKeyListening()
     */
    public void startGlobalKeyListening() {
        try {
            if (!GlobalScreen.isNativeHookRegistered()) {
                GlobalScreen.registerNativeHook();
                System.out.println("Native Hook Registered!");
            }
            if (this.globaly_active) {
                GlobalScreen.addNativeKeyListener(GLOBAL_LISTENER);
                System.out.println("Global Listener is Active");
                this.globaly_active = false;
            }
        } catch (NativeHookException e) {
            e.printStackTrace();
        }
    }

    /**
     * Stops globall key listening for {@link Script}.
     * 
     * @see #startGlobalKeyListening()
     */
    public void stopGlobalKeyListening() {
        GlobalScreen.removeNativeKeyListener(GLOBAL_LISTENER);
        System.out.println("Global Listener Removed!");
    }

    /**
     * Calls {@link #stopGlobalKeyListening()} in order to listen for single key to
     * be captured and added as a text field.
     * 
     * @see ScriptSettingController#askKey()
     * @param scriptSettingController
     */
    public void listenForSingleKey(ScriptSettingController scriptSettingController) {
        listeningForScriptSetting = scriptSettingController;
        Main.keyListener.stopGlobalKeyListening();
        System.out.println("Global Listener Removed!");
        GlobalScreen.addNativeKeyListener(SINGLE_KEY_LISTENER);
        System.out.println("Single Key Listener is Active");
    }

    /**
     * Stops listening for single Key and calls {@link #startGlobalKeyListening()}
     */
    public void stopListeningForSingleKey() {
        GlobalScreen.removeNativeKeyListener(SINGLE_KEY_LISTENER);
        Main.keyListener.startGlobalKeyListening();
    }

    /**
     * Calls {@link #stopGlobalKeyListening()} in order to listen for {@link Script}
     * activetion sequence to be captured.
     * 
     * @see ScriptSettingsWindowContoller#askKeySequense()
     * @param scriptSettingsWindowContoller
     */
    public void listenForKeyCombination(ScriptSettingsWindowContoller scriptSettingsWindowContoller) {
        listeningForScriptSettingWindow = scriptSettingsWindowContoller;
        Main.keyListener.stopGlobalKeyListening();
        GlobalScreen.addNativeKeyListener(KEY_COMBINATION_LISTENER);
        System.out.println("Key Combination Listener is Active");
    }

    /**
     * Calls {@link #stopGlobalKeyListening()} in order to listen for
     * {@link MouseSetting} activetion sequence to be captured.
     * 
     * @see ViewController#askKeySequense(javafx.scene.input.MouseEvent)
     * @param controller
     */
    public void listenForKeyCombination(ViewController controller) {
        listeningForViewController = controller;
        Main.keyListener.stopGlobalKeyListening();
        GlobalScreen.addNativeKeyListener(KEY_COMBINATION_LISTENER);
        System.out.println("Key Combination Listener is Active");
    }

    /**
     * Stops any Key Combination Listeners and calls
     * {@link #startGlobalKeyListening()}.
     */
    public void stopListeningForKeyCombination() {
        GlobalScreen.removeNativeKeyListener(KEY_COMBINATION_LISTENER);
        System.out.println("Key Combination Listener Removed!");
        Main.keyListener.startGlobalKeyListening();
    }

    public void stopAllListeners() {
        GlobalScreen.removeNativeKeyListener(SINGLE_KEY_LISTENER);
        GlobalScreen.removeNativeKeyListener(GLOBAL_LISTENER);
        GlobalScreen.removeNativeKeyListener(KEY_COMBINATION_LISTENER);
        System.out.println("ALL KeyBoard Listeners Removed!");
    }

    public void setGlobalyActive(boolean active) {
        this.globaly_active = active;
    }

    public void stopAllThreads() {
        activeThreads.forEach(x -> {
            x.stopExecution();
        });
    }
}

/**
 * Sub class for executing {@link Script}s captured by {@link KeyListener}.
 * 
 * @see KeyListener
 * @author Volodymyr Davybida
 */
class KeyKlicker extends Thread {
    private Robot robot;
    private Script script;
    private volatile boolean active = true;
    private Thread thisThread;
    private ExecutorService executor = Executors.newCachedThreadPool();

    public KeyKlicker(Script script) {
        this.script = script;
        initializeRobot();
    }

    @SuppressWarnings("deprecation")
    public void stopExecution() {
        active = false;
        if (!this.executor.isShutdown()) {
            this.executor.shutdown();
            this.executor.shutdownNow();
        }
        this.relase();
        if (this.robot != null) {
            this.robot = null;
        }
        this.stop();
    }

    public void initializeRobot() {
        try {
            if (this.robot == null) {
                this.robot = new Robot();
                System.out.println("Robot Initialized!");
            } else {
                System.out.println("Robot Already Initialized!");
            }
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    /**
     * Attemps to release any holding key if {@link #run()} has called wait.
     * </p>
     * Calls {@link #interrupt()}.
     * 
     * @see #run
     */
    public void relase() {
        try {
            if (robot == null) {
                robot = new Robot();
            }
            for (ScriptCommand command : this.script.getActions()) {
                robot.keyRelease(getIntCode(command.getKey().toUpperCase().replaceAll(" ", "_")));
            }
        } catch (AWTException e) {
            e.printStackTrace();
        }
        thisThread.interrupt();
    }

    public void removeRobot() {
        try {
            if (this.robot != null) {
                this.robot = null;
                System.out.println("Robot Was Removed!");
            } else {
                System.out.println("Robot Was not initialised!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Executes {@code script} passed as parameter to {@link #KeyListener(Script)}.
     * </p>
     * If Scripts key is executed infinitely it is passed to new thread, otherwise
     * {@link #executeSingleButton(String, int, int)} is called.
     * 
     * @see #relase()
     * @see #stopExecution()
     * @see #initializeRobot()
     */
    @Override
    public void run() {
        try {
            thisThread = Thread.currentThread();
            for (ScriptCommand command : this.script.getActions()) {
                if (command.getRepeatTimes() != -1) {
                    this.executeSingleButton(command.getKey(), command.getRepeatTimes(), command.getDelay());
                } else {
                    executor.execute(new Thread() {
                        @Override
                        public void run() {
                            try {
                                Robot inf_robot = new Robot();
                                while (active) {
                                    inf_robot.keyPress(getIntCode(command.getKey().toUpperCase().replaceAll(" ", "_")));
                                    Thread.sleep(command.getDelay());
                                    inf_robot.keyRelease(getIntCode(command.getKey().toUpperCase().replaceAll(" ", "_")));
                                }
                            } catch (AWTException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                System.out.println(getName() + " Got Interupted!");
                            }
                        }
                    });
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!active) {
            KeyListener.activeThreads.remove(this);
        }
    }

    /**
     * Executes single key passed from {@link #run()}
     * 
     * @param key
     * @param times
     * @param delay
     */
    public void executeSingleButton(String key, int times, int delay) {
        int key_code = getIntCode(key.toUpperCase().replaceAll(" ", "_"));
        try {
            for (int i = 0; i < times; i++) {
                this.robot.keyPress(key_code);
                System.out.println(i + " Robot Pressed ->" + KeyEvent.getKeyText(key_code));
                Thread.sleep(delay);
                this.robot.keyRelease(key_code);
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.out.println(getName() + " Got Interupted!");
        }
    }

    /**
     * @param key
     * @return int code for key
     */
    public static int getIntCode(String key) {
        switch (key) {
        case "UNDEFINED": {
            return 0;
        }
        case "CANCEL": {
            return 3;
        }
        case "BACKSPACE": {
            return 8;
        }
        case "TAB": {
            return 9;
        }
        case "ENTER": {
            return 10;
        }
        case "CLEAR": {
            return 12;
        }
        case "SHIFT": {
            return 16;
        }
        case "CONTROL": {
            return 17;
        }
        case "ALT": {
            return 18;
        }
        case "PAUSE": {
            return 19;
        }
        case "CAPS_LOCK": {
            return 20;
        }
        case "KANA": {
            return 21;
        }
        case "FINAL": {
            return 24;
        }
        case "KANJI": {
            return 25;
        }
        case "ESCAPE": {
            return 27;
        }
        case "CONVERT": {
            return 28;
        }
        case "NONCONVERT": {
            return 29;
        }
        case "ACCEPT": {
            return 30;
        }
        case "MODECHANGE": {
            return 31;
        }
        case "SPACE": {
            return 32;
        }
        case "PAGE_UP": {
            return 33;
        }
        case "PAGE_DOWN": {
            return 34;
        }
        case "END": {
            return 35;
        }
        case "HOME": {
            return 36;
        }
        case "LEFT": {
            return 37;
        }
        case "UP": {
            return 38;
        }
        case "RIGHT": {
            return 39;
        }
        case "DOWN": {
            return 40;
        }
        case "COMMA": {
            return 44;
        }
        case "MINUS": {
            return 45;
        }
        case "PERIOD": {
            return 46;
        }
        case "SLASH": {
            return 47;
        }
        case "0": {
            return 48;
        }
        case "1": {
            return 49;
        }
        case "2": {
            return 50;
        }
        case "3": {
            return 51;
        }
        case "4": {
            return 52;
        }
        case "5": {
            return 53;
        }
        case "6": {
            return 54;
        }
        case "7": {
            return 55;
        }
        case "8": {
            return 56;
        }
        case "9": {
            return 57;
        }
        case "SEMICOLON": {
            return 59;
        }
        case "EQUALS": {
            return 61;
        }
        case "A": {
            return 65;
        }
        case "B": {
            return 66;
        }
        case "C": {
            return 67;
        }
        case "D": {
            return 68;
        }
        case "E": {
            return 69;
        }
        case "F": {
            return 70;
        }
        case "G": {
            return 71;
        }
        case "H": {
            return 72;
        }
        case "I": {
            return 73;
        }
        case "J": {
            return 74;
        }
        case "K": {
            return 75;
        }
        case "L": {
            return 76;
        }
        case "M": {
            return 77;
        }
        case "N": {
            return 78;
        }
        case "O": {
            return 79;
        }
        case "P": {
            return 80;
        }
        case "Q": {
            return 81;
        }
        case "R": {
            return 82;
        }
        case "S": {
            return 83;
        }
        case "T": {
            return 84;
        }
        case "U": {
            return 85;
        }
        case "V": {
            return 86;
        }
        case "W": {
            return 87;
        }
        case "X": {
            return 88;
        }
        case "Y": {
            return 89;
        }
        case "Z": {
            return 90;
        }
        case "OPEN_BRACKET": {
            return 91;
        }
        case "BACK_SLASH": {
            return 92;
        }
        case "CLOSE_BRACKET": {
            return 93;
        }
        case "NUMPAD0": {
            return 96;
        }
        case "NUMPAD1": {
            return 97;
        }
        case "NUMPAD2": {
            return 98;
        }
        case "NUMPAD3": {
            return 99;
        }
        case "NUMPAD4": {
            return 100;
        }
        case "NUMPAD5": {
            return 101;
        }
        case "NUMPAD6": {
            return 102;
        }
        case "NUMPAD7": {
            return 103;
        }
        case "NUMPAD8": {
            return 104;
        }
        case "NUMPAD9": {
            return 105;
        }
        case "MULTIPLY": {
            return 106;
        }
        case "ADD": {
            return 107;
        }
        case "SEPARATER": {
            return 108;
        }
        case "SEPARATOR": {
            return 108;
        }
        case "SUBTRACT": {
            return 109;
        }
        case "DECIMAL": {
            return 110;
        }
        case "DIVIDE": {
            return 111;
        }
        case "F1": {
            return 112;
        }
        case "F2": {
            return 113;
        }
        case "F3": {
            return 114;
        }
        case "F4": {
            return 115;
        }
        case "F5": {
            return 116;
        }
        case "F6": {
            return 117;
        }
        case "F7": {
            return 118;
        }
        case "F8": {
            return 119;
        }
        case "F9": {
            return 120;
        }
        case "F10": {
            return 121;
        }
        case "F11": {
            return 122;
        }
        case "F12": {
            return 123;
        }
        case "DELETE": {
            return 127;
        }
        case "DEAD_GRAVE": {
            return 128;
        }
        case "DEAD_ACUTE": {
            return 129;
        }
        case "DEAD_CIRCUMFLEX": {
            return 130;
        }
        case "DEAD_TILDE": {
            return 131;
        }
        case "DEAD_MACRON": {
            return 132;
        }
        case "DEAD_BREVE": {
            return 133;
        }
        case "DEAD_ABOVEDOT": {
            return 134;
        }
        case "DEAD_DIAERESIS": {
            return 135;
        }
        case "DEAD_ABOVERING": {
            return 136;
        }
        case "DEAD_DOUBLEACUTE": {
            return 137;
        }
        case "DEAD_CARON": {
            return 138;
        }
        case "DEAD_CEDILLA": {
            return 139;
        }
        case "DEAD_OGONEK": {
            return 140;
        }
        case "DEAD_IOTA": {
            return 141;
        }
        case "DEAD_VOICED_SOUND": {
            return 142;
        }
        case "DEAD_SEMIVOICED_SOUND": {
            return 143;
        }
        case "NUM_LOCK": {
            return 144;
        }
        case "SCROLL_LOCK": {
            return 145;
        }
        case "AMPERSAND": {
            return 150;
        }
        case "ASTERISK": {
            return 151;
        }
        case "QUOTEDBL": {
            return 152;
        }
        case "LESS": {
            return 153;
        }
        case "PRINTSCREEN": {
            return 154;
        }
        case "INSERT": {
            return 155;
        }
        case "HELP": {
            return 156;
        }
        case "META": {
            return 157;
        }
        case "GREATER": {
            return 160;
        }
        case "BRACELEFT": {
            return 161;
        }
        case "BRACERIGHT": {
            return 162;
        }
        case "BACK_QUOTE": {
            return 192;
        }
        case "QUOTE": {
            return 222;
        }
        case "KP_UP": {
            return 224;
        }
        case "KP_DOWN": {
            return 225;
        }
        case "KP_LEFT": {
            return 226;
        }
        case "KP_RIGHT": {
            return 227;
        }
        case "ALPHANUMERIC": {
            return 240;
        }
        case "KATAKANA": {
            return 241;
        }
        case "HIRAGANA": {
            return 242;
        }
        case "FULL_WIDTH": {
            return 243;
        }
        case "HALF_WIDTH": {
            return 244;
        }
        case "ROMAN_CHARACTERS": {
            return 245;
        }
        case "ALL_CANDIDATES": {
            return 256;
        }
        case "PREVIOUS_CANDIDATE": {
            return 257;
        }
        case "CODE_INPUT": {
            return 258;
        }
        case "JAPANESE_KATAKANA": {
            return 259;
        }
        case "JAPANESE_HIRAGANA": {
            return 260;
        }
        case "JAPANESE_ROMAN": {
            return 261;
        }
        case "KANA_LOCK": {
            return 262;
        }
        case "INPUT_METHOD_ON_OFF": {
            return 263;
        }
        case "AT": {
            return 512;
        }
        case "COLON": {
            return 513;
        }
        case "CIRCUMFLEX": {
            return 514;
        }
        case "DOLLAR": {
            return 515;
        }
        case "EURO_SIGN": {
            return 516;
        }
        case "EXCLAMATION_MARK": {
            return 517;
        }
        case "INVERTED_EXCLAMATION_MARK": {
            return 518;
        }
        case "LEFT_PARENTHESIS": {
            return 519;
        }
        case "NUMBER_SIGN": {
            return 520;
        }
        case "PLUS": {
            return 521;
        }
        case "RIGHT_PARENTHESIS": {
            return 522;
        }
        case "UNDERSCORE": {
            return 523;
        }
        case "WINDOWS": {
            return 524;
        }
        case "CONTEXT_MENU": {
            return 525;
        }
        case "F13": {
            return 61440;
        }
        case "F14": {
            return 61441;
        }
        case "F15": {
            return 61442;
        }
        case "F16": {
            return 61443;
        }
        case "F17": {
            return 61444;
        }
        case "F18": {
            return 61445;
        }
        case "F19": {
            return 61446;
        }
        case "F20": {
            return 61447;
        }
        case "F21": {
            return 61448;
        }
        case "F22": {
            return 61449;
        }
        case "F23": {
            return 61450;
        }
        case "F24": {
            return 61451;
        }
        case "COMPOSE": {
            return 65312;
        }
        case "BEGIN": {
            return 65368;
        }
        case "ALT_GRAPH": {
            return 65406;
        }
        case "STOP": {
            return 65480;
        }
        case "AGAIN": {
            return 65481;
        }
        case "PROPS": {
            return 65482;
        }
        case "UNDO": {
            return 65483;
        }
        case "COPY": {
            return 65485;
        }
        case "PASTE": {
            return 65487;
        }
        case "FIND": {
            return 65488;
        }
        case "CUT": {
            return 65489;
        }
        }
        return -1;
    }
}