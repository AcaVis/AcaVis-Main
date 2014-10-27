/*****************************************
 * This file is part of AcaVis, a tool for research of scientific
 * literature using the benefits of visualizations.
 * Copyright (C) 2014 - Sebastian Holzki
 * 
 * AcaVis is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License Version 2
 * as published by the Free Software Foundation;
 *****************************************/
package de.kbs.acavis.service.models.visualization;

public class YearlyDistributionEntry
{
  public short year = 0;
  public int count = 0;


  public YearlyDistributionEntry(short year, int count)
  {
    this.year = year;
    this.count = count;
  }
}
