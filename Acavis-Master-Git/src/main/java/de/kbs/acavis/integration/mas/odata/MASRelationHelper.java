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


public class MASRelationHelper
{
  /**
   * The number of ids that can be contained in a filter-expression of format &lt;<em>EntityID eq {id} or EntityID eq ...</em>&gt; without
   * reaching the HTTP-Headers maximum size for GET-Requests which is specified by the HTTP-Server.<br />
   * <br />
   * <b>Important info</b><br />
   * This is an <u>experimental</u> value which differs for the average length of the ids used and the surrounding expressions like binary
   * operators, field-names etc.<br />
   * It may be necessary to reduce this value if an HTTP-error of type <em>413 Entity too large</em> occurs!
   */
  private static final int REQUEST_MAXIDS = 400;

  /**
   * Specifies a select-expression (See OData-Specification) for field-names of the Paper_Ref table that should be returned in a request.<br />
   * 
   * This reduces the size of data returned.
   */
  private static final String PAPERREF_SELECTION = "SrcID,DstID,SeqID";

  private static final int PAPERREF_TOP = 50000;
  private static final int PAPERAUTHOR_TOP = 20000;
  private static final int PAPERKEYWORD_TOP = 40000;
  private static final int PAPERCATEGORY_TOP = 20000;

  private ODataConsumer consumer = null;


  public MASRelationHelper(ODataConsumer consumer)
  {
    this.consumer = consumer;
  }


  public MASRelationHelper(String odataBaseUrl)
  {
    consumer = MASOdataConnector.create(odataBaseUrl);
  }


  public MASRelationHelper()
  {
    consumer = MASOdataConnector.create();
  }


  /**
   * Retrieves the references from a given paper to other papers.<br />
   * The number of references returned is limited by the given parameter. If limit is 0, all references are returned.<br />
   * The order is the one given by MAS, the references are NOT ordered by (SeqID)!
   * 
   * @param sourceId
   *          The id of the referencing paper
   * @param limit
   *          The maximum number of entries to return
   * @return The relation-entities, containing information about the referenced papers
   */
  public Enumerable<OEntity> getPaperReferences(int sourceId, int limit)
  {
    if (limit < 1)
      return getPaperReferences(sourceId);

    return consumer.getEntities("Paper_Ref").filter(ignoreSelfRefs("SrcID eq " + sourceId)).select(PAPERREF_SELECTION)
        .top(limit).execute();
  }


  /**
   * Retrieves all the references from a given paper to other papers.<br />
   * The order is the one given by MAS, the references are NOT ordered by (SeqID)!
   * 
   * @param sourceId
   *          The id of the referencing paper
   * @return The relation-entities, containing information about the referenced papers
   */
  public Enumerable<OEntity> getPaperReferences(int sourceId)
  {
    // The old implementation without bulk-paging
    // return getPaperReferences(sourceId, PAPERREF_TOP);
    OQueryRequest<OEntity> request =
      consumer.getEntities("Paper_Ref").filter(ignoreSelfRefs("SrcID eq " + sourceId)).select(PAPERREF_SELECTION);

    return RetrievalHelper.bulkPaging(request, PAPERREF_TOP);
  }


  /**
   * Retrieves the references from a given set of papers to other papers.<br />
   * The number of references returned is limited by the given parameter. If limit is 0, all references are returned.<br />
   * The order is the one given by MAS, the references are NOT ordered by (SeqID)!<br />
   * <b>Caution!</b> This method may returns duplicates that have to be filtered manually!
   * 
   * @param sourceIds
   *          The ids of the referencing papers
   * @param limit
   *          The maximum number of entries to return per given paper
   * @return The relation-entities, containing information about the referenced papers
   */
  public Enumerable<OEntity> getPaperReferences(Set<Integer> sourceIds, int limit)
  {
    Enumerable<OEntity> results = Enumerable.<OEntity> create();

    // The old implementation without bulk-paging and bulk-filters
    // return consumer.getEntities("Paper_Ref").filter(ignoreSelfRefs(EntityHelper.idInList("SrcID", sourceIds)))
    // .select(PAPERREF_SELECTION).top(limit).execute();

    for (int sourceId : sourceIds)
      results = results.concat(getPaperReferences(sourceId, limit));

    return results;
  }


  /**
   * Retrieves the references from a given set of papers to other papers.<br />
   * The order is the one given by MAS, the references are NOT ordered by (SeqID)!<br />
   * <b>Caution!</b> This method may returns duplicates that have to be filtered manually!
   * 
   * @param sourceIds
   *          The ids of the referencing papers
   * @return The relation-entities, containing information about the referenced papers
   */
  public Enumerable<OEntity> getPaperReferences(Set<Integer> sourceIds)
  {
    // The old implementation without bulk-paging and bulk-filters
    // return getPaperReferences(sourceIds, PAPERREF_TOP);

    if (sourceIds.isEmpty())
      return Enumerable.<OEntity> create();

    OQueryRequest<OEntity> request = consumer.getEntities("Paper_Ref").select(PAPERREF_SELECTION);

    String[] bulkFilterExpressions =
      RetrievalHelper
          .createBulkFilterExpressionsForIds(sourceIds, REQUEST_MAXIDS, "SrcID", "SrcID ne DstID and (", ")");

    return RetrievalHelper.bulkFilterPaging(request, bulkFilterExpressions, PAPERREF_TOP);
  }


  /**
   * Retrieves the citations to a given paper by other papers.<br />
   * The number of references returned is limited by the given parameter. If limit is 0, all references are returned.<br />
   * The order is the one given by MAS, the references are NOT ordered by (SeqID)!
   * 
   * @param targetId
   *          The id of the cited paper
   * @param limit
   *          The maximum number of entries to return
   * @return The relation-entities, containing information about the citing papers
   */
  public Enumerable<OEntity> getPaperCitations(int targetId, int limit)
  {
    if (limit < 1)
      return getPaperCitations(targetId);

    return consumer.getEntities("Paper_Ref").filter(ignoreSelfRefs("DstID eq " + targetId)).select(PAPERREF_SELECTION)
        .top(limit).execute();
  }


  /**
   * Retrieves the citations to a given paper by other papers.<br />
   * The order is the one given by MAS, the references are NOT ordered by (SeqID)!
   * 
   * @param targetId
   *          The id of the cited paper
   * @return The relation-entities, containing information about the citing papers
   */
  public Enumerable<OEntity> getPaperCitations(int targetId)
  {
    // The old implementation without bulk-paging
    // return consumer.getEntities("Paper_Ref").filter(ignoreSelfRefs("DstID eq " + targetId)).select(PAPERREF_SELECTION)
    // .top(PAPERREF_TOP).execute();

    OQueryRequest<OEntity> request =
      consumer.getEntities("Paper_Ref").filter(ignoreSelfRefs("DstID eq " + targetId)).select(PAPERREF_SELECTION);

    return RetrievalHelper.bulkPaging(request, PAPERREF_TOP);
  }


  /**
   * Retrieves the citations to a given set of papers by other papers. Only one level of citations is returned.<br />
   * The order is the one given by MAS, the references are NOT ordered by (SeqID)!<br />
   * <b>Caution!</b> This method may returns duplicates that have to be filtered manually!
   * 
   * @param targetIds
   *          The ids of the cited papers
   * @param limit
   *          The maximum number of entries to return per given paper
   * @return The relation-entities, containing information about the citing papers
   */
  public Enumerable<OEntity> getPaperCitations(Set<Integer> targetIds, int limit)
  {
    // The old implementation without bulk-paging
    // return consumer.getEntities("Paper_Ref").filter(ignoreSelfRefs(EntityHelper.idInList("DstID", targetIds)))
    // .top(PAPERREF_TOP).select(PAPERREF_SELECTION).execute();

    Enumerable<OEntity> results = Enumerable.<OEntity> create();

    for (int sourceId : targetIds)
      results = results.concat(getPaperCitations(sourceId, limit));

    return results;
  }


  /**
   * Retrieves the citations to a given set of papers by other papers. Only one level of citations is returned.<br />
   * The order is the one given by MAS, the references are NOT ordered by (SeqID)!<br />
   * <b>Caution!</b> This method may returns duplicates that have to be filtered manually!
   * 
   * @param targetIds
   *          The ids of the cited papers
   * @return The relation-entities, containing information about the citing papers
   */
  public Enumerable<OEntity> getPaperCitations(Set<Integer> targetIds)
  {
    if (targetIds.isEmpty())
      return Enumerable.<OEntity> create();

    // The old implementation without bulk-paging and bulk-filters
    // return consumer.getEntities("Paper_Ref").filter(ignoreSelfRefs(EntityHelper.idInList("DstID", targetIds)))
    // .top(PAPERREF_TOP).select(PAPERREF_SELECTION).execute();

    OQueryRequest<OEntity> request = consumer.getEntities("Paper_Ref").select(PAPERREF_SELECTION);

    String[] bulkFilterExpressions =
      RetrievalHelper
          .createBulkFilterExpressionsForIds(targetIds, REQUEST_MAXIDS, "DstID", "SrcID ne DstID and (", ")");

    return RetrievalHelper.bulkFilterPaging(request, bulkFilterExpressions, PAPERREF_TOP);
  }


  /**
   * Retrieves the authors of a given paper.<br />
   * The request only returns the MAS's relation-entities from (Paper_Author). This entity contains the name of the author and foreign-key
   * to the (Author)-entity. Further information about the author must be retrieved using an author-entity-request.
   * 
   * @param paperId
   *          The id of the paper
   * @return The author-entities (Table: Paper_Author)
   */
  public Enumerable<OEntity> getAuthorsByPaper(int paperId)
  {
    // return consumer.getEntities("Paper_Author").filter("PaperID eq " + paperId).top(PAPERAUTHOR_TOP).execute();

    OQueryRequest<OEntity> request = consumer.getEntities("Paper_Author").filter("PaperID eq " + paperId);

    return RetrievalHelper.bulkPaging(request, PAPERAUTHOR_TOP);
  }


  /**
   * Retrieves the authors of given papers.<br />
   * The request only returns the MAS's relation-entities from (Paper_Author). This is actually a relation-table and contains the name of
   * the author as well as the foreign-key to the real (Author)-entity. Further information about the author must be retrieved using an
   * author-entity-request.
   * 
   * @param paperIds
   *          The id of the paper
   * @return The author-entities (Table: Paper_Author)
   */
  public Enumerable<OEntity> getAuthorsByPaper(Set<Integer> paperIds)
  {
    if (paperIds.isEmpty())
      return Enumerable.<OEntity> create();

    // return consumer.getEntities("Paper_Author").filter(EntityHelper.idInList("PaperID", paperIds)).top(PAPERAUTHOR_TOP)
    // .execute();

    OQueryRequest<OEntity> request = consumer.getEntities("Paper_Author");

    String[] bulkFilterExpressions =
      RetrievalHelper.createBulkFilterExpressionsForIds(paperIds, REQUEST_MAXIDS, "PaperID");

    return RetrievalHelper.bulkFilterPaging(request, bulkFilterExpressions, PAPERAUTHOR_TOP);

  }


  /**
   * Retrieves paper-keyword-relations for a single paper.
   * 
   * @param paperId
   *          Id of the paper
   * @return The paper-keyword-relations
   */
  public Enumerable<OEntity> getKeywordsByPaper(int paperId)
  {
    OQueryRequest<OEntity> request = consumer.getEntities("Paper_Keyword").filter("CPaperID eq " + paperId);

    return RetrievalHelper.bulkPaging(request, PAPERKEYWORD_TOP);
  }


  /**
   * Retrieves paper-keyword-relations for multiple papers.
   * 
   * @param paperIds
   *          Ids of the papers
   * @return The paper-keyword-relations
   */
  public Enumerable<OEntity> getKeywordsByPaper(Set<Integer> paperIds)
  {
    if (paperIds.isEmpty())
      return Enumerable.<OEntity> create();

    OQueryRequest<OEntity> request = consumer.getEntities("Paper_Keyword");

    // return consumer.getEntities("Paper_Keyword").filter(EntityHelper.idInList("CPaperID", paperIds))
    // .top(PAPERKEYWORD_TOP).execute();

    String[] bulkFilterExpressions =
      RetrievalHelper.createBulkFilterExpressionsForIds(paperIds, REQUEST_MAXIDS, "CPaperID");

    return RetrievalHelper.bulkFilterPaging(request, bulkFilterExpressions, PAPERKEYWORD_TOP);
  }


  /**
   * Retrieves paper-category-relations (disciplines and sub-disciplines) for a single paper.
   * 
   * @param paperId
   *          Id of the paper
   * @return The paper-category-relations
   */
  public Enumerable<OEntity> getPaperCategoriesByPaper(int paperId)
  {
    return consumer.getEntities("Paper_Category").filter("CPaperID eq " + paperId).top(PAPERCATEGORY_TOP).execute();
  }


  /**
   * Retrieves paper-category-relations (disciplines and sub-disciplines) for multiple papers.
   * 
   * @param paperIds
   *          Ids of the papers
   * @return The paper-category-relations
   */
  public Enumerable<OEntity> getPaperCategoriesByPapers(Set<Integer> paperIds)
  {
    if (paperIds.isEmpty())
      return Enumerable.<OEntity> create();

    return consumer.getEntities("Paper_Category").filter(EntityHelper.idInList("CPaperID", paperIds))
        .top(PAPERCATEGORY_TOP).execute();
  }


  /**
   * Helper-method that surrounds a given filter-expression like follows: <em>SrcID ne DstID and ({filter-expression})</em>. This avoids to
   * return self-references of papers which make no sense here.
   * 
   * @param filterExpression
   *          The original filter-expression
   * @return The modified filter-expression
   */
  private static String ignoreSelfRefs(String filterExpression)
  {
    return "(" + filterExpression + ") and SrcID ne DstID";
  }
}
