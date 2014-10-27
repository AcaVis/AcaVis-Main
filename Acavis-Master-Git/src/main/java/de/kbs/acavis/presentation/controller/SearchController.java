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

import java.util.Calendar;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.HtmlUtils;

import de.kbs.acavis.integration.IntegratedInfrastructure;
import de.kbs.acavis.integration.IntegrationUnavailableException;
import de.kbs.acavis.integration.PublicationProvider;
import de.kbs.acavis.integration.UnimplementedFeatureException;
import de.kbs.acavis.presentation.controller.NavigationInterceptor.WithoutNavigation;
import de.kbs.acavis.presentation.model.IntegrationOption;
import de.kbs.acavis.service.IntegrationMetainfos;
import de.kbs.acavis.service.InvalidIntegrationException;
import de.kbs.acavis.service.SearchOptions;
import de.kbs.acavis.service.controller.ApplicationContextProvider;
import de.kbs.acavis.service.controller.EntityNotFoundException;
import de.kbs.acavis.service.controller.SearchManager;
import de.kbs.acavis.service.models.visualization.YearlyDistributionEntry;


@Controller
public class SearchController
{
  // private static final Logger logger = LoggerFactory.getLogger(SearchController.class);

  private SearchManager searchManager = ApplicationContextProvider.getApplicationContext().getBean(
      SearchManager.class);


  @RequestMapping(value = { "/", "/search" })
  public String landingForm(@RequestParam(value = "query", defaultValue = "", required = false) String searchQuery,
      @RequestParam(value = "send", defaultValue = "false", required = false) boolean performedSearch,
      Model pageVariables, HttpSession session, RedirectAttributes ra)
  {
    SearchOptions options = new SearchOptions(session, true);
    int thisYear = Calendar.getInstance().get(Calendar.YEAR);

    pageVariables.addAttribute("thisyear", thisYear);
    pageVariables.addAttribute("section", "search");
    pageVariables.addAttribute("query", HtmlUtils.htmlEscape(searchQuery));
    populateSearchOptionsToModel(pageVariables, options);

    integrationSelection(pageVariables);

    // Just show the plain form
    if (!performedSearch)
      return "search/landing-form";

    // Check for crappy inputs before executing a search
    if (searchQuery.isEmpty())
    {
      showSearchAlert(pageVariables, "warning", "Empty search-input!",
          "No search-input was specified. Please enter any searchterms and try again.");
    }
    else if (searchQuery.length() < 4)
    {
      showSearchAlert(pageVariables, "warning", "The minimum-length of the search-input is 4 characters!",
          "Please change your search-input and try again.");
    }

    // Execute the search if everything is fine
    else
    {
      try
      {
        long searchId = 0;

        if (options.getSearchfields().equals("author"))
          searchId = searchManager.searchByPublication(options.getIntegration(), searchQuery, options.getLimit());
        else
          searchId = searchManager.searchByPublication(options.getIntegration(), searchQuery, options.getLimit());

        if (!searchQuery.isEmpty())
          return "redirect:search/results/" + searchId;
      }

      // Handle the bunch of exceptions that are possible in this context
      catch (InvalidIntegrationException e)
      {
        showSearchAlert(pageVariables, "error", "The specified data-source doesn't exist!",
            "Please check the search-options and select an integration that is listed.");
      }
      catch (UnimplementedFeatureException e)
      {
        showSearchAlert(pageVariables, "error", "This kind of search is not available for the specified data-source!",
            "Please check the search-options and choose another integration.");
      }
      catch (IntegrationUnavailableException e)
      {
        showSearchAlert(pageVariables, "error",
            "The external data-source is not available! Unfortunately there is nothing you can do about it, sorry.",
            e.getMessage());
      }
    }

    return "search/landing-form";
  }


  @RequestMapping(value = "/search/results/{searchId}", method = RequestMethod.GET)
  public String results(HttpSession session, Model pageVariables, @PathVariable long searchId)
  {
    SearchOptions options = new SearchOptions(session, true);

    pageVariables.addAttribute("section", "search");
    populateSearchOptionsToModel(pageVariables, options);
    publicationIntegrationSelection(pageVariables);

    integrationSelection(pageVariables);

    try
    {
      pageVariables.addAttribute("search", searchManager.getSearch(searchId));
    }
    catch (EntityNotFoundException e)
    {
      showSearchAlert(pageVariables, "error", "The search and its search-result are not available anymore!",
          "Please perform a new search using the form above.");
    }

    return "search/results";
  }


  @RequestMapping("/search/options")
  @ResponseBody
  @WithoutNavigation
  public String options(HttpSession session, @RequestParam("searchfields") String searchfields,
      @RequestParam("integration") String integration,
      @RequestParam(value = "earliest", required = false, defaultValue = "0") int earliest,
      @RequestParam(value = "ignore_earliest", required = false, defaultValue = "false") boolean ignoreEarliest,
      @RequestParam(value = "latest", required = false, defaultValue = "0") int latest,
      @RequestParam(value = "ignore_latest", required = false, defaultValue = "false") boolean ignoreLatest,
      @RequestParam("limit") int limit)
  {
    SearchOptions options = new SearchOptions(session, true);

    options.setSearchfields(searchfields);
    options.setIntegration(integration);

    // 0 identifies invalid years
    if (earliest != 0)
      options.setEarliest(earliest);
    if (latest != 0)
      options.setLatest(latest);

    options.setIgnoreEarliest(ignoreEarliest);
    options.setIgnoreLatest(ignoreLatest);
    options.setLimit(limit);

    options.storeToSession();

    return "";
  }


  @RequestMapping("/search/history")
  public String history(Model pageVariables)
  {
    pageVariables.addAttribute("section", "search");
    pageVariables.addAttribute("searches", searchManager.searchHistory());

    return "search/history";
  }


  @RequestMapping("/search/results/{searchId}/distribution")
  @WithoutNavigation
  public String distribution(Model pageVariables, @PathVariable long searchId)
    throws PageSnippetException
  {
    pageVariables.addAttribute("searchid", searchId);

    try
    {
      pageVariables.addAttribute("search", searchManager.getSearch(searchId));
    }
    catch (EntityNotFoundException e)
    {
      throw new PageSnippetException("A search with the id <" + searchId + "> couldn't be found!");
    }

    return "search/distribution";
  }


  @RequestMapping("/search/results/{searchId}/distributionData")
  @ResponseBody
  @WithoutNavigation
  public Collection<YearlyDistributionEntry> distributionData(@PathVariable long searchId)
    throws JsonException
  {
    try
    {
      return searchManager.statsOfDistribution(searchId).getDistribution();
    }
    catch (EntityNotFoundException e)
    {
      throw new JsonException("Search not found!", "A search with the id <" + searchId + "> couldn't be found.");
    }
  }


  /**
   * Handles exceptions that need to be transferred as JSON.
   * 
   * @param exception
   *          The message-container of type {@link JsonException}
   * @return The exception-object, the transformation to JSON-string is done by spring through {@link ResponseBody}-annotation
   */
  @ExceptionHandler(JsonException.class)
  @ResponseBody
  public JsonException handleJsonException(JsonException exception)
  {
    return exception;
  }


  /**
   * Handles exceptions that need to be transferred as a part of the page (page-snippet).
   * 
   * @param exception
   *          The message-container of type {@link PageSnippetException}
   * @param pageVariables
   *          The view-model, supplied by spring
   * @return A snippet containing the error-message
   */
  @ExceptionHandler(PageSnippetException.class)
  public String handlePageSnippetException(PageSnippetException exception, Model pageVariables)
  {
    pageVariables.addAttribute("message", exception.getMessage());

    return "snippet-error";
  }


  /**
   * Registers all the search-options with the given page-model
   * 
   * @param pageVariables
   *          The page-model
   * @param options
   *          The container-object of the search-options
   */
  private void populateSearchOptionsToModel(Model pageVariables, SearchOptions options)
  {
    pageVariables.addAttribute(SearchOptions.FIELD_SEARCHOPTION_SEARCHFIELDS, options.getSearchfields());
    pageVariables.addAttribute(SearchOptions.FIELD_SEARCHOPTION_INTEGRATION, options.getIntegration());
    pageVariables.addAttribute(SearchOptions.FIELD_SEARCHOPTION_EARLIEST, options.getEarliest());
    pageVariables.addAttribute(SearchOptions.FIELD_SEARCHOPTION_LATEST, options.getLatest());
    pageVariables.addAttribute(SearchOptions.FIELD_SEARCHOPTION_IGNEARLIEST, options.isIgnoreEarliest());
    pageVariables.addAttribute(SearchOptions.FIELD_SEARCHOPTION_IGNLATEST, options.isIgnoreLatest());
    pageVariables.addAttribute(SearchOptions.FIELD_SEARCHOPTION_LIMIT, options.getLimit());
  }


  /**
   * Prepares the data set for integration-selection and adds the List-container to the page-model
   * 
   * @param pageVariables
   *          The page-model
   */
  private void integrationSelection(Model pageVariables)
  {
    List<IntegrationOption> integrations = new LinkedList<IntegrationOption>();

    for (Class<?> integration : IntegrationMetainfos.getIntegrations())
      integrations.add(new IntegrationOption(integration.getName(), integration.getAnnotation(
          IntegratedInfrastructure.class).name()));

    pageVariables.addAttribute("integrations", integrations);
  }


  /**
   * Prepares the data set for integration-selection and adds the List-container to the page-model.<br />
   * Only adds providers that support the interface {@link PublicationProvider}.
   * 
   * @param pageVariables
   *          The page-model
   */
  private void publicationIntegrationSelection(Model pageVariables)
  {
    List<IntegrationOption> integrations = new LinkedList<IntegrationOption>();

    for (Class<?> integration : IntegrationMetainfos.getIntegrationsByProvider(PublicationProvider.class))
      integrations.add(new IntegrationOption(integration.getName(), integration.getAnnotation(
          IntegratedInfrastructure.class).name()));

    pageVariables.addAttribute("publication_integrations", integrations);
  }


  /**
   * Shorthand function to create an alert-box below the search-form.
   * 
   * @param pageVariables
   *          The page-model
   * @param level
   *          The level of error, either 'warning' or 'error'. Accessible in the model thorugh 'alert_level'.
   * @param majorText
   *          The bold printed text, should be as short as possible. Accessible in the model through 'alert_majortext'.
   * @param minorText
   *          A normal printed message, may contain explanations or suggestions. Accessible in the model through 'alert_minortext'.
   */
  private void showSearchAlert(Model pageVariables, String level, String majorText, String minorText)
  {
    if (!level.equals("error") && !level.equals("warning"))
      return;

    pageVariables.addAttribute("alert_level", level);
    pageVariables.addAttribute("alert_majortext", majorText);
    pageVariables.addAttribute("alert_minortext", minorText);
  }
}
