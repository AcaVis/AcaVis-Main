/*****************************************
 * This file is part of AcaVis, a tool for research of scientific
 * literature using the benefits of visualizations.
 * Copyright (C) 2014 - Sebastian Holzki
 * 
 * AcaVis is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License Version 2
 * as published by the Free Software Foundation;
 *****************************************/
package de.kbs.acavis.service.models.visualization;

import java.util.Collection;
import java.util.TreeMap;


/**
 * Provides a container for values of a yearly distribution, which for example can be used in a bar-chart.
 * 
 * @author Sebastian
 *
 */
public class YearlyDistribution
{
  private boolean ignoreZero = false;
  private TreeMap<Short, YearlyDistributionEntry> distribution = new TreeMap<Short, YearlyDistributionEntry>();


  public YearlyDistribution(boolean ignoreZero)
  {
    this.ignoreZero = ignoreZero;
  }


  /**
   * Increases the count of a specific year by 1.
   * 
   * @param year
   */
  public void increaseCount(short year)
  {
    if (ignoreZero && year == 0)
      return;

    YearlyDistributionEntry entry = findOrCreate(year);
    entry.count++;
  }


  /**
   * Decreases the count of a specific year by 1. The lowest possible count is 0 and will be limited by default.
   * 
   * @param year
   */
  public void decreaseCount(short year)
  {
    if (ignoreZero && year == 0)
      return;

    YearlyDistributionEntry entry = findOrCreate(year);
    entry.count = (entry.count > 0) ? entry.count - 1 : 0;
  }


  /**
   * Sets the count of a specific year to given count. The lowest possible count is 0 and will be limited by default.
   * 
   * @param year
   * @param count
   */
  public void setCount(short year, int count)
  {
    if (ignoreZero && year == 0)
      return;

    YearlyDistributionEntry entry = findOrCreate(year);
    entry.count = (count > 0) ? count : 0;
  }


  /**
   * Returns a collection containing the tuples of year and corresponding count.
   * 
   * @return A collection of tuples
   */
  public Collection<YearlyDistributionEntry> getDistribution()
  {
    return distribution.values();
  }


  /**
   * Finds and returns the entry for a given year. If none, an empty entry is created and returned afterwards.
   * 
   * @param year
   * @return The entry for the given year
   */
  private YearlyDistributionEntry findOrCreate(short year)
  {
    if (!distribution.containsKey(year))
      distribution.put(year, new YearlyDistributionEntry(year, 0));

    return distribution.get(year);
  }
}
