/*
 * Author: steve.killelay
 * Last Updated: 21/02/19 22:05
 *
 * Copyright {c} 2019, ITG
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice,  this list of conditions and the following disclaimer in the documentation  and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */
package com.itg.plugins.confluence.rest;

import com.atlassian.json.jsonorg.JSONArray;
import com.atlassian.json.jsonorg.JSONObject;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.itg.plugins.confluence.implementation.utils.Helpers;
import com.itg.plugins.confluence.implementation.models.JobStepsModel;
import com.itg.plugins.confluence.implementation.providers.JobStepsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Steve.Killelay
 */
@Path("js")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public class JobStepsService {

	private static final Logger LOGGER = LoggerFactory.getLogger(JobStepsService.class);
	private final JobStepsProvider _jobStepsProvider;

	@Autowired
	public JobStepsService(final JobStepsProvider provider) {

		this._jobStepsProvider = provider;
	}

	@AnonymousAllowed
	@GET
	@Path("{sessionKey:([0-9]*)}/{id:([0-9]*)}")
	public Response getSteps(@PathParam("sessionKey") final String sessionId, @PathParam("id") final int id) {

		try {
			List<Map<String, Object>> data = _jobStepsProvider.getSteps(sessionId, id);

			JSONArray json = Helpers.mapToJson(data);
			return Response.ok(json.toString(), MediaType.APPLICATION_JSON).build();
		} catch (SQLException e) {

			LOGGER.error("SQL error while retrieving client details -->", e);
			return Response.noContent().build();
		}
	}

	@POST
	@Path("{sessionKey:([0-9]*)}")
	public Response createStep(@PathParam("sessionKey") final long sessionKey, JobStepsModel steps) {

		try {
			steps.sessionKey = sessionKey;
			Map<String, Object> data = _jobStepsProvider.createStep(steps);

			JSONObject json = Helpers.mapToJson(data);
			return Response.ok(json.toString(), MediaType.APPLICATION_JSON).build();
		} catch (SQLException e) {

			LOGGER.error("SQL error while creating a new client -->", e);
			return Response.serverError().type(MediaType.APPLICATION_JSON).build();
		}
	}

	@PUT
	@Path("{sessionKey:([0-9]*)}/{id:([0-9]*)}")
	public Response updateSteps(@PathParam("id") final int id, JobStepsModel steps) {

		try {
			Map<String, Object> data = _jobStepsProvider.updateStep(steps);

			JSONObject json = Helpers.mapToJson(data);
			return Response.ok(json.toString(), MediaType.APPLICATION_JSON).build();
		} catch (SQLException e) {

			LOGGER.error("SQL error while creating a new client -->", e);
			return Response.serverError().type(MediaType.APPLICATION_JSON).build();
		}
	}

	@DELETE
	@Path("{sessionKey:([0-9]*)}/{id:([0-9]*)}")
	public Response deleteStep(@PathParam("id") final int id) {

		try {
			_jobStepsProvider.deleteStep(id);
			return Response.ok().status(202).build();
		} catch (SQLException e) {

			LOGGER.error("Error while deleting the record -->", e);
			return Response.serverError().build();
		}
	}
}
