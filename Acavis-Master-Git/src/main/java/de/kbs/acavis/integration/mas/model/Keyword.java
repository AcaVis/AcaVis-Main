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
@Table(name = "Keyword")
public class Keyword
{
  @Id
  @Column(name = "ID", nullable = false)
  private long id;

  @Column(name = "DisplayName")
  private String displayName;


  // 'Type'-field explanation:
  // ---------
  // Indicates the keyword's location within a paper.
  // 0: No keyword.
  // 1: Text of the paper.
  // 2: Abstract of the paper.
  // 4: Title of the paper.
  // 8: Keyword section of the paper.

  /**
   * This constructor should <b>never</b> be used on the developers side!
   */
  public Keyword()
  {}


  public Keyword(long id, String displayName)
  {
    this.id = id;
    this.displayName = displayName;
  }


  public Keyword(OEntity keyword)
  {
    this.id = (long) EntityHelper.<Integer> extractProperty(keyword, "ID");
    this.displayName = EntityHelper.<String> extractProperty(keyword, "DisplayName");
  }


  @Override
  public boolean equals(Object anotherObject)
  {
    if (this == anotherObject)
      return true;

    if (anotherObject == null || !(anotherObject instanceof Keyword))
      return false;

    Keyword anotherKeyword = (Keyword) anotherObject;

    return getId() == anotherKeyword.getId();
  }


  public int hashCode()
  {
    return (int) id;
  }


  public Long getId()
  {
    return id;
  }


  public String getDisplayName()
  {
    return displayName;
  }
}
