/*****************************************
 * This file is part of AcaVis, a tool for research of scientific
 * literature using the benefits of visualizations.
 * Copyright (C) 2014 - Sebastian Holzki
 * 
 * AcaVis is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License Version 2
 * as published by the Free Software Foundation;
 *****************************************/
package de.kbs.acavis.integration.mas.model;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.*;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;

import de.kbs.acavis.integration.model.PublicationIdentifier;
import de.kbs.acavis.integration.model.PublicationLinkData;


@NamedQueries({ @NamedQuery(name = "SparsePaper.All", query = "SELECT p FROM SparsePaper p"),
    @NamedQuery(name = "SparsePaper.ByID", query = "SELECT p FROM SparsePaper p WHERE p.id = :id"),
    @NamedQuery(name = "SparsePaper.ByIDs", query = "SELECT p FROM SparsePaper p WHERE p.id IN (:ids)") })
@Entity
@Table(name = "Paper")
public class SparsePaper
  extends PublicationLinkData
{
  private static final long serialVersionUID = -6396528341031810837L;

  @Id
  @Column(name = "ID")
  private long id;

  @Column(name = "Title")
  private String title = "";

  @Column(name = "Year")
  private Integer year = 0;

  @OneToMany(fetch = FetchType.EAGER, mappedBy = "paper")
  @NotFound(action = NotFoundAction.IGNORE)
  @Sort(type = SortType.NATURAL)
  private SortedSet<PaperAuthor> authors = new TreeSet<PaperAuthor>();

  @ManyToMany(fetch = FetchType.EAGER)
  @NotFound(action = NotFoundAction.IGNORE)
  @JoinTable(name = "Paper_Ref", joinColumns = { @JoinColumn(name = "SrcID") }, inverseJoinColumns = { @JoinColumn(name = "DstID") })
  private Set<SparsePaper> references = new HashSet<SparsePaper>();

  @ManyToMany(fetch = FetchType.EAGER)
  @NotFound(action = NotFoundAction.IGNORE)
  @JoinTable(name = "Paper_Ref", joinColumns = { @JoinColumn(name = "DstID") }, inverseJoinColumns = { @JoinColumn(name = "SrcID") })
  private Set<SparsePaper> citations = new HashSet<SparsePaper>();

  @Transient
  private PublicationIdentifier identifierContainer = null;


  /**
   * This constructor should <b>never</b> be used on the developers side!
   */
  public SparsePaper()
  {}


  public SparsePaper(PublicationIdentifier identifier, String title)
  {
    this.identifierContainer = identifier;
    this.title = title;
  }


  @Override
  public PublicationIdentifier getId()
  {
    return identifierContainer;
  }


  @Override
  public String getTitle()
  {
    return title;
  }


  @Override
  public int getYear()
  {
    return year;
  }


  @Override
  public List<String> getAuthors()
  {
    LinkedList<String> authorNames = new LinkedList<String>();

    for (PaperAuthor author : authors)
      authorNames.add(author.getName());

    return authorNames;
  }


  @Override
  public Set<PublicationLinkData> getReferences()
  {
    return new HashSet<PublicationLinkData>(references);
  }


  @Override
  public Set<PublicationLinkData> getCitations()
  {
    return new HashSet<PublicationLinkData>(citations);
  }


  @Override
  public int getReferenceCount()
  {
    return references.size();
  }


  @Override
  public int getCitationCount()
  {
    return citations.size();
  }

}
