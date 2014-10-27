/*****************************************
 * This file is part of AcaVis, a tool for research of scientific
 * literature using the benefits of visualizations.
 * Copyright (C) 2014 - Sebastian Holzki
 * 
 * AcaVis is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License Version 2
 * as published by the Free Software Foundation;
 *****************************************/
package de.kbs.acavis.integration;

public class PublicationUnavailableException
  extends Exception
{
  private static final long serialVersionUID = 8890171324220865161L;


  public PublicationUnavailableException(String messsage)
  {
    super(messsage);
  }
}
