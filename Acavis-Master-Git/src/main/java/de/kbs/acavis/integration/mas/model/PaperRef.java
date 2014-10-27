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

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.odata4j.core.OEntity;


@Entity
@Table(name = "Paper_Ref")
public class PaperRef
{
  @EmbeddedId
  private PaperRefPrimaryKey pk;

  @Column(name = "SeqID")
  private Integer seqId;


  /**
   * This constructor should <b>never</b> be used on the developers side!
   */
  public PaperRef()
  {}


  public PaperRef(int srcId, int dstId, int seqId)
  {
    pk = new PaperRefPrimaryKey(srcId, dstId);

    this.seqId = seqId;
  }


  public PaperRef(OEntity paperReference)
  {
    int srcId = paperReference.getProperty("SrcID", Integer.class).getValue();
    int dstId = paperReference.getProperty("DstID", Integer.class).getValue();

    pk = new PaperRefPrimaryKey(srcId, dstId);

    this.seqId = (int) paperReference.getProperty("SeqID", Short.class).getValue();
  }


  @Override
  public int hashCode()
  {
    return pk.hashCode();
  }


  @Override
  public boolean equals(Object anotherObject)
  {
    if (this == anotherObject)
      return true;

    if (anotherObject == null || !(anotherObject instanceof PaperRef))
      return false;

    PaperRef anotherPaperRef = (PaperRef) anotherObject;

    return pk.equals(anotherPaperRef);
  }


  public PaperRefPrimaryKey getPk()
  {
    return pk;
  }


  public long getSrcId()
  {
    return pk.getSrcId();
  }


  public long getDstId()
  {
    return pk.getDstId();
  }


  @Override
  public String toString()
  {
    return "Paper-reference from <" + getSrcId() + "> to <" + getDstId() + ">";
  }
}
