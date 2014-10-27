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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.commons.codec.binary.Base64;

import de.kbs.acavis.integration.model.AuthorIdentifier;
import de.kbs.acavis.integration.model.PublicationIdentifier;


public class SerializationHelper
{
  public static String serializePublicationIdentifier(PublicationIdentifier identifier)
    throws IOException
  {
    ByteArrayOutputStream bo = new ByteArrayOutputStream();
    ObjectOutputStream so = new ObjectOutputStream(bo);

    so.writeObject(identifier);
    so.flush();

    String serialization = bo.toString();

    so.close();
    bo.close();

    return serialization;
  }


  public static PublicationIdentifier deserializePublicationIdentifier(String serialization)
    throws IOException, ClassNotFoundException
  {
    byte b[] = serialization.getBytes();

    ByteArrayInputStream bi = new ByteArrayInputStream(b);
    ObjectInputStream si = new ObjectInputStream(bi);

    PublicationIdentifier identifier = (PublicationIdentifier) si.readObject();

    si.close();
    bi.close();

    return identifier;
  }


  public static String serializePublicationIdentifierBase64(PublicationIdentifier identifier)
    throws IOException
  {
    return Base64.encodeBase64URLSafeString(serializePublicationIdentifier(identifier).getBytes("UTF-8"));
  }


  public static PublicationIdentifier deserializePublicationIdentifierBase64(String serialization)
    throws ClassNotFoundException, IOException
  {
    return deserializePublicationIdentifier(new String(Base64.decodeBase64(serialization.getBytes("UTF-8")), "UTF-8"));
  }


  public static String serializeAuthorIdentifier(AuthorIdentifier identifier)
    throws IOException
  {
    ByteArrayOutputStream bo = new ByteArrayOutputStream();
    ObjectOutputStream so = new ObjectOutputStream(bo);

    so.writeObject(identifier);
    so.flush();

    String serialization = bo.toString();

    so.close();
    bo.close();

    return serialization;
  }


  public static AuthorIdentifier deserializeAuthorIdentifier(String serialization)
    throws IOException, ClassNotFoundException
  {
    byte b[] = serialization.getBytes();

    ByteArrayInputStream bi = new ByteArrayInputStream(b);
    ObjectInputStream si = new ObjectInputStream(bi);

    AuthorIdentifier identifier = (AuthorIdentifier) si.readObject();

    si.close();
    bi.close();

    return identifier;
  }


  public static String serializeAuthorIdentifierBase64(AuthorIdentifier identifier)
    throws IOException
  {
    return Base64.encodeBase64URLSafeString(serializeAuthorIdentifier(identifier).getBytes("UTF-8"));
  }


  public static AuthorIdentifier deserializeAuthorIdentifierBase64(String serialization)
    throws ClassNotFoundException, IOException
  {
    return deserializeAuthorIdentifier(new String(Base64.decodeBase64(serialization.getBytes("UTF-8")), "UTF-8"));
  }
}
