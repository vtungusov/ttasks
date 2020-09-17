<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<html>
<head>
    <title>Calculator</title>
</head>
<body>
<div style="display:inline-block; margin:20% 40% 10% 40%;">
    <div style="color:red">
        <html:errors/>
    </div>
    <html:form action="/Compute.do" method="post">
        <html:messages id="err_name" property="common.compute.err">
            <div style="color:red">
                <bean:write name="err_name"/>
            </div>
        </html:messages>

        <input style="width: 80px" name="operand1" value="" type="number" step="any" required autofocus/>

        <html:select property="action">
            <html:option value="">Select Operation</html:option>
            <html:optionsCollection name="calcForm" property="operations" label="value" value="name"/>
        </html:select>

        <input style="width: 80px" name="operand2" value="" type="number" step="any" required/>
        <input type="submit" value="compute"/>
    </html:form>
    <logic:present name="calcForm" property="result">
        <bean:write name="calcForm" property="result"/>
    </logic:present>
</div>
</body>
</html>
