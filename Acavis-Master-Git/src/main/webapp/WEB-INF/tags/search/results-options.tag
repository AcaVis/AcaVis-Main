<%@tag description="Search options modal" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

        <!-- Search and collection management-buttons -->
        <div class="row searchresults-management">
        
          <!-- Global Collection additions -->
          <div class="col-sm-6">
            <div class="dropdown dropdown-inline">
              <button class="btn btn-sm btn-default dropdown-toggle" type="button" id="dropdownSortResults" data-toggle="dropdown">
                <span class="glyphicon glyphicon-share"></span> Add to collection &hellip; <span class="caret"></span>
              </button>
              <ul class="dropdown-menu" role="menu">
                <li role="presentation" data-target="#modalAddAllToCollection" data-toggle="modal"><a href="#"><strong>All</strong> publications</a></li>
                <li role="presentation" data-target="#modalAddSelectedToCollection" data-toggle="modal"><a href="#"><strong>Selected</strong> publications</a></li>
              </ul>
            </div>
          </div>
          <!-- /Global Collection additions -->
          
          <!-- Sort and stats -->
          <div class="col-sm-6 text-right">
            <button type="button" class="btn btn-sm btn-primary triggerSearchStatistics" data-remote="${pageContext.request.contextPath}/search/results/${search.id}/distribution" data-toggle="modal" data-target="#modalSearchStatistics">
              <span class="glyphicon glyphicon-stats"></span>
              Statistics for this search
            </button>
            &nbsp;&nbsp;
            
            <!--<div class="dropdown dropdown-inline">
              <button class="btn btn-sm btn-default dropdown-toggle" type="button" id="dropdownSortResults" data-toggle="dropdown">
                <span class="glyphicon glyphicon-sort"></span> Sort <span class="caret"></span>
              </button>
              <ul class="dropdown-menu" role="menu" aria-labelledby="dropdownSortResults">
                <li role="presentation" class="dropdown-header">General</li>
                <li role="presentation"><a role="menuitem" tabindex="-1" href="#">By year</a></li>
                <li role="presentation"><a role="menuitem" tabindex="-1" href="#">By publication-title</a></li>
                <li role="presentation" class="divider"></li>
                <li role="presentation" class="dropdown-header">Integration-Specific</li>
                <li role="presentation"><a role="menuitem" tabindex="-1" href="#">By retrieved order</a></li>
                <li role="presentation"><a role="menuitem" tabindex="-1" href="#">By citation count</a></li>
              </ul>
            </div>
            <div class="dropdown dropdown-inline">
              <button class="btn btn-sm btn-default dropdown-toggle" type="button" id="dropdownSortOrderResults" data-toggle="dropdown">
                <span class="glyphicon glyphicon-sort-by-attributes"></span> Sort-order <span class="caret"></span>
              </button>
              <ul class="dropdown-menu" role="menu" aria-labelledby="dropdownSortOrderResults">
                <li role="presentation"><a role="menuitem" tabindex="-1" href="#"><span class="glyphicon glyphicon-sort-by-attributes"></span> Ascending</a></li>
                <li role="presentation"><a role="menuitem" tabindex="-1" href="#"><span class="glyphicon glyphicon-sort-by-attributes-alt"></span> Descending</a></li>
              </ul>
            </div>-->
            
            Sort:
            
            <select class="selectpicker" data-style="btn-sm btn-default" data-width="10em">
              <option>by year</option>
              <option>by title</option>
              <option data-divider="true"></option>
              <option data-subtext="from MAS">by retrieved order</option>
              <option data-subtext="from MAS">by citation-count</option>
            </select>
            
            <select class="selectpicker" data-style="btn-sm btn-default" data-width="9em">
              <option data-content='<span class="glyphicon glyphicon-sort-by-attributes"></span> ascending'>ascending</option>
              <option data-content='<span class="glyphicon glyphicon-sort-by-attributes-alt"></span> descending'>descending</option>
            </select>
            
          </div>
        </div>
        <!-- /Sort and stats -->