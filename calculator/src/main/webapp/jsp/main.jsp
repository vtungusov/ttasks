<%@taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<html>
<head>
    <title>Calculator</title>
</head>
<body>
<div style="display:inline-block; margin:20% 40% 10% 40%;">
    <form action="./Compute.do" method="post">
        <html:messages id="err_name" property="common.compute.err">
            <div style="color:red">
                <bean:write name="err_name"/>
            </div>
        </html:messages>

        <input autofocus style="width: 80px" name="operand1" value="" type="text" pattern="[+-]?([0-9]*[.])?[0-9]+"
               required/>

        <select name="action" required>
            <option selected value="+">+</option>
            <option value="-">-</option>
            <option value="*">*</option>
            <option value="/">/</option>
        </select>

        <input style="width: 80px" name="operand2" value="" type="text" pattern="[+-]?([0-9]*[.])?[0-9]+" required/>
        <input type="submit" value="compute"/>
    </form>
    <logic:present name="calcForm" property="result">
        <bean:write name="calcForm" property="result"/>
    </logic:present>
</div>
</body>
</html>
