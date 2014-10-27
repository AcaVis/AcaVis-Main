/*****************************************
 * This file is part of AcaVis, a tool for research of scientific
 * literature using the benefits of visualizations.
 * Copyright (C) 2014 - Sebastian Holzki
 * 
 * AcaVis is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License Version 2
 * as published by the Free Software Foundation;
 *****************************************/
package de.kbs.acavis.integration.mas.controller;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.core4j.Enumerable;
import org.hibernate.internal.SessionImpl;
import org.odata4j.core.OEntity;

import de.kbs.acavis.integration.mas.model.Category;
import de.kbs.acavis.integration.mas.model.Domain;
import de.kbs.acavis.integration.mas.model.Keyword;
import de.kbs.acavis.integration.mas.model.Paper;
import de.kbs.acavis.integration.mas.model.PaperAuthor;
import de.kbs.acavis.integration.mas.model.PaperCategory;
import de.kbs.acavis.integration.mas.model.PaperKeyword;
import de.kbs.acavis.integration.mas.model.PaperRef;
import de.kbs.acavis.integration.mas.model.PaperUrl;


/**
 * This DAO provides methods to pump the plain entities and relations from the Microsoft Academic API into a temporary in-memory database.<br />
 * <br />
 * Microsoft Academic doesn't provide the "$expand"-option for OData-queries. Therefore every entity and every relation have to be retrieved
 * separately and glued together. As it's a hell to fiddle around with sets and lists solving such a problem, an in-memory database is used
 * instead.
 * 
 * @author Sebastian
 */
public class InMemoryDao
{
  private EntityManager entityManager = Persistence.createEntityManagerFactory("acavis-memdb").createEntityManager();
  private EntityTransaction transaction = entityManager.getTransaction();

  // Standalone-entities cache
  private Set<Paper> papers = new HashSet<Paper>();
  private Set<Keyword> keywords = new HashSet<Keyword>();
  private Set<Domain> domains = new HashSet<Domain>();
  private Set<Category> categories = new HashSet<Category>();

  // Pseudo standalone-entities cache
  private Set<PaperAuthor> paperAuthors = new HashSet<PaperAuthor>();
  private Set<PaperUrl> paperUrls = new HashSet<PaperUrl>();

  // Relations cache relying on foreign-key constraints
  private Set<PaperRef> paperRefs = new HashSet<PaperRef>();
  private Set<PaperKeyword> paperKeywords = new HashSet<PaperKeyword>();
  private Set<PaperCategory> paperCategories = new HashSet<PaperCategory>();


  public InMemoryDao()
  {
    // Disable foreign-key constraints to avoid violation-stuff and reduce complexity of the code
    Connection c = ((SessionImpl) entityManager.getDelegate()).connection();
    try
    {
      c.createStatement().execute("SET DATABASE REFERENTIAL INTEGRITY FALSE");
    }
    catch (SQLException e)
    {}
  }


  public EntityManager getEntityManager()
  {
    return entityManager;
  }


  /**
   * Persists a given paper if, and only if a paper with the associated primary key doesn't already exist.
   * 
   * @param paper
   *          An {@link OEntity} containing the paper-information to persist
   */
  public void persistPaper(OEntity paper)
  {
    papers.add(new Paper(paper));
  }


  /**
   * Persists a given set of papers if, and only if a paper with the associated primary key doesn't already exist.
   * 
   * @param papers
   *          A set of {@link OEntity} containing the paper-information to persist
   */
  public void persistPapers(Enumerable<OEntity> papers)
  {
    for (OEntity paper : papers)
      persistPaper(paper);
  }


  public void persistKeywords(Enumerable<OEntity> keywords)
  {
    for (OEntity keyword : keywords)
      this.keywords.add(new Keyword(keyword));
  }


  public void persistCategories(Enumerable<OEntity> categories)
  {
    for (OEntity category : categories)
      this.categories.add(new Category(category));
  }


  public void persistDomains(Enumerable<OEntity> domains)
  {
    for (OEntity domain : domains)
      this.domains.add(new Domain(domain));
  }


  public void persistPaperRefs(Enumerable<OEntity> paperRefs)
  {
    for (OEntity paperRef : paperRefs)
      this.paperRefs.add(new PaperRef(paperRef));
  }


  public void persistPaperAuthors(Enumerable<OEntity> paperAuthors)
  {
    for (OEntity paperAuthor : paperAuthors)
      this.paperAuthors.add(new PaperAuthor(paperAuthor));
  }


  public void persistPaperKeywords(Enumerable<OEntity> paperKeywords)
  {
    for (OEntity paperKeyword : paperKeywords)
      this.paperKeywords.add(new PaperKeyword(paperKeyword));
  }


  public void persistPaperCategories(Enumerable<OEntity> paperCategories)
  {
    for (OEntity paperCategory : paperCategories)
      this.paperCategories.add(new PaperCategory(paperCategory));
  }


  public void persistPaperUrls(Enumerable<OEntity> paperUrls)
  {
    for (OEntity paperUrl : paperUrls)
      this.paperUrls.add(new PaperUrl(paperUrl));
  }


  public void commitPublicationData()
  {
    transaction.begin();

    // Entities
    persistAll(papers);
    persistAll(keywords);
    persistAll(domains);
    persistAll(categories);

    // Pseudo entities
    persistAll(paperAuthors);
    persistAll(paperUrls);

    // Relations
    persistAll(paperRefs);
    persistAll(paperKeywords);
    persistAll(paperCategories);

    transaction.commit();
  }


  private <T> void persistAll(Set<T> entities)
  {
    for (T entity : entities)
      entityManager.persist(entity);
  }
}
