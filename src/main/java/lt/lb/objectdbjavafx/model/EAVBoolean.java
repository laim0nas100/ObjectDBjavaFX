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
public class EAVBoolean extends EAValue {

    public Boolean bool;

    @Override
    public Boolean get() {
        return bool;
    }

    @Override
    public void set(Object v) {
        bool = F.cast(v);
    }

}
