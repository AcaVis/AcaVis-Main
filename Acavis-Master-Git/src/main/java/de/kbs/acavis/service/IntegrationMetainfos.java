/*****************************************
 * This file is part of AcaVis, a tool for research of scientific
 * literature using the benefits of visualizations.
 * Copyright (C) 2014 - Sebastian Holzki
 * 
 * AcaVis is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License Version 2
 * as published by the Free Software Foundation;
 *****************************************/
package de.kbs.acavis.service;

import java.util.LinkedHashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.reflections.Reflections;

import de.kbs.acavis.integration.DefaultInfrastructure;
import de.kbs.acavis.integration.IntegratedInfrastructure;


public class IntegrationMetainfos
{
  /**
   * The package containing the implementations of the integration-layer.
   */
  public static final String INTEGRATION_CONTROLLER = "de.kbs.acavis.integration.controller";
  public static final String INTEGRATION_CONTROLLER_PREFIX = INTEGRATION_CONTROLLER + ".";


  /**
   * Returns all integrations regardless of what interfaces they fulfill.
   * 
   * @return A set of integrations
   */
  public static Set<Class<?>> getIntegrations()
  {
    Reflections reflections = new Reflections(INTEGRATION_CONTROLLER);

    return reflections.getTypesAnnotatedWith(IntegratedInfrastructure.class);
  }


  /**
   * Returns the default integration (annotated with {@Link DefaultInfrastructure}).<br />
   * <b>Caution!</b> If multiple integration-providers are annotated as default, this function does NOT show deterministic behavior! (Due to
   * a {@link Set} used internally)
   * 
   * @return The class-reflection of the default integration
   * @throws InvalidIntegrationException
   *           If none of the integrations is annotated as default.
   */
  public static Class<?> getDefaultIntegration()
    throws InvalidIntegrationException
  {
    Reflections reflections = new Reflections(INTEGRATION_CONTROLLER);

    try
    {
      return reflections.getTypesAnnotatedWith(DefaultInfrastructure.class).iterator().next();
    }
    catch (NoSuchElementException e)
    {
      throw new InvalidIntegrationException("");
    }
  }


  /**
   * Returns the names of all integrations regardless of what interfaces they fulfill.
   * 
   * @return A sorted set of integration-names
   */
  public static SortedSet<String> getIntegratedServiceNames()
  {
    TreeSet<String> names = new TreeSet<String>();

    for (Class<?> integration : getIntegrations())
      names.add(integration.getAnnotation(IntegratedInfrastructure.class).name());

    return names;
  }


  /**
   * Returns integrations that are annotated as {@link IntegratedInfrastructure} and implement the given interface.
   * 
   * @param theInterface
   *          The class of an interface that has to be implemented by every class in the returned set.
   * @return A set containing classes that are properly annotated and do implement the given interface.
   */
  public static <T> Set<Class<T>> getIntegrationsByProvider(Class<T> theInterface)
  {
    LinkedHashSet<Class<T>> providingIntegrations = new LinkedHashSet<Class<T>>();

    Reflections reflections = new Reflections(INTEGRATION_CONTROLLER);
    Set<Class<? extends T>> providers = reflections.getSubTypesOf(theInterface);

    for (Class<? extends T> provider : providers)
    {
      // Providers must be annotated as IntegratedInfrastructure by convention
      if (!provider.isAnnotationPresent(IntegratedInfrastructure.class))
        continue;

      @SuppressWarnings("unchecked")
      Class<T> superclassProvider = (Class<T>) provider;

      providingIntegrations.add(superclassProvider);
    }

    return providingIntegrations;
  }


  /**
   * Determines if a class identified by the given package/classname input-string implements the given interface. This is useful to filter
   * invalid input from userfields on the webpage.<br />
   * <br />
   * <b>Caution! False is returned, if the class is not located in the package specified by {@link #INTEGRATION_CONTROLLER}!</b>
   * 
   * @param providerClassName
   *          Qualified package/classname for a class to test
   * @param theInterface
   *          An interface to test for implementation
   * @return True, if the given class exists and fulfills the given interface. False otherwise. False is also returned, if the class is not
   *         located within the the package specified by {@link #INTEGRATION_CONTROLLER}!
   */
  public static boolean isIntegratingProvider(String providerClassName, Class<?> theInterface)
  {
    if (!providerClassName.startsWith(INTEGRATION_CONTROLLER))
      return false;

    try
    {
      return theInterface.isAssignableFrom(Class.forName(providerClassName));
    }
    catch (ClassNotFoundException e)
    {
      // If the class doesn't exist, it can't be a subclass of the given interface
      return false;
    }
  }
}
