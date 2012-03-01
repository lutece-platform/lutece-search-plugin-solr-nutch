/*
 * Copyright (c) 2002-2012, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.search.solr.nutch.business;

import fr.paris.lutece.plugins.search.solr.business.SolrHighlights;
import fr.paris.lutece.plugins.search.solr.business.SolrSearchResult;
import fr.paris.lutece.plugins.search.solr.nutch.util.NutchUtil;
import fr.paris.lutece.portal.service.search.SearchEngine;
import fr.paris.lutece.portal.service.search.SearchResult;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;


/**
 *
 * NutchSearchEngine
 *
 */
public final class NutchSearchEngine implements SearchEngine
{
    private static final String PROPERTY_SOLR_RESPONSE_MAX = "solr.nutch.reponse.max";
    private static final String PROPERTY_SEARCH_SITE = "solr.nutch.search.site";
    private static final SolrServer SOLR_SERVER = NutchServerService.getInstance(  ).getSolrServer(  );
    private static NutchSearchEngine _instance;

    /**
     * Empty private constructor
     */
    private NutchSearchEngine(  )
    {
    }

    /**
     * Returns an instance of NutchSearchEngine.
     *
     * @return an instance of NutchSearchEngine.
     */
    public static NutchSearchEngine getInstance(  )
    {
        if ( _instance == null )
        {
            _instance = new NutchSearchEngine(  );
        }

        return _instance;
    }

    /**
     * Return search results
     *
     * @param strQuery The search query
     * @param request The HTTP request
     * @return Results as a collection of SearchResult
     */
    public List<SearchResult> getSearchResults( String strQuery, HttpServletRequest request )
    {
        Integer nNbItemsPerPage = new Integer( AppPropertiesService.getProperty( PROPERTY_SOLR_RESPONSE_MAX ) );

        List<SolrSearchResult> nutchResults = getNutchSearchResults( strQuery, request, nNbItemsPerPage );
        List<SearchResult> searchResults = new ArrayList<SearchResult>( nutchResults.size(  ) );

        for ( SolrSearchResult nutchResult : nutchResults )
        {
            searchResults.add( nutchResult );
        }

        return searchResults;
    }

    /**
     * Return the result with highlights.
     *
     * @param strQuery the query
     * @param request the request
     * @return the results with highlights
     */
    public List<SolrSearchResult> getNutchSearchResults( String strQuery, HttpServletRequest request,
        Integer nNbItemsPerPage )
    {
        String strSearchSite = AppPropertiesService.getProperty( PROPERTY_SEARCH_SITE );

        List<SolrSearchResult> results = new ArrayList<SolrSearchResult>(  );

        if ( SOLR_SERVER != null )
        {
            strQuery = escapeIsoLatin( strQuery );

            SolrQuery query = new SolrQuery( strQuery );
            query.setParam( "fl", "id,title,url,tstamp" );

            if ( ( strSearchSite != null ) && ( strSearchSite.length(  ) != 0 ) )
            {
                query.setParam( "fq", "url:" + strSearchSite );
            }

            query.setHighlight( true );
            query.setRows( nNbItemsPerPage );

            try
            {
                QueryResponse response = SOLR_SERVER.query( query );
                SolrDocumentList documentList = response.getResults(  );
                Map<String, Map<String, List<String>>> highlightsMap = response.getHighlighting(  );
                SolrHighlights highlights = null;

                if ( highlightsMap != null )
                {
                    highlights = new SolrHighlights( highlightsMap );
                }

                results = NutchUtil.transformSolrDocumentList( documentList, highlights );
            }
            catch ( SolrServerException e )
            {
                AppLogService.error( e.getMessage(  ), e );
            }
        }

        return results;
    }

    /**
     * Remove IsoLatin char, the request will work because Solr is configured with ISOLatin1AccentFilterFactory analyser
     *
     * @param s
     * @return s but without accents
     */
    private String escapeIsoLatin( String s )
    {
        s = s.replaceAll( "[èéêë]", "e" );
        s = s.replaceAll( "[ûù]", "u" );
        s = s.replaceAll( "[ïî]", "i" );
        s = s.replaceAll( "[àâ]", "a" );
        s = s.replaceAll( "Ô", "o" );

        s = s.replaceAll( "[ÈÉÊË]", "E" );
        s = s.replaceAll( "[ÛÙ]", "U" );
        s = s.replaceAll( "[ÏÎ]", "I" );
        s = s.replaceAll( "[ÀÂ]", "A" );
        s = s.replaceAll( "Ô", "O" );

        return s;
    }
}
