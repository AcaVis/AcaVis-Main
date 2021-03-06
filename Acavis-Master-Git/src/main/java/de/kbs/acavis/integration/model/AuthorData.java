/*****************************************
 * This file is part of AcaVis, a tool for research of scientific
 * literature using the benefits of visualizations.
 * Copyright (C) 2014 - Sebastian Holzki
 * 
 * AcaVis is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License Version 2
 * as published by the Free Software Foundation;
 *****************************************/
package de.kbs.acavis.integration.model;

import java.io.Serializable;


public abstract class AuthorData
  implements Serializable
{
  private static final long serialVersionUID = -3429190444175946191L;


  public abstract AuthorIdentifier getId();


  public abstract String getName();
}
