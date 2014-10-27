/*****************************************
 * This file is part of AcaVis, a tool for research of scientific
 * literature using the benefits of visualizations.
 * Copyright (C) 2014 - Sebastian Holzki
 * 
 * AcaVis is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License Version 2
 * as published by the Free Software Foundation;
 *****************************************/
package de.kbs.acavis.service;

import de.kbs.acavis.integration.PublicationProvider;


public class IntegrationProviderFactory
{
  public static PublicationProvider createPublicationProvider(String providerClassName)
    throws InvalidIntegrationException
  {
    if (!IntegrationMetainfos.isIntegratingProvider(providerClassName, PublicationProvider.class))
      throw new InvalidIntegrationException("The given provider is not applicable for publication-related requests.");

    try
    {
      return (PublicationProvider) Class.forName(providerClassName).newInstance();
    }
    catch (ClassNotFoundException | InstantiationException | IllegalAccessException e)
    {
      throw new InvalidIntegrationException("No integrated provider found: \"" + providerClassName + "\"");
    }
  }


  public static PublicationProvider createDefaultPublicationProvider()
    throws InvalidIntegrationException
  {
    if (!IntegrationMetainfos.isIntegratingProvider(IntegrationMetainfos.getDefaultIntegration().getName(),
        PublicationProvider.class))
      throw new InvalidIntegrationException("The default provider is not applicable for publication-related requests.");

    try
    {
      return (PublicationProvider) IntegrationMetainfos.getDefaultIntegration().newInstance();
    }
    catch (InstantiationException | IllegalAccessException e)
    {
      throw new InvalidIntegrationException("No integrated default provider found!");
    }
  }


  public static PublicationProvider createAuthorProvider(String providerClassName)
    throws InvalidIntegrationException
  {
    if (!IntegrationMetainfos.isIntegratingProvider(providerClassName, PublicationProvider.class))
      throw new InvalidIntegrationException("The given provider is not applicable for author-related requests.");

    try
    {
      return (PublicationProvider) Class.forName(providerClassName).newInstance();
    }
    catch (ClassNotFoundException | InstantiationException | IllegalAccessException e)
    {
      throw new InvalidIntegrationException("No integrated provider found: \"" + providerClassName + "\"");
    }
  }


  public static PublicationProvider createDefaultAuthorProvider()
    throws InvalidIntegrationException
  {
    if (!IntegrationMetainfos.isIntegratingProvider(IntegrationMetainfos.getDefaultIntegration().getName(),
        PublicationProvider.class))
      throw new InvalidIntegrationException("The default provider is not applicable for author-related requests.");

    try
    {
      return (PublicationProvider) IntegrationMetainfos.getDefaultIntegration().newInstance();
    }
    catch (InstantiationException | IllegalAccessException e)
    {
      throw new InvalidIntegrationException("No integrated default provider found!");
    }
  }
}
