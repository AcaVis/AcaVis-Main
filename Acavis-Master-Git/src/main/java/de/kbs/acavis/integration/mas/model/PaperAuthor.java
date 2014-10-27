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

import javax.persistence.*;

import org.odata4j.core.OEntity;


@Entity
@Table(name = "Paper_Author")
public class PaperAuthor
  implements Comparable<PaperAuthor>
{
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Paper_Author_Sequence")
  @SequenceGenerator(name = "Paper_Author_Sequence", sequenceName = "paper_author_id")
  private long id;

  @Column(name = "PaperID")
  private int paperId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "PaperID", insertable = false, updatable = false)
  private Paper paper;

  @Column(name = "AuthorID")
  private int authorId;

  @Column(name = "SeqID")
  private short seqId;

  @Column(name = "Name")
  private String name;


  /**
   * This constructor should <b>never</b> be used on the developers side!
   */
  public PaperAuthor()
  {}


  public PaperAuthor(OEntity paperAuthor)
  {
    this.paperId = paperAuthor.getProperty("PaperID", Integer.class).getValue();
    this.authorId = paperAuthor.getProperty("AuthorID", Integer.class).getValue();
    this.seqId = paperAuthor.getProperty("SeqID", Short.class).getValue();
    this.name = paperAuthor.getProperty("Name", String.class).getValue();
  }


  @Override
  public boolean equals(Object anotherObject)
  {
    if (this == anotherObject)
      return true;

    if (anotherObject == null || !(anotherObject instanceof PaperAuthor))
      return false;

    PaperAuthor anotherPaperAuthor = (PaperAuthor) anotherObject;

    return getPaperId() == anotherPaperAuthor.getPaperId() && getAuthorId() == anotherPaperAuthor.getAuthorId();
  }


  @Override
  public int hashCode()
  {
    return (int) paperId + authorId;
  }


  public int getPaperId()
  {
    return paperId;
  }


  public int getAuthorId()
  {
    return authorId;
  }


  public short getSeqId()
  {
    return seqId;
  }


  public String getName()
  {
    return name;
  }


  /**
   * Extracts the last name of the author.
   * 
   * @return A String containing the last name
   */
  public String getLastName()
  {
    return name.substring(name.lastIndexOf(" ") + 1);
  }


  /**
   * Compares the author by column SeqID in ascending order.
   */
  @Override
  public int compareTo(PaperAuthor anotherAuthor)
  {
    return getSeqId() - anotherAuthor.getSeqId();
  }
}
