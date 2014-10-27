/*****************************************
 * This file is part of AcaVis, a tool for research of scientific
 * literature using the benefits of visualizations.
 * Copyright (C) 2014 - Sebastian Holzki
 * 
 * AcaVis is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License Version 2
 * as published by the Free Software Foundation;
 *****************************************/
package de.kbs.acavis.mas;

import java.util.HashSet;

import org.core4j.Enumerable;
import org.odata4j.core.OEntity;

import de.kbs.acavis.integration.mas.odata.EntityHelper;
import de.kbs.acavis.integration.mas.odata.MASEntityHelper;
import de.kbs.acavis.integration.mas.odata.MASOdataConnector;
import de.kbs.acavis.integration.mas.odata.MASRelationHelper;
import de.kbs.acavis.integration.mas.odata.RelationHelper;


public class LargePaperReferenceTest
{
  public static void main(String[] args)
  {
    MASOdataConnector.create();
    MASEntityHelper eh = new MASEntityHelper(MASOdataConnector.consumer);
    MASRelationHelper rh = new MASRelationHelper(MASOdataConnector.consumer);

    // Tufte quantitative
    int paperId = 1258503;

    Enumerable<OEntity> references = rh.getPaperReferences(paperId);
    Enumerable<OEntity> citations = rh.getPaperCitations(paperId);

    System.out.println("Refcount: " + references.count());
    System.out.println("Citcount: " + citations.count());

    HashSet<Integer> ids = new HashSet<Integer>(1 + references.count() + citations.count());
    ids.add(paperId);
    ids.addAll(RelationHelper.extractReferencedPaperIds(references));
    ids.addAll(RelationHelper.extractCitingPaperIds(citations));

    System.out.println("papers (theoretically): " + ids.size());

    Enumerable<OEntity> papers = eh.getPapers(ids);

    System.out.println("papers: " + papers.count());

    for (OEntity paper : papers)
    {
      int id = EntityHelper.<Integer> extractProperty(paper, "ID");
      String title = EntityHelper.<String> extractProperty(paper, "Title");
      System.out.println(id + ": " + title);
    }

  }
}
