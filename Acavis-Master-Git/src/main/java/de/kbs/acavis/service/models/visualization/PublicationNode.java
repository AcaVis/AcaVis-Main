/*****************************************
 * This file is part of AcaVis, a tool for research of scientific
 * literature using the benefits of visualizations.
 * Copyright (C) 2014 - Sebastian Holzki
 * 
 * AcaVis is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License Version 2
 * as published by the Free Software Foundation;
 *****************************************/
package de.kbs.acavis.service.models.visualization;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import de.kbs.acavis.integration.model.PublicationIdentifier;
import de.kbs.acavis.service.SerializationHelper;


public class PublicationNode
  extends Node
  implements Comparable<PublicationNode>
{
  // Structural Information
  public boolean root = false;

  // Labeling Information
  public String publicationIdentifier = "";
  public List<String> authors = null;
  public String firstAuthor = "";
  public String title = "";
  public int year = 0;

  // Cluster and Metrics
  public HashMap<String, Double> metrics = new HashMap<String, Double>();


  public PublicationNode(int id, boolean root, PublicationIdentifier identifier, String title, List<String> authors,
      int year)
  {
    super(id, title);

    try
    {
      this.publicationIdentifier = SerializationHelper.serializePublicationIdentifierBase64(identifier);
    }
    catch (IOException e)
    {}

    this.root = root;
    this.title = title;
    this.authors = authors;
    this.year = year;
  }


  @Override
  public String toString()
  {
    return getName();
  }


  @Override
  public int hashCode()
  {
    return (int) getId();
  }


  @Override
  public boolean equals(Object anotherObject)
  {
    if (this == anotherObject)
      return true;

    if (anotherObject == null || !getClass().equals(anotherObject.getClass()))
      return false;

    PublicationNode anotherNode = (PublicationNode) anotherObject;

    return getId() == anotherNode.getId();
  }


  @Override
  public int compareTo(PublicationNode anotherPublication)
  {
    return this.getId() - anotherPublication.getId();
  }


  public String getFirstAuthor()
  {
    try
    {
      Iterator<String> iter = authors.iterator();
      return iter.next();
    }
    // Name is not available if the set is empty
    catch (NoSuchElementException e)
    {
      return "N/A";
    }
    // Name is not available if the set wasn't initialized
    catch (NullPointerException e)
    {
      return "N/A";
    }
  }


  public String getFirstAuthorsLastname()
  {
    String authorName = getFirstAuthor();

    return authorName.substring(authorName.lastIndexOf(" ") + 1);
  }


  public void prepareForJsonOutput()
  {
    // Labeling attributes of publication
    setName(getFirstAuthorsLastname() + " (" + year + ")");
    firstAuthor = getFirstAuthorsLastname();

    // Metrics
    metrics = metricContainer.getMetrics();
  }
}
