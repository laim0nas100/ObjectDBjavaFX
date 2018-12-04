/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lt.lb.objectdbjavafx.model;

import javax.jdo.annotations.PersistenceCapable;
import lt.lb.commons.F;

/**
 *
 * @author laim0nas100
 */
@PersistenceCapable
public class EAVString extends EAValue{
public static transient String clsName = EAVString.class.getName();
    public String str;
    @Override
    public String get() {
        return str;
    }

    @Override
    public void set(Object v) {
        this.str = F.cast(v);
    }
    
}
