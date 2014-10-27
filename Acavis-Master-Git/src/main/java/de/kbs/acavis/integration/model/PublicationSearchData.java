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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import de.kbs.acavis.integration.IdentifierException;


public class PublicationSearchData
  implements Comparable<PublicationSearchData>, Serializable
{
  private static final long serialVersionUID = -590838868825683644L;

  private PublicationIdentifier identifier = null;

  private String title = "";

  private List<String> authors = new ArrayList<String>();

  private Number metric = 0;
  private String metricName = null;

  private String teaserText = "";

  private String source = "";

  private int year = 0;


  public PublicationSearchData(PublicationIdentifier identifier, String title, int metric, String metricName,
      String teaserText, String source, int year)
  {
    this.identifier = identifier;

    this.title = title;
    this.metric = metric;
    this.metricName = metricName;

    this.teaserText = teaserText;

    this.source = source;
    this.year = year;
  }


  public PublicationSearchData(PublicationIdentifier identifier, String title)
  {
    this.identifier = identifier;
    this.title = title;
  }


  public PublicationSearchData(PublicationIdentifier identifier)
  {
    this.identifier = identifier;
  }


  public String toString()
  {
    try
    {
      return "(" + identifier.getMasIdentifier() + ") " + getTitle();
    }
    catch (NullPointerException | IdentifierException e)
    {
      return "(id-error) " + getTitle();
    }
  }


  /**
   * Uses the metric-field to compare two search-data-results.
   */
  @Override
  public int compareTo(PublicationSearchData anotherObject)
  {
    Double comp = getMetric().doubleValue() - anotherObject.getMetric().doubleValue();

    if (comp < 0)
      return -1;
    if (comp > 0)
      return 1;

    return 0;
  }


  public PublicationIdentifier getId()
  {
    return identifier;
  }


  public String getTitle()
  {
    return title;
  }


  public void setTitle(String title)
  {
    this.title = title;
  }


  public List<String> getAuthors()
  {
    return authors;
  }


  public Number getMetric()
  {
    return metric;
  }


  public void setMetric(int metric)
  {
    this.metric = metric;
  }


  /**
   * The name of the associated metric.
   * 
   * @return The name of the metric or null, if no special metric has been assigned
   */
  public String getMetricName()
  {
    return metricName;
  }


  /**
   * Assigns a metric-name with the data.
   * 
   * @param metricName
   *          The name of the metric or null, if there is no associated metric
   */
  public void setMetricName(String metricName)
  {
    this.metricName = metricName;
  }


  public String getTeaserText()
  {
    return teaserText;
  }


  public void setTeaserText(String teaserText)
  {
    this.teaserText = teaserText;
  }


  public int getYear()
  {
    return year;
  }


  public void setYear(int year)
  {
    this.year = year;
  }


  public String getSource()
  {
    return source;
  }


  public void setSource(String source)
  {
    this.source = source;
  }


  public void addAuthor(String author)
  {
    authors.add(author);
  }


  public void addAuthors(Set<String> authors)
  {
    this.authors.addAll(authors);
  }
}
