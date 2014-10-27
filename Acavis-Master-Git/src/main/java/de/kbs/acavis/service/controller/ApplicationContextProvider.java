/*****************************************
 * This file is part of AcaVis, a tool for research of scientific
 * literature using the benefits of visualizations.
 * Copyright (C) 2014 - Sebastian Holzki
 * 
 * AcaVis is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License Version 2
 * as published by the Free Software Foundation;
 *****************************************/
package de.kbs.acavis.service.controller;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;


/**
 * This class is a simple provider for the current {@link ApplicationContext} created by spring-framework.<br />
 * The context is needed for accessing spring-created beans.
 * 
 * @author Sebastian
 *
 */
public class ApplicationContextProvider
  implements ApplicationContextAware
{
  private static ApplicationContext ctx = null;


  public static ApplicationContext getApplicationContext()
  {
    return ctx;
  }


  @Override
  public void setApplicationContext(ApplicationContext ctx)
    throws BeansException
  {
    ApplicationContextProvider.ctx = ctx;
  }
}
