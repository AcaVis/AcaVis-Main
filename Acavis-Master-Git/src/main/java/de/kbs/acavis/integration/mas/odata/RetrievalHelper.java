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

import java.util.Collection;
import java.util.List;

import org.core4j.Enumerable;
import org.odata4j.core.OEntity;
import org.odata4j.core.OQueryRequest;


/**
 * The class contains methods to 'extend' a basic {@link OQueryRequest} with further functionalities or to emulate functionalities not
 * supported by OData4J or by MAS DB.
 * 
 * @author Sebastian
 *
 */
public class RetrievalHelper
{
  /**
   * Performs requests using the settings of a given request with a bulk of filter-expressions and merges the retrieved entities. Handling
   * this behaviour manually is necessary, because OData4J doesn't support native OData-bulk-requests and MAS limits the HTTP-Header size.<br />
   * <br />
   * <b>Caution!</b> This method does NOT handle duplicated entities! These may appear because more than one request is performed. You have
   * to take care of this by yourself.<br />
   * <br />
   * Former filter-expressions that were set to the request-object are <u>overwritten</u> by the nature of this method.
   * 
   * @param request
   *          A request-object
   * @param bulkFilterExpressions
   *          An array of valid filter-expressions, that should additionally be short enough to pass MAS without an HTTP-error (413 Entity
   *          too large)
   * @return Entities that were returned for the given filter-expressions
   */
  public static Enumerable<OEntity> bulkFilter(OQueryRequest<OEntity> request, String[] bulkFilterExpressions)
  {
    Enumerable<OEntity> results = Enumerable.<OEntity> create();

    for (String bulkFilterExpression : bulkFilterExpressions)
      results = results.concat(request.filter(bulkFilterExpression).execute());

    return results;
  }


  /**
   * Retrieves results of a query in multiple steps. Per step only a given amount of entities are returned. The default 'page-size' of MAS
   * is just 100 entries which is way too less for retrieving result-sets with several thousand entries.<br />
   * If the given page-size is less than one, a default-value of 1000 is assigned, to avoid infinite loops.<br />
   * <br />
   * Former values of <em>skip()</em> and <em>top()</em> are <u>overwritten</u> by nature of this method.<br />
   * <b>A filter-expression must be set to the request <u>before</u> calling this method if you want one!</b>
   * 
   * @param request
   *          A request-object
   * @param maxPerPage
   *          The maximum number of entries per page aka page-size
   * @return All entities, merged into a single result-set
   */
  public static Enumerable<OEntity> bulkPaging(OQueryRequest<OEntity> request, int maxPerPage)
  {
    // Avoid infinite looping
    maxPerPage = maxPerPage < 1 ? 1000 : maxPerPage;

    Enumerable<OEntity> results = Enumerable.<OEntity> create();
    Enumerable<OEntity> result;

    int page = 0;

    request.top(maxPerPage);

    do
    {
      // offset starting with 0 * maxPerPage
      result = request.skip(page * maxPerPage).execute();

      results = results.concat(result);

      page++;
    }
    // If the number of returned results equals or is greater than the given page-size, there are (possibly) more results on the next page
    // Values greater than the page size should never occur, this is just a fallback
    while (result.count() >= maxPerPage);

    return results;
  }


  /**
   * This method combines the techniques presented in {@link #bulkFilter(OQueryRequest, String[])} and
   * {@link #bulkPaging(OQueryRequest, int)}. Ergo this one should be used for large input-sets that additionally return large result-sets.<br />
   * <br />
   * Former values of <em>skip()</em> and <em>top()</em> are <u>overwritten</u> as well as filter-expressions by nature of this method.
   * 
   * @param request
   *          A request-object
   * @param bulkFilterExpressions
   *          An array of valid filter-expressions, that should additionally be short enough to pass MAS without an HTTP-error (413 Entity
   *          too large)
   * @param maxPerPage
   *          The maximum number of entries per page aka page-size
   * @return All entities, merged into a single result-set
   */
  public static Enumerable<OEntity> bulkFilterPaging(OQueryRequest<OEntity> request, String[] bulkFilterExpressions,
      int maxPerPage)
  {
    Enumerable<OEntity> results = Enumerable.<OEntity> create();

    for (String bulkFilterExpression : bulkFilterExpressions)
    {
      request.filter(bulkFilterExpression);

      results = results.concat(bulkPaging(request, maxPerPage));
    }

    return results;
  }


  /**
   * Transforms an arbitrary collection of ids into an array of single filter-expressions that use at most a given number of ids.
   * (Partitioning) The resulting String-array can be used as input for a bulk-filter of this class.<br />
   * <br />
   * You have to take care of the filter-expressions to not exceed a char-limit on your own!
   * 
   * @param ids
   *          A collection of ids
   * @param bulkSize
   *          The maximum number of ids per filter-expression
   * @param property
   *          The name of the property that corresponds to the ids
   * @param prefix
   *          A prefix for every filter-expression (Can be used for static conditions)
   * @param suffix
   *          A suffix used for every filter-expression (Can be used for static conditions)
   * @return An array of filter-expressions
   */
  public static String[] createBulkFilterExpressionsForIds(Collection<Integer> ids, int bulkSize, String property,
      String prefix, String suffix)
  {
    List<List<Integer>> idPartitions = EntityHelper.idPartitioner(ids, bulkSize);

    String[] filterExpressions = new String[idPartitions.size()];

    int index = 0;
    for (List<Integer> idPartition : idPartitions)
      filterExpressions[index++] = prefix + EntityHelper.idInList(property, idPartition) + suffix;

    return filterExpressions;
  }


/**
   * Works just like {@link #createBulkFilterExpressionsForIds(Collection, int, String, String, String) but doesn't use a prefix and suffix.
   * 
   * @param ids
   *          A collection of ids
   * @param bulkSize
   *          The maximum number of ids per filter-expression
   * @param property
   *          The name of the property that corresponds to the ids
   * @return An array of filter-expressions
   */
  public static String[] createBulkFilterExpressionsForIds(Collection<Integer> ids, int bulkSize, String property)
  {
    return createBulkFilterExpressionsForIds(ids, bulkSize, property, "", "");
  }
}
