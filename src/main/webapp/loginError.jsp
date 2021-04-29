<!--
Budget Game Rankings
Author: Kevin Leader
Date: Spring 2021
Filename: loginError.jsp
-->

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="title" value="BGR - Invalid Login"/>

<html>

<%@include file="head.jsp"%>

<body>
  <div class="container">
    <div class="row"><jsp:include page="headerAndNav.jsp"/></div>

    <main>
      <section>
        <h2>Invalid Login. <a href="login">Try again?</a></h2>
      </section>
    </main>

    <!-- <div class="row">footer</div> -->
  </div>
</body>
</html>
