/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lt.lb.objectdbjavafx;

import java.util.Arrays;
import java.util.Optional;
import lt.lb.commons.F;
import lt.lb.commons.Log;
import lt.lb.objectdbjavafx.model.EAVType;
import lt.lb.objectdbjavafx.model.FileEntity;
import lt.lb.objectdbjavafx.model.FileEntityFolder;
import lt.lb.objectdbjavafx.model.Meta;

/**
 *
 * @author laim0nas100
 */
public class Bootstrap {

    public static void bootstrapMeta() {
        Optional<Throwable> submit = Q.submit(pm -> {
            Log.print("Bootstrap Meta");
            FAPI.createMeta(MetaEnums.fileName, EAVType.STRING);
            FAPI.createMeta(MetaEnums.root, EAVType.BOOLEAN);
            FAPI.createMeta(MetaEnums.createdDate, EAVType.DATE);
            FAPI.createMeta(MetaEnums.lastModifiedDate, EAVType.DATE);
            FAPI.createMeta(MetaEnums.textContent, EAVType.STRING);
            FAPI.createMeta(MetaEnums.parentID, EAVType.STRING);
            Log.print("Bootstrap meta ok");
        });
        if (submit.isPresent()) {
            throw new RuntimeException(submit.get());
        }
    }

    public static void bootstrapFileSystem() {
        //create few files
        Optional<Throwable> submit = Q.submit((em) -> {

            Log.print("Bootsrap FS");
            FileEntityFolder folder = FAPI.createFolder("MainFolder");

            folder.meta.addAttribute(FAPI.valueOf("root", true));

            F.unsafeRun(() -> {
                FileEntity f1 = FAPI.createTextFile("File1.txt", "Some text f1");

                FileEntity f2 = FAPI.createTextFile("File2.txt", "Some other text f2");

                FAPI.addAssignFolder(folder, f1);
                FAPI.addAssignFolder(folder, f2);
            });

            F.unsafeRun(() -> {

                FileEntity f1 = FAPI.createTextFile("File11.txt", "Some text f11");

                FileEntity f2 = FAPI.createTextFile("File22.txt", "Some other text f22");
                FileEntityFolder deeper = FAPI.createFolder("DeeperFolder");
                FAPI.addAssignFolder(deeper, f1);
                FAPI.addAssignFolder(deeper, f2);
                FAPI.addAssignFolder(folder,deeper);
            });

            Log.print("Total file count:" + folder.count());

            Log.print("Boosrap FS ok");
        });
        if (submit.isPresent()) {
            throw new RuntimeException(submit.get());
        }
    }
}
