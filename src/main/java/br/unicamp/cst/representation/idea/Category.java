/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package br.unicamp.cst.representation.idea;

import java.util.List;

/**
 *
 * @author rgudwin
 */
public interface Category {
    public Idea instantiation(List<Idea> constraints );
    public double membership(Idea idea);
}
