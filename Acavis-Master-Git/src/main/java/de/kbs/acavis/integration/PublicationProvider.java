/*****************************************
 * This file is part of AcaVis, a tool for research of scientific
 * literature using the benefits of visualizations.
 * Copyright (C) 2014 - Sebastian Holzki
 * 
 * AcaVis is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License Version 2
 * as published by the Free Software Foundation;
 *****************************************/
package de.kbs.acavis.integration;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import javax.persistence.NoResultException;

import de.kbs.acavis.integration.model.AuthorData;
import de.kbs.acavis.integration.model.PublicationData;
import de.kbs.acavis.integration.model.PublicationIdentifier;
import de.kbs.acavis.integration.model.PublicationLinkData;
import de.kbs.acavis.integration.model.PublicationSearchData;


public interface PublicationProvider
{
  /**
   * Provides the results of a publication-based publication-search in a sorted set.
   * 
   * @param query
   *          An arbitrary search-query to use at integration-level
   * @param limit
   *          Limit the number of returned results, unlimited if 0 or less
   * @return The results of the publication-search {@link PublicationSearchData} in a {@link LinkedHashSet} to maintain order of the
   *         specific integration.
   * @throws UnimplementedFeatureException
   *           If the feature is not implemented by the integration.
   * @throws IntegrationUnavailableException
   *           If the integration is temporarily or permanently not available for this type of request.
   */
  public LinkedHashSet<PublicationSearchData> searchPublicationByPublication(String query, int limit)
    throws UnimplementedFeatureException, IntegrationUnavailableException;


  /**
   * Provides the results of a author-based publication-search in a sorted set.
   * 
   * @param query
   *          An arbitrary search-query to use at integration-level
   * @param limit
   *          Limit the number of returned results, unlimited if 0 or less
   * @return The results of the publication-search {@link PublicationSearchData} in a {@link LinkedHashSet} to maintain order of the
   *         specific integration.
   * @throws UnimplementedFeatureException
   *           If the feature is not implemented by the integration.
   * @throws IntegrationUnavailableException
   *           If the integration is temporarily or permanently not available for this type of request.
   */
  public LinkedHashSet<PublicationSearchData> searchPublicationByAuthor(String query, int limit)
    throws UnimplementedFeatureException, IntegrationUnavailableException;


  /**
   * Provides information of a single publication, without its citation-network.
   * 
   * @param identifier
   *          The identifier of the publication
   * @return The information corresponding to the identifier wrapped as class {@link PublicationData}.
   * @throws UnimplementedFeatureException
   *           If the feature is not implemented by the integration.
   * @throws IntegrationUnavailableException
   *           If the integration is temporarily or permanently not available for this type of request.
   * @throws IdentifierException
   *           If none of the suitable identifiers for the publication were valid.
   * @throws NoResultException
   *           If no result was found for the given identifier.
   * @throws PublicationUnavailableException
   *           If the given identifier is formally valid but didn't return any results
   */
  public PublicationData getPublicationOverview(PublicationIdentifier identifier)
    throws UnimplementedFeatureException, IntegrationUnavailableException, IdentifierException, NoResultException,
    PublicationUnavailableException;


  /**
   * Provides information of several publications, without their citation-networks.
   * 
   * @param identifiers
   *          The identifiers of the publications
   * @param ignoreInvalidIdentifiers
   *          Tells whether an exception is thrown if a single invalid identifier is contained in the collection of identifiers or if
   *          they're ignored alternatively
   * @return The information corresponding to the identifiers wrapped as a list of class {@link PublicationData}.
   * @throws UnimplementedFeatureException
   *           If the feature is not implemented by the integration.
   * @throws IntegrationUnavailableException
   *           If the integration is temporarily or permanently not available for this type of request.
   * @throws IdentifierException
   *           If none of the suitable identifiers for the publication were valid.
   */
  public List<PublicationData> getPublicationsOverview(Collection<PublicationIdentifier> identifiers,
      boolean ignoreInvalidIdentifiers)
    throws UnimplementedFeatureException, IntegrationUnavailableException, IdentifierException;


  /**
   * Provides information about publication-links, that are: a root-publication, publications that are cited by the root and publications
   * that are citing the root. Only a sparse set of information is stored along with the publication-entities.
   * 
   * @param identifier
   *          The identifier of the root publication
   * @param maxCiting
   *          Maximum number of citing publications, infinite if 0 or less
   * @param maxCited
   *          Maximum number of cited publications, infinite if 0 or less
   * @return The root-publication with two lists containing citing and cited publications
   * @throws UnimplementedFeatureException
   *           If this feature is not implemented by the integration.
   * @throws IntegrationUnavailableException
   *           If the integration is temporarily or permanently not available for this type of request.
   * @throws IdentifierException
   *           If none of the suitable identifiers for the publication were valid.
   */
  public PublicationLinkData getPublicationLinks(PublicationIdentifier identifier, int maxCiting, int maxCited)
    throws UnimplementedFeatureException, IntegrationUnavailableException, IdentifierException;


  /**
   * Provides information about publication-links, that are: several root-publications, publications that are cited by the roots and
   * publications that are citing the roots. Only a sparse set of information is stored along with the publication-entities.
   * 
   * @param identifiers
   *          The identifiers of the root-publications
   * @param maxCiting
   *          Maximum number of citing publications per <b>single</b> root-publication, infinite if 0 or less
   * @param maxCited
   *          Maximum number of cited publications per <b>single</b> root-publication, infinite if 0 or less
   * @return The root-publications with two lists each containing citing and cited publications
   * @throws UnimplementedFeatureException
   *           If this feature is not implemented by the integration.
   * @throws IntegrationUnavailableException
   *           If the integration is temporarily or permanently not available for this type of request.
   * @throws IdentifierException
   *           If none of the suitable identifiers for the publication were valid.
   */
  public List<PublicationLinkData> getPublicationsLinks(Collection<PublicationIdentifier> identifiers, int maxCiting,
      int maxCited)
    throws UnimplementedFeatureException, IntegrationUnavailableException, IdentifierException;


  public PublicationLinkData getPublicationNetwork(PublicationIdentifier publicationId)
    throws UnimplementedFeatureException, IntegrationUnavailableException, IdentifierException;


  public List<PublicationLinkData> getPublicationsNetwork(Collection<PublicationIdentifier> publicationIds)
    throws UnimplementedFeatureException, IntegrationUnavailableException, IdentifierException;


  public List<AuthorData> getPublicationAuthors(PublicationIdentifier publicationId)
    throws UnimplementedFeatureException, IntegrationUnavailableException, IdentifierException;


  public Map<PublicationIdentifier, List<AuthorData>> getPublicationsAuthors(
      Collection<PublicationIdentifier> publicationIds)
    throws UnimplementedFeatureException, IntegrationUnavailableException, IdentifierException;

  /**
   * Provides a single publication with all of the available data including the full citation-network.
   * 
   * @param publicationId
   *          The identifier of the publication
   * @param levelsOfTraversal
   *          The number of publication-levels to traverse when creating references an citations
   * @return An instance of {@link PublicationData} containing all information available for the request by the implementing web-service
   * @throws IdentifierException
   *           If none of the suitable identifiers for the publication were valid.
   * @throws UnimplementedFeatureException
   *           If the feature is not implemented by the integration.
   * @throws IntegrationUnavailableException
   *           If the integration is temporarily or permanently not available for this type of request.
   */
  // public PublicationData getPublicationNetwork(PublicationIdentifier publicationId, int levelsOfTraversal)
  // throws UnimplementedFeatureException, IntegrationUnavailableException, IdentifierException;

  /**
   * Provides a number of publications with all of the available data including the full citation-networks.
   * 
   * @param publicationIds
   *          A set of publication-identifiers
   * @param levelsOfTraversal
   *          The number of publication-levels to traverse when creating references an citations
   * @return A set of {@link PublicationData} instances containing all information available for the request by the implementing web-service
   * @throws IdentifierException
   *           If none of the suitable identifiers for the publication were valid
   * @throws UnimplementedFeatureException
   *           If the feature is not implemented by the integration.
   * @throws IntegrationUnavailableException
   *           If the integration is temporarily or permanently not available for this type of request.
   */
  // public Set<PublicationData> getPublicationNetworks(Set<PublicationIdentifier> publicationIds, int levelsOfTraversal)
  // throws UnimplementedFeatureException, IntegrationUnavailableException, IdentifierException;
}
