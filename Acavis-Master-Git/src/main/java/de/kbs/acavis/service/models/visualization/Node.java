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

import org.codehaus.jackson.annotate.JsonIgnore;


/**
 * @author Sebastian
 *
 */
public class Node
{
  private int id = -1;
  private String name = "";
  private int cluster = 0;

  @JsonIgnore
  protected MetricContainer metricContainer = new MetricContainer();


  public Node(int id, String name)
  {
    this.id = id;
    this.name = name;
  }


  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + id;
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    return result;
  }


  @Override
  public boolean equals(Object anotherObject)
  {
    if (this == anotherObject)
      return true;

    if (anotherObject == null || getClass() != anotherObject.getClass())
      return false;

    Node anotherNode = (Node) anotherObject;

    if (getId() != anotherNode.getId())
      return false;

    if (name == null)
    {
      if (anotherNode.name != null)
        return false;
    }
    else if (!name.equals(anotherNode.name))
      return false;

    return true;
  }


  public String toString()
  {
    return "Node " + id;
  }


  public String getName()
  {
    return name;
  }


  public void setName(String name)
  {
    this.name = name;
  }


  public int getId()
  {
    return id;
  }


  public void setCluster(int cluster)
  {
    this.cluster = cluster;
  }


  public int getCluster()
  {
    return cluster;
  }


  public Double getMetric(String metricName)
  {
    return metricContainer.getMetric(metricName);
  }


  public void setMetric(String metricName, double metricValue)
  {
    metricContainer.setMetric(metricName, metricValue);
  }
}
