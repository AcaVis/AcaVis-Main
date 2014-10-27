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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.core4j.Enumerable;
import org.odata4j.core.OEntity;


public class EntityHelper
{
  public static String idInList(String property, Collection<Integer> ids)
  {
    // Avoid errors caused by empty filter-statements using a valid but false expression
    if (ids.isEmpty())
      return "1 eq 0";

    return property + " eq " + StringUtils.join(ids, " or " + property + " eq ");
  }


  public static String idLongInList(String property, Collection<Long> ids)
  {
    // Avoid errors caused by empty filter-statements using a valid but false expression
    if (ids.isEmpty())
      return "1 eq 0";

    return property + " eq " + StringUtils.join(ids, " or " + property + " eq ");
  }


  @SuppressWarnings("unchecked")
  public static <T> Set<T> extractProperty(Enumerable<OEntity> entities, String propertyName)
  {
    HashSet<T> properties = new HashSet<T>();

    // We can try to cast the property because getProperty would throw such an exception anyways
    for (OEntity entity : entities)
      properties.add((T) (entity.getProperty(propertyName).getValue()));

    return properties;
  }


  @SuppressWarnings("unchecked")
  public static <T> T extractProperty(OEntity entity, String propertyName)
  {
    // We can try to cast the property because getProperty would throw such an exception anyways
    return (T) (entity.getProperty(propertyName)).getValue();
  }


  public static List<List<Integer>> idPartitioner(Collection<Integer> ids, int partitionSize)
  {
    return ListUtils.partition(new LinkedList<Integer>(ids), partitionSize);
  }

}
