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
 * The class declares the interface for publication-data from the integration-layer to the service-layer.<br />
 * <br />
 * In order to force the integrations to provide a serializable version of their implementation, an abstract class is used above a plain
 * interface.<br />
 * Id and title are mandatory, other fields can be omitted but should be added when possible.
 * 
 * @author Sebastian
 *
 */
public abstract class PublicationData
  implements Serializable
{
  private static final long serialVersionUID = -5184520796087875867L;


  public PublicationData(PublicationIdentifier identifier, String title)
  {}


  public abstract PublicationIdentifier getId();


  public abstract String getTitle();


  public abstract int getYear();


  public abstract List<String> getAuthors();


  public abstract String getAbstractText();


  public abstract String getSource();


  public abstract Set<String> getKeywords();


  public abstract Set<String> getDisciplines();


  public abstract Set<String> getSubDisciplines();


  public abstract Set<String> getUrls();


  public abstract Set<PublicationData> getReferences();


  public abstract Set<PublicationData> getCitations();


  public abstract int getReferenceCount();


  public abstract int getCitationCount();
}
