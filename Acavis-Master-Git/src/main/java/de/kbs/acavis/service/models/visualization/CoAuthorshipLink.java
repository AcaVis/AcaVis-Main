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

import java.util.HashSet;
import java.util.Set;

import de.kbs.acavis.integration.model.PublicationIdentifier;


/**
 * Represents a co-authorship of two authors in a co-authorship-network. A co-authorship is "identified" by a set of publications that two
 * authors have written together. The weight of a co-authorship-link is the number of these publications (by convention of this model).
 * 
 * @author Sebastian
 */
public class CoAuthorshipLink
  extends Link
{
  /**
   * Container for a simple link-publication. It consists of an identifier, a title and the publication-year.
   * 
   * @author Sebastian
   */
  public class CoPublication
  {
    public PublicationIdentifier identifier = null;
    public String title = "";
    public int year = -1;


    public CoPublication(PublicationIdentifier identifier, String title, int year)
    {
      this.identifier = identifier;
      this.title = title;
      this.year = year;
    }
  }


  private Set<CoPublication> coPublications = new HashSet<CoPublication>();


  /**
   * The simplest constructor with just a representational id. The initial weight of a link equals 0.
   * 
   * @param id
   *          The sequential id as needed by D3
   */
  public CoAuthorshipLink(int id)
  {
    // A CoAuthorship is undirected by convention and the initial weight is zero
    super(id, false, 0);
  }


  /**
   * A sophisticated constructor using additional information about an initial publication. Afterwards this link has a real valid state. and
   * the weight of 1.
   * 
   * @param id
   *          The sequential id as needed by D3
   * @param identifier
   *          The identifier of the initial publication
   * @param title
   *          The title of the initial publication
   * @param year
   *          The year of publication of the initial publication
   */
  public CoAuthorshipLink(int id, PublicationIdentifier identifier, String title, int year)
  {
    this(id);

    addCoPublication(identifier, title, year);
  }


  /**
   * Adds Information about a publication to the internal set of publications that the two authors affected by this link have published
   * together.
   * 
   * @param identifier
   *          The identifier of the initial publication
   * @param title
   *          The title of the initial publication
   * @param year
   *          The year of publication of the initial publication
   */
  public void addCoPublication(PublicationIdentifier identifier, String title, int year)
  {
    coPublications.add(new CoPublication(identifier, title, year));

    setWeight(coPublications.size());
  }

}
