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
import javax.persistence.Embeddable;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


/**
 * May be embedded by an entity to store creation- and modification-timestamps. The modification-timestamp is updated automatically on every
 * update.
 * 
 * @author Sebastian
 */
@Embeddable
@MappedSuperclass
public class CreationModificationTimestamps
{
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "created", nullable = false)
  private Date created;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "updated", nullable = false)
  private Date updated;


  protected void onCreate()
  {
    updated = created = new Date();
  }


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
