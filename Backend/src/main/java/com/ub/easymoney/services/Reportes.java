/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ub.easymoney.services;

import com.ub.easymoney.daos.DaoCapital;
import com.ub.easymoney.daos.DaoCliente;
import com.ub.easymoney.daos.DaoCobro;
import com.ub.easymoney.daos.DaoMovimiento;
import com.ub.easymoney.entities.Capital;
import com.ub.easymoney.entities.Cliente;
import com.ub.easymoney.entities.Prestamo;
import com.ub.easymoney.models.ModelClienteLiquidado;
import com.ub.easymoney.models.ModelReporteCapital;
import com.ub.easymoney.models.commons.exceptions.TokenExpiradoException;
import com.ub.easymoney.models.commons.exceptions.TokenInvalidoException;
import com.ub.easymoney.models.commons.reponses.Response;
import com.ub.easymoney.utils.UtilsJWT;
import com.ub.easymoney.utils.UtilsService;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author Ulises Beltrán Gómez - beltrangomezulises@gmail.com
 */
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("/reportes")
public class Reportes {

    /**
     * Genera el modelo con los datos del reporte de capital
     *
     * @param token token de sesion
     * @param fechaInicial fecha inicial del rango a consultar el capital generado
     * @param fechaFinal fecha final del rango a consultar el capital generado
     * @return
     */
    @GET
    @Path("/capital/{fechaInicial}/{fechaFinal}")
    public Response<ModelReporteCapital> reporteCapital(@HeaderParam("Authorization") final String token, @PathParam("fechainicial") final long fechaInicial, @PathParam("fechaFinal") final long fechaFinal) {
        Response<ModelReporteCapital> res = new Response<>();
        try {
            ModelReporteCapital mrc = new ModelReporteCapital();

            UtilsJWT.validateSessionToken(token);
            DaoCobro daoCobro = new DaoCobro();
            DaoMovimiento daoMov = new DaoMovimiento();

            Date fInicial = new Date(fechaInicial);
            Date fFinal = new Date(fechaFinal);

            //consultas
            mrc.setTotalRecuperado(daoCobro.totalCobradoDesdeHasta(fInicial, fFinal));
            List<Integer> cantidadesMovs = daoMov.cantidadesDesdeHasta(fInicial, fFinal);

            long totalIngreso = 0;
            long totalEgreso = 0;
            for (double cantidad : cantidadesMovs) {
                if (cantidad >= 0) {
                    totalIngreso += cantidad;
                } else {
                    totalEgreso += cantidad;
                }
            }
            mrc.setTotalMovsIngreso(totalIngreso);
            mrc.setTotalMovsEgreso(totalEgreso);
            UtilsService.setOkResponse(res, mrc, "Reporte de capital generado en un rango de fechas");
        } catch (TokenExpiradoException | TokenInvalidoException e) {
            UtilsService.setInvalidTokenResponse(res);
        } catch (Exception e) {
            UtilsService.setErrorResponse(res, e);
            e.printStackTrace();
        }

        return res;
    }

    /**
     * Consulta los clientes que ya no tienen prestamos activos, llamados liquidados
     *
     * @param token token de sesion
     * @return obtiene la lista de clientes que ya tienen todos sus prestamos liquidados
     */
    @GET
    @Path("/clientesLiquidados")
    public Response<List<ModelClienteLiquidado>> reporteClientesLiquidados(@HeaderParam("Authorization") final String token) {
        Response<List<ModelClienteLiquidado>> res = new Response<>();
        try {
            UtilsJWT.validateSessionToken(token);

            DaoCliente daoCliente = new DaoCliente();
            List<Cliente> clientes = daoCliente.findAll();
            List<ModelClienteLiquidado> clientesLiquidados = new ArrayList<>();

            for (Cliente cliente : clientes) {
                Date fechaUltimoAbono = null;
                boolean clienteLiquidado = true;
                if (cliente.getPrestamoList().isEmpty()) {
                    clienteLiquidado = false;
                }
                for (Prestamo prestamo : cliente.getPrestamoList()) {
                    boolean todosAbonados = prestamo.getAbonoList().stream().allMatch(a -> a.getAbonado());
                    if (todosAbonados) {
                        int totalCantidadAbonada = prestamo.getAbonoList().stream().mapToInt(a -> a.getCantidad()).sum();
                        if (totalCantidadAbonada == prestamo.getCantidadPagar()) {
                            prestamo.getAbonoList().sort((a1, a2) -> a1.getAbonoPK().getFecha().compareTo(a2.getAbonoPK().getFecha()));
                            if (fechaUltimoAbono == null) {
                                fechaUltimoAbono = prestamo.getAbonoList().get(prestamo.getAbonoList().size() - 1).getAbonoPK().getFecha();
                            } else {
                                Date fechaUltimoAbonoPrestamoActual = fechaUltimoAbono = prestamo.getAbonoList().get(prestamo.getAbonoList().size() - 1).getAbonoPK().getFecha();
                                if (fechaUltimoAbonoPrestamoActual.after(fechaUltimoAbono)) {
                                    fechaUltimoAbono = fechaUltimoAbonoPrestamoActual;
                                }
                            }
                        } else {
                            clienteLiquidado = false;
                            break;
                        }
                    } else {
                        clienteLiquidado = false;
                        break;
                    }
                }
                if (clienteLiquidado) {
                    ModelClienteLiquidado mcl = new ModelClienteLiquidado(cliente.getId(), cliente.getNombre(), cliente.getApodo(), cliente.getTelefono(), fechaUltimoAbono);
                    clientesLiquidados.add(mcl);
                }
                fechaUltimoAbono = null;
            }
            UtilsService.setOkResponse(res, clientesLiquidados, "Clientes liquidados");
        } catch (TokenExpiradoException | TokenInvalidoException e) {
            UtilsService.setInvalidTokenResponse(res);
        } catch (Exception e) {
            UtilsService.setErrorResponse(res, e);
        }
        return res;
    }

    /**
     * Consulta el capital fisico que existe
     *
     * @param token token de sesion
     * @return Objecto de capital fisico
     * @throws Exception
     */
    @GET
    @Path("/capitalFisico")
    public Capital consultaCapitaFisico(@HeaderParam("Authorization") final String token) throws Exception {
        UtilsJWT.validateSessionToken(token);
        return new DaoCapital().findFirst();
    }

}
