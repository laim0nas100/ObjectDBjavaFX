/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lt.lb.objectdbjavafx.model;

import java.io.Serializable;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.PrimaryKey;
import lt.lb.commons.UUIDgenerator;

/**
 *
 * @author laim0nas100
 */
@PersistenceCapable
public class Meta implements Serializable {
public static transient String clsName = Meta.class.getName();
    
    @PrimaryKey
    public String id = UUIDgenerator.nextUUID(this.getClass().getSimpleName());
    public String name;
    public EAVType valType;
    
    public Meta(){};
    public Meta(String str, EAVType type){
        this.name = str;
        this.valType = type;
    }

}
