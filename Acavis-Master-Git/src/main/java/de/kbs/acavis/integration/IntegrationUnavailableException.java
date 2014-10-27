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

/**
 * Indicates that there are temporal problems using the selected integration. This may be caused by the integrated service being unreachable
 * or the implementation of data-collection being out of date with the integrated services' specification.
 * 
 * @author Sebastian
 *
 */
public class IntegrationUnavailableException
  extends Exception
{
  private static final long serialVersionUID = 7927957238565651502L;


  public IntegrationUnavailableException(String message, Throwable cause)
  {
    super(message, cause);
  }


  public IntegrationUnavailableException(String message)
  {
    super(message);
  }
}
