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

public class GeneralNetworkCluster
{
  private int id = 0;
  private String name = "";


  public GeneralNetworkCluster(int id, String name)
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
    return result;
  }


  @Override
  public boolean equals(Object anotherObject)
  {
    if (this == anotherObject)
      return true;

    if (anotherObject == null || getClass() != anotherObject.getClass())
      return false;

    GeneralNetworkCluster anotherCluster = (GeneralNetworkCluster) anotherObject;

    return id == anotherCluster.getId();
  }


  public int getId()
  {
    return id;
  }


  public void setId(int id)
  {
    this.id = id;
  }


  public String getName()
  {
    return name;
  }


  public void setName(String name)
  {
    this.name = name;
  }

}
