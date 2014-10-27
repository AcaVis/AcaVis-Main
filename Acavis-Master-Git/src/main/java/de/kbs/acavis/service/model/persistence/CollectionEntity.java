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

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.persistence.*;

import de.kbs.acavis.integration.model.PublicationIdentifier;


@Entity
@Table(name = "Stored_Collection")
@NamedQueries({ @NamedQuery(name = "Collection.all", query = "SELECT c FROM CollectionEntity c ORDER BY c.id ASC"),
    @NamedQuery(name = "Collection.latest", query = "SELECT c FROM CollectionEntity c ORDER BY c.id DESC"),
    @NamedQuery(name = "Collection.byId", query = "SELECT c FROM CollectionEntity c WHERE c.id = :id") })
public class CollectionEntity
  extends AbstractTimestampEntity
{
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  // @Embedded
  // private CreationModificationTimestamps timestamps;

  @Column
  private String name = "";

  @Column
  private int statPublications = 0;

  @Column
  private int statAuthors = 0;

  @Column
  private short statEarliestYear = 0;

  @Column
  private short statLatestYear = 0;

  @OneToMany(mappedBy = "collection", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  @OrderBy(value = "id DESC")
  private List<CollectionItem> publications = new LinkedList<CollectionItem>();

  // TODO Keyword cache of a collection
  @Transient
  private List<String> keywords = new LinkedList<String>();


  // To add personal recognition in a later version, the owner of this search must be stored along with every performed search.
  // Something like, where 'owner' is the owners primary key or some other identifying attribute:
  // public Long owner;

  /**
   * The constructor is used by hibernate and shouldn't be used on the programmer's side.
   */
  public CollectionEntity()
  {}


  public CollectionEntity(String name)
  {
    this.name = name;
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

    CollectionEntity anotherCollectionEntity = (CollectionEntity) anotherObject;

    return id == anotherCollectionEntity.getId();
  }


  public String getName()
  {
    return name;
  }


  public void setName(String name)
  {
    this.name = name;
  }


  public Long getId()
  {
    return id;
  }


  public int getStatPublications()
  {
    return statPublications;
  }


  public int getStatAuthors()
  {
    return statAuthors;
  }


  public int getStatEarliestYear()
  {
    return statEarliestYear;
  }


  public int getStatLatestYear()
  {
    return statLatestYear;
  }


  public List<CollectionItem> getPublications()
  {
    return publications;
  }


  public Set<PublicationIdentifier> getPublicationIdentifiers()
  {
    Set<PublicationIdentifier> identifiers = new HashSet<PublicationIdentifier>(publications.size());

    // Extract publication-identifiers into set
    for (CollectionItem publication : publications)
      identifiers.add(publication.getIdentifier());

    return identifiers;
  }


  public void addPublication(CollectionItem publication)
  {
    publications.add(publication);

    updateStats();
    updateKeywords();
  }


  public void addPublications(Collection<CollectionItem> publications)
  {
    this.publications.addAll(publications);

    updateStats();
    updateKeywords();
  }


  public void removePublication(CollectionItem publication)
  {
    publications.remove(publication);

    updateStats();
    updateKeywords();
  }


  public void removePublications(Collection<CollectionItem> publications)
  {
    this.publications.removeAll(publications);

    updateStats();
    updateKeywords();
  }


  /**
   * Re-calculates the four stat-parameters of the collection using the current state of the publication-list.
   */
  private void updateStats()
  {
    // Set the values to values that are far above any causal value
    statEarliestYear = 10000;
    statLatestYear = 0;

    // We use a set to determine number of unique authors
    Set<String> authors = new HashSet<String>();

    for (CollectionItem publication : publications)
    {
      // Update earliest year
      if (publication.getYear() < statEarliestYear)
        statEarliestYear = publication.getYear();

      // Update latest year
      if (publication.getYear() > statLatestYear)
        statLatestYear = publication.getYear();

      // Add all authors
      authors.addAll(publication.getAuthors());
    }

    // Update number of publications
    statPublications = publications.size();

    // Update number of authors
    statAuthors = authors.size();
  }


  /**
   * Creates a cached list of the entries used for the Tag-cloud.<br />
   * Disciplines, sub-disciplines and actual keywords of the publications are used at the moment.
   */
  private void updateKeywords()
  {
    // Maybe do text-analysis of abstract and title? (need stopword-list, stemming, etc *sigh*)

    keywords.clear();

    for (CollectionItem publication : publications)
    {
      keywords.addAll(publication.getDisciplines());
      keywords.addAll(publication.getSubDisciplines());
      keywords.addAll(publication.getKeywords());
    }
  }
}
