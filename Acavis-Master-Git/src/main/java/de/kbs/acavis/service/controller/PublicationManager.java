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

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

import javax.persistence.NoResultException;

import de.kbs.acavis.integration.IdentifierException;
import de.kbs.acavis.integration.IntegrationUnavailableException;
import de.kbs.acavis.integration.PublicationProvider;
import de.kbs.acavis.integration.PublicationUnavailableException;
import de.kbs.acavis.integration.UnimplementedFeatureException;
import de.kbs.acavis.integration.model.PublicationData;
import de.kbs.acavis.integration.model.PublicationIdentifier;
import de.kbs.acavis.integration.model.PublicationLinkData;
import de.kbs.acavis.integration.model.PublicationSearchData;
import de.kbs.acavis.service.IntegrationMetainfos;
import de.kbs.acavis.service.IntegrationProviderFactory;
import de.kbs.acavis.service.InvalidIntegrationException;


public class PublicationManager
{
  private PublicationProvider provider = null;


  public PublicationManager(String integrationClassName)
    throws InvalidIntegrationException
  {
    if (!IntegrationMetainfos.isIntegratingProvider(integrationClassName, PublicationProvider.class))
      throw new InvalidIntegrationException("The given provider is not applicable for searching by publication-fields.");

    provider = IntegrationProviderFactory.createPublicationProvider(integrationClassName);
  }


  public PublicationData getPublicationData(PublicationIdentifier identifier)
    throws UnimplementedFeatureException, IntegrationUnavailableException, IdentifierException, NoResultException,
    PublicationUnavailableException
  {
    return provider.getPublicationOverview(identifier);
  }


  public List<PublicationData> getPublicationsData(Collection<PublicationIdentifier> identifiers,
      boolean ignoreInvalidIdentifiers)
    throws UnimplementedFeatureException, IntegrationUnavailableException, IdentifierException
  {
    return provider.getPublicationsOverview(identifiers, ignoreInvalidIdentifiers);
  }


  public PublicationLinkData getPublicationLinks(PublicationIdentifier identifier, int max)
    throws UnimplementedFeatureException, IntegrationUnavailableException, IdentifierException
  {
    return provider.getPublicationLinks(identifier, max, max);
  }


  public List<PublicationLinkData> getPublicationsNetwork(Collection<PublicationIdentifier> identifiers)
    throws UnimplementedFeatureException, IntegrationUnavailableException, IdentifierException
  {
    return provider.getPublicationsNetwork(identifiers);
  }


  /**
   * Retrieves publication-data by a given publication-identifier from the underlying infrastructure.
   * 
   * @param identifier
   *          A publication-identifier to use
   * @return A publication (containing the publication-data)
   * @throws UnimplementedFeatureException
   *           If the infrastructure doesn't support the corresponding interface for publications or the retrieval of full publication-data
   * @throws IdentifierException
   *           If the identifier didn't contain any identifying information
   * @throws IntegrationUnavailableException
   */
  // public PublicationData getPublicationNetwork(PublicationIdentifier identifier)
  // throws UnimplementedFeatureException, IdentifierException, IntegrationUnavailableException
  // {
  // // Detailed publication-info only contains first references and citations by convention, so the level of traversal is 2
  // return provider.getPublicationNetwork(identifier, 2);
  // }

  /**
   * Retrieves publication-data by publication-identifier from the underlying infrastructure.
   * 
   * @param identifiers
   *          A set of identifiers to use for retrieval
   * @return A set of publications or an empty set (unsorted)
   * @throws UnimplementedFeatureException
   *           If the infrastructure doesn't support the corresponding interface for publications or the retrieval of full publication-data
   * @throws IdentifierException
   *           If one of the identifiers didn't contain any identifying information
   * @throws IntegrationUnavailableException
   */
  // public Set<PublicationData> getPublicationNetwork(Set<PublicationIdentifier> identifiers)
  // throws UnimplementedFeatureException, IdentifierException, IntegrationUnavailableException
  // {
  // // Detailed publication-info only contains first references and citations by convention, so the level of traversal is 2
  // return provider.getPublicationNetworks(identifiers, 2);
  // }

  /**
   * Performs a full-text-search for publications in the selected underlying integrated infrastructure, specified on instantiation of the
   * class.
   * 
   * @param query
   *          The query to search for in publications
   * @param offset
   *          An offset indicating a number of results to skip at the beginning (allows paging)
   * @param limit
   *          A limit indicating how many results to return, beginning at the offset-position
   * @return A sorted set of search-results
   * @throws UnimplementedFeatureException
   *           If the infrastructure supports the corresponding interface for publications, but not the full-text-search
   * @throws IntegrationUnavailableException
   *           If the infrastructure is temporarily or permanently unavailable
   */
  public LinkedHashSet<PublicationSearchData> searchPublication(String query, int offset, int limit)
    throws UnimplementedFeatureException, IntegrationUnavailableException
  {
    return provider.searchPublicationByPublication(query, limit);
  }


  /**
   * Performs a search for publications by uthor-name using the underlying infrastructure which was specified on creation of the instance.
   * 
   * @param query
   *          The author-query
   * @param offset
   *          An offset indicating a number of results to skip at the beginning (allows paging)
   * @param limit
   *          A limit indicating how many results to return, beginning at the offset-position
   * @return A sorted set of search-results
   * @throws UnimplementedFeatureException
   *           If the infrastructure supports the corresponding interface for publications, but not the full-text-search
   * @throws IntegrationUnavailableException
   *           If the infrastructure is temporarily or permanently unavailable
   */
  public LinkedHashSet<PublicationSearchData> searchPublicationByAuthor(String query, int offset, int limit)
    throws UnimplementedFeatureException, IntegrationUnavailableException
  {
    return provider.searchPublicationByAuthor(query, limit);
  }

  /**
   * Creates and returns a complete publication-citation-network, based on the given publication. References are as well retrieved as
   * citations. The number of levels for publication-traversal by references and citations can be specified. The level of traversal is
   * limited internally to avoid harmful requests.
   * 
   * @param identifier
   *          The identifier of the publication to use as 'base' of the traversal
   * @param levelsOfTraversal
   *          The number of levels that references and citations should be retrieved for
   * @return A directed network in adjusted format, also containing all the publication-data
   * @throws UnimplementedFeatureException
   *           If retrieval of publication-citation-networks isn't supported by the underlying integration, specified when calling the
   *           constructor of this class.
   * @throws IdentifierException
   *           If one of the identifiers didn't contain any identifying information
   * @throws IntegrationUnavailableException
   */
  // public PublicationCitationNetwork getPublicationCitationNetwork(PublicationIdentifier identifier,
  // int levelsOfTraversal)
  // throws UnimplementedFeatureException, IdentifierException, IntegrationUnavailableException
  // {
  // PublicationCitationNetwork network = new PublicationCitationNetwork();
  //
  // Set<PublicationData> publications = provider.getPublicationsFlatFormat(identifier, levelsOfTraversal);
  //
  // int n = 0;
  // for (PublicationData publication : publications)
  // network.addNode(new PublicationNode(n++, publication));
  //
  // // Cross-check for citations and references between the nodes
  // int l = 0;
  // Collection<PublicationNode> nodes = network.getNodes();
  // for (PublicationNode publicationNodeA : nodes)
  // for (PublicationNode publicationNodeB : nodes)
  // if (publicationNodeA.linksTo(publicationNodeB))
  // network.addLink(new CitationLink(l++), publicationNodeA, publicationNodeB);
  //
  // return network;
  // }
}
