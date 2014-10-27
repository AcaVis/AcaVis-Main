/*****************************************
 * This file is part of AcaVis, a tool for research of scientific
 * literature using the benefits of visualizations.
 * Copyright (C) 2014 - Sebastian Holzki
 * 
 * AcaVis is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License Version 2
 * as published by the Free Software Foundation;
 *****************************************/
package de.kbs.acavis.integration.mas.controller;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.core4j.Enumerable;
import org.odata4j.core.OEntity;

import de.kbs.acavis.integration.IdentifierException;
import de.kbs.acavis.integration.PublicationUnavailableException;
import de.kbs.acavis.integration.mas.odata.EntityHelper;
import de.kbs.acavis.integration.mas.odata.MASEntityHelper;
import de.kbs.acavis.integration.mas.odata.MASOdataConnector;
import de.kbs.acavis.integration.mas.odata.MASRelationHelper;
import de.kbs.acavis.integration.mas.odata.RelationHelper;
import de.kbs.acavis.integration.model.PublicationIdentifier;
import de.kbs.acavis.integration.model.PublicationLinkData;


/**
 * Offers methods that use a combination of OData-Requests to achieve a high-level aim. The results are directly transferred to the given
 * (in-memory) database.
 * 
 * @author Sebastian
 *
 */
public class OdataRequests
{
  private MASEntityHelper entityHelper = null;
  private MASRelationHelper relationHelper = null;
  private InMemoryDao dao = null;


  public OdataRequests(InMemoryDao dao)
  {
    MASOdataConnector.create();
    entityHelper = new MASEntityHelper(MASOdataConnector.consumer);
    relationHelper = new MASRelationHelper(MASOdataConnector.consumer);

    this.dao = dao;
  }


  /**
   * Transfers a publication with all available information to the database.<br />
   * <br />
   * No citing and cited papers are transferred! Use the 'network'-methods for this purpose.
   * 
   * @param publicationIdentifier
   *          The identifier of the publication to transfer
   * @throws IdentifierException
   *           If the identifier (mas-id) is not available
   * @throws PublicationUnavailableException
   */
  public void publicationOverviewToMemdb(PublicationIdentifier publicationIdentifier)
    throws IdentifierException, PublicationUnavailableException
  {
    // MAS only supports its own ID to be searched for
    int paperId = publicationIdentifier.getMasIdentifier();

    // The papers themselves
    OEntity paperEntity = entityHelper.getPaper(paperId);

    if (paperEntity == null)
      throw new PublicationUnavailableException("There is no publication available for the given identifier!");

    dao.persistPaper(paperEntity);

    // The references and citations, we only use them here to count them
    dao.persistPaperRefs(relationHelper.getPaperReferences(paperId));
    dao.persistPaperRefs(relationHelper.getPaperCitations(paperId));

    // Get authors for all papers
    dao.persistPaperAuthors(relationHelper.getAuthorsByPaper(paperId));

    // Keywords
    Enumerable<OEntity> rootPaperKeywords = relationHelper.getKeywordsByPaper(paperId);
    dao.persistKeywords(entityHelper.getKeywords(EntityHelper.<Integer> extractProperty(rootPaperKeywords, "KeywordID")));
    dao.persistPaperKeywords(rootPaperKeywords);

    // Disciplines and sub-disciplines
    Enumerable<OEntity> rootPaperCategories = relationHelper.getPaperCategoriesByPaper(paperId);
    dao.persistPaperCategories(rootPaperCategories);
    dao.persistCategories(entityHelper.getCategories());
    dao.persistDomains(entityHelper.getDomains());

    // Paper Urls
    dao.persistPaperUrls(entityHelper.getUrlsByPaper(paperId));

    // Commit the persisted data
    dao.commitPublicationData();
  }


  /**
   * Transfers multiple publications with all available information to the database.<br />
   * <br />
   * No citing and cited publications are transferred! Use the 'network'-methods for this purpose.
   * 
   * @param publicationIdentifiers
   *          A set of publication-idientifiers to use
   * @param ignoreInvalidIdentifiers
   *          Whether to ignore publication-identifiers that raise an exception
   * @throws IdentifierException
   *           If invalid identifiers are not indicated to be ignored by the corresponding parameter and one of the publication-identfiers
   *           (mas-ids) raises an exception.
   */
  public void publicationOverviewsToMemdb(Collection<PublicationIdentifier> publicationIdentifiers,
      boolean ignoreInvalidIdentifiers)
    throws IdentifierException
  {
    Set<Integer> paperIds = new HashSet<Integer>(publicationIdentifiers.size());

    // Get the mas-ids out of the container
    for (PublicationIdentifier publicationIdentifier : publicationIdentifiers)
    {
      try
      {
        paperIds.add(publicationIdentifier.getMasIdentifier());
      }
      catch (IdentifierException e)
      {
        // Decide whether to throw the exception ore to ignore it
        if (!ignoreInvalidIdentifiers)
          throw e;
      }
    }

    // The papers themselves
    dao.persistPapers(entityHelper.getPapers(paperIds));

    // The references and citations, we only use them here to count them
    dao.persistPaperRefs(relationHelper.getPaperReferences(paperIds));
    dao.persistPaperRefs(relationHelper.getPaperCitations(paperIds));

    // Get authors for all papers
    dao.persistPaperAuthors(relationHelper.getAuthorsByPaper(paperIds));

    // Keywords
    Enumerable<OEntity> rootPaperKeywords = relationHelper.getKeywordsByPaper(paperIds);
    dao.persistKeywords(entityHelper.getKeywords(EntityHelper.<Integer> extractProperty(rootPaperKeywords, "KeywordID")));
    dao.persistPaperKeywords(rootPaperKeywords);

    // Disciplines and sub-disciplines
    Enumerable<OEntity> rootPaperCategories = relationHelper.getPaperCategoriesByPapers(paperIds);
    dao.persistPaperCategories(rootPaperCategories);
    dao.persistCategories(entityHelper.getCategories());
    dao.persistDomains(entityHelper.getDomains());

    // Paper Urls
    dao.persistPaperUrls(entityHelper.getUrlsByPaper(paperIds));

    // Commit the persisted data
    dao.commitPublicationData();
  }


  /**
   * Prepares data for a root-publication and its cited/citing publications. The data will be stored in a sparse format
   * {@link PublicationLinkData} so we just need papers and their author.
   * 
   * @param publicationIdentifier
   *          The identifier of the root-publication
   * @param maxCiting
   *          The maximum number of citing papers to retrieve (unordered)
   * @param maxCited
   *          The maximum number of cited papers to retrieve (ordered)
   * @throws IdentifierException
   *           If the given root-publication-identifier (mas-id) is invalid
   */
  public void publicationLinkListToMemDb(PublicationIdentifier publicationIdentifier, int maxCiting, int maxCited)
    throws IdentifierException
  {
    // MAS only supports its own ID to be searched for
    int paperId = publicationIdentifier.getMasIdentifier();

    // The references and citations
    Enumerable<OEntity> references = relationHelper.getPaperReferences(paperId, maxCited);
    Enumerable<OEntity> citations = relationHelper.getPaperCitations(paperId, maxCiting);
    dao.persistPaperRefs(references);
    dao.persistPaperRefs(citations);

    // The referenced and cited papers
    HashSet<Integer> paperIds = new HashSet<Integer>(1 + references.count() + citations.count());
    paperIds.add(paperId);
    paperIds.addAll(RelationHelper.extractReferencedPaperIds(references));
    paperIds.addAll(RelationHelper.extractCitingPaperIds(citations));

    // The papers
    dao.persistPapers(entityHelper.getPapers(paperIds));

    // The paper-authors (We only need authors because we store them into a PublicationLink later on)
    dao.persistPaperAuthors(relationHelper.getAuthorsByPaper(paperIds));

    // Commit the persisted data
    dao.commitPublicationData();
  }


  /**
   * Prepares data for a set of root-publications and their cited/citing publications. The data will be stored in a sparse format
   * {@link PublicationLinkData} so we just need papers and their author.
   * 
   * @param publicationIdentifiers
   *          A set of identifier for the root-publications
   * @param maxCiting
   *          The maximum number of citing papers to retrieve (unordered)
   * @param maxCited
   *          The maximum number of cited papers to retrieve (ordered)
   * @param ignoreInvalidIdentifiers
   *          Whether to ignore root-publication-identifiers that raise an exception
   * @throws IdentifierException
   *           If invalid identifiers are not indicated to be ignored by the corresponding parameter and one of the publication-identfiers
   *           (mas-ids) raises an exception.
   */
  public void publicationLinksListsToMemDb(Collection<PublicationIdentifier> publicationIdentifiers, int maxCiting,
      int maxCited, boolean ignoreInvalidIdentifiers)
    throws IdentifierException
  {
    HashSet<Integer> paperIds = new HashSet<Integer>();

    // Get the mas-ids out of the container
    for (PublicationIdentifier identifier : publicationIdentifiers)
    {
      try
      {
        paperIds.add(identifier.getMasIdentifier());
      }
      catch (IdentifierException e)
      {
        // Decide whether to throw the exception ore to ignore it
        if (!ignoreInvalidIdentifiers)
          throw e;
      }
    }

    // The references and citations
    Enumerable<OEntity> references = relationHelper.getPaperReferences(paperIds, maxCited);
    Enumerable<OEntity> citations = relationHelper.getPaperCitations(paperIds, maxCiting);
    dao.persistPaperRefs(references);
    dao.persistPaperRefs(citations);

    // The referenced and cited papers
    paperIds.addAll(RelationHelper.extractReferencedPaperIds(references));
    paperIds.addAll(RelationHelper.extractCitingPaperIds(citations));

    // The papers
    dao.persistPapers(entityHelper.getPapers(paperIds));

    // The paper-authors (We only need authors because we store them into a PublicationLink later on)
    dao.persistPaperAuthors(relationHelper.getAuthorsByPaper(paperIds));

    // Commit the persisted data
    dao.commitPublicationData();
  }

  // @Deprecated
  // public void publicationWithRefs(PublicationIdentifier rootPublicationIdentifier)
  // throws IdentifierException
  // {
  // // MAS only supports its own ID to be searched for
  // int rootPublicationId = rootPublicationIdentifier.getMasIdentifier();
  //
  // Set<Integer> paperIds = new HashSet<Integer>(20);
  // paperIds.add(rootPublicationId);
  //
  // // Fetch ids of all referenced and citing papers, so we have all the nodes necessary for this case
  // Enumerable<OEntity> rootCitations = relationHelper.getPaperCitations(rootPublicationId);
  // Enumerable<OEntity> rootReferences = relationHelper.getPaperReferences(rootPublicationId);
  // paperIds.addAll(RelationHelper.extractCitingPaperIds(rootCitations));
  // paperIds.addAll(RelationHelper.extractReferencedPaperIds(rootReferences));
  //
  // System.out.println("Papers: " + paperIds.size());
  //
  // // Get all the papers
  // List<Enumerable<OEntity>> papers = entityHelper.getPapersBulk(paperIds);
  // for (Enumerable<OEntity> pape : papers)
  // dao.persistPapers(pape);
  //
  // // Get all the references (implicitly contains citations too, because it's a bidirectional table with unique entries)
  // // Enumerable<OEntity> r = relationHelper.getPaperReferencesBulk(paperIds);
  // // dao.persistPaperRefs(r);
  // System.out.println("Cits: " + rootCitations.count());
  // System.out.println("Refs: " + rootReferences.count());
  // dao.persistPaperRefs(rootCitations);
  // dao.persistPaperRefs(rootReferences);
  //
  // // Get authors for all papers
  // dao.persistPaperAuthors(relationHelper.getAuthorsByPaperBulk(paperIds));
  //
  // // Get all the secondary data _only_ for the root paper
  // Enumerable<OEntity> rootPaperKeywords = relationHelper.getKeywordsByPaper(rootPublicationId);
  // dao.persistPaperKeywords(rootPaperKeywords);
  // dao.persistKeywords(entityHelper.getKeywords(EntityHelper.<Integer> extractProperty(rootPaperKeywords, "KeywordID")));
  // dao.persistPaperUrls(entityHelper.getUrlsByPaper(rootPublicationId));
  //
  // // Commit the persisted data
  // dao.commitPublicationData();
  // }

  /**
   * Transfers Publication-data from MAS to the In-Memory DB using the OData-API. The world consist of the root-publication, its
   * second-level referenced papers and all references and metadata belonging to this partial network.
   * 
   * @param rootPublicationIdentifier
   *          The identifier of the root-publication, to start fetching.
   * @throws IdentifierException
   *           If the MAS-identifier wasn't set or is invalid.
   */
  // @Deprecated
  // public void publicationToMemdb(PublicationIdentifier rootPublicationIdentifier, int levelsOfTraversal)
  // throws IdentifierException
  // {
  // // MAS only supports its own ID to be searched for
  // int rootPublicationId = rootPublicationIdentifier.getMasIdentifier();
  //
  // // Collect all needed papers first (Tree traversal) to minimize OData-requests
  // Enumerable<OEntity> papers = Enumerable.create(entityHelper.getPaper(rootPublicationId));
  // dao.persistPapers(papers);
  //
  // // We use two levels because the return-type only stores its references and citation
  // paperNetworkTraversal(papers.toSet(), levelsOfTraversal);
  //
  // Set<Integer> overallPaperIds = null;// dao.getPersistedPaperIds();
  //
  // // Authors
  // dao.persistPaperAuthors(relationHelper.getAuthorsByPaper(overallPaperIds));
  //
  // // Keywords
  // Enumerable<OEntity> paperKeywords = relationHelper.getKeywordsByPaper(overallPaperIds);
  // dao.persistPaperKeywords(paperKeywords);
  // dao.persistKeywords(entityHelper.getKeywords(EntityHelper.<Integer> extractProperty(paperKeywords, "KeywordID")));
  //
  // // Urls
  // dao.persistPaperUrls(entityHelper.getUrlsByPaper(overallPaperIds));
  //
  // // Commit the persisted data
  // dao.commitPublicationData();
  // }

  /**
   * Transfers Publication-data from MAS to the In-Memory DB using the OData-API. The world consist of the root-publications, their
   * second-level referenced papers and all references and metadata belonging to this partial network.
   * 
   * @param rootPublicationIdentifiers
   *          The identifiers of the root-publications, to start fetching.
   * @throws IdentifierException
   *           If one of the MAS-identifiers wasn't set or is invalid.
   */
  // @Deprecated
  // public void publicationToMemdb(Set<PublicationIdentifier> rootPublicationIdentifiers, int levelsOfTraversal)
  // throws IdentifierException
  // {
  // // MAS only supports its own ID to be searched for
  // HashSet<Integer> masRootPaperIds = new HashSet<Integer>();
  // for (PublicationIdentifier identifier : rootPublicationIdentifiers)
  // masRootPaperIds.add(identifier.getMasIdentifier());
  //
  // // Collect all needed papers first (Tree traversal) to minimize OData-requests
  // Enumerable<OEntity> papers = Enumerable.create(entityHelper.getPapers(masRootPaperIds));
  // dao.persistPapers(papers);
  // // We use two levels because the return-type only stores its references and citation
  // paperNetworkTraversal(papers.toSet(), levelsOfTraversal);
  //
  // Set<Integer> overallPaperIds = null;// dao.getPersistedPaperIds();
  //
  // // Authors
  // dao.persistPaperAuthors(relationHelper.getAuthorsByPaper(overallPaperIds));
  //
  // // Keywords
  // Enumerable<OEntity> paperKeywords = relationHelper.getKeywordsByPaper(overallPaperIds);
  // dao.persistPaperKeywords(paperKeywords);
  // dao.persistKeywords(entityHelper.getKeywords(EntityHelper.<Integer> extractProperty(paperKeywords, "KeywordID")));
  //
  // // Urls
  // dao.persistPaperUrls(entityHelper.getUrlsByPaper(overallPaperIds));
  // }

  /**
   * Retrieves all papers along the citation- and reference-tree. The papers and paper-refs are stored within the enumerables given as
   * input-parameters.
   * 
   * @param papers
   *          The enumerable to hold the paper-results. Initially has to contain the base set of papers to traverse from.
   * @param nodeLevels
   *          The number of levels within the reference-/citation-tree that should be resolved. The number of levels is limited to 3
   *          internally to avoid harmful requests.
   */
  // public void paperNetworkTraversal(Set<OEntity> papers, int nodeLevels)
  // {
  // // Limit the maximum number of levels to 3
  // nodeLevels = Math.min(nodeLevels, 3);
  //
  // // The first set of nodes has already been inserted at the entry-point, that's why we have to decrement first
  // nodeLevels -= 1;
  //
  // HashSet<Integer> queuedPaperIds = new HashSet<Integer>();
  // for (OEntity paper : papers)
  // queuedPaperIds.add(EntityHelper.<Integer> extractProperty(paper, "ID"));
  //
  // for (Integer id : queuedPaperIds)
  // System.out.println(id);
  //
  // paperNetworkRecursion(queuedPaperIds, nodeLevels);
  //
  // // System.out.println(dao.getPersistedPaperIds().size() + " Papers");
  // }

  /**
   * The recursion-method for {@link #paperNetworkTraversal(Enumerable, Enumerable, int)}.
   * 
   * @param queuedPaperIds
   *          The ids of papers that should be retrieved in the next step.
   * @param level
   *          The current level in the reference-/citation-tree.
   */
  // private void paperNetworkRecursion(Set<Integer> queuedPaperIds, int level)
  // {
  // // Get references
  // Enumerable<OEntity> references = relationHelper.getPaperReferences(queuedPaperIds);
  // dao.persistPaperRefs(references);
  //
  // // Get citations but DON'T add them to avoid duplicates! We just use them to get the papers
  // Enumerable<OEntity> citations = relationHelper.getPaperCitations(queuedPaperIds);
  //
  // System.out.println("Added " + references.count() + " Refs");
  //
  // // Abort after reaching the last level and receiving the last levels' references
  // if (level == 0)
  // return;
  //
  // // Store paper-ids that were not expanded yet
  // Set<Integer> newPaperIds = RelationHelper.extractReferencedPaperIds(references);
  // newPaperIds.addAll(RelationHelper.extractCitingPaperIds(citations));
  // // newPaperIds.removeAll(dao.getPersistedPaperIds());
  //
  // Enumerable<OEntity> tmp = entityHelper.getPapers(newPaperIds);
  // dao.persistPapers(tmp);
  //
  // System.out.println("Added " + tmp.count() + " Papers");
  //
  // paperNetworkRecursion(newPaperIds, --level);
  // }
}
