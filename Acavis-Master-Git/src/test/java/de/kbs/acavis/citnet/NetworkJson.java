/*****************************************
 * This file is part of AcaVis, a tool for research of scientific
 * literature using the benefits of visualizations.
 * Copyright (C) 2014 - Sebastian Holzki
 * 
 * AcaVis is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License Version 2
 * as published by the Free Software Foundation;
 *****************************************/
package de.kbs.acavis.citnet;

import de.kbs.acavis.integration.IdentifierException;
import de.kbs.acavis.integration.IntegrationUnavailableException;
import de.kbs.acavis.integration.UnimplementedFeatureException;
import de.kbs.acavis.service.InvalidIntegrationException;
import de.kbs.acavis.service.controller.ApplicationContextProvider;
import de.kbs.acavis.service.controller.CollectionManager;
import de.kbs.acavis.service.controller.EntityNotFoundException;
import de.kbs.acavis.service.models.visualization.PublicationCitationNetwork;


public class NetworkJson
{

  public static void main(String[] args)
    throws EntityNotFoundException, InvalidIntegrationException, UnimplementedFeatureException,
    IntegrationUnavailableException, IdentifierException
  {
    CollectionManager collectionManager =
      ApplicationContextProvider.getApplicationContext().getBean(CollectionManager.class);

    PublicationCitationNetwork net =
      collectionManager.collectionCitations(3, "de.kbs.acavis.integration.controller.MicrosoftAcademicIntegration");

    net.prepareForJsonOutput();

    System.out.println("Clusters: " + net.clusters);
    System.out.println("Nodes: " + net.nodes);
    System.out.println("Links: " + net.links);
  }

}
