/*
 * Copyright (C) 2017 Ulises Beltrán Gómez --- beltrangomezulises@gmail.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.ub.easymoney.utils.commons;

import com.ub.easymoney.utils.commons.ForeignKeyException;
import com.ub.easymoney.utils.commons.IEntity;
import com.ub.easymoney.utils.commons.ManagerFacade;
import com.ub.easymoney.utils.commons.ManagerSQL;
import com.ub.easymoney.models.commons.ModelCantidad;
import com.ub.easymoney.models.commons.enums.Status;
import com.ub.easymoney.models.commons.exceptions.EntidadYaExistenteException;
import com.ub.easymoney.models.commons.exceptions.TokenExpiradoException;
import com.ub.easymoney.models.commons.exceptions.TokenInvalidoException;
import com.ub.easymoney.models.commons.reponses.Response;
import com.ub.easymoney.utils.UtilsService;
import static com.ub.easymoney.utils.UtilsService.setErrorResponse;
import static com.ub.easymoney.utils.UtilsService.setInvalidTokenResponse;
import static com.ub.easymoney.utils.UtilsService.setOkResponse;
import static com.ub.easymoney.utils.UtilsService.setWarningResponse;
import java.security.InvalidParameterException;
import java.util.List;
import java.util.Map;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 * clase de servicios generales LCRUD para entidades que no requiere profundidad de acceso
 *
 * @author Ulises Beltrán Gómez --- beltrangomezulises@gmail.com
 * @param <T> entidad a manejar por esta clase servicio
 * @param <K> tipo de dato de llave primaria de la entidad a menejar por esta clase servicio
 */
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ServiceFacade<T extends IEntity<K>, K> {

    protected ManagerSQL<T, K> manager;

    public ServiceFacade(ManagerSQL<T, K> manager) {
        this.manager = manager;
    }

    public final ManagerFacade<T, K> getManager() {
        return manager;
    }
    
    @GET
    public Response<List> findAll(@HeaderParam("Authorization") String token,
            @QueryParam("select") String select,
            @QueryParam("from") Integer from,
            @QueryParam("to") Integer to,
            @QueryParam("orderBy") String orderBy) {
        Response response = new Response();
        try {
            manager.setToken(token);
            if (select == null) {
                setOkResponse(response, manager.findAll(), "Entidades encontradas");
            } else {
                setOkResponse(response, manager.findAll(select, orderBy, from, to), "Entidades encontradas");
            }
        } catch (TokenExpiradoException | TokenInvalidoException e) {
            setInvalidTokenResponse(response);
        } catch (Exception ex) {
            setErrorResponse(response, ex);
        }
        return response;
    }

    /**
     * obtiene una entidad en particular por su identificador de esta clase servicio
     *
     * @param token token de sesion
     * @param id identificador de la entidad buscada
     * @return response, con su campo data asignado con la entidad buscada
     */
    @GET
    @Path("/{id}")
    public Response<T> detalle(@HeaderParam("Authorization") String token, @PathParam("id") String id) {
        Response response = new Response();
        try {
            this.manager.setToken(token);
            response.setData(manager.findOne(manager.stringToKey(id)));
            response.setMessage("Entidad encontrada");
        } catch (TokenExpiradoException | TokenInvalidoException ex) {
            setInvalidTokenResponse(response);
        } catch (Exception e) {
            setErrorResponse(response, e);
        }
        return response;
    }

    /**
     * persiste la entidad de esta clase servicio en base de datos
     *
     * @param token token de sesion
     * @param t entidad a persistir en base de datos
     * @return response con el estatus y el mensaje
     */
    @POST
    public Response<T> alta(@HeaderParam("Authorization") String token, T t) {
        Response response = new Response();
        try {
            this.manager.setToken(token);
            response.setData(manager.persist(t));
            response.setMessage("Entidad persistida");
        } catch (TokenExpiradoException | TokenInvalidoException ex) {
            setInvalidTokenResponse(response);
        } catch (EntidadYaExistenteException e) {
            setWarningResponse(response, e.getMessage(), "La entidad ya existe en el sistema");
        } catch (InvalidParameterException e) {
            setWarningResponse(response, e.getMessage(), "");
        } catch (Exception e) {
            setErrorResponse(response, e);
        }
        return response;
    }

    /**
     * actualiza la entidad proporsionada a su equivalente en base de datos, tomando como referencia su identificador
     *
     * @param token token de sesion
     * @param t entidad con los datos actualizados
     * @return Response, en data asignado con la entidad que se actualizó
     */
    @PUT
    public Response<T> modificar(@HeaderParam("Authorization") String token, T t) {
        Response response = new Response();
        try {
            this.manager.setToken(token);
            manager.update(t);
            response.setData(t);
            response.setMessage("Entidad actualizada");
        } catch (TokenExpiradoException | TokenInvalidoException ex) {
            setInvalidTokenResponse(response);
        } catch (EntidadYaExistenteException e) {
            UtilsService.setWarningResponse(response, e.getMessage(), "La entidad ya existe en el sistema");
        } catch (InvalidParameterException e) {
            UtilsService.setWarningResponse(response, e.getMessage(), null);
        } 
        catch (Exception e) {
            setErrorResponse(response, e);
        }
        return response;
    }

    /**
     * eliminar la entidad proporsionada
     *
     * @param token token de sesion
     * @param t entidad proporsionada
     * @return
     */
    @DELETE
    public Response<T> eliminar(@HeaderParam("Authorization") String token, T t) {
        Response response = new Response();
        try {
            this.manager.setToken(token);
            manager.delete(t.obtenerIdentificador());
            setOkResponse(response, t, "Entidad Eliminada");
        } catch (TokenExpiradoException | TokenInvalidoException ex) {
            setInvalidTokenResponse(response);
        } catch (ForeignKeyException e) {
            response.setStatus(Status.WARNING);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            setErrorResponse(response, e);
        }
        return response;
    }

    /**
     * cuenta la cantidad de entidades existentes
     *
     * @return numero con la cantidad existente de entidades actualmente
     * @throws Exception si existe un error de I/O con la base de datos
     */
    @GET
    @Path("/count")
    public ModelCantidad contar() throws Exception {
        return new ModelCantidad(manager.count());
    }

}
