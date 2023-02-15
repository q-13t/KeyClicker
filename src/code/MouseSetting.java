package code;

/**
 * Somple class representing settings
 * 
 * @author Volodymyr Davybida
 * @see ViewController
 * @see FileOperator#readMouseData()
 * @see MouseListener
 */
public class MouseSetting {
    private String name;
    private boolean active;
    private boolean activeOnHold;
    private String ActivationSequence;

    public MouseSetting(String name, boolean active, boolean activeOnHold, String ActivationSequence) {
        this.name = name;
        this.active = active;
        this.activeOnHold = activeOnHold;
        this.ActivationSequence = ActivationSequence;
    }

    public MouseSetting() {
    }

    public String getName() {
        return name;
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

    public boolean isActiveOnHold() {
        return activeOnHold;
    }

    public void setActiveOnHold(boolean activeOnHold) {
        this.activeOnHold = activeOnHold;
    }

    public String getActivationSequence() {
        return ActivationSequence;
    }

    public void setactivationSequence(String ActivationSequence) {
        this.ActivationSequence = ActivationSequence;
    }

    /**
     * Parsess String into Mouse Setting
     * 
     * @param line
     * @return {@link MouseSetting}
     * @see FileOperator#readMouseData()
     */
    public static MouseSetting parseString(String line) {
        MouseSetting ms = new MouseSetting();
        String[] args = line.split(" ");
        ms.setName(args[0]);
        ms.setActive(Boolean.parseBoolean(args[1]));
        ms.setActiveOnHold(Boolean.parseBoolean(args[2]));
        if (args.length == 4) {
            ms.setactivationSequence(args[3]);
        }
        return ms;
    }

    @Override
    public String toString() {
        if (this.ActivationSequence != null) {
            return this.name + " " + this.isActive() + " " + this.isActiveOnHold() + " " + this.ActivationSequence.replace(" ", ".");
        }
        return this.name + " " + this.isActive() + " " + this.isActiveOnHold() + " " + this.ActivationSequence;
    }
}
