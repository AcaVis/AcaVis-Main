/*****************************************
 * This file is part of AcaVis, a tool for research of scientific
 * literature using the benefits of visualizations.
 * Copyright (C) 2014 - Sebastian Holzki
 * 
 * AcaVis is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License Version 2
 * as published by the Free Software Foundation;
 *****************************************/
package de.kbs.acavis.integration.mas.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;


/**
 * The primary-key container of a paper-reference (citaion) from MAS.<br />
 * A reference consist of a source-paper (identified by SrcID) and a destination paper (DstID). The combination of these papers'
 * primary-keys is the complex identifier of a paper-reference-entity.
 * 
 * @author Sebastian
 */
@Embeddable
public class PaperRefPrimaryKey
  implements Serializable
{
  private static final long serialVersionUID = 5221451952741298639L;

  @Column(name = "SrcID", nullable = false)
  private int srcId;

  @Column(name = "DstID", nullable = false)
  private int dstId;


  /**
   * This constructor should <b>never</b> be used on the developers side!
   */
  public PaperRefPrimaryKey()
  {}


  public PaperRefPrimaryKey(int srcId, int dstId)
  {
    this.srcId = srcId;
    this.dstId = dstId;
  }


  @Override
  public int hashCode()
  {
    return (int) srcId + dstId;
  }


  @Override
  public boolean equals(Object anotherObject)
  {
    if (this == anotherObject)
      return true;

    if (anotherObject == null || !(anotherObject instanceof PaperRefPrimaryKey))
      return false;

    PaperRefPrimaryKey anotherPrimaryKey = (PaperRefPrimaryKey) anotherObject;

    return getSrcId() == anotherPrimaryKey.getSrcId() && getDstId() == anotherPrimaryKey.getDstId();
  }


  public int getSrcId()
  {
    return srcId;
  }


  public void setSrcId(int srcId)
  {
    this.srcId = srcId;
  }


  public int getDstId()
  {
    return dstId;
  }


  public void setDstId(int dstId)
  {
    this.dstId = dstId;
  }

}
