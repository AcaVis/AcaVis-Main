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
import javax.persistence.Id;
import javax.persistence.Table;

import org.odata4j.core.OEntity;

import de.kbs.acavis.integration.mas.odata.EntityHelper;


@Entity
@Table(name = "Domain")
public class Domain
{
  @Id
  @Column(name = "ID")
  private long id = 0;

  @Column(name = "Name", length = 250)
  private String name = "";


  /**
   * This constructor should <b>never</b> be used on the developers side!
   */
  public Domain()
  {}


  public Domain(OEntity domain)
  {
    id = EntityHelper.<Integer> extractProperty(domain, "ID");
    name = EntityHelper.<String> extractProperty(domain, "Name");
  }


  public Domain(int id, String name)
  {
    this.id = id;
    this.name = name;
  }


  @Override
  public boolean equals(Object anotherObject)
  {
    if (this == anotherObject)
      return true;

    if (anotherObject == null || !(anotherObject instanceof Domain))
      return false;

    Domain anotherDomain = (Domain) anotherObject;

    return getId() == anotherDomain.getId();
  }


  @Override
  public int hashCode()
  {
    return (int) id;
  }


  public long getId()
  {
    return id;
  }


  public String getName()
  {
    return name;
  }

}
