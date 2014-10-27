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


/**
 * Will not be implemented at the moment.
 * 
 * @author Sebastian
 *
 */
@Deprecated
@Entity
@Table(name = "Author")
public class Author
{
  @Id
  @Column(name = "ID")
  private long id;

  @Column(name = "Name")
  private String name;

  @Column(name = "AffiliationID")
  private Integer affiliationId;

  @Column(name = "Affiliation", length = 500)
  private String affiliation;


  /**
   * This constructor should <b>never</b> be used on the developers side!
   */
  public Author()
  {}


  @Override
  public int hashCode()
  {
    return (int) id;
  }


  @Override
  public boolean equals(Object anotherObject)
  {
    if (this == anotherObject)
      return true;

    if (anotherObject == null || getClass() != anotherObject.getClass())
      return false;

    Author anotherAuthor = (Author) anotherObject;

    return id == anotherAuthor.id;
  }
}
