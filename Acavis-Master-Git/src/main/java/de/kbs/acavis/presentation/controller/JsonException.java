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
public class JsonException
  extends Exception
{
  private static final long serialVersionUID = -3286010109821105966L;

  private String majorText = "";
  private String minorText = "";


  public JsonException(String majorText, String minorText)
  {
    super(majorText + " - " + minorText);

    this.majorText = majorText;
    this.minorText = minorText;
  }


  public JsonException(String majorText)
  {
    super(majorText);

    this.majorText = majorText;
  }


  public String getMajorText()
  {
    return majorText;
  }


  public String getMinorText()
  {
    return minorText;
  }
}
