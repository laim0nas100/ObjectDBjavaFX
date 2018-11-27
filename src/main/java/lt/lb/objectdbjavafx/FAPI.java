package lt.lb.objectdbjavafx;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import javax.jdo.Query;
import lt.lb.commons.F;
import lt.lb.commons.Log;
import lt.lb.commons.containers.BooleanValue;
import lt.lb.commons.containers.Value;
import lt.lb.objectdbjavafx.model.EAVBoolean;
import lt.lb.objectdbjavafx.model.EAVDate;
import lt.lb.objectdbjavafx.model.EAVDecimal;
import lt.lb.objectdbjavafx.model.EAVInteger;
import lt.lb.objectdbjavafx.model.EAVString;
import lt.lb.objectdbjavafx.model.EAVType;
import lt.lb.objectdbjavafx.model.EAValue;
import lt.lb.objectdbjavafx.model.FileEntity;
import lt.lb.objectdbjavafx.model.FileEntityFolder;
import lt.lb.objectdbjavafx.model.Meta;
import lt.lb.objectdbjavafx.model.MetaList;

/**
 * File API 
 * yes, I'm 12
 *
 * @author laim0nas100
 *
 */
public class FAPI {
    
    public static void addAssignFolder(FileEntityFolder folder, FileEntity file) {
        folder.getFiles().add(file);
        file.meta.addAttribute(valueOf(MetaEnums.parentID, folder.id));
    }
    
    public static void report(Optional<Throwable> t) {
        if (t.isPresent()) {
            Throwable get = t.get();
            Log.print("ERROR:" + get.getLocalizedMessage());
            get.printStackTrace();
        }
    }
    
    public static Meta createMeta(String name, EAVType type) {
        Meta meta = new Meta(name, type);
        Q.submit(pm -> {
            pm.makePersistent(meta);
        });
        return meta;
    }
    
    public static Meta getMetaByName(String name) {
        Value<Meta> val = new Value<>();
        Q.submit(pm -> {
            Query q = pm.newQuery(Meta.class);
            q.declareParameters("String find");
            q.setFilter("this.name == find");
            Q.getFirst(val, q, name);
        });
        return val.get();
    }
    
    public static EAValue valueOf(String name, Object ob) {
        Meta meta = getMetaByName(name);
        Objects.requireNonNull(meta, "No meta by name:" + name);
        return FAPI.valueOf(name, ob, meta);
        
    }
    
    public static EAValue valueOf(String name, Object ob, Meta meta) {
        EAValue val = null;
        EAVType recievedType = null;
        
        if (ob instanceof Date) {
            val = new EAVDate();
            recievedType = EAVType.DATE;
        }
        if (ob instanceof String) {
            val = new EAVString();
            recievedType = EAVType.STRING;
        }
        if (ob instanceof Number) {
            if (F.instanceOf(ob, Float.class, Double.class)) {
                val = new EAVDecimal();
                recievedType = EAVType.DECIMAL;
            } else {
                val = new EAVInteger();
                recievedType = EAVType.INTEGER;
            }
            
        }
        if (ob instanceof Boolean) {
            val = new EAVBoolean();
            recievedType = EAVType.BOOLEAN;
        }
        if (val == null) {
            throw new IllegalArgumentException("Unsupported argument" + ob);
        }
        val.meta = meta;
        val.set(ob);
        if (recievedType != val.meta.valType) {
            throw new IllegalArgumentException("EAValue type and meta type missmatch recieved:" + recievedType + " expected:" + val.meta.valType);
        }
        
        return val;
        
    }
    
    public static <T extends FileEntity> T createFile(Supplier<T> sup, String name) {
        
        T ent = sup.get();
        ent.meta = new MetaList();
        Date now = new Date();
        ent.meta.addAttribute(FAPI.valueOf(MetaEnums.fileName, name));
        ent.meta.addAttribute(FAPI.valueOf(MetaEnums.createdDate, now));
        ent.meta.addAttribute(FAPI.valueOf(MetaEnums.lastModifiedDate, now));
        Q.submit((t) -> {
            t.makePersistent(ent);
        });
        return ent;
    }
    
    public static FileEntity createTextFile(String name, String text) {
        FileEntity file = createFile(FileEntity::new, name);
        file.meta.addAttribute(FAPI.valueOf(MetaEnums.textContent, text));
        return file;
    }
    
    public static FileEntityFolder createFolder(String name) {
        FileEntityFolder createFile = createFile(FileEntityFolder::new, name);
        return createFile;
    }
    
    public static FileEntityFolder getMainFolder() {
        Value<FileEntityFolder> val = new Value<>();
        Optional<Throwable> submit = Q.submit((pm) -> {
            Query query = pm.newQuery(FileEntityFolder.class);
            query.declareParameters("String metaName");
            query.setFilter("this.meta.hasMetaName(metaName)");
            
            Q.getFirst(val, query, MetaEnums.root);
        });
        report(submit);
        return val.get();
    }
    
    public static List<FileEntity> getWithAttribute(String name) {
        Value<List<FileEntity>> listValue = new Value<>();
        Q.submit(pm -> {
            Query q = pm.newQuery(FileEntity.class);
            q.declareParameters("String nm");
            q.setFilter("this.meta.hasMetaName(nm)");
            Q.getAll(listValue, q, name);
        });
        return listValue.get();
    }
    
    public static List<EAValue> getValuesByMeta(Meta meta) {
        Value<List<EAValue>> listValue = new Value<>();
        Q.submit(pm -> {
            Query q = pm.newQuery(EAValue.class);
            q.declareParameters("String ID");
            q.setFilter("this.meta.id == ID");
            Q.getAll(listValue, q, meta.id);
        });
        return listValue.get();
    }
    
    public static List<FileEntity> fullTextSearch(String like) {
        Value<List<FileEntity>> listValue = new Value<>();
        Optional<Throwable> submit = Q.submit(pm -> {
            Query q = pm.newQuery(FileEntity.class);
            String ss = "%" + like + "%";
            q.declareParameters("String ss, String metaName");
            q.setFilter("((EAVString) this.meta.get(metaName)).get() like ss");
            Q.getAll(listValue, q, ss, MetaEnums.textContent);
        });
        FAPI.report(submit);
        return listValue.get();
    }
    
    public static Boolean renameFile(String id, String newName) {
        BooleanValue bool = BooleanValue.FALSE();
        Q.submit(pm -> {
            FileEntity objectById = pm.getObjectById(FileEntity.class, id);
            if (objectById != null) {
                EAVString get = F.cast(objectById.meta.get(MetaEnums.fileName));
                get.set(newName);
                bool.setTrue();
            }
        });
        return bool.get();
    }
    
    public static Optional<FileEntityFolder> getParent(FileEntity file) {
        Value<FileEntityFolder> val = new Value<>();
        Q.submit(pm -> {
            Query newQuery = pm.newQuery(FileEntityFolder.class);
            newQuery.declareParameters("String ID");
            newQuery.setFilter("this.id == ID");
            Q.getFirst(val, newQuery, file.meta.getString(MetaEnums.parentID));
        });
        return Optional.ofNullable(val.get());
    }
    
    public static void deleteFile(FileEntity file) {
        Optional<Throwable> submit = Q.submit(pm -> {
            
            if (file instanceof FileEntityFolder) {
                FileEntityFolder folder = F.cast(file);
                Log.print("Deleting folder " + file);
                ArrayList<Runnable> actions = new ArrayList<>();
                folder.getFiles().forEach(child -> {
                    actions.add(() -> {
                        deleteFile(child);
                    });
                });
                actions.forEach(action -> action.run());
                
            }
            Optional<FileEntityFolder> parentOpt = getParent(file);
            
            if (parentOpt.isPresent()) {
                FileEntityFolder parent = parentOpt.get();
                parent.getFiles().remove(file);
            }
            pm.deletePersistent(file);
            
        });
        report(submit);
    }
    
}
