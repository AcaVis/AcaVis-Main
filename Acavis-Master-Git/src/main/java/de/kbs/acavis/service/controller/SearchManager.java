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

import java.util.LinkedHashSet;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import de.kbs.acavis.integration.IntegrationUnavailableException;
import de.kbs.acavis.integration.PublicationProvider;
import de.kbs.acavis.integration.UnimplementedFeatureException;
import de.kbs.acavis.integration.model.PublicationSearchData;
import de.kbs.acavis.service.IntegrationMetainfos;
import de.kbs.acavis.service.IntegrationProviderFactory;
import de.kbs.acavis.service.InvalidIntegrationException;
import de.kbs.acavis.service.model.persistence.SearchEntity;
import de.kbs.acavis.service.model.persistence.SearchResult;
import de.kbs.acavis.service.models.visualization.YearlyDistribution;


/**
 * The DAO implementation for AcaVis-Searches.<br />
 * <br />
 * The database-connection is injected by spring. The name of the persistence-unit is <em>acavis</em>.
 * 
 * @author Sebastian
 *
 */
@Component
public class SearchManager
{
  @PersistenceContext(unitName = "acavis")
  private EntityManager entityManager;


  /**
   * Performs a search for publications by publication-fields using the given integration. The search-wrapper and its results are available
   * through the database.
   * 
   * @param providerClassName
   *          Qualified package-/classname of the integration to use for the search
   * @param query
   *          The input-query in plain (unescaped) format
   * @param limit
   *          The maximum number of results to return
   * @return The id of the search in the database
   * @throws InvalidIntegrationException
   *           If the given qualified package-/classname doesn't refer to a valid provider. See
   *           {@link IntegrationMetainfos#isIntegratingProvider(String, Class)} for criteria.
   * @throws UnimplementedFeatureException
   *           If the underlying integration doesn't provide searching for publications by publication-fields
   * @throws IntegrationUnavailableException
   *           If the underlying integration is temporarily or permanently not available. See exception-message for details.
   */
  @Transactional
  public long searchByPublication(String providerClassName, String query, int limit)
    throws InvalidIntegrationException, UnimplementedFeatureException, IntegrationUnavailableException
  {
    if (!IntegrationMetainfos.isIntegratingProvider(providerClassName, PublicationProvider.class))
      throw new InvalidIntegrationException("The given provider is not applicable for searching by publication-fields.");

    PublicationProvider provider = IntegrationProviderFactory.createPublicationProvider(providerClassName);

    SearchEntity search = new SearchEntity(query, providerClassName, limit);

    // The underlying exceptions are thrown because the presentation-layer should give a response to the user
    LinkedHashSet<PublicationSearchData> results = provider.searchPublicationByPublication(query, limit);
    for (PublicationSearchData result : results)
      search.addResult(new SearchResult(search, result));

    long searchId = persistSearch(search);

    return searchId;
  }


  /**
   * Performs a search for publications by author's name using the given integration. The search-wrapper and its results are available
   * through the database.
   * 
   * @param providerClassName
   *          Qualified package-/classname of the integration to use for the search
   * @param authorName
   *          The author's name in plain (unescaped) format
   * @param limit
   *          The maximum number of results to return
   * @return The id of the search in the database
   * @throws InvalidIntegrationException
   *           If the given qualified package-/classname doesn't refer to a valid provider. See
   *           {@link IntegrationMetainfos#isIntegratingProvider(String, Class)} for criteria.
   * @throws UnimplementedFeatureException
   *           If the underlying integration doesn't provide searching for publications by publication-fields
   * @throws IntegrationUnavailableException
   *           If the underlying integration is temporarily or permanently not available. See exception-message for details.
   */
  @Transactional
  public long searchByAuthor(String providerClassName, String authorName, int limit)
    throws InvalidIntegrationException, UnimplementedFeatureException, IntegrationUnavailableException
  {
    if (!IntegrationMetainfos.isIntegratingProvider(providerClassName, PublicationProvider.class))
      throw new InvalidIntegrationException("The given provider is not applicable for searching by author's name.");

    PublicationProvider provider = IntegrationProviderFactory.createPublicationProvider(providerClassName);

    SearchEntity search = new SearchEntity(authorName, providerClassName, limit);

    // The underlying exceptions are thrown because the presentation-layer should give a response to the user
    LinkedHashSet<PublicationSearchData> results = provider.searchPublicationByAuthor(authorName, limit);
    for (PublicationSearchData result : results)
      search.addResult(new SearchResult(search, result));

    long searchId = persistSearch(search);

    return searchId;
  }


  /**
   * Stores a search-entity to the database.<br />
   * A caller has to have a {@link Transactional} annotation!
   * 
   * @param search
   *          The search-entity
   * @return The id of the entity, as assigned by the database
   */
  private long persistSearch(SearchEntity search)
  {
    entityManager.persist(search);

    entityManager.flush();

    // The id is valid AFTER flush() or commit()
    return search.getId();
  }


  /**
   * Returns the yearly distribution of the search-results associated with a specific cached search.
   * 
   * @param searchid
   *          The id of the cached search
   * @return The yearly distribution in a container-class of type {@link YearlyDistribution}
   * @throws EntityNotFoundException
   *           If there was no cached search for the given id
   */
  public YearlyDistribution statsOfDistribution(long searchid)
    throws EntityNotFoundException
  {
    SearchEntity search = getSearch(searchid);

    YearlyDistribution distribution = new YearlyDistribution(true);

    for (SearchResult result : search.getResults())
      distribution.increaseCount(result.getYear());

    return distribution;
  }


  /**
   * Returns a search-wrapper object and the corresponding search-results.
   * 
   * @param searchId
   *          The id of the search-wrapper
   * @return The search-wrapper from database-cache
   * @throws EntityNotFoundException
   *           If there was no search with the given id
   */
  @Transactional(readOnly = true)
  public SearchEntity getSearch(long searchId)
    throws EntityNotFoundException
  {
    try
    {
      return entityManager.createNamedQuery("Search.byId", SearchEntity.class).setParameter("id", searchId)
          .getSingleResult();
    }
    catch (NoResultException e)
    {
      throw new EntityNotFoundException("A cached search with the id '" + searchId + "' couldn't be found!");
    }
  }


  /**
   * Returns all of the cached searches in historical order (descending).
   * 
   * @return A list containing instances of type {@link SearchEntity}
   */
  @Transactional(readOnly = true)
  public List<SearchEntity> searchHistory()
  {
    return entityManager.createNamedQuery("Search.latest", SearchEntity.class).getResultList();
  }
}
