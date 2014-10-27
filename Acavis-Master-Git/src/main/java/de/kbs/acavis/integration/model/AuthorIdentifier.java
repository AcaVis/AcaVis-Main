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

import de.kbs.acavis.integration.IdentifierException;


public class AuthorIdentifier
  implements Serializable
{
  private static final long serialVersionUID = -5277210803211450058L;

  private int masIdentifier = -1;

  // Experimental: Unused and untested yet!
  private String openId = "";
  private String orcId = "";


  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + masIdentifier;
    return result;
  }


  @Override
  public boolean equals(Object anotherObject)
  {
    if (this == anotherObject)
      return true;
    if (anotherObject == null || getClass() != anotherObject.getClass())
      return false;

    AuthorIdentifier anotherIdentifier = (AuthorIdentifier) anotherObject;

    try
    {
      return masIdentifier == anotherIdentifier.getMasIdentifier();
    }
    catch (IdentifierException e)
    {
      return false;
    }
  }


  public int getMasIdentifier()
    throws IdentifierException
  {
    if (!validateMasIdentifier((masIdentifier)))
      throw new IdentifierException("");

    return masIdentifier;
  }


  public void setMasIdentifier(int masIdentifier)
  {
    this.masIdentifier = masIdentifier;
  }


  public static boolean validateMasIdentifier(int masIdentifier)
  {
    return masIdentifier >= 0;
  }

}
