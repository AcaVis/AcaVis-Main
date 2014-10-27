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
 * Indicates that the used integration implements the called interface-method, as it's part of the integration-layer interface, but the
 * integrated service doesn't offer any suitable data for this purpose.
 * 
 * @author Sebastian
 *
 */
public class UnimplementedFeatureException
  extends Exception
{
  private static final long serialVersionUID = -4066452415115013348L;


  public UnimplementedFeatureException(String message)
  {
    super(message);
  }
}
