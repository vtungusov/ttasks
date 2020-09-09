<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<html>
<head>
    <title>Calculator</title>
</head>
<body>
<html:form action="/Compute" method="post">
    <html:messages id="err_name" property="common.compute.err">
        <div style="color:red">
            <bean:write name="err_name"/>
        </div>
    </html:messages>
    <html:text style="width: 80px" property="num1"/>

    <html:select property="action">
        <html:option value="+">+</html:option>
        <html:option value="-">-</html:option>
        <html:option value="/">/</html:option>
        <html:option value="*">*</html:option>
    </html:select>

    <html:text style="width: 80px" property="num2"/>
    <html:submit value="submit"/>
</html:form>
<logic:present name="calcForm" property="result">
    <bean:write name="calcForm" property="result"/>
</logic:present>
</body>
</html>
