/*****************************************
 * This file is part of AcaVis, a tool for research of scientific
 * literature using the benefits of visualizations.
 * Copyright (C) 2014 - Sebastian Holzki
 * 
 * AcaVis is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License Version 2
 * as published by the Free Software Foundation;
 *****************************************/
package de.kbs.acavis.integration.mas.odata;

import java.util.Set;

import org.core4j.Enumerable;
import org.odata4j.consumer.ODataConsumer;
import org.odata4j.core.OEntity;
import org.odata4j.core.OQueryRequest;


public class MASEntityHelper
{
  private static final int REQUEST_MAXIDS = 400;

  private static final String PAPER_SELECTION = "ID,Title,Year,DOI,ISBN,Abstract";
  private static final String AUTHOR_SELECTION = "ID,Name,Affiliation";
  private static final String KEYWORD_SELECTION = "ID,DisplayName";

  private static final int PAPER_TOP = 10000;
  private static final int AUTHOR_TOP = 1000;
  private static final int KEYWORD_TOP = 20000;
  private static final int URL_TOP = 1000;

  private ODataConsumer consumer = null;


  public MASEntityHelper(ODataConsumer consumer)
  {
    this.consumer = consumer;
  }


  public MASEntityHelper(String odataBaseUrl)
  {
    consumer = MASOdataConnector.create(odataBaseUrl);
  }


  public MASEntityHelper()
  {
    consumer = MASOdataConnector.create();
  }


  /**
   * Retrieves papers by a given filter-expression. If filter-expression is empty, the top 10 records are returned.
   * 
   * @param consumer
   *          An instance of the {@link ODataComsumer}
   * @param filterExpression
   *          Optional: An expression to use as filter-attribute
   * @return A set of papers in {@link OEntity}-format.
   * @see #getPapersById(HashSet)
   * @see #getPaper(String)
   */
  // public Enumerable<OEntity> getPapers(String filterExpression)
  // {
  // if (filterExpression == null || filterExpression.isEmpty())
  // return consumer.getEntities("Paper").top(10).execute();
  //
  // return consumer.getEntities("Paper").filter(filterExpression).select(PAPER_SELECTION).top(PAPER_TOP).execute();
  // }

  // public Enumerable<OEntity> getPapersBulk(Set<Integer> ids)
  // {
  // // if (ids.isEmpty())
  // // return new LinkedList<Enumerable<OEntity>>();
  // //
  // // List<Enumerable<OEntity>> results = new LinkedList<Enumerable<OEntity>>();
  // //
  // // List<List<Integer>> idPartitions = EntityHelper.idPartitioner(ids, REQUEST_MAXIDS);
  // //
  // // System.out.println("Paper Partitions: " + idPartitions.size());
  // //
  // // for (List<Integer> sids : idPartitions)
  // // results.add(getPapers(EntityHelper.idInList("ID", sids)));
  // //
  // // return results;
  //
  // if (ids.isEmpty())
  // return Enumerable.<OEntity> create();
  //
  // OQueryRequest<OEntity> request = consumer.getEntities("Paper").select(PAPER_SELECTION);
  //
  // String[] bulkFilterExpressions = RetrievalHelper.createBulkFilterExpressionsForIds(ids, REQUEST_MAXIDS, "ID");
  //
  // return RetrievalHelper.bulkFilterPaging(request, bulkFilterExpressions, PAPER_TOP);
  // }

  /**
   * Retrieves a paper by given filter-expression. Even if there are several papers, only the first one is returned.
   * 
   * @param filterExpression
   *          An expression to get the papers for.
   * @return An {@link OEntity} with the paper-information or null.
   * @see #getPaper(int)
   * @see #getPapers(String)
   */
  // public OEntity getPaper(String filterExpression)
  // {
  // if (filterExpression == null || filterExpression.isEmpty())
  // return consumer.getEntities("Paper").top(1).execute().first();
  //
  // return consumer.getEntities("Paper").filter(filterExpression).select(PAPER_SELECTION).top(1).execute().first();
  // }

  /**
   * Retrieves a paper by given ID.
   * 
   * @param paperId
   *          A paper ID
   * @return An {@link OEntity} with the paper-information or <b>null</b> if there was no paper with the given id
   * @see #getPapers(Set)
   */
  public OEntity getPaper(int paperId)
  {
    return consumer.getEntities("Paper").filter("ID eq " + paperId).select(PAPER_SELECTION).top(1).execute()
        .firstOrNull();
  }


  /**
   * Retrieves multiple papers by their IDs.
   * 
   * @param paperIds
   *          A set of IDs to use
   * @return A set of papers in {@link OEntity}-format.
   * @see #getPaper(int)
   */
  public Enumerable<OEntity> getPapers(Set<Integer> paperIds)
  {
    if (paperIds.isEmpty())
      return Enumerable.create();

    OQueryRequest<OEntity> request = consumer.getEntities("Paper").select(PAPER_SELECTION);

    String[] bulkFilterExpressions = RetrievalHelper.createBulkFilterExpressionsForIds(paperIds, REQUEST_MAXIDS, "ID");

    return RetrievalHelper.bulkFilterPaging(request, bulkFilterExpressions, PAPER_TOP);
  }


  /**
   * Retrieves a single author by given author-ID.
   * 
   * @param authorId
   *          An author-ID
   * @return An Author-Entity or <b>null</b> if there was no author with the given id
   */
  public OEntity getAuthor(int authorId)
  {
    return consumer.getEntities("Author").filter("ID eq " + authorId).select(AUTHOR_SELECTION).top(1).execute()
        .firstOrNull();
  }


  /**
   * Retrieves authors by given set of author-IDs.
   * 
   * @param authorIds
   *          A Set of author-IDs
   * @return A Set of Author-Entities
   */
  public Enumerable<OEntity> getAuthors(Set<Integer> authorIds)
  {
    if (authorIds.isEmpty())
      return Enumerable.create();

    OQueryRequest<OEntity> request = consumer.getEntities("Author").select(AUTHOR_SELECTION);

    // return consumer.getEntities("Author").filter(EntityHelper.idInList("ID", authorIds)).select(AUTHOR_SELECTION)
    // .execute();

    String[] bulkFilterExpressions = RetrievalHelper.createBulkFilterExpressionsForIds(authorIds, REQUEST_MAXIDS, "ID");

    return RetrievalHelper.bulkFilterPaging(request, bulkFilterExpressions, AUTHOR_TOP);
  }


  /**
   * Retrieves a keyword by id.
   * 
   * @param keywordId
   *          The id of the keyword
   * @return A set of keyword-entities
   */
  public Enumerable<OEntity> getKeywords(int keywordId)
  {
    // return consumer.getEntities("Keyword").filter("ID eq " + id).select(KEYWORD_SELECTION).execute();

    OQueryRequest<OEntity> request =
      consumer.getEntities("Keyword").filter("ID eq " + keywordId).select(KEYWORD_SELECTION);

    return RetrievalHelper.bulkPaging(request, KEYWORD_TOP);
  }


  /**
   * Retrieves keywords by their ids.
   * 
   * @param keywordIds
   *          The ids of the keywords
   * @return A set of keyword-entities
   */
  public Enumerable<OEntity> getKeywords(Set<Integer> keywordIds)
  {
    if (keywordIds.isEmpty())
      return Enumerable.create();

    // return consumer.getEntities("Keyword").filter(EntityHelper.idInList("ID", keywordIds)).select(KEYWORD_SELECTION).execute();

    OQueryRequest<OEntity> request = consumer.getEntities("Keyword").select(KEYWORD_SELECTION);

    String[] bulkFilterExpressions =
      RetrievalHelper.createBulkFilterExpressionsForIds(keywordIds, REQUEST_MAXIDS, "ID");

    return RetrievalHelper.bulkFilterPaging(request, bulkFilterExpressions, KEYWORD_TOP);
  }


  /**
   * Retrieves <b>all</b> available categories.
   * 
   * @return The category-entities
   */
  public Enumerable<OEntity> getCategories()
  {
    // We use a static upper limit here instead of a paging algorithm because this method is called very frequent
    return consumer.getEntities("Category").top(5000).execute();
  }


  /**
   * Retrieves categories (sub-disciplines) by their ids.
   * 
   * @param categoryIds
   *          The ids of the categories
   * @return The category-entities
   */
  public Enumerable<OEntity> getCategories(int domainId, Set<Integer> categoryIds)
  {
    if (categoryIds.isEmpty())
      return Enumerable.create();

    // We use a static upper limit here instead of a paging algorithm because this method is called very frequent
    // We don't need a selection because we want all fields
    return consumer.getEntities("Category")
        .filter("DomainID eq " + domainId + " and (" + EntityHelper.idInList("SubDomainID", categoryIds) + ")")
        .top(5000).execute();
  }


  /**
   * Retrieves categories (sub-disciplines) by their parent-domain.
   * 
   * @param domainId
   *          The id of the parent-domain
   * @return The category-entities
   */
  public Enumerable<OEntity> getCategoriesByDomain(int domainId)
  {
    // We use a static upper limit here instead of a paging algorithm because this method is called very frequent
    // We don't need a selection because we want all fields
    return consumer.getEntities("Category").filter("DomainID eq " + domainId).top(5000).execute();
  }


  /**
   * Retrieves <b>all</b> available domains.
   * 
   * @return The domain-entities
   */
  public Enumerable<OEntity> getDomains()
  {
    // We use a static upper limit here instead of a paging algorithm because this method is called very frequent
    return consumer.getEntities("Domain").top(5000).execute();
  }


  /**
   * Retrieves domains (major disciplines) by their ids.
   * 
   * @param domainIds
   *          The ids of the domains
   * @return The domain-entities
   */
  public Enumerable<OEntity> getDomains(Set<Integer> domainIds)
  {
    if (domainIds.isEmpty())
      return Enumerable.create();

    // We don't need a selection because we want all fields
    return consumer.getEntities("Domain").filter(EntityHelper.idInList("ID", domainIds)).top(5000).execute();
  }


  /**
   * Retrieves urls for a given paper.
   * 
   * @param id
   *          The id of the paper
   * @return A set of url-entities
   */
  public Enumerable<OEntity> getUrlsByPaper(int id)
  {
    // return consumer.getEntities("Paper_Url").filter("PaperID eq " + id).execute();

    OQueryRequest<OEntity> request = consumer.getEntities("Paper_Url").filter("PaperID eq " + id);

    return RetrievalHelper.bulkPaging(request, URL_TOP);
  }


  /**
   * Retrieves urls for given papers.
   * 
   * @param ids
   *          The ids of the papers
   * @return A set of url-entities
   */
  public Enumerable<OEntity> getUrlsByPaper(Set<Integer> ids)
  {
    if (ids.isEmpty())
      return Enumerable.create();

    // return consumer.getEntities("Paper_Url").filter(EntityHelper.idInList("PaperID", ids)).execute();

    OQueryRequest<OEntity> request = consumer.getEntities("Paper_Url");

    String[] bulkFilterExpressions = RetrievalHelper.createBulkFilterExpressionsForIds(ids, REQUEST_MAXIDS, "PaperID");

    return RetrievalHelper.bulkFilterPaging(request, bulkFilterExpressions, URL_TOP);
  }
}
