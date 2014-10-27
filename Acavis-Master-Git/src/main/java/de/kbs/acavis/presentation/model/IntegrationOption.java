/*****************************************
 * This file is part of AcaVis, a tool for research of scientific
 * literature using the benefits of visualizations.
 * Copyright (C) 2014 - Sebastian Holzki
 * 
 * AcaVis is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License Version 2
 * as published by the Free Software Foundation;
 *****************************************/
package de.kbs.acavis.presentation.model;

/**
 * Data-container for integrations
 * 
 * @author Sebastian
 *
 */
public class IntegrationOption
{
  private String qualifiedname = "";
  private String label = "";


  public IntegrationOption(String qualifiedname, String label)
  {
    this.qualifiedname = qualifiedname;
    this.label = label;
  }


  public String getQualifiedname()
  {
    return qualifiedname;
  }


  public String getLabel()
  {
    return label;
  }
}
