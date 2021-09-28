/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.cst.util.wme;

import br.unicamp.cst.representation.wme.Idea;

/**
 *
 * @author rgudwin
 */
public interface IdeaEditorListener {
    
    public void notifyRootChange(Idea newAO);
    
}
