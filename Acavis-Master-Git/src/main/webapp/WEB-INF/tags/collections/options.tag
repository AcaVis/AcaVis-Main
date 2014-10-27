<%@tag description="Search options modal" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

        <!-- Upper button row -->
        <div class="row collection-buttonrow">
          <div class="col-sm-4">
            <div class="input-group input-group-sm">
              <input type="text" class="form-control" placeholder="Title or author ..">
              <span class="input-group-btn">
                <button class="btn btn-default" type="button"><span class="glyphicon glyphicon-filter"></span> Filter</button>
                <button class="btn btn-default" type="button"><span class="glyphicon glyphicon-remove"></span> Clear</button>
              </span>
            </div>
          </div>
          <div class="col-sm-5 text-right">
            
          </div>
        </div>
        <!-- /Upper button row -->
        
        <!-- Lower Button row -->
        <div class="row collection-buttonrow">
          <div class="col-sm-4">
          
            <button type="button" class="btn btn-default btn-sm">
              <span class="glyphicon glyphicon-remove"></span> Remove selected
            </button>
            
            <div class="dropdown dropdown-inline">
              <button class="btn btn-sm btn-default dropdown-toggle" type="button" id="dropdownMovePublications" data-toggle="dropdown">
                <span class="glyphicon glyphicon-share"></span> Move selected to <span class="caret"></span>
              </button>
              <ul class="dropdown-menu" role="menu">
                <li role="presentation"><a href="#">Abstract units and visualization</a></li>
                <li role="presentation"><a href="#">Data mining</a></li>
                <li role="presentation"><a href="#">Edward Tuftes publications</a></li>
              </ul>
            </div>
            
          </div>
          <div class="col-sm-5 text-right">
          
            <!--<div class="dropdown dropdown-inline">
              <button class="btn btn-sm btn-default dropdown-toggle" type="button" id="dropdownSortResults" data-toggle="dropdown">
                <span class="glyphicon glyphicon-sort"></span> Sort <span class="caret"></span>
              </button>
              <ul class="dropdown-menu" role="menu" aria-labelledby="dropdownSortResults">
                <li role="presentation" class="dropdown-header">Publication-properties</li>
                <li role="presentation"><a role="menuitem" tabindex="-1" href="#">By year</a></li>
                <li role="presentation"><a role="menuitem" tabindex="-1" href="#">By publication-title</a></li>
                <li role="presentation"><a role="menuitem" tabindex="-1" href="#">By date of addition (collection)</a></li>
                <li role="presentation" class="divider"></li>
                <li role="presentation" class="dropdown-header">Impact-metrics</li>
                <li role="presentation"><a role="menuitem" tabindex="-1" href="#">By PageRank</a></li>
                <li role="presentation"><a role="menuitem" tabindex="-1" href="#">By authority-metric (HITS)</a></li>
                <li role="presentation"><a role="menuitem" tabindex="-1" href="#">By hub-metric (HITS)</a></li>
              </ul>
            </div>-->
            
            <!--<div class="dropdown dropdown-inline">
              <button class="btn btn-sm btn-default dropdown-toggle" type="button" id="dropdownSortOrderResults" data-toggle="dropdown">
                <span class="glyphicon glyphicon-sort-by-attributes"></span> Sort-order <span class="caret"></span>
              </button>
              <ul class="dropdown-menu" role="menu" aria-labelledby="dropdownSortOrderResults">
                <li role="presentation"><a role="menuitem" tabindex="-1" href="#"><span class="glyphicon glyphicon-sort-by-attributes"></span> Ascending</a></li>
                <li role="presentation"><a role="menuitem" tabindex="-1" href="#"><span class="glyphicon glyphicon-sort-by-attributes-alt"></span> Descending</a></li>
              </ul>
            </div>-->
            
            Sort:
            
            <select id="collectionSortItems" class="selectpicker" data-style="btn-sm btn-default" data-width="10em">
              <option value="year">by year</option>
              <option value="title" selected>by title</option>
              <option value="added" data-subtext="when added">by date</option>
<!--               <option data-divider="true"></option> -->
              <option value="citations">by citation-count</option>
              <!-- <option>by PageRank</option>
              <option data-subtext="HITS">by authority-score</option>
              <option data-subtext="HITS">by hub-score</option> -->
            </select>
            
            <select id="collectionOrderItems" class="selectpicker" data-style="btn-sm btn-default" data-width="9em">
              <option value="asc" data-content='<span class="glyphicon glyphicon-sort-by-attributes" selected></span> ascending'>ascending</option>
              <option value="desc" data-content='<span class="glyphicon glyphicon-sort-by-attributes-alt"></span> descending'>descending</option>
            </select>
            
          </div>
        </div>
        <!-- /Lower Button row -->