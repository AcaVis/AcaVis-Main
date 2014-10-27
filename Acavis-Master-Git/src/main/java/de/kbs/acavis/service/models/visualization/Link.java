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



/**
 * @author Sebastian
 *
 */
public class Link
  extends MetricContainer
{
  private int id = -1;
  private boolean directed = true;


  /**
   * Creates a link between two nodes using the specified parameters.
   * 
   * @param id
   *          The (unique) id of this link
   * @param source
   *          The source-node
   * @param target
   *          The target-node
   * @param directed
   *          Sets the state of this link to directed (true) or undirected (false)
   * @param weight
   *          The value of the weight-metric of this link
   */
  public Link(int id, boolean directed, double weight)
  {
    this.id = id;

    setDirected(directed);
    setWeight(weight);
  }


  /**
   * Creates a link between two nodes using the specified parameters.
   * 
   * @param id
   *          The (unique) id of this link
   * @param source
   *          The source-node
   * @param target
   *          The target-node
   * @param directed
   *          Sets the state of this link to directed (true) or undirected (false)
   * @param weight
   *          The value of the weight-metric of this link
   */
  public Link(int id, boolean directed)
  {
    this.id = id;

    setDirected(directed);
  }


  /**
   * Creates an unweighted link between two nodes using the specified parameters.
   * 
   * @param id
   *          The (unique) id of this link
   * @param source
   *          The source-node
   * @param target
   *          The target-node
   * @param directed
   *          Sets the state of this link to directed (true) or undirected (false)
   */
  public Link(int id)
  {
    this(id, true, 1.0);
  }


  public int getId()
  {
    return id;
  }


  /**
   * Returns if this link is directed or undirected.
   * 
   * @return True, if the link is directed and false otherwise
   */
  public boolean isDirected()
  {
    return directed;
  }


  /**
   * Sets the state of this link to directed or undirected
   * 
   * @param directed
   *          True for a directed link and false for an undirected link
   */
  public void setDirected(boolean directed)
  {
    this.directed = directed;
  }


  /**
   * Returns the value of the link-weight-metric stored via {@link MetricContainer#LINKWEIGHT}.
   * 
   * @return The value of link-weight
   */
  public double getWeight()
  {
    return getMetric(MetricContainer.LINKWEIGHT);
  }


  /**
   * Sets the value of the link-weight-metric which is stored via {@link MetricContainer#LINKWEIGHT}.
   * 
   * @param weight
   *          The value of the link-weight
   */
  public void setWeight(double weight)
  {
    setMetric(MetricContainer.LINKWEIGHT, weight);
  }
}
