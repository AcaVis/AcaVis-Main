/*****************************************
 * This file is part of AcaVis, a tool for research of scientific
 * literature using the benefits of visualizations.
 * Copyright (C) 2014 - Sebastian Holzki
 * 
 * AcaVis is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License Version 2
 * as published by the Free Software Foundation;
 *****************************************/
package de.kbs.acavis.service.model.persistence;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;

import de.kbs.acavis.integration.model.PublicationIdentifier;
import de.kbs.acavis.integration.model.PublicationSearchData;
import de.kbs.acavis.service.SerializationHelper;


@Entity
@Table(name = "Publication_Searchresult")
public class SearchResult
  extends AbstractTimestampEntity
{
  @Id
  @GeneratedValue(strategy = GenerationType.TABLE)
  private long id;

  // @Embedded
  // private CreationModificationTimestamps timestamps;

  /**
   * Each search-result is associated with exactly one search by convention.<br />
   * This may leads to a number of duplicate entries. As it's suggested to associate previous searches with a lifespan or limit the number
   * of cached searches, this is a minor caveat.
   */
  @ManyToOne(cascade = CascadeType.ALL)
  private SearchEntity search;

  @Column(length = 1024)
  private PublicationIdentifier identifier = null;

  @Column(length = 512)
  private String title = "";

  @Column
  private short year = 0;

  @Column(length = 2048)
  private LinkedList<String> authors = new LinkedList<String>();

  @Column(length = 1024)
  private Number metric = 0;

  /**
   * A null-value indicates that there is no metric available
   */
  @Column(nullable = true)
  private String metricName = null;

  @Column(length = 2048)
  private String teaserText = "";

  @Column(length = 512)
  private String source = "";


  /**
   * The constructor is used by hibernate and shouldn't be used on the programmer's side.
   */
  public SearchResult()
  {
    super();
  }


  @Deprecated
  public SearchResult(SearchEntity search, PublicationIdentifier identifier, String title)
  {
    this.search = search;
    this.identifier = identifier;
    this.title = title;
  }


  /**
   * The constructor plays the role of an adapter, taking data from {@link PublicationSearchData}.
   * 
   * @param search
   *          The storage-object for this result
   * @param result
   *          The source-data contained in a {@link PublicationSearchData}
   */
  public SearchResult(SearchEntity search, PublicationSearchData result)
  {
    this.search = search;

    identifier = result.getId();

    title = result.getTitle();

    year = (short) result.getYear();
    authors.addAll(result.getAuthors());

    metric = result.getMetric();
    metricName = result.getMetricName();

    teaserText = result.getTeaserText();
    source = result.getSource();
  }


  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + (int) (id ^ (id >>> 32));
    return result;
  }


  @Override
  public boolean equals(Object anotherObject)
  {
    if (this == anotherObject)
      return true;

    if (anotherObject == null || getClass() != anotherObject.getClass())
      return false;

    SearchResult anotherSearchResult = (SearchResult) anotherObject;

    return id == anotherSearchResult.getId();
  }


  public Long getId()
  {
    return id;
  }


  public SearchEntity getSearch()
  {
    return search;
  }


  public PublicationIdentifier getIdentifier()
  {
    return identifier;
  }


  public String getSerializedIdentifier()
  {
    try
    {
      return SerializationHelper.serializePublicationIdentifierBase64(identifier);
    }
    catch (IOException e)
    {
      return "";
    }
  }


  public String getTitle()
  {
    return title;
  }


  public short getYear()
  {
    return year;
  }


  public List<String> getAuthors()
  {
    return authors;
  }


  public String getPlainAuthors()
  {
    return StringUtils.join(authors, ", ");
  }


  public Number getMetric()
  {
    return metric;
  }


  public String getMetricName()
  {
    return metricName;
  }


  public String getTeaserText()
  {
    return teaserText;
  }


  public String getSource()
  {
    return source;
  }

}
