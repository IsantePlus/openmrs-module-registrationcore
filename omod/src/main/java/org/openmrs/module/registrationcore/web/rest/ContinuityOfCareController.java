/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.registrationcore.web.rest;

import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.registrationcore.api.RegistrationCoreService;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseUriSetup;
import org.openmrs.module.xdssender.api.domain.Ccd;
import org.openmrs.module.xdssender.api.service.CcdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Continuity-of-care / roaming retrieval trigger. Given a local patient, pulls their consolidated
 * International Patient Summary (IPS) from SEDISH (the SHR IPS mediator, resolved via OpenCR to the
 * golden record) and returns it for display. Called from the UI once the patient's identity is known.
 *
 * <p>GET {@code /ws/rest/v1/registrationcore/ips?patientUuid=<uuid>}
 */
@Controller
public class ContinuityOfCareController {

    @Autowired
    BaseUriSetup baseUriSetup;

    @Autowired
    @Qualifier("xdsSender.CcdService")
    private CcdService ccdService;

    @RequestMapping(value = "/rest/v1/registrationcore/ips", method = RequestMethod.GET)
    @ResponseBody
    public SimpleObject getIps(@RequestParam("patientUuid") String patientUuid, HttpServletRequest request,
            HttpServletResponse response) throws ResponseException {
        baseUriSetup.setup(request);

        Patient patient = Context.getPatientService().getPatientByUuid(patientUuid);
        if (patient == null) {
            return new SimpleObject().add("error", "Patient not found: " + patientUuid);
        }

        // Reuse the existing continuity-of-care import. When xdssender.ipsEndpoint is configured, this
        // pulls the consolidated IPS; otherwise it falls back to the legacy CCD aggregation.
        Integer documentId = Context.getService(RegistrationCoreService.class).importCcd(patient);
        Ccd document = ccdService.getLocallyStoredCcd(patient);

        return new SimpleObject().add("patientUuid", patientUuid).add("documentId", documentId)
                .add("retrieved", document != null && document.getDocument() != null)
                .add("summary", document != null ? document.getDocument() : null);
    }
}
