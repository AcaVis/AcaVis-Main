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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import de.kbs.acavis.integration.IdentifierException;
import de.kbs.acavis.integration.IntegrationUnavailableException;
import de.kbs.acavis.integration.model.PublicationIdentifier;
import de.kbs.acavis.integration.model.PublicationSearchData;


public class WebRequests
{
  public static final String MAS_SEARCH_BASEURL = "http://academic.research.microsoft.com/Search";

  /**
   * Microsoft Academic Search has a fairly large response-time at the moment. Unit is milliseconds.
   */
  public static final int MAS_CONNECTION_TIMEOUT = 15000;
  /**
   * A lot of pages don't want their core-data to be crawled. Fake a real user-agent to hide the crawler.
   */
  public static final String MAS_CONNECTION_AGENT = "Mozilla";

  /**
   * An element that surrounds all real data of a single result.
   */
  public static final String MAS_SELECTOR_RESULT = "li.paper-item";
  /**
   * Specific here: Contains the title (inner-text) and MAS-ID (in 'href'-attribute) of a result.
   */
  public static final String MAS_SELECTOR_IDENTIFIER = "a[href~=(?i)Publication/\\d+/]";
  /**
   * The 'href' attribute, where to find the MAS-ID via regular-expression (see {@link #MAS_REGEX_IDENTIFIER}).
   */
  public static final String MAS_ATTRIBUTE_IDENTIFIER = "href";
  /**
   * The element that may contains an abstract or summary of the result.
   */
  public static final String MAS_SELECTOR_ABSTRACT = ".abstract";
  /**
   * The elements containing a <b>single</b> author of the result.
   */
  public static final String MAS_SELECTOR_AUTHORS = ".content a";
  /**
   * The element containing the citation-count, which is used as secondary metric for MAS. The value of the metric is determined by a regex
   * (see {@link #MAS_REGEX_CITATIONCOUNT}).
   */
  public static final String MAS_SELECTOR_CITATIONCOUNT = ".citation";
  /**
   * All the different conference-elements. There are three of them.<br />
   * First is for conferences, second for journals and third for other sources. Only one of them has information, the others are empty.<br />
   * Practically jsoups ':has()'-pseudoselector is used to get the right field.
   */
  public static final String MAS_SELECTOR_SOURCE = ".conference:has(span)";

  /**
   * The regular expression that contains the MAS-ID (only digits) as <b>first match-group</b>.
   */
  public static final String MAS_REGEX_IDENTIFIER = "Publication/(\\d+)/";
  /**
   * The regular expression that contains the citation-count (only digits) as <b>first match-group</b>.
   */
  public static final String MAS_REGEX_CITATIONCOUNT = "(\\d+)";
  /**
   * The regular expression that contains the publishing year (only digits) as <b>first match-group</b>.<br />
   * This can be applied to the 'source'-field.
   */
  public static final String MAS_REGEX_YEAR = "(\\d{4})(?!.*\\d{4})";


  public static LinkedHashSet<PublicationSearchData> searchPublications(String query, int limit)
    throws IntegrationUnavailableException
  {
    LinkedHashSet<PublicationSearchData> searchResults = new LinkedHashSet<PublicationSearchData>();

    try
    {
      Connection webConnection = Jsoup.connect(MAS_SEARCH_BASEURL);

      // The search-query
      webConnection.data("query", query);
      // Search exactly the given query, no auto-corrections
      webConnection.data("s", "0");

      // Retrieve results from 1 to <limit>, currently the max number of returned results doesn't seem to be limited
      // If MAS adds such limit, a change to page-based retrieval would be necessary
      webConnection.data("start", "1").data("end", String.valueOf(limit));

      // Connection-related stuff and UA fake
      webConnection.userAgent(MAS_CONNECTION_AGENT).timeout(MAS_CONNECTION_TIMEOUT);

      Document document = webConnection.get();
      document.absUrl(MAS_SEARCH_BASEURL);

      // For all results on this page
      Elements papers = document.select(MAS_SELECTOR_RESULT);
      for (Element paper : papers)
      {
        String title = "";
        String href = "";
        PublicationIdentifier identifier = new PublicationIdentifier();

        // Main part: Find the title and id, if none this entry is useless
        try
        {
          Element identifyingElement = paper.select(MAS_SELECTOR_IDENTIFIER).first();

          title = identifyingElement.text();
          href = identifyingElement.attr(MAS_ATTRIBUTE_IDENTIFIER);

          Matcher m = Pattern.compile(MAS_REGEX_IDENTIFIER, Pattern.CASE_INSENSITIVE).matcher(href);
          m.find();
          identifier.setMasIdentifier(Integer.valueOf(m.group(1)));
        }
        catch (NullPointerException | NumberFormatException | IllegalStateException | IdentifierException e)
        {
          // If this catch-block is entered, microsoft either changed the html-markup or somebody messed with the selector-configuration
          throw new IntegrationUnavailableException(
              "Microsoft Academic Search changed the HTML-markup, no valid publication-id can be found. Please contact an administrator because the parser may be out of date.",
              e);
        }

        // Authors
        Set<String> authors = extractAuthors(paper);

        // Citation-count
        int citationCount = extractCitationCount(paper);

        // Teaser-text
        String teaserText = extractAbstract(paper);

        // Source
        String source = extractSource(paper);

        // Year
        int year = extractYear(source);

        PublicationSearchData searchResult =
          new PublicationSearchData(identifier, title, citationCount, "citations", teaserText, source, year);
        searchResult.addAuthors(authors);

        searchResults.add(searchResult);
      }
    }
    catch (SocketTimeoutException e)
    {
      throw new IntegrationUnavailableException(
          "Publication-search via Microsoft Academic Search is currently offline. The service didn't respond within "
              + MAS_CONNECTION_TIMEOUT + " milliseconds.", e);
    }
    catch (MalformedURLException e)
    {
      throw new IntegrationUnavailableException(
          "Publication-search via Microsoft Academic Search is currently broken. The services request-url is inavlid, please contact an administrator.",
          e);
    }
    catch (HttpStatusException | UnsupportedMimeTypeException e)
    {
      throw new IntegrationUnavailableException(
          "Publication-search via Microsoft Academic Search is currently broken. The service-response couldn't be handled, please contact an administrator.",
          e);
    }
    catch (IOException e)
    {
      throw new IntegrationUnavailableException(
          "Publication-search via Microsoft Academic Search is currently broken. (" + e.getMessage() + ") Sorry!", e);
    }

    return searchResults;
  }


  private static String extractAbstract(Element baseElement)
  {
    try
    {
      return baseElement.select(MAS_SELECTOR_ABSTRACT).first().text();
    }
    // If we get a null-pointer exception because no element was found, continue using an empty abstract
    catch (NullPointerException e)
    {
      return "";
    }
  }


  private static Set<String> extractAuthors(Element baseElement)
  {
    HashSet<String> authors = new HashSet<String>();
    Elements authorElements = baseElement.select(MAS_SELECTOR_AUTHORS);

    for (Element authorElement : authorElements)
    {
      authors.add(authorElement.text());
    }

    return authors;
  }


  private static int extractCitationCount(Element baseElement)
  {
    try
    {
      String citation = baseElement.select(MAS_SELECTOR_CITATIONCOUNT).first().text();
      Matcher citationMatcher = Pattern.compile(MAS_REGEX_CITATIONCOUNT).matcher(citation);

      if (citationMatcher.find())
        return Integer.valueOf(citationMatcher.group(1));
      else
        return 0;
    }
    // If we get a null-pointer exception because no element was found, continue using a citation-count of 0
    catch (NullPointerException | NumberFormatException e)
    {
      return 0;
    }
  }


  private static String extractSource(Element baseElement)
  {
    try
    {
      return baseElement.select(MAS_SELECTOR_SOURCE).first().text();
    }
    // If we get a null-pointer exception because no element was found, continue using an empty abstract
    catch (NullPointerException e)
    {
      return "";
    }
  }


  private static int extractYear(String source)
  {
    try
    {
      Matcher citationMatcher = Pattern.compile(MAS_REGEX_YEAR).matcher(source);

      if (citationMatcher.find())
        return Integer.valueOf(citationMatcher.group(1));
      else
        return 0;
    }
    // If we get a null-pointer exception because no element was found, continue using a year of 0
    catch (NullPointerException | NumberFormatException e)
    {
      return 0;
    }
  }
}
