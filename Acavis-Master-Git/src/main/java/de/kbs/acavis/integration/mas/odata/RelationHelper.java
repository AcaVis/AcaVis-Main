/*****************************************
 * This file is part of AcaVis, a tool for research of scientific
 * literature using the benefits of visualizations.
 * Copyright (C) 2014 - Sebastian Holzki
 * 
 * AcaVis is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License Version 2
 * as published by the Free Software Foundation;
 *****************************************/
package de.kbs.acavis.integration.mas.odata;

import java.util.Set;

import org.core4j.Enumerable;
import org.odata4j.core.OEntity;


public class RelationHelper
{

  public static Set<Integer> extractReferencedPaperIds(Enumerable<OEntity> references)
  {
    return EntityHelper.<Integer> extractProperty(references, "DstID");
  }


  public static int extractReferencedPaperIds(OEntity reference)
  {
    return EntityHelper.<Integer> extractProperty(reference, "DstID");
  }


  public static Set<Integer> extractCitingPaperIds(Enumerable<OEntity> citations)
  {
    return EntityHelper.<Integer> extractProperty(citations, "SrcID");
  }


  public static int extractCitingPaperIds(OEntity citation)
  {
    return EntityHelper.<Integer> extractProperty(citation, "SrcID");
  }
}
