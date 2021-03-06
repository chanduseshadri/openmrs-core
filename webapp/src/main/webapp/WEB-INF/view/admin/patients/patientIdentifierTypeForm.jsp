<%@ include file="/WEB-INF/template/include.jsp" %>

<openmrs:require privilege="Manage Identifier Types" otherwise="/login.htm" redirect="/admin/patients/patientIdentifierType.form" />

<%@ include file="/WEB-INF/template/header.jsp" %>
<%@ include file="localHeader.jsp" %>

<script type="text/javascript">

	function confirmPurge() {
		if (confirm("Are you sure you want to purge this object? It will be permanently removed from the system.")) {
			return true;
		} else {
			return false;
		}
	}
	
</script>

<h2><openmrs:message code="PatientIdentifierType.title"/></h2>

<c:if test="${patientIdentifierType.retired && not empty patientIdentifierType.patientIdentifierTypeId}">
	<form action="" method="post">
		<div class="retiredMessage">
			<div>
				<openmrs:message code="general.retiredBy"/>
				${patientIdentifierType.retiredBy.personName}
				<openmrs:formatDate date="${patientIdentifierType.dateRetired}" type="medium" />
				-
				${patientIdentifierType.retireReason}
				<input type="submit" value='<openmrs:message code="PatientIdentifierType.unretirePatientIdentifierType"/>' name="unretire"/>
			</div>
		</div>
	</form>
</c:if>

<form method="post">
<fieldset>
<table>
	<tr>
		<td><openmrs:message code="general.name"/></td>
		<td>
			<spring:bind path="patientIdentifierType.name">
				<input type="text" name="name" value="${status.value}" size="35" />
				<c:if test="${status.errorMessage != ''}"><span class="error">${status.errorMessage}</span></c:if>
			</spring:bind>
		</td>
	</tr>
	<tr>
		<td valign="top"><openmrs:message code="general.description"/></td>
		<td valign="top">
			<spring:bind path="patientIdentifierType.description">
				<textarea name="description" rows="3" cols="40">${status.value}</textarea>
				<c:if test="${status.errorMessage != ''}"><span class="error">${status.errorMessage}</span></c:if>
			</spring:bind>
		</td>
	</tr>
	<tr>
		<td><openmrs:message code="PatientIdentifierType.format"/></td>
		<td>
			<spring:bind path="patientIdentifierType.format">
				<input type="text" name="format" value="${status.value}" size="35" />
				<c:if test="${status.errorMessage != ''}"><span class="error">${status.errorMessage}</span></c:if>
			</spring:bind>
		</td>
	</tr>
	<tr>
	<td><openmrs:message code="PatientIdentifierType.formatDescription"/></td>
		<td>
			<spring:bind path="patientIdentifierType.formatDescription">
				<input type="text" name="${status.expression}" value="${fn:replace(status.value, "\"", "&quot;")}" size="50" />
				<c:if test="${status.errorMessage != ''}"><span class="error">${status.errorMessage}</span></c:if>
			</spring:bind>
		</td>
	</tr>
	<tr>
		<td><openmrs:message code="PatientIdentifierType.required" /></td>
		<td><spring:bind path="patientIdentifierType.required">
			<input type="hidden" name="_${status.expression}">
			<input type="checkbox" name="${status.expression}" value="true"
				<c:if test="${status.value == true}">checked</c:if> />
			<c:if test="${status.errorMessage != ''}">
				<span class="error">${status.errorMessage}</span>
			</c:if>
		</spring:bind></td>
	</tr>
	<tr>
		<td><openmrs:message code="PatientIdentifierType.locationBehavior" /></td>
		<td><spring:bind path="patientIdentifierType.locationBehavior">
			<select name="${status.expression}">
				<option value=""></option>
				<c:forEach var="locationBehavior" items="${locationBehaviors}">
					<option value="${locationBehavior}" <c:if test="${status.value == locationBehavior}">selected</c:if>>
						<openmrs:message code="PatientIdentifierType.locationBehavior.${locationBehavior}" />
					</option>
				</c:forEach>
			</select>
			<c:if test="${status.errorMessage != ''}">
				<span class="error">${status.errorMessage}</span>
			</c:if>
		</spring:bind></td>
	</tr>
	<tr>
		<td><openmrs:message code="PatientIdentifierType.validator" /></td>
		<td><spring:bind path="patientIdentifierType.validator">
			<select name="${status.expression}">
				<option value="">None</option>
				<c:forEach var="piv" items="${patientIdentifierValidators}">
					<option value="${piv['class'].name}" 
						<c:if test="${status.value == piv['class'].name}">selected</c:if> 
					/>
					${piv.name}
						<c:if test="${defaultValidatorName == piv.name}"> (default)</c:if>
					</option>
				</c:forEach>
			</select>
			<c:if test="${status.errorMessage != ''}">
				<span class="error">${status.errorMessage}</span>
			</c:if>
		</spring:bind></td>
	</tr>
	<c:if test="${!(patientIdentifierType.creator == null)}">
		<tr>
			<td><openmrs:message code="general.createdBy" /></td>
			<td>
				${patientIdentifierType.creator.personName} -
				<openmrs:formatDate date="${patientIdentifierType.dateCreated}" type="long" />
			</td>
		</tr>
	</c:if>
</table>
<input type="hidden" name="patientIdentifierTypeId:int" value="${patientIdentifierType.patientIdentifierTypeId}">
<br />
<input type="submit" value="<openmrs:message code="PatientIdentifierType.save"/>" name="save" />
</fieldset>
</form>

<br/>

<c:if test="${not patientIdentifierType.retired && not empty patientIdentifierType.patientIdentifierTypeId}">
	<form method="post">
		<fieldset>
			<h4><openmrs:message code="PatientIdentifierType.retirePatientIdentifierType"/></h4>
			
			<b><openmrs:message code="general.reason"/></b>
			<input type="text" value="" size="40" name="retireReason" />
			<spring:hasBindErrors name="patientIdentifierType">
				<c:forEach items="${errors.allErrors}" var="error">
					<c:if test="${error.code == 'retireReason'}"><span class="error"><openmrs:message code="${error.defaultMessage}" text="${error.defaultMessage}"/></span></c:if>
				</c:forEach>
			</spring:hasBindErrors>
			<br/>
			<input type="submit" value='<openmrs:message code="PatientIdentifierType.retirePatientIdentifierType"/>' name="retire"/>
		</fieldset>
	</form>
	
	<br/>
</c:if>

<c:if test="${not empty patientIdentifierType.patientIdentifierTypeId}">
	<openmrs:hasPrivilege privilege="Purge Identifier Types">
		<form id="purge" method="post" onsubmit="return confirmPurge()">
			<fieldset>
				<h4><openmrs:message code="PatientIdentifierType.purgePatientIdentifierType"/></h4>
				<input type="submit" value='<openmrs:message code="PatientIdentifierType.purgePatientIdentifierType"/>' name="purge" />
			</fieldset>
		</form>
	</openmrs:hasPrivilege>
</c:if>

<script type="text/javascript">
 document.forms[0].elements[0].focus();
</script>

<%@ include file="/WEB-INF/template/footer.jsp" %>