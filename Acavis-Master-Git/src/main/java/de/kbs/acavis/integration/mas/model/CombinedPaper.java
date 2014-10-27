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

import org.hibernate.annotations.FilterJoinTable;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;

import de.kbs.acavis.integration.IdentifierException;
import de.kbs.acavis.integration.model.PublicationData;
import de.kbs.acavis.integration.model.PublicationIdentifier;
import de.kbs.acavis.integration.model.PublicationIdentifierFactory;


@NamedQueries({ @NamedQuery(name = "retrievePaperById", query = "SELECT p FROM CombinedPaper p WHERE p.id = :id"),
    @NamedQuery(name = "retrievePapersByIds", query = "select p FROM CombinedPaper p WHERE p.id IN (:ids)"),
    @NamedQuery(name = "retrieveAllPapers", query = "SELECT p FROM CombinedPaper p") })
@Entity
@Table(name = "Paper")
public class CombinedPaper
  extends PublicationData
{
  private static final long serialVersionUID = -5223727717308691870L;

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

  @Column(name = "Abstract")
  private String abstractText = "";

  @Transient
  private String source = "";

  @ManyToMany(fetch = FetchType.EAGER)
  @NotFound(action = NotFoundAction.IGNORE)
  @JoinTable(name = "Paper_Ref", joinColumns = { @JoinColumn(name = "SrcID") }, inverseJoinColumns = { @JoinColumn(name = "DstID") })
  private Set<CombinedPaper> references = new HashSet<CombinedPaper>();

  @ManyToMany(fetch = FetchType.EAGER)
  @NotFound(action = NotFoundAction.IGNORE)
  @JoinTable(name = "Paper_Ref", joinColumns = { @JoinColumn(name = "DstID") }, inverseJoinColumns = { @JoinColumn(name = "SrcID") })
  private Set<CombinedPaper> citations = new HashSet<CombinedPaper>();

  @ManyToMany(fetch = FetchType.EAGER)
  @NotFound(action = NotFoundAction.IGNORE)
  @JoinTable(name = "Paper_Keyword", joinColumns = { @JoinColumn(name = "CPaperID") }, inverseJoinColumns = { @JoinColumn(name = "KeywordID") })
  private Set<Keyword> keywords = new HashSet<Keyword>();

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(name = "Paper_Category", joinColumns = { @JoinColumn(name = "CPaperID") }, inverseJoinColumns = { @JoinColumn(name = "DomainID") })
  /* For major-disciplines the subdomain-id is zero. */
  @FilterJoinTable(name = "MajorDisciplinesFilter", condition = "SubDomainID = 0")
  private List<Domain> disciplines = new LinkedList<Domain>();

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(name = "Paper_Category", joinColumns = { @JoinColumn(name = "CPaperID") }, inverseJoinColumns = {
      @JoinColumn(name = "DomainID"), @JoinColumn(name = "SubDomainID") })
  private List<Category> subDisciplines = new LinkedList<Category>();

  @OneToMany(fetch = FetchType.EAGER, mappedBy = "paper")
  @NotFound(action = NotFoundAction.IGNORE)
  private Set<PaperUrl> urls = new HashSet<PaperUrl>();

  @Transient
  private PublicationIdentifier identifierContainer = null;


  /**
   * This constructor should <b>never</b> be used on the developers side!
   */
  public CombinedPaper()
  {
    super(null, "N/A");
  }


  @Override
  public boolean equals(Object anotherObject)
  {
    if (this == anotherObject)
      return true;

    if (anotherObject == null || !(anotherObject instanceof CombinedPaper))
      return false;

    PublicationData anotherPublication = (PublicationData) anotherObject;

    return getId() == anotherPublication.getId();
  }


  @Override
  public int hashCode()
  {
    return (int) id;
  }


  @Override
  public String toString()
  {
    return id + ": " + title;
  }


  @Override
  public PublicationIdentifier getId()
  {
    if (identifierContainer == null)
    {
      try
      {
        identifierContainer = PublicationIdentifierFactory.createMasPublicationIdentifier((int) id);
      }
      catch (IdentifierException e)
      {
        identifierContainer = new PublicationIdentifier();
      }
    }

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
  public String getAbstractText()
  {
    return abstractText;
  }


  @Override
  public String getSource()
  {
    return source;
  }


  @Override
  public Set<String> getKeywords()
  {
    HashSet<String> keywords = new HashSet<String>();

    for (Keyword keyword : this.keywords)
      keywords.add(keyword.getDisplayName());

    return keywords;
  }


  @Override
  public Set<String> getUrls()
  {
    HashSet<String> urls = new HashSet<String>();

    for (PaperUrl url : this.urls)
      urls.add(url.getUrl());

    return urls;
  }


  @Override
  public Set<PublicationData> getReferences()
  {
    return new HashSet<PublicationData>(references);
  }


  @Override
  public Set<PublicationData> getCitations()
  {
    return new HashSet<PublicationData>(citations);
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


  @Override
  public Set<String> getDisciplines()
  {
    Set<String> disciplines = new HashSet<String>(this.disciplines.size());

    for (Domain discipline : this.disciplines)
      disciplines.add(discipline.getName());

    return disciplines;
  }


  @Override
  public Set<String> getSubDisciplines()
  {
    Set<String> subDisciplines = new HashSet<String>(this.disciplines.size());

    for (Category subDiscipline : this.subDisciplines)
      subDisciplines.add(subDiscipline.getName());

    return subDisciplines;
  }
}
