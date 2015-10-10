<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c' %>

<c:set var="context" value="${pageContext.request.contextPath}"/>
<c:set var="subscriberGroupId" value="${subscriberGroupDTO.id}"/>
<jsp:include page="../general/delete_confirmation.jsp"></jsp:include>

</br>
<table>
    <td>
        <a href="#modal-form-subscriber" class="add-subscribe-button" data-toggle="modal">
            <button class="btn btn-info" type="button">
                <i class="icon-plus-sign white bigger-120"></i>
                <spring:message code="label.add"/>
            </button>
        </a>
    </td>
    <td>
        <a href="#">
            <button class="btn btn-info">
                <i class="icon-search white bigger-120"></i>
                <spring:message code="label.search"/>
            </button>
        </a>
    </td>
    <td>

        <div>
            <input type="text"
                   name="emailAddress"
                   size="50"
                   maxlength="50"
                   placeholder="<spring:message code="label.common.emailaddress"/>"
                   class="col-lg-12"/>
        </div>
    </td>
</table>

</br>
</br>

<table id="table-subscriber" name="table-user" class="table table-striped table-bordered table-hover"
       width="500px">
    <thead>
    <tr>
        <th><spring:message code="label.subscriber.email_address"/></th>
        <th><spring:message code="label.subscriber.status"/></th>
        <th><spring:message code="label.action"/></th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${subscriberDTOList}" var="subscriber">
        <tr>
            <td>${subscriber.emailAddress}</td>
            <td>
                <c:if test="${subscriber.subscribeStatus == 'SUBSCRIBED'}">
                    <span class="label label-sm label-success">Subscribed</span>
                </c:if>
                <c:if test="${subscriber.subscribeStatus == 'UNSUBSCRIBED'}">
                    <span class="label label-sm label-danger">Unsubscribed</span>
                </c:if>
            </td>
            <td>
                <div class="btn-group">
                    <button data-toggle="dropdown" class="btn btn-xs btn-info dropdown-toggle">
                        Action
                        <span class="icon-caret-down icon-on-right"></span>
                    </button>

                    <ul class="dropdown-menu dropdown-default">
                        <li>
                            <c:if test="${subscriber.subscribeStatus == 'SUBSCRIBED'}">
                                <a href="#" id="${subscriber.id}" class="unsubscribeContact">Unsubscribe</a>
                            </c:if>
                            <c:if test="${subscriber.subscribeStatus == 'UNSUBSCRIBED'}">
                                <a href="#" id="${subscriber.id}" class="subscribeContact">Subscribe</a>
                            </c:if>
                        </li>

                        <%--<li>--%>
                            <%--<a href="#" id="${subscriber.id}" class="deleteContact">Delete</a>--%>
                        <%--</li>--%>
                    </ul>
                </div>
                <!-- /btn-group -->
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>

<div class="clearfix form-actions">
    <div class="col-md-offset-3 col-md-9">
        <button id="back_to_subscriber_list" class="btn" onclick="backToSubcriberList()" type="reset">
            <i class="icon-undo bigger-110"></i>
            <spring:message code="label.subscriber.subscriber_group.subscriber_group_list"/>
        </button>
    </div>
</div>

<script type="text/javascript">
    $(document).ready(function () {
        $(".unsubscribeContact").click(function (event) {
            var subscriberId = $(this).attr("id");
            unsubscribeContact(subscriberId, "UNSUBSCRIBED")
        });
        $(".subscribeContact").click(function (event) {
            var subscriberId = $(this).attr("id");
            unsubscribeContact(subscriberId, "SUBSCRIBED")
        });

        $(".deleteContact").click(function (event) {
            var contactId = $(this).attr("id");
            deleteContact(contactId)
        });

    });

    function unsubscribeContact(id, status) {
        var href = "${context}/main/merchant/subscriber_management/subscriber_list/${subscriberGroupId}/1?nextPage=SUBSCRIBER_LIST";
        $.ajax({
            url: "${context}/main/merchant/subscriber_management/change_subscribe_status/" + status + "/" + id + "/${subscriberGroupId}",
            type: "PUT",
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                alert("An error has occurred making the request: " + errorThrown);

            },
            success: function (data) {
                if (data != 'SUCCESS') {
                    alert("<spring:message code="general.exception.delete"/>");
                }
                else {
                    //Do stuff here on success such as modal info
                    window.location.replace(href);
                }

            }
        });
    }

    function deleteContact(id) {
        var href = "${context}/main/merchant/subscriber_management/subscriber_list/${subscriberGroupId}/1?nextPage=SUBSCRIBER_LIST";
        $.ajax({
            url: "${context}/main/merchant/subscriber_management/delete_subscriber/" + id + "",
            type: "DELETE",
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                alert("An error has occurred making the request: " + errorThrown);

            },
            success: function (data) {
                if (data != 'SUCCESS') {
                    alert("<spring:message code="general.exception.delete"/>");
                }
                else {
                    //Do stuff here on success such as modal info
                    window.location.replace(href);
                }

            }
        });
    }

</script>