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

import java.util.HashMap;


/**
 * @author Sebastian
 *
 */
public class MetricContainer
{
  // Node-divisions (clustering)
  public static final String CLUSTER = "cluster";
  public static final String EDGE_BETWEENNESS = "Edge Betweenness";

  // Node-scores
  public static final String NODEWEIGHT = "weight";
  public static final String PAGERANK = "PageRank";
  public static final String EIGENVECTOR_CENTRALITY = "Eigenvector-Centrality";
  public static final String CLOSENESS_CENTRALITY = "Closeness-Centrality";
  public static final String BETWEENNESS_CENTRALITY = "Betweenness-Centrality";
  public static final String HITS_HUB = "Hub-Metric (HITS)";
  public static final String HITS_AUTHORITY = "Authority-Metric (HITS)";

  // Link-scores
  public static final String LINKWEIGHT = "weight";

  /**
   * Cache for the entities' metrics.
   */
  private HashMap<String, Double> metrics = new HashMap<String, Double>();


  /**
   * Returns the value of a specified metric.
   * 
   * @param metricName
   *          Name of the metric to use
   * @return The value of the specified metric or -1 if the metric is not available
   */
  public double getMetric(String metricName)
  {
    Double metric = metrics.get(metricName);
    if (metric == null)
      return -1;

    return metric;
  }


  /**
   * Sets the value of a specified metric.
   * 
   * @param metricName
   * @param metricValue
   */
  public void setMetric(String metricName, double metricValue)
  {
    metrics.put(metricName, metricValue);
  }


  public HashMap<String, Double> getMetrics()
  {
    return metrics;
  }
}
