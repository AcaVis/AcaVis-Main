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
 * Represents a citation of a target-publication by a source-publication using their IDs. The field-names are chosen to be suitable for D3.
 * 
 * @author Sebastian
 */
public class CitationLink
  extends Link
{
  private int source = 0;
  private int target = 0;


  /**
   * Creates a directed link (citation) between two publications, where a source-node cites a target-node.
   * 
   * @param id
   *          Unique-id of this link
   * @param source
   *          The <b>id</b> of the source-node (As set at node-constructor)
   * @param target
   *          The <b>id</b> of the target-node (As set at node-constructor)
   */
  public CitationLink(int id, int source, int target)
  {
    super(id);

    this.source = source;
    this.target = target;
  }


  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + source;
    result = prime * result + target;
    return result;
  }


  @Override
  public boolean equals(Object anotherObject)
  {
    if (this == anotherObject)
      return true;

    if (anotherObject == null || getClass() != anotherObject.getClass())
      return false;

    CitationLink anotherLink = (CitationLink) anotherObject;

    return source == anotherLink.getSource() && target == anotherLink.getTarget();
  }


  public int getSource()
  {
    return source;
  }


  public int getTarget()
  {
    return target;
  }

}
