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

public class ComplexObject
{
  public int a = 0;
  public int b = 0;


  public ComplexObject(int a, int b)
  {
    this.a = a;
    this.b = b;
  }


  public boolean equals(Object an)
  {
    if (an == this)
      return true;

    if (an == null || !(an instanceof ComplexObject))
      return false;

    ComplexObject c = (ComplexObject) an;

    return a == c.a && b == c.b;
  }


  public int hashCode()
  {
    return a * b;
  }


  public String toString()
  {
    return "a: " + a + ", b: " + b + "; ";
  }
}
