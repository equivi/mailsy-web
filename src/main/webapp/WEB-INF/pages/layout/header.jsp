<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c' %>

<div class="navbar navbar-default" id="navbar">
    <script type="text/javascript">
        try {
            ace.settings.check('navbar', 'fixed')
        } catch (e) {
        }
    </script>

    <div class="navbar-container" id="navbar-container">

        <div class="navbar-header pull-left">
            <a href="#" class="navbar-brand">
                <small>
                    <img src="${pageContext.request.contextPath}/resources/images/mailsy_header.png"/>
                    <spring:message code="label.product.name"/>
                    &copy;
                    <img src="${pageContext.request.contextPath}/resources/images/equivi_logo.png"/>
                </small>
            </a><!-- /.brand -->
        </div>
        <!-- /.navbar-header -->

        <div class="navbar-header pull-right" role="navigation">
            <ul class="nav ace-nav">

                <li class="light-blue">
                    <a data-toggle="dropdown" href="#" class="dropdown-toggle">
                        <i class="icon-user"></i>
                        ${userLoggedIn.firstName} ${userLoggedIn.middleName} ${userLoggedIn.lastName}
                        <i class="icon-caret-down"></i>
                    </a>

                    <ul class="dropdown-menu dropdown-close">
                        <li>
                            <a href="#modal-form" role="button" data-toggle="modal">
                                <i class="icon-lock"></i>
                                <spring:message code="label.change.password"/>
                            </a>
                        </li>
                        <li class="divider"></li>
                        <li>
                            <a href="${pageContext.request.contextPath}/logout"/>
                            <i class="icon-off"></i>
                            <spring:message code="label.logout"/>
                            </a>
                        </li>
                    </ul>
                </li>
            </ul>
            <!-- /.ace-nav -->
        </div>
        <!-- /.navbar-header -->
    </div>
    <!-- /.container -->
</div>

<jsp:include page="../general/change_password.jsp"/>