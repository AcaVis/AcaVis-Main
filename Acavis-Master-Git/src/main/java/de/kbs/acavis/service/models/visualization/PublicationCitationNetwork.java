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
import java.util.HashSet;
import java.util.LinkedList;

import edu.uci.ics.jung.algorithms.scoring.PageRank;


public class PublicationCitationNetwork
  extends DirectedNetwork<PublicationNode, CitationLink>
{
  /* The following fields are automatically transformed into a JSON-String when serialized by Jackson. */

  public Collection<GeneralNetworkCluster> clusters = new HashSet<GeneralNetworkCluster>();
  public Collection<PublicationNode> nodes = new LinkedList<PublicationNode>();
  public Collection<CitationLink> links = new LinkedList<CitationLink>();

  public Collection<String> availableNodeMetrics = new HashSet<String>();
  public Collection<String> availableLinkMetrics = new HashSet<String>();


  public PublicationCitationNetwork()
  {}


  /**
   * Calculates the PageRank-Score for every publication in the network.<br />
   * <br />
   * The metric is stored as floating point value using the identifier {@link MetricContainer#PAGERANK}.<br />
   * <br />
   * The parameter influences at what probability a random surfer will visit nodes in the network that don't have any incoming links.
   * 
   * @param randomTransitionProbability
   *          The probability for a random surfer as explained above
   */
  public void calculatePageRank(double randomTransitionProbability)
  {
    // Calculate PageRank-score using the algorithm implemented by Jung-Framework
    PageRank<PublicationNode, CitationLink> pageRank =
      new PageRank<PublicationNode, CitationLink>(getNetwork(), randomTransitionProbability);
    pageRank.evaluate();

    // Add the PageRanks as metric to the publications
    Collection<PublicationNode> publications = getNodes();
    for (PublicationNode publication : publications)
    {
      publication.setMetric(MetricContainer.PAGERANK, pageRank.getVertexScore(publication));
    }

    // Add the metric to internal cache
    availableNodeMetrics.add(MetricContainer.PAGERANK);
  }


  public void calculateEigenvectorCentrality()
  {
    // TODO

    // Add the metric to internal cache
    availableNodeMetrics.add(MetricContainer.EIGENVECTOR_CENTRALITY);
  }


  public void calculateClosenessCentrality()
  {
    // TODO

    // Add the metric to internal cache
    availableNodeMetrics.add(MetricContainer.CLOSENESS_CENTRALITY);
  }


  public void calculateBetweennessCentrality()
  {
    // TODO

    // Add the metric to internal cache
    availableNodeMetrics.add(MetricContainer.BETWEENNESS_CENTRALITY);
  }


  public void calculateHubsAndAuthorities()
  {
    // TODO

    // Add the metric to internal cache
    availableNodeMetrics.add(MetricContainer.HITS_AUTHORITY);
    availableNodeMetrics.add(MetricContainer.HITS_HUB);
  }


  public void prepareForJsonOutput()
  {
    nodes = getNodes();
    links = getLinks();

    clusters = new HashSet<GeneralNetworkCluster>();
    for (PublicationNode node : nodes)
    {
      clusters.add(new GeneralNetworkCluster(node.getCluster(), "Cluster #" + (node.getCluster() + 1)));
    }
  }

  // @Deprecated
  // public String getD3Json()
  // {
  // // TODO Improve creation of the JSON-string
  // String buffer = "";
  //
  // SortedSet<PublicationNode> sortedNodes = new TreeSet<PublicationNode>(getNodes());
  // DirectedSparseGraph<PublicationNode, CitationLink> network = getNetwork();
  // Collection<CitationLink> links = getLinks();
  //
  // for (PublicationNode publication : sortedNodes)
  // {
  // buffer += publication.toString() + System.lineSeparator();
  // }
  //
  // for (CitationLink link : links)
  // {
  // buffer += network.getSource(link).getId() + " -> " + network.getDest(link).getId() + System.lineSeparator();
  // }
  //
  // return buffer;
  // }
  //
  //
  // @Deprecated
  // public String getD3JsonLinksOnly()
  // {
  // // TODO Improve creation of the JSON-string
  // String buffer = "";
  //
  // DirectedSparseGraph<PublicationNode, CitationLink> network = getNetwork();
  // Collection<CitationLink> links = getLinks();
  //
  // for (CitationLink link : links)
  // {
  // buffer += network.getSource(link).getId() + " -> " + network.getDest(link).getId() + System.lineSeparator();
  // }
  //
  // return buffer;
  // }
}
