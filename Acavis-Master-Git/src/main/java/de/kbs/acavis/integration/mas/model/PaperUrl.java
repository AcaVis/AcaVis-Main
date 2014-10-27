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
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.odata4j.core.OEntity;

import de.kbs.acavis.integration.mas.odata.EntityHelper;


@Entity
@Table(name = "Paper_Url")
public class PaperUrl
{
  @Id
  @Column(name = "ID")
  private long id;

  @Column(name = "PaperID")
  private Integer paperId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "PaperID", insertable = false, updatable = false)
  private Paper paper;

  /*
   * A length of 1000 chars at max is specified by MAS.
   */
  @Column(name = "Url", length = 1000)
  private String url;


  /**
   * This constructor should <b>never</b> be used on the developers side!
   */
  public PaperUrl()
  {}


  public PaperUrl(long id, int paperId, String url)
  {
    this.id = id;
    this.paperId = paperId;
    this.url = url;
  }


  public PaperUrl(OEntity paperUrl)
  {
    this.id = (long) EntityHelper.<Integer> extractProperty(paperUrl, "ID");
    this.paperId = EntityHelper.<Integer> extractProperty(paperUrl, "PaperID");
    this.url = EntityHelper.<String> extractProperty(paperUrl, "Url");
  }


  @Override
  public boolean equals(Object anotherObject)
  {
    if (this == anotherObject)
      return true;

    if (anotherObject == null || !(anotherObject instanceof PaperUrl))
      return false;

    PaperUrl anotherPaperUrl = (PaperUrl) anotherObject;

    return getId() == anotherPaperUrl.getId() && getPaperId() == anotherPaperUrl.getPaperId();
  }


  @Override
  public int hashCode()
  {
    return (int) id + paperId;
  }


  public Long getId()
  {
    return id;
  }


  public Integer getPaperId()
  {
    return paperId;
  }


  public String getUrl()
  {
    return url;
  }
}
