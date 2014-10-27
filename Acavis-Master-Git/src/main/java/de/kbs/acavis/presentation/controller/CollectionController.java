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

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.mcavallo.opencloud.Tag;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import de.kbs.acavis.integration.IdentifierException;
import de.kbs.acavis.integration.IntegrationUnavailableException;
import de.kbs.acavis.integration.UnimplementedFeatureException;
import de.kbs.acavis.integration.model.PublicationIdentifier;
import de.kbs.acavis.presentation.controller.NavigationInterceptor.WithoutNavigation;
import de.kbs.acavis.service.InvalidIntegrationException;
import de.kbs.acavis.service.SearchOptions;
import de.kbs.acavis.service.SerializationHelper;
import de.kbs.acavis.service.controller.ApplicationContextProvider;
import de.kbs.acavis.service.controller.CollectionManager;
import de.kbs.acavis.service.controller.EntityNotFoundException;
import de.kbs.acavis.service.model.persistence.CollectionEntity;
import de.kbs.acavis.service.models.visualization.CoAuthorshipNetwork;
import de.kbs.acavis.service.models.visualization.PublicationCitationNetwork;
import de.kbs.acavis.service.models.visualization.YearlyDistribution;
import de.kbs.acavis.service.models.visualization.YearlyDistributionEntry;


@Controller
public class CollectionController
{
  // private static final Logger logger = LoggerFactory.getLogger(CollectionController.class);

  private CollectionManager collectionManager = ApplicationContextProvider.getApplicationContext().getBean(
      CollectionManager.class);


  @RequestMapping("/collections")
  public String overview(Model pageVariables)
  {
    pageVariables.addAttribute("section", "collection");

    return "collection";
  }


  @RequestMapping("/collections/{collectionId}")
  public String view(Model pageVariables, @PathVariable long collectionId)
    throws JsonException
  {
    pageVariables.addAttribute("section", "collection");
    pageVariables.addAttribute("current_collection", collectionId);

    try
    {
      CollectionEntity collection = collectionManager.getCollection(collectionId);

      List<Tag> tagCloud = collectionManager.collectionTagcloud(collection, 25, 2.0);

      pageVariables.addAttribute("collection", collection);
      pageVariables.addAttribute("tagcloud", tagCloud);
    }
    catch (EntityNotFoundException e)
    {
      showAlert(pageVariables, "error", "Collection not found!", "A collection using the id <" + collectionId
          + "> couldn't be found!");
    }

    return "collections/collection";
  }


  @RequestMapping(value = "/collections/addto", method = RequestMethod.POST)
  @ResponseBody
  @WithoutNavigation
  public String addPublication(@RequestParam(value = "collection", required = false) Long collectionId,
      @RequestParam(value = "newcollection", required = false, defaultValue = "false") boolean newCollection,
      @RequestParam(value = "newcollection_name", required = false) String newCollectionName,
      @RequestParam(value = "publications[]") String[] serializedIdentifiers,
      @RequestParam(value = "integration") String integrationName)
    throws JsonException
  {
    // Variables have to be checked manually because they are not required in every case
    if (!newCollection && collectionId == null)
      throw new JsonException("Publications not added!", "Invalid collection selected.");
    if (newCollection && newCollectionName == null)
      throw new JsonException("Collection couldn't be created!", "Collection-name was empty.");
    if (newCollection && newCollectionName.length() < 3)
      throw new JsonException("Collection couldn't be created!", "Collection-name must be longer than 3 characters.");
    if (serializedIdentifiers == null || serializedIdentifiers.length == 0)
      throw new JsonException("No publications to add!", "At least one publication must be selected to add.");

    // Use new collection, if one
    if (newCollection)
      collectionId = collectionManager.addCollection(newCollectionName);

    // Deserialize publication-identifiers
    List<PublicationIdentifier> identifiers = new LinkedList<PublicationIdentifier>();
    for (String serailizedIdentifier : serializedIdentifiers)
    {
      try
      {
        identifiers.add(SerializationHelper.deserializePublicationIdentifierBase64(serailizedIdentifier));
      }
      catch (ClassNotFoundException | IOException e)
      {}
    }

    // We can now safely add the publications, but first we need to get in contact with a provider
    try
    {
      collectionManager.addPublicationsToCollection(collectionId, integrationName, identifiers);
    }
    catch (InvalidIntegrationException e)
    {
      throw new JsonException("Invalid data-source!", e.getMessage());
    }
    catch (UnimplementedFeatureException e)
    {
      throw new JsonException("Insufficient data-source!", e.getMessage());
    }
    catch (IntegrationUnavailableException e)
    {
      throw new JsonException("Data-source unavailable!", e.getMessage());
    }
    catch (EntityNotFoundException e)
    {
      throw new JsonException("Publications not added!", "Invalid collection selected.");
    }

    return "";
  }


  @RequestMapping(value = "/collections/removefrom", method = RequestMethod.POST)
  @ResponseBody
  @WithoutNavigation
  public String removePublications(@RequestParam(value = "collection") Long collectionId,
      @RequestParam(value = "collectionitems[]") Long[] itemIds)
    throws JsonException
  {
    try
    {
      collectionManager.getCollection(collectionId);
    }
    catch (EntityNotFoundException e)
    {
      throw new JsonException("Publications not remove!", "Invalid collection specified.");
    }

    // TODO Json publications remove by item id

    return "";
  }


  @RequestMapping(value = "/collections/create", method = RequestMethod.POST)
  public String create(Model pageVariables)
  {
    // TODO <create collection> implementation, is it a page or ajax? Relevant for withoutNavigation
    return "";
  }


  @RequestMapping(value = "/collections/remove", method = RequestMethod.POST)
  public String remove(Model pageVariables)
  {
    // TODO <remove collection> implementation, is it a page or ajax? Relevant for withoutNavigation
    return "";
  }


  @RequestMapping(value = "/collections/edit", method = RequestMethod.POST)
  public String edit(Model pageVariables)
  {
    // TODO <edit collection> implementation, is it a page or ajax? Relevant for withoutNavigation
    return "";
  }


  @RequestMapping("/collections/{collectionId}/distribution")
  @WithoutNavigation
  public String collectionDistribution(Model pageVariables, @PathVariable long collectionId)
    throws PageSnippetException
  {
    try
    {
      CollectionEntity collection = collectionManager.getCollection(collectionId);

      pageVariables.addAttribute("collection", collection);
      pageVariables.addAttribute("collectionid", collectionId);
    }
    catch (EntityNotFoundException e)
    {
      throw new PageSnippetException("A collection with the id <" + collectionId + "> couldn't be found!");
    }

    return "collections/distribution";
  }


  @RequestMapping("/collections/{collectionId}/distributionData")
  @ResponseBody
  @WithoutNavigation
  public Collection<YearlyDistributionEntry> collectionDistributionData(@PathVariable long collectionId,
      HttpSession session)
    throws JsonException
  {
    try
    {
      YearlyDistribution distribution = collectionManager.statsOfDistribution(collectionId);

      return distribution.getDistribution();
    }
    catch (EntityNotFoundException e)
    {
      throw new JsonException("Collection not found!", "A collection with the id <" + collectionId
          + "> couldn't be found.");
    }
  }


  @RequestMapping("/collections/{collectionId}/citations")
  @WithoutNavigation
  public String collectionCitations(Model pageVariables, @PathVariable long collectionId)
    throws PageSnippetException
  {
    try
    {
      CollectionEntity collection = collectionManager.getCollection(collectionId);

      pageVariables.addAttribute("collection", collection);
      pageVariables.addAttribute("collectionid", collectionId);
    }
    catch (EntityNotFoundException e)
    {
      throw new PageSnippetException("A collection with the id <" + collectionId + "> couldn't be found!");
    }

    return "collections/citations";
  }


  @RequestMapping("/collections/{collectionId}/citationData")
  @ResponseBody
  @WithoutNavigation
  public PublicationCitationNetwork collectionCitationData(@PathVariable long collectionId, HttpSession session)
    throws JsonException
  {
    SearchOptions options = new SearchOptions(session, true);

    try
    {
      PublicationCitationNetwork network =
        collectionManager.collectionCitations(collectionId, options.getIntegration());
      network.prepareForJsonOutput();

      return network;
    }
    catch (EntityNotFoundException e)
    {
      throw new JsonException("Collection not found!", "A collection with the id <" + collectionId
          + "> couldn't be found.");
    }
    catch (InvalidIntegrationException e)
    {
      throw new JsonException("Invalid data-source!", e.getMessage());
    }
    catch (UnimplementedFeatureException e)
    {
      throw new JsonException("Insufficient data-source!", e.getMessage());
    }
    catch (IntegrationUnavailableException e)
    {
      throw new JsonException("Data-source unavailable!", e.getMessage());
    }
    catch (IdentifierException e)
    {
      throw new JsonException("Error is stored data!", e.getMessage());
    }
  }


  @RequestMapping("/collections/{collectionId}/coAuthors")
  @WithoutNavigation
  public String collectionCoAuthors(Model pageVariables, @PathVariable long collectionId)
    throws PageSnippetException
  {
    try
    {
      CollectionEntity collection = collectionManager.getCollection(collectionId);

      pageVariables.addAttribute("collection", collection);
      pageVariables.addAttribute("collectionid", collectionId);
    }
    catch (EntityNotFoundException e)
    {
      throw new PageSnippetException("A collection with the id <" + collectionId + "> couldn't be found!");
    }

    return "collections/coauthors";
  }


  @RequestMapping("/collections/{collectionId}/coAuthorData")
  @ResponseBody
  @WithoutNavigation
  public CoAuthorshipNetwork collectionCoAuthorData(@PathVariable long collectionId, HttpSession session)
    throws JsonException
  {
    SearchOptions options = new SearchOptions(session, true);

    try
    {
      CoAuthorshipNetwork network = collectionManager.collectionCoAuthors(collectionId, options.getIntegration());
      network.prepareForJsonOutput();

      return network;
    }
    catch (EntityNotFoundException e)
    {
      throw new JsonException("Collection not found!", "A collection with the id <" + collectionId
          + "> couldn't be found.");
    }
    catch (InvalidIntegrationException e)
    {
      throw new JsonException("Invalid data-source!", e.getMessage());
    }
    catch (UnimplementedFeatureException e)
    {
      throw new JsonException("Insufficient data-source!", e.getMessage());
    }
    catch (IntegrationUnavailableException e)
    {
      throw new JsonException("Data-source unavailable!", e.getMessage());
    }
    catch (IdentifierException e)
    {
      throw new JsonException("Error is stored data!", e.getMessage());
    }
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
   * Shorthand function to create an alert-box.
   * 
   * @param pageVariables
   *          The page-model
   * @param level
   *          The level of error, either 'warning' or 'error'. Accessible in the model through 'alert_level'.
   * @param majorText
   *          The bold printed text, should be as short as possible. Accessible in the model through 'alert_majortext'.
   * @param minorText
   *          A normal printed message, may contain explanations or suggestions. Accessible in the model through 'alert_minortext'.
   */
  private void showAlert(Model pageVariables, String level, String majorText, String minorText)
  {
    if (!level.equals("error") && !level.equals("warning"))
      return;

    pageVariables.addAttribute("alert_level", level);
    pageVariables.addAttribute("alert_majortext", majorText);
    pageVariables.addAttribute("alert_minortext", minorText);
  }
}
