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

import de.kbs.acavis.integration.mas.odata.EntityHelper;


@Entity
@Table(name = "Category")
public class Category
{
  @EmbeddedId
  private CategoryPrimaryKey pk;

  @Column(name = "Name", length = 250)
  private String name = "";


  /**
   * This constructor should <b>never</b> be used on the developers side!
   */
  public Category()
  {}


  public Category(OEntity category)
  {
    int domainId = EntityHelper.<Integer> extractProperty(category, "DomainID");
    int subDomainId = EntityHelper.<Integer> extractProperty(category, "SubDomainID");

    pk = new CategoryPrimaryKey(domainId, subDomainId);

    name = EntityHelper.<String> extractProperty(category, "Name");
  }


  public Category(int domainId, int subDomainId, String name)
  {
    pk = new CategoryPrimaryKey(domainId, subDomainId);

    this.name = name;
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

    if (anotherObject == null || !(anotherObject instanceof Category))
      return false;

    Category anotherCategory = (Category) anotherObject;

    return getPk().equals(anotherCategory.getPk());
  }


  public CategoryPrimaryKey getPk()
  {
    return pk;
  }


  public int getDomainId()
  {
    return pk.getDomainId();
  }


  public int getSubDomainId()
  {
    return pk.getSubDomainId();
  }


  public String getName()
  {
    return name;
  }

}
