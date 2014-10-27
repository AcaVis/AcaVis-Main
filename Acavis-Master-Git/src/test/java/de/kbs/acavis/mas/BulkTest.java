/*****************************************
 * This file is part of AcaVis, a tool for research of scientific
 * literature using the benefits of visualizations.
 * Copyright (C) 2014 - Sebastian Holzki
 * 
 * AcaVis is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License Version 2
 * as published by the Free Software Foundation;
 *****************************************/
package de.kbs.acavis.mas;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.core4j.Enumerable;
import org.odata4j.consumer.ODataConsumer;
import org.odata4j.core.OEntity;
import org.odata4j.core.OQueryRequest;

import de.kbs.acavis.integration.mas.odata.EntityHelper;
import de.kbs.acavis.integration.mas.odata.MASOdataConnector;


public class BulkTest
{
  private ODataConsumer consumer = MASOdataConnector.create();


  public static void main(String[] args)
  {
    BulkTest bulk = new BulkTest();

    // OQueryRequest<OEntity> r = bulk.consumer.getEntities("Paper_Ref").filter("DstID eq 1258503").top(10000);

    // Enumerable<OEntity> entities = BulkTest.bulkPaging(r, 500);
    // System.out.println(entities.count());

    // String[] f = new String[2];
    // f[0] = "DstID eq 1258503";
    // f[1] = "SrcID eq 53895";
    // Enumerable<OEntity> entities = BulkTest.bulkFilter(r, f);
    //
    // System.out.println(entities.count());
    // for (OEntity ent : entities)
    // {
    // if (EntityHelper.<Integer> extractProperty(ent, "SrcID") == 53895
    // && EntityHelper.<Integer> extractProperty(ent, "DstID") == 1258503)
    // System.out.println("dupl");
    // }

    // int[] ints = { 1, 2, 3, 4, 5, 6, 7, 8 };

    List<Integer> l = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8);

    String[] s = createBulkFilterExpressionsForIds(l, 3, "SrcID", "SrcID ne DstID and (", ")");

    for (String ss : s)
      System.out.println(ss);
  }


  public static Enumerable<OEntity> bulkFilter(OQueryRequest<OEntity> request, String[] bulkFilterExpressions)
  {
    Enumerable<OEntity> results = Enumerable.<OEntity> create();

    for (String bulkFilterExpression : bulkFilterExpressions)
      results = results.concat(request.filter(bulkFilterExpression).execute());

    return results;
  }


  public static Enumerable<OEntity> bulkPaging(OQueryRequest<OEntity> request, int maxPerPage)
  {
    maxPerPage = maxPerPage < 1 ? 10 : maxPerPage;

    Enumerable<OEntity> results = Enumerable.<OEntity> create();
    Enumerable<OEntity> result;

    int page = 0;

    request.top(maxPerPage);

    do
    {
      result = request.skip(page * maxPerPage).execute();

      results = results.concat(result);

      page++;
    }
    while (result.count() >= maxPerPage);

    return results;
  }


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


  public static String[] createBulkFilterExpressionsForIds(Collection<Integer> ids, int bulkSize, String property,
      String prefix, String postfix)
  {
    List<String> filterExpressions = new LinkedList<String>();
    List<List<Integer>> idPartitions = EntityHelper.idPartitioner(ids, bulkSize);

    for (List<Integer> idPartition : idPartitions)
      filterExpressions.add(prefix + EntityHelper.idInList(property, idPartition) + postfix);

    return filterExpressions.toArray(new String[0]);
  }


  public static String[] createBulkFilterExpressionsForIds(Collection<Integer> ids, int bulkSize, String property)
  {
    return createBulkFilterExpressionsForIds(ids, bulkSize, property, "", "");
  }
}
