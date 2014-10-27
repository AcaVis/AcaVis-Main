/*****************************************
 * This file is part of AcaVis, a tool for research of scientific
 * literature using the benefits of visualizations.
 * Copyright (C) 2014 - Sebastian Holzki
 * 
 * AcaVis is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License Version 2
 * as published by the Free Software Foundation;
 *****************************************/
package de.kbs.acavis.service;

public class InvalidIntegrationException
  extends Exception
{
  private static final long serialVersionUID = 3037394880349313947L;


  public InvalidIntegrationException(String message)
  {
    super(message);
  }
}
