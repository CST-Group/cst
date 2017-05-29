/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.cst.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author suelen
 */
public class NameGenerator {

    private static List<String> listNames = new ArrayList<String>();

    static int rand(int Str, int End) {
        return (int) Math.ceil(Math.random() * (End - Str + 1)) - 1 + Str;
    }

    public String generateWord() {

        String newW = "";
        String vowels = "aeiou";
        boolean find = false;

        int numberOfSyllables = rand(2, 4);
        do {
            Random ra = new Random();
            for (int x = 0; x < numberOfSyllables; x++) {
                int valor = 0;

                do {
                    valor = (97 + ra.nextInt(122 - 97));
                } while (vowels.contains(String.valueOf((char) valor)));

                newW = newW + String.valueOf((char) valor);

                newW = newW + String.valueOf(vowels.charAt(ra.nextInt(vowels.length())));
            }

            find = checkName(newW);

        } while (find == true);

        //System.out.println("A palavra gerada eh: " + newW);
        listNames.add(newW);
        return newW;

    }

    public boolean checkName(String name) {

        boolean find = false;

        for (int i = 0; i < listNames.size(); i++) {

            if (name.equals(listNames.get(i))) {

                find = true;

            }
        }

        return find;

    }

    public static void main(String[] args) {
        int cont = 0;

        while (cont < 10) {
            NameGenerator ng = new NameGenerator();
            System.out.println(" >> " + ng.generateWord());
            cont++;
        }

    }

}
