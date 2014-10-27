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

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.*;


@Entity
@Table(name = "Performed_Search")
@NamedQueries({ @NamedQuery(name = "Search.all", query = "SELECT s FROM SearchEntity s ORDER BY s.id ASC"),
    @NamedQuery(name = "Search.latest", query = "SELECT s FROM SearchEntity s ORDER BY s.id DESC"),
    @NamedQuery(name = "Search.byId", query = "SELECT s FROM SearchEntity s WHERE s.id = :id") })
public class SearchEntity
  extends AbstractTimestampEntity
{
  @Id
  @GeneratedValue(strategy = GenerationType.TABLE)
  private long id;

  // @Embedded
  // private CreationModificationTimestamps timestamps;

  @Column
  private String query = "";

  @Column
  private String providerClassName = "";

  @Column
  private int maxResults = 0;

  @Column
  private int statResults = 0;

  @Column
  private int statAuthors = 0;

  @Column
  private int statMinYear = Calendar.getInstance().get(Calendar.YEAR);

  @Column
  private int statMaxYear = 0;

  @OneToMany(mappedBy = "search", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  @OrderBy(value = "id ASC")
  private List<SearchResult> results = new LinkedList<SearchResult>();


  // To add personal recognition in a later version, the owner of this search must be stored along with every performed search.
  // Something like, where 'owner' is the owners primary key or some other identifying attribute:
  // public Long owner;

  public SearchEntity()
  {
    super();
  }


  public SearchEntity(String query, String providerClassName, int maxResults)
  {
    this.query = query;
    this.providerClassName = providerClassName;
    this.maxResults = maxResults;
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

    SearchEntity anotherSearchEntity = (SearchEntity) anotherObject;

    return id == anotherSearchEntity.getId();
  }


  public String getQuery()
  {
    return query;
  }


  public void setQuery(String query)
  {
    this.query = query;
  }


  public int getMaxResults()
  {
    return maxResults;
  }


  public void setMaxResults(int maxResults)
  {
    this.maxResults = maxResults;
  }


  public void setStatResults(int statResults)
  {
    this.statResults = statResults;
  }


  public int getStatResults()
  {
    return statResults;
  }


  public Long getId()
  {
    return id;
  }


  public List<SearchResult> getResults()
  {
    return results;
  }


  public void addResult(SearchResult result)
  {
    results.add(result);
    updateStats();
  }


  private void updateStats()
  {
    statResults = results.size();

    int authors = 0;
    int minYear = statMinYear;
    int maxYear = statMaxYear;

    for (SearchResult result : results)
    {
      authors += result.getAuthors().size();
      minYear = (result.getYear() < minYear) ? result.getYear() : minYear;
      maxYear = (result.getYear() > maxYear) ? result.getYear() : maxYear;
    }

    if (statResults == 0)
      minYear = maxYear = Calendar.getInstance().get(Calendar.YEAR);

    statAuthors = authors;
    statMinYear = minYear;
    statMaxYear = maxYear;
  }
}
