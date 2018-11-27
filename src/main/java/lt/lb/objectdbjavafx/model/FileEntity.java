/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lt.lb.objectdbjavafx.model;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.PrimaryKey;
import lt.lb.commons.UUIDgenerator;


/**
 *
 * @author laim0nas100
 */
@PersistenceCapable
public class FileEntity {

    @PrimaryKey
    public String id = UUIDgenerator.nextUUID(this.getClass().getSimpleName());
    
    public Long count(){
        return 1L;
    }
    public MetaList meta;
    
    @Override
    public String toString(){
        return id;
    }

}
