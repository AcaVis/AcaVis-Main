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
import java.util.HashSet;
import java.util.LinkedList;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import de.kbs.acavis.integration.model.PublicationData;
import de.kbs.acavis.integration.model.PublicationIdentifier;
import de.kbs.acavis.service.SerializationHelper;


@Entity
@NamedQueries({ @NamedQuery(name = "CollectionItem.byId", query = "SELECT i FROM CollectionItem i WHERE i.id = :id"),
    @NamedQuery(name = "CollectionItem.byIds", query = "SELECT i FROM CollectionItem i WHERE i.id IN (:ids)") })
public class CollectionItem
  extends AbstractTimestampEntity
{
  @Id
  @GeneratedValue(strategy = GenerationType.TABLE)
  private long id;

  // @Embedded
  // private CreationModificationTimestamps timestamps;

  @ManyToOne(cascade = CascadeType.ALL)
  private CollectionEntity collection;

  @Column(length = 1024)
  private PublicationIdentifier identifier = null;

  @Column
  private String title = "";

  @Column
  private short year = 0;

  @Column(length = 2048)
  private LinkedList<String> authors = new LinkedList<String>();

  @Column(length = 4096)
  private String abstractText = "";

  @Column
  private String source = "";

  @Column
  private int citationCount = 0;

  @Column
  private int referenceCount = 0;

  @Column(length = 2048)
  private HashSet<String> keywords = new HashSet<String>();

  @Column(length = 2048)
  private HashSet<String> disciplines = new HashSet<String>();

  @Column(length = 2048)
  private HashSet<String> subDisciplines = new HashSet<String>();

  @Column(length = 2048)
  private HashSet<String> urls = new HashSet<String>();


  // INFO: We don't need references and citations here

  /**
   * The constructor is used by hibernate and shouldn't be used on the programmer's side.
   */
  public CollectionItem()
  {}


  /**
   * The constructor plays the role of an adapter, taking data from {@link PublicationData}.
   * 
   * @param collection
   *          The storage-object for this result
   * @param publication
   *          The source-data contained in a {@link PublicationData}
   */
  public CollectionItem(CollectionEntity collection, PublicationData publication)
  {
    this.collection = collection;

    // We have to unbox all the information because we need to manage the fields with hibernate

    identifier = publication.getId();

    title = publication.getTitle();
    year = (short) publication.getYear();

    authors.addAll(publication.getAuthors());

    abstractText = publication.getAbstractText();
    source = publication.getSource();

    citationCount = publication.getCitationCount();
    referenceCount = publication.getReferenceCount();

    keywords.addAll(publication.getKeywords());
    disciplines.addAll(publication.getDisciplines());
    subDisciplines.addAll(publication.getSubDisciplines());
    urls.addAll(publication.getUrls());
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

    CollectionItem anotherCollectionItem = (CollectionItem) anotherObject;

    return id == anotherCollectionItem.getId();
  }


  public Long getId()
  {
    return id;
  }


  public CollectionEntity getCollection()
  {
    return collection;
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


  public LinkedList<String> getAuthors()
  {
    return authors;
  }


  public String getAbstractText()
  {
    return abstractText;
  }


  public String getSource()
  {
    return source;
  }


  public int getCitationCount()
  {
    return citationCount;
  }


  public int getReferenceCount()
  {
    return referenceCount;
  }


  public HashSet<String> getKeywords()
  {
    return keywords;
  }


  public HashSet<String> getDisciplines()
  {
    return disciplines;
  }


  public HashSet<String> getSubDisciplines()
  {
    return subDisciplines;
  }


  public HashSet<String> getUrls()
  {
    return urls;
  }
}
