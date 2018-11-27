/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lt.lb.objectdbjavafx;

import java.util.List;
import javax.jdo.Query;
import lt.lb.commons.Log;
import lt.lb.objectdbjavafx.model.EAValue;
import lt.lb.objectdbjavafx.model.FileEntity;
import lt.lb.objectdbjavafx.model.FileEntityFolder;
import lt.lb.objectdbjavafx.model.Meta;

/**
 *
 * @author laim0nas100
 */
public class JDOTests {
    public static void selectAllFolders(){
        Log.print("Select all folders");
        Q.submit(pm->{
            Log.printLines(Q.getAll(pm.newQuery(FileEntityFolder.class)));
        });
    }
    
    public static void selectJustFiles(){
        Log.print("Select all files");
        Q.submit(pm->{
            Query q = pm.newQuery(FileEntity.class);
            String cls = FileEntityFolder.class.getName();
            
            q.setFilter("!(this instanceof "+cls+")");
            Log.printLines(Q.getAll(q));
        });
    }
    
    public static void selectEAVbyMetaRoot(){
        Log.print("Select EAValue by meta [root]");
        Meta metaByName = FAPI.getMetaByName("root");
        List<EAValue> valuesByMeta = FAPI.getValuesByMeta(metaByName);
        Log.printLines(valuesByMeta);
    }
    
    public static void selectEAVbyMetaFileName(){
        Log.print("Select EAValue by meta [fileName]");
        Meta metaByName = FAPI.getMetaByName("fileName");
        List<EAValue> valuesByMeta = FAPI.getValuesByMeta(metaByName);
        Log.printLines(valuesByMeta);
    }
    
    public static void fullTextSearch1(){
        Log.print("Full text search test with [other]]");
        Log.printLines(FAPI.fullTextSearch("other"));
    }
        public static void fullTextSearch2(){
        Log.print("Full text search test with [text]]");
        Log.printLines(FAPI.fullTextSearch("text"));
    }
        
        public static void fullTextSearch3(){
        Log.print("Full text search test with [some long text]");
        Log.printLines(FAPI.fullTextSearch("some long text"));
    }
    
}
