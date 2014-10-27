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

import java.util.Calendar;

import javax.servlet.http.HttpSession;

import org.springframework.web.util.HtmlUtils;

import de.kbs.acavis.integration.controller.MicrosoftAcademicIntegration;


public class SearchOptions
{
  public static final String FIELD_SEARCHOPTION_SEARCHFIELDS = "searchfields";
  public static final String FIELD_SEARCHOPTION_INTEGRATION = "integration";
  public static final String FIELD_SEARCHOPTION_EARLIEST = "earliest";
  public static final String FIELD_SEARCHOPTION_LATEST = "latest";
  public static final String FIELD_SEARCHOPTION_IGNEARLIEST = "ignore_earliest";
  public static final String FIELD_SEARCHOPTION_IGNLATEST = "ignore_latest";
  public static final String FIELD_SEARCHOPTION_LIMIT = "limit";

  private HttpSession session = null;

  private int thisYear = Calendar.getInstance().get(Calendar.YEAR);

  private String searchfields = "";
  private String integration = "";
  private int earliest = 0;
  private int latest = 0;
  private boolean ignoreEarliest = false;
  private boolean ignoreLatest = false;
  private int limit = 1;


  public SearchOptions(HttpSession session, boolean loadSession)
  {
    this.session = session;
    loadDefault();

    if (loadSession)
      loadFromSession();
  }


  public SearchOptions(HttpSession session)
  {
    this(session, false);
  }


  public void loadDefault()
  {
    // Maybe load those from a config-file
    setSearchfields("publication");
    setIntegration(MicrosoftAcademicIntegration.class.getName());
    setEarliest(thisYear - 10);
    setLatest(thisYear);
    setIgnoreEarliest(true);
    setIgnoreLatest(true);
    setLimit(25);
  }


  public void loadFromSession()
  {
    setSearchfields(this.<String> fromSession(FIELD_SEARCHOPTION_SEARCHFIELDS, searchfields));
    setIntegration(this.<String> fromSession(FIELD_SEARCHOPTION_INTEGRATION, integration));
    setEarliest(this.<Integer> fromSession(FIELD_SEARCHOPTION_EARLIEST, earliest));
    setLatest(this.<Integer> fromSession(FIELD_SEARCHOPTION_LATEST, latest));
    setIgnoreEarliest(this.<Boolean> fromSession(FIELD_SEARCHOPTION_IGNEARLIEST, ignoreEarliest));
    setIgnoreLatest(this.<Boolean> fromSession(FIELD_SEARCHOPTION_IGNLATEST, ignoreLatest));
    setLimit(this.<Integer> fromSession(FIELD_SEARCHOPTION_LIMIT, limit));
  }


  public void storeToSession()
  {
    session.setAttribute(FIELD_SEARCHOPTION_SEARCHFIELDS, searchfields);
    session.setAttribute(FIELD_SEARCHOPTION_INTEGRATION, integration);
    session.setAttribute(FIELD_SEARCHOPTION_EARLIEST, earliest);
    session.setAttribute(FIELD_SEARCHOPTION_LATEST, latest);
    session.setAttribute(FIELD_SEARCHOPTION_IGNEARLIEST, ignoreEarliest);
    session.setAttribute(FIELD_SEARCHOPTION_IGNLATEST, ignoreLatest);
    session.setAttribute(FIELD_SEARCHOPTION_LIMIT, limit);
  }


  public String getSearchfields()
  {
    return searchfields;
  }


  public void setSearchfields(String searchfields)
  {
    this.searchfields = HtmlUtils.htmlEscape(searchfields);
  }


  public String getIntegration()
  {
    return integration;
  }


  public void setIntegration(String integration)
  {
    this.integration = HtmlUtils.htmlEscape(integration);
  }


  public int getEarliest()
  {
    return earliest;
  }


  public void setEarliest(int earliest)
  {
    this.earliest = Math.max(1, earliest);
  }


  public int getLatest()
  {
    return latest;
  }


  public void setLatest(int latest)
  {
    this.latest = Math.min(thisYear, latest);
  }


  public boolean isIgnoreEarliest()
  {
    return ignoreEarliest;
  }


  public void setIgnoreEarliest(boolean ignoreEarliest)
  {
    this.ignoreEarliest = ignoreEarliest;
  }


  public boolean isIgnoreLatest()
  {
    return ignoreLatest;
  }


  public void setIgnoreLatest(boolean ignoreLatest)
  {
    this.ignoreLatest = ignoreLatest;
  }


  public int getLimit()
  {
    return limit;
  }


  public void setLimit(int limit)
  {
    this.limit = Math.min(250, Math.max(1, limit));
  }


  @SuppressWarnings("unchecked")
  private <T> T fromSession(String name, T fallback)
  {
    T value;

    try
    {
      value = (session.getAttribute(name) == null) ? fallback : (T) session.getAttribute(name);
    }
    catch (ClassCastException e)
    {
      value = fallback;

      // To correct changes of datatypes and so on
      session.setAttribute(name, fallback);
    }

    return value;
  }

}
