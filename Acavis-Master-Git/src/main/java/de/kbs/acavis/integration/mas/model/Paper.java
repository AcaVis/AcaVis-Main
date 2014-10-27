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


/**
 * Local representation of a paper-entity of MAS's ER-model.
 * 
 * @author Sebastian
 */
@Entity
@Table(name = "Paper")
public class Paper
{
  @Id
  @Column(name = "ID")
  private long id;

  // 'DocType'-field explanation:
  // ---------
  // Indicates the document-class of paper.
  // 0: Unknown
  // 1: Paper
  // 2: Book
  // 3: Poster

  // @Column(name = "DocType")
  // private short docType;

  /*
   * A length of 1000 chars at max is specified by MAS. Own tests surprisingly showed that there are titles with about 2.5k chars.
   */
  @Column(name = "Title", length = 8000)
  private String title;

  @Column(name = "Year")
  private Integer year;

  /*
   * There is no length specified (strange?) but I suggest there will be no abstract longer than about 16k chars.
   */
  @Column(name = "Abstract", length = 16384)
  private String abstractText;


  /**
   * This constructor should <b>never</b> be used on the developers side!
   */
  public Paper()
  {}


  public Paper(OEntity paper)
  {
    this.id = (long) EntityHelper.<Integer> extractProperty(paper, "ID");
    this.title = EntityHelper.<String> extractProperty(paper, "Title");
    this.year = EntityHelper.<Integer> extractProperty(paper, "Year");
    this.abstractText = EntityHelper.<String> extractProperty(paper, "Abstract");
  }


  public Paper(long id, String title, int year, String abstractText)
  {
    this.id = id;
    this.title = title;
    this.year = year;
    this.abstractText = abstractText;
  }


  @Override
  public boolean equals(Object anotherObject)
  {
    if (this == anotherObject)
      return true;

    if (anotherObject == null || getClass() != anotherObject.getClass())
      return false;

    Paper anotherPaper = (Paper) anotherObject;

    return getId() == anotherPaper.getId();
  }


  @Override
  public int hashCode()
  {
    return (int) id;
  }


  public Long getId()
  {
    return id;
  }

}
