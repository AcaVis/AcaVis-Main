<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:layout>

    <jsp:attribute name="modals">
    
    </jsp:attribute>
    
    <jsp:body>

        <div class="alert alert-danger" role="alert">
          <strong>${alert_majortext}</strong><br />${alert_minortext} 
        </div>

    </jsp:body>

</t:layout>