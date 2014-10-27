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

import org.odata4j.consumer.ODataConsumer;
import org.odata4j.consumer.ODataConsumers;
import org.odata4j.format.FormatType;


public class MASOdataConnector
{
  public static String odataBaseURL = "https://api.datamarket.azure.com/MRC/MicrosoftAcademic/v2/";
  public static ODataConsumer consumer = null;


  private static void initializeOdataService()
  {
    consumer = ODataConsumers.newBuilder(odataBaseURL).setFormatType(FormatType.JSON).build();
  }


  public static ODataConsumer create(String odataBaseUrl)
  {
    MASOdataConnector.odataBaseURL = odataBaseUrl;

    initializeOdataService();

    return consumer;
  }


  public static ODataConsumer create()
  {
    initializeOdataService();

    return consumer;
  }
}
