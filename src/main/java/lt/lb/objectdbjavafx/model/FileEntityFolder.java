/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lt.lb.objectdbjavafx.model;

import java.util.HashSet;
import java.util.Set;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.annotations.PersistenceCapable;
import lt.lb.commons.containers.NumberValue;
import lt.lb.objectdbjavafx.Q;

/**
 *
 * @author laim0nas100
 */
@PersistenceCapable
public class FileEntityFolder extends FileEntity {

    private Set<FileEntity> files = new HashSet<>();
    

    public Set<FileEntity> getFiles() {
        return files;
    }

    @Override
    public Long count() {
        Long c = super.count();
        for(FileEntity fe:files){
            c+= fe.count();
        }
        return c;
    }

    public void setFiles(Set<FileEntity> files) {
        this.files = files;
    }

}
