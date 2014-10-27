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
 * Indicates that a given entity-identifier doesn't match the selected format (e.g. a primary-key being less than 1 or an invalid ISBN).<br />
 * <br />
 * Another use is to indicate that the selected identifier wasn't set in the identifier-wrapper.
 * 
 * @author Sebastian
 *
 */
public class IdentifierException
  extends Exception
{

  private static final long serialVersionUID = -6097028648617063415L;


  public IdentifierException(String message)
  {
    super(message);
  }

}
