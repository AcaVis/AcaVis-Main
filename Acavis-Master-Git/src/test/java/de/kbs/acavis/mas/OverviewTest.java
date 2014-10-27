/*****************************************
 * This file is part of AcaVis, a tool for research of scientific
 * literature using the benefits of visualizations.
 * Copyright (C) 2014 - Sebastian Holzki
 * 
 * AcaVis is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License Version 2
 * as published by the Free Software Foundation;
 *****************************************/
package de.kbs.acavis.mas;

import java.util.LinkedList;
import java.util.List;

import de.kbs.acavis.integration.IdentifierException;
import de.kbs.acavis.integration.IntegrationUnavailableException;
import de.kbs.acavis.integration.UnimplementedFeatureException;
import de.kbs.acavis.integration.model.PublicationData;
import de.kbs.acavis.integration.model.PublicationIdentifier;
import de.kbs.acavis.integration.model.PublicationIdentifierFactory;
import de.kbs.acavis.service.InvalidIntegrationException;
import de.kbs.acavis.service.controller.PublicationManager;


public class OverviewTest
{

  public static void main(String[] args)
    throws IdentifierException, InvalidIntegrationException, UnimplementedFeatureException,
    IntegrationUnavailableException
  {
    String integrationName = "de.kbs.acavis.integration.controller.MicrosoftAcademicIntegration";
    LinkedList<PublicationIdentifier> identifiers = new LinkedList<PublicationIdentifier>();

    // Tufte: Visual display of quantitative information
    identifiers.add(PublicationIdentifierFactory.createMasPublicationIdentifier(1258503));
    // identifiers.add(PublicationIdentifierFactory.createMasPublicationIdentifier(123));

    PublicationManager manager = new PublicationManager(integrationName);
    List<PublicationData> publications = manager.getPublicationsData(identifiers, true);

    PublicationData tufte = publications.get(0);

    System.out.println("Tufte: Visual display of quantitative information");
    System.out.println("--------");
    // System.out.println("Authors: " + tufte.getAuthors().size());
    // System.out.println(Arrays.toString(tufte.getAuthors().toArray()));
    // System.out.println("Disciplines: " + tufte.getDisciplines().size());
    // System.out.println(Arrays.toString(tufte.getDisciplines().toArray()));
    // System.out.println("Subdisciplines: " + tufte.getSubDisciplines().size());
    // System.out.println(Arrays.toString(tufte.getSubDisciplines().toArray()));
    System.out.println("References: " + tufte.getReferenceCount());
    System.out.println("Citations: " + tufte.getCitationCount());
  }

}
