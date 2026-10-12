/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.deza;

/**
 *
 * @author Admin
 */
public record Item(String name, boolean imbue) {
    public Item(String name) { this(name, false); }
}
