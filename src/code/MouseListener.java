package code;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
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
public class MouseListener {
    private boolean globaly_active;
    public static ArrayList<MouseKlicker> activeThreads = new ArrayList<MouseKlicker>();
    private static String keyCombo = new String();
    private static int ignoreKey = -1;
    private static ExecutorService executor = Executors.newCachedThreadPool();

    private static final NativeKeyListener GLOBAL_MOUSE_KEY_LISTENER = new NativeKeyListener() {
        public void nativeKeyPressed(NativeKeyEvent event) {
            if ((!keyCombo.contains(NativeKeyEvent.getKeyText(event.getKeyCode()))) && (ignoreKey != event.getKeyCode())) {
                keyCombo += NativeKeyEvent.getKeyText(event.getKeyCode()) + " ";
                ignoreKey = event.getKeyCode();
            }
        };

        public void nativeKeyReleased(NativeKeyEvent event) {
            if (!keyCombo.isEmpty()) {
                for (MouseSetting iterable : Main.mouseSettings) {
                    if (iterable.getActivationSequence().replaceAll("[^A-Z]", "").equalsIgnoreCase(keyCombo.replaceAll("[^A-Z]", ""))) {
                        System.out.println(iterable.getName());
                        if (iterable.isActive()) {
                            MouseKlicker klicker = new MouseKlicker(getKeyInt(iterable.getName()));
                            klicker.setName(iterable.getName());
                            klicker.setHold(iterable.isActiveOnHold());
                            boolean active = false;
                            int position = -1;
                            for (int i = 0; i < activeThreads.size(); i++) {
                                if (activeThreads.get(i).getName().equalsIgnoreCase(iterable.getName())) {
                                    active = true;
                                    position = i;
                                    break;
                                }
                            }
                            if (active && (position != -1)) {
                                activeThreads.get(position).stopExecution();
                                activeThreads.remove(position);
                            } else {
                                activeThreads.add(klicker);
                                executor.submit(klicker);
                            }
                        }
                        break;
                    }
                }
                keyCombo = "";
                ignoreKey = -1;
            }
        };
    };

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

    protected static int getKeyInt(String string) {
        switch (string) {
        case "RMB": {
            return InputEvent.BUTTON1_DOWN_MASK;
        }
        case "LMB": {
            return InputEvent.BUTTON2_DOWN_MASK;
        }
        case "MMB": {
            return InputEvent.BUTTON3_DOWN_MASK;
        }
        }
        return -1;
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
     * Start globall key listening for {@link MouseSetting}s activation sequence
     * capture and execution.
     * 
     * @see #stopGlobalMouseKeyListening()
     */
    public void startGlobalMouseKeyListening() {

        try {
            if (!GlobalScreen.isNativeHookRegistered()) {
                addNathiveHook();
                System.out.println("Native Hook Registered!");
            }
            if (this.globaly_active) {
                GlobalScreen.addNativeKeyListener(GLOBAL_MOUSE_KEY_LISTENER);
                System.out.println("Global Mouse Listener Registered");
                this.globaly_active = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Stops globall key listening for {@link MouseSetting}.
     * 
     * @see #startGlobalMouseKeyListening()
     */
    public void stopGlobalMouseKeyListening() {
        GlobalScreen.removeNativeKeyListener(GLOBAL_MOUSE_KEY_LISTENER);
        System.out.println("Global Mouse Listener Removed");
    }

    public void setGlobalyActive(boolean active) {
        this.globaly_active = active;
    }

    public void stopAllListeners() {
        GlobalScreen.removeNativeKeyListener(GLOBAL_MOUSE_KEY_LISTENER);
        System.out.println("ALL Mouse Listeners Removed!");
    }

    public void stopAllThreads() {
        executor.shutdown();
        activeThreads.clear();

    }
}

/**
 * Sub class for executing {@link MouseSetting}s captured by
 * {@link MouseListener}.
 * 
 * @see MouseListener
 * @author Volodymyr Davybida
 */
class MouseKlicker extends Thread {
    private Robot robot;
    private volatile boolean active = true;
    private volatile boolean hold = false;
    private int button;
    private Thread thisThread;

    @SuppressWarnings("deprecation")
    public void stopExecution() {
        active = false;
        this.relase();
        if (this.robot != null) {
            this.robot = null;
        }
        this.stop();
    }

    public boolean isHold() {
        return hold;
    }

    public void setHold(boolean hold) {
        this.hold = hold;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public MouseKlicker(int button) {
        this.button = button;
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
            robot.mouseRelease(button);
        } catch (AWTException e) {
            e.printStackTrace();
        }
        thisThread.interrupt();
    }

    /**
     * Executes {@link MouseSetting}s actions.
     * </p>
     * NOTE: if button has hold active Thread will press the button and sleep for
     * {@code Long.MAX_VALUE}, otherwise Thread will sleep for {@code 5}
     * millisecond.
     * 
     * @see #relase()
     * @see #stopExecution()
     */
    @Override
    public void run() {
        try {
            thisThread = Thread.currentThread();
            robot = new Robot();
            if (hold) {
                while (active) {
                    robot.mousePress(button);
                    Thread.sleep(Long.MAX_VALUE);
                    robot.mouseRelease(button);
                }
            } else {
                while (active) {
                    Thread.sleep(5);
                    robot.mousePress(button);
                    robot.mouseRelease(button);
                }
            }
        } catch (AWTException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.out.println(getName() + " Got Interupted!");
        }
    }

}