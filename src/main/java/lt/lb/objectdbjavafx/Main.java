/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lt.lb.objectdbjavafx;

import com.objectdb.Utilities;
import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.function.Supplier;
import javax.jdo.PersistenceManager;
import lt.lb.commons.F;
import lt.lb.commons.Log;
import lt.lb.commons.UUIDgenerator;
import lt.lb.commons.containers.LazyValue;
import lt.lb.commons.javafx.FX;
import lt.lb.commons.javafx.scenemanagement.Frame;
import lt.lb.commons.javafx.scenemanagement.MultiStageManager;
import lt.lb.objectdbjavafx.model.EAValue;
import lt.lb.objectdbjavafx.model.FileEntity;
import lt.lb.objectdbjavafx.model.FileEntityFolder;

/**
 *
 * @author laim0nas100
 */
public class Main {

    public static final boolean tests = false;

    public static final String databaseUri = "../db/database.odb";
    public static final LazyValue<PersistenceManager> pm = LazyValue.of(() -> {
        return Utilities.getPersistenceManager(databaseUri);
    });

    public static MultiStageManager sceneManager = new MultiStageManager();

    public static void makeMetaEditWindow(EAValue val) {
        URL resource = sceneManager.getResource("/fxml/MetaEdit.fxml");
        F.unsafeRun(() -> {
            Frame newFrame = sceneManager.newFrame(resource, UUIDgenerator.nextUUID("MetaEdit"), "MetaEdit", false);
            EAVEditController controller = newFrame.getController();
            controller.value = val;
            FX.submit(() -> {
                newFrame.getStage().show();
                controller.update();
            });

        });
    }

    public static void makeNewWindow(FileEntityFolder folder) {
        // create main window
        URL resource = sceneManager.getResource("/fxml/MainWindow.fxml");
        F.unsafeRun(() -> {
            Frame newFrame = sceneManager.newFrame(resource, UUIDgenerator.nextUUID("MainWindow"), "Main window", false);
            FX.submit(() -> {
                MainWindowController controller = newFrame.getController();
                controller.folder = folder;
                newFrame.getStage().show();
                controller.update();
            });
        });
    }

    public static void makeNewWindow(Supplier<Collection<FileEntity>> update, String title) {
        // create main window
        URL resource = sceneManager.getResource("/fxml/MainWindow.fxml");
        F.unsafeRun(() -> {
            Frame newFrame = sceneManager.newFrame(resource, UUIDgenerator.nextUUID("MainWindow"), title, false);
            FX.submit(() -> {
                MainWindowController controller = newFrame.getController();
                controller.filePopulatingFunction = update;
                newFrame.getStage().show();
                controller.update();
                
            });
        });
    }

    public static void makeTextEditWindow(FileEntity entity) {
        URL resource = sceneManager.getResource("/fxml/TextEdit.fxml");
        F.unsafeRun(() -> {
            Frame newFrame = sceneManager.newFrame(resource, UUIDgenerator.nextUUID("TextEdit"), "TextEdit", false);
            TextEditController controller = newFrame.getController();
            controller.file = entity;
            FX.submit(() -> {
                newFrame.getStage().show();
                controller.update();
            });

        });
    }

    public static void updateAllWindows() {
        F.iterate(sceneManager.frames, (k, fr) -> {
            fr.getController().update();
        });
    }

    public static void main(String... args) {

        // delete prev database, ignore if not found 
        F.checkedRun(() -> {
            File file = new File(databaseUri);
            Path parent = Paths.get(file.getAbsolutePath()).getParent();
            Log.print("Parent", parent.toAbsolutePath(), Files.isDirectory(parent));
            Files.walk(parent).forEach(path -> {
                if (!Files.isDirectory(path)) {
                    F.unsafeRun(() -> {
                        boolean deleted = Files.deleteIfExists(path);
                        Log.print("Deleted:", path, deleted);
                    });

                }
            });
        });

        // set up db
        Bootstrap.bootstrapMeta();
        Bootstrap.bootstrapFileSystem();

        Log.print("DB setup ok");

        if (tests) {
            Log.print("Tests!!!");
            JDOTests.selectAllFolders();
            JDOTests.selectJustFiles();
            JDOTests.selectEAVbyMetaRoot();
            JDOTests.selectEAVbyMetaFileName();
            JDOTests.fullTextSearch1();
            JDOTests.fullTextSearch2();
            JDOTests.fullTextSearch3();

            Main.pm.get().close();
            System.exit(0);
        } else {
            Main.makeNewWindow(FAPI.getMainFolder());
        }

    }
}
