/*****************************************
 * This file is part of AcaVis, a tool for research of scientific
 * literature using the benefits of visualizations.
 * Copyright (C) 2014 - Sebastian Holzki
 * 
 * AcaVis is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License Version 2
 * as published by the Free Software Foundation;
 *****************************************/
package de.kbs.acavis.integration.model;

import de.kbs.acavis.integration.IdentifierException;


public class PublicationIdentifierFactory
{
  /**
   * Factory for creating {@link PublicationIdentifier} with proper mas-id.
   * 
   * @param masId
   *          The MAs-id
   * @return A publication-identifier
   * @throws IdentifierException
   *           If the mas-id has of invalid format
   */
  public static PublicationIdentifier createMasPublicationIdentifier(int masId)
    throws IdentifierException
  {
    PublicationIdentifier identifier = new PublicationIdentifier();

    identifier.setMasIdentifier(masId);

    return identifier;
  }


  /**
   * Factory for creating {@link PublicationIdentifier} with proper doi-identifier.
   * 
   * @param doi
   *          The DOI-identifier
   * @return A publication-identifier
   * @throws IdentifierException
   *           If the DOI-identifier has invalid format
   */
  public static PublicationIdentifier createDoiPublicationIdentifier(String doi)
    throws IdentifierException
  {
    PublicationIdentifier identifier = new PublicationIdentifier();

    identifier.setDoiIdentifier(doi);

    return identifier;
  }
}
