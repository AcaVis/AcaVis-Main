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

import java.util.Arrays;
import java.util.HashSet;


public class HashSetTest
{

  public static void main(String[] args)
  {
    HashSet<ComplexObject> h = new HashSet<ComplexObject>();

    h.add(new ComplexObject(1, 2));
    h.add(new ComplexObject(2, 2));
    h.add(new ComplexObject(1, 2));

    System.out.println(Arrays.toString(h.toArray()));
  }
}
