<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c' %>

<div class="sidebar" id="sidebar">
    <script type="text/javascript">
        try {
            ace.settings.check('sidebar', 'fixed')
        } catch (e) {
        }
    </script>
    <ul class="nav nav-list" id="navigation-menu">

        <c:if test="${currentPage.currentSubMenu == 'EMAIL_COLLECTOR'}">
        <li id="email-collector" class="active">
            </c:if>

            <c:if test="${currentPage.currentSubMenu != 'EMAIL_COLLECTOR'}">
        <li id="email-collector">
            </c:if>
            <a id="email-collector-menu" href="${pageContext.request.contextPath}/main/emailcollector/new">
                <i class="icon-search"></i>
                <spring:message code="label.menu.email.collector"/>
            </a>
        </li>

        <c:if test="${currentPage.currentSubMenu == 'EMAIL_VERIFIER'}">
        <li id="email-verifier" class="active">
            </c:if>

            <c:if test="${currentPage.currentSubMenu != 'EMAIL_VERIFIER'}">
        <li id="email-verifier">
            </c:if>
            <a id="email-verifier-menu" href="${pageContext.request.contextPath}/main/merchant/emailverifier">
                <i class="icon-ok-sign"></i>
                <spring:message code="label.menu.email.verifier"/>
            </a>
        </li>

        <c:if test="${currentPage.currentSubMenu == 'CAMPAIGN_MANAGEMENT'}">
        <li id="campaign-management" class="active">
            </c:if>

            <c:if test="${currentPage.currentSubMenu != 'CAMPAIGN_MANAGEMENT'}">
        <li id="campaign-management">
            </c:if>
            <a id="campaign-management-menu"
               href="${pageContext.request.contextPath}/main/merchant/campaign_management/1">
                <i class="icon-envelope"></i>
                <spring:message code="label.menu.campaign"/>
            </a>
        </li>

        <c:if test="${currentPage.currentSubMenu == 'SUBSCRIBER_MANAGEMENT'}">
        <li id="contact-management" class="active">
            </c:if>

            <c:if test="${currentPage.currentSubMenu != 'SUBSCRIBER_MANAGEMENT'}">
        <li id="contact-management">
            </c:if>
            <a id="subscriber-management-menu"
               href="${pageContext.request.contextPath}/main/merchant/subscriber_management/1">
                <i class="icon-book"></i>
                <spring:message code="label.menu.subscriber"/>
            </a>
        </li>

            <security:authorize access="hasAuthority('MAILSY_ADMIN')">
        <c:if test="${currentPage.currentSubMenu == 'USER_MANAGEMENT'}">
        <li id="user-management" class="active">
            </c:if>

            <c:if test="${currentPage.currentSubMenu != 'USER_MANAGEMENT'}">
        <li id="user-management">
            </c:if>
            <a id="user-management-menu" href="${pageContext.request.contextPath}/main/admin/users/1">
                <i class="icon-user"></i>
                <spring:message code="label.menu.administration.usermanagement"/>
            </a>
        </li>
        </security:authorize>
</div>

<script type="text/javascript">
    try {
        ace.settings.check('sidebar', 'fixed')
    } catch (e) {
    }
</script>

