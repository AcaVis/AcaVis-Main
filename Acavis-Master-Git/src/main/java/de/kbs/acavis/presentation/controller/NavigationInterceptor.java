/*****************************************
 * This file is part of AcaVis, a tool for research of scientific
 * literature using the benefits of visualizations.
 * Copyright (C) 2014 - Sebastian Holzki
 * 
 * AcaVis is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License Version 2
 * as published by the Free Software Foundation;
 *****************************************/
package de.kbs.acavis.presentation.controller;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import de.kbs.acavis.service.controller.ApplicationContextProvider;
import de.kbs.acavis.service.controller.CollectionManager;


/**
 * Contains a post-controller hook that adds necessary data to the page-model.
 * 
 * @author Sebastian
 */
public class NavigationInterceptor
  extends HandlerInterceptorAdapter
{
  /**
   * Indicates that information about the navigation is not necessary for the view associated with this controller.<br />
   * For example if it's a JSON-output.
   * 
   * @author Sebastian
   */
  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.METHOD)
  public @interface WithoutNavigation
  {}


  /**
   * Adds existing collections to the page-model if controller-method is not annotated {@link WithoutNavigation}.
   */
  @Override
  public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
      ModelAndView modelAndView)
    throws Exception
  {
    // The DAO has to be loaded locally because this class is instantiated as a bean too. This makes it impossible to access a complete
    // application-context on instantiation.
    CollectionManager collectionManager =
      ApplicationContextProvider.getApplicationContext().getBean(CollectionManager.class);

    boolean withoutNavigation = false;

    // If controller is annotated, don't add navigation-stuff to model
    if (handler != null && handler instanceof HandlerMethod)
    {
      HandlerMethod x = (HandlerMethod) handler;
      withoutNavigation = x.getMethod().isAnnotationPresent(WithoutNavigation.class);
    }

    if (!withoutNavigation)
      request.setAttribute("existing_collections", collectionManager.getCollections());
  }
}
