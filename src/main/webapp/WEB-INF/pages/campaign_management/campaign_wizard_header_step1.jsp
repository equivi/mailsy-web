<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c' %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<div id="fuelux-wizard" class="row-fluid" data-target="#step-container">
    <ul class="wizard-steps">
        <li data-target="#step1" class="active">
            <span class="step">1</span>
            <span class="title"><spring:message code="label.campaign.info"/></span>
        </li>

        <li data-target="#step2">
            <span class="step">2</span>
            <span class="title"><spring:message code="label.campaign.email.content"/></span>
        </li>

        <li data-target="#step3">
            <span class="step">3</span>
            <span class="title"><spring:message code="label.campaign.email.recipients"/></span>
        </li>

        <li data-target="#step4">
            <span class="step">4</span>
            <span class="title"><spring:message code="label.campaign.email.review_and_confirm"/></span>
        </li>


        <%--<li data-target="#step5">--%>
            <%--<span class="step">5</span>--%>
            <%--<span class="title"><spring:message code="label.campaign.scheduled_send_date"/></span>--%>
        <%--</li>--%>

        <li data-target="#step5">
            <span class="step">5</span>
            <span class="title"><spring:message code="label.finish"/></span>
        </li>
    </ul>
</div>
