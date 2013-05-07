/*
 * Copyright (c) 2002-2013, Mairie de Paris
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
package fr.paris.lutece.plugins.search.solr.nutch.util;

import fr.paris.lutece.plugins.search.solr.business.SolrHighlights;
import fr.paris.lutece.plugins.search.solr.business.SolrSearchResult;
import fr.paris.lutece.plugins.search.solr.nutch.business.NutchSearchItem;

import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;


/**
 *
 * Util class for Nutch search.
 *
 */
public final class NutchUtil
{
    private static final String EMPTY_STRING = "";

    /**
     * Empty private constructor
     */
    private NutchUtil(  )
    {
    }

    /**
     * Transform a DocumentList to SearchResult List.
     * @param documentList the document list to transform
     * @param highlights the highlights
     * @return a SearchResult List.
     */
    public static List<SolrSearchResult> transformSolrDocumentList( SolrDocumentList documentList,
        SolrHighlights highlights )
    {
        List<SolrSearchResult> results = new ArrayList<SolrSearchResult>(  );

        for ( SolrDocument document : documentList )
        {
            NutchSearchItem searchItem = new NutchSearchItem( document );
            SolrSearchResult searchResult = new SolrSearchResult(  );
            searchResult.setId( searchItem.getId(  ) );
            searchResult.setSummary( EMPTY_STRING );
            searchResult.setTitle( searchItem.getTitle(  ) );
            searchResult.setType( EMPTY_STRING );
            searchResult.setUrl( searchItem.getUrl(  ) );
            searchResult.setDate( searchItem.getDate(  ) );
            searchResult.setRole( new ArrayList<String>(  ) );

            if ( highlights != null )
            {
                searchResult.setHighlight( highlights.getHighlights( searchResult.getUrl(  ) ) );
            }

            results.add( searchResult );
        }

        return results;
    }

    /**
     * Creates a date from Nutch long timestamps
     * @param tStamp the long nutch timestamp
     * @return the date
     */
    public static Date getDateFormNutchTStamp( Long tStamp )
    {
        String strTStamp = tStamp.toString(  );

        if ( strTStamp.length(  ) < 8 )
        {
            return null;
        }

        int nYear = Integer.parseInt( strTStamp.substring( 0, 4 ) );
        int nMonth = Integer.parseInt( strTStamp.substring( 4, 6 ) );
        int nDay = Integer.parseInt( strTStamp.substring( 6, 8 ) );
        Calendar calendar = GregorianCalendar.getInstance(  );
        calendar.set( nYear, nMonth - 1, nDay );

        return calendar.getTime(  );
    }
}
