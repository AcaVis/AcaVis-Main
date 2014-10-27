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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.odata4j.core.OEntity;

import de.kbs.acavis.integration.mas.odata.EntityHelper;


@Entity
@Table(name = "Paper_Keyword")
public class PaperKeyword
{
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Paper_Keyword_Sequence")
  @SequenceGenerator(name = "Paper_Keyword_Sequence", sequenceName = "paper_keyword_id")
  private Long id;

  @Column(name = "CPaperID")
  private Integer cpaperId;

  @Column(name = "KeywordID")
  private Integer keywordId;


  /**
   * This constructor should <b>never</b> be used on the developers side!
   */
  public PaperKeyword()
  {}


  public PaperKeyword(int cpaperId, int KeywordId)
  {
    this.cpaperId = cpaperId;
    this.keywordId = KeywordId;
  }


  public PaperKeyword(OEntity paperKeyword)
  {
    this.cpaperId = EntityHelper.<Integer> extractProperty(paperKeyword, "CPaperID");
    this.keywordId = EntityHelper.<Integer> extractProperty(paperKeyword, "KeywordID");
  }


  @Override
  public boolean equals(Object anotherObject)
  {
    if (this == anotherObject)
      return true;

    if (anotherObject == null || !(anotherObject instanceof PaperKeyword))
      return false;

    PaperKeyword anotherPaperKeyword = (PaperKeyword) anotherObject;

    return getCpaperId() == anotherPaperKeyword.getCpaperId() && getKeywordId() == anotherPaperKeyword.getKeywordId();
  }


  @Override
  public int hashCode()
  {
    return (int) cpaperId + keywordId;
  }


  public int getCpaperId()
  {
    return cpaperId;
  }


  public int getKeywordId()
  {
    return keywordId;
  }
}
