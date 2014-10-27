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
 * The primary-key container of a category-entity from MAS.<br />
 * A category-entity in MAS is identified by the combination of its parent-domains primary-key (DomainID) and a sequence-number
 * (SubDomainID). Every parent-domain has its own sequence. This convention (by MAS) causes the sequence to not be a primary-key of a
 * category on its own and makes this complex identifier necessary.
 * 
 * @author Sebastian
 *
 */
@Embeddable
public class CategoryPrimaryKey
  implements Serializable
{
  private static final long serialVersionUID = 8610447298590220118L;

  @Column(name = "DomainID")
  private int domainId;

  @Column(name = "SubDomainID")
  private int subDomainId;


  /**
   * This constructor should <b>never</b> be used on the developers side!
   */
  public CategoryPrimaryKey()
  {}


  public CategoryPrimaryKey(int domainId, int subDomainId)
  {
    this.domainId = domainId;
    this.subDomainId = subDomainId;
  }


  public int hashCode()
  {
    return (int) domainId + subDomainId;
  }


  public boolean equals(Object anotherObject)
  {
    if (anotherObject == this)
      return true;

    if (anotherObject == null || !(anotherObject instanceof CategoryPrimaryKey))
      return false;

    CategoryPrimaryKey anotherPk = (CategoryPrimaryKey) anotherObject;

    return getDomainId() == anotherPk.getDomainId() && getSubDomainId() == anotherPk.getSubDomainId();
  }


  public int getDomainId()
  {
    return domainId;
  }


  public void setDomainId(int domainId)
  {
    this.domainId = domainId;
  }


  public int getSubDomainId()
  {
    return subDomainId;
  }


  public void setSubDomainId(int subDomainId)
  {
    this.subDomainId = subDomainId;
  }

}
