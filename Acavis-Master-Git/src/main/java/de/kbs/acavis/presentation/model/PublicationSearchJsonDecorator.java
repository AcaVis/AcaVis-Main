/*****************************************
 * This file is part of AcaVis, a tool for research of scientific
 * literature using the benefits of visualizations.
 * Copyright (C) 2014 - Sebastian Holzki
 * 
 * AcaVis is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License Version 2
 * as published by the Free Software Foundation;
 *****************************************/
package de.kbs.acavis.presentation.model;

import java.io.IOException;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;

import de.kbs.acavis.integration.model.PublicationSearchData;
import de.kbs.acavis.service.SerializationHelper;


/**
 * Decorates a given Publication-search result to be serialized by the jackson-integration of Spring MVC. All Data available via getters in
 * this class appear in the serialized JSON, as non of the methods is marked using {@link JsonIgnore} or similar annotations.
 * 
 * @author Sebastian
 *
 */
public class PublicationSearchJsonDecorator
{
  private PublicationSearchData data = null;


  public PublicationSearchJsonDecorator(PublicationSearchData data)
  {
    this.data = data;
  }


  public String getId()
  {
    String serializedObject = "";

    try
    {
      serializedObject = SerializationHelper.serializePublicationIdentifierBase64(data.getId());
    }
    catch (IOException e)
    {}

    return serializedObject;
  }


  public String getTitle()
  {
    return data.getTitle();
  }


  public List<String> getAuthors()
  {
    return data.getAuthors();
  }


  public Number getMetric()
  {
    return data.getMetric();
  }


  public String getTeaserText()
  {
    return data.getTeaserText();
  }
}
