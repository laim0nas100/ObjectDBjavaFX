/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lt.lb.objectdbjavafx.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Optional;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.PrimaryKey;
import lt.lb.commons.ArrayOp;
import lt.lb.commons.F;
import lt.lb.commons.UUIDgenerator;
import lt.lb.commons.containers.tuples.Tuples;
import lt.lb.commons.interfaces.ValueProxy;

/**
 *
 * @author laim0nas100
 */
@PersistenceCapable
public class EAValue implements Serializable, ValueProxy<Object> {

    @PrimaryKey
    public String id = UUIDgenerator.nextUUID(this.getClass().getSimpleName());

    public Meta meta;

    public EAValue() {
    }

    @Override
    public String toString() {
        return this.id + " " + get();
    }

    @Override
    public Object get() {
        return null;
    }

    @Override
    public void set(Object v) {
    }

}
