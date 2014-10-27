/*****************************************
 * This file is part of AcaVis, a tool for research of scientific
 * literature using the benefits of visualizations.
 * Copyright (C) 2014 - Sebastian Holzki
 * 
 * AcaVis is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License Version 2
 * as published by the Free Software Foundation;
 *****************************************/
package de.kbs.acavis;

import java.io.IOException;

import de.kbs.acavis.integration.IdentifierException;
import de.kbs.acavis.integration.model.PublicationIdentifier;
import de.kbs.acavis.service.SerializationHelper;


public class TestSerialization
{

  public static void main(String[] args)
  {
    PublicationIdentifier p = new PublicationIdentifier();
    try
    {
      p.setMasIdentifier(35);
    }
    catch (IdentifierException e)
    {
      e.printStackTrace();
    }

    try
    {
      String ps = SerializationHelper.serializePublicationIdentifierBase64(p);
      PublicationIdentifier p2 = SerializationHelper.deserializePublicationIdentifierBase64(ps);

      System.out.println(ps);

      System.out.println(p2.getMasIdentifier());

    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
    catch (ClassNotFoundException e)
    {
      e.printStackTrace();
    }
    catch (IdentifierException e)
    {
      e.printStackTrace();
    }
    // catch (EncoderException e)
    // {
    // e.printStackTrace();
    // }
    // catch (DecoderException e)
    // {
    // e.printStackTrace();
    // }

  }

}
