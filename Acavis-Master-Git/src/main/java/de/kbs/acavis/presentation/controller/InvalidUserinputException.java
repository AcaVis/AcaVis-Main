/*****************************************
 * This file is part of AcaVis, a tool for research of scientific
 * literature using the benefits of visualizations.
 * Copyright (C) 2014 - Sebastian Holzki
 * 
 * AcaVis is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License Version 2
 * as published by the Free Software Foundation;
 *****************************************/
package de.kbs.acavis.presentation.controller;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;


@JsonIgnoreProperties({ "cause", "stackTrace", "suppressed" })
public class InvalidUserinputException
  extends Exception
{
  private static final long serialVersionUID = 2694040669037223142L;


  public InvalidUserinputException(String message, Throwable e)
  {
    super(message, e);
  }


  public InvalidUserinputException(String message)
  {
    super(message);
  }
}
