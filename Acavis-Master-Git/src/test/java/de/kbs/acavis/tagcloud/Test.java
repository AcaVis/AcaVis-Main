/*****************************************
 * This file is part of AcaVis, a tool for research of scientific
 * literature using the benefits of visualizations.
 * Copyright (C) 2014 - Sebastian Holzki
 * 
 * AcaVis is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License Version 2
 * as published by the Free Software Foundation;
 *****************************************/
package de.kbs.acavis.tagcloud;

import java.io.IOException;

import org.mcavallo.opencloud.Cloud;
import org.mcavallo.opencloud.Cloud.Case;
import org.mcavallo.opencloud.Tag;


public class Test
{
  public static void main(String[] args)
    throws IOException
  {
    Cloud c = new Cloud();
    c.setTagCase(Case.PRESERVE_CASE);
    c.setMaxTagsToDisplay(20);

    testPublicationTitles(c);

    for (Tag t : c.tags())
    {
      System.out.println(t.getName() + ": " + t.getScoreInt());
    }
  }


  public static void testGeneral(Cloud c)
  {
    c.addTag("bla");
    c.addTag("bla");
    c.addTag("blubb");
  }


  public static void testPublicationTitles(Cloud c)
  {
    String title = "Life cycle of<i>Pseudodiamesa branickii</i> (Chironomidae) in a small upland stream";

    c.addText(title);

    c.addTag("life");
  }
}
