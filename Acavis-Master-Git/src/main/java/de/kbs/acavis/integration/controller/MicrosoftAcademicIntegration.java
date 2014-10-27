/*****************************************
 * This file is part of AcaVis, a tool for research of scientific
 * literature using the benefits of visualizations.
 * Copyright (C) 2014 - Sebastian Holzki
 * 
 * AcaVis is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License Version 2
 * as published by the Free Software Foundation;
 *****************************************/
package de.kbs.acavis.integration.controller;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.persistence.NoResultException;

import de.kbs.acavis.integration.IdentifierException;
import de.kbs.acavis.integration.IntegratedInfrastructure;
import de.kbs.acavis.integration.IntegrationUnavailableException;
import de.kbs.acavis.integration.PublicationProvider;
import de.kbs.acavis.integration.PublicationUnavailableException;
import de.kbs.acavis.integration.UnimplementedFeatureException;
import de.kbs.acavis.integration.mas.controller.InMemoryDao;
import de.kbs.acavis.integration.mas.controller.OdataRequests;
import de.kbs.acavis.integration.mas.controller.WebRequests;
import de.kbs.acavis.integration.mas.model.OverviewPaper;
import de.kbs.acavis.integration.mas.model.SparsePaper;
import de.kbs.acavis.integration.model.AuthorData;
import de.kbs.acavis.integration.model.PublicationData;
import de.kbs.acavis.integration.model.PublicationIdentifier;
import de.kbs.acavis.integration.model.PublicationLinkData;
import de.kbs.acavis.integration.model.PublicationSearchData;


/**
 * The integration of Microsoft Academic Search. Uses the official OData-API and traditional web-crawling as the API doesn't provide
 * fulltext-search. <br />
 * <br />
 * At the moment the Microsoft Academic Integration fully supports the implemented interfaces.
 * 
 * @author Sebastian
 */
@IntegratedInfrastructure(name = "Microsoft Academic Search")
public class MicrosoftAcademicIntegration
  implements PublicationProvider
{
  InMemoryDao dao = null;
  OdataRequests odata = null;


  @Override
  public LinkedHashSet<PublicationSearchData> searchPublicationByPublication(String query, int limit)
    throws IntegrationUnavailableException
  {
    return WebRequests.searchPublications(query, limit);
  }


  public LinkedHashSet<PublicationSearchData> searchPublicationByAuthor(String query, int limit)
    throws IntegrationUnavailableException
  {
    // Microsoft Academic Search performs author-search through the regular interface, surrounding the query with author(..query..)
    query = "author(" + query + ")";

    return WebRequests.searchPublications(query, limit);
  }


  private void initializeApiQueries()
  {
    dao = new InMemoryDao();
    odata = new OdataRequests(dao);
  }


  @Override
  public OverviewPaper getPublicationOverview(PublicationIdentifier identifier)
    throws UnimplementedFeatureException, IntegrationUnavailableException, IdentifierException, NoResultException,
    PublicationUnavailableException
  {
    initializeApiQueries();
    long masIdentifier = identifier.getMasIdentifier();

    // Transfers everything from OData to MemDB
    odata.publicationOverviewToMemdb(identifier);

    // Requests joined data from MemDB
    return dao.getEntityManager().createNamedQuery("retrieveOverviewPaperById", OverviewPaper.class)
        .setParameter("id", masIdentifier).setMaxResults(1).getSingleResult();
  }


  @Override
  public List<PublicationData> getPublicationsOverview(Collection<PublicationIdentifier> identifiers,
      boolean ignoreInvalidIdentifiers)
    throws UnimplementedFeatureException, IntegrationUnavailableException, IdentifierException
  {
    initializeApiQueries();

    // Transfers everything from OData to MemDB
    odata.publicationOverviewsToMemdb(identifiers, ignoreInvalidIdentifiers);

    // Get valid MAS-ids
    LinkedList<Long> masIds = new LinkedList<Long>();
    for (PublicationIdentifier identifier : identifiers)
    {
      try
      {
        masIds.add((long) identifier.getMasIdentifier());
      }
      catch (IdentifierException e)
      {}
    }

    // The papers from mem-db
    if (masIds.size() > 0)
    {
      List<OverviewPaper> papers =
        dao.getEntityManager().createNamedQuery("retrieveOverviewPapersByIds", OverviewPaper.class)
            .setParameter("ids", masIds).getResultList();

      // We have to adapt the list to an interface-conform version
      return new LinkedList<PublicationData>(papers);
    }

    // Fallback
    return new LinkedList<PublicationData>();
  }


  @Override
  public PublicationLinkData getPublicationLinks(PublicationIdentifier identifier, int maxCiting, int maxCited)
    throws UnimplementedFeatureException, IntegrationUnavailableException, IdentifierException, NoResultException
  {
    initializeApiQueries();

    // Get papers to mem-db
    odata.publicationLinkListToMemDb(identifier, maxCiting, maxCited);

    return dao.getEntityManager().createNamedQuery("SparsePaper.ByID", SparsePaper.class)
        .setParameter("id", identifier.getMasIdentifier()).getSingleResult();
  }


  @Override
  public List<PublicationLinkData> getPublicationsLinks(Collection<PublicationIdentifier> identifiers, int maxCiting,
      int maxCited)
    throws UnimplementedFeatureException, IntegrationUnavailableException, IdentifierException
  {
    initializeApiQueries();

    // Get valid MAS-ids
    LinkedList<Long> masIds = new LinkedList<Long>();
    for (PublicationIdentifier identifier : identifiers)
    {
      try
      {
        masIds.add((long) identifier.getMasIdentifier());
      }
      catch (IdentifierException e)
      {}
    }

    // Get papers to mem-db
    odata.publicationLinksListsToMemDb(identifiers, maxCiting, maxCited, true);

    // The papers from mem-db
    if (masIds.size() > 0)
    {
      List<SparsePaper> papers =
        dao.getEntityManager().createNamedQuery("SparsePaper.ByIDs", SparsePaper.class).setParameter("ids", masIds)
            .getResultList();

      // We have to adapt the list to an interface-conform version
      return new LinkedList<PublicationLinkData>(papers);
    }

    // Fallback
    return new LinkedList<PublicationLinkData>();
  }


  @Override
  public PublicationLinkData getPublicationNetwork(PublicationIdentifier publicationId)
    throws UnimplementedFeatureException, IntegrationUnavailableException, IdentifierException
  {
    initializeApiQueries();

    // TODO Auto-generated method stub
    return null;
  }


  @Override
  public List<PublicationLinkData> getPublicationsNetwork(Collection<PublicationIdentifier> publicationIdentifiers)
    throws UnimplementedFeatureException, IntegrationUnavailableException, IdentifierException
  {
    initializeApiQueries();

    // Get valid MAS-ids
    LinkedList<Long> masIds = new LinkedList<Long>();
    for (PublicationIdentifier identifier : publicationIdentifiers)
    {
      try
      {
        masIds.add((long) identifier.getMasIdentifier());
      }
      catch (IdentifierException e)
      {}
    }

    // Get papers to mem-db
    odata.publicationLinksListsToMemDb(publicationIdentifiers, -1, -1, true);

    // The papers from mem-db
    if (masIds.size() > 0)
    {
      List<SparsePaper> papers =
        dao.getEntityManager().createNamedQuery("SparsePaper.ByIDs", SparsePaper.class).setParameter("ids", masIds)
            .getResultList();

      // We have to adapt the list to an interface-conform version
      return new LinkedList<PublicationLinkData>(papers);
    }

    // Fallback
    return new LinkedList<PublicationLinkData>();
  }


  @Override
  public List<AuthorData> getPublicationAuthors(PublicationIdentifier publicationId)
    throws UnimplementedFeatureException, IntegrationUnavailableException, IdentifierException
  {
    throw new UnimplementedFeatureException(
        "Retrieving full author-infos for publications is currently not available via MAS-integration.");
  }


  @Override
  public Map<PublicationIdentifier, List<AuthorData>> getPublicationsAuthors(
      Collection<PublicationIdentifier> publicationIds)
    throws UnimplementedFeatureException, IntegrationUnavailableException, IdentifierException
  {
    throw new UnimplementedFeatureException(
        "Retrieving full author-infos for publications is currently not available via MAS-integration.");
  }

}
