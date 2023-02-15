package code;

import java.util.ArrayList;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 * Initial Key Script Class
 * 
 * @author Volodymyr davybida
 * @see ScriptCommand
 * @see FileOperator#readScriptsData()
 * @see KeyListener
 */
public class Script {
    private String name;
    private boolean active;
    private String activationSequence;
    private ArrayList<ScriptCommand> actions = new ArrayList<ScriptCommand>();

    Script(String name, boolean active, String activationSequence) {
        this.name = name;
        this.active = active;
        this.activationSequence = activationSequence;
    }

    Script(String name) {
        this.name = name;
    }

    Script() {
    }

    /**
     * Parsess provided String into {@code Script}
     * 
     * @see FileOperator#readScriptsData()
     * @see ScriptCommand#parseString(String)
     * @param text
     * @return {@link Script}
     */
    public static Script parseString(String text) {
        Script script = new Script();
        String[] lines = text.split("\n");
        script.setName(lines[0].split("->")[1].replaceAll("^ ", ""));
        script.setActivationSequence(lines[1].split("->")[1].replaceAll("^ | $", ""));
        script.setActive(Boolean.parseBoolean(lines[2].split("->")[1].replace(" ", "")));
        if (lines.length > 3) {
            ArrayList<ScriptCommand> actions = new ArrayList<ScriptCommand>();
            for (int i = 3; i < lines.length; i++) {
                ScriptCommand command = new ScriptCommand();
                actions.add(command.parseString(lines[i]));
            }
            script.setActions(actions);
        }
        return script;
    }

    /**
     * Setts {@link ScriptCommand} for specific script.
     * 
     * @see ScriptSettingsWindowContoller#saveNewScript()
     * @see ScriptCommand#parsePane(Pane)
     * @param settings
     */
    public void setActions(VBox settings) {
        actions.clear();
        settings.getChildren().forEach(iterable -> {
            if (!iterable.getId().equals("add_script_action_button")) {
                Pane setting = (Pane) iterable;
                actions.add(new ScriptCommand().parsePane(setting));
            }
        });
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        StringBuilder actions_str = new StringBuilder();

        actions.forEach(elemnt -> {
            actions_str.append(elemnt.toString() + "\n");
        });
        return "Script -> " + name + "\nActivation Sequence -> " + activationSequence + "\nActive -> " + active + "\n" + actions_str.toString();
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getActivationSequence() {
        return activationSequence;
    }

    public void setActivationSequence(String activationSequence) {
        this.activationSequence = activationSequence;
    }

    public ArrayList<ScriptCommand> getActions() {
        return actions;
    }

    public void setActions(ArrayList<ScriptCommand> actions) {
        this.actions = actions;
    }

}

/**
 * Sub class representing {@link Script}s actions.
 * 
 * @see Script
 * @author Volodymyr Davybida
 */
class ScriptCommand {
    private String key;
    private int repeatTimes;
    private int delay;

    public ScriptCommand(String key, int Times, int delay) {
        this.key = key;
        this.repeatTimes = Times;
        this.delay = delay;
    }

    /**
     * Parsses provided string into unique {@link ScriptCommand}.
     * 
     * @see Script#parseString(String text)
     * @param string
     * @return {@link ScriptCommand}
     */
    public ScriptCommand parseString(String string) {
        String[] lines = string.split(";");
        this.key = lines[0].split("->")[1].replaceAll(" ", "");
        String times = lines[1].split("->")[1].replaceAll(" ", "");
        if (times.equalsIgnoreCase("Infinite")) {
            this.repeatTimes = -1;
        } else {
            this.repeatTimes = Integer.parseInt(times);
        }
        this.delay = Integer.parseInt(lines[2].split("->")[1].replaceAll(" ", ""));
        return this;
    }

    /**
     * Parsess provided {@link ScriptSettingController} pane into unique script
     * command.
     * 
     * @param setting
     * @return {@link ScriptCommand}
     */
    public ScriptCommand parsePane(Pane setting) {
        this.key = ((TextField) setting.getChildren().get(1)).getText();
        String times = ((TextField) setting.getChildren().get(3)).getText();
        if (times.equalsIgnoreCase("Infinite")) {
            this.repeatTimes = -1;
        } else {
            this.repeatTimes = Integer.parseInt(times);
        }
        this.delay = Integer.parseInt(((TextField) setting.getChildren().get(7)).getText());
        return this;
    }

    public ScriptCommand() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getRepeatTimes() {
        return repeatTimes;
    }

    public void setRepeatTimes(int repeatTimes) {
        this.repeatTimes = repeatTimes;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    @Override
    public String toString() {
        if (repeatTimes == -1) {
            return "Action -> " + key + "; Times -> Infinite; Delay -> " + delay;
        }
        return "Action -> " + key + "; Times -> " + repeatTimes + "; Delay -> " + delay;
    }
}
