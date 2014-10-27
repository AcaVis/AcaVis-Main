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

import de.kbs.acavis.integration.model.AuthorIdentifier;


public class AuthorNode
  extends Node
  implements Comparable<AuthorNode>
{
  private AuthorIdentifier identifier = null;
  private String authorName = "";


  public AuthorNode(int id, AuthorIdentifier identifier, String authorName)
  {
    super(id, authorName);

    this.identifier = identifier;
    this.authorName = authorName;
  }


  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((identifier == null) ? 0 : identifier.hashCode());
    return result;
  }


  @Override
  public boolean equals(Object anotherObject)
  {
    if (this == anotherObject)
      return true;

    if (!super.equals(anotherObject) || getClass() != anotherObject.getClass())
      return false;

    AuthorNode anotherAuthorNode = (AuthorNode) anotherObject;

    if (identifier == null)
    {
      if (anotherAuthorNode.identifier != null)
        return false;
    }

    return identifier.equals(anotherAuthorNode.identifier);
  }


  public int compareTo(AuthorNode anotherAuthor)
  {
    return this.getId() - anotherAuthor.getId();
  }


  public AuthorIdentifier getIdentifier()
  {
    return identifier;
  }


  public String getAuthorsLastname()
  {
    return authorName.substring(authorName.lastIndexOf(" ") + 1);
  }

}
