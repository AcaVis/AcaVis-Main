/*****************************************
 * This file is part of AcaVis, a tool for research of scientific
 * literature using the benefits of visualizations.
 * Copyright (C) 2014 - Sebastian Holzki
 * 
 * AcaVis is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License Version 2
 * as published by the Free Software Foundation;
 *****************************************/
package de.kbs.acavis.presentation.controller;

import java.io.IOException;

import javax.persistence.NoResultException;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import de.kbs.acavis.integration.IdentifierException;
import de.kbs.acavis.integration.IntegrationUnavailableException;
import de.kbs.acavis.integration.PublicationProvider;
import de.kbs.acavis.integration.PublicationUnavailableException;
import de.kbs.acavis.integration.UnimplementedFeatureException;
import de.kbs.acavis.integration.model.PublicationData;
import de.kbs.acavis.integration.model.PublicationIdentifier;
import de.kbs.acavis.integration.model.PublicationLinkData;
import de.kbs.acavis.presentation.controller.NavigationInterceptor.WithoutNavigation;
import de.kbs.acavis.service.IntegrationProviderFactory;
import de.kbs.acavis.service.InvalidIntegrationException;
import de.kbs.acavis.service.SearchOptions;
import de.kbs.acavis.service.SerializationHelper;


@Controller
public class PublicationController
{
  // private static final Logger logger = LoggerFactory.getLogger(PublicationController.class);

  @RequestMapping(value = "/publication", method = RequestMethod.GET)
  public String show(Model pageVariables, HttpSession session,
      @RequestParam(value = "pubid", required = true) String identifierString)
  {
    pageVariables.addAttribute("section", "publication");
    SearchOptions options = new SearchOptions(session, true);

    try
    {
      PublicationIdentifier identifier = SerializationHelper.deserializePublicationIdentifierBase64(identifierString);

      PublicationProvider publicationProvider = null;
      try
      {
        publicationProvider = IntegrationProviderFactory.createPublicationProvider(options.getIntegration());
      }
      catch (InvalidIntegrationException e)
      {
        // Try default provider, if it fails, show an alert
        publicationProvider = IntegrationProviderFactory.createDefaultPublicationProvider();
      }

      PublicationData rootPublication = publicationProvider.getPublicationOverview(identifier);

      pageVariables.addAttribute("publication", rootPublication);
    }
    catch (ClassNotFoundException | IOException e)
    {
      return alert(pageVariables, "Malformed URL!", "Publication identifier contains rubbish.");
    }
    catch (InvalidIntegrationException e)
    {
      return alert(pageVariables, "No Provider found!",
          "You may have to select and save a different default-provider in the serach options.");
    }
    catch (UnimplementedFeatureException e)
    {
      return alert(pageVariables, "Integration-provider doesn't support this type of query!",
          "We are sorry, but there is nothing you can do about it.");
    }
    catch (IntegrationUnavailableException e)
    {
      return alert(pageVariables, "Integration-provider is currently not available!",
          "Please try again in a few minutes. If this state doesn't change for a longer period, please contact an administrator.");
    }
    catch (IdentifierException e)
    {
      return alert(pageVariables, "Integration-provider has no support for given publication-identifier(s)!",
          "We are sorry but there is nothing you can do about it.");
    }
    catch (NoResultException e)
    {
      return alert(pageVariables, "Something went internally wrong, not your fault!", e.getMessage());
    }
    catch (PublicationUnavailableException e)
    {
      return alert(pageVariables, "No publication available!", e.getMessage());
    }

    return "publication";
  }


  @RequestMapping(value = "/publication/links", method = RequestMethod.GET)
  @ResponseBody
  @WithoutNavigation
  public PublicationLinkData links(Model pageVariables, HttpSession session,
      @RequestParam(value = "pubid", required = true) String identifierString,
      @RequestParam(value = "limit", required = false, defaultValue = "100") int limit)
  {
    SearchOptions options = new SearchOptions(session, true);

    try
    {
      PublicationIdentifier identifier = SerializationHelper.deserializePublicationIdentifierBase64(identifierString);

      PublicationProvider publicationProvider = null;
      try
      {
        publicationProvider = IntegrationProviderFactory.createPublicationProvider(options.getIntegration());
      }
      catch (InvalidIntegrationException e)
      {
        // Try default provider, if it fails, show an alert
        publicationProvider = IntegrationProviderFactory.createDefaultPublicationProvider();
      }

      return publicationProvider.getPublicationLinks(identifier, limit, limit);
    }
    catch (ClassNotFoundException | IOException e)
    {
      // TODO show an alert
    }
    catch (InvalidIntegrationException e)
    {
      // TODO show an alert
    }
    catch (UnimplementedFeatureException e)
    {
      // TODO show an alert
      e.printStackTrace();
    }
    catch (IntegrationUnavailableException e)
    {
      // TODO show an alert
      e.printStackTrace();
    }
    catch (IdentifierException e)
    {
      // TODO show an alert
      e.printStackTrace();
    }

    return null;
  }


  private String alert(Model pageVariables, String majorText, String minorText)
  {
    pageVariables.addAttribute("alert_majortext", majorText);
    pageVariables.addAttribute("alert_minortext", minorText);
    return "publication-error";
  }

}
