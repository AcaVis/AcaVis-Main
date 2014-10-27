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


/**
 * @author Sebastian
 *
 */
public class CoAuthorshipNetwork
  extends UndirectedNetwork<AuthorNode, CoAuthorshipLink>
{
  /* The following fields are automatically transformed into a JSON-String when serialized by Jackson. */

  public Collection<GeneralNetworkCluster> clusters = new HashSet<GeneralNetworkCluster>();
  public Collection<AuthorNode> nodes = new LinkedList<AuthorNode>();
  public Collection<CoAuthorshipLink> links = new LinkedList<CoAuthorshipLink>();


  public CoAuthorshipNetwork()
  {}


  public void prepareForJsonOutput()
  {
    nodes = getNodes();
    links = getLinks();

    clusters = new HashSet<GeneralNetworkCluster>();
    for (AuthorNode node : nodes)
    {
      clusters.add(new GeneralNetworkCluster(node.getCluster(), "Cluster #" + (node.getCluster() + 1)));
    }
  }
}
