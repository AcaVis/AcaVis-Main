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
import java.util.Set;

import edu.uci.ics.jung.algorithms.cluster.EdgeBetweennessClusterer;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.graph.util.EdgeType;


public class UndirectedNetwork<N extends Node, L extends Link>
{
  private UndirectedSparseGraph<N, L> network = new UndirectedSparseGraph<N, L>();


  public UndirectedNetwork()
  {}


  public void addNode(N node)
  {
    network.addVertex(node);
  }


  public void addLink(L link, N source, N target)
  {
    network.addEdge(link, source, target, EdgeType.UNDIRECTED);
  }


  public UndirectedSparseGraph<N, L> getNetwork()
  {
    return network;
  }


  public Collection<N> getNodes()
  {
    return network.getVertices();
  }


  public Collection<L> getLinks()
  {
    return network.getEdges();
  }


  /**
   * Divides the network into clusters by edge-betweenness. Calculates the edge-betweenness-metric for each publication and assigns a
   * numerical cluster-number [0 .. n] to each node.<br />
   * <br />
   * The metric is stored using both the identifiers {@link MetricContainer#EDGE_BETWEENNESS} and {@link MetricContainer#CLUSTER}. All
   * previously calculated values for the cluster-metric are overwritten.
   * 
   * @param edgesToRemove
   *          The parameter influences how the clusters are determined. A higher parameter-value creates smaller and more cohesive clusters.
   *          A value of about 5 to 10 should be good choice.
   */
  public void calculateEdgeBetweennessClustering(int edgesToRemove)
  {
    // Calculate Edge-Betweenness using the Jung-Framework
    EdgeBetweennessClusterer<N, L> clusterer = new EdgeBetweennessClusterer<N, L>(edgesToRemove);

    try
    {
      Set<Set<N>> clusteredPublications = clusterer.transform(network);

      // Assign cluster numbers to each node
      int clusterNo = 0;
      for (Set<N> cluster : clusteredPublications)
      {
        for (N publication : cluster)
        {
          // publication.setMetric(MetricContainer.CLUSTER, clusterNo);
          publication.setCluster(clusterNo);
          publication.setMetric(MetricContainer.EDGE_BETWEENNESS, clusterNo);
        }

        clusterNo++;
      }
    }
    // Clustering doesn't make sense using the Betweenness-Clusterer (edgesToRemove must be positive and smaller than total number of edges
    // in the network)
    catch (IllegalArgumentException e)
    {
      Collection<N> nodes = network.getVertices();

      for (N publication : nodes)
      {
        publication.setCluster(0);
        publication.setMetric(MetricContainer.EDGE_BETWEENNESS, 0);
      }
    }

    // Clustering is not a metric!
  }
}
