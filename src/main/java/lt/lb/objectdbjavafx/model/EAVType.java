/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lt.lb.objectdbjavafx.model;

import javax.jdo.annotations.PersistenceCapable;

/**
 *
 * @author laim0nas100
 */
@PersistenceCapable
public enum EAVType {
    DECIMAL,INTEGER,STRING,BOOLEAN,DATE
}
