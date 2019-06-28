/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.cst.util;

import br.unicamp.cst.representation.owrl.AbstractObject;

/**
 *
 * @author rgudwin
 */
public interface AbstractObjectEditorListener {
    
    public void notifyRootChange(AbstractObject newAO);
    
}
