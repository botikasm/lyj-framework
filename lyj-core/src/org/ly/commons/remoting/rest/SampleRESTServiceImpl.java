/*
 * LY (ly framework)
 * This program is a generic framework.
 * Support: Please, contact the Author on http://www.smartfeeling.org.
 * Copyright (C) 2014  Gian Angelo Geminiani
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

package org.ly.commons.remoting.rest;


import org.json.JSONException;
import org.json.JSONObject;
import org.ly.commons.remoting.rest.annotations.*;

import java.util.LinkedList;
import java.util.List;

@Path("/test")
public class SampleRESTServiceImpl extends RESTService {

    public SampleRESTServiceImpl() {
    }

    @GET
    @Path("/all")
    public List getAll() throws JSONException {
        final List result = new LinkedList();
        for(int i=0;i<10;i++){
            final JSONObject item = new JSONObject();
            item.putOpt("index", i);
            result.add(item);
        }
        return result;
    }

    @GET
    @Path("{id}")
    public Object get(@PathParam("id") String id){

        return "passed " + id;
    }

    @GET
    @Path("{token}/{id}")
    public Object get(@PathParam("token") String token, @PathParam("id")String id){

        return "passed " + token + "-" + id;
    }

    @GET
    @Path("/form")
    public Object formGET(@FormParam("param1") String param){

        return "passed: " + param;
    }

    @POST
    @Path("/form")
    public Object formPOST(@FormParam("param1") String param){

        return "passed: " + param;
    }


    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

}
