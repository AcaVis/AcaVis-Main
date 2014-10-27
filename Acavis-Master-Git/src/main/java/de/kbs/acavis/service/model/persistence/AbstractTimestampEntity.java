/*****************************************
 * This file is part of AcaVis, a tool for research of scientific
 * literature using the benefits of visualizations.
 * Copyright (C) 2014 - Sebastian Holzki
 * 
 * AcaVis is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License Version 2
 * as published by the Free Software Foundation;
 *****************************************/
package de.kbs.acavis.service.model.persistence;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


/**
 * The class defines a general model to provide timestamps of creation and last update for inheriting database-entities.<br />
 * <br />
 * <b>Use {@link CreationModificationTimestamps} instead!</b>
 * 
 * @author Sebastian
 *
 */
@MappedSuperclass
public abstract class AbstractTimestampEntity
{

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "created", nullable = false)
  private Date created;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "updated", nullable = false)
  private Date updated;


  @PrePersist
  protected void onCreate()
  {
    updated = created = new Date();
  }


  @PreUpdate
  protected void onUpdate()
  {
    updated = new Date();
  }


  public Date getCreated()
  {
    return created;
  }


  public Date getUpdated()
  {
    return updated;
  }
}
