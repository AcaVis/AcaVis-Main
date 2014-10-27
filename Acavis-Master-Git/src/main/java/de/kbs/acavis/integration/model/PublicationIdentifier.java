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


/**
 * A container-class for all the available publication-identifiers. None of the identifiers has to be set, but at least one of them should
 * be set to get proper results when using them along the application-layers.<br />
 * <br />
 * If for some reason a new identifier is needed or should be accessible on the upper layers, a field and corresponding getters/setters have
 * to be added. Additionally, every identifier should have a comparator within the {@link #equals(PublicationIdentifier)} method.
 * 
 * @author Sebastian
 *
 */
public class PublicationIdentifier
  implements Serializable
{
  private static final long serialVersionUID = 5406837546845938481L;

  /**
   * Identifier-names for each identifier-option
   */
  private int mas = -1;
  private long mendeley = -1;
  private String doi = "";
  private String isbn = "";


  public int getMasIdentifier()
    throws IdentifierException
  {
    if (mas < 0)
      throw new IdentifierException("The MAS-Identifier was not set or is invalid");

    return mas;
  }


  public void setMasIdentifier(int mas)
    throws IdentifierException
  {
    if (this.mas < 0)
      this.mas = mas;
    else
      throw new IdentifierException("The given MAS-identifier has invalid format");
  }


  public long getMendeleyIdentifier()
    throws IdentifierException
  {
    if (mendeley < 0)
      throw new IdentifierException("The Mendeley-Identifier was not set or is invalid");

    return mendeley;
  }


  public void setMendeleyIdentifier(long mendeley)
    throws IdentifierException
  {
    if (this.mendeley < 0)
      this.mendeley = mendeley;
    else
      throw new IdentifierException("The given doi-identifier has invalid format");
  }


  public String getDoiIdentifier()
    throws IdentifierException
  {
    if (doi == null || doi.isEmpty())
      throw new IdentifierException("The DOI-Identifier was not set or is invalid");

    return doi;
  }


  public void setDoiIdentifier(String doi)
    throws IdentifierException
  {
    if (isValidDoi(this.doi))
      this.doi = doi;
    else
      throw new IdentifierException("The given doi-identifier has invalid format");
  }


  /**
   * Checks if a given doi-identifier is valid. Only raw doi-identifiers are recognized, urls like dx.doi.org/... etc. will result in an
   * invalid doi!<br />
   * Credits go to <a href="https://github.com/jprante">Jörg Prante (jprante)</a>.
   * 
   * @param doi
   *          A doi-identifier-string to test for validity
   * @return True if the string has some valid doi-format
   */
  public static boolean isValidDoi(String doi)
  {
    return doi.matches("\\b10\\.\\d{4}([.][0-9]+)*/[a-z0-9/\\-.()<>_:;\\\\]+\\b");
  }


  public String getIsbnIdentifier()
    throws IdentifierException
  {
    if (isbn == null || isbn.isEmpty())
      throw new IdentifierException("The ISBN-Identifier was not set or is invalid");

    return isbn;
  }


  public void setIsbnIdentifier(String isbn)
  {
    this.isbn = isbn;
  }


  /**
   * If at least one of the identifiers is equal, this method returns true.
   */
  @Override
  public boolean equals(Object anotherObject)
  {
    // First the usual equals-checkups
    if (anotherObject == null || !getClass().equals(anotherObject.getClass()))
      return false;

    PublicationIdentifier anotherIdentifier = (PublicationIdentifier) anotherObject;

    // Check every identifier for invalid values and for equality
    try
    {
      if (getMasIdentifier() == anotherIdentifier.getMasIdentifier())
        return true;
    }
    catch (IdentifierException e)
    {
      System.err.println(e.getMessage());
    }

    try
    {
      if (getMendeleyIdentifier() == anotherIdentifier.getMendeleyIdentifier())
        return true;
    }
    catch (IdentifierException e)
    {
      System.err.println(e.getMessage());
    }

    try
    {
      if (getDoiIdentifier().equals(anotherIdentifier.getDoiIdentifier()))
        return true;
    }
    catch (IdentifierException e)
    {
      System.err.println(e.getMessage());
    }

    try
    {
      if (getIsbnIdentifier().equals(anotherIdentifier.getIsbnIdentifier()))
        return true;
    }
    catch (IdentifierException e)
    {
      System.err.println(e.getMessage());
    }

    return false;
  }


  @Override
  public int hashCode()
  {
    return (int) (mas * mendeley) % 2000000000;
  }
}
