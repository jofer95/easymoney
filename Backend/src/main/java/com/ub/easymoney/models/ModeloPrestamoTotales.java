/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ub.easymoney.models;

/**
 * modelo para obtener en el detalle del prestamo, cuando queremos sacar los totales de un prestamo en particular
 * @author Ulises Beltrán Gómez --- beltrangomezulises@gmail.com
 */
public class ModeloPrestamoTotales {

    private int totalAbonado;
    private int totalMultado;
    private int totalRecuperado;
    private int porcentajePagado;

    public ModeloPrestamoTotales() {
    }

    public ModeloPrestamoTotales(int totalAbonado, int totalMultado, int totalRecuperado, int porcentajePagado) {
        this.totalAbonado = totalAbonado;
        this.totalMultado = totalMultado;
        this.totalRecuperado = totalRecuperado;
        this.porcentajePagado = porcentajePagado;
    }

    public int getTotalAbonado() {
        return totalAbonado;
    }

    public void setTotalAbonado(int totalAbonado) {
        this.totalAbonado = totalAbonado;
    }

    public int getTotalMultado() {
        return totalMultado;
    }

    public void setTotalMultado(int totalMultado) {
        this.totalMultado = totalMultado;
    }

    public int getTotalRecuperado() {
        return totalRecuperado;
    }

    public void setTotalRecuperado(int totalRecuperado) {
        this.totalRecuperado = totalRecuperado;
    }

    public int getPorcentajePagado() {
        return porcentajePagado;
    }

    public void setPorcentajePagado(int porcentajePagado) {
        this.porcentajePagado = porcentajePagado;
    }

}