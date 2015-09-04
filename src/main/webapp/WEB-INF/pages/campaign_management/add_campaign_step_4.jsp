<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<div class="page-header">
    <h1>
        <spring:message code="label.campaign.email.review_and_confirm"/>
    </h1>
</div>

<c:set var="sendDate"><spring:message code="label.campaign.scheduled_send_date"/></c:set>

<jsp:include page="email_quota.jsp"/>
<jsp:include page="campaign_wizard_header_step4.jsp"/>
</br>
<div align="right">
    <button id="test_email" class="btn btn-info">
        <i class="icon-inbox icon-on-right"></i>
        <spring:message code="label.campaign.send_test_email"/>
    </button>
</div>

</br>

<div class="widget-header widget-header-flat widget-header-small">
    <h5>
        <spring:message code="label.campaign.info"/>
    </h5>
</div>
<div class="widget-body">
    <div class="widget-main">
        <div class="row">
            <div class="form-group">
                <label class="col-sm-2 control-label no-padding-right"><b><spring:message
                        code="label.campaign.name"></spring:message></b></label>

                <div class="col-sm-9">
                    <label class="col-sm-3 control-label no-padding-right">${campaignDTO.campaignName}</label>
                </div>
            </div>
        </div>
        <div class="row">
            <div class="form-group">
                <label class="col-sm-2 control-label no-padding-right"><b><spring:message
                        code="label.campaign.email.from"></spring:message></b></label>

                <div class="col-sm-9">
                    <label class="col-sm-3 control-label no-padding-right">${campaignDTO.emailFrom}</label>
                </div>
            </div>
        </div>
        <div class="row">
            <div class="form-group">
                <label class="col-sm-2 control-label no-padding-right"><b><spring:message
                        code="label.campaign.subject"></spring:message></b></label>

                <div class="col-sm-9">
                    <label class="col-sm-3 control-label no-padding-right">${campaignDTO.emailSubject}</label>
                </div>
            </div>
        </div>
    </div>
</div>

</br>
</br>


<div class="widget-header widget-header-flat widget-header-small">
    <h5>
        <spring:message code="label.campaign.email.content"/>
    </h5>
</div>
<div class="widget-body">
    <div class="widget-main">
        <div id="email-content-div" class="widget-main-email-content">
            <c:out value="${campaignDTO.emailContent}"/>
        </div>
    </div>
</div>
</br>
</br>

<div class="clearfix form-actions">
    <div class="col-md-offset-3 col-md-9">
        <button id="back_to_user_id_list" class="btn" onclick="backToRecipients(${campaignDTO.id})">
            <i class="icon-arrow-left icon-on-right"></i>
            <spring:message code="label.prev"/>
        </button>
        &nbsp; &nbsp; &nbsp;

        <button class="btn btn-success btn-next" type="submit" onclick="sendEmail(${campaignDTO.id})">
            <spring:message code="label.confirm"/>
            <i class="icon-arrow-right icon-on-right"></i>
        </button>

    </div>
</div>

<input type="hidden" id="email-content" value="${campaignDTO.emailContent}">

<script type="text/javascript">
    var content = $('input#email-content').val();
    $("#email-content-div").html(content);
</script>


<c:set var="context" value="${pageContext.request.contextPath}"/>

<c:set var="campaignId" value="${campaignDTO.id}"/>
<script type="text/javascript">

    jQuery(window).load(function () {
        // quota info
        var emailSendingQuota = parseInt($("#emailSendingQuota").val());
        var emailSendingQuotaUsed = parseInt($("#emailSendingQuotaUsed").val());

        if (emailSendingQuotaUsed >= emailSendingQuota) {
            $("#btn-send-email").attr('disabled', 'disabled');
        }
    });

    jQuery(function ($) {
        $("#test_email").on(ace.click_event, function () {
            bootbox.prompt("Email to", function (result) {
                if (result === null) {
                    alert("Email to cannot be null");
                } else {
                    var url = "${context}/main/merchant/campaign_management/test_email/" + ${campaignId};

                    $.ajax({
                        url: url,
                        type: "POST",
                        data: result,

                        contentType: 'application/json',

                        success: function () {
                            $.gritter.add({
                                title: 'Send Test Email',
                                text: 'Email test has been send to : '+result,
                                class_name: 'gritter-success gritter-center gritter-light'
                            });

                            return false;
                        },
                        error: function (jqXHR, textStatus, errorThrown) {
                            console.log("Send email content - the following error occured: " + textStatus, errorThrown);
                        }
                    });

                }
            });
        });
    });

    <%--function goToScheduleDeliveryDate(campaignId) {--%>
        <%--window.location.replace("${context}/main/merchant/campaign_management/" + campaignId + "/campaignManagementEmailDeliveryPage");--%>
    <%--}--%>

    function sendEmail(campaignId) {
        // quota info
        var emailSendingQuota = parseInt($("#emailSendingQuota").val());
        var emailSendingQuotaUsed = parseInt($("#emailSendingQuotaUsed").val());

        if (emailSendingQuotaUsed < emailSendingQuota) {
            var url = "${context}/main/merchant/campaign_management/" + campaignId + "/saveCampaignDelivery";

            var scheduledDate = $('#id-date-picker-1').val();
            var scheduledTime = $('#timepicker1').val();

            $.ajax({
                url: url,
                type: "POST",
                data: scheduledDate + " " + scheduledTime,

                contentType: 'application/json',
                success: function () {
                    window.location.replace("${context}/main/merchant/campaign_management/goToFinishPage");
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    console.log("Save email content - the following error occured: " + textStatus, errorThrown);
                }
            });
        }

    }

    function backToRecipients(campaignId) {

        window.location.replace("${context}/main/merchant/campaign_management/" + campaignId + "/campaignManagementRecipientsPage");
    }
</script>