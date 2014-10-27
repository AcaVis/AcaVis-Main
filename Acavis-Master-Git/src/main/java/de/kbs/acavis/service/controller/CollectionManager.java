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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import org.mcavallo.opencloud.Cloud;
import org.mcavallo.opencloud.Cloud.Case;
import org.mcavallo.opencloud.Tag;
import org.mcavallo.opencloud.filters.DictionaryFilter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import de.kbs.acavis.integration.IdentifierException;
import de.kbs.acavis.integration.IntegrationUnavailableException;
import de.kbs.acavis.integration.PublicationProvider;
import de.kbs.acavis.integration.PublicationUnavailableException;
import de.kbs.acavis.integration.UnimplementedFeatureException;
import de.kbs.acavis.integration.model.AuthorData;
import de.kbs.acavis.integration.model.AuthorIdentifier;
import de.kbs.acavis.integration.model.PublicationData;
import de.kbs.acavis.integration.model.PublicationIdentifier;
import de.kbs.acavis.integration.model.PublicationLinkData;
import de.kbs.acavis.service.IntegrationProviderFactory;
import de.kbs.acavis.service.InvalidIntegrationException;
import de.kbs.acavis.service.model.persistence.CollectionEntity;
import de.kbs.acavis.service.model.persistence.CollectionItem;
import de.kbs.acavis.service.models.visualization.AuthorNode;
import de.kbs.acavis.service.models.visualization.CitationLink;
import de.kbs.acavis.service.models.visualization.CoAuthorshipLink;
import de.kbs.acavis.service.models.visualization.CoAuthorshipNetwork;
import de.kbs.acavis.service.models.visualization.PublicationCitationNetwork;
import de.kbs.acavis.service.models.visualization.PublicationNode;
import de.kbs.acavis.service.models.visualization.YearlyDistribution;


/**
 * The DAO implementation for AcaVis-collections.<br />
 * <br />
 * The database-connection is injected by spring. The name of the persistence-unit is <em>acavis</em>.
 * 
 * @author Sebastian
 */
@Component
public class CollectionManager
{
  @PersistenceContext(unitName = "acavis")
  private EntityManager entityManager;


  @Transactional(readOnly = true)
  public List<CollectionEntity> getCollections()
  {
    return entityManager.createNamedQuery("Collection.all", CollectionEntity.class).getResultList();
  }


  @Transactional(readOnly = true)
  public CollectionEntity getCollection(long id)
    throws EntityNotFoundException
  {
    try
    {
      return entityManager.createNamedQuery("Collection.byId", CollectionEntity.class).setParameter("id", id)
          .getSingleResult();
    }
    catch (NoResultException e)
    {
      throw new EntityNotFoundException("A collection with the id '" + id + "' couldn't be found!");
    }
  }


  @Transactional
  public long addCollection(String name)
  {
    // Escaping the name is important to avoid xss-attacks
    CollectionEntity collection = new CollectionEntity(HtmlUtils.htmlEscape(name));

    entityManager.persist(collection);

    entityManager.flush();

    // The id is valid AFTER flush() or commit()
    return collection.getId();
  }


  @Transactional
  public void removeCollection(long collectionId)
    throws EntityNotFoundException
  {
    CollectionEntity collection = getCollection(collectionId);

    entityManager.remove(collection);

    entityManager.flush();
  }


  @Transactional
  public void changeCollectionName(long collectionId, String name)
    throws EntityNotFoundException
  {
    CollectionEntity collection = getCollection(collectionId);

    collection.setName(name);

    entityManager.merge(collection);
  }


  @Transactional
  public void addPublicationToCollection(long collectionId, String integrationName, PublicationIdentifier identifier)
    throws EntityNotFoundException, InvalidIntegrationException, UnimplementedFeatureException,
    IntegrationUnavailableException, PublicationUnavailableException
  {
    CollectionEntity collection = getCollection(collectionId);

    try
    {
      PublicationManager manager = new PublicationManager(integrationName);
      PublicationData publication = manager.getPublicationData(identifier);

      collection.addPublication(new CollectionItem(collection, publication));

      entityManager.merge(collection);
    }

    // Identifier-exceptions should not occur because we wanted to ignore them
    catch (IdentifierException e)
    {}
  }


  @Transactional
  public void addPublicationsToCollection(long collectionId, String integrationName,
      List<PublicationIdentifier> identifiers)
    throws EntityNotFoundException, InvalidIntegrationException, UnimplementedFeatureException,
    IntegrationUnavailableException
  {
    CollectionEntity collection = getCollection(collectionId);

    try
    {
      PublicationManager manager = new PublicationManager(integrationName);
      List<PublicationData> publications = manager.getPublicationsData(identifiers, true);

      for (PublicationData publication : publications)
        collection.addPublication(new CollectionItem(collection, publication));

      entityManager.merge(collection);
    }

    // Identifier-exceptions should not occur because we wanted to ignore them
    catch (IdentifierException e)
    {}
  }


  @Transactional
  public void removeItemFromCollection(long collectionId, long itemId)
    throws EntityNotFoundException
  {
    CollectionEntity collection = getCollection(collectionId);

    try
    {
      CollectionItem item =
        entityManager.createNamedQuery("CollectionItem.byId", CollectionItem.class).setParameter("id", itemId)
            .getSingleResult();

      collection.removePublication(item);

      entityManager.merge(collection);
    }
    catch (NoResultException e)
    {
      throw new EntityNotFoundException("A collection-item with given id couldn't be found!");
    }
  }


  @Transactional
  public void removeItemsFromCollection(long collectionId, Collection<Long> itemIds)
    throws EntityNotFoundException
  {
    CollectionEntity collection = getCollection(collectionId);

    List<CollectionItem> items =
      entityManager.createNamedQuery("CollectionItem.byIds", CollectionItem.class).setParameter("id", itemIds)
          .getResultList();

    collection.removePublications(items);

    entityManager.merge(collection);
  }


  /**
   * Creates a list of tags for a tag-cloud based on the properties of the items within a collection.<br />
   * Titles, abstracts, keywords, disciplines and sub-disciplines of each publication are added to the tag-list.<br />
   * An internal stopword-list filters the most common stopwords of the English language.
   * 
   * @param collection
   *          The collection to get the publication-information from
   * @param maxTags
   *          The maximum number of tags in the list
   * @param minOccurrence
   *          The minimum score of tags to be included in the list
   * @return A list of tags
   */
  public List<Tag> collectionTagcloud(CollectionEntity collection, int maxTags, double minOccurrence)
  {
    // A list of english stop-words
    // Source: http://www.ranks.nl/stopwords (30.09.2014)
    String[] stopwords =
      { "a", "about", "above", "after", "again", "against", "all", "am", "an", "and", "any", "are", "aren't", "as",
          "at", "be", "because", "been", "before", "being", "below", "between", "both", "but", "by", "can't", "cannot",
          "could", "couldn't", "did", "didn't", "do", "does", "doesn't", "doing", "don't", "down", "during", "each",
          "few", "for", "from", "further", "had", "hadn't", "has", "hasn't", "have", "haven't", "having", "he", "he'd",
          "he'll", "he's", "her", "here", "here's", "hers", "herself", "him", "himself", "his", "how", "how's", "i",
          "i'd", "i'll", "i'm", "i've", "if", "in", "into", "is", "isn't", "it", "it's", "its", "itself", "let's",
          "me", "more", "most", "mustn't", "my", "myself", "no", "nor", "not", "of", "off", "on", "once", "only", "or",
          "other", "ought", "our", "ours   ourselves", "out", "over", "own", "same", "shan't", "she", "she'd",
          "she'll", "she's", "should", "shouldn't", "so", "some", "such", "than", "that", "that's", "the", "their",
          "theirs", "them", "themselves", "then", "there", "there's", "these", "they", "they'd", "they'll", "they're",
          "they've", "this", "those", "through", "to", "too", "under", "until", "up", "very", "was", "wasn't", "we",
          "we'd", "we'll", "we're", "we've", "were", "weren't", "what", "what's", "when", "when's", "where", "where's",
          "which", "while", "who", "who's", "whom", "why", "why's", "with", "won't", "would", "wouldn't", "you",
          "you'd", "you'll", "you're", "you've", "your", "yours", "yourself", "yourselves" };

    Cloud wordcloud = new Cloud();

    // Cloud-configuration
    wordcloud.setTagCase(Case.PRESERVE_CASE);
    wordcloud.setMaxTagsToDisplay(maxTags);
    wordcloud.setThreshold(minOccurrence);
    wordcloud.addOutputFilter(new DictionaryFilter(stopwords));

    for (CollectionItem publication : collection.getPublications())
    {
      // Split publication-title and add words
      wordcloud.addText(publication.getTitle());

      // Split publication-abstract and add words
      if (publication.getAbstractText() != null && !publication.getAbstractText().isEmpty())
        wordcloud.addText(publication.getAbstractText());

      // Add publication-keywords
      for (String keyword : publication.getKeywords())
        wordcloud.addTag(keyword);

      // Add publication-disciplines
      for (String discipline : publication.getDisciplines())
        wordcloud.addTag(discipline);

      // Add publication-sub-disciplines
      for (String subDiscipline : publication.getSubDisciplines())
        wordcloud.addTag(subDiscipline);
    }

    return wordcloud.tags();
  }


  /**
   * Creates a publication-citation-network for a collection in the cache. The returned network-container contains all the publications of a
   * collection as nodes and their citations/references (if some) as links. Additionally some metrics and a clustering is calculated.
   * 
   * @param collectionId
   *          ID of the collection to use as base
   * @param integrationName
   *          Full name of the integration-provider
   * @return A network-container with the publications and its citations/references
   * @throws EntityNotFoundException
   *           If a collection with given id couldn't be found
   * @throws InvalidIntegrationException
   *           If an integration-provider with the given full-name couldn't be found
   * @throws UnimplementedFeatureException
   *           If the given integration-provider is valid but doesn't deliver the necessary information
   * @throws IntegrationUnavailableException
   *           If the integration-provider is temporarily or permanently unavailable to handle the necessary request(s)
   * @throws IdentifierException
   *           If a publication-identifier cannot be used with the integration (Should practically never occur because the contained methods
   *           ignore such errors)
   */
  public PublicationCitationNetwork collectionCitations(long collectionId, String integrationName)
    throws EntityNotFoundException, InvalidIntegrationException, UnimplementedFeatureException,
    IntegrationUnavailableException, IdentifierException
  {
    // TODO Use Factory 'IntegrationProviderFactory' instead
    PublicationManager manager = new PublicationManager(integrationName);
    PublicationCitationNetwork network = new PublicationCitationNetwork();
    CollectionEntity collection = getCollection(collectionId);
    HashMap<PublicationIdentifier, PublicationNode> nodeCache =
      new HashMap<PublicationIdentifier, PublicationNode>(collection.getPublications().size());

    // Extract publication-identifiers
    Set<PublicationIdentifier> publicationIdentifiers = collection.getPublicationIdentifiers();

    // Get links between publications from the integration
    Collection<PublicationLinkData> publications = manager.getPublicationsNetwork(publicationIdentifiers);

    // Nodes
    int id = 0;
    // We assume that all of the nodes are contained in the base set
    for (PublicationLinkData publication : publications)
    {
      PublicationNode publicationNode =
        new PublicationNode(id++, true, publication.getId(), publication.getTitle(), publication.getAuthors(),
            publication.getYear());

      network.addNode(publicationNode);
      nodeCache.put(publication.getId(), publicationNode);
    }

    // Links
    id = 0;
    for (PublicationLinkData publication : publications)
    {
      // We only use referenced links, not cited, because we want to maintain the direction of the links in a directed graph. Furthermore,
      // it's a symmetric linkage
      for (PublicationLinkData referenced : publication.getReferences())
      {
        PublicationNode source = nodeCache.get(publication);
        PublicationNode destination = nodeCache.get(referenced);

        if (source == null || destination == null)
          continue;

        network.addLink(new CitationLink(id++, source.getId(), destination.getId()), source, destination);
      }
    }

    // The metrics and clustering
    network.calculatePageRank(0.85);
    network.calculateEdgeBetweennessClustering(5);

    return network;
  }


  /**
   * Creates a co-authorship network using the publications and authors of acollection.
   * 
   * @param collectionId
   *          ID of the collection
   * @param integrationName
   *          Full package- and classname of the integration-provider to use
   * @return A co-authorship network in a suitable container
   * @throws EntityNotFoundException
   * @throws InvalidIntegrationException
   * @throws UnimplementedFeatureException
   * @throws IntegrationUnavailableException
   * @throws IdentifierException
   */
  public CoAuthorshipNetwork collectionCoAuthors(long collectionId, String integrationName)
    throws EntityNotFoundException, InvalidIntegrationException, UnimplementedFeatureException,
    IntegrationUnavailableException, IdentifierException
  {
    // PublicationProvider provider = IntegrationProviderFactory.createPublicationProvider(integrationName);
    CollectionEntity collection = getCollection(collectionId);
    // CoAuthorshipNetwork network = new CoAuthorshipNetwork();
    // HashMap<AuthorIdentifier, AuthorNode> nodeCache = new HashMap<AuthorIdentifier, AuthorNode>();

    // Extract publication-identifiers
    Set<PublicationIdentifier> publicationIdentifiers = collection.getPublicationIdentifiers();

    // Map<PublicationIdentifier, List<AuthorData>> publicationAuthors =
    // provider.getPublicationsAuthors(publicationIdentifiers);
    //
    // // Add all authors first
    // int id = 0;
    // for (List<AuthorData> authors : publicationAuthors.values())
    // {
    // for (AuthorData author : authors)
    // {
    // if (!nodeCache.containsKey(author.getId()))
    // {
    // AuthorNode authorNode = new AuthorNode(id++, author.getId(), author.getName());
    //
    // nodeCache.put(author.getId(), authorNode);
    // network.addNode(authorNode);
    // }
    // }
    // }
    //
    // // Add links for each publication
    // id = 0;
    // for (List<AuthorData> authors : publicationAuthors.values())
    // {
    // for (AuthorData author1 : authors)
    // {
    // for (AuthorData author2 : authors)
    // {
    // AuthorNode authorNode1 = nodeCache.get(author1.getId());
    // AuthorNode authorNode2 = nodeCache.get(author2.getId());
    //
    // if (authorNode1 == null || authorNode2 == null)
    // continue;
    //
    // network.addLink(new CoAuthorshipLink(id++), authorNode1, authorNode2);
    // }
    // }
    // }
    //
    // // The metrics and clustering
    // network.calculateEdgeBetweennessClustering(3);

    return publicationsCoAuthors(publicationIdentifiers, integrationName);
  }


  /**
   * Creates a co-authorship network using a set of publications by given publication-identifiers.
   * 
   * @param publicationIdentifiers
   *          A set of publication-identifiers for building the network
   * @param integrationName
   *          Full package- and classname of the integration-provider to use
   * @return A co-authorship network in a suitable container
   * @throws InvalidIntegrationException
   *           If the given integration-provider is invalid
   * @throws UnimplementedFeatureException
   *           If the given provider doesn't support the necessary data-requests for retrieving publications and their authors
   * @throws IntegrationUnavailableException
   *           If the integration-provider is temporarily or permanently not available
   * @throws IdentifierException
   *           If at least one of the given identifiers couldn't be used to retrieve a publication from the given integration-provider
   */
  public CoAuthorshipNetwork publicationsCoAuthors(Collection<PublicationIdentifier> publicationIdentifiers,
      String integrationName)
    throws InvalidIntegrationException, UnimplementedFeatureException, IntegrationUnavailableException,
    IdentifierException
  {
    PublicationProvider provider = IntegrationProviderFactory.createPublicationProvider(integrationName);

    CoAuthorshipNetwork network = new CoAuthorshipNetwork();
    HashMap<AuthorIdentifier, AuthorNode> nodeCache = new HashMap<AuthorIdentifier, AuthorNode>();

    Map<PublicationIdentifier, List<AuthorData>> publicationAuthors =
      provider.getPublicationsAuthors(publicationIdentifiers);

    // Add all authors first
    int id = 0;
    for (List<AuthorData> authors : publicationAuthors.values())
    {
      for (AuthorData author : authors)
      {
        if (!nodeCache.containsKey(author.getId()))
        {
          AuthorNode authorNode = new AuthorNode(id++, author.getId(), author.getName());

          nodeCache.put(author.getId(), authorNode);
          network.addNode(authorNode);
        }
      }
    }

    // Add links for each publication
    id = 0;
    for (List<AuthorData> authors : publicationAuthors.values())
    {
      // Nested loop for cross-product of publications' authors
      for (AuthorData author1 : authors)
      {
        for (AuthorData author2 : authors)
        {
          AuthorNode authorNode1 = nodeCache.get(author1.getId());
          AuthorNode authorNode2 = nodeCache.get(author2.getId());

          if (authorNode1 == null || authorNode2 == null)
            continue;

          network.addLink(new CoAuthorshipLink(id++), authorNode1, authorNode2);
        }
      }
    }

    // The metrics and clustering
    network.calculateEdgeBetweennessClustering(3);
    // TODO pagerank and other metrics

    return network;
  }


  /**
   * Returns the yearly distribution of the publications in a collection.
   * 
   * @param collectionId
   *          ID of the collection to show data for
   * @return The yearly distribution in a container-class of type {@link YearlyDistribution}
   * @throws EntityNotFoundException
   *           If there was no collection for given id
   */
  public YearlyDistribution statsOfDistribution(long collectionId)
    throws EntityNotFoundException
  {
    CollectionEntity search = getCollection(collectionId);

    YearlyDistribution distribution = new YearlyDistribution(true);

    for (CollectionItem publication : search.getPublications())
      distribution.increaseCount(publication.getYear());

    return distribution;
  }
}
