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
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.util.EdgeType;


public class DirectedNetwork<N extends Node, L extends Link>
{
  private DirectedSparseGraph<N, L> network = new DirectedSparseGraph<N, L>();


  public DirectedNetwork()
  {}


  /**
   * Adds a directed link if a directed link between these nodes doesn't already exist. The nodes are created automatically if not present.
   * 
   * @param link
   * @param source
   * @param target
   */
  public void addLink(L link, N source, N target)
  {
    network.addEdge(link, source, target, EdgeType.DIRECTED);
  }


  /**
   * 
   * @param node
   */
  public void addNode(N node)
  {
    network.addVertex(node);
  }


  /**
   * Provides access to the internal network-representation, provided by Jung-framework. Only accessible for subclasses and classes in the
   * same package.
   * 
   * @return An instance of {@link DirectedSparseGraph}
   */
  protected DirectedSparseGraph<N, L> getNetwork()
  {
    return network;
  }


  /**
   * Provides access to the nodes contained in the network. Only accessible for subclasses and classes in the same package.
   * 
   * @return A Collection of nodes
   */
  public Collection<N> getNodes()
  {
    return network.getVertices();
  }


  /**
   * Provides access to the links contained in the network. Only accessible for subclasses and classes in the same package.
   * 
   * @return A collection of links
   */
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
