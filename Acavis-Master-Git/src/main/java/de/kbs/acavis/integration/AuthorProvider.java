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

import java.util.Set;
import java.util.SortedSet;

import de.kbs.acavis.integration.model.AuthorData;
import de.kbs.acavis.integration.model.AuthorSearchData;
import de.kbs.acavis.integration.model.PublicationData;


public interface AuthorProvider
{
  public SortedSet<AuthorSearchData> searchAuthor(String query, int limit);


  public Set<PublicationData> getPublicationsOfAuthor(int authorId);


  public AuthorData getAuthor(int authorId);


  public Set<AuthorData> getAuthors(Set<Integer> authorIds);
}
