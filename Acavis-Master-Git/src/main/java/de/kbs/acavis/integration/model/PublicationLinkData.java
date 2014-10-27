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

import java.io.Serializable;
import java.util.List;
import java.util.Set;


/**
 * Contains the data necessary to create a citation-network or citing-/cited-paper-lists. Id and title are mandatory, year and authors may
 * be omitted (by convention) but should be added when possible.<br />
 * <br />
 * Sub-classes must be serializable so this abstract class implements {@link Serializable}.
 * 
 * @author Sebastian
 *
 */
public abstract class PublicationLinkData
  implements Serializable
{
  private static final long serialVersionUID = -6766327595616479174L;


  public abstract PublicationIdentifier getId();


  public abstract String getTitle();


  public abstract int getYear();


  public abstract List<String> getAuthors();


  public abstract Set<PublicationLinkData> getReferences();


  public abstract Set<PublicationLinkData> getCitations();


  public abstract int getReferenceCount();


  public abstract int getCitationCount();
}
