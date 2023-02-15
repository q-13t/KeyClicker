
package code;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;

public class FileOperator {
    protected static File main_files_dir = new File(System.getProperty("user.dir") + "/data");
    private static File mouseFile;

    /**
     * Checks wether {@code data} directory exists, if not - will be created
     * 
     * @return {@code true} if {@code data} folder contains any files, {@code false}
     *         otherwise
     */
    public static boolean checkData() {
        if (!main_files_dir.exists()) {
            main_files_dir.mkdir();
            System.out.println("Created data file");
        }
        if (main_files_dir.listFiles().length != 0) {
            return true;
        }
        return false;
    }

    /**
     * Reads scripts {@code data} folder and all scripts within.
     * 
     * @see Script
     */
    public static void readScriptsData() {
        try {

            Files.walkFileTree(Paths.get(main_files_dir.getAbsolutePath()), new FileVisitor<Path>() {

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    if (dir.toFile().exists()) {
                        return FileVisitResult.CONTINUE;
                    }
                    return FileVisitResult.TERMINATE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (file.toFile().getName().equals("mouse")) {
                        return FileVisitResult.CONTINUE;
                    }
                    BufferedReader br = new BufferedReader(new FileReader(file.toFile()));
                    String line = "";
                    StringBuffer stringBuffer = new StringBuffer();
                    while ((line = br.readLine()) != null) {
                        stringBuffer.append(line + "\n");
                    }
                    Main.scripts.add(Script.parseString(stringBuffer.toString()));
                    br.close();
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                    return FileVisitResult.TERMINATE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    return FileVisitResult.CONTINUE;
                }

            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes {@link Script}s data to {@code data} folder.
     * 
     * @param script wich needs to be written
     * @return {@code true} if save was successfull, {@code false} otherwise.
     */
    public static boolean saveScript(Script script) {
        File file = new File(main_files_dir + "\\", script.getName());
        try (FileWriter fileWriter = new FileWriter(file);) {
            file.createNewFile();
            fileWriter.write(script.toString());
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Delates provided {@link Script} from {@code data} folder.
     * 
     * @see ScriptCardController#YES_button()
     * @see ScriptSettingsWindowContoller#saveNewScript()
     * @param script
     */
    public static void deleteScript(Script script) {
        File file = new File(main_files_dir + "\\", script.getName());
        file.delete();
    }

    public static void saveMouseData() {
        try (FileWriter fw = new FileWriter(mouseFile)) {
            fw.write(Main.mouseSettings.get(0).toString() + "\n");
            fw.append(Main.mouseSettings.get(1).toString() + "\n");
            fw.append(Main.mouseSettings.get(2).toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads {@code data} folder for MouseSetting within.
     * 
     * @see MouseSetting
     */
    public static void readMouseData() {
        try {
            mouseFile = new File(main_files_dir, "/mouse");
            if (!mouseFile.exists()) {
                mouseFile.createNewFile();
                writeStarterMouseData();
                System.out.println("New Mouse Data File Created");
                readMouseData();
            } else {
                BufferedReader br = new BufferedReader(new FileReader(mouseFile));
                String line = "";
                while ((line = br.readLine()) != null) {

                    Main.mouseSettings.add(MouseSetting.parseString(line));
                }
                br.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes initial data for {@link MouseSetting} into {@code data} folder.
     * </p>
     * NOTE: all settings are set to false
     */
    private static void writeStarterMouseData() {
        try (FileWriter fw = new FileWriter(mouseFile)) {
            fw.write("RMB false false\n");
            fw.append("LMB false false\n");
            fw.append("MMB false false");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
