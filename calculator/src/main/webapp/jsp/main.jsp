<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<html>
<head>
    <title>Calculator</title>
</head>
<body>
<div style="display:inline-block; margin:20% 40% 10% 40%;">
    <html:form action="/Compute" method="post" focus="num1">
        <html:messages id="err_name" property="common.compute.err">
            <div style="color:red">
                <bean:write name="err_name"/>
            </div>
        </html:messages>
        <html:text style="width: 80px" property="num1" value=""/>

        <html:select property="action">
            <html:option value="+">+</html:option>
            <html:option value="-">-</html:option>
            <html:option value="/">/</html:option>
            <html:option value="*">*</html:option>
        </html:select>

        <html:text style="width: 80px" property="num2" value=""/>
        <html:submit value="submit"/>
    </html:form>
    <logic:present name="calcForm" property="result">
        <bean:write name="calcForm" property="result"/>
    </logic:present>
</div>
</body>
</html>
