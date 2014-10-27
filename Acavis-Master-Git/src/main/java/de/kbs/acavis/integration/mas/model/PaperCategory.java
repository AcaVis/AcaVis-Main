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
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.odata4j.core.OEntity;

import de.kbs.acavis.integration.mas.odata.EntityHelper;


@Entity
@Table(name = "Paper_Category")
public class PaperCategory
{
  @Id
  @GeneratedValue(strategy = GenerationType.TABLE)
  private long id;

  @Column(name = "CPaperID")
  private int paperId = 0;

  @Column(name = "DomainID")
  private int domainId = 0;

  @Column(name = "SubDomainID")
  private int subDomainId = 0;


  /**
   * This constructor should <b>never</b> be used on the developers side!
   */
  public PaperCategory()
  {}


  public PaperCategory(OEntity paperCategory)
  {
    paperId = EntityHelper.<Integer> extractProperty(paperCategory, "CPaperID");
    domainId = EntityHelper.<Integer> extractProperty(paperCategory, "DomainID");
    subDomainId = EntityHelper.<Integer> extractProperty(paperCategory, "SubDomainID");
  }


  public PaperCategory(int paperId, int domainId, int subDomainId)
  {
    this.paperId = paperId;
    this.domainId = domainId;
    this.subDomainId = subDomainId;
  }


  @Override
  public boolean equals(Object anotherObject)
  {
    if (this == anotherObject)
      return true;

    if (anotherObject == null || !(anotherObject instanceof PaperCategory))
      return false;

    PaperCategory anotherPaperCategory = (PaperCategory) anotherObject;

    return getPaperId() == anotherPaperCategory.getPaperId() && getDomainId() == anotherPaperCategory.getDomainId()
        && getSubDomainId() == anotherPaperCategory.getSubDomainId();
  }


  @Override
  public int hashCode()
  {
    return (int) paperId + domainId + subDomainId;
  }


  public int getPaperId()
  {
    return paperId;
  }


  public int getDomainId()
  {
    return domainId;
  }


  public int getSubDomainId()
  {
    return subDomainId;
  }

}
